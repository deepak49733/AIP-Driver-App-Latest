package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactModel {

    @SerializedName("ContactName")
    @Expose
    private String ContactName;

    @SerializedName("CellPhone")
    @Expose
    private String CellPhone;

    public ContactModel(String contactName, String cellPhone) {
        ContactName = contactName;
        CellPhone = cellPhone;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getCellPhone() {
        return CellPhone;
    }

    public void setCellPhone(String cellPhone) {
        CellPhone = cellPhone;
    }
}
