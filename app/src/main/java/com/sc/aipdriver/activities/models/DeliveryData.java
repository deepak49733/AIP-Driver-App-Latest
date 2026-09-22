package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 14/3/18.
 */

public class DeliveryData {

    @SerializedName("id")
    @Expose
    private String  id;
    @SerializedName("driverId")
    @Expose
    private String  driverId;
    @SerializedName("vehicleId")
    @Expose
    private String  vehicleId;
    @SerializedName("routeId")
    @Expose
    private String  routeId;
    @SerializedName("UID")
    @Expose
    private String  uID;
    @SerializedName("NumberOfBagsDelivered")
    @Expose
    private String numberOfBagsDelivered;

    @SerializedName("TemperatureOfSemenDelivered")
    @Expose
    private String temperatureOfSemenDelivered;

    @SerializedName("CommentsDelivered")
    @Expose
    private String commentsDelivered;

    @SerializedName("FarmLng")
    @Expose
    private String farmLng;

    @SerializedName("FarmLat")
    @Expose
    private String farmLat;
    @SerializedName("Action")
    @Expose
    private String action;

    @SerializedName("FK_DriverRide_Main_Id")
    @Expose
    private String fkrideid;

    @SerializedName("CustomerSeemanCoolarTemp")
    @Expose
   private String CustomerSeemanCoolarTemp;

    @SerializedName("Address")
    @Expose
    private String Address;

    @SerializedName("State")
    @Expose
    private String State;

    @SerializedName("City")
    @Expose
    private String City;

    @SerializedName("Country")
    @Expose
    private String Country;




    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCustomerSeemanCoolarTemp() {
        return CustomerSeemanCoolarTemp;
    }

    public void setCustomerSeemanCoolarTemp(String customerSeemanCoolarTemp) {
        CustomerSeemanCoolarTemp = customerSeemanCoolarTemp;
    }

    public String getFkrideid() {
        return fkrideid;
    }

    public void setFkrideid(String fkrideid) {
        this.fkrideid = fkrideid;
    }

    public String  getId(String farmid) {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public String  getDriverId() {
        return driverId;
    }

    public void setDriverId(String  driverId) {
        this.driverId = driverId;
    }

    public String  getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String  vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String  getRouteId() {
        return routeId;
    }

    public void setRouteId(String  routeId) {
        this.routeId = routeId;
    }

    public String  getUID() {
        return uID;
    }

    public void setUID(String  uID) {
        this.uID = uID;
    }

    public String getNumberOfBagsDelivered() {
        return numberOfBagsDelivered;
    }

    public void setNumberOfBagsDelivered(String numberOfBagsDelivered) {
        this.numberOfBagsDelivered = numberOfBagsDelivered;
    }

    public String getTemperatureOfSemenDelivered() {
        return temperatureOfSemenDelivered;
    }

    public void setTemperatureOfSemenDelivered(String temperatureOfSemenDelivered) {
        this.temperatureOfSemenDelivered = temperatureOfSemenDelivered;
    }

    public String getCommentsDelivered() {
        return commentsDelivered;
    }

    public void setCommentsDelivered(String commentsDelivered) {
        this.commentsDelivered = commentsDelivered;
    }

    public String getFarmLng() {
        return farmLng;
    }

    public void setFarmLng(String farmLng) {
        this.farmLng = farmLng;
    }

    public String getFarmLat() {
        return farmLat;
    }

    public void setFarmLat(String farmLat) {
        this.farmLat = farmLat;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
