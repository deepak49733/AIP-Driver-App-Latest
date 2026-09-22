package com.sc.aipdriver.activities.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhotoSyncDao {

    @Insert
    void insert(PhotoSyncEntity entity);

    @Query("SELECT * FROM photo_sync_queue WHERE isSynced = 0")
    List<PhotoSyncEntity> getPendingPhotos();

    @Query("UPDATE photo_sync_queue SET isSynced = 1 WHERE localId = :id")
    void markSynced(int id);
    @Query("SELECT DISTINCT rideId FROM photo_sync_queue WHERE isSynced = 0 AND apiType = :type")
    List<String> getPendingRideIds(String type);

    @Query("SELECT * FROM photo_sync_queue WHERE isSynced = 0 AND apiType = :type AND rideId = :rideId")
    List<PhotoSyncEntity> getPhotosByRide(String type, String rideId);

    @Query("SELECT DISTINCT farmId FROM photo_sync_queue WHERE isSynced = 0 AND apiType = :type")
    List<String> getPendingFarmIds(String type);

    @Query("SELECT * FROM photo_sync_queue WHERE isSynced = 0 AND apiType = :type AND farmId = :farmId")
    List<PhotoSyncEntity> getPhotosByFarm(String type, String farmId);

    @Query("UPDATE photo_sync_queue SET farmId = :newFarmId WHERE farmId = :oldFarmId AND apiType = :type")
    void updateFarmId(String type, String oldFarmId, String newFarmId);

    @Query("UPDATE photo_sync_queue SET rideId = :newRideId WHERE rideId = :oldRideId AND apiType = :type")
    void updateRideId(String type, String oldRideId, String newRideId);
    // ✅ NEW: delete single
    @Query("DELETE FROM photo_sync_queue WHERE localId = :id")
    void deleteById(int id);

    // ✅ NEW: delete by ride (best for your flow)
    @Query("DELETE FROM photo_sync_queue WHERE rideId = :rideId AND apiType = :type")
    void deleteByRide(String type, String rideId);

    // ✅ NEW: delete by farm (if needed)
    @Query("DELETE FROM photo_sync_queue WHERE farmId = :farmId AND apiType = :type")
    void deleteByFarm(String type, String farmId);
}
