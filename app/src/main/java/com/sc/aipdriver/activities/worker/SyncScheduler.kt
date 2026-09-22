package com.sc.aipdriver.activities.worker

import android.content.Context
import androidx.work.*
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager
import java.util.concurrent.TimeUnit

object SyncScheduler {

    fun startSyncWorker(context: Context) {

//        if (!SharedprefrenceManager(context).isSyncMode()) {
//            WorkManager.getInstance(context).cancelUniqueWork("IMMEDIATE_SYNC")
//            WorkManager.getInstance(context).cancelUniqueWork("SYNC_QUEUE")
//            return
//        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "SYNC_QUEUE",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    fun runImmediateSync(context: Context) {
        if (!SharedprefrenceManager(context).isSyncMode()) {
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "IMMEDIATE_SYNC",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}