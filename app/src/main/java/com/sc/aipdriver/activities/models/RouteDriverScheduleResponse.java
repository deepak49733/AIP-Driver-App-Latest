package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.SerializedName;

public class RouteDriverScheduleResponse {

    @SerializedName("Version")
    private String version;

    @SerializedName(value = "Statuscode", alternate = {"StatusCode", "statuscode"})
    private int statusCode;

    @SerializedName("Message")
    private String message;

    @SerializedName("Data")
    private RouteDriverScheduleData data;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

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

    public RouteDriverScheduleData getData() {
        return data;
    }

    public void setData(RouteDriverScheduleData data) {
        this.data = data;
    }
}
