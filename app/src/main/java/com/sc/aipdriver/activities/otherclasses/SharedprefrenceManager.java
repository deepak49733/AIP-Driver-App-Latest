package com.sc.aipdriver.activities.otherclasses;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.sc.aipdriver.activities.models.LogInData;
import com.sc.aipdriver.activities.models.UserCred;


/**
 * Created by dev on 2/8/17.
 */
public class SharedprefrenceManager {

    private static final String USERNAME = "UserName";
    private static final String TOKEN = "token";
    private static final String PSWD = "pswd";
    public static final String USER_ID = "userId";
    public static final String FID = "fId";
    public static final String ORG_ID = "orgId";
    public static final String RIDE_STATUS = "ride_status";
    public static final String DRIVER_ID = "driverId";
    public static final String ANOTHERLOGIN = "anotherdevicelogin";
    public static final String USER_REFRESH_TOKEN = "userRefreshToken";
    private static final String USER_TOKEN = "token";
    private static final String USER_UID = "uid";
    private static final String PREF_KEY = "BILINGSOFTWARE";
    public static final String VEHICLE_ID="vahicle_id";
    public static final String ROUTE_ID="route_id";
    public static final String IS_PAUSED="is_paused";
    public static final String CARODOMETER="carOdometer";

    private static final String TOGGLE_STATE = "toggle_state";
    private static final String APP_MODE = "app_mode";
    public static final String MODE_SYNC = "SYNC";
    public static final String MODE_ONLINE = "ONLINE";
    public static final String OILPERCENTAGE="oil";
    public static final String RouteName="RouteName";
    public static final String Date="DateName";
    public static final String ImprovedDate="ImprovedDateName";
    public static final String ShowDate="ShowDate";
    public static final String TimeRemaining="TimeRemaining";
    public static final String StartingCarOdometer="StartingCarOdometer";
    private static final String KEY_BG_LOCATION_GRANTED = "bg_location_granted";
    private static final String LAST_ACTIVE_ACTIVITY = "last_active_activity";
    private static final String LAST_FARM_ID = "last_farm_id";
    private static final String LAST_ROUTE_ID = "last_route_id";
    private static final String LAST_ORDER_DATE = "last_order_date";
    private static final String LAST_ROUTE_NAME = "last_route_name";
    private static final String LAST_FARM_NAME = "last_farm_name";

    private static final String RIDE_ID = "Rid";
    private static final String IMAGE2 = "img2";
    private static final String IsStart = "0";
    private static final String IMAGE3 = "img3";
    private static final String IMAGE4 = "img4";
    private static final String IMAGE5 = "img5";
    private static final String IMAGE6 = "img6";
    private static final String IMAGE7 = "img7";
    private static final String IMAGE8 = "img8";
    private static final String IMAGE1 = "img1";
    private static final String PARENT_ID = "Pid";
    private static final String hasStart = "hasStartImage";

    private static final String Farm_ID = "Frmid";

    private Context mContext;
    private SharedPreferences sharedPreferences;

    public SharedprefrenceManager(Context mContext) {
        this.mContext = mContext;
        this.sharedPreferences = this.mContext.getSharedPreferences(PREF_KEY, 0);
    }

    public Boolean getAnotherLoggedIn() {
        return sharedPreferences.getBoolean(ANOTHERLOGIN,false);
    }
    public void setUserCredentials(LogInData userCredentials) {
        this.sharedPreferences.edit().putString(USER_ID, userCredentials.getId()).apply();
       this.sharedPreferences.edit().putString(USER_TOKEN, userCredentials.getUserName()).apply();
    }
    public void setUserPswd(UserCred userCredentials) {
        this.sharedPreferences.edit().putString(USERNAME, userCredentials.getUserName()).apply();
       this.sharedPreferences.edit().putString(PSWD, userCredentials.getPswd()).apply();
    }
    public void setToggleState(boolean isEnabled) {
        sharedPreferences.edit().putBoolean(TOGGLE_STATE, isEnabled).apply();
        setAppMode(isEnabled ? MODE_SYNC : MODE_ONLINE);
    }

    public void setAppMode(String mode) {
        String normalized = MODE_SYNC;
        if (mode != null && mode.equalsIgnoreCase(MODE_ONLINE)) {
            normalized = MODE_ONLINE;
        }
        sharedPreferences.edit().putString(APP_MODE, normalized).apply();
        // Keep existing toggle consumers working.
        sharedPreferences.edit().putBoolean(TOGGLE_STATE, MODE_SYNC.equals(normalized)).apply();
    }

    public void applyLoginMode(String loginModeValue) {
        if (loginModeValue == null) {
            return;
        }
        String normalized = loginModeValue.trim().toUpperCase();
        if (normalized.isEmpty()) {
            return;
        }

        if ("1".equals(normalized)
                || "TRUE".equals(normalized)
                || "SYNC".equals(normalized)
                || "SYNC_MODE".equals(normalized)
                || "OFFLINE".equals(normalized)
                || "OFFLINE_SYNC".equals(normalized)) {
            setAppMode(MODE_SYNC);
        } else if ("0".equals(normalized)
                || "FALSE".equals(normalized)
                || "ONLINE".equals(normalized)
                || "ONLINE_MODE".equals(normalized)
                || "LIVE".equals(normalized)) {
            setAppMode(MODE_ONLINE);
        }
    }

    public String getAppMode() {
        String mode = sharedPreferences.getString(APP_MODE, null);
        if (mode == null || mode.trim().isEmpty()) {
            return sharedPreferences.getBoolean(TOGGLE_STATE, false) ? MODE_SYNC : MODE_ONLINE;
        }
        return mode;
    }

    public boolean isSyncMode() {
        return MODE_SYNC.equalsIgnoreCase(getAppMode());
    }

    public boolean isOnlineMode() {
        return !isSyncMode();
    }

    public void setToken(String token) {
        this.sharedPreferences.edit().putString(TOKEN, token).apply();
    }

    public void setUsername(String username) {
        this.sharedPreferences.edit().putString(USERNAME, username).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(USERNAME, "");
    }

    public UserCred getUserCredentials() {
        UserCred userCred = new UserCred();
        userCred.setUserName(sharedPreferences.getString(USERNAME, "0"));
        userCred.setPswd( sharedPreferences.getString(PSWD, "0"));
        return userCred;
    }
    public void setDriverID(String DRid) {
        this.sharedPreferences.edit().putString(DRIVER_ID, DRid).apply();
    }
    public void isAnotherLoggedIn(Boolean DRid) {
        this.sharedPreferences.edit().putBoolean(ANOTHERLOGIN, DRid).apply();
    }
    public void setTimeRemaining(String TIME_REMAINING) {
        this.sharedPreferences.edit().putString(TimeRemaining, TIME_REMAINING).apply();
    }


    public void setOrgIdID(String orgIdID) {
        this.sharedPreferences.edit().putString(ORG_ID, orgIdID).apply();
    }

    public void setRideStatus(String status) {
        this.sharedPreferences.edit().putString(RIDE_STATUS, status).apply();
    }
    public boolean getToggleState() {
        return isSyncMode();
    }
    public String getRideStatus(){
        return sharedPreferences.getString(RIDE_STATUS,"Moving");
    }
    public String getOrgID() {
        return sharedPreferences.getString(ORG_ID,"0");
    } public String getToken() {
        return sharedPreferences.getString(TOKEN,"0");
    }
    public String getDriverID() {
        return sharedPreferences.getString(DRIVER_ID,"0");
    }
    public String getRouteName() {
        return sharedPreferences.getString(RouteName,"");
    }
    public String getDate() {
        return sharedPreferences.getString(Date,"");
    }public String getImprovedDate() {
        return sharedPreferences.getString(ImprovedDate,"");
    }
    public String getShowDate() {
        return sharedPreferences.getString(ShowDate,"");
    }
    public String getUserId(){
        return sharedPreferences.getString(USER_ID,"0");
    }
    public String getTimeRemaining() {
        return sharedPreferences.getString(TimeRemaining,"");
    }

    public String getUsername(){
        return sharedPreferences.getString(USER_TOKEN,"0");
    }

    public String getIsStart(){
        return sharedPreferences.getString(IsStart,"0");
    }

    public void setVehicleId(Integer id) {
        this.sharedPreferences.edit().putString(VEHICLE_ID,""+id).apply();
    }
    public void setIsStart(String id) {
        this.sharedPreferences.edit().putString(IsStart,id).apply();
    }
    public void setRouteName(String rname) {
        this.sharedPreferences.edit().putString(RouteName,rname).apply();
    }

    public void setDate(String date) {
        this.sharedPreferences.edit().putString(Date,date).apply();
    }

    public void setShouldNavigateToMap(boolean should) {
        this.sharedPreferences.edit().putBoolean("ShouldNavigateToMap", should).apply();
    }

    public boolean getShouldNavigateToMap() {
        return sharedPreferences.getBoolean("ShouldNavigateToMap", false);
    }

    public boolean rideStarted() {
        return sharedPreferences.getString(IsStart,"0").equals("1");
    }
    public void setImprovedDate(String date) {
        this.sharedPreferences.edit().putString(ImprovedDate,date).apply();
    }
    public void setShowDate(String date) {
        this.sharedPreferences.edit().putString(ShowDate,date).apply();
    }

    public String getVehicleId() {
        return sharedPreferences.getString(VEHICLE_ID,"4");
    }
    public void setRouteId(Integer id) {
        this.sharedPreferences.edit().putString(ROUTE_ID,""+id).apply();
    }
    public String getRouteId() {
        return sharedPreferences.getString(ROUTE_ID,"0");
    }
    public void setIsPause(Integer id) {
        this.sharedPreferences.edit().putInt(IS_PAUSED,id).apply();
    }
    public int getIsPaused() {
        return sharedPreferences.getInt(IS_PAUSED,0);
    }

    public void setRideIdString(String id) {
        this.sharedPreferences.edit().putString(RIDE_ID,id).apply();
    }
    public void setRideId(Integer id) {
        this.sharedPreferences.edit().putString(RIDE_ID,""+id).apply();
    }


    public void setImage1(String img1) {
        this.sharedPreferences.edit().putString(IMAGE1,img1).apply();
    }
    public String getImg1() {

        return sharedPreferences.getString(IMAGE1,"");
    }
    public String getImg2() {

        return sharedPreferences.getString(IMAGE2,"");
    }
    public String getImg3() {

        return sharedPreferences.getString(IMAGE3,"");
    }
    public String getImg4() {

        return sharedPreferences.getString(IMAGE4,"");
    }
    public String getImg5() {

        return sharedPreferences.getString(IMAGE5,"");
    }
    public String getImg6() {

        return sharedPreferences.getString(IMAGE6,"");
    }
    public String getImg7() {

        return sharedPreferences.getString(IMAGE7,"");
    }
    public String getImg8() {

        return sharedPreferences.getString(IMAGE8,"");
    }
    public void setImage2(String img2) {
        this.sharedPreferences.edit().putString(IMAGE2,img2).apply();
    }  public void setImage3(String img3) {
        this.sharedPreferences.edit().putString(IMAGE3,img3).apply();
    }
    public void setImage4(String img4) {
        this.sharedPreferences.edit().putString(IMAGE4,img4).apply();
    }
    public void setImage5(String img5) {
        this.sharedPreferences.edit().putString(IMAGE5,img5).apply();
    }
    public void setImage6(String img6) {
        this.sharedPreferences.edit().putString(IMAGE6,img6).apply();
    }
    public void setImage7(String img7) {
        this.sharedPreferences.edit().putString(IMAGE7,img7).apply();
    }
    public void setImage8(String img8) {
        this.sharedPreferences.edit().putString(IMAGE8,img8).apply();
    }
    public void setParentId(Integer id) {
        this.sharedPreferences.edit().putString(PARENT_ID,""+id).apply();
    }

    public String getRideId() {
        return sharedPreferences.getString(RIDE_ID,"0");
    }

    public int getRideIdInt() {
        String rideId = getRideId();
        if (rideId == null || rideId.isEmpty() || rideId.startsWith("OFF_")) {
            return 0;
        }
        try {
            return Integer.parseInt(rideId);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getDriverIdInt() {
        String id = getDriverID();
        if (id == null || id.isEmpty()) return 0;
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getVehicleIdInt() {
        String id = getVehicleId();
        if (id == null || id.isEmpty()) return 0;
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getUserIdInt() {
        String id = getUserId();
        if (id == null || id.isEmpty()) return 0;
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getParentIdInt() {
        String id = getParentId();
        if (id == null || id.isEmpty()) return 0;
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean isOfflineRide() {
        String rideId = getRideId();
        return rideId != null && rideId.startsWith("OFF_");
    }
    //
    public String getParentId() {
        return sharedPreferences.getString(PARENT_ID,"0");
    }
    public String hasImages() {
        return sharedPreferences.getString(hasStart,"no");
    }

    public void setFID(String id) {
        //Log.d("Analysis__","Setting in db from SP");
        this.sharedPreferences.edit().putString(FID,""+id).apply();
    }
    public void setHasImage(String id) {
        //Log.d("Analysis__","Setting in db from SP");
        this.sharedPreferences.edit().putString(hasStart,id).apply();
    }

    public String getFID() {
        return sharedPreferences.getString(FID,"");
    }
    //
    public void setStartOdoMeter(String begningcarodometertext) {
        this.sharedPreferences.edit().putString(CARODOMETER,begningcarodometertext).apply();
    }
    public String getCarodometer() {
        return sharedPreferences.getString(CARODOMETER,"0");
    }

    public void setBegningOil(String begningcarodometertext) {
        this.sharedPreferences.edit().putString(OILPERCENTAGE,""+begningcarodometertext).apply();
    }
    public String getBegningOil() {
        return sharedPreferences.getString(OILPERCENTAGE,"0");
    }

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

    public void setMailSent(String mailType, String routeId, String farmId, String date, boolean sent) {
        String key = "mail_" + mailType + "_" + routeId + "_" + farmId + "_" + date;
        Log.d("DEBUG_EMAIL", "Setting MailSent: " + key + " = " + sent);
        sharedPreferences.edit().putBoolean(key, sent).commit(); // ✅ Use commit() for immediate persistence to avoid race conditions
    }

    public boolean isMailSent(String mailType, String routeId, String farmId, String date) {
        String key = "mail_" + mailType + "_" + routeId + "_" + farmId + "_" + date;
        boolean result = sharedPreferences.getBoolean(key, false);
        Log.d("DEBUG_EMAIL", "Checking MailSent: " + key + " = " + result);
        return result;
    }

    public void saveBackgroundLocationGranted() {
        sharedPreferences
                .edit()
                .putBoolean(KEY_BG_LOCATION_GRANTED, true)
                .apply();
    }

    public boolean isDisclosureAlreadyShown() {
        return sharedPreferences
                .getBoolean(KEY_BG_LOCATION_GRANTED, false);
    }

    public void setLastActiveActivity(String className) {
        sharedPreferences.edit().putString(LAST_ACTIVE_ACTIVITY, className).apply();
    }

    public String getLastActiveActivity() {
        return sharedPreferences.getString(LAST_ACTIVE_ACTIVITY, null);
    }

    public void setLastFarmId(String farmId) {
        sharedPreferences.edit().putString(LAST_FARM_ID, farmId).apply();
    }

    public String getLastFarmId() {
        return sharedPreferences.getString(LAST_FARM_ID, "");
    }

    public void setLastRouteId(String routeId) {
        sharedPreferences.edit().putString(LAST_ROUTE_ID, routeId).apply();
    }

    public String getLastRouteId() {
        return sharedPreferences.getString(LAST_ROUTE_ID, "");
    }

    public void setLastOrderDate(String date) {
        sharedPreferences.edit().putString(LAST_ORDER_DATE, date).apply();
    }

    public String getLastOrderDate() {
        return sharedPreferences.getString(LAST_ORDER_DATE, "");
    }

    public void setLastRouteName(String routeName) {
        sharedPreferences.edit().putString(LAST_ROUTE_NAME, routeName).apply();
    }

    public String getLastRouteName() {
        return sharedPreferences.getString(LAST_ROUTE_NAME, "");
    }

    public void setLastFarmName(String farmName) {
        sharedPreferences.edit().putString(LAST_FARM_NAME, farmName).apply();
    }

    public String getLastFarmName() {
        return sharedPreferences.getString(LAST_FARM_NAME, "");
    }

    public void clearLastActiveActivity() {
        sharedPreferences.edit()
                .remove(LAST_ACTIVE_ACTIVITY)
                .remove(LAST_FARM_ID)
                .remove(LAST_ROUTE_ID)
                .remove(LAST_ORDER_DATE)
                .remove(LAST_ROUTE_NAME)
                .remove(LAST_FARM_NAME)
                .apply();
    }

    public void clearRideState() {
        clearLastActiveActivity();
        setParentId(0);
        setRideId(0);
        setFID("0");
        setRideStatus("End");
    }

}