package com.sc.aipdriver.activities.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ride_sync")
data class RideSyncEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val token:String,
    val payload: String,   // JSON of StartDataSend
    val apiType: String,   // START / LOG / FINISH
    var isSynced: Boolean = false
)