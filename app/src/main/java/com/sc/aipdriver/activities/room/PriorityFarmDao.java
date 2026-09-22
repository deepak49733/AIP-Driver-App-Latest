package com.sc.aipdriver.activities.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData;
import com.sc.aipdriver.activities.models.PriorityFarmData;

import java.util.List;

@Dao
public interface PriorityFarmDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PriorityFarmData> farms);

    @Query("SELECT * FROM priority_farms WHERE Remove = 0 ORDER BY priority ASC")
    List<PriorityFarmData> getAllFarms();

    // DATE WISE DATA
    @Query("SELECT * FROM priority_farms WHERE orderDate = :date AND Remove = 0 ORDER BY priority ASC")
    List<PriorityFarmData> getFarmsByDate(String date);

    @Query("DELETE FROM priority_farms")
    void clearFarms();

    @Query("SELECT * FROM priority_farms WHERE id = :farmId AND orderDate = :date LIMIT 1")
    PriorityFarmData getFarmTempBags(String farmId, String date);

    @Query("UPDATE priority_farms SET begs = :begs, tmperature = :temp, isLoaded = :isLoaded, isSynced = :isSynced WHERE id = :farmId AND orderDate = :date")
    void updateFarmTempBags(int farmId, String date, int begs, double temp, int isLoaded, int isSynced);
    @Query("SELECT * FROM priority_farms WHERE id = :farmId AND orderdate = :date LIMIT 1")
    PriorityFarmData getFarmByIdAndDate(int farmId, String date);
    @Query("SELECT COUNT(*) FROM priority_farms WHERE orderDate = :date AND Remove = 0")
    int getFarmCountByDate(String date);
    @Query("SELECT COUNT(*) FROM priority_farms WHERE orderDate = :date AND Remove = 0 AND (isLoaded = 1 OR isCompleted = 1)")
    int getProcessedFarmCountByDate(String date);

    @androidx.room.Update
    void updateFarm(PriorityFarmData farm);

    @Query("UPDATE priority_farms SET isEmailSent = :isEmailSent WHERE id = :farmId AND orderDate = :date")
    void updateEmailSent(int farmId, String date, int isEmailSent);
}