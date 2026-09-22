package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.SerializedName;

public class MonthlyDoses {
    @SerializedName("DosesDisplayName")
    private String dosesDisplayName;

    @SerializedName("DosesId")
    private int dosesId;

    public String getDosesDisplayName() {
        return dosesDisplayName;
    }

    public void setDosesDisplayName(String dosesDisplayName) {
        this.dosesDisplayName = dosesDisplayName;
    }

    public int getDosesId() {
        return dosesId;
    }

    public void setDosesId(int dosesId) {
        this.dosesId = dosesId;
    }

    public String getDosesQty() {
        return dosesQty;
    }

    public void setDosesQty(String dosesQty) {
        this.dosesQty = dosesQty;
    }

    public double getDosesPrice() {
        return dosesPrice;
    }

    public void setDosesPrice(double dosesPrice) {
        this.dosesPrice = dosesPrice;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @SerializedName("DosesQty")
    private String dosesQty;

    @SerializedName("DosesPrice")
    private double dosesPrice;

    @SerializedName("IsChecked")
    private boolean isChecked;

    // Add other fields as needed

    // Getters and Setters
}
