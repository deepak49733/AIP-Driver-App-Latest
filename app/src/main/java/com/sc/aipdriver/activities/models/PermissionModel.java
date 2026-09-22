package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PermissionModel {

    @SerializedName("Version")
    @Expose
    private String version;
    @SerializedName("Statuscode")
    @Expose
    private Integer statuscode;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Data")
    @Expose
    private PermissionData data;



    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(Integer statuscode) {
        this.statuscode = statuscode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PermissionData getData() {
        return data;
    }

    public void setData(PermissionData data) {
        this.data = data;
    }

   /*   public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }*/
}
