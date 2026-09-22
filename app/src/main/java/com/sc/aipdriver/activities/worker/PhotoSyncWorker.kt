//package com.sc.aipdriver.activities.worker
//
//import android.content.Context
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import com.sc.aipdriver.activities.interfaces.ApiClient
//import com.sc.aipdriver.activities.interfaces.ApiInterface
//import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager
//import com.sc.aipdriver.activities.room.AppDatabase
//import com.sc.aipdriver.activities.room.PhotoSyncEntity
//import com.sc.aipdriver.activities.utils.SharedPrefManager
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import retrofit2.Retrofit
//import java.io.File
//
//class PhotoSyncWorker(
//    context: Context,
//    workerParams: WorkerParameters
//) : Worker(context, workerParams) {
//
//    private val db = AppDatabase.getDatabase(context)
//    private val sharedPref = SharedprefrenceManager(context)
//
//    private val apiService: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
//
//    override fun doWork(): Result {
//        return try {
//
//            syncFarmEnd()
//            syncStartRide()
//            syncReceipt()
//            syncEndRide()
//
//            Result.success()
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.retry()
//        }
//    }
//
//    // --------------------------------------------------
//    // 🔥 FARM_END (4 IMAGES)
//    // --------------------------------------------------
//    private fun syncFarmEnd() {
//
//        val rideIds = db.photoSyncDao().getPendingRideIds("FARM_END")
//
//        for (rideId in rideIds) {
//
//            val photos = db.photoSyncDao().getPhotosByRide("FARM_END", rideId)
//
//            var img1: MultipartBody.Part? = null
//            var img2: MultipartBody.Part? = null
//            var img3: MultipartBody.Part? = null
//            var img4: MultipartBody.Part? = null
//
//            for (photo in photos) {
//
//                val part = createPart(photo) ?: continue
//
//                when (photo.imageIndex) {
//                    1 -> img1 = part
//                    2 -> img2 = part
//                    3 -> img3 = part
//                    4 -> img4 = part
//                }
//            }
//
//            val response = apiService.uploadFarmEndImage(
//                rideId,
//                img1,
//                img2,
//                img3,
//                img4
//            ).execute()
//
//            if (response.isSuccessful) {
//                markAllSynced(photos)
//            } else {
//                throw Exception("FARM_END failed")
//            }
//        }
//    }
//
//    // --------------------------------------------------
//    // 🚀 START_RIDE (8 IMAGES)
//    // --------------------------------------------------
//    private fun syncStartRide() {
//
//        val rideIds = db.photoSyncDao().getPendingRideIds("START_RIDE")
//
//        for (rideId in rideIds) {
//
//            val photos = db.photoSyncDao().getPhotosByRide("START_RIDE", rideId)
//
//            val parts = Array<MultipartBody.Part?>(8) { null }
//
//            for (photo in photos) {
//                val part = createPart(photo) ?: continue
//                if (photo.imageIndex in 1..8) {
//                    parts[photo.imageIndex - 1] = part
//                }
//            }
//
//            val response = apiService.uploadImage(
//                sharedPref.token,
//                rideId,
//                parts[0], parts[1], parts[2], parts[3],
//                parts[4], parts[5], parts[6], parts[7]
//            ).execute()
//
//            if (response.isSuccessful) {
//                markAllSynced(photos)
//            } else {
//                throw Exception("START_RIDE failed")
//            }
//        }
//    }
//
//    // --------------------------------------------------
//    // 🧾 RECEIPT (4 IMAGES)
//    // --------------------------------------------------
//    private fun syncReceipt() {
//
//        val farmIds = db.photoSyncDao().getPendingFarmIds("RECEIPT")
//
//        for (farmId in farmIds) {
//
//            val photos = db.photoSyncDao().getPhotosByFarm("RECEIPT", farmId)
//
//            var img1: MultipartBody.Part? = null
//            var img2: MultipartBody.Part? = null
//            var img3: MultipartBody.Part? = null
//            var img4: MultipartBody.Part? = null
//
//            for (photo in photos) {
//
//                val part = createPart(photo) ?: continue
//
//                when (photo.imageIndex) {
//                    1 -> img1 = part
//                    2 -> img2 = part
//                    3 -> img3 = part
//                    4 -> img4 = part
//                }
//            }
//
//            val response = apiService.uploadReceiptImg(
//                sharedPref.token,
//                farmId,
//                img1,
//                img2,
//                img3,
//                img4
//            ).execute()
//
//            if (response.isSuccessful) {
//                markAllSynced(photos)
//            } else {
//                throw Exception("RECEIPT failed")
//            }
//        }
//    }
//
//    // --------------------------------------------------
//    // 🏁 END_RIDE (2 IMAGES)
//    // --------------------------------------------------
//    private fun syncEndRide() {
//
//        val rideIds = db.photoSyncDao().getPendingRideIds("END_RIDE")
//
//        for (rideId in rideIds) {
//
//            val photos = db.photoSyncDao().getPhotosByRide("END_RIDE", rideId)
//
//            var img1: MultipartBody.Part? = null
//            var img2: MultipartBody.Part? = null
//
//            for (photo in photos) {
//
//                val part = createPart(photo) ?: continue
//
//                when (photo.imageIndex) {
//                    1 -> img1 = part
//                    2 -> img2 = part
//                }
//            }
//
//            val response = apiService.uploadEndImage(
//                rideId,
//                img1,
//                img2
//            ).execute()
//
//            if (response.isSuccessful) {
//                markAllSynced(photos)
//            } else {
//                throw Exception("END_RIDE failed")
//            }
//        }
//    }
//
//    // --------------------------------------------------
//    // 🧩 COMMON HELPERS
//    // --------------------------------------------------
//
//    private fun createPart(photo: PhotoSyncEntity): MultipartBody.Part? {
//        val file = File(photo.imagePath)
//
//        if (!file.exists()) {
//            db.photoSyncDao().markSynced(photo.localId)
//            return null
//        }
//
//        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//
//        return MultipartBody.Part.createFormData(
//            "img${photo.imageIndex}",
//            file.name,
//            requestFile
//        )
//    }
//
//    private fun markAllSynced(photos: List<PhotoSyncEntity>) {
//        for (photo in photos) {
//            db.photoSyncDao().markSynced(photo.localId)
//        }
//    }
//}