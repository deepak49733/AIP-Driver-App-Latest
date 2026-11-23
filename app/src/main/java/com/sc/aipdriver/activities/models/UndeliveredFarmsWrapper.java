package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UndeliveredFarmsWrapper {

    @SerializedName("Status")
    @Expose
    private Boolean status;

    @SerializedName("Message")
    @Expose
    private String message;

    @SerializedName("DriverId")
    @Expose
    private Integer driverId;

    @SerializedName("RouteId")
    @Expose
    private Integer routeId;

    @SerializedName("OrderDate")
    @Expose
    private String orderDate;

    @SerializedName("TotalFarms")
    @Expose
    private Integer totalFarms;

    @SerializedName("Data")
    @Expose
    private List<UndeliveredFarmData> data;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Integer getTotalFarms() {
        return totalFarms;
    }

    public void setTotalFarms(Integer totalFarms) {
        this.totalFarms = totalFarms;
    }

    public List<UndeliveredFarmData> getData() {
        return data;
    }

    public void setData(List<UndeliveredFarmData> data) {
        this.data = data;
    }
}
