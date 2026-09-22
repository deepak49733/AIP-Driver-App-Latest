package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 12/3/18.
 */

public class LiRoutePlannerDetail {
    @SerializedName("FIRMID")
    @Expose
    private String fIRMID;
    @SerializedName("NumberOfBagsLoaded")
    @Expose
    private String numberOfBagsLoaded;
    @SerializedName("TemperatureOfSemenLoaded")
    @Expose
    private String temperatureOfSemenLoaded;

    public String getCustomerSeemanCoolarTemp() {
        return CustomerSeemanCoolarTemp;
    }

    public void setCustomerSeemanCoolarTemp(String customerSeemanCoolarTemp) {
        CustomerSeemanCoolarTemp = customerSeemanCoolarTemp;
    }

    @SerializedName("CustomerSeemanCoolarTemp")
    @Expose
    private String CustomerSeemanCoolarTemp;

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

    public String getTemperatureOfSemenLoaded() {
        return temperatureOfSemenLoaded;
    }

    public void setTemperatureOfSemenLoaded(String temperatureOfSemenLoaded) {
        this.temperatureOfSemenLoaded = temperatureOfSemenLoaded;
    }

}
