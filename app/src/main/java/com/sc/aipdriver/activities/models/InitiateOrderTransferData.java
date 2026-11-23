package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InitiateOrderTransferData {

    @SerializedName("Status")
    @Expose
    private Boolean status;

    @SerializedName("Message")
    @Expose
    private String message;

    @SerializedName("TransferId")
    @Expose
    private Integer transferId;

    @SerializedName("FromDriverId")
    @Expose
    private Integer fromDriverId;

    @SerializedName("ToDriverId")
    @Expose
    private Integer toDriverId;

    @SerializedName("RideIds")
    @Expose
    private List<Integer> rideIds;

    @SerializedName("RouteId")
    @Expose
    private Integer routeId;

    @SerializedName("OrderDate")
    @Expose
    private String orderDate;

    @SerializedName("TotalRides")
    @Expose
    private Integer totalRides;

    @SerializedName("TotalOrders")
    @Expose
    private Integer totalOrders;

    @SerializedName("TransferStatus")
    @Expose
    private String transferStatus;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
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

    public List<Integer> getRideIds() {
        return rideIds;
    }

    public void setRideIds(List<Integer> rideIds) {
        this.rideIds = rideIds;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(Integer totalRides) {
        this.totalRides = totalRides;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }
}
