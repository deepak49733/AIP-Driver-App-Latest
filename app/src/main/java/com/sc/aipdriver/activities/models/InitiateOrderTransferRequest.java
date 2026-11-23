package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InitiateOrderTransferRequest {

    @SerializedName("RideIds")
    @Expose
    private List<Integer> rideIds;

    @SerializedName("FromDriverId")
    @Expose
    private Integer fromDriverId;

    @SerializedName("ToDriverId")
    @Expose
    private Integer toDriverId;

    @SerializedName("OrgId")
    @Expose
    private Integer orgId;

    @SerializedName("StudId")
    @Expose
    private Integer studId;

    public List<Integer> getRideIds() {
        return rideIds;
    }

    public void setRideIds(List<Integer> rideIds) {
        this.rideIds = rideIds;
    }

    public Integer getFromDriverId() {
        return fromDriverId;
    }

    public void setFromDriverId(Integer fromDriverId) {
        this.fromDriverId = fromDriverId;
    }

    public Integer getToDriverId() {
        return toDriverId;
    }

    public void setToDriverId(Integer toDriverId) {
        this.toDriverId = toDriverId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getStudId() {
        return studId;
    }

    public void setStudId(Integer studId) {
        this.studId = studId;
    }
}
