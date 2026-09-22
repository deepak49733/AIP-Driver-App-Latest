package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.SerializedName;

public class CourierRoute {
    @SerializedName("Id")
    private int id;

    @SerializedName("CourierRouteName")
    private String courierRouteName;

    @SerializedName("DayOfWk")
    private String dayOfWk;

    @SerializedName("CourierCost")
    private String courierCost;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourierRouteName() {
        return courierRouteName;
    }

    public void setCourierRouteName(String courierRouteName) {
        this.courierRouteName = courierRouteName;
    }

    public String getDayOfWk() {
        return dayOfWk;
    }

    public void setDayOfWk(String dayOfWk) {
        this.dayOfWk = dayOfWk;
    }

    public String getCourierCost() {
        return courierCost;
    }

    public void setCourierCost(String courierCost) {
        this.courierCost = courierCost;
    }
}
