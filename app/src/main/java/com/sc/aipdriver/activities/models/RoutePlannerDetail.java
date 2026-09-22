package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by dev on 13/3/18.
 */

public class RoutePlannerDetail {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("FK_DriverRide_Main_Id")
    @Expose
    private String fKDriverRideMainId;

    @SerializedName("FIRMID")
    @Expose
    private String fIRMID;

    @SerializedName("NumberOfBagsLoaded")
    @Expose
    private String numberOfBagsLoaded;

    @SerializedName("NumberOfBagsDelivered")
    @Expose
    private Object numberOfBagsDelivered;

    @SerializedName("CommentsDelivered")
    @Expose
    private Object commentsDelivered;

    @SerializedName("TemperatureOfSemenLoaded")
    @Expose
    private String temperatureOfSemenLoaded;

    @SerializedName("TemperatureOfSemenDelivered")
    @Expose
    private Object temperatureOfSemenDelivered;

    @SerializedName("DeleveryTime")
    @Expose
    private String deleveryTime;

    @SerializedName("DeleveryDateTime")
    @Expose
    private String deleveryDateTime;

    @SerializedName("FarmName")
    @Expose
    private String farmName;

    @SerializedName("SemenDropLocationdesc")
    @Expose
    private Object semenDropLocationdesc;

    @SerializedName("DirectionsSemenDropSite")
    @Expose
    private String directionsSemenDropSite;

    @SerializedName("FarmLat")
    @Expose
    private double farmLat;

    @SerializedName("FarmLng")
    @Expose
    private double farmLng;

    @SerializedName("Priority")
    @Expose
    private int priority;


    @SerializedName("emergencyContacts")
    @Expose
    private ArrayList<ContactModel> alContacts;


    public String getfKDriverRideMainId() {
        return fKDriverRideMainId;
    }

    public void setfKDriverRideMainId(String fKDriverRideMainId) {
        this.fKDriverRideMainId = fKDriverRideMainId;
    }

    public String getfIRMID() {
        return fIRMID;
    }

    public void setfIRMID(String fIRMID) {
        this.fIRMID = fIRMID;
    }

    public ArrayList<ContactModel> getAlContacts() {
        return alContacts;
    }

    public void setAlContacts(ArrayList<ContactModel> alContacts) {
        this.alContacts = alContacts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFKDriverRideMainId() {
        return fKDriverRideMainId;
    }

    public void setFKDriverRideMainId(String fKDriverRideMainId) {
        this.fKDriverRideMainId = fKDriverRideMainId;
    }

    public String getFIRMID() {
        return fIRMID;
    }

    public void setFIRMID(String fIRMID) {
        this.fIRMID = fIRMID;
    }

    public String getNumberOfBagsLoaded() {
        return numberOfBagsLoaded;
    }

    public void setNumberOfBagsLoaded(String numberOfBagsLoaded) {
        this.numberOfBagsLoaded = numberOfBagsLoaded;
    }

    public Object getNumberOfBagsDelivered() {
        return numberOfBagsDelivered;
    }

    public void setNumberOfBagsDelivered(Object numberOfBagsDelivered) {
        this.numberOfBagsDelivered = numberOfBagsDelivered;
    }

    public Object getCommentsDelivered() {
        return commentsDelivered;
    }

    public void setCommentsDelivered(Object commentsDelivered) {
        this.commentsDelivered = commentsDelivered;
    }

    public String getTemperatureOfSemenLoaded() {
        return temperatureOfSemenLoaded;
    }

    public void setTemperatureOfSemenLoaded(String temperatureOfSemenLoaded) {
        this.temperatureOfSemenLoaded = temperatureOfSemenLoaded;
    }

    public Object getTemperatureOfSemenDelivered() {
        return temperatureOfSemenDelivered;
    }

    public void setTemperatureOfSemenDelivered(Object temperatureOfSemenDelivered) {
        this.temperatureOfSemenDelivered = temperatureOfSemenDelivered;
    }

    public String getDeleveryTime() {
        return deleveryTime;
    }

    public void setDeleveryTime(String deleveryTime) {
        this.deleveryTime = deleveryTime;
    }

    public String getDeleveryDateTime() {
        return deleveryDateTime;
    }

    public void setDeleveryDateTime(String deleveryDateTime) {
        this.deleveryDateTime = deleveryDateTime;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public Object getSemenDropLocationdesc() {
        return semenDropLocationdesc;
    }

    public void setSemenDropLocationdesc(Object semenDropLocationdesc) {
        this.semenDropLocationdesc = semenDropLocationdesc;
    }

    public String getDirectionsSemenDropSite() {
        return directionsSemenDropSite;
    }

    public void setDirectionsSemenDropSite(String directionsSemenDropSite) {
        this.directionsSemenDropSite = directionsSemenDropSite;
    }

    public double getFarmLat() {
        return farmLat;
    }

    public void setFarmLat(double farmLat) {
        this.farmLat = farmLat;
    }

    public double getFarmLng() {
        return farmLng;
    }

    public void setFarmLng(double farmLng) {
        this.farmLng = farmLng;
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
