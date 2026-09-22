package com.sc.aipdriver.activities.fragments


import android.Manifest
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sc.aipdriver.R
import com.sc.aipdriver.databinding.FragmentPerrmissionBinding
import java.util.*


class PermissionFragment : DialogFragment() {
    private lateinit var binding: FragmentPerrmissionBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    companion object {
        val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        private const val LOCATION_PERMISSION = "android.permission.ACCESS_FINE_LOCATION"
        var PERMISSIONS = arrayOf(CAMERA)
        lateinit var fragment: PermissionFragment
        private const val KEY_TITLE = "PERMISSION_TYPE"
        private const val KEY_DESC = "desc"
        lateinit var grantPermission: GrantPermission
        
        fun newInstance(type: Array<String>, grantPermission: GrantPermission): PermissionFragment {
            this.grantPermission = grantPermission
            val args = Bundle()
            args.putStringArray(KEY_TITLE, type)
            val fragment = PermissionFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(type: Array<String>): PermissionFragment {
           // this.grantPermission = grantPermission
            val args = Bundle()
            args.putStringArray(KEY_TITLE, type)
            val fragment = PermissionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerrmissionBinding.inflate(inflater)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppDialogTheme)
        PERMISSIONS = arguments?.getStringArray(KEY_TITLE) as Array<String>

        //  //Log.d("Analysis__", "onCreatedwdqcw: "+Gson().toJson(PERMISSIONS))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val per = TextUtils.join(",", PERMISSIONS)
        val desc =
            "You don't have permission to access $per,\nWithout allowing above permission you can't use the app features.\nClick on the proceed button to allow the permission."
        binding.tvDes.text = desc
        binding.proceed.setOnClickListener {
            checkPermissionRequired(PERMISSIONS)
        }
        binding.cancle.setOnClickListener {
            grantPermission.isGrant(false, true)
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        //var find:Boolean=PERMISSIONS.any{ it == "android.permission.ACCESS_FINE_LOCATION"}

        if (LOCATION_PERMISSION in PERMISSIONS == false) {
            if (hasPermissions(requireContext(), PERMISSIONS)) {
                grantPermission.isGrant(true)
                dismiss()
            }
        } else {
            if (hasPermissions(requireContext(), PERMISSIONS)) {
                if (isLocationEnabled(requireActivity())) {
                    grantPermission.isGrant(true)
                    dismiss()
                }

            }


        }

    }

    fun hasPermissions(context: Context, list: Array<String>): Boolean = list.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermissions(context: Context, list: Array<String>): Boolean = list.all {
        return ActivityCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED && isLocationEnabled(context)

    }

    fun permissionLocation(): Array<String> {
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun hasPermissions(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */

    fun showAlert(context: Activity) {
        val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle(getString(R.string.location_pop_up_title))
        alertBuilder.setMessage(getString(R.string.location_pop_up_description))
        alertBuilder.setNegativeButton(getString(R.string.not_now)) { dialog, which ->
            dialog.dismiss()
        }
        alertBuilder.setPositiveButton(getString(R.string.continue_now)) { dialog, which ->
            requireActivity().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            dialog.dismiss()

        }
        val alertLocation = alertBuilder.create()
        alertLocation.show()
    }

    fun showGPSNotEnabledDialog(context: Context) {
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.enable_gps))
            .setMessage(context.getString(R.string.required_for_this_app))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }

    private fun checkPermissionRequired(list: Array<String>) {
        requireContext().let {
            if (hasPermissions(requireContext(), list)) {
                grantPermission.isGrant(true)
                if (LOCATION_PERMISSION in PERMISSIONS == true) {
                    if (isLocationEnabled(requireActivity())) {
                        dismiss()
                    } else {
                        showAlert(requireActivity())
                    }

                } else {
                    dismiss()
                }

            } else {
                permReqLauncher.launch(list)
            }
        }
    }

    var permissionsList = ArrayList<String>()
    var permissionsCount = 0
    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissionsCount = 0
            val list = ArrayList<Boolean>(permissions.values)
            permissionsList.clear()

            for (i in list.indices) {
                if (shouldShowRequestPermissionRationale(PERMISSIONS[i])) {
                    permissionsList.add(PERMISSIONS[i])
                } else if (!hasPermissions(requireContext(), PERMISSIONS)) {
                    permissionsCount++
                }
            }
            if (permissionsList.size > 0) {
                val array: Array<String> =
                    permissionsList.toArray(arrayOfNulls<String>(permissionsList.size))
                checkPermissionRequired(array)
            } else if (permissionsCount > 0) {
                //Show alert dialog
                showPermissionDialog()
            } else {
                if (LOCATION_PERMISSION in permissions == false) {
                    grantPermission.isGrant(true)
                } else {
                    if (isLocationEnabled(requireActivity())) {
                        startLocationService()
                    } else {
                        showAlert(requireActivity())
                    }

                }

                //All permissions granted. Do your stuff 🤞
            }
            // }
        }

    fun startLocationService() {

    }


    private fun showPermissionDialog() {
        try {
            val builder = AlertDialog.Builder(requireContext())
            builder.setCancelable(false)
            builder.setTitle("Permission required")
            builder.setMessage("Some permissions are needed to be allowed to use this app without any problems.")
            builder.setPositiveButton("Grant") { dialog, which ->
                dialog.cancel()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
                grantPermission.isGrant(true)
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            requireActivity().getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service_status", "Running")
                return true
            }
        }
        Log.i("Service_status", "Not running")
        return false
    }

    override fun onDestroy() {
        /*val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(requireActivity(), Restarter::class.java)
        requireActivity().sendBroadcast(broadcastIntent)*/
        super.onDestroy()
    }

    interface GrantPermission {
        fun isGrant(isPermission: Boolean, isCancel: Boolean = false)
    }

}