package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UndeliveredFarmData {

    @SerializedName("RideId")
    @Expose
    private Integer rideId;

    @SerializedName("DriverRideDetailId")
    @Expose
    private Integer driverRideDetailId;

    @SerializedName("FarmId")
    @Expose
    private Integer farmId;

    @SerializedName("FarmName")
    @Expose
    private String farmName;

    @SerializedName("DriverId")
    @Expose
    private Integer driverId;

    @SerializedName("RouteId")
    @Expose
    private Integer routeId;

    @SerializedName("OrderDate")
    @Expose
    private String orderDate;

    private boolean isSelected = false;

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public Integer getDriverRideDetailId() {
        return driverRideDetailId;
    }

    public void setDriverRideDetailId(Integer driverRideDetailId) {
        this.driverRideDetailId = driverRideDetailId;
    }

    public Integer getFarmId() {
        return farmId;
    }

    public void setFarmId(Integer farmId) {
        this.farmId = farmId;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
