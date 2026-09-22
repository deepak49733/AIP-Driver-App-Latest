package com.sc.aipdriver.activities.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EmailSyncDao {

    @Insert
    fun insert(entity: EmailSyncEntity)

    @Query("SELECT * FROM email_sync_queue WHERE synced = 0")
    fun getPending(): List<EmailSyncEntity>

    @Query("UPDATE email_sync_queue SET synced = 1 WHERE id = :id")
    fun markSynced(id: Int)

    @Query("DELETE FROM email_sync_queue WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM email_sync_queue WHERE synced = 1")
    fun deleteSynced()
}
