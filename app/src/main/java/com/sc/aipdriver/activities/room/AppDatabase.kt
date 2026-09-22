package com.sc.aipdriver.activities.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData
import com.sc.aipdriver.activities.models.PriorityFarmData

@Database(
    entities = [
        RideSyncEntity::class,
        PriorityFarmData::class ,
        FarmTempBegEntity::class,
        RideFinishEntity::class,
        ImprovedPriorityFarmData::class,
        PhotoSyncEntity::class,
        EmailSyncEntity::class,
        ReceiptEntity::class
    ],
    version = 13
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rideSyncDao(): RideSyncDao
    abstract fun priorityFarmDao(): PriorityFarmDao
    abstract fun improvedPriorityFarmDao(): ImprovedPriorityFarmDao
    abstract fun farmTempBegDao(): FarmTempBegDao
    abstract fun rideFinishDao(): RideFinishDao
    abstract fun photoSyncDao(): PhotoSyncDao
    abstract fun emailSyncDao(): EmailSyncDao
    abstract fun receiptDao(): ReceiptDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ride_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}