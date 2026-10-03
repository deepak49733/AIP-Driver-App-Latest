package com.sc.aipdriver.activities.dialogs

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sc.aipdriver.R
import com.sc.aipdriver.activities.interfaces.ApiClient
import com.sc.aipdriver.activities.interfaces.ApiInterface
import com.sc.aipdriver.activities.models.AddressModel
import com.sc.aipdriver.activities.models.ApiResponse
import com.sc.aipdriver.activities.models.BaseResponse
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager
import com.sc.aipdriver.activities.room.AppDatabase
import com.sc.aipdriver.activities.ui.FarmListRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.concurrent.Executors

class FarmDetailSheet (
        private val farmInfo: ImprovedPriorityFarmData?
    ) : BottomSheetDialogFragment() {
    lateinit var ivLargeView: ImageView
    lateinit var ivClose: ImageView
    lateinit var clLargeView: ConstraintLayout
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return inflater.inflate(R.layout.sheet_farm_detail, container, false)
        }
    private fun showImageLarge(userImage1: String) {
        clLargeView.visibility=VISIBLE
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_launcher_)
        Glide.with(requireContext())
            .load(userImage1)
            .apply(requestOptions)
            .into(ivLargeView)
    }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
            val farmName = view.findViewById<TextView>(R.id.farmName)
            val farmMobile = view.findViewById<TextView>(R.id.farmMobile)
            val emergencyMobile = view.findViewById<TextView>(R.id.emergencyMobile)
            val emergencyAlternateMobile = view.findViewById<TextView>(R.id.emergencyAlternateMobile)
            val farmAddress = view.findViewById<TextView>(R.id.farmAddress)
            val instruction = view.findViewById<TextView>(R.id.instruction)
            val ivFarm = view.findViewById<ImageView>(R.id.iv_farm)
            ivLargeView = view.findViewById<ImageView>(R.id.iv_largeview)
            ivClose = view.findViewById<ImageView>(R.id.ivClose)
            clLargeView = view.findViewById<ConstraintLayout>(R.id.cl_largeImage)
            val ivFarmDrop = view.findViewById<ImageView>(R.id.iv_farmdrop)
            val ivBack = view.findViewById<ImageView>(R.id.iv_back)
        Log.d("Analysis__","Showing Bottom Sheet with farm info: $farmInfo")
        tvTitle.text = "Farm Detail"
        farmInfo?.let {
            farmName.text = it.farmName
            farmMobile.text = it.officePhone
            emergencyMobile.text = it.farmEmergencyNo
            emergencyAlternateMobile.text = it.farmEmergencyNo_2nd
            
            if (!it.delAddress.isNullOrBlank()) {
                farmAddress.text = it.delAddress
            } else {
                val lat = it.latitude.toDoubleOrNull()
                val lng = it.longitude.toDoubleOrNull()

                if (lat != null && lng != null) {
                    getAddressModel(requireContext(), lat, lng) { addressModel ->
                        val formatted =
                            "${addressModel.address}, ${addressModel.city}, ${addressModel.state} ${addressModel.zipCode}"
                        farmAddress.text = formatted
                    }
                } else {
                    farmAddress.text = "Invalid location"
                }
            }
            instruction.text = it.callaheadinstructionstext

            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_launcher_)
            Log.d("Analysis__","Loading farm image with URL: ${ApiClient.BASE_URL+it.userImage1}")
            Glide.with(requireContext())
                .load(ApiClient.BASE_URL+it.userImage1)
                .apply(requestOptions)
                .into(ivFarm)
            Log.d("Analysis__","Loading farm image with URL: ${ApiClient.BASE_URL+it.semenDropLocationImage}")

            Glide.with(requireContext())
                .load(ApiClient.BASE_URL+it.semenDropLocationImage)
                .apply(requestOptions)
                .into(ivFarmDrop)
        }
            farmMobile.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${farmMobile.text}")
                startActivity(intent)
            }
            emergencyAlternateMobile.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${farmMobile.text}")
                startActivity(intent)
            }
            emergencyMobile.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${farmMobile.text}")
                startActivity(intent)
            }
            ivBack.setOnClickListener {
                dismiss()
            }
            ivFarm.setOnClickListener { _ ->
                showImageLarge(ApiClient.BASE_URL+farmInfo?.userImage1) }
            ivFarmDrop.setOnClickListener { _ ->
                showImageLarge(ApiClient.BASE_URL+farmInfo?.semenDropLocationImage) }
            ivClose.setOnClickListener { clLargeView.visibility=GONE }
        }

    fun refreshData() {
        val farmId = farmInfo?.id?.toString() ?: ""
        if (farmId.isEmpty() || !isAdded) return

        val ctx = context ?: return
        val sharedprefrenceManager = SharedprefrenceManager(ctx)
        val apiService = ApiClient.getClient(ctx).create(ApiInterface::class.java)

        // 1. First refresh from updated Room DB (background thread)
        Executors.newSingleThreadExecutor().execute {
            try {
                val db = AppDatabase.getDatabase(ctx)
                val farmIdInt = farmId.toIntOrNull() ?: 0
                val date = sharedprefrenceManager.date ?: ""

                val updatedFarm = db.improvedPriorityFarmDao().getAllImprovedFarms().firstOrNull { it.id == farmIdInt }
                    ?: db.improvedPriorityFarmDao().getFarmByIdAndDate(farmIdInt, date)

                if (updatedFarm != null) {
                    Handler(Looper.getMainLooper()).post {
                        if (!isAdded) return@post
                        val v = view ?: return@post

                        val farmName = v.findViewById<TextView>(R.id.farmName)
                        val farmMobile = v.findViewById<TextView>(R.id.farmMobile)
                        val emergencyMobile = v.findViewById<TextView>(R.id.emergencyMobile)
                        val emergencyAlternateMobile = v.findViewById<TextView>(R.id.emergencyAlternateMobile)
                        val farmAddress = v.findViewById<TextView>(R.id.farmAddress)
                        val instruction = v.findViewById<TextView>(R.id.instruction)

                        if (!updatedFarm.farmName.isNullOrBlank()) farmName?.text = updatedFarm.farmName
                        if (!updatedFarm.officePhone.isNullOrBlank()) farmMobile?.text = updatedFarm.officePhone
                        if (!updatedFarm.farmEmergencyNo.isNullOrBlank()) emergencyMobile?.text = updatedFarm.farmEmergencyNo
                        if (!updatedFarm.farmEmergencyNo_2nd.isNullOrBlank()) emergencyAlternateMobile?.text = updatedFarm.farmEmergencyNo_2nd
                        if (!updatedFarm.callaheadinstructionstext.isNullOrBlank()) instruction?.text = updatedFarm.callaheadinstructionstext

                        if (!updatedFarm.delAddress.isNullOrBlank()) {
                            farmAddress?.text = updatedFarm.delAddress
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("FarmDetailSheet", "Error reading DB for refresh", e)
            }
        }

        // 2. Also call getAllFarmList API directly to get fresh ImprovedPriorityFarmData list
        val route = sharedprefrenceManager.routeId ?: ""
        val parentId = sharedprefrenceManager.parentId ?: "0"
        val rawDate = sharedprefrenceManager.date ?: ""
        val driverId = sharedprefrenceManager.driverID ?: ""
        val token = sharedprefrenceManager.token ?: ""
        val normalizedDate = FarmListRoute.formatToMMDDYYYY(rawDate)

        if (route.isNotEmpty() && driverId.isNotEmpty() && token.isNotEmpty()) {
            apiService.getAllFarmList(route, parentId, normalizedDate, driverId, token)
                .enqueue(object : Callback<BaseResponse<MutableList<ImprovedPriorityFarmData>>> {
                    override fun onResponse(
                        call: Call<BaseResponse<MutableList<ImprovedPriorityFarmData>>>,
                        response: Response<BaseResponse<MutableList<ImprovedPriorityFarmData>>>
                    ) {
                        if (response.isSuccessful && response.body()?.data != null && isAdded) {
                            val farms = response.body()!!.data
                            val updatedFarm = farms.firstOrNull { it.id == farmId.toIntOrNull() }
                            if (updatedFarm != null) {
                                val v = view ?: return
                                val farmName = v.findViewById<TextView>(R.id.farmName)
                                val farmMobile = v.findViewById<TextView>(R.id.farmMobile)
                                val emergencyMobile = v.findViewById<TextView>(R.id.emergencyMobile)
                                val emergencyAlternateMobile = v.findViewById<TextView>(R.id.emergencyAlternateMobile)
                                val farmAddress = v.findViewById<TextView>(R.id.farmAddress)
                                val instruction = v.findViewById<TextView>(R.id.instruction)
                                val ivFarm = v.findViewById<ImageView>(R.id.iv_farm)
                                val ivFarmDrop = v.findViewById<ImageView>(R.id.iv_farmdrop)

                                if (!updatedFarm.farmName.isNullOrBlank()) farmName?.text = updatedFarm.farmName
                                if (!updatedFarm.officePhone.isNullOrBlank()) farmMobile?.text = updatedFarm.officePhone
                                if (!updatedFarm.farmEmergencyNo.isNullOrBlank()) emergencyMobile?.text = updatedFarm.farmEmergencyNo
                                if (!updatedFarm.farmEmergencyNo_2nd.isNullOrBlank()) emergencyAlternateMobile?.text = updatedFarm.farmEmergencyNo_2nd
                                if (!updatedFarm.callaheadinstructionstext.isNullOrBlank()) instruction?.text = updatedFarm.callaheadinstructionstext

                                if (!updatedFarm.delAddress.isNullOrBlank()) {
                                    farmAddress?.text = updatedFarm.delAddress
                                }

                                val requestOptions = RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(R.drawable.ic_launcher_)

                                val userImg = updatedFarm.userImage1 ?: ""
                                val dropImg = updatedFarm.semenDropLocationImage ?: ""

                                val userImgUrl = if (userImg.startsWith("http")) userImg else ApiClient.BASE_URL + userImg
                                val dropImgUrl = if (dropImg.startsWith("http")) dropImg else ApiClient.BASE_URL + dropImg

                                if (ivFarm != null && userImg.isNotEmpty()) {
                                    Glide.with(ctx)
                                        .load(userImgUrl)
                                        .apply(requestOptions)
                                        .into(ivFarm)
                                }

                                if (ivFarmDrop != null && dropImg.isNotEmpty()) {
                                    Glide.with(ctx)
                                        .load(dropImgUrl)
                                        .apply(requestOptions)
                                        .into(ivFarmDrop)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse<MutableList<ImprovedPriorityFarmData>>>, t: Throwable) {
                        Log.e("FarmDetailSheet", "getAllFarmList refresh failed: ${t.message}")
                    }
                })
        }
    }

        override fun onStart() {
            super.onStart()
            dialog?.window?.let { window ->
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true
                }
            }
        }
        fun getAddressModel(
            context: Context,
            lat: Double,
            lng: Double,
            callback: (AddressModel) -> Unit
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val geocoder = Geocoder(context, Locale.getDefault())
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val a = addresses[0]

                        val model = AddressModel(
                            address = a.thoroughfare ?: a.subLocality ?: "",
                            city = a.locality ?: "",
                            state = a.adminArea ?: "",
                            zipCode = a.postalCode ?: ""
                        )
                        callback(model)
                    }
                }
            } else {
                Executors.newSingleThreadExecutor().execute {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(lat, lng, 1)

                        if (!addresses.isNullOrEmpty()) {
                            val a = addresses[0]

                            val model = AddressModel(
                                address = a.thoroughfare ?: a.subLocality ?: "",
                                city = a.locality ?: "",
                                state = a.adminArea ?: "",
                                zipCode = a.postalCode ?: ""
                            )

                            Handler(Looper.getMainLooper()).post {
                                callback(model)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }