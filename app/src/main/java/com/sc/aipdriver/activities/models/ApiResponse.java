package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("Version")
    private String version;

    @SerializedName("Statuscode")
    private int statusCode;

    @SerializedName("Message")
    private String message;

    @SerializedName("Data")
    private FarmInfo data;

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

    public FarmInfo getData() {
        return data;
    }

    public void setData(FarmInfo data) {
        this.data = data;
    }
// Getters and Setters
}
