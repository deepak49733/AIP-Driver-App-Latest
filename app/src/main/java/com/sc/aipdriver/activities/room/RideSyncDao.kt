package com.sc.aipdriver.activities.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RideSyncDao {

    @Insert
    fun insert(data: RideSyncEntity)

    @Query("SELECT * FROM ride_sync WHERE isSynced = 0")
    fun getPending(): List<RideSyncEntity>

    @Query("UPDATE ride_sync SET isSynced = 1 WHERE id = :id")
    fun markSynced(id: Int)

    @Query("UPDATE ride_sync SET isSynced = 1 WHERE apiType = :type AND isSynced = 0")
    fun markLatestSynced(type: String)

    // ✅ NEW: delete after sync
    @Query("DELETE FROM ride_sync WHERE id = :id")
    fun deleteById(id: Int)
}