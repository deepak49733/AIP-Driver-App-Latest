package com.sc.aipdriver.activities.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "farm_temp_beg_queue",
        primaryKeys = {"farmId","orderDate"}
)
public class FarmTempBegEntity {
    @NonNull
    public int farmId;

    public int beg;

    public String temp;

    public String driverId;

    public String Token;

    public String coolerTemp;

    public String routeName;
    @NonNull
    public String orderDate="";
    public boolean isImproved = false;

    public boolean isSynced = false;
    public int ParentId;
}