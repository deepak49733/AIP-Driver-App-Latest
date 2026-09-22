package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TempBags {
    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("FarmID")
    @Expose
    private Integer farmID;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @SerializedName("ParentId")
    @Expose
    private Integer parentId;
    @SerializedName("RouteName")
    @Expose
    private String routeName;
    @SerializedName("Temp")
    @Expose
    private String temp;
    @SerializedName("Beg")
    @Expose
    private Integer beg;
    @SerializedName("RouteID")
    @Expose
    private Integer routeId;
    @SerializedName("CustomerSeemanCoolarTemp")
    @Expose
    private String refTem;

    public String getRefTem() {
        return refTem;
    }

    public void setRefTem(String refTem) {
        this.refTem = refTem;
    }

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public Integer getFarmID() {
        return farmID;
    }

    public void setFarmID(Integer farmID) {
        this.farmID = farmID;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getTemp() {
        return temp;
    }

    public Integer getiD() {
        return iD;
    }

    public void setiD(Integer iD) {
        this.iD = iD;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Integer getBeg() {
        return beg;
    }

    public void setBeg(Integer beg) {
        this.beg = beg;
    }
}
