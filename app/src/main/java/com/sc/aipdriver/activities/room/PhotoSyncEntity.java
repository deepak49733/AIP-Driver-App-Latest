package com.sc.aipdriver.activities.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photo_sync_queue")
public class PhotoSyncEntity {

    @PrimaryKey(autoGenerate = true)
    public int localId;

    public String rideId;
    public String farmId; // Can be null if it's a general ride photo
    public String imagePath;
    public String apiType; // START_RIDE, END_RIDE, RECEIPT, FARM_END
    public int imageIndex; // To know which part it belongs to (img1, img2, etc.)
    public String driverId;
    public String Token;
    public boolean isSynced = false;
}
