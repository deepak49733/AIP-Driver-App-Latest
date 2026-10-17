package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteDriverScheduleData {

    @SerializedName(value = "Statuscode", alternate = {"StatusCode", "statuscode"})
    private int statusCode;

    @SerializedName("Message")
    private String message;

    @SerializedName("LastUpdatedDate")
    private String lastUpdatedDate;

    @SerializedName("Data")
    private List<RouteDriverScheduleItem> data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public List<RouteDriverScheduleItem> getData() {
        return data;
    }

    public void setData(List<RouteDriverScheduleItem> data) {
        this.data = data;
    }
}
