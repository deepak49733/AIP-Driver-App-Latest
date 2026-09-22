package com.sc.aipdriver.activities.worker

import androidx.lifecycle.MutableLiveData

object SyncStatusManager {

    val syncCompleted = MutableLiveData<Boolean>()
    val syncStarted = MutableLiveData<Boolean>()

}