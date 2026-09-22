package com.sc.aipdriver.activities.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sc.aipdriver.activities.models.TempBags;

import java.util.List;

@Dao
public interface FarmTempBegDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FarmTempBegEntity entity);

    @Query("SELECT * FROM farm_temp_beg_queue WHERE isSynced = 0")
    List<FarmTempBegEntity> getPending();

    @Query("UPDATE farm_temp_beg_queue SET isSynced = 1 WHERE farmId = :farmId AND orderDate = :orderDate")
    void markSynced(int farmId, String orderDate);
}