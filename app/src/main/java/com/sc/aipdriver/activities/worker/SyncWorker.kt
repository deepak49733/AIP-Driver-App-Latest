package com.sc.aipdriver.activities.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sc.aipdriver.activities.interfaces.ApiClient
import com.sc.aipdriver.activities.interfaces.ApiInterface
import com.sc.aipdriver.activities.models.RideFinishRequest
import com.sc.aipdriver.activities.models.ReceiptResponse
import com.sc.aipdriver.activities.models.RouteLog
import com.sc.aipdriver.activities.models.StartDataSend
import com.sc.aipdriver.activities.models.TempBags
import com.sc.aipdriver.activities.models.EmailRequest
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager
import com.sc.aipdriver.activities.otherclasses.Utils
import com.sc.aipdriver.activities.room.AppDatabase
import com.sc.aipdriver.activities.room.PhotoSyncEntity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean


class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private val isSyncing = AtomicBoolean(false)
    }

    override fun doWork(): Result {
        val sharedprefrenceManager = SharedprefrenceManager(applicationContext)
        if (!sharedprefrenceManager.isSyncMode()) {
            SyncStatusManager.syncCompleted.postValue(false)
            SyncStatusManager.syncStarted.postValue(false)
            return Result.success()
        }

        // ✅ CONCURRENCY GUARD: Prevent multiple workers from running simultaneously
        if (isSyncing.getAndSet(true)) {
            Log.d("SyncWorker", "Another sync is already in progress, skipping this run.")
            return Result.success()
        }

        return try {
            val db = AppDatabase.getDatabase(applicationContext)
            val apiService = ApiClient.getClient(applicationContext).create(ApiInterface::class.java)
            val apiHTTP1 = ApiClient.getHttp1Client(applicationContext).create(ApiInterface::class.java)
            val sharedPref = SharedprefrenceManager(applicationContext)

            SyncStatusManager.syncStarted.postValue(true)

            /** ---------------------------------------------------------
             * ✅ STEP 1: SYNC FARM TEMP FIRST (HIGHEST PRIORITY)
             * --------------------------------------------------------- */
            val pendingTemp = db.farmTempBegDao().getPending()
            for (entity in pendingTemp) {
                try {
                    val model = TempBags().apply {
                        farmID = entity.farmId
                        beg = entity.beg
                        temp = entity.temp
                        refTem = entity.coolerTemp
                        parentId = entity.ParentId
                        routeName = entity.routeName
                    }
                    val response = apiService.sendTempBags(arrayListOf(model), entity.driverId, entity.Token).execute()
                    if (response.isSuccessful) {
                        db.farmTempBegDao().markSynced(entity.farmId, entity.orderDate)
                        if (entity.isImproved) {
                            db.improvedPriorityFarmDao().updateImprovedFarmTempBags(
                                entity.farmId, entity.orderDate, entity.beg,
                                entity.temp.toDoubleOrNull() ?: 0.0, 1, 1, 1
                            )
                        } else {
                            db.priorityFarmDao().updateFarmTempBags(
                                entity.farmId, entity.orderDate, entity.beg,
                                entity.temp.toDoubleOrNull() ?: 0.0, 1, 1
                            )
                        }
                    } else return Result.retry()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return Result.retry()
                }
            }

            /** ---------------------------------------------------------
             * ✅ STEP 2: SYNC RIDE START / LOG / FINISH / RECEIPT
             * --------------------------------------------------------- */
            val pendingSync = db.rideSyncDao().getPending()
            for (entity in pendingSync) {
                if (entity.apiType == "FINISH_EMAIL") continue
                val response = when (entity.apiType) {
                    "START" -> {
                        val req = Gson().fromJson(entity.payload, StartDataSend::class.java)
                        val res = apiService.sendData(req, entity.token).execute()
                        if (res.isSuccessful && res.body()?.data != null) {
                            val serverRideId = res.body()?.data
                            val offlineRideId = req.offlineRideId
                            if (!serverRideId.isNullOrEmpty() && !offlineRideId.isNullOrEmpty()) {
                                db.photoSyncDao().updateRideId("FARM_END", offlineRideId, serverRideId)
                                val firmId = req.liRoutePlannerDetail?.get(0)?.firmid?.toIntOrNull() ?: 0
                                if (firmId != 0) db.improvedPriorityFarmDao().updateRideId(firmId, req.orderDate, serverRideId)
                            }
                        }
                        res
                    }
                    "LOG" -> apiService.sendLog(Gson().fromJson(entity.payload, RouteLog::class.java), entity.token).execute()
                    "FINISH" -> apiHTTP1.rideFinish(Gson().fromJson(entity.payload, RideFinishRequest::class.java), sharedPref.driverID, entity.token).execute()
                    "RECEIPT" -> {
                        val req = Gson().fromJson(entity.payload, ReceiptResponse::class.java)
                        val res = apiService.ReceiptDetails(req, sharedPref.driverID, entity.token).execute()
                        if (res.isSuccessful && res.body()?.data != null) {
                            val serverId = res.body()?.data
                            val clientRID = req.getRID()
                            if (!serverId.isNullOrEmpty() && !clientRID.isNullOrEmpty()) {
                                db.photoSyncDao().updateFarmId("RECEIPT", clientRID, serverId)
                                db.receiptDao().updateServerId(clientRID, serverId)
                            }
                        }
                        res
                    }
                    else -> null
                }
                if (response?.isSuccessful == true) {
                    db.rideSyncDao().markSynced(entity.id)
                    db.rideSyncDao().deleteById(entity.id)
                } else return Result.retry()
            }

            /** ---------------------------------------------------------
             * ✅ STEP 3: SYNC RIDE FINISH DATA FIRST, THEN PHOTOS
             * --------------------------------------------------------- */
            // 3A: Send Ride Finish Data first so backend registers completion, bags, temp, comments & timestamp
            val pendingFinish = db.rideFinishDao().getPending()
            for (entity in pendingFinish) {
                var rideIdToUse = entity.rideId
                if (rideIdToUse.startsWith("OFF_") || rideIdToUse == "0" || rideIdToUse.isEmpty()) {
                    val firmIdInt = entity.firmId?.toIntOrNull() ?: 0
                    if (firmIdInt != 0) {
                        val farm = db.improvedPriorityFarmDao().getFarmByIdAndDate(firmIdInt, com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY(sharedPref.date))
                        if (farm != null && !farm.rideId.startsWith("OFF_") && farm.rideId != "0" && farm.rideId.isNotEmpty()) {
                            rideIdToUse = farm.rideId
                        }
                    }
                }
                if (rideIdToUse == "0" || rideIdToUse.isEmpty()) {
                    Log.w("SyncWorker", "Skipping finish sync for firm ${entity.firmId}: rideId is 0 or empty")
                    continue
                }
                val request = RideFinishRequest().apply {
                    commentsDelivered = entity.comments; endDateTime = entity.EndDateTime; action = entity.action; rideId = rideIdToUse; uid = entity.uid; lat = entity.lat; lng = entity.lng; endOdometer = entity.endOdometer; totalMiles = entity.totalMiles; address = entity.address; state = entity.state; city = entity.city; country = entity.country; firmId = entity.firmId; bagsDelivered = entity.bagsDelivered; temperature = entity.temperature; routeId = entity.routeId; customerSeemanCoolarTemp = entity.coolerTemp
                }
                try {
                    val res = apiService.rideFinish(request, entity.driverId, entity.Token).execute()
                    if (res.isSuccessful) {
                        db.rideFinishDao().markSynced(entity.localId)
                        db.rideFinishDao().deleteById(entity.localId)
                    } else {
                        Log.e("SyncWorker", "rideFinish failed with code ${res.code()} for firm ${entity.firmId}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 3B: Upload Farm End Photos for completed farms
            val rideIds = db.photoSyncDao().getPendingRideIds("FARM_END")
            for (rideId in rideIds) {
                val photos = db.photoSyncDao().getPhotosByRide("FARM_END", rideId)
                if (photos.isEmpty()) continue
                var rideIdToUse = rideId
                if (rideId.startsWith("OFF_") || rideId == "0" || rideId.isEmpty()) {
                    val firmId = photos.first().farmId
                    if (!firmId.isNullOrEmpty()) {
                        val farm = db.improvedPriorityFarmDao().getFarmByIdAndDate(firmId.toIntOrNull() ?: 0, com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY(sharedPref.date))
                        if (farm != null && !farm.rideId.startsWith("OFF_") && farm.rideId != "0" && farm.rideId.isNotEmpty()) {
                            rideIdToUse = farm.rideId
                            db.photoSyncDao().updateRideId("FARM_END", rideId, rideIdToUse)
                        }
                    }
                }
                if (rideIdToUse == "0" || rideIdToUse.isEmpty()) {
                    Log.w("SyncWorker", "Skipping photo upload for farm ${photos.first().farmId}: rideId is 0 or empty")
                    continue
                }
                var img1: MultipartBody.Part? = null
                var img2: MultipartBody.Part? = null
                var img3: MultipartBody.Part? = null
                var img4: MultipartBody.Part? = null
                for (photo in photos) {
                    val part = createPart(photo, db) ?: continue
                    when (photo.imageIndex) {
                        1 -> img1 = part
                        2 -> img2 = part
                        3 -> img3 = part
                        4 -> img4 = part
                    }
                }
                try {
                    val res = apiService.uploadFarmEndImage(photos.first().Token ?: sharedPref.token, rideIdToUse, img1, img2, img3, img4).execute()
                    if (res.isSuccessful) {
                        photos.forEach {
                            db.photoSyncDao().markSynced(it.localId)
                            db.photoSyncDao().deleteById(it.localId)
                        }
                    } else {
                        Log.e("SyncWorker", "uploadFarmEndImage failed with code ${res.code()} for ride $rideIdToUse")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            /** ---------------------------------------------------------
             * ✅ STEP 4: SYNC FINISH EMAILS
             * --------------------------------------------------------- */
            val pendingEmails = db.rideSyncDao().getPending()
            val processedKeys = mutableSetOf<String>()
            val gson = Gson()
            for (entity in pendingEmails) {
                if (entity.apiType != "FINISH_EMAIL") continue
                try {
                    val json = gson.fromJson(entity.payload, JsonObject::class.java)
                    val firmId = json.get("FIRMID")?.asString?.trim() ?: ""
                    var rideId = json.get("rideId")?.asString?.trim() ?: ""
                    if (rideId.startsWith("OFF_") || rideId == "0" || rideId.isEmpty()) {
                        val farm = db.improvedPriorityFarmDao().getFarmByIdAndDate(firmId.toIntOrNull() ?: 0, com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY(sharedPref.date))
                        if (farm != null && !farm.rideId.startsWith("OFF_") && farm.rideId != "0" && farm.rideId.isNotEmpty()) {
                            rideId = farm.rideId; json.addProperty("rideId", rideId)
                        }
                    }
                    if (rideId == "0" || rideId.isEmpty()) {
                        Log.w("SyncWorker", "Skipping email sync for firm $firmId: rideId is 0 or empty")
                        continue
                    }
                    val key = "$firmId-$rideId"
                    if (processedKeys.contains(key)) {
                        db.rideSyncDao().deleteById(entity.id); continue
                    }
                    val res = apiHTTP1.rideFinishEmail(json, sharedPref.driverID, entity.token).execute()
                    if (res.isSuccessful) {
                        processedKeys.add(key)
                        db.rideSyncDao().deleteById(entity.id)
                        val remaining = db.rideSyncDao().getPending()
                        for (p in remaining) {
                            if (p.apiType == "FINISH_EMAIL") {
                                try {
                                    val pJson = gson.fromJson(p.payload, JsonObject::class.java)
                                    val pFirmId = pJson.get("FIRMID")?.asString ?: ""
                                    val pRideId = pJson.get("rideId")?.asString ?: ""
                                    if (pFirmId == firmId && (pRideId == rideId || pRideId == json.get("rideId")?.asString)) {
                                        db.rideSyncDao().deleteById(p.id)
                                    }
                                } catch (e: Exception) {}
                            }
                        }
                        if (firmId.isNotEmpty()) db.improvedPriorityFarmDao().updateEmailSent(firmId.toIntOrNull() ?: 0, com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY(sharedPref.date), 1)
                    } else {
                        Log.e("SyncWorker", "rideFinishEmail failed with code ${res.code()} for firm $firmId")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            /** ---------------------------------------------------------
             * ✅ STEP 5: SYNC RECEIPT IMAGES
             * --------------------------------------------------------- */
            val receiptIds = db.photoSyncDao().getPendingFarmIds("RECEIPT")
            for (receiptId in receiptIds) {
                val photos = db.photoSyncDao().getPhotosByFarm("RECEIPT", receiptId)
                if (photos.isEmpty()) continue
                var img1: MultipartBody.Part? = null; var img2: MultipartBody.Part? = null; var img3: MultipartBody.Part? = null; var img4: MultipartBody.Part? = null
                for (photo in photos) {
                    val part = createPart(photo, db) ?: continue
                    when (photo.imageIndex) { 1 -> img1 = part; 2 -> img2 = part; 3 -> img3 = part; 4 -> img4 = part }
                }
                val res = apiService.uploadReceiptImg(photos.first().Token ?: sharedPref.token, receiptId, img1, img2, img3, img4).execute()
                if (res.isSuccessful) {
                    photos.forEach { db.photoSyncDao().markSynced(it.localId); db.photoSyncDao().deleteById(it.localId) }
                } else return Result.retry()
            }

            SyncStatusManager.syncCompleted.postValue(true)
            SyncStatusManager.syncStarted.postValue(false)
            Result.success()

        } catch (e: Exception) {
            SyncStatusManager.syncCompleted.postValue(true)
            SyncStatusManager.syncStarted.postValue(false)
            e.printStackTrace()
            Result.retry()
        } finally {
            isSyncing.set(false)
        }
    }

    private fun createPart(photo: PhotoSyncEntity, db: AppDatabase): MultipartBody.Part? {
        val file = File(photo.imagePath)
        val uploadFile = Utils.compressImageFile(applicationContext, file)
        if (!file.exists()) {
            db.photoSyncDao().markSynced(photo.localId)
            return null
        }
        val requestFile = uploadFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("Image${photo.imageIndex}", uploadFile.name, requestFile)
    }
}
