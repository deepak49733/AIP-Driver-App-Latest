package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 5/3/18.
 */

public class LogInData {


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("OrgId")
    @Expose
    private String orgId;

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    @SerializedName("Token")
    @Expose
    private String Token;

    public Boolean getAnotherDeviceLogin() {
        return IsAnotherDeviceLogin;
    }

    public void setAnotherDeviceLogin(Boolean anotherDeviceLogin) {
        IsAnotherDeviceLogin = anotherDeviceLogin;
    }

    @SerializedName("IsAnotherDeviceLogin")
    @Expose
    private Boolean IsAnotherDeviceLogin;

    @SerializedName(value = "Mode", alternate = {"mode", "ModeKey", "modeKey", "SyncMode", "sync_mode", "appMode"})
    @Expose
    private String modeKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getModeKey() {
        return modeKey;
    }

    public void setModeKey(String modeKey) {
        this.modeKey = modeKey;
    }
}
