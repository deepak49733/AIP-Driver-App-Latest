package com.sc.aipdriver.activities.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey
    val RID: String,
    val ID: String?,
    val Recipt_Img: String?,
    val ParentId: String?,
    val FuelOdometer: String?,
    val Gallon: String?,
    val PricePgallon: String?,
    val FuelCost: String?,
    val WashCost: String?
)
