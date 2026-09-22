package com.sc.aipdriver.activities.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dev on 12/3/18.
 */


@Entity(tableName = "ride_actions")
public class StartDataSend {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName("DriverId")
    @Expose
    private String driverId;
    @SerializedName("orderDate")
    @Expose
    private String orderDate;

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    @SerializedName("VehicleId")
    @Expose
    private String vehicleId;

    @SerializedName("RouteId")
    @Expose
    private String routId;

    @SerializedName("UID")
    @Expose
    private String uID;

    @SerializedName("StartOdometer")
    @Expose
    private String startOdometer;

    @SerializedName("lng")
    @Expose
    private String lng;

    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("Action")
    @Expose
    private String action;

    @SerializedName("Address")
    @Expose
    private String address;

    @SerializedName("State")
    @Expose
    private String state;

    @SerializedName("City")
    @Expose
    private String city;

    @SerializedName("Country")
    @Expose
    private String country;

    @SerializedName("RideId")
    @Expose
    private Integer rideId;

    @SerializedName("offlineRideId")
    @Expose
    private String offlineRideId;

    public String getOfflineRideId() {
        return offlineRideId;
    }

    public void setOfflineRideId(String offlineRideId) {
        this.offlineRideId = offlineRideId;
    }

    @SerializedName("ParentId")
    @Expose
    private Integer ParentId;

    public String    getStartOilPercent() {
        return StartOilPercent;
    }

    public void setStartOilPercent(String startOilPercent) {
        StartOilPercent = startOilPercent;
    }

    @SerializedName("StartOilPercent")
    @Expose
    private String StartOilPercent;

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public Integer getParentId() {
        return ParentId;
    }

    public void setParentId(Integer parentId) {
        ParentId = parentId;
    }

    @TypeConverters(RouteDetailConverter.class)
    @SerializedName("liRoutePlannerDetail")
    @Expose
    private List<LiRoutePlannerDetail> liRoutePlannerDetail = null;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRoutId() {
        return routId;
    }

    public void setRoutId(String routId) {
        this.routId = routId;
    }

    public String getUID() {
        return uID;
    }

    public void setUID(String uID) {
        this.uID = uID;
    }

    public String getStartOdometer() {
        return startOdometer;
    }

    public void setStartOdometer(String startOdometer) {
        this.startOdometer = startOdometer;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public List<LiRoutePlannerDetail> getLiRoutePlannerDetail() {
        return liRoutePlannerDetail;
    }

    public void setLiRoutePlannerDetail(List<LiRoutePlannerDetail> liRoutePlannerDetail) {
        this.liRoutePlannerDetail = liRoutePlannerDetail;
    }

}
