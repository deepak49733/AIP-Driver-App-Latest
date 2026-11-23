package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.SerializedName;

public class RouteDriverScheduleItem {

    @SerializedName(value = "Id", alternate = {"id"})
    private int id;

    @SerializedName(value = "ScheduleDate", alternate = {"Scheduledate", "scheduleDate", "date"})
    private String scheduleDate;

    @SerializedName(value = "RouteId", alternate = {"Routeid", "routeId"})
    private int routeId;

    @SerializedName(value = "RouteName", alternate = {"Routename", "routeName", "Route", "route"})
    private String routeName;

    @SerializedName(value = "DriverId", alternate = {"Driverid", "driverId"})
    private int driverId;

    @SerializedName(value = "DriverName", alternate = {"Drivername", "driverName", "Driver", "driver"})
    private String driverName;

    @SerializedName(value = "VehicleId", alternate = {"Vehicleid", "vehicleId"})
    private int vehicleId;

    @SerializedName(value = "VehicleName", alternate = {"Vehiclename", "vehicleName", "Vehicle", "vehicle"})
    private String vehicleName;

    @SerializedName(value = "VehicleImage", alternate = {"Vehicleimage", "vehicleImage", "UserImage", "userImage", "Image", "image"})
    private String vehicleImage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }
}
