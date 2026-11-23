package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateTransferLocationRequest {

    @SerializedName("TransferId")
    @Expose
    private Integer transferId;

    @SerializedName("DriverId")
    @Expose
    private Integer driverId;

    @SerializedName("FinishingLocation")
    @Expose
    private String finishingLocation;

    @SerializedName("Latitude")
    @Expose
    private Double latitude;

    @SerializedName("Longitude")
    @Expose
    private Double longitude;

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getFinishingLocation() {
        return finishingLocation;
    }

    public void setFinishingLocation(String finishingLocation) {
        this.finishingLocation = finishingLocation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
