package com.sc.aipdriver.activities.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(
        tableName = "improved_priority_farms",
        primaryKeys = {"id","orderDate"}
)
public class ImprovedPriorityFarmData {

    @SerializedName("FirmId")
    @Expose
    private int id;
    @SerializedName("isActiveRide")
    @Expose
    private Integer isActiveRide = 0;

    public Integer getIsActiveRide() {
        return isActiveRide;
    }

    public void setIsActiveRide(Integer isActiveRide) {
        this.isActiveRide = isActiveRide;
    }
    @SerializedName("Remove")
    @Expose
    private int Remove;

    @SerializedName("FarmName")
    @Expose
    private String farmName;

    @NonNull
    @SerializedName("orderdate")
    @Expose
    private String orderDate="";

    @SerializedName("RideId")
    @Expose
    private String RideId="0";
    @SerializedName("FarmEmergencyNo")
    @Expose
    private String FarmEmergencyNo;
    @SerializedName("OfficePhone")
    @Expose
    private String OfficePhone;
    @SerializedName("Longitude")
    @Expose
    private String Longitude;
    @SerializedName("Latitude")
    @Expose
    private String Latitude;
    @SerializedName("SemenDropLocationImage")
    @Expose
    private String SemenDropLocationImage;
    @SerializedName("Callaheadinstructionstext")
    @Expose
    private String Callaheadinstructionstext;
    @SerializedName("DelAddress")
    @Expose
    private String DelAddress;

    @SerializedName("UserImage")
    @Expose
    private String userImage1;

    public String getUserImage1() {
        return userImage1;
    }

    public void setUserImage1(String userImage1) {
        this.userImage1 = userImage1;
    }

    public String getFarmEmergencyNo() {
        return FarmEmergencyNo;
    }

    public void setFarmEmergencyNo(String farmEmergencyNo) {
        FarmEmergencyNo = farmEmergencyNo;
    }

    public String getOfficePhone() {
        return OfficePhone;
    }

    public void setOfficePhone(String officePhone) {
        OfficePhone = officePhone;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getSemenDropLocationImage() {
        return SemenDropLocationImage;
    }

    public void setSemenDropLocationImage(String semenDropLocationImage) {
        SemenDropLocationImage = semenDropLocationImage;
    }

    public String getCallaheadinstructionstext() {
        return Callaheadinstructionstext;
    }

    public void setCallaheadinstructionstext(String callaheadinstructionstext) {
        Callaheadinstructionstext = callaheadinstructionstext;
    }

    public String getDelAddress() {
        return DelAddress;
    }

    public void setDelAddress(String delAddress) {
        DelAddress = delAddress;
    }

    public String getFarmEmergencyNo_2nd() {
        return FarmEmergencyNo_2nd;
    }

    public void setFarmEmergencyNo_2nd(String farmEmergencyNo_2nd) {
        FarmEmergencyNo_2nd = farmEmergencyNo_2nd;
    }



    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public Integer getRouteId() {
        return RouteId;
    }

    public void setRouteId(Integer routeId) {
        RouteId = routeId;
    }

    @SerializedName("FarmEmergencyNo_2nd")
    @Expose
    private String FarmEmergencyNo_2nd;
    @SerializedName("IsVisited")
    @Expose
    private boolean isVisited;

    @SerializedName("Priority")
    @Expose
    private Integer priority;

    @SerializedName("IsLoaded")
    @Expose
    private Integer IsLoaded;

    public Integer getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(Integer isSynced) {
        this.isSynced = isSynced;
    }

    public boolean isVisited() {
        return isVisited;
    }

    @SerializedName("isSynced")
    @Expose
    private Integer isSynced;

    @SerializedName("Beg")
    @Expose
    private Integer begs;

    @SerializedName("Temperature")
    @Expose
    private double tmperature;

    @SerializedName("IsCompleted")
    @Expose
    private Integer isCompleted;
    @SerializedName("isEmailSent")
    @Expose
    private Integer isEmailSent;
    @SerializedName("RouteId")
    @Expose
    private Integer RouteId;

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