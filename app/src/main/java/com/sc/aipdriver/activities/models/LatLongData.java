package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dev on 13/3/18.
 */

public class LatLongData {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("DriverId")
    @Expose
    private Integer driverId;
    @SerializedName("RideId")
    @Expose
    private Integer rideId;
    @SerializedName("ParentId")
    @Expose
    private Integer parentId;

    @SerializedName("VehicleId")
    @Expose
    private Integer vehicleId;

    @SerializedName("RouteId")
    @Expose
    private Integer routeId;

    @SerializedName("UID")
    @Expose
    private Integer uID;

    @SerializedName("startTime")
    @Expose
    private String startTime;

    @SerializedName("EndTime")
    @Expose
    private String endTime;

    @SerializedName("StartOdometer")
    @Expose
    private String startOdometer;


    @SerializedName("EndOdeometer")
    @Expose
    private Object endOdeometer;

    @SerializedName("lat")
    @Expose
    private Object lat;

    @SerializedName("lng")
    @Expose
    private Object lng;

    @SerializedName("Action")
    @Expose
    private Object action;

    @SerializedName("liRoutePlannerDetail")
    @Expose
    private List<RoutePlannerDetail> routePlannerDetails = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getuID() {
        return uID;
    }

    public void setuID(Integer uID) {
        this.uID = uID;
    }

    public List<RoutePlannerDetail> getRoutePlannerDetails() {
        return routePlannerDetails;
    }

    public void setRoutePlannerDetails(List<RoutePlannerDetail> routePlannerDetails) {
        this.routePlannerDetails = routePlannerDetails;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getUID() {
        return uID;
    }

    public void setUID(Integer uID) {
        this.uID = uID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartOdometer() {
        return startOdometer;
    }

    public void setStartOdometer(String startOdometer) {
        this.startOdometer = startOdometer;
    }

    public Object getEndOdeometer() {
        return endOdeometer;
    }

    public void setEndOdeometer(Object endOdeometer) {
        this.endOdeometer = endOdeometer;
    }

    public Object getLat() {
        return lat;
    }

    public void setLat(Object lat) {
        this.lat = lat;
    }

    public Object getLng() {
        return lng;
    }

    public void setLng(Object lng) {
        this.lng = lng;
    }

    public Object getAction() {
        return action;
    }

    public void setAction(Object action) {
        this.action = action;
    }

    public List<RoutePlannerDetail> getRoutePlannerDetail() {
        return routePlannerDetails;
    }

    public void setLiRoutePlannerDetail(List<RoutePlannerDetail> routePlannerDetails) {
        this.routePlannerDetails = routePlannerDetails;
    }

}
