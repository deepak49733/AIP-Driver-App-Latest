package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverTransferData {

    @SerializedName("DriverId")
    @Expose
    private Integer driverId;

    @SerializedName("DriverName")
    @Expose
    private String driverName;

    @SerializedName("ContactNumber")
    @Expose
    private String contactNumber;

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return driverName != null ? driverName : "";
    }
}
