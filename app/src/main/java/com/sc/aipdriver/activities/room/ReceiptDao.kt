package com.sc.aipdriver.activities.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceipts(receipts: List<ReceiptEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReceipt(receipt: ReceiptEntity)

    @Query("SELECT * FROM receipts WHERE ParentId = :parentId")
    fun getReceiptsByParentId(parentId: String): List<ReceiptEntity>

    @Query("DELETE FROM receipts WHERE ParentId = :parentId")
    fun deleteReceiptsByParentId(parentId: String)

    @Query("UPDATE receipts SET ID = :serverId WHERE RID = :rid")
    fun updateServerId(rid: String, serverId: String)
}
