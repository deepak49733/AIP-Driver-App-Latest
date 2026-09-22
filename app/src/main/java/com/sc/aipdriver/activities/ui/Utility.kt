package com.sc.aipdriver.activities.ui


import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ContentValues
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.sc.aipdriver.R
import com.sc.aipdriver.activities.fragments.PermissionFragment
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


public class Utility {

    fun showSettingsDialog(context: Activity, dismis: DismissSetting? = null) {

        if (alertDialog != null) {
            if (!alertDialog?.isShowing!!) {
                alertPermission(context, dismis)
            }
        } else {
            alertPermission(context, dismis)
        }
    }
    fun compressImageFile(context: Context, originalFile: File): File? {
        return try {
            if (!originalFile.exists() || !originalFile.canRead()) {
                Log.e("Compress", "File not found or unreadable: " + originalFile.absolutePath)
                return originalFile
            }

            // Decode with inSampleSize for memory efficiency (e.g. max 1280px)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(originalFile.absolutePath, options)
            val maxDim = 1280
            var scale = 1
            while (options.outWidth / scale > maxDim || options.outHeight / scale > maxDim) {
                scale *= 2
            }
            options.inSampleSize = scale
            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath, options)
            if (bitmap == null) {
                Log.e("Compress", "Bitmap decode failed")
                return originalFile
            }
            val out = ByteArrayOutputStream()
            var quality = 100

            // Try WebP for Android R and above
            val format: Bitmap.CompressFormat
            format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.JPEG
            }
            bitmap.compress(format, quality, out)

            // Reduce quality loop
            while (out.toByteArray().size / 1024 > 100 && quality > 10) {
                out.reset()
                quality -= 5
                bitmap.compress(format, quality, out)
            }

            // Save compressed file
            val ext = if (format == Bitmap.CompressFormat.WEBP_LOSSY) ".webp" else ".jpg"
            val compressedFile =
                File(context.cacheDir, "compressed_" + System.currentTimeMillis() + ext)
            val fos = FileOutputStream(compressedFile)
            fos.write(out.toByteArray())
            fos.flush()
            fos.close()
            compressedFile
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            originalFile
        }
    }


    fun permissionArray(): Array<String> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                // Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                REQUIRED_PERMISSIONS = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES
            )
        }
        return REQUIRED_PERMISSIONS

    }



    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI_AWARE
                    )
                ) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                } else if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    return true
                }
//                else{
//                        val activeNetworkInfo = connectivityManager.activeNetworkInfo
//                        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
//                            return true
//                        }
//                }
            }
        }
        return false
    }


    fun TextView.textColor(context: Context, id: Int) {
        this.setTextColor(ContextCompat.getColor(context, id))
    }


    var alertDialog: AlertDialog? = null
     val MULTIPLE_PERMISSIONS = 10
    lateinit var REQUIRED_PERMISSIONS: Array<String>
    lateinit var REQUIRED_CAMERA_PERMISSIONS: Array<String>

    fun hasPermissions(context: Activity, permissions: Array<String>): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(context, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                context, listPermissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    fun alertPermission(context: Activity, dismis: DismissSetting? = null) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("\"Need Permissions\"")
        builder.setMessage("\"This app needs permission to use this feature. You can grant them in app settings.\"")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("GOTO SETTINGS")
        { dialogInterface, which ->
            alertDialog?.dismiss()
            alertDialog = null
            openSettings(context)
        }
        builder.setNegativeButton("Cancel") { dialogInterface, which ->
            alertDialog?.dismiss()
            alertDialog = null
            if (dismis != null) {
                dismis.alertSetting()
            }


        }
        alertDialog = builder.create()
        alertDialog?.setCancelable(false)
        alertDialog!!.show()
    }

    fun openSettings(context: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivityForResult(intent, 101)
    }




    @SuppressLint("SimpleDateFormat")
    fun getCurrentMonth(month: Int): Int {
        val c: Calendar = GregorianCalendar()
        c.add(Calendar.MONTH, -month)
        val sdfr = SimpleDateFormat("MM")
        return sdfr.format(c.time).toInt()
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDay(): Int {
        val sdf = SimpleDateFormat("dd")
        return sdf.format(Date()).toInt()
    }

    fun parseDateToddMMyyyy(date1: String?): String? {
        val inputPattern = "dd/MM/yyyy"
        val outputPattern = "yyyy-MM-dd"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(date1)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }
    fun changeTimeTo12Hr(time: String): Date {
        val dateFormat: DateFormat = SimpleDateFormat("hh:mm aa")
        val date = dateFormat.parse(time)
        return date
    }

    fun getAttendanceDate(currtdate: String): Date {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.parse(currtdate)
        return date
    }

    fun permissionCameraList(): Array<String> {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {

            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

        } else {

            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        }

    }


    fun isLocationPermission(context: Context): Boolean {
        val PERMISSIONS = permissionLocation()
        val permission = PermissionFragment.newInstance(PERMISSIONS)
        if (permission.hasLocationPermissions(context, PERMISSIONS)) {
            return true
        } else {
            return false

        }
    }

    fun permissionLocation(): Array<String> {
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    }

    fun readWritePermission(): Array<String> {
        lateinit var REQUIRED_PERMISSIONS: Array<String>
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                REQUIRED_PERMISSIONS = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        }
        return REQUIRED_PERMISSIONS

    }

    fun notificationPermission(): Array<String> {
        lateinit var REQUIRED_PERMISSIONS: Array<String>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
            )
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                REQUIRED_PERMISSIONS = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        return REQUIRED_PERMISSIONS

    }

    fun <T> getPayload(payloadClass: Class<T>?, jsonData: String?): T {
        return Gson().fromJson(jsonData, payloadClass)
    }

    var SHORT_TOAST = 0




//    fun setFileMultipartBody(fileName: File, key: String): MultipartBody.Part {
//
//        val faceBody = fileName.asRequestBody("image/*".toMediaTypeOrNull())
//        return MultipartBody.Part.createFormData(
//            key, fileName.name, faceBody
//        )
//    }

    fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else appContext.filesDir
    }

//    fun getRequestBody(value: String): RequestBody {
//        return value.toRequestBody("text/plain".toMediaTypeOrNull())
//    }

    fun deviceInfo(context: Context): String {
        var deviceInfoMessage: String = ""
        try {
            deviceInfoMessage = "Client id: ${
                Settings.Secure.getString(
                    context.contentResolver, Settings.Secure.ANDROID_ID
                )
            }, Device OS: Android,  Device OS version: ${Build.VERSION.RELEASE}, App Version: ${
                getAppVersion(
                    context
                )
            },Device Brand: ${Build.BRAND}, Device Model:  ${Build.MODEL} , Device Manufacturer: ${Build.MANUFACTURER}"

        } catch (e: Exception) {
        }

        return deviceInfoMessage;
    }

    fun getAppVersion(mCotext: Context): String {
        var version = ""
        try {
            val manager = mCotext.packageManager
            val info: PackageInfo
            info = manager.getPackageInfo(
                mCotext.packageName, 0
            )
            version = info.versionName!!
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: Exception) {

        }

        return version
    }

    fun flip(src: Bitmap): Bitmap? {
        // create new matrix for transformation
        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f)
        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    @SuppressLint("Range")
    fun getFileName(uri: Uri, context: Context): String? {
        var result: String? = null
        var file: File? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                    file = File(context.cacheDir, result)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val outputStream = FileOutputStream(file)
                    var read: Int
                    val maxBufferSize = 1 * 1024 * 1024
                    val bytesAvailable = inputStream!!.available()

                    val bufferSize =
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            Integer.min(bytesAvailable, maxBufferSize)
                        } else {
                            // TODO("VERSION.SDK_INT < N")
                            Math.min(bytesAvailable, maxBufferSize)
                        }

                    val buffers = ByteArray(bufferSize)

                    while (inputStream.read(buffers).also { read = it } != -1) {
                        outputStream.write(buffers, 0, read)
                    }

                    inputStream.close()
                    outputStream.close()
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            /* val cut = result!!.lastIndexOf('/')
             if (cut != -1) {
                 result = result.substring(cut + 1)
             }*/
        }
        //filePath = file!!.absolutePath
        return file?.absolutePath
    }



    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return sdf.format(Date())
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd")
        return sdf.format(Date())
    }

    fun getCurrentMonth(): String {
        val sdf = SimpleDateFormat("MMMM")
        return sdf.format(Date())
    }

    fun getCurrentDayName(): String {
        val sdf = SimpleDateFormat("EEEE")
        return sdf.format(Date())
    }


    fun getTimeFromDate(date: String): String {
        try{
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val sdf2 = SimpleDateFormat("hh:mm aa")
            val timeDate = sdf.parse(date)
            return sdf2.format(timeDate)
        }
        catch (e:Exception)
        {
            return "00:00"
        }
    }

    fun getTimeHHMM(date: String): String {
        try{
            val sdf = SimpleDateFormat("HH:mm")
            val sdf2 = SimpleDateFormat("hh:mm aa")
            val timeDate = sdf.parse(date)
            return sdf2.format(timeDate)
        }
        catch (e:Exception)
        {
            return "00:00"
        }
    }

    fun getTimeDate(date: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val sdf2 = SimpleDateFormat("dd MMM yyyy HH:mm aa")
        val timeDate = sdf.parse(date)
        return sdf2.format(timeDate)

    }

    fun getNotificationTime(date: String?): String {
        if (date!=null)
        {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val sdf2 = SimpleDateFormat("dd MMM yyyy HH:mm aa")
            val timeDate = sdf.parse(date)
            return sdf2.format(timeDate)
        }
        return ""


    }

    fun getRegulariztionDate(date: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val sdf2 = SimpleDateFormat("dd/MM/yyyy")
        val timeDate = sdf.parse(date)
        return sdf2.format(timeDate)

    }


    fun getDateDDMMYY(date: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        // val sdf2 = SimpleDateFormat("dd MMM yyyy")
        val sdf2 = SimpleDateFormat("dd MMMM yyyy")
        val timeDate = sdf.parse(date)
        return sdf2.format(timeDate)
    }

    fun getDD(date: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val sdf2 = SimpleDateFormat("EEEE")
        val timeDate = sdf.parse(date)
        return sdf2.format(timeDate)
    }


    fun checkCurrentDate(date: String): Boolean {
        return try {
            val sdf2 = SimpleDateFormat("dd/MM/yyyy")
            val strDate = sdf2.parse(date)
            return if (getCurrentDateMilisecond() == strDate.time) {
                true
            } else
                false
        } catch (e: java.lang.Exception) {
            false
        }

    }

    fun checkDefaultDateRange(): String {
        val sdf = SimpleDateFormat("MM/yyyy")
        return sdf.format(Date())

    }


    fun getCurrentDateMilisecond(): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        var timeInMilliseconds: Long = 0
        var date = sdf.format(Date())
        try {
            val mDate = sdf.parse(date)
            timeInMilliseconds = mDate.time
            return timeInMilliseconds
        } catch (e: ParseException) {
            e.printStackTrace()
            return 0
        }
    }

    //get given time in AM/PM
    fun getDisplayTime(time: String?): String? {
        var output_time: String? = "-"
        if (time != null) {
            val sdf3 = SimpleDateFormat("HH:mm:ss")
            try {
                val date4 = sdf3.parse(time)
                //new format
                val sdf4 = SimpleDateFormat("hh:mm aa")
                //formatting the given time to new format with AM/PM
                output_time = sdf4.format(date4)
                println("Given date and time in AM/PM: " + sdf4.format(date4))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }


        return output_time
    }

    fun setAddress(lat: Double, lon: Double, context: Context): String {
        try {
            val addresses: List<Address>
            val geocoder = Geocoder(context, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                lat,
                lon,
                1
            ) as List<Address> // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            /* val city = addresses[0].locality
             val state = addresses[0].adminArea
             val country = addresses[0].countryName
             val postalCode = addresses[0].postalCode
             val knownName = addresses[0].featureName*/
            return "$address"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    fun setCity(lat: Double, lon: Double, context: Context): List<Address>? {
        if (lat != null && lon != null) {
            val addresses: List<Address>
            try {

                val geocoder = Geocoder(context, Locale.getDefault())
                addresses = geocoder.getFromLocation(
                    lat,
                    lon,
                    1
                ) as List<Address> // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                return addresses
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            return null
        }
        return null
    }

    fun fileTo_Base64(activity: Activity, uri: Uri): String {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(
                activity.contentResolver, uri
            )
            val stream = ByteArrayOutputStream()
            val matrix = Matrix()
            matrix.postRotate(getOrientation(uri).toFloat())
            val mbitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
            )
            mbitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream)

            val imageBytes: ByteArray = stream.toByteArray()
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)


        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }


    }

    fun getOrientation(photoUri: Uri): Int {
        val imageFile = File(photoUri.path)
        return try { //from w  ww. j  a v  a2  s.c  o  m
            val exif = ExifInterface(
                imageFile.absolutePath
            )
            when (exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> ExifInterface.ORIENTATION_UNDEFINED
            }
        } catch (e: IOException) {
            Log.e("WallOfLightApp", e.message!!)
            90
        }
    }


    fun getDistanceFromLatLonInKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double {
        val startPoint = Location("locationA")
        /*val lat1 = 28.5002676
        val lon1 = 77.0778731

        val lat2 = 28.5002676
        val lon2 = 77.0778731*/

//        val lat2=28.50067579577303
//        val lon2=77.0771079318845

        Log.d("distanceValuse", "distance:\n lat1 $lat1 lon1 $lon1 \n lat2 $lat2 lon2 $lon2 ")


        startPoint.latitude = lat1 //28.5002676,77.0778731
        startPoint.longitude = lon1

        val endPoint = Location("locationB")
        endPoint.latitude = lat2 //28.446890,77.028236    28.45084999342639, 77.02659188466039
        endPoint.longitude = lon2

        var distance: Double = startPoint.distanceTo(endPoint).toDouble()
        distance = String.format("%.2f", distance).toDouble()
        Log.d("distanceValuse", "distance:\n $distance ")
        return distance
    }

    fun UrlToBase64(url: String): String {

        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

            StrictMode.setThreadPolicy(policy)
            val url = URL(url)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            var myBitmap: Bitmap = BitmapFactory.decodeStream(input)
            val stream = ByteArrayOutputStream()
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()
            ////Log.d("userImageBase64", userImageBase64);
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            return ""
        }


    }

    fun getDatesBetween(dateString1: String, dateString2: String): List<String> {
        val dates = ArrayList<String>()
        val input = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = input.parse(dateString1)
            date2 = input.parse(dateString2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        date1?.let {
            val cal1 = Calendar.getInstance()
            cal1.time = date1
            val cal2 = Calendar.getInstance()
            cal2.time = date2
            while (!cal1.after(cal2)) {
                val output = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dates.add(output.format(cal1.time))
                cal1.add(Calendar.DATE, 1)
            }
        }

        return dates
    }


    fun getName(fName: String?, mName: String?, lName: String?): String {
        var nameVaule = ""
        try {
            if (fName != null && !fName.trim().equals(null) && fName.trim().isNotEmpty()) {

                val upperString: String =
                    fName.trim().substring(0, 1).uppercase(Locale.getDefault()) + fName.trim()
                        .substring(1).lowercase()
                nameVaule = upperString
            }
            if (mName != null && !mName.trim().equals(null) && mName.trim().isNotEmpty()) {
                val upperString: String =
                    mName.trim().substring(0, 1).uppercase(Locale.getDefault()) + mName.trim()
                        .substring(1).lowercase()

                nameVaule = "$nameVaule ${upperString.trim()}"
            }
            if (lName != null && !lName.trim().equals(null) && lName.trim().isNotEmpty()) {
                val upperString: String =
                    lName.trim().substring(0, 1).uppercase(Locale.getDefault()) + lName.trim()
                        .substring(1).lowercase()

                nameVaule = "$nameVaule ${upperString.trim()}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return nameVaule
    }

    fun camalCalse(text: String): String {
        return text!!.substring(0, 1).uppercase() + text.substring(1)
            .lowercase()
    }



    fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(textView.context, color),
                        PorterDuff.Mode.SRC_IN
                    )
            }
        }
    }




    fun appVersionChangeInt(version1: String): Int {
        var version = version1
        if (version.contains("-")) {
            val split: List<String> = version1.split("-")
            val firstSubString = split[0]
            version = firstSubString
        }
        var vers = version.replace(".", "")
        return vers.toInt()

    }

    fun getLicenceCurrentDate(): String? {
        //12-06-2023 17:38
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
        return simpleDateFormat.format(System.currentTimeMillis())
    }

    fun LicenceEndDateDiffrence(endDate: String): Boolean {
        var LicenceExp = false
        try {
            val CurrentDate = getLicenceCurrentDate()
            val endDate = endDate
            val cuttent: Date
            val end_Date: Date
            val dates = SimpleDateFormat("dd-MM-yyyy HH:mm")
            cuttent = dates.parse(CurrentDate)
            end_Date = dates.parse(endDate)

            val cuttentInMilliseconds1: Long = cuttent.getTime()
//            val startInMilliseconds2: Long = start_date.getTime()
            val endMilliseconds: Long = end_Date.getTime()

            if (endMilliseconds >= cuttentInMilliseconds1
            ) {
                LicenceExp = true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return LicenceExp
    }



    fun base64ToBitmap(encodedImage: String): Bitmap? {
        try {
            var cleanImage = encodedImage.replace("data:image/png;base64,", "")
                .replace("data:image/jpeg;base64,", "")
            val decodedString = Base64.decode(cleanImage, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            return decodedByte
        } catch (e: Exception) {
            return null
        }
    }

    fun getLocationWithCheckNetworkAndGPS(mContext: Context): Location? {
        val lm = (mContext.getSystemService(Service.LOCATION_SERVICE) as LocationManager)
        var isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var isNetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var networkLoacation: Location? = null
        var gpsLocation: Location? = null
        var finalLoc: Location? = null
        if (isGpsEnabled) if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (isNetworkLocationEnabled) networkLoacation =
            lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (gpsLocation != null && networkLoacation != null) {

            //smaller the number more accurate result will
            return if (gpsLocation.accuracy > networkLoacation.accuracy) networkLoacation.also {
                finalLoc = it
            } else gpsLocation.also { finalLoc = it }
        } else {
            if (gpsLocation != null) {
                return gpsLocation.also { finalLoc = it }
            } else if (networkLoacation != null) {
                return networkLoacation.also { finalLoc = it }
            }
        }
        return finalLoc
    }

    fun validateMobileNumber(mobileNumber: String?): Boolean {
        Log.d("VishalData", "$mobileNumber")
        // Regular expression for a typical 10-digit mobile number
        val regex = "^[0-9]{10}$"

        // Compile the regex pattern
        val pattern: Pattern = Pattern.compile(regex)

        // Create a Matcher object
        val matcher: Matcher = pattern.matcher(mobileNumber)

        // Check if the mobile number matches the pattern
        return matcher.matches()
    }

    companion object {
        @JvmStatic
        fun saveImageToGallery(context: Context, sourceFile: File): Uri? {
            return saveToGalleryInternal(context, sourceFile, "AIPDriver")
        }

        private fun saveToGalleryInternal(context: Context, sourceFile: File, albumName: String): Uri? {
            if (!sourceFile.exists()) {
                Log.w("GallerySave", "Source file does not exist: ${sourceFile.absolutePath}")
                return null
            }

            return try {
                val fileName = sourceFile.name

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(
                            MediaStore.Images.Media.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + "/$albumName"
                        )
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }

                    val resolver = context.contentResolver
                    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                        ?: return null

                    resolver.openOutputStream(uri)?.use { outputStream ->
                        sourceFile.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    } ?: run {
                        resolver.delete(uri, null, null)
                        return null
                    }

                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                    uri
                } else {
                    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val albumDir = File(picturesDir, albumName)
                    if (!albumDir.exists() && !albumDir.mkdirs()) {
                        Log.w("GallerySave", "Unable to create gallery folder: ${albumDir.absolutePath}")
                    }

                    val destinationFile = File(albumDir, fileName)
                    sourceFile.inputStream().use { inputStream ->
                        FileOutputStream(destinationFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(destinationFile.absolutePath),
                        arrayOf("image/jpeg"),
                        null
                    )
                    Uri.fromFile(destinationFile)
                }
            } catch (e: Exception) {
                Log.e("GallerySave", "Failed to save image to gallery", e)
                null
            }
        }
    }
}