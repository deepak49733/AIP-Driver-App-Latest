package com.sc.aipdriver.activities.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.sc.aipdriver.R
import com.sc.aipdriver.activities.adapters.ImprovedFarmListAdapterByRote
import com.sc.aipdriver.activities.adapters.ReceiptAdapter
import com.sc.aipdriver.activities.dialogs.FarmDetailSheet
import com.sc.aipdriver.activities.dialogs.LoadTemprature
import com.sc.aipdriver.activities.dialogs.PhotoDialog
import com.sc.aipdriver.activities.dialogs.ShowLoading
import com.sc.aipdriver.activities.fragments.FinishDialog
import com.sc.aipdriver.activities.fragments.ReceiptDialog
import com.sc.aipdriver.activities.fragments.SelectCar
import com.sc.aipdriver.activities.interfaces.ApiClient
import com.sc.aipdriver.activities.interfaces.ApiInterface
import com.sc.aipdriver.activities.interfaces.OnClickImprovedFarm
import com.sc.aipdriver.activities.interfaces.OnClickSubmit
import com.sc.aipdriver.activities.interfaces.OnImprovedItemClick
import com.sc.aipdriver.activities.interfaces.OnMailDone
import com.sc.aipdriver.activities.interfaces.OnReceiptClick
import com.sc.aipdriver.activities.isInternetAvailable
import com.sc.aipdriver.activities.models.BaseResponse
import com.sc.aipdriver.activities.models.CommonError
import com.sc.aipdriver.activities.models.EmailRequest
import com.sc.aipdriver.activities.models.FarmData
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData
import com.sc.aipdriver.activities.models.PriorityFarmData
import com.sc.aipdriver.activities.models.LiRoutePlannerDetail
import com.sc.aipdriver.activities.models.PermissionModel
import com.sc.aipdriver.activities.models.ReceiptResponse
import com.sc.aipdriver.activities.models.ReceiptResponseMain
import com.sc.aipdriver.activities.models.RideFinishRequest
import com.sc.aipdriver.activities.models.RouteLog
import com.sc.aipdriver.activities.models.StartDataSend
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager
import com.sc.aipdriver.activities.otherclasses.ShouldLogout
import com.sc.aipdriver.activities.room.AppDatabase
import com.sc.aipdriver.activities.room.AppDatabase.Companion.getDatabase
import com.sc.aipdriver.activities.room.EmailSyncEntity
import com.sc.aipdriver.activities.room.ReceiptEntity
import com.sc.aipdriver.activities.room.RideFinishEntity
import com.sc.aipdriver.activities.room.RideSyncEntity
import com.sc.aipdriver.activities.services.ForegroundLocationService
import com.sc.aipdriver.activities.worker.SyncScheduler
import com.sc.aipdriver.activities.worker.SyncScheduler.runImmediateSync
import com.sc.aipdriver.activities.worker.SyncStatusManager.syncStarted
import cn.pedant.SweetAlert.SweetAlertDialog
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import java.util.stream.Collectors

class ImprovedFarmList : AppCompatActivity(), OnImprovedItemClick, OnClickImprovedFarm, OnMailDone,OnClickSubmit,
    OnReceiptClick, ReceiptDialog.OnReceiptSubmit, ReceiptDialog.UploadReceiptImages,
    ReceiptDialog.OnReceiptUpdate, FinishDialog.SendData, FinishDialog.UploadImages {
    var showLoading: ShowLoading? = null
    var sharedprefrenceManager: SharedprefrenceManager? = null
    var isPending=false
    var apiService: ApiInterface? = null
    var startDataSend: StartDataSend? = null
    var apiService2: ApiInterface? = null
    var currentlat = ""
    var location: Location? = null
    var lastSent: Date? = null
    var rcptId: String? = "0"
    var entryDone =false
    var sendMail:Boolean = false
    var fusedLocationClient: FusedLocationProviderClient? = null
    var addresses: List<Address>? = ArrayList()
    var addresses1: List<Address>? = ArrayList()
    var isResumeRide = false
    var routeid: String = ""
    var parentId: String = ""
    private var timeRemaining: Long = 30000
    var isFinised = false
    var bagsDelivered = "0"
    var bagsDeliveredd = "0"
    var refTemp = "0"
    var temprature = "0"
    var isCounterCanceled = false
    var currentlng = ""
    var farmnamee: String? = ""
    private val triggeringArrivalEmails = HashSet<String>()
    private val queuedArrivalEmails = HashSet<String>()
    private val lastMailSentTime = HashMap<String, Long>() // Debounce: farmId -> timestamp
    var routeNamee: String? = ""
    var geocoder: Geocoder? = null
    var actionn=0;
    private var syncDialog: AlertDialog? = null
    var isPaused = false
    var startClicked = false
    var longitude = ""
    var latitude = ""
    var endodometer = ""
    var totalmiles = ""
    var endoil = ""
    var comments=""
    var shouldWaitForUpload = false
    var logStatus = "Start"
    var img1: MultipartBody.Part? = null
    var img2: MultipartBody.Part? = null
    var img3: MultipartBody.Part? = null
    var img4: MultipartBody.Part? = null
    private var receiptImgPath1: String = ""
    private var receiptImgPath2: String = ""
    private var receiptImgPath3: String = ""
    private var receiptImgPath4: String = ""
    private var pendingOfflineReceiptId: String? = null
    private var receiptListDialog: Dialog? = null
    var receiptAdapter: ReceiptAdapter? = null
    var btnFinishRide: Button? = null
    var alReceipts: java.util.ArrayList<ReceiptResponse?> = java.util.ArrayList<ReceiptResponse?>()
    private val SkyLabLatitude = 28.440766
    private val SkyLabLongitude = 77.070499
    private val LOCATION_PERMISSION = 101
    var gson: Gson? = GsonBuilder().setLenient().create()
    var rideStatus = "Start"
    var rideStart = false
    var bottomSheetDialogFragment: BottomSheetDialogFragment? = null
    var shouldStart = 0
    var orderDate=""
    var routeName=""
    var farmName=""
    var liRoutePlannerDetails = java.util.ArrayList<LiRoutePlannerDetail>()
    var shouldLogout: ShouldLogout? = null
    var farmDataList: MutableList<FarmData> = ArrayList<FarmData>()
    var farmlist: MutableList<ImprovedPriorityFarmData> = ArrayList<ImprovedPriorityFarmData>()
    var improvedAdapter: ImprovedFarmListAdapterByRote? = null
    //public class FarmListRoute extends AppCompatActivity implements FarmListAdapterByRote.AdapterbyRoute, OnStartDragListener {
    var recyclerView: RecyclerView? = null
    var recyclerViewReceipt: RecyclerView? = null

    var tvCancel: TextView? = null
    var btnAddReceipt: Button? = null
    var btnViewReceipt: Button? = null
    var viewClicked = false
    var clLargeImage: View? = null
    var ivLargeView: ImageView? = null
    var ivRefresh: ImageView? = null
    var ivClose: ImageView? = null
    var bgTransparent: View? = null
    var isResume: Boolean = false
    var rideIdd: Int = 0
    var routeIdd: Int = 0
    var updatedRouteId:Int = 0
    var parentIdd: Int = 0
    private var date = ""
    var farmIdd: Int = 0
    var rideId: String = "0"
    var shouldUpdateRouteAfterRefresh: Boolean = false

    private fun generateOfflineRideId(farmId: Int): String {
        return "OFF_${farmId}_${System.currentTimeMillis()}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_improved_farm_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        farmName = intent.getStringExtra("FarmName").toString()
        shouldStart = intent.getIntExtra("shouldstart", 0)
        actionn = intent.getIntExtra("actionn", 0)

        Log.d("Analysis__", "Intent farmid 249 is $shouldStart")

        routeName = intent.getStringExtra("RouteName").toString()
        showLoading = ShowLoading(this)
        apiService = ApiClient.getClient(this).create(ApiInterface::class.java)
        apiService2 = ApiClient.getClient2().create(ApiInterface::class.java)
        startDataSend = StartDataSend()
        sharedprefrenceManager = SharedprefrenceManager(this)
        routeid = sharedprefrenceManager!!.routeId
        val switchCompat = findViewById<SwitchCompat?>(R.id.switchOfflineMode)
        switchCompat.setVisibility(GONE)
        switchCompat.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            sharedprefrenceManager!!.setToggleState(isChecked)
            if (!isChecked){
                SyncScheduler.runImmediateSync(this)
            }
        })
        geocoder = Geocoder(this, Locale.getDefault())
        tvCancel = findViewById<TextView?>(R.id.tv_cancel)
        btnAddReceipt = findViewById(R.id.btn_add_receipt)
        btnViewReceipt = findViewById(R.id.btn_view_receipt)
        btnFinishRide = findViewById(R.id.endRoute)
        clLargeImage = findViewById(R.id.cl_largeImage)
        ivLargeView = findViewById(R.id.iv_largeview)
        ivRefresh = findViewById(R.id.refresh)
        ivClose = findViewById(R.id.ivClose)
        bgTransparent = findViewById(R.id.bgTrasnparent)
        val input = intent.getStringExtra("orderdate") ?: ""

        val regex = Regex("\\b\\d{2}/\\d{2}/\\d{4}\\b")
        val match = regex.find(input)
        if (match != null) {
            orderDate = match.value
        }

        if (orderDate.isEmpty()) {
            orderDate = sharedprefrenceManager!!.getDate()
            Log.d("ImprovedFarmList", "Using saved date from preferences: $orderDate")
        }

        shouldLogout = ShouldLogout(this, sharedprefrenceManager)
        recyclerView = findViewById<RecyclerView?>(R.id.recyclerview)
        recyclerViewReceipt = RecyclerView(this)
        routeid = sharedprefrenceManager!!.routeId
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))
        recyclerViewReceipt!!.setLayoutManager(LinearLayoutManager(this))
        receiptAdapter = ReceiptAdapter(this, this, alReceipts)
        getLocation()
        ivRefresh!!.setOnClickListener { getAllFarms() }
        recyclerViewReceipt!!.setAdapter(receiptAdapter)
        improvedAdapter = ImprovedFarmListAdapterByRote(
            this,
            this@ImprovedFarmList,
            farmlist,
"",
            routeid,
            this,sharedprefrenceManager
        )
        gson = GsonBuilder().setLenient().setPrettyPrinting().create()
        recyclerView!!.setAdapter(improvedAdapter)

        loadFromDB(false) // Always load from local DB first to ensure screen is not blank

        if (!sharedprefrenceManager!!.isSyncMode) {
            if (isInternetAvailable(this)) {
                getAllFarms()
            }
        }

        btnAddReceipt?.setOnClickListener {
            if (!sharedprefrenceManager!!.isSyncMode()) {
                if (!isInternetAvailable(this@ImprovedFarmList)) {
                    Toast.makeText(
                        this@ImprovedFarmList,
                        "No Internet Connection",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@setOnClickListener
                }
            }
            val receiptDialog = ReceiptDialog().instance(
                this,
                this,
                false,
                null,
                this,
                this
            )
            receiptDialog.show(supportFragmentManager, "ReceiptDialog")
        }
        if (sharedprefrenceManager!!.isSyncMode){
            getReceiptData()
        }
        btnViewReceipt?.setOnClickListener {
            viewClicked = true
            if (sharedprefrenceManager!!.isSyncMode){
               getReceiptData()
            }else {
                if (!isInternetAvailable(this@ImprovedFarmList)) {
                    Toast.makeText(
                        this@ImprovedFarmList,
                        "No Internet Connection",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@setOnClickListener
                }
                getReceiptData()
            }
        }

        ivClose?.setOnClickListener {
            clLargeImage?.visibility = View.GONE
            bgTransparent?.visibility = View.GONE
        }

        tvCancel?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!isInternetAvailable(this@ImprovedFarmList)) {
                    Toast.makeText(this@ImprovedFarmList, "No Internet Connection", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                if (areAllPhotosUploaded() == 1) {
                    cancelRide()
                } else {
                    showPendingImagesDialog(true)
                }
            }
        })
        btnFinishRide!!.setOnClickListener {
            if (!isInternetAvailable(this)) {
                Toast.makeText(this@ImprovedFarmList, "No internet Connection", Toast.LENGTH_SHORT)
                    .show()
            } else {
                btnFinishRide!!.setEnabled(false)
                if (actionn == 1) {
                    Toast.makeText(
                        this@ImprovedFarmList,
                        "Please complete started ride first.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (areAllPhotosUploaded() == 1) {
                        bottomSheetDialogFragment =
                            FinishDialog().instance(
                                "",
                                this@ImprovedFarmList,
                                this@ImprovedFarmList
                            )
                        bottomSheetDialogFragment!!.show(
                            getSupportFragmentManager(),
                            bottomSheetDialogFragment!!.getTag()
                        )
                    } else {
                        showPendingImagesDialog(false)
                    }
                }
                btnFinishRide!!.postDelayed(object : Runnable {
                    override fun run() {
                        btnFinishRide!!.setEnabled(true)
                    }
                }, 1000) // 1 second delay
            }
        }

        syncStarted.observe(this) { isSyncing ->
            if (isSyncing) {
                runOnUiThread {
                    ivRefresh!!.visibility=GONE
                    showSyncDialog()
                }
            } else {
                runOnUiThread {
                   // ivRefresh!!.visibility = VISIBLE
                    ivRefresh!!.visibility=GONE
                    hideSyncDialog()
                }
            }
        }
    }
    fun showSyncDialog() {
        return
        if (syncDialog?.isShowing == true) return 

        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_sync, null)
        builder.setView(view)
        builder.setCancelable(true)
        val btn= view.findViewById<Button>(R.id.btnclose)
        btn.setOnClickListener { syncDialog?.dismiss() }
            syncDialog = builder.create()
        syncDialog?.show()
    }
    fun hideSyncDialog() {
        return
        syncDialog?.dismiss()
        syncDialog = null
    }
    private fun showPendingImagesDialog(isCancel: Boolean) {
        var negative = "End Ride Anyway"
        var message =
            "Some images are not uploaded yet. You can upload them now or end the ride anyway."
        if (isCancel) {
            message =
                "Some images are not uploaded yet. You can upload them now or cancel the ride annyway."
            negative = "Cancel Ride Anyway"
        }
        AlertDialog.Builder(this)
            .setTitle("Pending Images")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                "Upload Images",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    dialog!!.dismiss()
                })

            .setNegativeButton(
                negative,
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    if (isCancel) {
                        cancelRide()
                    } else {
                        bottomSheetDialogFragment =
                            FinishDialog().instance("", this@ImprovedFarmList, this@ImprovedFarmList)

                        bottomSheetDialogFragment!!.show(
                            getSupportFragmentManager(),
                            bottomSheetDialogFragment!!.getTag()
                        )
                    }
                })

            .show()
    }
    private fun checkRide() {

        if (!sharedprefrenceManager!!.toggleState) {
            val call = apiService!!.showPermission(
                sharedprefrenceManager!!.getDriverIdInt(),
                sharedprefrenceManager!!.getToken()
            )
            call.enqueue(object : Callback<PermissionModel?> {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(
                    call: Call<PermissionModel?>,
                    response: Response<PermissionModel?>
                ) {
                    if (response.code() == 401) {
                        shouldLogout!!.logout(this@ImprovedFarmList)

                    } else if (response.code() == 200) {

                        if (response.body()!!.getData() != null) {
                            val pData = response.body()!!.getData()
                            val pRideId = pData.getRideId()
                            if (pRideId != 0) {
                                rideIdd = pRideId
                                sharedprefrenceManager!!.setRideId(rideIdd)
                            } else {
                                // RCA Fix: If server returns 0 but we have a local ride, keep it in memory
                                rideIdd = sharedprefrenceManager!!.rideIdInt
                            }

                            routeIdd = pData.getRouteId()
                            updatedRouteId = pData.routeId
                            sharedprefrenceManager!!.setRouteId(updatedRouteId)
                            parentIdd = pData.getParentId()
                            val pFarmId = pData.getFarmId()
                            val localFid = sharedprefrenceManager!!.fid.toIntOrNull() ?: 0
                            
                            if (pFarmId != 0) {
                                // RCA Fix: Highly defensive FID management. 
                                // Only trust server if we have NO local active farm OR if they match.
                                if (localFid == 0 || localFid == pFarmId) {
                                    farmIdd = pFarmId
                                    sharedprefrenceManager!!.setFID(farmIdd.toString())
                                } else {
                                    // Local FID exists and is different from server. 
                                    // Keep local FID as the source of truth.
                                    farmIdd = localFid
                                }
                            } else {
                                // Server says 0, but check if we have a local active ride
                                val rideIdStr = sharedprefrenceManager!!.getRideId()
                                val rideActive = (rideIdd != 0) || (rideIdStr != "0" && rideIdStr != "" && rideIdStr != "null")
                                
                                if (rideActive || localFid != 0) {
                                    farmIdd = localFid
                                } else {
                                    farmIdd = 0
                                    sharedprefrenceManager!!.setFID("0")
                                }
                            }
                            actionn = pData.getAction()
                            farmnamee = pData.getFarmName()
                            routeNamee = pData.getRouteName()
                            if (pData.getIsPaused() == 1) {
                                sharedprefrenceManager!!.setIsPause(1)
                            }
                            if (!pData.orderDate.isEmpty()) {
                                val formattedDate =
                                    FarmListRoute.formatToMMDDYYYY(pData.getOrderDate())
                                sharedprefrenceManager!!.setImprovedDate(formattedDate)
                            }
                            if (pData.getParentId() != 0) {
                                date = FarmListRoute.formatToMMDDYYYY(pData.getOrderDate())
                            }

                            if (parentIdd != 0 && rideIdd == 0) {
                                isResume = false
                            } else if (rideIdd != 0 && farmIdd == 0) {
                                isResume = false
                            } else if (rideIdd != 0 && farmIdd != 0) {
                                isResume = true
                                Executors.newSingleThreadExecutor().execute {
                                    val db = AppDatabase.getDatabase(this@ImprovedFarmList)

                                    db.improvedPriorityFarmDao().resetActiveFarms(orderDate)
                                    db.improvedPriorityFarmDao()
                                        .markFarmAsActive(farmIdd, orderDate)

                                    val updatedList =
                                        db.improvedPriorityFarmDao().getFarmsByDate(orderDate)

                                    runOnUiThread {
                                        farmlist.clear()
                                        farmlist.addAll(updatedList)
                                        improvedAdapter?.notifyDataSetChanged()
                                    }
                                }
                            } else {
                            }

                            if (rideIdd != 0) {
                                isResumeRide = true

                                Executors.newSingleThreadExecutor().execute {
                                    val db = AppDatabase.getDatabase(this@ImprovedFarmList)

                                    val activeFarm = db.improvedPriorityFarmDao()
                                        .getActiveFarm(sharedprefrenceManager!!.getDate())

                                    runOnUiThread {
                                        if (activeFarm != null) {
                                            improvedAdapter?.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }

                            if (parentIdd == 0 && rideIdd == 0 && farmlist.isEmpty()) {
                                sharedprefrenceManager!!.clearRideState()
                                Toast.makeText(this@ImprovedFarmList, "No active ride found. Redirecting...", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@ImprovedFarmList, SelectCar::class.java))
                                finish()
                            }
                        }
                    } else {
                        try {
                            val loginError = gson!!.fromJson<CommonError?>(
                                response.errorBody()!!.string(),
                                CommonError::class.java
                            )
                            Log.e("Data  Show Permission", loginError.getMessage())
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<PermissionModel?>, t: Throwable) {
                }
            })
        }
    }

    override fun uploadImages(img1: MultipartBody.Part?, img2: MultipartBody.Part?) {
        shouldWaitForUpload = true
        showLoading!!.show()
        val call = apiService!!.uploadEndImage(
            sharedprefrenceManager!!.getParentId(),
            img1,
            img2
        )
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                showLoading!!.dismiss()
                if (response.code() == 200) {
                    FinishData(endodometer, totalmiles, endoil)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
        })
    }

    override fun finishData(endodometer: String?, totalmiles: String?, endoil: String?) {
        this.endodometer = endodometer ?: ""
        this.totalmiles = totalmiles ?: ""
        this.endoil = endoil ?: ""
           if (shouldWaitForUpload) {
        } else {
            FinishData(endodometer, totalmiles, endoil)
        }
    }
    @SuppressLint("MissingPermission")
    fun FinishData(endodometer: String?, totalmiles: String?) {

        Log.d("Offline__", "Start FinishData")

        var address: String? = "Unknown"
        var city: String? = "Unknown"
        var state: String? = "Unknown"
        var country: String? = "Unknown"

        try {
            if (location != null && geocoder != null) {
                val addresses = geocoder?.getFromLocation(
                    location!!.latitude,
                    location!!.longitude,
                    1
                )

                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    address = addr.getAddressLine(0) ?: "Unknown"
                    city = addr.locality ?: "Unknown"
                    state = addr.adminArea ?: "Unknown"
                    country = addr.countryName ?: "Unknown"
                }
            }
        } catch (e: Exception) {
            Log.e("GEOCODER_ERROR", e.message ?: "Geocoder failed")
        }

        var rideIdToUse = this@ImprovedFarmList.rideId
        if (rideIdToUse.isNullOrEmpty() || rideIdToUse == "0") {
            val prefRideId = sharedprefrenceManager?.rideId
            if (!prefRideId.isNullOrEmpty() && prefRideId != "0") {
                rideIdToUse = prefRideId
            }
        }
        if (rideIdToUse.isNullOrEmpty()) {
            rideIdToUse = "0"
        }

        val rideFinishRequest = RideFinishRequest().apply {
            action = "End"
            rideId = rideIdToUse
            uid = sharedprefrenceManager?.driverID
            lat = latitude ?: "0.0"
            lng = longitude ?: "0.0"
            endOdometer = endodometer
            totalMiles = totalmiles
            commentsDelivered = comments
            this.address = address
            this.city = city
            this.state = state
            this.country = country
            firmId = farmIdd.toString()
            this.bagsDelivered = bagsDeliveredd
            temperature = temprature
            routeId = routeid
            customerSeemanCoolarTemp = refTemp
            token = sharedprefrenceManager?.token
        }

         if (sharedprefrenceManager!!.isSyncMode){
            Thread {
                try {
                    val db = AppDatabase.getDatabase(this@ImprovedFarmList)

                    val entity = RideFinishEntity(
                        comments = rideFinishRequest.commentsDelivered,
                        EndDateTime = getCurrentTime(),
                        action = rideFinishRequest.action,
                        rideId = rideFinishRequest.rideId,
                        uid = rideFinishRequest.uid,
                        lat = rideFinishRequest.lat,
                        lng = rideFinishRequest.lng,
                        endOdometer = rideFinishRequest.endOdometer,
                        totalMiles = rideFinishRequest.totalMiles,
                        address = rideFinishRequest.address,
                        state = rideFinishRequest.state,
                        city = rideFinishRequest.city,
                        country = rideFinishRequest.country,
                        firmId = rideFinishRequest.firmId,
                        bagsDelivered = rideFinishRequest.bagsDelivered,
                        temperature = rideFinishRequest.temperature,
                        routeId = rideFinishRequest.routeId,
                        coolerTemp = rideFinishRequest.customerSeemanCoolarTemp,
                        driverId = rideFinishRequest.uid,
                        Token = rideFinishRequest.token
                    )

                    db.rideFinishDao().insert(entity)

                    db.improvedPriorityFarmDao().updateImprovedFarmTempBags(
                        rideFinishRequest.firmId?.toIntOrNull() ?: 0,
                        FarmListRoute.formatToMMDDYYYY(sharedprefrenceManager?.date),
                        rideFinishRequest.bagsDelivered?.toIntOrNull() ?: 0,
                        rideFinishRequest.temperature?.toDoubleOrNull() ?: 0.0,
                        1,
                        1,
                        1
                    )

                    actionn = 0

                    runImmediateSync(this)
                    loadFromDB(false)
                } catch (e: Exception) {
                    Log.e("DB_ERROR", e.message ?: "DB insert failed")
                }

            }.start()
        }

        else {
            val call = apiService?.rideFinish(
                rideFinishRequest,
                sharedprefrenceManager?.driverID,
                sharedprefrenceManager?.getToken()
            )

            call?.enqueue(object : Callback<CommonError?> {

                override fun onResponse(
                    call: Call<CommonError?>,
                    response: Response<CommonError?>
                ) {
                    if (response.isSuccessful) {
                        Log.d("Offline__", "API success")
                        sendLog("End")
                    } else {
                        try {
                            val loginError = gson?.fromJson(
                                response.errorBody()?.string(),
                                CommonError::class.java
                            )
                            Log.e("API_ERROR", loginError?.message ?: "Unknown error")
                        } catch (e: Exception) {
                        }
                    }
                }

                override fun onFailure(call: Call<CommonError?>, t: Throwable) {
                    Log.e("Offline__", "API Failed: ${t.message}")

                    if (!sharedprefrenceManager!!.isSyncMode()) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ImprovedFarmList,
                                "Unable to finish ride without internet in Online mode.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return
                    }
                }
            })
        }
    }
    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return sdf.format(Date())
    }

    fun FinishData(endodometer: String?, totalmiles: String?, endoil: String?) {
        try {
            var address: String? = ""
            var city: String? = ""
            var state: String? = ""
            var country: String? = ""
            if (location != null) {
                addresses1 = geocoder!!.getFromLocation(
                    location!!.getLatitude(),
                    location!!.getLongitude(),
                    1
                )
                address = addresses1!!.get(0).getAddressLine(0)
                city = addresses1!!.get(0).getLocality()
                state = addresses1!!.get(0).getAdminArea()
                country = addresses1!!.get(0).getCountryName()
            }

            val rideFinishRequest = RideFinishRequest()
            rideFinishRequest.setAction("End")
            rideFinishRequest.setRideId(sharedprefrenceManager!!.getParentId() + "")
            rideFinishRequest.setUID(sharedprefrenceManager!!.getDriverID())
            rideFinishRequest.setLat(latitude.toString() + "")
            rideFinishRequest.setLng(longitude.toString() + "")
            rideFinishRequest.setEndOdometer(endodometer)
            rideFinishRequest.setEndOilPercent(endoil)
            rideFinishRequest.setTotalMiles(totalmiles)
            rideFinishRequest.setAddress(address)
            rideFinishRequest.setState(state)
            rideFinishRequest.setCity(city)
            rideFinishRequest.setRouteId(routeid + "")
            rideFinishRequest.setCountry(country)
            rideFinishRequest.token=sharedprefrenceManager!!.token

            val call = apiService!!.rideFinish(
                rideFinishRequest,
                sharedprefrenceManager!!.getDriverID(),
                sharedprefrenceManager!!.getToken()
            )

            call.enqueue(object : Callback<CommonError?> {
                override fun onResponse(
                    call: Call<CommonError?>,
                    response: Response<CommonError?>
                ) {
                    if (response.code() == 200) {
                        sharedprefrenceManager!!.clearRideState()
                        shouldLogout!!.shouldLogout(true, this@ImprovedFarmList)
                    } else try {
                        val loginError = gson!!.fromJson<CommonError?>(
                            response.errorBody()!!.string(),
                            CommonError::class.java
                        )
                        Log.e("Data", loginError.getMessage())
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CommonError?>, t: Throwable) {
                    Log.e("errorratro", t.message!!)
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onImprovedItemClick(model: ImprovedPriorityFarmData?) {
    }

    override fun onMailDone(value: Boolean?) {
        loadFromDB(false)
    }
    private fun getReceiptData() {
        Executors.newSingleThreadExecutor().execute {
            val db = getDatabase(this@ImprovedFarmList)
            val localList = db.receiptDao().getReceiptsByParentId(sharedprefrenceManager!!.parentId)
            runOnUiThread {
                alReceipts.clear()
                for (receipt in localList) {
                    val receiptResponse = ReceiptResponse().apply {
                        parentId = receipt.ParentId
                        id = receipt.ID
                        setRID(receipt.RID)
                        fuelCost = receipt.FuelCost
                        fuelOdometer = receipt.FuelOdometer
                        gallon = receipt.Gallon
                        pricePgallon = receipt.PricePgallon
                        recipt_Img = receipt.Recipt_Img
                        washCost = receipt.WashCost
                    }
                    alReceipts.add(receiptResponse)
                }
                if (viewClicked) {
                    showReceiptsDialog()
                    // Don't reset viewClicked here, let the network call also try to update/show if needed
                    // or better, reset it here and have network call update the UI if dialog is already showing
                }
            }
        }

        val call = apiService!!.getReceipts(sharedprefrenceManager!!.getParentIdInt(),sharedprefrenceManager!!.driverID,sharedprefrenceManager!!.token)
        call.enqueue(object : Callback<ReceiptResponseMain?> {
            override fun onResponse(
                call: Call<ReceiptResponseMain?>,
                response: Response<ReceiptResponseMain?>
            ) {
                showLoading?.dismiss()
                if (response.code() == 200 && response.body() != null) {
                    val alRcpts = response.body()!!.alReciptResponse
                    
                    Executors.newSingleThreadExecutor().execute(Runnable {
                        val db = getDatabase(this@ImprovedFarmList)
                        try {
                            val localMap = db.receiptDao().getReceiptsByParentId(sharedprefrenceManager!!.parentId)
                                .associateBy { it.RID }

                            for (receipt in alRcpts) {
                                var imageToSave = receipt?.recipt_Img
                                
                                if (imageToSave.isNullOrEmpty() || imageToSave.length <= 6) {
                                    val localEntity = localMap[receipt?.getRID()]
                                    if (localEntity != null && !localEntity.Recipt_Img.isNullOrEmpty()) {
                                        imageToSave = localEntity.Recipt_Img
                                    }
                                }

                                val entity = ReceiptEntity(
                                    RID = receipt?.getRID() ?: "",
                                    ID = receipt?.getID(),
                                    Recipt_Img = imageToSave,
                                    ParentId = receipt?.parentId,
                                    FuelOdometer = receipt?.fuelOdometer,
                                    Gallon = receipt?.gallon,
                                    PricePgallon = receipt?.pricePgallon,
                                    FuelCost = receipt?.fuelCost,
                                    WashCost = receipt?.washCost
                                )
                                db.receiptDao().insertReceipt(entity)
                            }

                            val updatedList = db.receiptDao().getReceiptsByParentId(sharedprefrenceManager!!.parentId)
                            runOnUiThread {
                                alReceipts.clear()
                                for (receipt in updatedList) {
                                    val resp = ReceiptResponse().apply {
                                        parentId = receipt.ParentId
                                        id = receipt.ID
                                        setRID(receipt.RID)
                                        fuelCost = receipt.FuelCost
                                        fuelOdometer = receipt.FuelOdometer
                                        gallon = receipt.Gallon
                                        pricePgallon = receipt.PricePgallon
                                        recipt_Img = receipt.Recipt_Img
                                        washCost = receipt.WashCost
                                    }
                                    alReceipts.add(resp)
                                }
                                if (viewClicked) {
                                    showReceiptsDialog()
                                    viewClicked = false
                                }
                            }

                        } catch (e: Exception) {
                            Log.e("Sync_Error", "Failed to update receipts from network: ${e.message}")
                        }
                    })
                } else {
                    try {
                        val loginError = gson!!.fromJson<CommonError?>(
                            response.errorBody()!!.string(),
                            CommonError::class.java
                        )
                        Toast.makeText(this@ImprovedFarmList, loginError.message, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ReceiptResponseMain?>, t: Throwable) {
                showLoading?.dismiss()
                Log.e("errorratro", t.message ?: "Unknown error")
                Executors.newSingleThreadExecutor().execute(Runnable {
                    val db = getDatabase(this@ImprovedFarmList)
                    val updatedList =
                        db.receiptDao().getReceiptsByParentId(sharedprefrenceManager!!.parentId)

                    runOnUiThread {
                        alReceipts.clear()
                        for (receipt in updatedList) {
                            val receiptResponse= ReceiptResponse()
                            receiptResponse.parentId=receipt.ParentId
                            receiptResponse.setID(receipt.ID)
                            receiptResponse.setRID(receipt.RID)
                            receiptResponse.fuelCost=receipt.FuelCost
                            receiptResponse.fuelOdometer=receipt.FuelOdometer
                            receiptResponse.gallon=receipt.Gallon
                            receiptResponse.pricePgallon=receipt.PricePgallon
                            receiptResponse.recipt_Img=receipt.Recipt_Img
                            receiptResponse.washCost=receipt.WashCost
                            alReceipts.add(receiptResponse)
                        }
                        if (viewClicked) {
                            showReceiptsDialog()
                            viewClicked = false
                        }
                    }
                })
            }
        })
    }

    private fun showReceiptsDialog() {
        if (receiptListDialog?.isShowing == true) {
            val rvReceipts = receiptListDialog!!.findViewById<RecyclerView>(R.id.rv_receipts)
            val tvNoData = receiptListDialog!!.findViewById<TextView>(R.id.tv_no_data)
            rvReceipts.adapter?.notifyDataSetChanged()
            
            if (alReceipts.isEmpty()) {
                tvNoData.visibility = View.VISIBLE
                rvReceipts.visibility = View.GONE
            } else {
                tvNoData.visibility = View.GONE
                rvReceipts.visibility = View.VISIBLE
            }
            return
        }
        
        receiptListDialog = Dialog(this)
        val dialog = receiptListDialog!!
        dialog.setContentView(R.layout.dialog_receipt_list)
        dialog.window?.apply {
            setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val rvReceipts = dialog.findViewById<RecyclerView>(R.id.rv_receipts)
        val ivCloseDialog = dialog.findViewById<ImageView>(R.id.iv_close)
        val tvNoData = dialog.findViewById<TextView>(R.id.tv_no_data)

        rvReceipts.layoutManager = LinearLayoutManager(this)
        val adapter = ReceiptAdapter(this, this, alReceipts as ArrayList<ReceiptResponse>)
        rvReceipts.adapter = adapter

        if (alReceipts.isEmpty()) {
            tvNoData.visibility = View.VISIBLE
            rvReceipts.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            rvReceipts.visibility = View.VISIBLE
        }
        ivCloseDialog.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onReceiptClick(
        position: Int,
        receiptDetails: ReceiptResponse?,
        isImage: Boolean
    ) {
        if (isImage) {
            showImageDialog(receiptDetails!!.recipt_Img)
        } else {
            val receiptDialog = ReceiptDialog().instance(
                this,
                this,
                true,
                receiptDetails,
                this,
                this
            )
            receiptDialog.show(supportFragmentManager, "ReceiptDialog")
        }
    }
    private fun showImageDialog(imageUrl: String?) {
        if (imageUrl.isNullOrEmpty() || imageUrl.length <= 6) return
        Log.d("ImageDialog", "Showing image dialog"+imageUrl)
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_image_preview)

        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(ColorDrawable(Color.BLACK))
        }

        val imageView = dialog.findViewById<ImageView>(R.id.iv_preview)
        val closeBtn = dialog.findViewById<ImageView>(R.id.iv_close)

        val imageSource: Any = if (imageUrl.startsWith("http")) {
            imageUrl
        } else {
            File(imageUrl)
        }

        Glide.with(this)
            .load(imageSource)
            .placeholder(R.drawable.ic_doc) 
            .into(imageView)

        closeBtn.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onReceiptSubmit(receiptDetails: ReceiptResponse?) {
        receiptDetails!!.setParentId(sharedprefrenceManager!!.getParentId())
        val generatedRID = System.currentTimeMillis().toString() + ""
        rcptId = generatedRID
        receiptDetails.setRID(rcptId)
        Log.d("Analysis__", "onReceiptSubmit: Generated RID = $rcptId")

            if (sharedprefrenceManager!!.isSyncMode){
                pendingOfflineReceiptId = rcptId
                queueReceiptForSync(receiptDetails)
                return
            }else {
                if (!isInternetAvailable(this)) {

                    Toast.makeText(
                        this@ImprovedFarmList,
                        "Internet is required in Online mode.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            }
        showLoading?.show()
        val call = apiService!!.ReceiptDetails(receiptDetails,sharedprefrenceManager!!.driverID,sharedprefrenceManager!!.token)
        call.enqueue(object : Callback<CommonError?> {
            override fun onResponse(call: Call<CommonError?>, response: Response<CommonError?>) {
                showLoading?.dismiss()
                if (response.code() == 200) {
                    val serverId = response.body()?.data
                    Log.d("Analysis__", "onReceiptSubmit: Success. RID = $rcptId, Server ID = $serverId")
                    if (!serverId.isNullOrEmpty()) {
                        rcptId = serverId
                        Log.d("Analysis__", "onReceiptSubmit: Updated rcptId to Server ID: $rcptId")
                    }
                    if (img1 != null || img2 != null || img3 != null || img4 != null) {
                        Log.d("Analysis__", "onReceiptSubmit: Images found, starting uploadRcpts")
                        uploadRcpts()
                    } else {
                        Log.d("Analysis__", "onReceiptSubmit: No images to upload")
                    }
                    Toast.makeText(this@ImprovedFarmList, "Receipt submitted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Analysis__", "onReceiptSubmit: Failed with code ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("Analysis__", "onReceiptSubmit: Error Body = $errorBody")
                    Toast.makeText(this@ImprovedFarmList, "Failed to submit receipt", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CommonError?>, t: Throwable) {
                showLoading?.dismiss()
                if(sharedprefrenceManager!!.isSyncMode){
                    Toast.makeText(this@ImprovedFarmList, "Receipt saved offline.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun uploadRcpts() {
        showLoading!!.show()
        Log.d("Analysis__", "uploadRcpts: Uploading images for RID = $rcptId")
        val call = apiService!!.uploadReceiptImg(sharedprefrenceManager!!.token, rcptId, img1, img2, img3, img4)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                showLoading!!.dismiss()
                if (response.code() == 200) {
                    Log.d("Analysis__", "uploadRcpts: Success")
                    clearReceiptImages()
                } else {
                    Log.e("Analysis__", "uploadRcpts: Failed with code ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("Analysis__", "uploadRcpts: Error Body = $errorBody")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                showLoading!!.dismiss()
                Log.e("errorratro", t.message!!)
            }
        })
    }
    override fun uploadReceiptImages(
        isUpdate: Boolean,
        img1: MultipartBody.Part?,
        img2: MultipartBody.Part?,
        img3: MultipartBody.Part?,
        img4: MultipartBody.Part?,
        imgPath1: String?,
        imgPath2: String?,
        imgPath3: String?,
        imgPath4: String?,
        receiptDetails: ReceiptResponse
    ) {
        this.img1 = img1
        this.img2 = img2
        this.img3 = img3
        this.img4 = img4
        this.receiptImgPath1 = imgPath1 ?: ""
        this.receiptImgPath2 = imgPath2 ?: ""
        this.receiptImgPath3 = imgPath3 ?: ""
        this.receiptImgPath4 = imgPath4 ?: ""

        Log.d("Analysis__", "uploadReceiptImages: isUpdate = $isUpdate, RID = ${receiptDetails.getRID()}, img1 is null? = ${img1 == null}")

        if (img1 != null || img2 != null || img3 != null || img4 != null) {
            if (!sharedprefrenceManager!!.isSyncMode) {
                if (isUpdate) {
                    showLoading?.show()
                    val idToUse = if (!receiptDetails.getRID().isNullOrEmpty()) receiptDetails.getRID() else receiptDetails.getID() ?: sharedprefrenceManager!!.parentId
                    Log.d("Analysis__", "uploadReceiptImages: Update case, using ID = $idToUse")
                    val call = apiService!!.uploadReceiptImg(sharedprefrenceManager!!.token, idToUse, img1, img2, img3, img4)
                    call.enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                            showLoading?.dismiss()
                            if (response.code() == 200) {
                                Log.d("Analysis__", "uploadReceiptImages: Update Success")
                                clearReceiptImages()
                                Toast.makeText(this@ImprovedFarmList, "Images updated successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("Analysis__", "uploadReceiptImages: Update Failed with code ${response.code()}")
                                Toast.makeText(this@ImprovedFarmList, "Failed to upload images", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            showLoading?.dismiss()
                            Log.e("Analysis__", "uploadReceiptImages: Update Error", t)
                            Toast.makeText(this@ImprovedFarmList, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            } else {
                val db = AppDatabase.getDatabase(this)
                val idForOffline = if (!receiptDetails.getID().isNullOrEmpty()) receiptDetails.getID()
                                  else if (!receiptDetails.getRID().isNullOrEmpty()) receiptDetails.getRID()
                                  else rcptId ?: ""
                Log.d("Analysis__", "uploadReceiptImages: SyncMode, queueing photos with RID/ID = $idForOffline")
                
                if (receiptImgPath1.isNotEmpty()) {
                    queueReceiptPhotoIfPresent(db, idForOffline, receiptImgPath1, 1, receiptDetails)
                }
                if (receiptImgPath2.isNotEmpty()) {
                    queueReceiptPhotoIfPresent(db, idForOffline, receiptImgPath2, 2, receiptDetails)
                }
                if (receiptImgPath3.isNotEmpty()) {
                    queueReceiptPhotoIfPresent(db, idForOffline, receiptImgPath3, 3, receiptDetails)
                }
                if (receiptImgPath4.isNotEmpty()) {
                    queueReceiptPhotoIfPresent(db, idForOffline, receiptImgPath4, 4, receiptDetails)
                }
            }
        }
    }

    private fun clearReceiptImages() {
        img1 = null
        img2 = null
        img3 = null
        img4 = null
        receiptImgPath1 = ""
        receiptImgPath2 = ""
        receiptImgPath3 = ""
        receiptImgPath4 = ""
    }

    private fun queueReceiptForSync(receiptDetails: ReceiptResponse) {
        try {
            val payload = gson!!.toJson(receiptDetails)
            Executors.newSingleThreadExecutor().execute {
                try {
                    val db = AppDatabase.getDatabase(this)
                    db.rideSyncDao().insert(
                        RideSyncEntity(
                            token = sharedprefrenceManager!!.token,
                            payload = payload,
                            apiType = "RECEIPT",
                            isSynced = false
                        )
                    )
                    db.receiptDao().insertReceipt(ReceiptEntity(
                        RID = receiptDetails.getRID() ?: "",
                        ID = receiptDetails.getID(),
                        Recipt_Img = receiptDetails.recipt_Img,
                        ParentId = receiptDetails.parentId,
                        FuelOdometer = receiptDetails.fuelOdometer,
                        Gallon = receiptDetails.gallon,
                        PricePgallon = receiptDetails.pricePgallon,
                        FuelCost = receiptDetails.fuelCost,
                        WashCost = receiptDetails.washCost
                    ))

                } catch (e: Exception) {
                    Log.e("DB_ERROR", "Failed to queue receipt details: ${e.message}")
                }
            }
            Toast.makeText(this,"Saved in offline mode. It will be synced later.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("DB_ERROR", "Failed to serialize receipt details: ${e.message}")
        }
    }


    private fun queueReceiptPhotoIfPresent(
        db: AppDatabase,
        receiptId: String,
        path: String,
        index: Int,receiptDetails: ReceiptResponse
    ) {
        val photo = com.sc.aipdriver.activities.room.PhotoSyncEntity().apply {
            imagePath = path
            apiType = "RECEIPT"
            farmId = receiptId
            rideId = sharedprefrenceManager!!.rideId
            driverId = sharedprefrenceManager!!.driverID
            Token = sharedprefrenceManager!!.token
            imageIndex = index
        }
        Executors.newSingleThreadExecutor().execute {
            db.photoSyncDao().insert(photo)
            SyncScheduler.runImmediateSync(this)
            db.receiptDao().insertReceipt(ReceiptEntity(
                RID = receiptId,
                ID = receiptDetails.getID(),
                Recipt_Img = path,
                ParentId = sharedprefrenceManager!!.getParentId(),
                FuelOdometer = receiptDetails.getFuelOdometer(),
                Gallon = receiptDetails.getGallon(),
                PricePgallon = receiptDetails.getPricePgallon(),
                FuelCost = receiptDetails.getFuelCost(),
                WashCost = receiptDetails.getWashCost()
            ))
        }
        img1 = null
        Toast.makeText(this,"Saved in offline mode. It will be synced later.", Toast.LENGTH_SHORT).show()


    }

    override fun uploadReceiptUpdate(id: String?) {
    }

    override fun onClickSubmit(
        clicked: Boolean,
        liRoutePlannerDetails: List<LiRoutePlannerDetail?>?,
        comments:String,
        senddMail:Boolean
    ) {
        this.sendMail = senddMail
            temprature = liRoutePlannerDetails!!.get(0)!!.temperatureOfSemenLoaded
            bagsDelivered = liRoutePlannerDetails.get(0)!!.numberOfBagsLoaded
            bagsDeliveredd = liRoutePlannerDetails.get(0)!!.numberOfBagsLoaded
            refTemp = liRoutePlannerDetails.get(0)!!.customerSeemanCoolarTemp
            Log.d("Offline__","line 2016")
            this.comments = comments
            FinishData("", "")

        if (senddMail) {
            val targetFarmId = if (farmIdd != 0) farmIdd.toString() else sharedprefrenceManager?.fid ?: ""
            if (targetFarmId.isNotEmpty() && targetFarmId != "0") {
                FinishDataMail(
                    targetFarmId,
                    routeid,
                    rideId,
                    true // Force send after photo upload
                )
            }
        }
    }

    override fun onPhotoUpload(isUpdate: Boolean, senddMail: Boolean) {
        loadFromDB(false)
        if (senddMail) {
            val targetFarmId = if (farmIdd != 0) farmIdd.toString() else sharedprefrenceManager?.fid ?: ""
            if (targetFarmId.isNotEmpty() && targetFarmId != "0") {
                FinishDataMail(
                    targetFarmId,
                    routeid,
                    rideId,
                    true // Force send after photo upload
                )
            }
        }
    }
    @JvmOverloads
    fun FinishDataMail(farmId: String?, routeId: String?, rideId: String?, force: Boolean = false) {
        if (farmId.isNullOrEmpty()) return
        val emailKey = "$farmId-$rideId"
        
        val currentTime = System.currentTimeMillis()
        val lastSent = lastMailSentTime[farmId] ?: 0L
        
        // Debounce: Don't send more than one mail every 10 seconds for the same farm unless force is specifically for photos
        if (!force && (currentTime - lastSent < 10000)) {
            Log.d("Analysis__", "FinishDataMail: Debounce triggered for $farmId")
            return
        }

        synchronized(queuedArrivalEmails) {
            if (!force && queuedArrivalEmails.contains(emailKey)) {
                Log.d("Analysis__", "FinishDataMail: Session check - Email already queued for $emailKey")
                return
            }
            
            val lastSent = lastMailSentTime[farmId] ?: 0L
            if (!force && (currentTime - lastSent < 3000)) { // Don't debounce when force is true for photo re-upload
                Log.d("Analysis__", "FinishDataMail: Debounce (Force-Aware) triggered for $farmId")
                return
            }

            queuedArrivalEmails.add(emailKey)
            lastMailSentTime[farmId] = currentTime
        }

        Executors.newSingleThreadExecutor().execute {
            val db = getDatabase(this@ImprovedFarmList)
            val date = FarmListRoute.formatToMMDDYYYY(sharedprefrenceManager?.date)
            val farm = db.improvedPriorityFarmDao().getFarmByIdAndDate(farmId.toIntOrNull() ?: 0, date)

            // If force is true, we want to re-send even if already marked as sent in DB
            // This is crucial for updated emails with photos
            if (!force && farm != null && farm.isEmailSent == 1) {
                Log.d("Analysis__", "FinishDataMail: DB check - Email already sent for $emailKey")
                return@execute
            }

            if (force && farm != null) {
                // Reset isEmailSent so worker or direct API knows it needs to be processed
                db.improvedPriorityFarmDao().updateEmailSent(farmId.toIntOrNull() ?: 0, date, 0)
            }

            runOnUiThread {
                sendActualMail(farmId, routeId, rideId, emailKey, force)
            }
        }
    }

    private fun sendActualMail(farmId: String, routeId: String?, rideId: String?, emailKey: String, force: Boolean = false) {
        Log.d("Sendingmail__", "Sending mail for $emailKey")

        var finalRideId = rideId
        if (finalRideId.isNullOrEmpty() || finalRideId == "0") {
            val prefRideId = sharedprefrenceManager?.rideId
            if (!prefRideId.isNullOrEmpty() && prefRideId != "0") {
                finalRideId = prefRideId
            }
        }

        val request = JsonObject().apply {
            addProperty("ParentId", sharedprefrenceManager!!.getParentId())
            addProperty("FIRMID", farmId)
            addProperty("rideId", finalRideId ?: "")
            addProperty("RouteId", routeId)
        }
        
        if (sharedprefrenceManager!!.isSyncMode) {
            queueFinishMailSync(request, farmId, force)
            return
        }
        
        if (!isInternetAvailable(this)) return

        try {
            val api = ApiClient.getHttp1Client(this@ImprovedFarmList).create<ApiInterface?>(ApiInterface::class.java)
            val call = api!!.rideFinishEmail(request, sharedprefrenceManager!!.driverID, sharedprefrenceManager!!.token)

            call.enqueue(object : Callback<CommonError?> {
                override fun onResponse(call: Call<CommonError?>, response: Response<CommonError?>) {
                    if (response.code() == 200) {
                        markEmailSentInDb(farmId)
                    }
                }
                override fun onFailure(call: Call<CommonError?>, t: Throwable) {
                    Log.e("errorratro", t.message!!)
                }
            })
        } catch (e: Exception) {
            queueFinishMailSync(request, farmId, force)
        }
    }

    private fun markEmailSentInDb(farmId: String) {
        Executors.newSingleThreadExecutor().execute {
            val db = getDatabase(this@ImprovedFarmList)
            db.improvedPriorityFarmDao().updateEmailSent(
                farmId.toIntOrNull() ?: 0,
                FarmListRoute.formatToMMDDYYYY(sharedprefrenceManager?.date),
                1
            )
        }
    }

    private fun queueFinishMailSync(request: JsonObject, farmId: String?, force: Boolean = false) {

        val token = sharedprefrenceManager?.token
        if (token.isNullOrEmpty()) {
            Log.e("Offline__", "Skipping FINISH_EMAIL queue: token missing")
            return
        }

        val payload = gson?.toJson(request) ?: request.toString()
        Executors.newSingleThreadExecutor().execute {
            try {
                val db = getDatabase(this@ImprovedFarmList)
                
                val pending = db.rideSyncDao().getPending()
                val existingEntity = pending.find { p ->
                    if (p.apiType == "FINISH_EMAIL") {
                        try {
                            val pJson = gson?.fromJson(p.payload, JsonObject::class.java)
                            pJson?.get("FIRMID")?.asString == farmId
                        } catch (e: Exception) { false }
                    } else false
                }

                if (existingEntity != null) {
                    if (force) {
                        // Delete old pending entry to re-queue with updated force payload
                        db.rideSyncDao().deleteById(existingEntity.id)
                    } else {
                        Log.d("Offline__", "Skipping FINISH_EMAIL queue: email for farm $farmId is already pending sync")
                        return@execute
                    }
                }

                db.rideSyncDao().insert(
                    RideSyncEntity(
                        token = token,
                        payload = payload,
                        apiType = "FINISH_EMAIL",
                        isSynced = false
                    )
                )
                
                SyncScheduler.runImmediateSync(this)
                Log.d("Offline__", "Finish mail queued for sync")
            } catch (e: Exception) {
                Log.e("DB_ERROR", "Failed to queue FINISH_EMAIL: ${e.message}")
            }
        }
    }

    private fun logout() {
        shouldLogout?.shouldLogout(true,this@ImprovedFarmList)
    }

    private fun cancelRide() {
        val call = apiService!!.cancelRide(sharedprefrenceManager!!.getParentId(),sharedprefrenceManager!!.getDriverID(), sharedprefrenceManager!!.getToken())
        call.enqueue(object : Callback<BaseResponse<*>?> {
            override fun onResponse(
                call: Call<BaseResponse<*>?>,
                response: Response<BaseResponse<*>?>
            ) {
                if (response.code() == 200) {
                    sharedprefrenceManager!!.clearRideState()
                    logout()
                }
            }

            override fun onFailure(call: Call<BaseResponse<*>?>, t: Throwable) {
                Log.e("Data  errorratro", t.message!!)
                Toast.makeText(this@ImprovedFarmList, "No internt Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    fun areAllPhotosUploaded(): Int {
        for (item in farmlist) {
            if (item.getIsCompleted() == 1) {
                if (item.getIsPhotoUploaded() == 0) {
                    return 0
                }
            }
        }
        return 1
    }

    private fun startRide(model: ImprovedPriorityFarmData?) {
        if (rideId==""){
            rideId="0"
        }
        sharedprefrenceManager!!.setRideIdString(rideId)
        rideId = model!!.rideId
        rideIdd = sharedprefrenceManager!!.getRideIdInt()
            if (actionn==1){
                Toast.makeText(this,"Please complete started ride first.", Toast.LENGTH_SHORT).show()
            }
            else {
                if (!startClicked) {
                    Log.d("Analysis__", "Intent farmid 173 is $shouldStart")

                        startClicked = true
                    logStatus ="Start"
                        rideStatus = "Start"
                        liRoutePlannerDetails.clear()
                        val liRoutePlannerDetail = LiRoutePlannerDetail()
                        liRoutePlannerDetail.firmid = (model!!.id).toString()
                        liRoutePlannerDetail.numberOfBagsLoaded = "0"
                        liRoutePlannerDetail.temperatureOfSemenLoaded = "0"

                        liRoutePlannerDetails.add(liRoutePlannerDetail)
                    latitude=model.latitude
                    longitude=model.longitude
                        sendToServer(liRoutePlannerDetails,model)


                }
            }


    }

    private fun completeRide(model: ImprovedPriorityFarmData?) {

        logStatus = "End"
        rideStatus = "End"
        farmIdd = model!!.id
        farmnamee = model.farmName

        var actualRideId = model.rideId
        if (actualRideId.isNullOrEmpty() || actualRideId == "0") {
            val prefRideId = sharedprefrenceManager?.rideId
            if (!prefRideId.isNullOrEmpty() && prefRideId != "0") {
                actualRideId = prefRideId
            }
        }
        if (actualRideId.isNullOrEmpty()) {
            actualRideId = "0"
        }
        rideId = actualRideId
        model.rideId = actualRideId
        rideIdd = if (rideId.startsWith("OFF_")) 0 else rideId.toIntOrNull() ?: 0

        sharedprefrenceManager!!.setRideIdString(rideId)

        Executors.newSingleThreadExecutor().execute {
            val db = AppDatabase.getDatabase(this@ImprovedFarmList)
            db.improvedPriorityFarmDao().updateRideId(model.id, FarmListRoute.formatToMMDDYYYY(model.orderDate), rideId)
        }

        bottomSheetDialogFragment = null
        bottomSheetDialogFragment =
            LoadTemprature().instance(
                this@ImprovedFarmList,
                "" + model!!.id,
                routeNamee!!,
                routeid,
                true,
                this@ImprovedFarmList,0
                ,0.0,
                sharedprefrenceManager!!.getDate()
            )
        bottomSheetDialogFragment!!.show(
            supportFragmentManager, bottomSheetDialogFragment!!.getTag()
        )
    }

    private fun sendToServer(liRoutePlannerDetails: List<LiRoutePlannerDetail?>?, model: ImprovedPriorityFarmData) {
        startDataSend!!.driverId = sharedprefrenceManager!!.driverID
        startDataSend!!.routId = sharedprefrenceManager!!.routeId
        startDataSend!!.startOdometer = "0"
        startDataSend!!.uid = sharedprefrenceManager!!.driverID
        startDataSend!!.parentId = sharedprefrenceManager!!.getParentIdInt()
        startDataSend!!.vehicleId = sharedprefrenceManager!!.vehicleId
        startDataSend!!.action = rideStatus
        startDataSend!!.lat=currentlat
        startDataSend!!.lng=currentlng
        startDataSend!!.rideId = if (model.rideId.startsWith("OFF_")) 0 else model.rideId.toIntOrNull() ?: 0
        if (rideStatus.equals("Start")) {
            startDataSend!!.orderDate=model.orderDate;

            // ✅ Use a flag to ensure the "UPDATED ARRIVAL TIME" email is sent only once per farm/ride
            val arrivalKey = "${model.id}-${model.orderDate}"
            
            Log.d("DEBUG_EMAIL", "[ImprovedFarmList] Start Clicked for farm ${model.id}. orderDate=${model.orderDate}")

            // ✅ Independent guard for individual ETA email
            if (!triggeringArrivalEmails.contains(arrivalKey) && !sharedprefrenceManager!!.isMailSent("ArrivalETA", sharedprefrenceManager!!.routeId, model.id.toString(), model.orderDate)) {
                Log.d("DEBUG_EMAIL", "[ImprovedFarmList] === TRIGGERING dispatchETAMail API START ===")
                triggeringArrivalEmails.add(arrivalKey)

                val emailRequest = EmailRequest().apply {
                    driverid = sharedprefrenceManager!!.driverID
                    parentid = sharedprefrenceManager!!.parentId
                    fid = model.id.toString()
                    routeId = sharedprefrenceManager!!.routeId
                    vehicleId = sharedprefrenceManager!!.vehicleId
                    lat = currentlat
                    lng = currentlng
                }

                // ✅ Mark as sent locally immediately
                sharedprefrenceManager!!.setMailSent("ArrivalETA", sharedprefrenceManager!!.routeId, model.id.toString(), model.orderDate, true)

                apiService2!!.dispatchETAMail(emailRequest, sharedprefrenceManager!!.token).enqueue(object : Callback<CommonError> {
                    override fun onResponse(call: Call<CommonError>, response: Response<CommonError>) {
                        Log.d("DEBUG_EMAIL", "[ImprovedFarmList] dispatchETAMail Response: ${response.code()}")
                    }

                    override fun onFailure(call: Call<CommonError>, t: Throwable) {
                        Log.e("DEBUG_EMAIL", "[ImprovedFarmList] dispatchETAMail Failure: ${t.message}")
                        triggeringArrivalEmails.remove(arrivalKey)
                    }
                })
            } else {
                Log.d("DEBUG_EMAIL", "[ImprovedFarmList] Skipping ETA mail: Already sent.")
            }
        }
        startDataSend!!.liRoutePlannerDetail = liRoutePlannerDetails

        if (!isInternetAvailable(this)) {
            if (!sharedprefrenceManager!!.isSyncMode()) {
                Toast.makeText(this, "Internet is required in Online mode.", Toast.LENGTH_SHORT).show()
                return
            }
             val offlineRideId = generateOfflineRideId(model.id)
             rideId = offlineRideId
             rideStart = true
             actionn = 1
             startDataSend!!.offlineRideId = offlineRideId
             
             val payload = gson!!.toJson(startDataSend)
             Executors.newSingleThreadExecutor().execute {
                 val db = AppDatabase.getDatabase(this)
                 db.rideSyncDao().insert(RideSyncEntity(token = sharedprefrenceManager!!.token, payload = payload, apiType = "START", isSynced = false))
                 
                 // Update local farm with offline rideId so completion uses it
                 db.improvedPriorityFarmDao().updateRideId(model.id, FarmListRoute.formatToMMDDYYYY(model.orderDate), offlineRideId)

             }
             sharedprefrenceManager!!.setRideStatus(rideStatus)
             sharedprefrenceManager!!.setRideIdString(rideId)
             
             Toast.makeText(this, "Started in offline mode. RideId: $rideId", Toast.LENGTH_SHORT).show()
             sendLog(logStatus)
             if (!isResumeRide)
                 sendRideStatus(1, rideStatus)
             return
         }

        showLoading?.show()
        val call = apiService2!!.sendData(startDataSend,sharedprefrenceManager!!.getToken())
        call.enqueue(object : Callback<CommonError> {
            override fun onResponse(call: Call<CommonError>, response: Response<CommonError>) {
                showLoading?.dismiss()
                if (response.code() == 200) {
                    rideStart = true
                    rideId = response.body()!!.data
                    actionn = 1
                    if (rideId == "") {
                        rideId = "0"
                    }
                    sharedprefrenceManager!!.setRideIdString(rideId)
                    // RCA Fix: Update memory variable immediately after start
                    rideIdd = if (rideId.startsWith("OFF_")) 0 else rideId.toIntOrNull() ?: 0
                    
                    // RCA Fix: Ensure FID is also set in memory and Prefs on success
                    farmIdd = model.id
                    sharedprefrenceManager!!.setFID(farmIdd.toString())
                    
                    model.rideId = rideId
                    Executors.newSingleThreadExecutor().execute {
                        val db = AppDatabase.getDatabase(this@ImprovedFarmList)
                        db.improvedPriorityFarmDao().updateRideId(model.id, FarmListRoute.formatToMMDDYYYY(model.orderDate), rideId)
                    }
                    sendLog(logStatus)
                    if (!isResumeRide)
                        sendRideStatus(1, rideStatus)
                } else try {
                    val loginError = gson!!.fromJson(
                        response.errorBody()!!.string(),
                        CommonError::class.java
                    )
                    Log.e("Data", loginError.message)
                } catch (e: IOException) {
                    showLoading?.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CommonError>, t: Throwable) {
                Log.e("errorratro", t.message!!)
                showLoading?.dismiss()

            }
        })

    }
    private fun sendRideStatus(statusId: Int, status: String) {
        if (!isInternetAvailable(this)) {
            Log.d("Analysis__", "sendRideStatus offline: $status")
            return
        }
        showLoading!!.show()
        val call = apiService!!.postVehicleStatus(
            sharedprefrenceManager!!.getDriverIdInt(),
            sharedprefrenceManager!!.getVehicleIdInt(),
            statusId,
            status
        )
        call.enqueue(object : Callback<CommonError?> {
            override fun onResponse(call: Call<CommonError?>, response: Response<CommonError?>) {
                if (response.code() == 200) {
                    showLoading!!.dismiss()
                    if (statusId == 1) {
                        sharedprefrenceManager!!.rideStatus="Start"
                        navigateToMap(farmIdd);
                    } else if (statusId == 0) {
                        sharedprefrenceManager!!.clearRideState()
                    }
                } else try {
                    showLoading!!.dismiss()
                    val loginError = gson!!.fromJson(
                        response.errorBody()!!.string(),
                        CommonError::class.java
                    )
                    Log.e("Data", loginError.message)
                } catch (e: java.lang.Exception) {
                    showLoading!!.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CommonError?>, t: Throwable) {
                showLoading!!.dismiss()
                Log.e("errorratro", t.message!!)
            }
        })
    }
    private fun navigateToMap(id: Int) {
        Executors.newSingleThreadExecutor().execute(Runnable {
            val db = getDatabase(this@ImprovedFarmList)
            val date = if (orderDate.isNotEmpty()) orderDate else sharedprefrenceManager!!.getDate()
            val dateToUse = FarmListRoute.formatToMMDDYYYY(date);

            val offlineFarms =
                db.improvedPriorityFarmDao().getFarmsByDate(dateToUse)
                val farm = offlineFarms.find { it.id == id }

                if (farm != null) {
                    latitude = farm.latitude
                    longitude = farm.longitude
                }
            })
       
        val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        startClicked = false
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_LONG).show()
        }
    }
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient!!.getLastLocation()
                .addOnSuccessListener(
                    this,
                    OnSuccessListener<Location?> { locationn ->
                        if (locationn != null) {
                            location = locationn
                            currentlat = locationn.latitude.toString()
                            currentlng = locationn.longitude.toString()
                            Log.d("LOCATION", "Lat: $currentlat, Lng: $currentlng")
                        }
                    })
        }
    }

    private fun showPauseResumeDialog(isPause: Boolean) {
        try {
            val dialog = Dialog(this@ImprovedFarmList, cn.pedant.SweetAlert.R.style.MyDialog)
            dialog.setContentView(R.layout.pauseresumedialog)
            val btnOk = dialog.findViewById<Button>(R.id.btn_ok)
            val btnNo = dialog.findViewById<Button>(R.id.btn_no)
            val txtView = dialog.findViewById<TextView>(R.id.txt_pause_resume)
            if (isPause) {
                txtView.setText(R.string.pause_ride)
            }
            else {
                txtView.setText(R.string.resume_ride)
            }
            btnOk.setOnClickListener {
                if (isPause) {
                    sendLog("Pause")
                    isCounterCanceled = true
                    dialog.dismiss()
                }
                else {
                    sendLog("Start")
                    isPaused = false
                    isCounterCanceled = false
                    dialog.dismiss()
                }
            }
            btnNo.setOnClickListener { dialog.dismiss() }

            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun sendLog(status: String) {
        if (!isFinised) {
            try {
                val routeLog = RouteLog()
                routeLog.lat=currentlat
                routeLog.lng=currentlng
                if (status.equals("Moving", ignoreCase = true)) {
                    routeLog.action = "Moving"
                    sharedprefrenceManager!!.rideStatus="Moving"
                    lastSent = Calendar.getInstance().time
                } else if (status.equals("Start", ignoreCase = true)) {
                    routeLog.action = "Start"
                    sharedprefrenceManager!!.rideStatus="Start"
                    sharedprefrenceManager!!.setIsPause(0)

                    navigateToMap(farmIdd)
                } else if (status.equals("Pause", ignoreCase = true)) {
                    routeLog.action = "Pause"
                    sharedprefrenceManager!!.rideStatus="Pause"
                    sharedprefrenceManager!!.setIsPause(1)
                } else if (status.equals("Arrived", ignoreCase = true)) {
                    routeLog.action = "Arrived"
                    sharedprefrenceManager!!.rideStatus="Arrived"

                } else if (status.equals("Cancel", ignoreCase = true)) {
                    routeLog.action = "Cancel"
                    sharedprefrenceManager!!.rideStatus="Cancel"
                    isFinised = true
                    stopService()

                    routeLog.lat = "" + SkyLabLatitude
                    routeLog.lng = "" + SkyLabLongitude
                } else if (status.equals("End", ignoreCase = true)) {
                    routeLog.action = "End"
                    stopService()
                }
                try {
                    if (location != null) {
                        addresses =
                            geocoder!!.getFromLocation(
                                location?.getLatitude()!!,
                                location?.getLongitude()!!, 1
                            )
                        val address: String = addresses!!.get(0).getAddressLine(0)
                        val city: String = addresses!!.get(0).getLocality()
                        val state: String = addresses!!.get(0).getAdminArea()
                        val country: String = addresses!!.get(0).getCountryName()
                        routeLog.city = city
                        routeLog.address = address
                        routeLog.country = country
                        routeLog.state = state

                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                routeLog.driverId = sharedprefrenceManager!!.userId
                routeLog.rideId = sharedprefrenceManager!!.rideId
                routeLog.routeId = sharedprefrenceManager!!.routeId
                routeLog.uid = sharedprefrenceManager!!.userId
                routeLog.vehicleId = sharedprefrenceManager!!.vehicleId

                if (!isInternetAvailable(this@ImprovedFarmList)) {
                    if (!sharedprefrenceManager!!.isSyncMode()) {
                        Toast.makeText(this@ImprovedFarmList, "Internet is required in Online mode.", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val payload = gson!!.toJson(routeLog)
                    Executors.newSingleThreadExecutor().execute {
                        val db = AppDatabase.getDatabase(this)
                        db.rideSyncDao().insert(RideSyncEntity(token = sharedprefrenceManager!!.token, payload = payload, apiType = "LOG", isSynced = false))
                    }
                    getAllFarms()
                    return
                }

                val call = apiService!!.sendLog(routeLog,sharedprefrenceManager!!.token)
                call.enqueue(object : Callback<CommonError> {
                    override fun onResponse(
                        call: Call<CommonError>,
                        response: Response<CommonError>
                    ) {
                        if (response.code() == 200) {
                            if (status.equals("End", ignoreCase = true)) {
                                sendRideStatus(0, "End")
                            }

                                getAllFarms()

                        } else try {
                            val loginError = gson!!.fromJson(
                                response.errorBody()!!.string(),
                                CommonError::class.java
                            )
                            Log.e("Data", loginError.message)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<CommonError>, t: Throwable) {
                        Log.e("errorratro", t.message!!)
                    }
                })
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopService() {
        val serviceIntent = Intent(
            this,
            ForegroundLocationService::class.java
        )
        stopService(serviceIntent)
    }

    override fun onPause() {
        super.onPause()
        if (!isFinised) {
            timeRemaining = timeRemaining - 1000
            isCounterCanceled = true

        }
    }

    fun startService() {
        val serviceIntent = Intent(
            this,
            ForegroundLocationService::class.java
        )
        serviceIntent.putExtra("remainingTime", timeRemaining.toString() + "")
        serviceIntent.putExtra("rideAction",  "")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION
                )
            } else {
                startForegroundService(serviceIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        
        // Initialize from SharedPrefs to ensure consistency
        rideIdd = sharedprefrenceManager!!.rideIdInt
        farmIdd = sharedprefrenceManager!!.fid.toIntOrNull() ?: 0

        isCounterCanceled = false
        if (!sharedprefrenceManager!!.timeRemaining.equals("", ignoreCase = true)) {
            timeRemaining = sharedprefrenceManager!!.timeRemaining.toLong()
            sharedprefrenceManager!!.timeRemaining = ""
        }

        loadFromDB(false) // Always refresh from local DB on resume

        if (isInternetAvailable(this)) {
            runOnUiThread {
                getFarmsByRoute()
            }
        }
    }
    fun getFarmsByRoute() {
        val routeName = sharedprefrenceManager?.routeName ?: ""
        val orgId = sharedprefrenceManager?.orgID ?: "0"
        val parentId = sharedprefrenceManager?.parentId ?: "0"
        val rawDate = sharedprefrenceManager?.date ?: ""
        val driverId = sharedprefrenceManager?.driverID ?: ""
        val token = sharedprefrenceManager?.token ?: ""

        val formattedDate = FarmListRoute.formatToMMDDYYYY(rawDate)

        apiService?.getFarmsWithPriorityDateWise(routeName, orgId, parentId, formattedDate, driverId, token)
            ?.enqueue(object : Callback<BaseResponse<List<PriorityFarmData>>> {
                override fun onResponse(call: Call<BaseResponse<List<PriorityFarmData>>>, response: Response<BaseResponse<List<PriorityFarmData>>>) {
                    if (response.isSuccessful) {
                        val farms = response.body()?.data
                        if (farms != null) {
                            Executors.newSingleThreadExecutor().execute {
                                val db = AppDatabase.getDatabase(this@ImprovedFarmList)
                                db.priorityFarmDao().clearFarms()
                                db.priorityFarmDao().insertAll(farms)
                            }
                        }
                    }
                    getAllFarms()
                }

                override fun onFailure(call: Call<BaseResponse<List<PriorityFarmData>>>, t: Throwable) {
                    getAllFarms()
                }
            })
    }

    fun getAllFarms() {
        Log.d("ImprovedFarmList", "getAllFarms called. shouldUpdateRouteAfterRefresh=$shouldUpdateRouteAfterRefresh")
        runOnUiThread {
           // Toast.makeText(this, "Refreshing route...", Toast.LENGTH_SHORT).show()
        }
        
        // Refresh route and date from preferences
        routeid = sharedprefrenceManager?.routeId ?: routeid
        orderDate = sharedprefrenceManager?.date ?: orderDate

        if (!sharedprefrenceManager!!.isSyncMode || shouldUpdateRouteAfterRefresh) {
            val call = apiService!!.getAllFarmList(
                routeid,
                sharedprefrenceManager!!.getParentId(),
                orderDate,
                sharedprefrenceManager!!.driverID,
                sharedprefrenceManager!!.token
            )
            call.enqueue(object : Callback<BaseResponse<MutableList<ImprovedPriorityFarmData>>> {
                override fun onResponse(
                    call: Call<BaseResponse<MutableList<ImprovedPriorityFarmData>>>,
                    response: Response<BaseResponse<MutableList<ImprovedPriorityFarmData>>>
                ) {

                    if (response.code() == 200) {
                        val farms = response.body()!!.getData()!!
                            .stream()
                            .filter { farm: ImprovedPriorityFarmData? -> farm!!.getRemove() == 0 }
                            .collect(Collectors.toList())

                        val rideIdStr = sharedprefrenceManager?.getRideId() ?: "0"
                        val isRideActive = (rideIdd != 0) || (rideIdStr != "0" && rideIdStr != "" && rideIdStr != "null")

                        if (isRideActive) {
                            val activeFid = if (farmIdd != 0) farmIdd else sharedprefrenceManager?.getFID()?.toIntOrNull() ?: 0
                            if (activeFid != 0) {
                                farms.forEach { farm ->
                                    if (farm.id == activeFid) {
                                        farm.setIsActiveRide(1)
                                    } else {
                                        farm.setIsActiveRide(0)
                                    }
                                }
                                
                                // Safety: If the active farm is not in the new list, reset it
                                // RCA Fix: Ensure the list is not empty before assuming it's gone
                                if (farms.isNotEmpty() && farms.none { it.id == activeFid }) {
                                    farmIdd = 0
                                    sharedprefrenceManager?.setFID("0")
                                }
                            }
                        } else {
                            farms.forEach { farm ->
                                farm.setIsActiveRide(0)
                            }
                        }

                        farmlist.clear()
                        farmlist.addAll(farms)

                        if (farms.isNotEmpty()) {
                            sharedprefrenceManager!!.setDate(FarmListRoute.formatToMMDDYYYY(farms[0].orderDate))
                        }

                        Executors.newSingleThreadExecutor().execute(Runnable {
                            val db = getDatabase(this@ImprovedFarmList)
                            db.improvedPriorityFarmDao().clearImprovedFarms()
                            db.improvedPriorityFarmDao().insertImprovedFarms(farms)
                        })
                        improvedAdapter!!.notifyDataSetChanged()

                        if (shouldUpdateRouteAfterRefresh) {
                            shouldUpdateRouteAfterRefresh = false
                            callUpdateRouteApi()
                        }

                    } else try {
                        val loginError = Gson().fromJson<CommonError?>(
                            response.errorBody()!!.string(),
                            CommonError::class.java
                        )
                        Log.e("FarmsError", loginError.getMessage())
                    } catch (e: IOException) {
                        showLoading!!.dismiss()
                        e.printStackTrace()
                    }
                    checkRide()
                }

                override fun onFailure(
                    call: Call<BaseResponse<MutableList<ImprovedPriorityFarmData>>>,
                    t: Throwable
                ) {
                    Log.e("FarmsFail", t.message!!)
                    loadFromDB(true)
                }
            })
        }

        else{
            loadFromDB(true)
        }
    }
    private fun loadFromDB(showToast: Boolean) {
        Executors.newSingleThreadExecutor().execute(Runnable {
            val db = getDatabase(this@ImprovedFarmList)
            val dateToUse = if (orderDate.isNotEmpty()) orderDate else sharedprefrenceManager!!.getDate()

            val rideIdStr = sharedprefrenceManager?.getRideId() ?: "0"
            val isRideActive = (rideIdd != 0) || (rideIdStr != "0" && rideIdStr != "" && rideIdStr != "null")

            if (!isRideActive) {
                db.improvedPriorityFarmDao().resetActiveFarms(dateToUse)
            }

            val offlineFarms =
                db.improvedPriorityFarmDao().getFarmsByDate(dateToUse)
                
            // RCA Fix: Ensure mapping is done even when loading from DB
            val currentFid = sharedprefrenceManager?.getFID()?.toIntOrNull() ?: 0
            if (isRideActive && currentFid != 0) {
                offlineFarms.forEach { farm ->
                    if (farm.id == currentFid) {
                        farm.setIsActiveRide(1)
                    } else {
                        farm.setIsActiveRide(0)
                    }
                }
            }
                
            runOnUiThread(Runnable {
                farmlist.clear()
                farmlist.addAll(offlineFarms)

                improvedAdapter?.notifyDataSetChanged()

                if (showToast && farmlist.isEmpty()) {
                    Toast.makeText(
                        this@ImprovedFarmList,
                        "No data found for $dateToUse",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        })
    }

    private fun callUpdateRouteApi() {
        val routeId = sharedprefrenceManager?.routeId
        val rawDate = sharedprefrenceManager?.date
        val driverId = sharedprefrenceManager?.driverID
        val token = sharedprefrenceManager?.token

        if (routeId == null || driverId == null || token == null) return

        val formattedDate = FarmListRoute.formatToMMDDYYYY(rawDate)

        apiService?.updateRoute(routeId, formattedDate, driverId, token)?.enqueue(object : Callback<CommonError> {
            override fun onResponse(call: Call<CommonError>, response: Response<CommonError>) {
                if (response.isSuccessful) {
                    Log.d("UpdateRoute", "Route update API success")
                } else {
                    Log.e("UpdateRoute", "Route update API error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CommonError>, t: Throwable) {
                Log.e("UpdateRoute", "Route update API failure: ${t.message}")
            }
        })
    }
    override fun onFarmClick(
        model: ImprovedPriorityFarmData?,
        name: String?
    ) {
        if (isInternetAvailable(this@ImprovedFarmList)){
            if (model!!.isCompleted == 1) {
                if (model.isPhotoUploaded == 0) {
                    bottomSheetDialogFragment =
                        PhotoDialog().instance(
                            this,
                            "" + model.id,
                            model.rideId,
                            routeid,
                            sharedprefrenceManager!!.parentId,
                            this,
                            model.farmName,
                            model.orderDate
                        )
                    bottomSheetDialogFragment!!.show(
                        getSupportFragmentManager(), bottomSheetDialogFragment!!.getTag()
                    )
                }
            }
            else {
                val intent = Intent(this, ImprovedFarmDetailActivity::class.java).apply {
                    putExtra("farmid", model?.id.toString())
                    putExtra("routeid", routeid)
                    putExtra("farmName", model?.farmName)
                }
            }
        }
        else {
            val intent = Intent(this, ImprovedFarmDetailActivity::class.java).apply {
                putExtra("farmid", model?.id.toString())
                putExtra("routeid", routeid)
                putExtra("farmName", model?.farmName)
            }
        }
    }

    override fun onFarmNameClick(
        model: ImprovedPriorityFarmData?,
        name: String?
    ) {
            val sheet = FarmDetailSheet(model)
            sheet.show(supportFragmentManager, "FarmDetailSheet")
    }

    override fun onStartRide(
        model: ImprovedPriorityFarmData?,
        name: String?
    ) {
        if (!isInternetAvailable(this) && !sharedprefrenceManager!!.isSyncMode) {
            Toast.makeText(this, "Internet is required in Online mode.", Toast.LENGTH_SHORT).show()
            return
        }
        model?.let {
            farmIdd = it.id
            farmnamee = it.farmName
            // RCA Fix: Update persistent FID immediately when ride starts
            sharedprefrenceManager!!.setFID(farmIdd.toString())
            
            for (farm in farmlist) {
                farm.setVisited(farm.id == it.id)
            }

            Executors.newSingleThreadExecutor().execute {
                val db = AppDatabase.getDatabase(this@ImprovedFarmList)
                val date = model.orderDate
                db.improvedPriorityFarmDao().resetActiveFarms(date)
                db.improvedPriorityFarmDao().markFarmAsActive(model.id, date)
                runOnUiThread {
                    improvedAdapter?.notifyDataSetChanged()
                }
            }
        }
        logStatus="Start"
        startRide(model)
        startService()
    }

    override fun onCompleteRide(
        model: ImprovedPriorityFarmData?,
        name: String?
    ) {
        completeRide(model)
    }

    override fun onPauseRide(
        model: ImprovedPriorityFarmData?,
        name: String?
    ) {
        rideId = model!!.rideId
        rideIdd = if (rideId.startsWith("OFF_")) 0 else rideId.toIntOrNull() ?: 0
        sharedprefrenceManager!!.setRideIdString(rideId)
        model?.let {
            val intent = Intent(this, ImprovedFarmDetailActivity::class.java).apply {
                putExtra("FarmId", it.id)
                putExtra("RouteId", it.routeId)
                putExtra("FarmName", it.farmName)
                putExtra("isResumed", true)
                putExtra("actionn", 1)
            }
            showPauseResumeDialog(true)
        }
    }

    override fun onResumeRide(
        model: ImprovedPriorityFarmData?,
        name: String?
    ) {
        showPauseResumeDialog(false)
    }

    override fun onParentClick(
        model: ImprovedPriorityFarmData?,
        name: String?
    ) {
                farmIdd = model!!.id
                rideId = model.rideId
                farmnamee = model.farmName
                
                bottomSheetDialogFragment =
                    PhotoDialog().instance(
                        this,
                        "" + model.getId(),
                        model.getRideId(),
                        routeid,
                        parentIdd.toString() + "",
                        this,
                        model.farmName,
                        model.orderDate
                    )
                bottomSheetDialogFragment!!.show(
                    getSupportFragmentManager(), bottomSheetDialogFragment!!.getTag()
                )
    }

    override fun onMapClick(
        model: ImprovedPriorityFarmData?,
        name: String?
    ) {
        navigateToMap(model!!.id)
    }
}
