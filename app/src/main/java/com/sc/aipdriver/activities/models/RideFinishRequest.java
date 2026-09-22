package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 22/3/18.
 */

public class RideFinishRequest {
    @SerializedName("driverId")
    @Expose
    private String driverId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    @SerializedName("Token")
    @Expose
    private String token;

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    @SerializedName("EndDateTime")
    @Expose
    private String EndDateTime;
    @SerializedName("UID")
    @Expose
    private String UID;
    @SerializedName("rideId")
    @Expose
    private String rideId;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("RouteId")
    @Expose
    private String routeId;
    @SerializedName("endOdometer")
    @Expose
    private String endOdometer;

    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("CommentsDelivered")
    @Expose
    private String commentsDelivered;

    public String getCommentsDelivered() {
        return commentsDelivered;
    }

    public void setCommentsDelivered(String commentsDelivered) {
        this.commentsDelivered = commentsDelivered;
    }

    @SerializedName("FIRMID")
    @Expose
    private String firmId;

    @SerializedName("NumberOfBagsDelivered")
    @Expose
    private String bagsDelivered;
    @SerializedName("TemperatureOfSemenDelivered")
    @Expose
    private String temperature;

    @SerializedName("Action")
    @Expose
    private String action;
    @SerializedName("TotalMiles")
    @Expose
    private String totalMiles;

    @SerializedName("State")
    @Expose
    private String state;

    @SerializedName("City")
    @Expose
    private String city;

    @SerializedName("Country")
    @Expose
    private String country;
    @SerializedName("EndOilPercent")
    @Expose
    private String EndOilPercent;
    @SerializedName("CustomerSeemanCoolarTemp")
    @Expose
    private String CustomerSeemanCoolarTemp;

    public String getEndOilPercent() {
        return EndOilPercent;
    }

    public void setEndOilPercent(String endOilPercent) {
        EndOilPercent = endOilPercent;
    }

    public String getCustomerSeemanCoolarTemp() {
        return CustomerSeemanCoolarTemp;
    }

    public void setCustomerSeemanCoolarTemp(String customerSeemanCoolarTemp) {
        CustomerSeemanCoolarTemp = customerSeemanCoolarTemp;
    }

    public String getAction() {
        return action;
    }


    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public String getBagsDelivered() {
        return bagsDelivered;
    }

    public void setBagsDelivered(String bagsDelivered) {
        this.bagsDelivered = bagsDelivered;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setAction(String action) {
        this.action = action;
    }



    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }


    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getEndOdometer() {
        return endOdometer;
    }

    public void setEndOdometer(String endOdometer) {
        this.endOdometer = endOdometer;
    }

    public String getTotalMiles() {
        return totalMiles;
    }

    public void setTotalMiles(String totalMiles) {
        this.totalMiles = totalMiles;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
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
}
