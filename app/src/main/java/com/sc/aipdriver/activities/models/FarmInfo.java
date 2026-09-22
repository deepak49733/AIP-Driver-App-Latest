package com.sc.aipdriver.activities.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dev on 9/3/18.
 */

public class FarmInfo {

    @SerializedName("FirmName")
    private String firmName;

    @SerializedName("FirmCompanyName")
    private String firmCompanyName;

    @SerializedName("EmailId")
    private String emailId;

    @SerializedName("FName")
    private String fName;

    @SerializedName("LName")
    private String lName;

    @SerializedName("UserId")
    private String userId;

    @SerializedName("OfficePhone")
    private String officePhone;

    @SerializedName("CellPhone")
    private String cellPhone;

    @SerializedName("UserName")
    private String userName;

    @SerializedName("Password")
    private String password;

    @SerializedName("SalesRepName")
    private String salesRepName;

    @SerializedName("SalesRepCellPhone")
    private String salesRepCellPhone;

    @SerializedName("VeterinarianName")
    private String veterinarianName;

    @SerializedName("VeterinarianCellPhone")
    private String veterinarianCellPhone;

    @SerializedName("VeterinarianEmail")
    private String veterinarianEmail;

    @SerializedName("ClinicName")
    private String clinicName;

    @SerializedName("ClinicPhone")
    private String clinicPhone;

    @SerializedName("StartDAte")
    private String startDate;

    @SerializedName("EndDate")
    private String endDate;

    @SerializedName("FarmEmergencyNo")
    private String farmEmergencyNo;

    public String getSemenDropLocation() {
        return semenDropLocation;
    }

    public void setSemenDropLocation(String semenDropLocation) {
        this.semenDropLocation = semenDropLocation;
    }

    @SerializedName("SemenDropLocation")
    private String semenDropLocation;

    @SerializedName("FarmEmergencyNo_2nd")
    private String farmEmergencyNo2nd;

    @SerializedName("CallAheadInstructions")
    private String callAheadInstructions;

    @SerializedName("DirectionstoSemenDropSite")
    private String directionToSemenDropSite;

    @SerializedName("Latitude")
    private String latitude;

    @SerializedName("Longitude")
    private String longitude;

    @SerializedName("Address")
    private String address;

    @SerializedName("City")
    private String city;

    @SerializedName("State")
    private String state;

    @SerializedName("ZipCode")
    private String zipCode;

    @SerializedName("DelAddress")
    private String delAddress;

    @SerializedName("DelCity")
    private String delCity;

    @SerializedName("DelState")
    private String delState;

    @SerializedName("DelZipCode")
    private String delZipCode;

    @SerializedName("DelCourierRoute")
    private String delCourierRoute;

    @SerializedName("UserImage1")
    private String userImage1;

    @SerializedName("SemenOrderFormFile1")
    private String semenOrderFormFile1;

    @SerializedName("SemenDropLocationImg1")
    private String semenDropLocationImg1;

    @SerializedName("RouteList")
    private List<CourierRoute> routeList;

    @SerializedName("firmExpectedMonthlyDosesModelList")
    private List<MonthlyDoses> monthlyDoses;

    // Add the rest as needed

    // Getters and Setters

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getFirmCompanyName() {
        return firmCompanyName;
    }

    public void setFirmCompanyName(String firmCompanyName) {
        this.firmCompanyName = firmCompanyName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalesRepName() {
        return salesRepName;
    }

    public void setSalesRepName(String salesRepName) {
        this.salesRepName = salesRepName;
    }

    public String getSalesRepCellPhone() {
        return salesRepCellPhone;
    }

    public void setSalesRepCellPhone(String salesRepCellPhone) {
        this.salesRepCellPhone = salesRepCellPhone;
    }

    public String getVeterinarianName() {
        return veterinarianName;
    }

    public void setVeterinarianName(String veterinarianName) {
        this.veterinarianName = veterinarianName;
    }

    public String getVeterinarianCellPhone() {
        return veterinarianCellPhone;
    }

    public void setVeterinarianCellPhone(String veterinarianCellPhone) {
        this.veterinarianCellPhone = veterinarianCellPhone;
    }

    public String getVeterinarianEmail() {
        return veterinarianEmail;
    }

    public void setVeterinarianEmail(String veterinarianEmail) {
        this.veterinarianEmail = veterinarianEmail;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicPhone() {
        return clinicPhone;
    }

    public void setClinicPhone(String clinicPhone) {
        this.clinicPhone = clinicPhone;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFarmEmergencyNo() {
        return farmEmergencyNo;
    }

    public void setFarmEmergencyNo(String farmEmergencyNo) {
        this.farmEmergencyNo = farmEmergencyNo;
    }

    public String getFarmEmergencyNo2nd() {
        return farmEmergencyNo2nd;
    }

    public void setFarmEmergencyNo2nd(String farmEmergencyNo2nd) {
        this.farmEmergencyNo2nd = farmEmergencyNo2nd;
    }

    public String getCallAheadInstructions() {
        return callAheadInstructions;
    }

    public void setCallAheadInstructions(String callAheadInstructions) {
        this.callAheadInstructions = callAheadInstructions;
    }

    public String getDirectionToSemenDropSite() {
        return directionToSemenDropSite;
    }

    public void setDirectionToSemenDropSite(String directionToSemenDropSite) {
        this.directionToSemenDropSite = directionToSemenDropSite;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getDelAddress() {
        return delAddress;
    }

    public void setDelAddress(String delAddress) {
        this.delAddress = delAddress;
    }

    public String getDelCity() {
        return delCity;
    }

    public void setDelCity(String delCity) {
        this.delCity = delCity;
    }

    public String getDelState() {
        return delState;
    }

    public void setDelState(String delState) {
        this.delState = delState;
    }

    public String getDelZipCode() {
        return delZipCode;
    }

    public void setDelZipCode(String delZipCode) {
        this.delZipCode = delZipCode;
    }

    public String getDelCourierRoute() {
        return delCourierRoute;
    }

    public void setDelCourierRoute(String delCourierRoute) {
        this.delCourierRoute = delCourierRoute;
    }

    public String getUserImage1() {
        return userImage1;
    }

    public void setUserImage1(String userImage1) {
        this.userImage1 = userImage1;
    }

    public String getSemenOrderFormFile1() {
        return semenOrderFormFile1;
    }

    public void setSemenOrderFormFile1(String semenOrderFormFile1) {
        this.semenOrderFormFile1 = semenOrderFormFile1;
    }

    public String getSemenDropLocationImg1() {
        return semenDropLocationImg1;
    }

    public void setSemenDropLocationImg1(String semenDropLocationImg1) {
        this.semenDropLocationImg1 = semenDropLocationImg1;
    }

    public List<CourierRoute> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<CourierRoute> routeList) {
        this.routeList = routeList;
    }

    public List<MonthlyDoses> getMonthlyDoses() {
        return monthlyDoses;
    }

    public void setMonthlyDoses(List<MonthlyDoses> monthlyDoses) {
        this.monthlyDoses = monthlyDoses;
    }
}
