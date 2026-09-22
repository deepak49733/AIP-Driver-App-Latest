package com.sc.aipdriver.activities.fragments;

import static com.sc.aipdriver.activities.IsInternetAvailableKt.isInternetAvailable;
import static com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.dialogs.ShowLoading;
import com.sc.aipdriver.activities.models.LatLongData;
import com.sc.aipdriver.activities.models.PermissionData;
import com.sc.aipdriver.activities.models.RideFinishRequest;
import com.sc.aipdriver.activities.models.RoutePlannerDetail;
import com.sc.aipdriver.activities.otherclasses.GeoTask;
import com.sc.aipdriver.activities.otherclasses.ShouldLogout;
import com.sc.aipdriver.activities.ui.FarmDetailActivity;
import com.sc.aipdriver.activities.ui.FarmListRoute;
import com.sc.aipdriver.activities.ui.ImprovedFarmList;
import com.sc.aipdriver.activities.ui.ImprovedFarmList;
import com.sc.aipdriver.activities.ui.LoginActivity;
import com.sc.aipdriver.activities.ui.MapsActivityNew;
import com.sc.aipdriver.activities.adapters.SelectCarAdapter;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.PermissionModel;
import com.sc.aipdriver.activities.models.VehicleListData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.services.LoginUpdateService;
import com.sc.aipdriver.activities.IsInternetAvailableKt;
import com.sc.aipdriver.activities.room.AppDatabase;
import com.sc.aipdriver.activities.room.PriorityFarmDao;
import com.sc.aipdriver.activities.room.ImprovedPriorityFarmDao;
import cn.pedant.SweetAlert.SweetAlertDialog;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Created by dev on 26/10/17.
 */

public class SelectCar extends AppCompatActivity {
    private static final int REQUEST_CHECK_SETTINGS = 101;

    RecyclerView recyclerView;

   // Button resume_ride;
    private static final int LOCATION_PERMISSION = 101;

    int rideId=0;
    int routeId=0;
    int parentId=0;
    int farmId=0;
    String farmname="";
    String routeName="";
    String orderDate="";
    int action=0;
    Button logout;
    double latitude;
    double longitude;
    Location location;
    SelectCarAdapter selectCarAdapter;
    ApiInterface apiService;
    Gson gson;
    ShowLoading showLoading;
    SharedprefrenceManager sharedprefrenceManager;
    List<VehicleListData> vehicleListDataList;
    String TAG="AipDriver";
    FusedLocationProviderClient fusedLocationClient ;

    String orgId;
    Geocoder geocoder;
    List<Address> addresses = new ArrayList<>();
    List<Address> addresses1 = new ArrayList<>();
    ShouldLogout shouldLogout=null;
    boolean rideStart = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private void handleOfflineRedirection() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.Companion.getDatabase(this);
            ImprovedPriorityFarmDao improvedDao = db.improvedPriorityFarmDao();
            PriorityFarmDao priorityDao = db.priorityFarmDao();

            String rawDate = sharedprefrenceManager.getDate();
            String rawImprovedDate = sharedprefrenceManager.getImprovedDate();

            String date = formatToMMDDYYYY(rawDate);
            String improvedDate = formatToMMDDYYYY(rawImprovedDate);

            int improvedCount = improvedDao.getImprovedFarmCountByDate(improvedDate);
            int priorityCount = priorityDao.getFarmCountByDate(date);

            Intent intent;

            if (improvedCount > 0) {
                // ✅ Always redirect to FarmListRoute first during preparation, even if all items are processed.
                // The user should manually click "Next" to go to ImprovedFarmList.
                intent = new Intent(SelectCar.this, FarmListRoute.class);
                intent.putExtra("ROUTE", sharedprefrenceManager.getRouteName());
                intent.putExtra("ROUTEID", sharedprefrenceManager.getRouteId() + "");
                intent.putExtra("SEQUENCE", "Start");
                intent.putExtra("date", improvedDate);
                intent.putExtra("orderdate", improvedDate);

            } else if (priorityCount > 0) {
                // ✅ Always redirect to FarmListRoute first during preparation
                intent = new Intent(SelectCar.this, FarmListRoute.class);
                intent.putExtra("ROUTE", sharedprefrenceManager.getRouteName());
                intent.putExtra("ROUTEID", sharedprefrenceManager.getRouteId() + "");
                intent.putExtra("SEQUENCE", "Start");
                intent.putExtra("date", date);
                intent.putExtra("orderdate", date);
            } else {
                checkInternet();
                return;
            }

            intent.putExtra("isResumed", true);
            startActivity(intent);
            finish();
        });
    }
    private void checkInternet() {
        runOnUiThread(() -> {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No Internet Connection")
                    .setContentText("No offline data available. Please connect to the internet to continue.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        if (isInternetAvailable(this)) {
                            recreate();
                        } else {
                            handleOfflineRedirection();
                        }
                    })
                    .show();
        });
    }

/*    private void handleOfflineRedirection() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.Companion.getDatabase(this);
            ImprovedPriorityFarmDao improvedDao = db.improvedPriorityFarmDao();
            PriorityFarmDao priorityDao = db.priorityFarmDao();

            String improvedDatee = sharedprefrenceManager.getImprovedDate();
            String formattedDate  = sharedprefrenceManager.getDate();
            String date = formatToMMDDYYYY(formattedDate);
            String improvedDate = formatToMMDDYYYY(improvedDatee);
            int improvedCount = improvedDao.getImprovedFarmCountByDate(improvedDate);
            int priorityCount = priorityDao.getFarmCountByDate(date);
            if (improvedCount > 0) {
                int loadedImprovedCount = improvedDao.getLoadedImprovedFarmCountByDate(improvedDate);
                Intent intent;
                if (loadedImprovedCount == improvedCount) {
                    intent = new Intent(SelectCar.this, ImprovedFarmList.class);
                } else {
                    intent = new Intent(SelectCar.this, FarmListRoute.class);
                }
                intent.putExtra("orderdate", improvedDate);
                intent.putExtra("isResumed", true);
                startActivity(intent);
                finish();
            }
            else if (priorityCount > 0) {


                    Intent intent = new Intent(SelectCar.this, FarmListRoute.class);
                    intent.putExtra("orderdate", date);
                    intent.putExtra("isResumed", true);
                    startActivity(intent);
                    finish();
                }
            else{
                runOnUiThread(() -> {
                    new SweetAlertDialog(SelectCar.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("No Internet Connection")
                            .setContentText("No offline data available. Please connect to the internet to continue.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                if (IsInternetAvailableKt.isInternetAvailable(SelectCar.this)) {
                                    recreate();
                                } else {
                                    handleOfflineRedirection();
                                }
                            })
                            .show();
                });
            }

        });
    }*/

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);
        
        sharedprefrenceManager = new SharedprefrenceManager(this);
        if (!isInternetAvailable(this)) {
            handleOfflineRedirection();
        }
        
        View rootView = findViewById(R.id.ll_cars);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });
        recyclerView = findViewById(R.id.recyclerview);
       // resume_ride = findViewById(R.id.resume_ride);
        logout = findViewById(R.id.logout);
        vehicleListDataList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectCarAdapter = new SelectCarAdapter(this,vehicleListDataList);

        recyclerView.setAdapter(selectCarAdapter);
        geocoder = new Geocoder(this, Locale.getDefault());
        showLoading = new ShowLoading(this);
// Check for location permission

        checkRuntimePermission();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted - request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission granted - get location
            getLocation();
        }

        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        gson = new GsonBuilder().setPrettyPrinting().create();
        sharedprefrenceManager = new SharedprefrenceManager(this);
        orgId = sharedprefrenceManager.getOrgID();
        shouldLogout = new ShouldLogout(this,sharedprefrenceManager);
        recyclerView.setVisibility(View.GONE);

       // ShowPermission();


        ShowPermission();
       // getVehicle();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable(SelectCar.this)) {
                    Toast.makeText(SelectCar.this, "No internet connection. Please connect to the internet to log out.", Toast.LENGTH_SHORT).show();
                    return;
                }
                shouldLogout.shouldLogout(true,SelectCar.this);
                    //logout();
            }
        });

        displayLocationSettingsRequest(this);
    }

    private void logout() {


    }

    private void accessLocation() {
        // Your code to access location goes here
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getStartedRide(String rideId,int action) {
        System.err.println("Getting Farm Routes  ");

        if (action==1){

        }else {
            if (rideId.length() > 1) {

            } else {
                rideId = sharedprefrenceManager.getRideId();
            }
        }
        Call<BaseResponse<LatLongData>> call = apiService.getstartNewRide(rideId,sharedprefrenceManager.getDriverID(),sharedprefrenceManager.getToken());
        call.enqueue(new Callback<BaseResponse<LatLongData>>() {
            @Override
            public void onResponse(Call<BaseResponse<LatLongData>> call, Response<BaseResponse<LatLongData>> response) {
                System.err.println("SelectCar getFarmsRoute   " + response + "    ----    " + response.body());
                if (response.code() == 200) {
                    //Log.d("Analysis__","Got response of already started Ride.,");
                        if (response.body().getData()!=null) {
                            if (action == 1) {
                                Log.d("Meter__","Start Odometer is "+response.body().getData().getStartOdometer());

                                sharedprefrenceManager.setStartOdoMeter(response.body().getData().getStartOdometer());
                            } else {
                                if (response.body().getData().getParentId() == null) {
                                    Log.d("Meter__","Start Odometer is "+response.body().getData().getStartOdometer());
                                    sharedprefrenceManager.setStartOdoMeter(response.body().getData().getStartOdometer());
                                    showRouteSelected(response.body().getData().getId());
                                } else if (response.body().getData().getParentId() == 0) {
                                    showRouteSelected(response.body().getData().getId());
                                }
                            }
                            Log.d("Meter__","Start Odometer is "+sharedprefrenceManager.getCarodometer());
                        }
                } else
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        // errorDialog(loginError.getMessage());

                        Log.e("Data Get Farmslist", loginError.getMessage());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<BaseResponse<LatLongData>> call, Throwable t) {
                Log.e("errorratro", t.getMessage());
//                System.err.println("MAPS ACTIVITY getFarmsRoute  Failure ");

            }
        });
    }

    private void showRouteSelected(int id) {
        try {
            //Log.d("Analysis__","Showing dialog");
            final Dialog dialog = new Dialog(SelectCar.this, cn.pedant.SweetAlert.R.style.MyDialog);
            dialog.setContentView(R.layout.pauseresumedialog);
            Button btnOk = dialog.findViewById(R.id.btn_ok);
            Button btnNo = dialog.findViewById(R.id.btn_no);

            final TextView txtView = dialog.findViewById(R.id.txt_pause_resume);
                txtView.setText("You have already selected a route. Do you want to continue same route?");
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String checkId=parentId+"";
                    //Log.d("Analysis__","checkid is "+checkId);
                    if (checkId.equals("0") || checkId!=null){
                        //Log.d("Analysis__","Inside if block");
                        Intent intent = new Intent(SelectCar.this, FarmListRoute.class);
                        intent.putExtra("ROUTE", routeName);
                        intent.putExtra("ROUTEID", routeId + "");
                        intent.putExtra("SEQUENCE", "Start");
                        intent.putExtra("isResumed", true);
                        intent.putExtra("date", orderDate);
                        sharedprefrenceManager.setRouteName(routeName);
                        startActivity(intent);
                        finish();
                    }else {
                        //Log.d("Analysis__","farmid line 300 "+farmId);
                        startActivity(new Intent(SelectCar.this, ImprovedFarmList.class).putExtra("FarmID", farmId+"").putExtra("farmidd", farmId+"").putExtra("FarmName", farmname)
                                .putExtra("orderdate",orderDate).putExtra("RouteName", routeName).putExtra("Routeid", routeId).putExtra("isResumeRide",false));
                    }
                        dialog.dismiss();
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishCompleteRide();
                    FinishData("","");
                    dialog.dismiss();
                }
            });
            // btnOk.performClick();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showRunningRide() {
        try {
            //Log.d("Analysis__","Showing dialog");
            final Dialog dialog = new Dialog(SelectCar.this, cn.pedant.SweetAlert.R.style.MyDialog);
            dialog.setContentView(R.layout.pauseresumedialog);
            Button btnOk = dialog.findViewById(R.id.btn_ok);
            Button btnNo = dialog.findViewById(R.id.btn_no);

            final TextView txtView = dialog.findViewById(R.id.txt_pause_resume);
                txtView.setText("You have already a incompleted ride. Do you want to continue same ride?");
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d("Analysis__","farmid line 335 "+farmId);
                    startActivity(new Intent(SelectCar.this, ImprovedFarmList.class).putExtra("farmIdd",farmId+"").putExtra("FarmID",farmId+"").putExtra("FarmName",farmname)
                            .putExtra("orderdate",orderDate).putExtra("RouteName",routeName).putExtra("Routeid",routeId).putExtra("isResumeRide",true));
                        dialog.dismiss();
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FinishData("","");
                    dialog.dismiss();
                }
            });
            // btnOk.performClick();
            dialog.show();
        } catch (Exception e) {
            showLoading.dismiss();
            e.printStackTrace();
        }
    }
    public void FinishData(String endodometer, String totalmiles) {
//        sendLog("End");
        try {
            String address="";
            String city="";
            String state="";
            String country="";
            if (location!=null) {
                addresses1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                 address = addresses1.get(0).getAddressLine(0);
                 city = addresses1.get(0).getLocality();
                 state = addresses1.get(0).getAdminArea();
                 country = addresses1.get(0).getCountryName();
            }

            RideFinishRequest rideFinishRequest=new RideFinishRequest();
            rideFinishRequest.setAction("End");
            rideFinishRequest.setRideId(parentId+"");
            rideFinishRequest.setUID(sharedprefrenceManager.getDriverID());
            rideFinishRequest.setLat(latitude+"");
            rideFinishRequest.setLat(longitude+"");
            rideFinishRequest.setEndOdometer(endodometer);
            rideFinishRequest.setTotalMiles(totalmiles);
            rideFinishRequest.setAddress(address);
            rideFinishRequest.setState(state);
            rideFinishRequest.setCity(city);
            rideFinishRequest.setRouteId(routeId+"");
            rideFinishRequest.setCountry(country);

            Call<CommonError> call = apiService.rideFinish( rideFinishRequest,sharedprefrenceManager.getDriverID(),sharedprefrenceManager.getToken());
            call.enqueue(new Callback<CommonError>() {
                @Override
                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                    if (response.code() == 200) {
                        //  Log.e("ta", response.body());
                        sharedprefrenceManager.setParentId(0);
                        sharedprefrenceManager.setRideId(0);
                        sharedprefrenceManager.setRouteId(0);
                        sharedprefrenceManager.setFID(0+"");
                       // finishCompleteRide();
                    } else
                        try {

                            CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                            //  errorDialog(loginError.getMessage());
                            Log.e("Data", loginError.getMessage());
                        } catch (Exception e) {
                            ShowPermission();
                            getVehicle();
                            e.printStackTrace();
                            Log.e("Data", e.toString());
                        }
                    ShowPermission();
                    getVehicle();
                }

                @Override
                public void onFailure(Call<CommonError> call, Throwable t) {
                    ShowPermission();
                    getVehicle();
                    Log.e("errorratro", t.getMessage());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void finishCompleteRide() {
        //sendLog("End");
        if (!sharedprefrenceManager.getToggleState()) {
            showLoading.show();
            Call<CommonError> call = apiService.postVehicleStatus(Integer.parseInt(sharedprefrenceManager.getUserId()), Integer.parseInt(sharedprefrenceManager.getVehicleId()), 0, "Finish");
            call.enqueue(new Callback<CommonError>() {
                @Override
                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                    if (response.code() == 200) {
                        showLoading.dismiss();
                        sharedprefrenceManager.setRideId(0);
                        //     success("Alert!", "Ride finished");
                    } else
                        try {
                            showLoading.dismiss();
                            CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                            //  errorDialog(loginError.getMessage());
                            Log.e("Data", loginError.getMessage());
                        } catch (Exception e) {
                            showLoading.dismiss();
                            e.printStackTrace();
                            Log.e("Data", e.toString());
                        }
                }

                @Override
                public void onFailure(Call<CommonError> call, Throwable t) {
                    showLoading.dismiss();
                    Log.e("errorratro", t.getMessage());
                }
            });
        }
    }
    private void getVehicle() {
        Call<BaseResponse<List<VehicleListData>>> call = apiService.getListVehicle(orgId,sharedprefrenceManager.getDriverID(),sharedprefrenceManager.getToken());
        call.enqueue(new Callback<BaseResponse<List<VehicleListData>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<VehicleListData>>> call, Response<BaseResponse<List<VehicleListData>>> response) {
                if (response.code() == 200) {
                    vehicleListDataList.clear();
                    vehicleListDataList.addAll(response.body().getData());
                    selectCarAdapter.notifyDataSetChanged();



                } else
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);

                        Log.e("Data   Error Message", loginError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<VehicleListData>>> call, Throwable t) {
                Log.e("Data  errorratro", t.getMessage());
            }
        });
    }
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SelectCar.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.err.println("In Select Car Start  " + sharedprefrenceManager.getParentId());
        if(sharedprefrenceManager.getParentId().equalsIgnoreCase("0")||sharedprefrenceManager.getParentId().equalsIgnoreCase("0")){
            System.err.println(sharedprefrenceManager.getRideId() +   "   RIDE ID IN SELECT CAR");
            recyclerView.setVisibility(View.VISIBLE);
        }

        else{
            recyclerView.setVisibility(View.GONE);

        }
    }

    private void ShowPermission(){
        if (!isInternetAvailable(this)){
            return;
        }
        Call<PermissionModel> call = apiService.showPermission(sharedprefrenceManager.getDriverIdInt(),sharedprefrenceManager.getToken());
        call.enqueue(new Callback<PermissionModel>() {
            @Override
            public void onResponse(Call<PermissionModel> call, Response<PermissionModel> response) {
                if (response.code() == 200) {

                    if (response.body().getData()!=null) {
                        PermissionData pData = response.body().getData();

                        rideId = pData.getRideId();
                        routeId = pData.getRouteId();
                        parentId = pData.getParentId();
                        farmId = pData.getFarmId();
                        action = pData.getAction();
                        farmname = pData.getFarmName();
                        routeName = pData.getRouteName();
                        Log.d("Analysis__", "Permission RideId is " + rideId + " routeid is " + routeId + " parentid is " + parentId + " farmid is " + farmId + " action is " + action + " farmname is " + farmname + " routeName is " + routeName);
                        sharedprefrenceManager.setParentId(parentId);
                        sharedprefrenceManager.setFID(farmId + "");
                        sharedprefrenceManager.setRouteId(routeId);
                        sharedprefrenceManager.setRideId(rideId);
                        sharedprefrenceManager.setRouteName(routeName);
                        if (pData.getOrderDate() != null && !pData.getOrderDate().isEmpty()) {
                            orderDate = formatToMMDDYYYY(pData.getOrderDate());
                            sharedprefrenceManager.setDate(orderDate);
                            sharedprefrenceManager.setImprovedDate(orderDate);
                        }
                        // You can now use these values as needed
                        if (parentId == 0) {
                            Log.e("Analysis__", "Inside line 625");
                            getVehicle();
                            recyclerView.setVisibility(View.VISIBLE);
                            logout.setVisibility(View.VISIBLE);
                        }
                        else if (rideId == 0) {
                            Log.d("Analysis__","Inside line 631 - Redirecting to FarmListRoute");
                            recyclerView.setVisibility(View.GONE);
                            logout.setVisibility(View.GONE);
                            orderDate = pData.getOrderDate();

                            Intent intent = new Intent(SelectCar.this, FarmListRoute.class);
                            intent.putExtra("ROUTE", routeName);
                            intent.putExtra("ROUTEID", routeId + "");
                            intent.putExtra("SEQUENCE", "Start");
                            intent.putExtra("isResumed", true);
                            intent.putExtra("date", pData.getOrderDate());
                            sharedprefrenceManager.setRouteName(routeName);
                            sharedprefrenceManager.setShouldNavigateToMap(true);
                            startActivity(intent);
                            finish();
                            getStartedRide(parentId + "", action);
                        } else if (farmId == 0) {
                            //Log.d("Analysis__","Inside line 644 - Redirecting to FarmListRoute");
                            Intent intent = new Intent(SelectCar.this, FarmListRoute.class);
                            intent.putExtra("ROUTE", routeName);
                            intent.putExtra("ROUTEID", routeId + "");
                            intent.putExtra("SEQUENCE", "Start");
                            intent.putExtra("isResumed", true);
                            intent.putExtra("date", pData.getOrderDate());
                            sharedprefrenceManager.setRouteName(routeName);
                            sharedprefrenceManager.setShouldNavigateToMap(true);
                            startActivity(intent);
                            finish();
                            recyclerView.setVisibility(View.GONE);
                            //    resume_ride.setVisibility(Vi vbvftfew.VISIBLE);
                            logout.setVisibility(View.GONE);
                            getStartedRide(parentId + "", action);
                        } else if (action == 1) {
                            getStartedRide(parentId + "", action);
                            //Log.d("Analysis__","Inside line 657 farmid is "+farmId);
                            sharedprefrenceManager.setShouldNavigateToMap(true);
                            sharedprefrenceManager.setIsStart("1");

                            startActivity(new Intent(SelectCar.this, ImprovedFarmList.class)
                                    .putExtra("orderdate", pData.getOrderDate())
                                    .putExtra("isResumeRide", true));
                            finish();
                        } else if (action == 0) {
                            getStartedRide(parentId + "", action);
                            recyclerView.setVisibility(View.GONE);
                            logout.setVisibility(View.GONE);
                        } else {

                            recyclerView.setVisibility(View.GONE);
                            //     resume_ride.setVisibility(View.VISIBLE);
                            logout.setVisibility(View.VISIBLE);
                        }


                    }

                }
                else if (response.code() == 401) {
                    shouldLogout.logout(SelectCar.this);
                } else
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        //  errorDialog(loginError.getMessage());
//                        System.err.println("In Select Car Show permission else part status not 200");
                        Log.e("Data  Show Permission" , loginError.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Data ExceptionShow P", e.toString());
                    }
            }

            @Override
            public void onFailure(Call<PermissionModel> call, Throwable t) {
                Log.e("errorratro", t.getMessage());
                System.err.println("In Select Car Show permission Failure");
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // actionAfterPermissionGranted();
                    Intent serviceIntent = new Intent(this, LoginUpdateService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this.startForegroundService(serviceIntent);
                    }
                }
            }
        }
    }
    private void checkRuntimePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.FOREGROUND_SERVICE_LOCATION}, LOCATION_PERMISSION);
        }
          //  actionAfterPermissionGranted();

    }

    private void getLocation(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location locationn) {
                            if (locationn != null) {
                                location= locationn;
                                 latitude = locationn.getLatitude();
                                 longitude = locationn.getLongitude();
                                Log.d("LOCATION", "Lat: " + latitude + ", Lng: " + longitude);
                            }
                        }
                    });
        }
    }

}
