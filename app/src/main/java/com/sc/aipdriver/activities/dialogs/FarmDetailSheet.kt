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
import com.sc.aipdriver.activities.models.AddressModel
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData
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