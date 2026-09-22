package com.sc.aipdriver.activities

import android.content.Context
import android.net.*
import com.sc.aipdriver.activities.worker.SyncScheduler

object NetworkListener {

    fun listenNetwork(context: Context) {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {

                    SyncScheduler.runImmediateSync(context)

                }
            })
    }
}