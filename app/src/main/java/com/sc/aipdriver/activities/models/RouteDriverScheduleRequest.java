package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.SerializedName;

public class RouteDriverScheduleRequest {

    @SerializedName("OrgId")
    private int orgId;

    @SerializedName("Year")
    private int year;

    @SerializedName("Month")
    private int month;

    public RouteDriverScheduleRequest(int orgId, int year, int month) {
        this.orgId = orgId;
        this.year = year;
        this.month = month;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
