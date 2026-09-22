package com.sc.aipdriver.activities.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "email_sync_queue")
data class EmailSyncEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val parentid: String,
    val driverid: String,
    val fid: String,
    val routeId: String,
    val lat: String,
    val lng: String,
    val vehicleId: String,
    val token: String,
    val createdAt: String = "",
    var synced: Boolean = false
)

