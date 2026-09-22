package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReceiptResponseMain {

    @SerializedName("Version")
    @Expose
    private String Version;
    @SerializedName("Statuscode")
    @Expose
    private Integer Statuscode;
    @SerializedName("Message")
    @Expose
    private String Message;
    @SerializedName("Data")
    @Expose
    private ArrayList<ReceiptResponse> alReciptResponse;

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public Integer getStatuscode() {
        return Statuscode;
    }

    public void setStatuscode(Integer statuscode) {
        Statuscode = statuscode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public ArrayList<ReceiptResponse> getAlReciptResponse() {
        return alReciptResponse;
    }

    public void setAlReciptResponse(ArrayList<ReceiptResponse> alReciptResponse) {
        this.alReciptResponse = alReciptResponse;
    }
}
