package com.sc.aipdriver.activities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager

fun isInternetAvailable( context: Context): Boolean {
    val sharedprefrenceManager= SharedprefrenceManager(context)
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    // Require validated internet capability so captive portals / unvalidated networks don't count
    val hasTransport = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

    val validated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    // In sync mode, treat the app as offline-first so queue/sync flow is used.
//    if (sharedprefrenceManager.isSyncMode()) {
//        return false
//    }
    return hasTransport && validated
}