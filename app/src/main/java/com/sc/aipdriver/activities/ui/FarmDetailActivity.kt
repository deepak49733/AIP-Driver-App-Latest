package com.sc.aipdriver.activities.ui

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.sc.aipdriver.R
import com.sc.aipdriver.activities.dialogs.LoadTemprature
import com.sc.aipdriver.activities.dialogs.NextActionBottomSheet
import com.sc.aipdriver.activities.dialogs.ShowLoading
import com.sc.aipdriver.activities.interfaces.ApiClient
import com.sc.aipdriver.activities.interfaces.ApiInterface
import com.sc.aipdriver.activities.interfaces.OnClickSubmit
import com.sc.aipdriver.activities.models.ApiResponse
import com.sc.aipdriver.activities.models.BaseResponse
import com.sc.aipdriver.activities.models.CommonError
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData
import com.sc.aipdriver.activities.models.LiRoutePlannerDetail
import com.sc.aipdriver.activities.models.PriorityFarmData
import com.sc.aipdriver.activities.models.RideFinishRequest
import com.sc.aipdriver.activities.models.RouteLog
import com.sc.aipdriver.activities.models.StartDataSend
import com.sc.aipdriver.activities.models.TempBags
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager
import com.sc.aipdriver.activities.otherclasses.ShouldLogout
import com.sc.aipdriver.activities.room.AppDatabase
import com.sc.aipdriver.activities.room.AppDatabase.Companion.getDatabase
import com.sc.aipdriver.activities.room.RideFinishEntity
import com.sc.aipdriver.activities.services.ForegroundLocationService
import com.sc.aipdriver.databinding.ActivityFarmDetailBinding
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import java.util.stream.Collectors


class FarmDetailActivity : AppCompatActivity(), OnClickSubmit {
    var farmId = ""
    var nextfarmId = ""
    var farmIdd = ""
    var routeName = ""
    var farmName = ""
    var startClicked = false
    private var binding: ActivityFarmDetailBinding? = null

    var apiService2: ApiInterface? = null
    var apiService3: ApiInterface? = null
    var sharedprefrenceManager: SharedprefrenceManager? = null
    var apiService: ApiInterface? = null
    var gson: Gson? = null

    var currentlat = ""
    var shouldStart = 0
    var currentlng = ""
    var geocoder: Geocoder? = null
    var longitude = ""
    var latitude = ""
    var rideStart = false
    var actionn=0;
    var orderDate="";
    var startDataSend: StartDataSend? = null
    var routeid: String = ""
    var parentId: String = ""
    var rideId: String = ""
    var showLoading: ShowLoading? = null
    var logStatus = "Start"
    var rideStatus = "Start"
    var isRideEnd = false
    var img1 = ""
    var img2 = ""
    var rideStat="Start"
    private val LOCATION_PERMISSION = 101
    private val REQUEST_CHECK_SETTINGS = 10
    var isPaused = false
    var shouldLogout: ShouldLogout? = null
    var isFinised = false
    var isCounterCanceled = false
    var lastSent: Date? = null
    var bagsLoaded = 0
    var temp = "0"
    var current: java.util.Date? = null
    var bagsDelivered = "0"
    var refTemp = "0"
    var temprature = "0"
    var liRoutePlannerDetails = java.util.ArrayList<LiRoutePlannerDetail>()
    var addresses: List<Address>? = ArrayList()
    var addresses1: List<Address>? = ArrayList()
    var isResumeRide = false
    var checkExisting = false

    //  Client remove comment on that time to send apk
    //     private final double SkyLabLatitude=45.696146;
    //    private final double SkyLabLongitude=-95.923010;
    // end
    var bottomSheetDialogFragment: BottomSheetDialogFragment? = null

    private val SkyLabLatitude = 28.440766
    private val SkyLabLongitude = 77.070499
    private var timeRemaining: Long = 30000
    var fusedLocationClient: FusedLocationProviderClient? = null
    var location: Location? = null
    var currentLoc: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFarmDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
        getLocation()
        clickListener()
        getFarmInfo(farmId)

    }
    private fun getNextFarm() {
        showLoading!!.show()
        val call = apiService!!.getNextFarm(
            farmId,
            routeid,
            sharedprefrenceManager!!.getDate(),sharedprefrenceManager!!.driverID,sharedprefrenceManager!!.token
        )
        call.enqueue(object : Callback<BaseResponse<PriorityFarmData>> {
            override fun onResponse(
                call: Call<BaseResponse<PriorityFarmData>>,
                response: Response<BaseResponse<PriorityFarmData>>
            ) {

                if (response.code() == 200) {

                    showLoading!!.dismiss()
                    //  start.setVisibility(View.VISIBLE);

                    nextfarmId = response.body()!!.getData()!!.id.toString();




                } else try {
                    showLoading!!.dismiss()

                    //  start.setVisibility(View.GONE);
                    val loginError = gson!!.fromJson<CommonError?>(
                        response.errorBody()!!.string(),
                        CommonError::class.java
                    )
                  //  errorDialog(loginError.getMessage())
                    System.err.println(" Farm List Error Message " + loginError.getMessage() + " ")

                    Log.e("FarmsError", loginError.getMessage())
                } catch (e: IOException) {
                    showLoading!!.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                call: Call<BaseResponse<PriorityFarmData>>,
                t: Throwable
            ) {
                Log.e("FarmsFail", t.message!!)
                showLoading!!.dismiss()
                System.err.println(" Farm List Error Failure ")


            }
        })
    }
    private fun getAllFarms() {
        showLoading!!.show()
        val call = apiService!!.getAllFarmList(
            routeid,
            sharedprefrenceManager!!.getParentId(),
            sharedprefrenceManager!!.improvedDate,
            sharedprefrenceManager!!.driverID,
            sharedprefrenceManager!!.token

        )
        call.enqueue(object : Callback<BaseResponse<MutableList<ImprovedPriorityFarmData>>> {
            override fun onResponse(
                call: Call<BaseResponse<MutableList<ImprovedPriorityFarmData>>>,
                response: Response<BaseResponse<MutableList<ImprovedPriorityFarmData>>>
            ) {
                if (response.code() == 200) {
                    showLoading!!.dismiss()
                    //  start.setVisibility(View.VISIBLE);
                    System.err.println("getFarmsByRoute  " + response.body()!!.getData()!!.size)
                    val farms = response.body()!!.getData()!!
                        .stream()
                        .collect(Collectors.toList())




                } else try {
                    showLoading!!.dismiss()

                    //  start.setVisibility(View.GONE);
                    val loginError = gson!!.fromJson<CommonError?>(
                        response.errorBody()!!.string(),
                        CommonError::class.java
                    )
                  //  errorDialog(loginError.getMessage())
                    System.err.println(" Farm List Error Message " + loginError.getMessage() + " ")

                    Log.e("FarmsError", loginError.getMessage())
                } catch (e: IOException) {
                    showLoading!!.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                call: Call<BaseResponse<MutableList<ImprovedPriorityFarmData>>>,
                t: Throwable
            ) {
                Log.e("FarmsFail", t.message!!)
                showLoading!!.dismiss()
                System.err.println(" Farm List Error Failure ")


            }
        })
    }
    private fun clickListener() {
        binding!!.farmMobile.setOnClickListener {
            binding!!.farmMobile.isEnabled =false

            openDialer(binding!!.farmMobile.text)
            binding!!.farmMobile.isEnabled =true
        }
        binding!!.ivFarmdrop.setOnClickListener {
            Log.d("Analysis__","imge1 "+img1);
            binding!!.ivFarmdrop.isEnabled =false
            if (img1.length > 6) {
                showImageDialog(img1)
            }
            binding!!.ivFarmdrop.isEnabled =true
        }
        binding!!.ivClose.setOnClickListener {
            binding!!.ivClose.isEnabled =false
            binding!!.clLargeImage.visibility = View.GONE
            binding!!.bgTrasnparent.visibility = View.GONE
            binding!!.llRoot.visibility = View.VISIBLE
            binding!!.ivClose.isEnabled =true
        }

        binding!!.ivFarm.setOnClickListener {
            Log.d("Analysis__","imge2 "+img2);
            binding!!.ivFarm.isEnabled =false
            if (img2.length>6) {
                showImageDialog(img2)
            }
            binding!!.ivFarm.isEnabled =true
        }
        binding!!.finish.setOnClickListener {
            binding!!.finish.isEnabled =false
            logStatus = "Finish"
            sendLog("Finish")
            binding!!.finish.isEnabled =true
        }
        binding!!.emergencyMobile.setOnClickListener {
            openDialer(binding!!.emergencyMobile.text)
        }

        binding!!.emergencyAlternateMobile.setOnClickListener {
            openDialer(binding!!.emergencyAlternateMobile.text)
        }

        binding!!.btnStart.setOnClickListener {

            binding!!.btnStart.isEnabled =false
            if (actionn==1){
                Toast.makeText(this,"Please complete started ride first.", Toast.LENGTH_SHORT).show()
            }
           else {
                if (!startClicked) {
                    Log.d("Analysis__", "Intent farmid 173 is $shouldStart")
                    if (shouldStart == 1) {
                        startClicked = true
                        rideStatus = "Start"
                       // getData()
                        val liRoutePlannerDetail = LiRoutePlannerDetail()
                        liRoutePlannerDetail.firmid = farmId
                        liRoutePlannerDetail.numberOfBagsLoaded = bagsLoaded.toString()
                        liRoutePlannerDetail.temperatureOfSemenLoaded = temp.toString()
                        liRoutePlannerDetails.add(liRoutePlannerDetail)
                        sendToServer(liRoutePlannerDetails)
                    } else {
                        startClicked = false
                        Toast.makeText(
                            this,
                            "Please load all the quantity and temprature details",
                            Toast.LENGTH_SHORT
                        ).show();
                    }
//        if (binding!!.btnStart.text.toString().lowercase().contains("start")) {

//        }
//        else if (binding!!.btnStart.text.toString().lowercase().contains(  "end")){
//                sendRideStatus(0,"End")
//        }
                }
            }
            binding!!.btnStart.isEnabled =true
        }
        binding!!.pause.setOnClickListener(View.OnClickListener {
            binding!!.pause.isEnabled =false
            val btntext = binding!!.pause.text.toString()
            var isPause= true
            if (btntext.equals("Pause")){
                    isPause=true
            }else{
                isPause=false
            }
            showPauseResumeDialog(isPause)
            binding!!.pause.isEnabled =true
        })
        binding!!.resume.setOnClickListener {
            binding!!.resume.isEnabled =false
            logStatus = "Start"
            sendLog("Start")
            Log.d("Analysis__","Going form line 227");
            sharedprefrenceManager!!.rideStatus="Start"
            binding!!.resume.isEnabled =true
            navigateToMap();
        }
        binding!!.end.setOnClickListener {
            binding!!.end.isEnabled =false
            logStatus = "End"
            rideStatus = "End"
            checkExisting = true;

            bottomSheetDialogFragment =
                LoadTemprature().instance(
                    this@FarmDetailActivity,
                    "" + farmId,
                    routeName,
                    routeid,
                    true,
                    this@FarmDetailActivity,0
                    ,0.0,
                    sharedprefrenceManager!!.getDate()
                )
            bottomSheetDialogFragment!!.show(
                supportFragmentManager, bottomSheetDialogFragment!!.getTag()
            )
            binding!!.end.postDelayed(object : Runnable {
                override fun run() {
                    binding!!.end.setEnabled(true)
                }
            }, 1000) // 1 second delay
        }
    }

    private fun init() {
        farmName = intent.getStringExtra("FarmName").toString()
        shouldStart = intent.getIntExtra("shouldstart", 0)
        actionn = intent.getIntExtra("actionn", 0)
        Log.d("Analysis__", "Intent farmid 249 is $shouldStart")

        routeName = intent.getStringExtra("RouteName").toString()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        farmId = intent.getStringExtra("FarmID").toString()
        farmIdd = intent.getStringExtra("farmidd").toString()
        Log.d("Analysis__", "Intent farmid 246 is $farmIdd")
        orderDate = intent.getStringExtra("orderDate").toString()
        isResumeRide = intent.getBooleanExtra("isResumeRide", false)
        Log.d("LOGIN__", "Intent farmid 249 is $orderDate")
        Log.d("Analysis__", "isResumeRide  250is $isResumeRide")
        showLoading = ShowLoading(this)
        geocoder = Geocoder(this, Locale.getDefault())
        binding!!.ivBack.setOnClickListener {
                onBackPressed()
        }
        binding!!.tvTitle.setText(farmName)
        apiService = ApiClient.getClient(this).create(ApiInterface::class.java)
        apiService2 = ApiClient.getClient2().create(ApiInterface::class.java)
        apiService3 = ApiClient.getHttp1Client(this@FarmDetailActivity).create(ApiInterface::class.java)
        sharedprefrenceManager = SharedprefrenceManager(this)
        routeid = sharedprefrenceManager!!.routeId
        shouldLogout = ShouldLogout(this, sharedprefrenceManager)
        Log.d("Analysis__", "routeid  is $routeid")
        gson = GsonBuilder().setPrettyPrinting().create()
        startDataSend = StartDataSend()
        if (sharedprefrenceManager!!.rideId.equals(
                "0",
                ignoreCase = true
            ) || sharedprefrenceManager!!.rideId.equals("0", ignoreCase = true)
        ) {
            System.err.println("Set  OnNoteListChanged  RIDE ID 0 " + sharedprefrenceManager!!.rideId)
            startDataSend!!.rideId = 0
        } else {
            System.err.println("Set Else part in OnNoteListChanged   " + sharedprefrenceManager!!.rideId)
            startDataSend!!.rideId = sharedprefrenceManager!!.rideIdInt
        }
        getNextFarm()
        getAllFarms()
    }

    private fun getData() {
        showLoading?.show()
        val call = apiService!!.getTempBags(routeName, farmId.toIntOrNull() ?: 0,sharedprefrenceManager!!.driverID,sharedprefrenceManager!!.getToken())
        call.enqueue(object : Callback<BaseResponse<List<TempBags>>> {
            override fun onResponse(
                call: Call<BaseResponse<List<TempBags>>>,
                response: Response<BaseResponse<List<TempBags>>>
            ) {
                showLoading?.dismiss()

                if (response.code() == 200) {
                    if (response.body()!!.data.size > 0) {
                        bagsLoaded = response.body()!!.data[0].beg
                        temp = response.body()!!.data[0].temp
                        val liRoutePlannerDetail = LiRoutePlannerDetail()
                        liRoutePlannerDetail.firmid = farmId
                        liRoutePlannerDetail.numberOfBagsLoaded = bagsLoaded.toString()
                        liRoutePlannerDetail.temperatureOfSemenLoaded = temp.toString()
                        liRoutePlannerDetails.add(liRoutePlannerDetail)
                        sendToServer(liRoutePlannerDetails)
                    }
                } else try {
                    val loginError = gson!!.fromJson(
                        response.errorBody()!!.string(),
                        CommonError::class.java
                    )
                    Log.e("FarmsError", loginError.message)
                    //                            Toast.makeText(activity, loginError.getMessage(), Toast.LENGTH_LONG).show();
                } catch (e: IOException) {
                    binding!!.btnStart.isEnabled =true
                    showLoading?.dismiss()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<TempBags>>>, t: Throwable) {
                showLoading?.dismiss()
                binding!!.btnStart.isEnabled =true
                Log.e("FarmsFail", t.message!!)
            }
        })
    }


    private fun showPauseResumeDialog(isPause: Boolean) {
        try {
            Log.d("Analysis__", "Showing dialog")
            val dialog = Dialog(this@FarmDetailActivity, cn.pedant.SweetAlert.R.style.MyDialog)
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
                    isPaused = true
                    isCounterCanceled = true
                    //                    StartTime = SystemClock.uptimeMillis();
                    //                    handler.postDelayed(runnable, 0);
                    binding!!.pause.setText(R.string.resume)
                    //        binding!!.pause.setTextColor(resources.getColor(com.sc.aipdriver.R.color.green))
                    dialog.dismiss()
                }
                else {
                    rideStat="Start"
                    sendLog(rideStat)
                    isPaused = false
                    isCounterCanceled = false
                    //   startTimer()
                    //                    TimeBuff += MillisecondTime;
                    binding!!.pause.setText(R.string.pause)
                    Log.d("Analysis__","Going form line 331");

                    //        binding!!.pause.setTextColor(resources.getColor(R.color.colorAccent))
                    dialog.dismiss()
                    navigateToMap()
                }
            }
            btnNo.setOnClickListener { dialog.dismiss() }

            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun sendToServer(liRoutePlannerDetails: List<LiRoutePlannerDetail?>?) {
        showLoading?.show()
        startDataSend!!.driverId = sharedprefrenceManager!!.driverID
        startDataSend!!.routId = sharedprefrenceManager!!.routeId
        startDataSend!!.startOdometer = "0"
        startDataSend!!.uid = sharedprefrenceManager!!.driverID
        startDataSend!!.parentId = sharedprefrenceManager!!.getParentIdInt()
        startDataSend!!.vehicleId = sharedprefrenceManager!!.vehicleId
        startDataSend!!.action = rideStatus
        startDataSend!!.lat=currentlat
        startDataSend!!.lng=currentlng
        if (rideStatus.equals("Start")) {
            startDataSend!!.rideId = 0
            startDataSend!!.orderDate=orderDate;
        }
        startDataSend!!.liRoutePlannerDetail = liRoutePlannerDetails
            val call = apiService2!!.sendData(startDataSend,sharedprefrenceManager!!.getToken())
            call.enqueue(object : Callback<CommonError> {
                override fun onResponse(call: Call<CommonError>, response: Response<CommonError>) {
                    System.err.println("FarmListRoute send to Server RESPONSE SEND TO SERVER:::::---   $response")
                    Log.e("RESPONSE  :::::   ", response.toString() + "")
                    showLoading?.dismiss()
                    if (response.code() == 200) {
                        binding!!.btnStart.visibility = View.GONE
                        Log.e("Data Send to server", "" + response.body()!!.message)
                        System.err.println(
                            "RESPONSE send server route id:::::---   " + response.body()!!
                                .data
                        )
                        getNextFarm()
                        getAllFarms()
                        rideStart = true
//                    binding!!.btnStart.setText("End")
                        routeid = response.body()!!.data
                        rideId = response.body()!!.data
                        sharedprefrenceManager!!.setRideIdString(rideId)
                        //                    sharedprefrenceManager.setRouteId(Integer.parseInt(routeid));
                        System.err.println(" ROUTEID:::   $routeid")
                        System.err.println(" RIDEID:::   " + sharedprefrenceManager!!.rideId + "  ROUTEID:::    " + sharedprefrenceManager!!.routeId)
                        sendLog(logStatus)
                        Log.d(
                            "Analysis__",
                            "value of has image is${sharedprefrenceManager!!.hasImages()}"
                        )
                        if (!isResumeRide)
                            sendRideStatus(1, rideStatus)

                    } else try {
                        val loginError = gson!!.fromJson(
                            response.errorBody()!!.string(),
                            CommonError::class.java
                        )
                        //  errorDialog(loginError.getMessage());
                        Log.e("Data", loginError.message)
                        System.err.println(" sendToServer  Error:::   $loginError")
                    } catch (e: IOException) {
                        showLoading?.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CommonError>, t: Throwable) {
                    Log.e("errorratro", t.message!!)
                    System.err.println(" sendToServer  :::   failure")
                    binding!!.btnStart.isEnabled =true
                    showLoading?.dismiss()

                }
            })

    }

    /*
        private fun sendStartImages() {
            Log.d("Analysis__","Sendinf images")

            val file1 = File(sharedprefrenceManager!!.img1)
            val file2 = File(sharedprefrenceManager!!.img2)
            val file3 = File(sharedprefrenceManager!!.img3)
            val file4 = File(sharedprefrenceManager!!.img4)
            val isStart = sharedprefrenceManager!!.getIsStart()
            val img1 = prepareFilePart("Image1", file1)
            val img2 = prepareFilePart("Image2", file2)
            val img3 = prepareFilePart("image3", file3)
            val img4 = prepareFilePart("image4", file4)
            val isStartPart = isStart!!.toRequestBody("text/plain".toMediaTypeOrNull())
            val call: Call<ResponseBody> = apiService!!.uploadImage(sharedprefrenceManager!!.parentId,img1, img2, img3, img4, isStartPart)

            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FarmDetailActivity, "Upload success", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this@FarmDetailActivity,
                            "Upload failed: " + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Toast.makeText(
                        this@FarmDetailActivity,
                        "Upload error: " + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }*/

    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private fun openDialer(text: CharSequence?) {
        val phoneNumber = text?.toString()?.trim()
        if (!phoneNumber.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            this.startActivity(intent)
        }
    }

    private fun getFarmInfo(farmid:String) {
        System.err.println("Analysis__ Farm Routes  farmId is: line 476" + farmId)
        val call: Call<ApiResponse> = apiService!!.getFarmInfo(
            sharedprefrenceManager!!.getRideId(),
            farmid
        )
        call.enqueue(object : Callback<ApiResponse?> {
            override fun onResponse(call: Call<ApiResponse?>, response: Response<ApiResponse?>) {
                if (response.code() == 200) {
                    val model = response.body()?.data
                    binding!!.farmName.text = model!!.firmName
                    binding!!.tvTitle.text = model.firmName
                    binding!!.farmMobile.text = model.officePhone
                    binding!!.emergencyMobile.text = model.farmEmergencyNo
                    binding!!.emergencyAlternateMobile.text = model.farmEmergencyNo2nd
                    binding!!.farmAddress.text = model.address
                    binding!!.instruction.text = model.semenDropLocation
                    img1 = model.semenDropLocationImg1
                    img2 = model.userImage1
                    Glide.with(this@FarmDetailActivity)
                        .load(model.userImage1) // or new File(currentPhotoPath)
                        .into(binding!!.ivFarm)
                    Glide.with(this@FarmDetailActivity)
                        .load(model.semenDropLocationImg1) // or new File(currentPhotoPath)
                        .into(binding!!.ivFarmdrop)
                    if (model.latitude!=null) {
                        latitude = model.latitude
                    }
                    if (model.longitude!=null) {
                        longitude = model.longitude
                    }
                    Log.d("Analysis__", "farmIdd is $farmIdd")
                    Log.d("Analysis__", "farmId  is $farmId")
                    Log.d("Analysis__", "isResumeRide  is $isResumeRide")
                    if (isResumeRide && farmId.equals(farmIdd)) {
                        binding!!.llButtons.visibility = View.VISIBLE
                        binding!!.btnStart.visibility = View.GONE
                        binding!!.resume.performClick()
                    } else {
                        binding!!.llButtons.visibility = View.GONE
                      //  binding!!.btnStart.visibility = View.VISIBLE
                    }
                } else try {
                    val loginError: CommonError = gson!!.fromJson<CommonError>(
                        response.errorBody()!!.string(),
                        CommonError::class.java
                    )
                    // errorDialog(loginError.getMessage());
                    System.err.println("")
                    Log.e("Data Get Farmslist", loginError.message)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ApiResponse?>, t: Throwable) {
                Log.e("errorratro", t.message!!)
                // If fetching farm info failed (likely no internet), redirect user to ImprovedFarmList
                try {
                    val intent = Intent(this@FarmDetailActivity, ImprovedFarmList::class.java)
                    intent.putExtra("orderdate", sharedprefrenceManager!!.getDate())
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onClickSubmit(
        clicked: Boolean,
        liRoutePlannerDetails: List<LiRoutePlannerDetail?>?,
        comments: String,sendmail: Boolean
    ) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (clicked) {



                    if (isResumeRide || checkExisting) {
                        isRideEnd = true
//                          onBackPressed()
                       setUpdatedFarm()

                        val sheet =
                            NextActionBottomSheet(object : NextActionBottomSheet.ActionListener {

                                override fun onStartNext() {
                                    startFarmRide()
                                }

                                override fun onGoBack() {
                                    finish() // or onBackPressedDispatcher.onBackPressed()
                                }

                                override fun onViewAllFarms() {
                                    showAllFarms()
                                }


                            })

                        sheet.show(supportFragmentManager, "NextActionSheet")
                    }
                    if (rideStatus.equals("End")) {

                        temprature = liRoutePlannerDetails!!.get(0)!!.temperatureOfSemenLoaded
                        bagsDelivered = liRoutePlannerDetails.get(0)!!.numberOfBagsLoaded
                        refTemp = liRoutePlannerDetails.get(0)!!.customerSeemanCoolarTemp

                        FinishData("", "")
                             sendRideStatus(0, "End")
                    }


                }
            }, 10
//            }, 4000
        )
    }

    private fun setUpdatedFarm() {
        getFarmInfo(nextfarmId)
    }

    private fun showAllFarms() {

    }
    private fun startFarmRide() {

    }



    override fun onPhotoUpload(boolean: Boolean,  snedMail: Boolean) {
        Log.d("LOGIN__","Finish data mail ")
        if (boolean)
        FinishDataMail()
    }

    private fun showImageDialog(imageUrl: String) {
        if (imageUrl.length > 6) {
            binding!!.llRoot.visibility = View.GONE
            binding!!.clLargeImage.visibility = View.VISIBLE
            binding!!.bgTrasnparent.visibility = View.VISIBLE
            Picasso.get()
                .load(imageUrl)
                .into(binding!!.ivLargeview, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        Log.d("ImageDialog", "Image loaded successfully")
                    }
                    override fun onError(e: Exception?) {
                        Log.e("ImageDialog", "Failed to load image", e)
                    }
                })
        }
    }

    private fun sendRideStatus(statusId: Int, status: String) {
        showLoading!!.show()
        System.err.println("Send Ride Status    " + sharedprefrenceManager!!.driverID + "   " + sharedprefrenceManager!!.vehicleId)
        val call = apiService!!.postVehicleStatus(
            sharedprefrenceManager!!.getDriverIdInt(),
            sharedprefrenceManager!!.getVehicleIdInt(),
            statusId,
            status
        )
        call.enqueue(object : Callback<CommonError?> {
            override fun onResponse(call: Call<CommonError?>, response: Response<CommonError?>) {
                System.err.println("RESPONSE SEND RIDE STATUS:::::---   $response")
                Log.e("RESPONSE  :::::   ", response.toString() + "")
                if (response.code() == 200) {
                    showLoading!!.dismiss()
                    if (statusId == 1) {
                        binding!!.llButtons.visibility = View.VISIBLE
                        sharedprefrenceManager!!.rideStatus="Start"
                        Log.d("Analysis__","Going form line 884");
                        navigateToMap();
                    } else if (statusId == 0) {

                        sharedprefrenceManager!!.rideStatus="End"
                        /* checkExisting = true;
                         LoadTemprature(
                             this@FarmDetailActivity,
                             "" + farmId,
                             routeName,
                             routeid,
                             true,
                             this@FarmDetailActivity
                         ).createDialog()*/
                    }
                    Log.d("Analysis__", "Maps location is $location")
                    if (location != null) {
                        //    sendLog(logStatus)
                        //sendLog("Moving");
                    }

                    System.err.println("ROUTEID IN INTENT ONM FARM ROUTE LIST   $routeid")
                } else try {
                    showLoading!!.dismiss()
                    val loginError = gson!!.fromJson(
                        response.errorBody()!!.string(),
                        CommonError::class.java
                    )
                    //  errorDialog(loginError.getMessage());
                    System.err.println("Send Ride Status  Login Error   $loginError")
                    Log.e("Data", loginError.message)
                } catch (e: java.lang.Exception) {
                    showLoading!!.dismiss()
                    e.printStackTrace()
                    Log.e("Data", e.toString())
                }
            }

            override fun onFailure(call: Call<CommonError?>, t: Throwable) {
                showLoading!!.dismiss()
                Log.e("errorratro", t.message!!)
                System.err.println("Send Ride Status  Failure  ")
            }
        })
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

    private fun navigateToMap() {
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
        //Second Way If You Want to Let User Choose the Map App
//          val geoUri = Uri.parse("geo:0,0?q=$latitude,$longitude")
//          val intent = Intent(Intent.ACTION_VIEW, geoUri)
//          startActivity(intent)
    }

/*    private fun finishCompleteRide() {
        //sendLog("End");
        showLoading!!.show()
        val call = apiService!!.postVehicleStatus(
            sharedprefrenceManager!!.getUserIdInt(),
            sharedprefrenceManager!!.getVehicleIdInt(),
            0,
            "Finish"
        )
        call.enqueue(object : Callback<CommonError?> {
            override fun onResponse(call: Call<CommonError?>, response: Response<CommonError?>) {
                if (response.code() == 200) {
                    showLoading!!.dismiss()
                    sharedprefrenceManager!!.setRideId(0)
                    //success("Alert!", "Ride finished")
                } else try {
                    showLoading!!.dismiss()
                    val loginError = gson!!.fromJson(
                        response.errorBody()!!.string(),
                        CommonError::class.java
                    )
                    //  errorDialog(loginError.getMessage());
                    Log.e("Data", loginError.message)
                } catch (e: java.lang.Exception) {
                    showLoading!!.dismiss()
                    e.printStackTrace()
                    Log.e("Data", e.toString())
                }
            }

            override fun onFailure(call: Call<CommonError?>, t: Throwable) {
                showLoading!!.dismiss()
                Log.e("errorratro", t.message!!)
            }
        })
    }*/

    private fun sendLog(status: String) {
        System.err.println("In Send Log " + "  812 LN")
        System.err.println("Is Finished $isFinised  813 LN")
        if (!isFinised) {
            try {
                val routeLog = RouteLog()
                routeLog.lat=currentlat
                routeLog.lng=currentlng
                //                routeLog.setLat("" + location.getLatitude());
//                routeLog.setLng("" + location.getLongitude());
                if (status.equals("Moving", ignoreCase = true)) {
                    //        System.err.println("INside sendLog Moving" + " 821 LN")
                    routeLog.action = "Moving"
                    sharedprefrenceManager!!.rideStatus="Moving"
                    //                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
                    lastSent = Calendar.getInstance().time
                    //    System.err.println("lastSent =   $lastSent  826LN")
                } else if (status.equals("Start", ignoreCase = true)) {
                    //  System.err.println("In SendLog  Start  " + "  839 LN")
                    routeLog.action = "Start"
                    sharedprefrenceManager!!.rideStatus="Start"
                    //                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
                } else if (status.equals("Pause", ignoreCase = true)) {
                    //   System.err.println("In SendLog  Pause  " + "  839 LN")
                    routeLog.action = "Pause"
                    sharedprefrenceManager!!.rideStatus="Pause"
                } else if (status.equals("Arrived", ignoreCase = true)) {
                    //     System.err.println("In SendLog  Arrived  " + "  835 LN")
                    routeLog.action = "Arrived"
                    sharedprefrenceManager!!.rideStatus="Arrived"
                    //                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
                } else if (status.equals("Cancel", ignoreCase = true)) {
                    //     System.err.println("In SendLog  Cancel  " + "  839 LN")
                    routeLog.action = "Cancel"
                    sharedprefrenceManager!!.rideStatus="Cancel"
                    isFinised = true
                    stopService()

//                    routeLog.setLat("" + demoLat);
//                    routeLog.setLng("" + demoLong);
                    routeLog.lat = "" + SkyLabLatitude
                    routeLog.lng = "" + SkyLabLongitude
                } else if (status.equals("End", ignoreCase = true)) {
                    System.err.println("In SendLog  End  " + "  839 LN")
                    routeLog.action = "End"
                    //                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
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
                    //sendrequest(markersList);
                } catch (e: IOException) {

                    Log.d("Analysis__", " exception ${e.printStackTrace()}")
                    e.printStackTrace()
                }
                routeLog.driverId = sharedprefrenceManager!!.userId
                routeLog.rideId = sharedprefrenceManager!!.rideId
                routeLog.routeId = sharedprefrenceManager!!.routeId
                routeLog.uid = sharedprefrenceManager!!.userId
                routeLog.vehicleId = sharedprefrenceManager!!.vehicleId

                val call = apiService!!.sendLog(routeLog,sharedprefrenceManager!!.token)
                call.enqueue(object : Callback<CommonError> {
                    override fun onResponse(
                        call: Call<CommonError>,
                        response: Response<CommonError>
                    ) {
                        if (response.code() == 200) {
                            System.err.println("Send Log Successfully  " + "  886 LN")
                            Log.e("Datatrue", "" + response.body()!!.message)
                        } else try {
                            val loginError = gson!!.fromJson(
                                response.errorBody()!!.string(),
                                CommonError::class.java
                            )
                            //  errorDialog(loginError.getMessage());
                            Log.e("Data", loginError.message)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            Log.e("Data", e.toString())
                        }
                    }

                    override fun onFailure(call: Call<CommonError>, t: Throwable) {
                        Log.e("errorratro", t.message!!)
                    }
                })
            } catch (e: java.lang.Exception) {
                Log.d("Analysis__", "in exception while sending logs ")
                Log.d("Analysis__", " exception ${e.printStackTrace()}")
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
            startService()
        }
    }

    fun startService() {
        val serviceIntent = Intent(
            this,
            ForegroundLocationService::class.java
        )
        serviceIntent.putExtra("remainingTime", timeRemaining.toString() + "")
        serviceIntent.putExtra("rideAction", binding!!.pause.getText().toString() + "")
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
                // Start your foreground service here
                startForegroundService(serviceIntent)
            }
        }
        Log.d("Analysis__", "2317")
    }

    override fun onResume() {
        super.onResume()
        stopService()
        isCounterCanceled = false
       // shouldLogout?.shouldLogout()
        println("yueyuwyuewyuewyuew_Moving_Resume " + sharedprefrenceManager!!.timeRemaining)
        Log.d("Analysis__", "line 2092")
        if (!sharedprefrenceManager!!.timeRemaining.equals("", ignoreCase = true)) {
            timeRemaining = sharedprefrenceManager!!.timeRemaining.toLong()
            sharedprefrenceManager!!.timeRemaining = ""
            Log.d("Analysis__", "line 2097")
        }
        println("ewuieiueuewue " + binding!!.pause.getText().toString())
        if (!binding!!.pause.getText().toString().equals("Resume Ride", ignoreCase = true)) {

            // startTimer()
        }


    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (isRideEnd || binding!!.btnStart.isVisible) {
            isResumeRide = true
        }
            if (isResumeRide){
                val intent = Intent(this, FarmListRoute::class.java)
                intent.putExtra("ROUTE", routeName)
                intent.putExtra("ROUTEID", routeid)
                intent.putExtra("SEQUENCE", "Start")
                intent.putExtra("isResumed", true)
                startActivity(intent)
            }
    }

    fun FinishData(endodometer: String?, totalmiles: String?) {
        try {
            var address: String? = ""
            var city: String? = ""
            var state: String? = ""
            var country: String? = ""
            if (location != null) {
                addresses1 =
                    geocoder!!.getFromLocation(location!!.latitude, location!!.longitude, 1)
                address = addresses1!![0].getAddressLine(0)
                city = addresses1!![0].locality
                state = addresses1!![0].adminArea
                country = addresses1!![0].countryName
            }
            val rideFinishRequest = RideFinishRequest()
            rideFinishRequest.action = "End"
            rideFinishRequest.rideId = sharedprefrenceManager!!.rideId + ""
            rideFinishRequest.uid = sharedprefrenceManager!!.driverID
            rideFinishRequest.lat = latitude + ""
            rideFinishRequest.lng = longitude + ""
            rideFinishRequest.endOdometer = endodometer
            rideFinishRequest.totalMiles = totalmiles
            rideFinishRequest.address = address
            rideFinishRequest.state = state
            rideFinishRequest.city = city
            rideFinishRequest.firmId = farmId
            rideFinishRequest.bagsDelivered = bagsDelivered
            rideFinishRequest.temperature = temprature
            rideFinishRequest.routeId = routeid
            rideFinishRequest.country = country
            rideFinishRequest.customerSeemanCoolarTemp = refTemp


            val call = apiService!!.rideFinish(rideFinishRequest,sharedprefrenceManager!!.driverID,sharedprefrenceManager!!.getToken())
            call.enqueue(object : Callback<CommonError?> {
                override fun onResponse(
                    call: Call<CommonError?>,
                    response: Response<CommonError?>
                ) {
                    if (response.code() == 200) {
                        //  Log.e("ta", response.body());

                        // finishCompleteRide();
                        sendLog("End")
                    } else try {
                        val loginError = gson!!.fromJson(
                            response.errorBody()!!.string(),
                            CommonError::class.java
                        )
                        //  errorDialog(loginError.getMessage());
                        Log.e("Data", loginError.message)
                    } catch (e: java.lang.Exception) {

                        e.printStackTrace()
                        Log.e("Data", e.toString())
                    }

                }

                override fun onFailure(call: Call<CommonError?>, t: Throwable) {

                    Log.e("errorratro", t.message!!)

                    if (sharedprefrenceManager?.isSyncMode() != true) {
                        Toast.makeText(
                            this@FarmDetailActivity,
                            "Unable to finish ride without internet in Online mode",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    Thread {

                        val db = AppDatabase.getDatabase(this@FarmDetailActivity)

                        val entity = RideFinishEntity(
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
                            driverId = rideFinishRequest.driverId,
                            Token = rideFinishRequest.token
                        )

                        db.rideFinishDao().insert(entity)

                    }.start()

                    Toast.makeText(this@FarmDetailActivity,
                        "Ride finish saved offline",
                        Toast.LENGTH_SHORT).show()


                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun FinishDataMail() {
        try {
            val request =  JsonObject();
            request.addProperty("ParentId",sharedprefrenceManager!!.parentId)
            request.addProperty("FIRMID",farmId)
            request.addProperty("rideId",sharedprefrenceManager!!.rideId + "")
            request.addProperty("RouteId",routeid)
            val call = apiService3!!.rideFinishEmail(request,sharedprefrenceManager!!.driverID,sharedprefrenceManager!!.token)
            call.enqueue(object : Callback<CommonError?> {
                override fun onResponse(
                    call: Call<CommonError?>,
                    response: Response<CommonError?>
                ) {
                    if (response.code() == 200) {
                        //  Log.e("ta", response.body());
                        Log.d("LOGIN__","Finish data mail success")
                        // finishCompleteRide();
                      //  sendLog("End")
                    } else try {
                        val loginError = gson!!.fromJson(
                            response.errorBody()!!.string(),
                            CommonError::class.java
                        )
                        //  errorDialog(loginError.getMessage());
                        Log.e("Data", loginError.message)
                    } catch (e: java.lang.Exception) {

                        e.printStackTrace()
                        Log.e("Data", e.toString())
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

}