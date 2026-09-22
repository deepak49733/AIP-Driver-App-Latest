package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PermissionData {

    @SerializedName("RideId")
    @Expose
    private int rideId;
    @SerializedName("Action")
    @Expose
    private int action;
    @SerializedName("FarmId")
    @Expose
    private int farmId;
    @SerializedName("FarmName")
    @Expose
    private String farmName;
    @SerializedName("RouteName")
    @Expose
    private String routeName;
    @SerializedName("ParentId")
    @Expose
    private int parentId;
    @SerializedName("RouteId")
    @Expose
    private int routeId;

    public int getIsPaused() {
        return isPaused;
    }

    public void setIsPaused(int isPaused) {
        this.isPaused = isPaused;
    }

    @SerializedName("IsPaused")
    @Expose
    private int isPaused;
//    @SerializedName("shouldLogout")
//    @Expose
//    private String shouldLogout;
    @SerializedName("OrderDate")
    @Expose
    private String OrderDate;

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

//    public String getShouldLogout() {
//        return shouldLogout;
//    }
//
//    public void setShouldLogout(String shouldLogout) {
//        this.shouldLogout = shouldLogout;
//    }
}
