package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReceiptResponse {

    @SerializedName("ID")
    @Expose
    private String ID;
    @SerializedName("Recipt_Img")
    @Expose
    private String Recipt_Img;
    @SerializedName("ParentId")
    @Expose
    private String ParentId;
    @SerializedName("FuelOdometer")
    @Expose
    private String FuelOdometer;

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    @SerializedName("RID")
    @Expose
    private String RID;
    @SerializedName("Gallon")
    @Expose
    private String Gallon;
    @SerializedName("PricePgallon")
    @Expose
    private String PricePgallon;
    @SerializedName("FuelCost")
    @Expose
    private String FuelCost;
    @SerializedName("WashCost")
    @Expose
    private String WashCost;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRecipt_Img() {
        return Recipt_Img;
    }

    public void setRecipt_Img(String recipt_Img) {
        Recipt_Img = recipt_Img;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public String getFuelOdometer() {
        return FuelOdometer;
    }

    public void setFuelOdometer(String fuelOdometer) {
        FuelOdometer = fuelOdometer;
    }

    public String getGallon() {
        return Gallon;
    }

    public void setGallon(String gallon) {
        Gallon = gallon;
    }

    public String getPricePgallon() {
        return PricePgallon;
    }

    public void setPricePgallon(String pricePgallon) {
        PricePgallon = pricePgallon;
    }

    public String getFuelCost() {
        return FuelCost;
    }

    public void setFuelCost(String fuelCost) {
        FuelCost = fuelCost;
    }

    public String getWashCost() {
        return WashCost;
    }

    public void setWashCost(String washCost) {
        WashCost = washCost;
    }
}
