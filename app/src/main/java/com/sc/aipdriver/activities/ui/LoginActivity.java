package com.sc.aipdriver.activities.ui;

import static kotlinx.coroutines.CoroutineScopeKt.CoroutineScope;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.fragments.SelectCar;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.LogInData;
import com.sc.aipdriver.activities.models.UserCred;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.room.AppDatabase;
import com.sc.aipdriver.activities.services.LoginUpdateService;


import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import kotlinx.coroutines.Dispatchers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button login;

    EditText username;
    private static final int LOCATION_PERMISSION = 101;
    EditText password;
    private static final int UPDATE_REQUEST_CODE = 1001;
    private AppUpdateManager appUpdateManager;
    ApiInterface apiService;
    Gson gson;
    SharedprefrenceManager sharedprefrenceManager;
    SweetAlertDialog pDialog;
    private AlertDialog backgroundLocationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.btn_login);
        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        gson = new GsonBuilder().setPrettyPrinting().create();
        sharedprefrenceManager = new SharedprefrenceManager(this);
//        if (sharedprefrenceManager.getUserCredentials() != null) {
//            UserCred userCred = sharedprefrenceManager.getUserCredentials();
//            if (userCred.getUserName().equals("0") || userCred.getPswd().equals("0")) {
//                username.setText("");
//                password.setText("");
//            } else {
//                username.setText(userCred.getUserName());
//                password.setText(userCred.getPswd());
//            }
//        }
        appUpdateManager = AppUpdateManagerFactory.create(this);

        checkForUpdate();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setEnabled(false);
                login.setClickable(false);
                // Clear Room database in Java
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.Companion.getDatabase(getApplicationContext()); // your Room DB instance
                        db.clearAllTables(); // Deletes all data in all tables
                    }
                }).start();
                if (valid()) {
                    sendrequest();
                }else{
                    login.setEnabled(true);
                    login.setClickable(true);
                }
            }
        });
        checkRuntimePermission();
        loginCheck();
    }
    private void checkForUpdate() {

        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {

                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.IMMEDIATE,
                                    this,
                                    UPDATE_REQUEST_CODE
                            );
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                // If user cancels or update fails -> Close app
                finish();
            }
        }
}

  /*  private void checkRuntimePermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION

                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {*//* ... *//*}
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {*//* ... *//*}
                }).check();

    }*/
  private void checkRuntimePermission() {
      showBackgroundLocationDisclosure();

  }

    /**
     * Step 2: Show a clear and prominent disclosure before requesting background location.
     */
    private void showBackgroundLocationDisclosure() {
        if (isFinishing()) return;

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Allow Background Location Access")
                    .setMessage("We collect your location data to track your delivery route and update it on our servers, even when the app is closed or not in use. " +
                            "This helps us show your path on the map and provide accurate delivery tracking. " +
                            "You can turn off location permissions anytime in your device settings.")
                    .setPositiveButton("Allow", (dialog, which) -> {
                        // dialog will be dismissed by the system; request permission next
                        requestBackgroundLocationPermission();
                    })
                    .setNegativeButton("No, thanks", (dialog, which) -> {
                        try {
                            dialog.dismiss();
                        } catch (Exception ignored) {
                        }
                    });

            // create and show, keep reference to dismiss later to avoid WindowLeaked
            backgroundLocationDialog = builder.create();
            backgroundLocationDialog.setCanceledOnTouchOutside(false);
            if (!isFinishing()) backgroundLocationDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Step 3: Request ACCESS_BACKGROUND_LOCATION after disclosure
     */
    private void requestBackgroundLocationPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION

                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {  }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {  }
                }).check();
    }

    @Override
    protected void onDestroy() {
        try {
            if (backgroundLocationDialog != null && backgroundLocationDialog.isShowing()) {
                backgroundLocationDialog.dismiss();
                backgroundLocationDialog = null;
            }
        } catch (Exception ignored) {
        }

        try {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog = null;
            }
        } catch (Exception ignored) {
        }

        super.onDestroy();
    }





    private void loginCheck() {
        if (sharedprefrenceManager.getUserId() != null && sharedprefrenceManager.getUserId().equalsIgnoreCase("0")) {

        } else {
            String lastActivityName = sharedprefrenceManager.getLastActiveActivity();
            if (lastActivityName != null && !lastActivityName.isEmpty()) {
                try {
                    Class<?> clazz = Class.forName(lastActivityName);
                    Intent intent = new Intent(LoginActivity.this, clazz);

                    String farmId = sharedprefrenceManager.getLastFarmId();
                    String routeId = sharedprefrenceManager.getLastRouteId();
                    String orderDate = sharedprefrenceManager.getLastOrderDate();
                    String routeName = sharedprefrenceManager.getLastRouteName();
                    String farmName = sharedprefrenceManager.getLastFarmName();

                    if (farmId != null && !farmId.isEmpty()) {
                        intent.putExtra("FarmID", farmId);
                        intent.putExtra("farmId", farmId);
                        intent.putExtra("farmIdd", farmId);
                        intent.putExtra("farmid", farmId);
                    }
                    if (routeId != null && !routeId.isEmpty()) {
                        intent.putExtra("ROUTEID", routeId);
                        intent.putExtra("routeId", routeId);
                        intent.putExtra("routeid", routeId);
                    }
                    if (orderDate != null && !orderDate.isEmpty()) {
                        intent.putExtra("orderdate", orderDate);
                        intent.putExtra("date", orderDate);
                    }
                    if (routeName != null && !routeName.isEmpty()) {
                        intent.putExtra("ROUTE", routeName);
                        intent.putExtra("RouteName", routeName);
                    }
                    if (farmName != null && !farmName.isEmpty()) {
                        intent.putExtra("FarmName", farmName);
                        intent.putExtra("farmName", farmName);
                    }

                    startActivity(intent);
                    finish();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            startActivity(new Intent(LoginActivity.this, SelectCar.class));
            finish();
        }
    }

    private boolean valid() {
        if (username.getText().toString().length() == 0) {
            username.setError("Enter UserName");
            return false;
        }
        if (password.getText().toString().length() == 0) {
            password.setError("Enter Password");
            return false;
        }
        return true;
    }

    private void sendrequest() {
        showLoading();
        Call<BaseResponse<LogInData>> call = apiService.getUserLogin(username.getText().toString().trim(), password.getText().toString().trim(), "", "");
        call.enqueue(new Callback<BaseResponse<LogInData>>() {
            @Override
            public void onResponse(Call<BaseResponse<LogInData>> call, Response<BaseResponse<LogInData>> response) {
                if (response.code() == 200) {
                    pDialog.dismissWithAnimation();
                    LogInData logInData = response.body().getData();
                    Log.e("Data Login Response", response.body().getMessage() + " " + logInData.getUserName());

                    if (logInData.getAnotherDeviceLogin() != null && logInData.getAnotherDeviceLogin()) {
                            errorDialog("Your account is logged in on another device. Please log out from that device to continue.");
                            login.setEnabled(true);
                            login.setClickable(true);
                    }else{
                        doLogin(logInData);

                    }

                } else
                    try {
                        login.setEnabled(true);
                        login.setClickable(true);
                        pDialog.dismissWithAnimation();
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        errorDialog(loginError.getMessage());
                        System.err.println("Data Login Error"+ loginError.getMessage());
                        Log.e("Data Login Error", loginError.getMessage());
                    } catch (IOException e) {
                        login.setEnabled(true);
                        login.setClickable(true);
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<BaseResponse<LogInData>> call, Throwable t) {
                if (pDialog.isShowing()) {
                    pDialog.dismissWithAnimation();
                }
                login.setEnabled(true);
                login.setClickable(true);
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                System.err.println("errorratro"+ t.getMessage());
                Log.e("errorratro", t.getMessage());
            }
        });
    }

    private void doLogin(LogInData logInData) {
        sharedprefrenceManager.setUserCredentials(logInData);
        sharedprefrenceManager.applyLoginMode(logInData.getModeKey());
        UserCred userCred = new UserCred();
        userCred.setUserName(username.getText().toString().trim());
        userCred.setPswd(password.getText().toString().trim());
        sharedprefrenceManager.setUserPswd(userCred);
        sharedprefrenceManager.setToken(logInData.getToken());
        System.err.println("DRIVER ID  ::::"+logInData.getId());
        sharedprefrenceManager.setDriverID(logInData.getId());
        sharedprefrenceManager.setOrgIdID(logInData.getOrgId());
        sharedprefrenceManager.isAnotherLoggedIn(logInData.getAnotherDeviceLogin());
        startActivity(new Intent(LoginActivity.this, SelectCar.class));
        finish();
    }

    private void showLoading() {
        try {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setContentText("Please Wait...");
            pDialog.setCancelable(false);
            if (!isFinishing()) pDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void errorDialog(String st) {
        try {
            if (isFinishing()) return;
            SweetAlertDialog d = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setContentText(st);
            d.show();
        } catch (Exception ignored) {
        }
    }

}
