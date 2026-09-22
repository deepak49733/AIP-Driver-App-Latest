package com.sc.aipdriver.activities.utils

import com.google.gson.JsonObject
import com.sc.aipdriver.activities.room.RideSyncEntity

data class FinishEmailGroup(
    val entity: RideSyncEntity,
    val json: JsonObject
                )