package com.sc.aipdriver.activities.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RideFinishDao {

    @Insert
    fun insert(entity: RideFinishEntity)

    @Query("SELECT * FROM ride_finish_queue WHERE synced = 0")
    fun getPending(): List<RideFinishEntity>

    @Query("UPDATE ride_finish_queue SET synced = 1 WHERE localId = :id")
    fun markSynced(id: Int)
    // ✅ DELETE single record
    @Query("DELETE FROM ride_finish_queue WHERE localId = :id")
    fun deleteById(id: Int)

    // ✅ DELETE all synced records (optional cleanup)
    @Query("DELETE FROM ride_finish_queue WHERE synced = 1")
    fun deleteSynced()

    // ✅ DELETE all pending (optional, rarely used)
    @Query("DELETE FROM ride_finish_queue")
    fun deleteAll()
}