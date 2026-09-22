package com.sc.aipdriver.activities.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ride_finish_queue")
data class RideFinishEntity(

    @PrimaryKey(autoGenerate = true)
    var localId: Int = 0,
    var EndDateTime: String="",
    var comments:String="",
    var action: String?,
    var rideId: String ="0",
    var uid: String?,
    var lat: String?,
    var lng: String?,
    var endOdometer: String?,
    var totalMiles: String?,
    var address: String?,
    var state: String?,
    var city: String?,
    var country: String?,
    var firmId: String?,
    var bagsDelivered: String?,
    var temperature: String?,
    var routeId: String?,
    var coolerTemp: String?,
    var driverId: String?,
    var Token: String?,
    var synced: Boolean = false
)