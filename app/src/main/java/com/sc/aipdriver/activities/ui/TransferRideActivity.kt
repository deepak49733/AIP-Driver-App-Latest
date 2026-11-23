package com.sc.aipdriver.activities.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sc.aipdriver.R
import com.sc.aipdriver.activities.adapters.UndeliveredFarmAdapter
import com.sc.aipdriver.activities.interfaces.ApiClient
import com.sc.aipdriver.activities.interfaces.ApiInterface
import com.sc.aipdriver.activities.models.BaseResponse
import com.sc.aipdriver.activities.models.DriverTransferData
import com.sc.aipdriver.activities.models.InitiateOrderTransferData
import com.sc.aipdriver.activities.models.InitiateOrderTransferRequest
import com.sc.aipdriver.activities.models.UndeliveredFarmData
import com.sc.aipdriver.activities.models.UndeliveredFarmsWrapper
import com.sc.aipdriver.activities.models.UpdateTransferLocationData
import com.sc.aipdriver.activities.models.UpdateTransferLocationRequest
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class TransferRideActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var spinnerDrivers: Spinner
    private lateinit var tvContactNumber: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSubmitTransfer: AppCompatButton
    private lateinit var rvUndeliveredFarms: RecyclerView
    private lateinit var cbSelectAllFarms: CheckBox
    private lateinit var tvNoFarms: TextView

    private lateinit var sharedprefrenceManager: SharedprefrenceManager
    private var driverList: List<DriverTransferData> = ArrayList()
    private var selectedDriver: DriverTransferData? = null

    private var undeliveredFarmList: List<UndeliveredFarmData> = ArrayList()
    private var farmAdapter: UndeliveredFarmAdapter? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null

    private var intentRouteId: String = ""
    private var intentOrderDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_ride)

        sharedprefrenceManager = SharedprefrenceManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        intentRouteId = intent.getStringExtra("routeid") ?: ""
        intentOrderDate = intent.getStringExtra("orderdate") ?: ""

        initViews()
        setupBackNavigation()
        fetchDriversForTransfer()
        fetchUndeliveredFarms()
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        })
    }

    private fun handleBackNavigation() {
        if (isTaskRoot) {
            val intent = Intent(this, ImprovedFarmList::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        finish()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.iv_back)
        spinnerDrivers = findViewById(R.id.spinner_drivers)
        tvContactNumber = findViewById(R.id.tv_contact_number)
        progressBar = findViewById(R.id.progress_bar)
        btnSubmitTransfer = findViewById(R.id.btn_submit_transfer)
        rvUndeliveredFarms = findViewById(R.id.rv_undelivered_farms)
        cbSelectAllFarms = findViewById(R.id.cb_select_all_farms)
        tvNoFarms = findViewById(R.id.tv_no_farms)

        rvUndeliveredFarms.layoutManager = LinearLayoutManager(this)

        ivBack.setOnClickListener {
            handleBackNavigation()
        }

        spinnerDrivers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in driverList.indices) {
                    selectedDriver = driverList[position]
                    val contact = selectedDriver?.contactNumber
                    if (!contact.isNullOrEmpty()) {
                        tvContactNumber.text = "Contact: $contact"
                        tvContactNumber.visibility = View.VISIBLE
                    } else {
                        tvContactNumber.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedDriver = null
                tvContactNumber.visibility = View.GONE
            }
        }

        cbSelectAllFarms.setOnCheckedChangeListener { _, isChecked ->
            farmAdapter?.selectAll(isChecked)
        }

        btnSubmitTransfer.setOnClickListener {
            val toDriver = selectedDriver
            val selectedFarms = farmAdapter?.getSelectedFarms() ?: emptyList()

            if (toDriver == null) {
                Toast.makeText(this, "Please select a driver", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedFarms.isEmpty()) {
                Toast.makeText(this, "Please select at least one farm to transfer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            initiateTransfer(toDriver, selectedFarms)
        }
    }

    private fun initiateTransfer(toDriver: DriverTransferData, selectedFarms: List<UndeliveredFarmData>) {
        val rideIds = selectedFarms.mapNotNull { it.rideId }
        val fromDriverId = sharedprefrenceManager.driverID.toIntOrNull()
            ?: sharedprefrenceManager.userId?.toIntOrNull()
            ?: 0
        val toDriverId = toDriver.driverId ?: 0
        val orgId = sharedprefrenceManager.orgID.toIntOrNull() ?: 0
        val studId = sharedprefrenceManager.getParentIdInt()
        val token = sharedprefrenceManager.token ?: ""

        val request = InitiateOrderTransferRequest().apply {
            this.rideIds = rideIds
            this.fromDriverId = fromDriverId
            this.toDriverId = toDriverId
            this.orgId = orgId
            this.studId = studId
        }

        btnSubmitTransfer.isEnabled = false
        progressBar.visibility = View.VISIBLE

        val apiService = ApiClient.getHttp1Client(this).create(ApiInterface::class.java)
        apiService.initiateOrderTransfer(request, token).enqueue(object : Callback<BaseResponse<InitiateOrderTransferData>> {
            override fun onResponse(
                call: Call<BaseResponse<InitiateOrderTransferData>>,
                response: Response<BaseResponse<InitiateOrderTransferData>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val transferData = response.body()?.data
                    val transferId = transferData?.transferId
                    if (transferId != null && transferId > 0) {
                        fetchLocationAndUpdateTransfer(transferId, fromDriverId, token)
                    } else {
                        progressBar.visibility = View.GONE
                        btnSubmitTransfer.isEnabled = true
                        Toast.makeText(this@TransferRideActivity, transferData?.message ?: "Transfer initiation failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    progressBar.visibility = View.GONE
                    btnSubmitTransfer.isEnabled = true
                    Toast.makeText(this@TransferRideActivity, "Failed to initiate transfer (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse<InitiateOrderTransferData>>, t: Throwable) {
                progressBar.visibility = View.GONE
                btnSubmitTransfer.isEnabled = true
                Toast.makeText(this@TransferRideActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchLocationAndUpdateTransfer(transferId: Int, driverId: Int, token: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                val lat = location?.latitude ?: 0.0
                val lng = location?.longitude ?: 0.0
                var address = if (lat != 0.0 && lng != 0.0) "$lat, $lng" else ""
                try {
                    if (lat != 0.0 && lng != 0.0) {
                        val addresses = geocoder?.getFromLocation(lat, lng, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val fullAddr = addresses[0].getAddressLine(0)
                            if (!fullAddr.isNullOrEmpty()) {
                                address = fullAddr
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TransferRideActivity", "Geocoder error: ${e.message}")
                }
                updateTransferLocation(transferId, driverId, address, lat, lng, token)
            }?.addOnFailureListener {
                updateTransferLocation(transferId, driverId, "", 0.0, 0.0, token)
            } ?: updateTransferLocation(transferId, driverId, "", 0.0, 0.0, token)
        } else {
            updateTransferLocation(transferId, driverId, "", 0.0, 0.0, token)
        }
    }

    private fun updateTransferLocation(
        transferId: Int,
        driverId: Int,
        address: String,
        latitude: Double,
        longitude: Double,
        token: String
    ) {
        val request = UpdateTransferLocationRequest().apply {
            this.transferId = transferId
            this.driverId = driverId
            this.finishingLocation = address
            this.latitude = latitude
            this.longitude = longitude
        }

        val apiService = ApiClient.getHttp1Client(this).create(ApiInterface::class.java)
        apiService.updateTransferLocation(request, token).enqueue(object : Callback<BaseResponse<UpdateTransferLocationData>> {
            override fun onResponse(
                call: Call<BaseResponse<UpdateTransferLocationData>>,
                response: Response<BaseResponse<UpdateTransferLocationData>>
            ) {
                progressBar.visibility = View.GONE
                btnSubmitTransfer.isEnabled = true
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@TransferRideActivity, "Transfer ride successfully", Toast.LENGTH_LONG).show()
                    cbSelectAllFarms.setOnCheckedChangeListener(null)
                    cbSelectAllFarms.isChecked = false
                    fetchUndeliveredFarms()
                } else {
                    Toast.makeText(this@TransferRideActivity, "Transfer failed (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse<UpdateTransferLocationData>>, t: Throwable) {
                progressBar.visibility = View.GONE
                btnSubmitTransfer.isEnabled = true
                Toast.makeText(this@TransferRideActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDriversForTransfer() {
        val driverId = sharedprefrenceManager.driverID ?: ""
        val orgId = sharedprefrenceManager.orgID ?: ""
        val token = sharedprefrenceManager.token ?: ""

        progressBar.visibility = View.VISIBLE

        val apiService = ApiClient.getHttp1Client(this).create(ApiInterface::class.java)
        apiService.getDriversForTransfer(driverId, orgId, token).enqueue(object : Callback<BaseResponse<List<DriverTransferData>>> {
            override fun onResponse(
                call: Call<BaseResponse<List<DriverTransferData>>>,
                response: Response<BaseResponse<List<DriverTransferData>>>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val baseResponse = response.body()
                    val data = baseResponse?.data
                    if (!data.isNullOrEmpty()) {
                        driverList = data
                        val adapter = ArrayAdapter(
                            this@TransferRideActivity,
                            android.R.layout.simple_spinner_item,
                            driverList
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerDrivers.adapter = adapter
                    } else {
                        Toast.makeText(this@TransferRideActivity, "No drivers found for transfer", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("TransferRideActivity", "API Error: ${response.code()}")
                    Toast.makeText(this@TransferRideActivity, "Failed to fetch drivers", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<DriverTransferData>>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("TransferRideActivity", "Network Error: ${t.message}", t)
                Toast.makeText(this@TransferRideActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUndeliveredFarms() {
        val driverId = sharedprefrenceManager.driverID ?: ""
        val routeId = intentRouteId.ifEmpty { sharedprefrenceManager.routeId ?: "" }
        val orderDate = intentOrderDate.ifEmpty { sharedprefrenceManager.date ?: sharedprefrenceManager.improvedDate ?: "" }
        val token = sharedprefrenceManager.token ?: ""

        progressBar.visibility = View.VISIBLE

        val apiService = ApiClient.getHttp1Client(this).create(ApiInterface::class.java)
        apiService.getUndeliveredFarms(driverId, routeId, orderDate, token).enqueue(object : Callback<BaseResponse<UndeliveredFarmsWrapper>> {
            override fun onResponse(
                call: Call<BaseResponse<UndeliveredFarmsWrapper>>,
                response: Response<BaseResponse<UndeliveredFarmsWrapper>>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val baseResponse = response.body()
                    val wrapper = baseResponse?.data
                    val farms = wrapper?.data
                    if (!farms.isNullOrEmpty()) {
                        undeliveredFarmList = farms
                        tvNoFarms.visibility = View.GONE
                        rvUndeliveredFarms.visibility = View.VISIBLE

                        farmAdapter = UndeliveredFarmAdapter(this@TransferRideActivity, undeliveredFarmList) {
                            val selectedCount = farmAdapter?.getSelectedFarms()?.size ?: 0
                            cbSelectAllFarms.setOnCheckedChangeListener(null)
                            cbSelectAllFarms.isChecked = (selectedCount == undeliveredFarmList.size && undeliveredFarmList.isNotEmpty())
                            cbSelectAllFarms.setOnCheckedChangeListener { _, isChecked ->
                                farmAdapter?.selectAll(isChecked)
                            }
                        }
                        rvUndeliveredFarms.adapter = farmAdapter
                    } else {
                        tvNoFarms.visibility = View.VISIBLE
                        rvUndeliveredFarms.visibility = View.GONE
                    }
                } else {
                    Log.e("TransferRideActivity", "UndeliveredFarms API Error: ${response.code()}")
                    tvNoFarms.visibility = View.VISIBLE
                    rvUndeliveredFarms.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<BaseResponse<UndeliveredFarmsWrapper>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("TransferRideActivity", "UndeliveredFarms Network Error: ${t.message}", t)
                tvNoFarms.visibility = View.VISIBLE
                rvUndeliveredFarms.visibility = View.GONE
            }
        })
    }
}
