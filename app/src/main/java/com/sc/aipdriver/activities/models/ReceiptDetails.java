package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 5/3/18.
 */

public class ReceiptDetails {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("ParentId")
    @Expose
    private String ParentId;
    @SerializedName("FuelOdometer")
    @Expose
    private String FuelOdometer;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @SerializedName("img")
    @Expose
    private String img="";

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

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }
}
