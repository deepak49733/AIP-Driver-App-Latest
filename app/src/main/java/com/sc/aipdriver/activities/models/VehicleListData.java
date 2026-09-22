package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 5/3/18.
 */

public class VehicleListData  {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("VehicleName")
    @Expose
    private String vehicleName;
    @SerializedName("VinNumber")
    @Expose
    private String vinNumber;
    @SerializedName("UserImage")
    @Expose
    private String userImage;
    @SerializedName("Status")
    @Expose
    private Boolean status;
    @SerializedName("MakeAndModel")
    @Expose
    private String makeAndModel;
    @SerializedName("PurchaseDate")
    @Expose
    private String purchaseDate;
    @SerializedName("SaleDate")
    @Expose
    private String saleDate;
    @SerializedName("PurchasePrice")
    @Expose
    private String purchasePrice;
    @SerializedName("SalePrice")
    @Expose
    private Object salePrice;
    @SerializedName("LicenseNumber")
    @Expose
    private String licenseNumber;
    @SerializedName("StartingMiles")
    @Expose
    private String startingMiles;
    @SerializedName("CurrentMileage")
    @Expose
    private String currentMileage;
    @SerializedName("VehicleImage")
    @Expose
    private String vehicleImage;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("IsAssigned")
    @Expose
    private Object isAssigned;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMakeAndModel() {
        return makeAndModel;
    }

    public void setMakeAndModel(String makeAndModel) {
        this.makeAndModel = makeAndModel;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Object getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Object salePrice) {
        this.salePrice = salePrice;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getStartingMiles() {
        return startingMiles;
    }

    public void setStartingMiles(String startingMiles) {
        this.startingMiles = startingMiles;
    }

    public String getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(String currentMileage) {
        this.currentMileage = currentMileage;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getIsAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(Object isAssigned) {
        this.isAssigned = isAssigned;
    }
}
