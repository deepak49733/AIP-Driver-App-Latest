package com.sc.aipdriver.activities.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(
        tableName = "priority_farms",
        primaryKeys = {"id","orderDate"}
)
public class PriorityFarmData {


    @SerializedName("FID")
    @Expose
    private int id;

    @SerializedName("Remove")
    @Expose
    private int Remove;

    @SerializedName("FarmName")
    @Expose
    private String farmName;

    @NonNull
    @SerializedName("OrderDate")
    @Expose
    private String orderDate="";

    @SerializedName("RideId")
    @Expose
    private String RideId;

    @SerializedName("Priority")
    @Expose
    private Integer priority;

    public Integer getRouteID() {

        return routeID;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    @SerializedName("RouteID")
    @Expose
    private Integer routeID;

    @SerializedName("IsLoaded")
    @Expose
    private Integer IsLoaded;

    @SerializedName("Beg")
    @Expose
    private Integer begs;

    @SerializedName("Temperature")
    @Expose
    private double tmperature;

    @SerializedName("IsCompleted")
    @Expose
    private Integer isCompleted;
    @SerializedName("isSynced")
    @Expose
    private Integer isSynced;

    public Integer getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(Integer isSynced) {
        this.isSynced = isSynced;
    }

    @SerializedName("isEmailSent")
    @Expose
    private Integer isEmailSent;

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIsEmailSent() {
        return isEmailSent;
    }

    public void setIsEmailSent(Integer isEmailSent) {
        this.isEmailSent = isEmailSent;
    }

    @SerializedName("isPhotoUploaded")
    @Expose
    private Integer isPhotoUploaded = 0;


    // getters & setters

    public int getId() {
        return id;
    }



    public int getRemove() {
        return Remove;
    }

    public void setRemove(int remove) {
        Remove = remove;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getRideId() {
        return RideId;
    }

    public void setRideId(String rideId) {
        RideId = rideId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getIsLoaded() {
        return IsLoaded;
    }

    public void setIsLoaded(Integer isLoaded) {
        IsLoaded = isLoaded;
    }

    public Integer getBegs() {
        return begs;
    }

    public void setBegs(Integer begs) {
        this.begs = begs;
    }

    public double getTmperature() {
        return tmperature;
    }

    public void setTmperature(double tmperature) {
        this.tmperature = tmperature;
    }

    public Integer getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Integer isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getIsPhotoUploaded() {
        return isPhotoUploaded;
    }

    public void setIsPhotoUploaded(Integer isPhotoUploaded) {
        this.isPhotoUploaded = isPhotoUploaded;
    }
}