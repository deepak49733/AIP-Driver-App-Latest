package com.sc.aipdriver.activities

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import cn.pedant.SweetAlert.SweetAlertDialog
import com.sc.aipdriver.activities.interfaces.ApiClient
import com.sc.aipdriver.activities.interfaces.ApiInterface
import com.sc.aipdriver.activities.models.BaseResponse
import com.sc.aipdriver.activities.models.CommonError
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData
import com.sc.aipdriver.activities.models.PermissionModel
import com.sc.aipdriver.activities.models.PriorityFarmData
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager
import com.sc.aipdriver.activities.room.AppDatabase
import com.sc.aipdriver.activities.ui.FarmListRoute
import com.sc.aipdriver.activities.ui.ImprovedFarmList
import com.sc.aipdriver.activities.ui.LoginActivity
import com.sc.aipdriver.activities.fragments.SelectCar
import com.sc.aipdriver.activities.worker.SyncScheduler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class AppController : Application(), Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null
    private var handler: Handler? = null
    private var statusCheckRunnable: Runnable? = null
    private var sharedprefrenceManager: SharedprefrenceManager? = null
    private var apiService: ApiInterface? = null
    private var isPopupShowing = false

    private val internalRouteId get() = sharedprefrenceManager?.routeId ?: "0"
    private val internalOrderDate get() = sharedprefrenceManager?.date ?: ""

    override fun onCreate() {
        super.onCreate()

        sharedprefrenceManager = SharedprefrenceManager(this)
        apiService = ApiClient.getClient(this).create(ApiInterface::class.java)
        registerActivityLifecycleCallbacks(this)
        startGlobalStatusCheck()

        // Start background sync worker
        SyncScheduler.startSyncWorker(this)
        NetworkListener.listenNetwork(this)
    }

    private fun startGlobalStatusCheck() {
        Log.d("GlobalStatus", "startGlobalStatusCheck: Timer initialized")
        handler = Handler(Looper.getMainLooper())
        statusCheckRunnable = object : Runnable {
            override fun run() {
                Log.d("GlobalStatus", "Timer triggered: Running checkGlobalRouteStatus")
                checkGlobalRouteStatus()
                handler?.postDelayed(this, 5000) // 5 sec
            }
        }
        handler?.post(statusCheckRunnable!!)
    }

    private fun checkGlobalRouteStatus() {
        val activity = currentActivity
        Log.d("GlobalStatus", "checkGlobalRouteStatus: currentActivity = ${activity?.javaClass?.simpleName}, isPopupShowing = $isPopupShowing")

        if (activity == null || activity is LoginActivity || isPopupShowing) {
            Log.d("GlobalStatus", "Check skipped: activity null, LoginActivity, or popup showing")
            return
        }
        if (!isInternetAvailable(this)) {
            Log.d("GlobalStatus", "Check skipped: No internet connection")
            return
        }

        val token = sharedprefrenceManager?.token
        val driverId = sharedprefrenceManager?.driverID

        if (token.isNullOrEmpty() || token == "0" || driverId.isNullOrEmpty() || driverId == "0") {
            Log.d("GlobalStatus", "Check skipped: Missing required token or driverId")
            return
        }

        // Step 1: Call showPermission to get latest assigned route and date
        Log.d("GlobalStatus", "Step 1: Calling showPermission for driverId: $driverId")
        apiService?.showPermission(driverId.toInt(), token)
            ?.enqueue(object : Callback<PermissionModel?> {
                override fun onResponse(call: Call<PermissionModel?>, response: Response<PermissionModel?>) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        val pData = response.body()!!.data

                        // Update SharedPrefs with latest info
                        sharedprefrenceManager?.setRouteId(pData.routeId)
                        sharedprefrenceManager?.setParentId(pData.parentId)

                        val pRideId = pData.getRideId()
                        val pFarmId = pData.getFarmId()

                        // RCA Fix: STRICT LOCAL-FIRST logic.
                        // Only adopt server's RideId/FarmId if local app is NOT in a ride.
                        val localRideId = sharedprefrenceManager?.getRideId() ?: "0"
                        val isLocalRideActive = localRideId != "0" && localRideId != "" && localRideId != "null"

                        if (!isLocalRideActive) {
                            if (pRideId != 0 && pFarmId != 0) {
                                sharedprefrenceManager?.setRideId(pRideId)
                                sharedprefrenceManager?.setFID(pFarmId.toString())
                            }
                        } else {
                            // If local ride is active, only update RideId if it's the same or if server provides a valid numeric ID for our current ride
                            if (pRideId != 0 && pFarmId != 0) {
                                val localFid = sharedprefrenceManager?.getFID()?.toIntOrNull() ?: 0
                                if (localFid == pFarmId) {
                                    sharedprefrenceManager?.setRideId(pRideId)
                                }
                            }
                        }

                        val rawDate = pData.orderDate ?: ""
                        if (rawDate.isNotEmpty()) {
                            sharedprefrenceManager?.date = rawDate
                        }

                        Log.d("GlobalStatus", "Permission received: routeId=${pData.routeId}, date=$rawDate")

                        // Step 2: Call getRoute with updated info
                        checkRouteUpdate(token)

                    } else {
                        Log.e("GlobalStatus", "showPermission API Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PermissionModel?>, t: Throwable) {
                    Log.e("GlobalStatus", "showPermission failure: ${t.message}")
                }
            })
    }
    private val safeRouteId get() = sharedprefrenceManager?.getRouteId()
    private val safeRawDate get() = sharedprefrenceManager?.getDate()
    private val safeDriverId get() = sharedprefrenceManager?.getDriverID()
    private val safeToken get() = sharedprefrenceManager?.getToken()
    private fun callUpdateRouteApi() {
        Log.d("UpdateRoute", "Route update API call")


        if (safeRouteId == null || safeDriverId == null || safeToken == null) return

        val formattedDate = FarmListRoute.formatToMMDDYYYY(safeRawDate ?: "")

        apiService!!.updateRoute(safeRouteId!!, internalOrderDate, safeDriverId!!, safeToken!!)
            .enqueue(object : Callback<CommonError?> {
                override fun onResponse(
                    call: Call<CommonError?>,
                    response: Response<CommonError?>
                ) {
                    if (response.isSuccessful()) {
                        Log.d("UpdateRoute", "Route update API success")
                    } else {
                        Log.e("UpdateRoute", "Route update API error: " + response.code())
                    }
                }

                override fun onFailure(call: Call<CommonError?>, t: Throwable) {
                    Log.e("UpdateRoute", "Route update API failure: " + t.message)
                }
            })
    }

    private fun checkRouteUpdate(token: String) {
        val driverId = sharedprefrenceManager?.driverID ?: "0"

        Log.d("GlobalStatus", "Step 2: Calling getRoute with routeId=$internalRouteId")

        apiService?.getRoute(internalRouteId, internalOrderDate, driverId, token)
            ?.enqueue(object : Callback<BaseResponse<Int>> {
                override fun onResponse(call: Call<BaseResponse<Int>>, response: Response<BaseResponse<Int>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()?.data
                        Log.d("GlobalStatus", "getRoute Data: $data")
                        if (data == 1) {
                            showGlobalPopup()
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Int>>, t: Throwable) {
                    Log.e("GlobalStatus", "getRoute failure: ${t.message}")
                }
            })
    }

    private fun showGlobalPopup() {
        val activity = currentActivity
        if (activity == null || activity.isFinishing || activity.isDestroyed || isPopupShowing) return

        isPopupShowing = true
        Log.d("GlobalStatus", "Displaying popup on ${activity.javaClass.simpleName}")
        activity.runOnUiThread {
            SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Route Update")
                .setContentText("Your route has been updated. Please check the farm list.")
                .setConfirmText("OK")
                .setConfirmClickListener { sDialog ->
                    isPopupShowing = false
                    sDialog.dismissWithAnimation()
                    if (activity is FarmListRoute) {
                        Log.d("GlobalStatus", "Refreshing FarmListRoute")
                        activity.shouldUpdateRouteAfterRefresh = true
                        activity.getFarmsByRoute()
                        callUpdateRouteApi()
                    } else if (activity is ImprovedFarmList) {
                        Log.d("GlobalStatus", "Refreshing ImprovedFarmList")
                        activity.shouldUpdateRouteAfterRefresh = true
                        activity.getFarmsByRoute()
                        callUpdateRouteApi()
                    } else {
                        Log.d("GlobalStatus", "Background refresh")
                        fetchUpdatedFarmsInBackground()
                    }
                }
                .show()
        }
    }

    private fun fetchUpdatedFarmsInBackground() {
        val route = sharedprefrenceManager?.routeId ?: ""
        val parentId = sharedprefrenceManager?.parentId ?: "0"
        val rawDate = sharedprefrenceManager?.date ?: ""
        val driverId = sharedprefrenceManager?.driverID ?: ""
        val token = sharedprefrenceManager?.token ?: ""

        if (route.isEmpty() || driverId.isEmpty() || token.isEmpty()) return

        val normalizedDate = FarmListRoute.formatToMMDDYYYY(rawDate)

        apiService?.getAllFarmList(route, parentId, normalizedDate, driverId, token)
            ?.enqueue(object : Callback<BaseResponse<List<ImprovedPriorityFarmData>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<ImprovedPriorityFarmData>>>,
                    response: Response<BaseResponse<List<ImprovedPriorityFarmData>>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val farms = response.body()?.data
                            ?.filter { it.getRemove() == 0 }
                        if (farms != null && farms.isNotEmpty()) {
                            Executors.newSingleThreadExecutor().execute {
                                val db = AppDatabase.getDatabase(this@AppController)
                                
                                // RCA Fix: Highly robust active farm preservation
                                val currentFidStr = sharedprefrenceManager?.getFID() ?: "0"
                                val currentFid = try { currentFidStr.toInt() } catch (e: Exception) { 0 }
                                
                                if (currentFid != 0) {
                                    farms.forEach { farm ->
                                        if (farm.id == currentFid) {
                                            farm.setIsActiveRide(1)
                                        } else {
                                            farm.setIsActiveRide(0)
                                        }
                                    }
                                }
                                
                                db.improvedPriorityFarmDao().clearImprovedFarms()
                                db.improvedPriorityFarmDao().insertImprovedFarms(farms)
                                Log.d("GlobalStatus", "Background improved farm update successful: ${farms.size} farms")

                                // Call UpdateRoute API after successful background update
                                apiService?.updateRoute(route, normalizedDate, driverId, token)?.enqueue(object : Callback<CommonError> {
                                    override fun onResponse(call: Call<CommonError>, response: Response<CommonError>) {
                                        if (response.isSuccessful) {
                                            Log.d("GlobalStatus Update", "Background UpdateRoute API success")
                                        } else {
                                            Log.e("GlobalStatus", "Background UpdateRoute API error: ${response.code()}")
                                        }
                                    }
                                    override fun onFailure(call: Call<CommonError>, t: Throwable) {
                                        Log.e("GlobalStatus", "Background UpdateRoute API failure: ${t.message}")
                                    }
                                })
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<ImprovedPriorityFarmData>>>, t: Throwable) {
                    Log.e("GlobalStatus", "Background improved farm update failed: ${t.message}")
                }
            })
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
        if (activity !is LoginActivity && activity !is SelectCar) {
            sharedprefrenceManager?.setLastActiveActivity(activity.javaClass.name)
            val extras = activity.intent?.extras
            if (extras != null) {
                val farmId = extras.getString("FarmID") ?: extras.getString("farmIdd") ?: extras.getString("farmId") ?: extras.getString("farmid") ?: ""
                val routeId = extras.getString("ROUTEID") ?: extras.getString("routeId") ?: extras.getString("routeid") ?: ""
                val orderDate = extras.getString("orderdate") ?: extras.getString("date") ?: ""
                val routeName = extras.getString("ROUTE") ?: extras.getString("RouteName") ?: ""
                val farmName = extras.getString("FarmName") ?: extras.getString("farmName") ?: ""

                sharedprefrenceManager?.setLastFarmId(farmId)
                sharedprefrenceManager?.setLastRouteId(routeId)
                sharedprefrenceManager?.setLastOrderDate(orderDate)
                sharedprefrenceManager?.setLastRouteName(routeName)
                sharedprefrenceManager?.setLastFarmName(farmName)
            }
        }
    }
    override fun onActivityPaused(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
