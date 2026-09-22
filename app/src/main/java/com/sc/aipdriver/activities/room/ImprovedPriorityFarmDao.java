package com.sc.aipdriver.activities.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData;
import com.sc.aipdriver.activities.models.PriorityFarmData;

import java.util.List;

@Dao
public interface ImprovedPriorityFarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImprovedFarms(List<ImprovedPriorityFarmData> farms);

    @Query("SELECT * FROM improved_priority_farms WHERE Remove = 0 ORDER BY priority ASC")
    List<ImprovedPriorityFarmData> getAllImprovedFarms();

    @Query("SELECT * FROM improved_priority_farms WHERE id = :farmId AND orderdate = :date LIMIT 1")
    ImprovedPriorityFarmData getFarmByIdAndDate(int farmId, String date);
    // DATE WISE DATA
    @Query("SELECT * FROM improved_priority_farms WHERE orderdate = :date AND Remove = 0 ORDER BY priority ASC")
    List<ImprovedPriorityFarmData> getFarmsByDate(String date);
    @Query("UPDATE improved_priority_farms SET isVisited = :isVisited WHERE id = :farmId AND orderdate = :date")
    void updateFarmVisited(int farmId, String date, boolean isVisited);
    @Query("DELETE FROM improved_priority_farms")

    void clearImprovedFarms();
    @Query("UPDATE improved_priority_farms SET isActiveRide = 1 WHERE id = :farmId AND orderdate = :date")
    void markFarmAsActive(int farmId, String date);
    @Query("UPDATE improved_priority_farms SET isActiveRide = 0 WHERE orderdate = :date")
    void resetActiveFarms(String date);
    @Query("SELECT * FROM improved_priority_farms WHERE isActiveRide = 1 AND orderdate = :date LIMIT 1")
    ImprovedPriorityFarmData getActiveFarm(String date);

    @Query("UPDATE improved_priority_farms SET begs = :begs, tmperature = :temp, IsLoaded= :isLoaded, isSynced = :isSynced, isCompleted = :isCompleted WHERE id = :farmId AND orderdate = :date")
    void updateImprovedFarmTempBags(int farmId, String date, int begs, double temp, int isLoaded, int isSynced,int isCompleted);

    @Query("UPDATE improved_priority_farms SET isCompleted = :status WHERE id = :farmId AND orderdate = :date")
    void updateImprovedFarmStatus(int farmId, String date, int status);

    @Query("SELECT COUNT(*) FROM improved_priority_farms WHERE orderdate = :date AND Remove = 0")
    int getImprovedFarmCountByDate(String date);

    @Query("SELECT COUNT(*) FROM improved_priority_farms WHERE orderdate = :date AND Remove = 0 AND isLoaded = 1")
    int getLoadedImprovedFarmCountByDate(String date);
    @Query("SELECT COUNT(*) FROM improved_priority_farms WHERE orderdate = :date AND Remove = 0 AND (isLoaded = 1 OR isCompleted = 1)")
    int getProcessedImprovedFarmCountByDate(String date);

    @androidx.room.Update
    void updateImprovedFarm(ImprovedPriorityFarmData farm);

    @Query("UPDATE improved_priority_farms SET isEmailSent = :isEmailSent WHERE id = :farmId AND orderdate = :date")
    void updateEmailSent(int farmId, String date, int isEmailSent);

    @Query("UPDATE improved_priority_farms SET RideId = :newRideId WHERE id = :farmId AND orderdate = :date")
    void updateRideId(int farmId, String date, String newRideId);

}