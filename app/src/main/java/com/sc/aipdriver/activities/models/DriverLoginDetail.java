package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dev on 5/3/18.
 */

class DriverLoginDetail {

    @SerializedName("liDriverModel")
    @Expose
    private List<Object> liDriverModel = null;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("DirverId")
    @Expose
    private Object dirverId;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("CellPhone")
    @Expose
    private Object cellPhone;
    @SerializedName("Fax")
    @Expose
    private Object fax;
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("Password")
    @Expose
    private Object password;
    @SerializedName("UserImage")
    @Expose
    private String userImage;
    @SerializedName("Rights")
    @Expose
    private Object rights;
    @SerializedName("CreatedDate")
    @Expose
    private Object createdDate;
    @SerializedName("Status")
    @Expose
    private Boolean status;
    @SerializedName("dirId")
    @Expose
    private Object dirId;

    public List<Object> getLiDriverModel() {
        return liDriverModel;
    }

    public void setLiDriverModel(List<Object> liDriverModel) {
        this.liDriverModel = liDriverModel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getDirverId() {
        return dirverId;
    }

    public void setDirverId(Object dirverId) {
        this.dirverId = dirverId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(Object cellPhone) {
        this.cellPhone = cellPhone;
    }

    public Object getFax() {
        return fax;
    }

    public void setFax(Object fax) {
        this.fax = fax;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Object getRights() {
        return rights;
    }

    public void setRights(Object rights) {
        this.rights = rights;
    }

    public Object getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Object createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Object getDirId() {
        return dirId;
    }

    public void setDirId(Object dirId) {
        this.dirId = dirId;
    }
}

