package com.sc.aipdriver.activities.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


import static com.sc.aipdriver.activities.IsInternetAvailableKt.isInternetAvailable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.NetworkListener;
import com.sc.aipdriver.activities.adapters.FarmListAdapterByRote;
import com.sc.aipdriver.activities.adapters.ReceiptAdapter;
import com.sc.aipdriver.activities.dialogs.DatePickerFullScreenDialog;
import com.sc.aipdriver.activities.dialogs.LoadTemprature;
import com.sc.aipdriver.activities.dialogs.PhotoDialog;
import com.sc.aipdriver.activities.dialogs.ShowLoading;
import com.sc.aipdriver.activities.fragments.FinishDialog;
import com.sc.aipdriver.activities.fragments.ReceiptDialog;
import com.sc.aipdriver.activities.fragments.SelectCar;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.interfaces.OnClickFarm;
import com.sc.aipdriver.activities.interfaces.OnClickSubmit;
import com.sc.aipdriver.activities.interfaces.OnFarmListChangedListener;
import com.sc.aipdriver.activities.interfaces.OnItemClick;
import com.sc.aipdriver.activities.interfaces.OnMailDone;
import com.sc.aipdriver.activities.interfaces.OnReceiptClick;
import com.sc.aipdriver.activities.interfaces.OnStartDragListener;
import com.sc.aipdriver.activities.interfaces.SimpleItemTouchHelperCallback;
import com.sc.aipdriver.activities.models.ApiResponse;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.EmailRequest;
import com.sc.aipdriver.activities.models.FarmData;
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData;
import com.sc.aipdriver.activities.models.LatLongData;
import com.sc.aipdriver.activities.models.LiRoutePlannerDetail;
import com.sc.aipdriver.activities.models.PermissionData;
import com.sc.aipdriver.activities.models.PermissionModel;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.models.ReceiptDetails;
import com.sc.aipdriver.activities.models.ReceiptResponse;
import com.sc.aipdriver.activities.models.ReceiptResponseMain;
import com.sc.aipdriver.activities.models.RideFinishRequest;
import com.sc.aipdriver.activities.models.RouteLog;
import com.sc.aipdriver.activities.models.RoutePlannerDetail;
import com.sc.aipdriver.activities.models.StartDataSend;
import com.sc.aipdriver.activities.models.UpdateFarmData;
import com.sc.aipdriver.activities.models.VehicleListData;
import com.sc.aipdriver.activities.otherclasses.GeoTask;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.otherclasses.ShouldLogout;
import com.sc.aipdriver.activities.otherclasses.Utils;
import com.sc.aipdriver.activities.room.AppDatabase;
import com.sc.aipdriver.activities.room.EmailSyncEntity;
import com.sc.aipdriver.activities.services.ForegroundLocationService;
import com.sc.aipdriver.activities.worker.SyncScheduler;
import com.sc.aipdriver.activities.worker.SyncStatusManager;
import com.squareup.picasso.Picasso;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import cn.pedant.SweetAlert.SweetAlertDialog;
import kotlin.text.Regex;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;












































public class FarmListRoute extends AppCompatActivity implements OnClickSubmit, ReceiptDialog.OnReceiptUpdate,ReceiptDialog.UploadReceiptImages,OnReceiptClick, OnItemClick, FinishDialog.UploadImages, FinishDialog.SendData, FarmListAdapterByRote.AdapterbyRoute, LocationListener, OnClickFarm, FarmListAdapterByRote.CheckFormEntry, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ReceiptDialog.OnReceiptSubmit, OnFarmListChangedListener, OnMailDone,OnStartDragListener {

//public class FarmListRoute extends AppCompatActivity implements FarmListAdapterByRote.AdapterbyRoute, OnStartDragListener {


    RecyclerView recyclerView;
    RecyclerView recyclerViewReceipt;
    boolean frist=true;
    ArrayList<LatLng> latLngslist;
    String endodometer = "";
    String totalmiles = "";
    private SweetAlertDialog errorDialog;
    String endoil = "";
    Boolean shouldWaitForUpload = false;
    Button start;
    int previous = 0;
    Boolean startImage = false;
    Location location;
    String sendlog = "true";
    long diffMinutes = 0;
    boolean backEnabled = true;
    boolean isResume = false;
    boolean resultcame = false;
    TextView tvDate;
    TextView tvOffline;
    Button save, next;
    FarmListAdapterByRote farmListAdapterByRote;
    MultipartBody.Part img1, img2, img3, img4;
    ApiInterface apiService;
    ApiInterface apiService2;
    ApiInterface apiService3;
    String rcptId = "0";
    View bgTransparent;
    ImageView ivClose;
    ConstraintLayout clLargeImage;
    ImageView ivLargeView;
    Boolean isAlreadyDone = false;
    public boolean shouldUpdateRouteAfterRefresh = false;
    private static boolean isMailTriggering = false;
    double latitude = 0.0;
    double longitude = 0.0;
    private TextView tvCancel;
    private static final int LOCATION_PERMISSION = 101;
    List<RoutePlannerDetail> alDeliveryPendingFarms;
    FusedLocationProviderClient fusedLocationClient;
    Gson gson;
    ShouldLogout shouldLogout = null;
    SharedprefrenceManager sharedprefrenceManager;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    private void setupNetworkListener() {

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d("FARMS__", "Line 249 ");

                runOnUiThread(() -> {
                    Log.d("NETWORK", "Internet Available");

                    // getFarmsByRoute();
                    //   Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);

                runOnUiThread(() -> {
                    Log.d("NETWORK", "Internet Lost");
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network,
                                              @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);

                boolean unmetered =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);

                Log.d("NETWORK", "Unmetered network: " + unmetered);
            }
        };

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    List<FarmData> farmDataList;
    List<PriorityFarmData> farmlist;

    int rideIdd = 0;
    int routeIdd = 0;
    int parentIdd = 0;
    int farmIdd = 0;
    LinearLayout rootView;
    String farmnamee = "";
    String routeNamee = "";
    int actionn = 0;
    Boolean backPressed = false;
    BottomSheetDialogFragment bottomSheetDialogFragment;
    BottomSheetDialogFragment rcptbottomSheetDialogFragment;
    BottomSheetDialogFragment loadBottomSheetDialogFragment = null;
    PhotoDialog bottomSheetPhoto = null;
    ArrayList<UpdateFarmData> priorityFarmList;
    String route, routeid, sequence;
    Boolean isResumed = false;
    StartDataSend startDataSend;
    List<Address> addresses1 = new ArrayList<>();
    ImageView ivBack;
    ImageView ivRefresh;
    UpdateFarmData updateFarmData;
    HashMap<String, RoutePlannerDetail> latLongDataHashMap;
    ItemTouchHelper mItemTouchHelper;
    ShowLoading showLoading;
    ArrayList<ReceiptResponse> alReceipts = new ArrayList<>();
    ReceiptAdapter receiptAdapter = null;
    private String imgPath1 = "";
    private String imgPath2 = "";
    private String imgPath3 = "";
    private String imgPath4 = "";
    private String imgPath5 = "";
    private String imgPath6 = "";
    private String imgPath7 = "";
    private String imgPath8 = "";
    private String date = "";
    Geocoder geocoder;
    TextView tvAddRcpt;
    List<Address> addresses;
    List<String> entryformlist;

    boolean rideStart = false;
    int rideId = 0;
    // client route
    // private final double SkyLabLatitude = 45.696146;
    // private final double SkyLabLongitude = -95.923010;
    // end client route

    // demo route
    boolean isFinised = false;
    boolean isPaused = false;
    boolean shouldSatrt = false;
    TextView title;
    private final double SkyLabLatitude = 28.440766;
    private final double SkyLabLongitude = 77.070499;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Date lastSent, current;

    private void GetUserLocation() {
        System.err.println("In GetUserLocation    " + "  1493 LN");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(3000);
            mLocationRequest.setFastestInterval(3000);
            mLocationRequest.setSmallestDisplacement(10);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            this.location = loc;

            // get all farms here...
            if (location != null) {
                if (sendlog.equals("true")) {
                    //    sendLog("Start");
                    sendlog = "false";
                }
                //   getFarmInfo();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(MapsActivityNew.this, "Location Changed", Toast.LENGTH_SHORT).show();
        // getFarmsRoute();
        System.err.println("In Location Changed    " + "  1394 LN");
        this.location = location;
        //Log.d("Analysis__", "Location is" + location);
        //Toast.makeText(MapsActivityNew.this, "Location Changed " + location.getLatitude() + " -- " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        Log.e("location", "" + location);

        if (sendlog.equalsIgnoreCase("true")) {
            if (location != null) {
                //  sendLog("Start");
                sendlog = "false";
            }
        }

        if (!isFinised) {
            current = Calendar.getInstance().getTime();
            System.err.println("current time " + current);

            if (lastSent != null) {
                long diff = current.getTime() - lastSent.getTime();
                diffMinutes = diff / (60 * 1000) % 60;
            }

            // Do the stuff
            System.err.println("Diiference ====  " + diffMinutes);
            //Toast.makeText(MapsActivityNew.this, "Time Interval " + diffMinutes, Toast.LENGTH_SHORT).show();
            if ((location != null && !isPaused)) {
                if (diffMinutes >= 1 || diffMinutes == 0) {
//                    Toast.makeText(MapsActivityNew.this, "On Update " + location.getLatitude() + " -- " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    //     updateRouteStatus();
                    //    sendLog("Moving");
                    //    checkArrivedStatus(true);
//                        }
                }
            }
            // handler1.postDelayed(this, 120000);


//        if ((location != null && !isPaused)) {
////                        if (diffMinutes>=2||diffMinutes==0) {
//            sendLog("Moving");
//            // checkArrivedStatus(true);
////                        }
//        }


//        new checkRoute().execute();

        }
    }

    private void sendLog(String status) {
        System.err.println("In Send Log " + "  812 LN");
        if (!isFinised) {
            try {
                RouteLog routeLog = new RouteLog();
//                routeLog.setLat("" + location.getLatitude());
//                routeLog.setLng("" + location.getLongitude());

                if (status.equalsIgnoreCase("Moving")) {
                    System.err.println("INside sendLog Moving" + " 821 LN");
                    routeLog.setAction("Moving");
//                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
                    lastSent = Calendar.getInstance().getTime();
                    System.err.println("lastSent =   " + lastSent + "  826LN");
                } else if (status.equalsIgnoreCase("Start")) {
                    System.err.println("In SendLog  Start  " + "  839 LN");
                    routeLog.setAction("Start");
//                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
                } else if (status.equalsIgnoreCase("Pause")) {
                    System.err.println("In SendLog  Pause  " + "  839 LN");
                    routeLog.setAction("Pause");

                } else if (status.equalsIgnoreCase("Arrived")) {
                    System.err.println("In SendLog  Arrived  " + "  835 LN");
                    routeLog.setAction("Arrived");
//                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
                } else if (status.equalsIgnoreCase("Cancel")) {
                    System.err.println("In SendLog  Cancel  " + "  839 LN");
                    routeLog.setAction("Cancel");
                    isFinised = true;
                    stopService();

//                    routeLog.setLat("" + demoLat);
//                    routeLog.setLng("" + demoLong);
                    routeLog.setLat("" + SkyLabLatitude);
                    routeLog.setLng("" + SkyLabLongitude);
                } else if (status.equalsIgnoreCase("End")) {
                    System.err.println("In SendLog  End  " + "  839 LN");
                    routeLog.setAction("End");
//                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
                }

                try {

                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();

                    routeLog.setCity(city);
                    routeLog.setAddress(address);
                    routeLog.setCountry(country);
                    routeLog.setState(state);
                    //sendrequest(markersList);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                routeLog.setDriverId(sharedprefrenceManager.getUserId());
                routeLog.setRideId(sharedprefrenceManager.getRideId());
                routeLog.setRouteId(sharedprefrenceManager.getRouteId());
                routeLog.setUID(sharedprefrenceManager.getUserId());
                routeLog.setVehicleId(sharedprefrenceManager.getVehicleId());
                System.err.println("In SendLog  Sending logs  " + "  880 LN");
                Call<CommonError> call = apiService.sendLog(routeLog, sharedprefrenceManager.getToken());
                call.enqueue(new Callback<CommonError>() {
                    @Override
                    public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                        if (response.code() == 200) {
                            System.err.println("Send Log Successfully  " + "  886 LN");
                            Log.e("Datatrue", "" + response.body().getMessage());
                        } else
                            try {
                                CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                                //  errorDialog(loginError.getMessage());
                                Log.e("Data", loginError.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Data", e.toString());
                            }
                    }

                    @Override
                    public void onFailure(Call<CommonError> call, Throwable t) {
                        Log.e("errorratro", t.getMessage());
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // end demo route


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkRuntimePermission();

    }

    private void checkRuntimePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMISSION);
        } else
            actionAfterPermissionGranted();
    }

    private void actionAfterPermissionGranted() {
        System.err.println("In actionAfterPermissionGranted    " + "  1453 LN");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setSmallestDisplacement(10);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        GetUserLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(FarmListRoute.this, 100);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundLocationService.class);
        stopService(serviceIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public boolean isAnyFarmGreen() {
        for (PriorityFarmData item : farmlist) {
            if (item.getIsCompleted() == 1) {

                return true;
            }
        }
        return false;
    }

    public boolean areAllItemsLoaded(List<PriorityFarmData> someList) {
        for (PriorityFarmData item : someList) {
            if (item.getIsLoaded() == 0 && item.getIsCompleted() == 0) {
                return false;
            }
        }
        return true;
    }

    

    public boolean areAllItemsDelivered(List<PriorityFarmData> someList) {
        for (PriorityFarmData item : someList) {
            if (item.getIsCompleted() == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmlistroute);
        setupNetworkListener();

        rootView = findViewById(R.id.ll_listRoute);
        tvDate = findViewById(R.id.tvDate);
        tvOffline = findViewById(R.id.tv_offline);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });
        sharedprefrenceManager = new SharedprefrenceManager(this);

        SwitchCompat switchCompat = findViewById(R.id.switchOfflineMode);

// Set saved state
//        switchCompat.setChecked(sharedprefrenceManager.getToggleState());
        switchCompat.setVisibility(GONE);
        // Save whenever user changes it
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedprefrenceManager.setToggleState(isChecked);
            if (!isChecked){
                SyncScheduler.INSTANCE.runImmediateSync(this);
            }
        });
        shouldLogout = new ShouldLogout(this, sharedprefrenceManager);
        imgPath1 = sharedprefrenceManager.getImg1();
        imgPath2 = sharedprefrenceManager.getImg2();
        imgPath3 = sharedprefrenceManager.getImg3();
        imgPath4 = sharedprefrenceManager.getImg4();
        imgPath5 = sharedprefrenceManager.getImg5();
        imgPath6 = sharedprefrenceManager.getImg6();
        imgPath7 = sharedprefrenceManager.getImg7();
        imgPath8 = sharedprefrenceManager.getImg8();
        ivLargeView = findViewById(R.id.iv_largeview);
        ivClose = findViewById(R.id.ivClose);
        clLargeImage = findViewById(R.id.cl_largeImage);
        bgTransparent = findViewById(R.id.bgTrasnparent);
        tvCancel = findViewById(R.id.tv_cancel);
        recyclerView = findViewById(R.id.recyclerview);
        tvAddRcpt = findViewById(R.id.tv_add_rcpt);
        recyclerViewReceipt = findViewById(R.id.recyclerviewreceipt);
        ivBack = findViewById(R.id.iv_back);
        ivRefresh = findViewById(R.id.refresh);
        start = findViewById(R.id.endRoute);
        // start.setVisibility(VISIBLE);
        save = findViewById(R.id.save);
        next = findViewById(R.id.next);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFarmsByRoute();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clLargeImage.setVisibility(GONE);
                bgTransparent.setVisibility(GONE);
                rootView.setVisibility(VISIBLE);
            }
        });

        tvAddRcpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetAvailable(FarmListRoute.this)) {
                    tvAddRcpt.setEnabled(false);
                    rcptbottomSheetDialogFragment = new ReceiptDialog().instance(FarmListRoute.this, FarmListRoute.this::onReceiptSubmit, false, null, FarmListRoute.this, FarmListRoute.this);
                    rcptbottomSheetDialogFragment.show(getSupportFragmentManager(), rcptbottomSheetDialogFragment.getTag());
                    tvAddRcpt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvAddRcpt.setEnabled(true);
                        }
                    }, 1000); // 1 second delay
                } else {
                    Toast.makeText(FarmListRoute.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        title = findViewById(R.id.tv_title);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backPressed = true;
                if (farmlist.size() > 0) {
                    if (isAnyFarmGreen()) {
                        Toast.makeText(FarmListRoute.this, "Please complete or cancel the ride first.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                checkRide();


            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (areAllPhotosUploaded() == 1) {
                    cancelRide();
                } else {
                    showPendingImagesDialog(true);
                }

            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (getIntent() != null) {
            route = getIntent().getStringExtra("ROUTE");
            date = getIntent().getStringExtra("date");
            if (date == null || date.trim().isEmpty()) {
                date = sharedprefrenceManager.getDate();
            } else {
                String normalizedDate = formatToMMDDYYYY(date);
                sharedprefrenceManager.setDate(normalizedDate);
                sharedprefrenceManager.setImprovedDate(normalizedDate);
                date = normalizedDate;
            }

            sequence = getIntent().getStringExtra("SEQUENCE");
            routeid = getIntent().getStringExtra("ROUTEID");
            if (routeid != null && !routeid.isEmpty()) {
                sharedprefrenceManager.setRouteId(Integer.valueOf(routeid));
            }
            isResumed = getIntent().getBooleanExtra("isResumed", false);
        }
        //Log.d("Analysis__", routeid + " Route Id hehe");
        title.setText("" + route);
        farmDataList = new ArrayList<>();
        farmlist = new ArrayList<>();
        priorityFarmList = new ArrayList<>();
        System.err.println("In FarmListRout  ");
//
        geocoder = new Geocoder(this, Locale.getDefault());

        showLoading = new ShowLoading(this);

        startDataSend = new StartDataSend();

        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        apiService2 = ApiClient.getClient2().create(ApiInterface.class);
        apiService3 = ApiClient.getHttp1Client(FarmListRoute.this).create(ApiInterface.class);
        gson = new GsonBuilder().setPrettyPrinting().create();
        getLocation();
        recyclerView.setLayoutManager(new LinearLayoutManager(FarmListRoute.this));
        recyclerViewReceipt.setLayoutManager(new LinearLayoutManager(FarmListRoute.this));
        receiptAdapter = new ReceiptAdapter(this, this, alReceipts);
        farmListAdapterByRote = new FarmListAdapterByRote(this, FarmListRoute.this, farmlist, route, routeid, sequence, this, this, this, this, this);
        //farmListAdapterByRote = new FarmListAdapterByRote(this, FarmListRoute.this, farmlist, route, routeid, sequence, this, this, this, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(farmListAdapterByRote);
//        mItemTouchHelper = new ItemTouchHelper(callback);
//        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerViewReceipt.setAdapter(receiptAdapter);
        recyclerView.setAdapter(farmListAdapterByRote);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priorityFarmList.size() > 0)
                    postFarmsPriority();
                else
                    errorDialog("Set Farm Priority Frist");
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sharedprefrenceManager.isSyncMode()) {
                    if(!isInternetAvailable(FarmListRoute.this)) {
                        Toast.makeText(FarmListRoute.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (areAllItemsLoaded(farmlist)) {
                    v.setEnabled(false); // ✅ Disable button immediately to prevent duplicate clicks

                    // ✅ Use a flag to ensure the "YOUR ORDER'S ON THE WAY" email is sent only once per route/date
                    String currentDate = tvDate.getText().toString().replace("Orders for ", "").trim();
                    String currentRouteId = sharedprefrenceManager.getRouteId();

                    Log.d("DEBUG_EMAIL", "[" + System.currentTimeMillis() + "] Next clicked. isMailTriggering=" + isMailTriggering + 
                          ", routeId=" + currentRouteId + ", date=" + currentDate);

                    if (!isMailTriggering && !currentDate.isEmpty()) {
                        boolean alreadySent = sharedprefrenceManager.isMailSent("AllDispatch", currentRouteId, "0", currentDate);
                        Log.d("DEBUG_EMAIL", "[" + System.currentTimeMillis() + "] isMailSent check: " + alreadySent);

                        if (!alreadySent) {
                            Log.d("DEBUG_EMAIL", "[" + System.currentTimeMillis() + "] === TRIGGERING AllDispatch API START ===");
                            isMailTriggering = true; 
                            
                            // ✅ Mark as sent locally IMMEDIATELY
                            sharedprefrenceManager.setMailSent("AllDispatch", currentRouteId, "0", currentDate, true);
                            
                            EmailRequest emailRequest = new EmailRequest();
                            emailRequest.setDriverid(sharedprefrenceManager.getDriverID());
                            emailRequest.setParentid(sharedprefrenceManager.getParentId());
                            emailRequest.setRouteId(currentRouteId);
                            emailRequest.setVehicleId(sharedprefrenceManager.getVehicleId());
                            emailRequest.setLat(String.valueOf(latitude));
                            emailRequest.setLng(String.valueOf(longitude));

                            apiService.AllDispatch(emailRequest, sharedprefrenceManager.getToken()).enqueue(new Callback<CommonError>() {
                                @Override
                                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                                    Log.d("DEBUG_EMAIL", "[" + System.currentTimeMillis() + "] AllDispatch API Response: " + response.code());
                                    isMailTriggering = false;
                                }

                                @Override
                                public void onFailure(Call<CommonError> call, Throwable t) {
                                    Log.e("DEBUG_EMAIL", "[" + System.currentTimeMillis() + "] AllDispatch API Failure: " + t.getMessage());
                                    v.setEnabled(true);
                                    isMailTriggering = false;
                                }
                            });
                        } else {
                            Log.d("DEBUG_EMAIL", "[" + System.currentTimeMillis() + "] Skipping API: Already sent today.");
                        }
                    }

                    startActivity(new Intent(FarmListRoute.this, ImprovedFarmList.class).putExtra("orderdate", tvDate.getText().toString().trim()));
                    finishAffinity();
                } else {
                    Toast.makeText(FarmListRoute.this, "Please load all the quantity and temprature details.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetAvailable(FarmListRoute.this)) {
                    start.setEnabled(false);
                    if (actionn == 1) {
                        Toast.makeText(FarmListRoute.this, "Please complete started ride first.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (areAllPhotosUploaded() == 1) {
                            bottomSheetDialogFragment = new FinishDialog().instance("", FarmListRoute.this, FarmListRoute.this);
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        } else {
                            showPendingImagesDialog(false);
                        }
                    }
                    // finishCompleteRide();
                    start.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start.setEnabled(true);
                        }
                    }, 1000); // 1 second delay
                } else {
                    Toast.makeText(FarmListRoute.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //  getReceiptData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    () -> {
                        backPressed = true;
                        if (isAnyFarmGreen()) {
                            Toast.makeText(FarmListRoute.this, "Please complete or cancel the ride first.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        checkRide(); // ✅ show date picker
                    }
            );
        }
        SyncStatusManager.INSTANCE.getSyncCompleted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean synced) {

                if (synced != null && synced) {

                    Log.e("SYNC", "Data synced refresh UI");
                    Log.d("FARMS__", "Line 849 ");
                    // refresh your UI here
                    Log.d("Analysis__", "getFarmsByRoute onCreate after sync");
                    getFarmsByRoute();

                }
            }
        });
        Log.d("Analysis__", "sharedprefrenceManager.getImprovedDate() is " + sharedprefrenceManager.getImprovedDate());


    }


    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String formatToMMDDYYYY(String input) {
        if (input == null || input.trim().isEmpty()) return input;

        String[] inputFormats = {
                "yyyy-M-d",
                "yyyy/M/d",
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "M/d/yyyy",
                "MM/dd/yyyy"
        };

        for (String format : inputFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                sdf.setLenient(false); // strict parsing

                Date date = sdf.parse(input);
                if (date != null) {
                    SimpleDateFormat outputFormat =
                            new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    return outputFormat.format(date);
                }
            } catch (Exception ignored) {
            }
        }

        // If no format matched, return original input
        return input;
    }

    public static String formatToYYYYMMDD(String input) {
        if (input == null || input.trim().isEmpty()) return input;

        String[] inputFormats = {
                "yyyy-M-d",
                "yyyy/M/d",
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "M/d/yyyy",
                "MM/dd/yyyy",
                "MM-dd-yyyy"
        };

        for (String format : inputFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                sdf.setLenient(false);

                Date date = sdf.parse(input);
                if (date != null) {
                    SimpleDateFormat outputFormat =
                            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    return outputFormat.format(date);
                }
            } catch (Exception ignored) {
            }
        }
        return input;
    }

    private void getAllFarms() {


            if (isOnlineMode() && !isInternetAvailable(FarmListRoute.this)) {
                showNoInternetRetryDialog(this::getAllFarms);
                return;
            }


//        showLoading.show();
            String rawDate = resolveOrderDateForApi();
            Log.d("Analysis__", "rawDate date for API: " + rawDate);
            String formattedDate = formatToMMDDYYYY(rawDate);

            Log.d("Analysis__", "Formatted date for API: " + formattedDate);

            Call<BaseResponse<List<ImprovedPriorityFarmData>>> call =
                    apiService.getAllFarmList(
                            routeid,
                            sharedprefrenceManager.getParentId(),
                            formattedDate,  // ✅ use formatted date here
                            sharedprefrenceManager.getDriverID(),
                            sharedprefrenceManager.getToken()
                    );
            call.enqueue(new Callback<BaseResponse<List<ImprovedPriorityFarmData>>>() {

                @Override
                public void onResponse(
                        Call<BaseResponse<List<ImprovedPriorityFarmData>>> call,
                        Response<BaseResponse<List<ImprovedPriorityFarmData>>> response
                ) {

                    if (response.code() == 200) {

//                    showLoading.dismiss();

                        System.err.println("getFarmsByRoute  " +
                                response.body().getData().size());

                        List<ImprovedPriorityFarmData> farms = response.body()
                                .getData()
                                .stream()
                                .filter(farm -> farm.getRemove() == 0)
                                .collect(Collectors.toList());

                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase db = AppDatabase.Companion.getDatabase(FarmListRoute.this);

                            int currentFid = 0;
                            try {
                                currentFid = Integer.parseInt(sharedprefrenceManager.getFID());
                            } catch (Exception ignored) {}

                            if (currentFid != 0) {
                                for (ImprovedPriorityFarmData farm : farms) {
                                    if (farm.getId() == currentFid) {
                                        farm.setIsActiveRide(1);
                                    }
                                }
                            }

                            db.improvedPriorityFarmDao().clearImprovedFarms();
                            db.improvedPriorityFarmDao().insertImprovedFarms(farms);
                        });

                    } else {

                        try {
                            showLoading.dismiss();
                            CommonError loginError = new Gson().fromJson(
                                    response.errorBody().string(),
                                    CommonError.class
                            );

                            System.err.println(" Farm List Error Message "
                                    + loginError.getMessage() + " ");

                            Log.e("FarmsError", loginError.getMessage());

                        } catch (IOException e) {

//                        showLoading.dismiss();
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(
                        Call<BaseResponse<List<ImprovedPriorityFarmData>>> call,
                        Throwable t
                ) {

                    Log.e("FarmsFail", t.getMessage());

//                showLoading.dismiss();

                    if (sharedprefrenceManager.isSyncMode()) {
                        loadFromDB();
                    } else {
                        showNoInternetRetryDialog(() -> FarmListRoute.this.getAllFarms());
                    }
                }
            });


    }

    private String resolveOrderDateForApi() {
        String rawDate = sharedprefrenceManager.getDate();

        if (rawDate == null || rawDate.trim().isEmpty()) {
            String intentDate = getIntent() != null ? getIntent().getStringExtra("date") : null;
            if (intentDate != null && !intentDate.trim().isEmpty()) {
                rawDate = intentDate;
            }
        }

        if (rawDate == null || rawDate.trim().isEmpty()) {
            rawDate = sharedprefrenceManager.getImprovedDate();
        }

        if (rawDate == null || rawDate.trim().isEmpty()) {
            rawDate = date;
        }

        if (rawDate != null && !rawDate.trim().isEmpty()) {
            String normalized = formatToMMDDYYYY(rawDate);
            sharedprefrenceManager.setDate(normalized);
            sharedprefrenceManager.setImprovedDate(normalized);
            return normalized;
        }

        Log.e("DATE__", "Order date is empty in prefs and intent");
        return "";
    }

    private void showPendingImagesDialog(Boolean isCancel) {
        String negative = "End Ride Anyway";
        String message = "Some images are not uploaded yet. You can upload them now or end the ride anyway.";
        if (isCancel) {
            message = "Some images are not uploaded yet. You can upload them now or cancel the ride annyway.";
            negative = "Cancel Ride Anyway";
        }
        new AlertDialog.Builder(this)
                .setTitle("Pending Images")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Upload Images", (dialog, which) -> {
                    // call your upload function here
                    dialog.dismiss();
                })

                .setNegativeButton(negative, (dialog, which) -> {
                    if (isCancel) {
                        cancelRide();
                    } else {
                        bottomSheetDialogFragment =
                                new FinishDialog().instance("", FarmListRoute.this, FarmListRoute.this);

                        bottomSheetDialogFragment.show(
                                getSupportFragmentManager(),
                                bottomSheetDialogFragment.getTag()
                        );
                    }

                })

                .show();
    }

    public int areAllPhotosUploaded() {
        for (PriorityFarmData item : farmlist) {
            if (item.getIsCompleted() == 1) {
                if (item.getIsPhotoUploaded() == 0) {
                    return 0;
                }
            }
        }
        return 1;
    }

    @Override
    public void onBackPressed() {

        backPressed = true;
        if (isAnyFarmGreen()) {
            Toast.makeText(FarmListRoute.this, "Please complete or cancel the ride first.", Toast.LENGTH_SHORT).show();
            return;
        }

        checkRide();
        //  goToDateSelection();

    }


    private void goToDateSelection() {
        date = sharedprefrenceManager.getDate();
        Log.d("Analysis__", "Showing date picker " + date);
        DatePickerFullScreenDialog dialog =
                DatePickerFullScreenDialog.newInstance(date, route);
        dialog.setListener(date -> {
            Log.d("Date__", "selected date is :" + date);
            Intent intent = new Intent(FarmListRoute.this, FarmListRoute.class);
            intent.putExtra("ROUTE", route);
            intent.putExtra("ROUTEID", routeid);
            intent.putExtra("SEQUENCE", "Start");
            intent.putExtra("isResumed", isResumed);
            intent.putExtra("date", date);
            sharedprefrenceManager.setDate(formatToMMDDYYYY(date));
            sharedprefrenceManager.setRouteName(route);
            startActivity(intent);
            this.finishAffinity();
        });
        dialog.show(getSupportFragmentManager(), "DatePicker");
    }

    private void handleBackPress() {
        if (backEnabled) {
            Log.d("Analysis__", "GOing to finish");
            //   onBackPressed();
            Log.d("previous__", "previous is " + previous);
            if (previous == 1) {
                goToDateSelection();
            }
//            else{
//                ivBack.setVisibility(GONE);
//            }
            //FinishData("", "", "");
        } else {
            Toast.makeText(this, "You have to complete or cancel the current ride to go back.", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        shouldLogout.shouldLogout(true, this);

    }

    private void cancelRide() {
        Call<BaseResponse> call = apiService.cancelRide(sharedprefrenceManager.getParentId(), sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == 200) {
                    for (PriorityFarmData farm : farmlist) {
                        if (farm.getIsCompleted() == 1) {
                            if (farm.getIsPhotoUploaded() == 0 && farm.getIsEmailSent() == 0) {
                                FinishDataMail(farm.getId() + "", routeid, farm.getRideId() + "");
                            }
                        }
                    }
                    sharedprefrenceManager.clearRideState();
                    logout();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e("Data  errorratro", t.getMessage());
                Toast.makeText(FarmListRoute.this, "No internt Connection", Toast.LENGTH_SHORT).show();
//                System.err.println("In Select Car Vehicle  Failure" );

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    private void getReceiptData() {
        Call<ReceiptResponseMain> call = apiService.getReceipts(Integer.valueOf(sharedprefrenceManager.getParentId()), sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
        call.enqueue(new Callback<ReceiptResponseMain>() {
            @Override
            public void onResponse(Call<ReceiptResponseMain> call, Response<ReceiptResponseMain> response) {
                System.err.println("FarmListRoute send to Server RESPONSE SEND TO SERVER:::::---   " + response);
                Log.e("RESPONSE  :::::   ", response + "");

                if (response.code() == 200) {
                    ArrayList<ReceiptResponse> alRcpts = response.body().getAlReciptResponse();
                    alReceipts.clear();
                    alReceipts.addAll(alRcpts);
                    receiptAdapter.notifyDataSetChanged();
                       /* if (alRcpts.size()>0) {
                            for (int i = 0; i < alRcpts.size(); i++) {
                                ReceiptDetails receiptDetails = new ReceiptDetails();
                                receiptDetails.setFuelOdometer(alRcpts.get(i).getFuelOdometer());
                                receiptDetails.setGallon(alRcpts.get(i).getGallon());
                                receiptDetails.setWashCost(alRcpts.get(i).getWashCost());
                                receiptDetails.setFuelCost(alRcpts.get(i).getFuelCost());
                                receiptDetails.setImg(alRcpts.get(i).getRecipt_Img());
                                receiptDetails.setPricePgallon(alRcpts.get(i).getPricePgallon());
                                alReceipts.add(receiptDetails);
                            }
                            receiptAdapter.notifyDataSetChanged();
                        }*/

                } else
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        //  errorDialog(loginError.getMessage());
                        Log.e("Data", loginError.getMessage());
                        System.err.println(" sendToServer  Error:::   " + loginError);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<ReceiptResponseMain> call, Throwable t) {
                Log.e("errorratro", t.getMessage());
                System.err.println(" sendToServer  :::   failure");
            }
        });
    }

    private void uploadStartImage() {
        File file1 = null;
        File file2 = null;
        File file3 = null;
        File file4 = null;
        File file5 = null;
        File file6 = null;
        File file7 = null;
        File file8 = null;
        MultipartBody.Part img1 = null;
        MultipartBody.Part img2 = null;
        MultipartBody.Part img3 = null;
        MultipartBody.Part img4 = null;
        MultipartBody.Part img5 = null;
        MultipartBody.Part img6 = null;
        MultipartBody.Part img7 = null;
        MultipartBody.Part img8 = null;

        if (imgPath1.length() > 2) {
            //Log.d("Analysis__", "Image1 " + imgPath1);
            file1 = new File(imgPath1);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file1 = Utils.compressImageFile(this, file1);
            }
            img1 = prepareFilePart("Image1", file1);
        }

        if (imgPath2.length() > 2) {
            //Log.d("Analysis__", "Image2 " + imgPath2);
            file2 = new File(imgPath2);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file2 = Utils.compressImageFile(this, file2);
            }
            img2 = prepareFilePart("Image2", file2);
        }
        if (imgPath3.length() > 2) {
            //Log.d("Analysis__", "Image3 " + imgPath3);
            file3 = new File(imgPath3);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file3 = Utils.compressImageFile(this, file3);
            }
            img3 = prepareFilePart("Image3", file3);
        }
        if (imgPath4.length() > 2) {
            //Log.d("Analysis__", "Image2 " + imgPath4);
            file4 = new File(imgPath4);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file4 = Utils.compressImageFile(this, file4);
            }
            img4 = prepareFilePart("Image4", file4);
        }
        if (imgPath5.length() > 2) {
            //Log.d("Analysis__", "Image2 " + imgPath5);
            file5 = new File(imgPath5);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file5 = Utils.compressImageFile(this, file5);
            }
            img5 = prepareFilePart("Image5", file5);
        }
        if (imgPath6.length() > 2) {
            //Log.d("Analysis__", "Image2 " + imgPath6);
            file6 = new File(imgPath6);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file6 = Utils.compressImageFile(this, file6);
            }
            img6 = prepareFilePart("Image6", file6);
        }
        if (imgPath7.length() > 2) {
            //Log.d("Analysis__", "Image2 " + imgPath7);
            file7 = new File(imgPath7);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file7 = Utils.compressImageFile(this, file7);
            }
            img7 = prepareFilePart("Image7", file7);
        }
        if (imgPath8.length() > 2) {
            //Log.d("Analysis__", "Image2 " + imgPath8);
            file8 = new File(imgPath8);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file8 = Utils.compressImageFile(this, file8);
            }
            img8 = prepareFilePart("Image8", file8);
        }

        if (imgPath1.length() > 2 || imgPath2.length() > 2 || imgPath3.length() > 2 || imgPath4.length() > 2 || imgPath5.length() > 2 || imgPath6.length() > 2 || imgPath7.length() > 2 || imgPath8.length() > 2) {
            uploadStartImages(img1, img2, img3, img4, img5, img6, img7, img8);
        }
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void getLocation() {
        Log.d("LOCATION__", "Getting location...");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location locationn) {
                                    if (locationn != null) {
                                        Log.d("LOCATION__", "Getting location...");
                                        location = locationn;
                                        latitude = locationn.getLatitude();
                                        longitude = locationn.getLongitude();
                                        Log.d("LOCATION__", "Lat: " + latitude + ", Lng: " + longitude);
                                        Log.d("Location", " isResumed "+isResumed +" parentid "+sharedprefrenceManager.getParentId() );
                                        if (isResumed && !sharedprefrenceManager.getParentId().equals("0")) {



                                        } else {
                                            Log.d("LOCATION__", "Sending First");

                                            Log.d("Analysis__", "Sending First");
                                            sendFirst();
                                        }
                                    }
                                }
                            }
                    );

        }else{
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            onBackPressed();

        }
    }

    public void FinishData(String endodometer, String totalmiles, String endoil) {
//        sendLog("End");
        try {
            String address = "";
            String city = "";
            String state = "";
            String country = "";
            if (location != null) {
                addresses1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                address = addresses1.get(0).getAddressLine(0);
                city = addresses1.get(0).getLocality();
                state = addresses1.get(0).getAdminArea();
                country = addresses1.get(0).getCountryName();
            }

            RideFinishRequest rideFinishRequest = new RideFinishRequest();
            rideFinishRequest.setAction("End");
            //Log.d("Analysis__", "getParent id is " + sharedprefrenceManager.getParentId());
            rideFinishRequest.setRideId(sharedprefrenceManager.getParentId() + "");
            //Log.d("Analysis__", "getRide id is " + sharedprefrenceManager.getRideId());
            rideFinishRequest.setUID(sharedprefrenceManager.getDriverID());
            rideFinishRequest.setLat(latitude + "");
            rideFinishRequest.setLng(longitude + "");
            rideFinishRequest.setEndOdometer(endodometer);
            rideFinishRequest.setEndOilPercent(endoil);
            rideFinishRequest.setTotalMiles(totalmiles);
            rideFinishRequest.setAddress(address);
            rideFinishRequest.setState(state);
            rideFinishRequest.setCity(city);
            rideFinishRequest.setRouteId(routeid + "");
            rideFinishRequest.setCountry(country);
            //Log.d("Analysis__", "going to finish id is " + new Gson().toJson(rideFinishRequest));

            Call<CommonError> call = apiService.rideFinish(rideFinishRequest, sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
            call.enqueue(new Callback<CommonError>() {
                @Override
                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                    if (response.code() == 200) {
                        //  Log.e("ta", response.body());
                        for (PriorityFarmData farm : farmlist) {
                            if (farm.getIsCompleted() == 1) {
                                if (farm.getIsPhotoUploaded() == 0 && farm.getIsEmailSent() == 0) {
                                    FinishDataMail(farm.getId() + "", routeid, farm.getRideId() + "");
                                }
                            }
                        }
                        sharedprefrenceManager.clearRideState();
                        shouldLogout.shouldLogout(true, FarmListRoute.this);
                    } else
                        try {

                            CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                            //  errorDialog(loginError.getMessage());
                            Log.e("Data", loginError.getMessage());
                        } catch (Exception e) {

                            e.printStackTrace();
                            Log.e("Data", e.toString());
                        }

                }

                @Override
                public void onFailure(Call<CommonError> call, Throwable t) {

                    Log.e("errorratro", t.getMessage());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showImageDialog(String imageUrl) {
        if (imageUrl != null && imageUrl.length() > 6) {
            rootView.setVisibility(GONE);
            clLargeImage.setVisibility(VISIBLE);
            bgTransparent.setVisibility(VISIBLE);

            Picasso.get()
                    .load(imageUrl)
                    .into(ivLargeView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("ImageDialog", "Image loaded successfully");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("ImageDialog", "Failed to load image", e);
                        }
                    });
        }
    }


    private void sendFirst() {

        ArrayList<LiRoutePlannerDetail> liRoutePlannerDetails = new ArrayList();
        //Log.d("Analysis__", "sending first");
        startDataSend.setDriverId(sharedprefrenceManager.getDriverID());
        startDataSend.setRoutId(sharedprefrenceManager.getRouteId());
        Log.d("Meter__", "get car odometer is " + sharedprefrenceManager.getCarodometer());
        startDataSend.setStartOdometer(sharedprefrenceManager.getCarodometer());
        startDataSend.setStartOilPercent(sharedprefrenceManager.getBegningOil());
        startDataSend.setUID(sharedprefrenceManager.getDriverID());
        startDataSend.setVehicleId(sharedprefrenceManager.getVehicleId());
        startDataSend.setLat(latitude + "");
        startDataSend.setLng(longitude + "");
        startDataSend.setAction("Start");
        LiRoutePlannerDetail details = new LiRoutePlannerDetail();
        details.setFIRMID("");
        details.setTemperatureOfSemenLoaded("");
        details.setNumberOfBagsLoaded("");
        liRoutePlannerDetails.add(details);
        startDataSend.setLiRoutePlannerDetail(liRoutePlannerDetails);

        Call<CommonError> call = apiService2.sendData(startDataSend, sharedprefrenceManager.getToken());
        call.enqueue(new Callback<CommonError>() {
            @Override
            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                System.err.println("FarmListRoute send to Server RESPONSE SEND TO SERVER:::::---   " + response);
                Log.e("RESPONSE  :::::   ", response + "");

                if (response.code() == 200) {
                    Log.e("Data Send to server", "" + response.body().getMessage());
                    System.err.println("RESPONSE send server route id:::::---   " + response.body().getData());

                    rideStart = true;
                    //  start.setText("Resume");
                    String parentid = response.body().getData();
                    sharedprefrenceManager.setParentId(Integer.valueOf(parentid));
                    //Log.d("Analysis__", "Parent id isssss " + sharedprefrenceManager.getParentId());
                    Log.d("Analysis__", "getFarmsByRoute SendFirst");
                    Log.d("FARMS__", "Line 1413 ");
                    getFarmsByRoute();
                    uploadStartImage();
                    sendRideStatus(1, "Start");
                } else
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        //  errorDialog(loginError.getMessage());
                        Log.e("Data", loginError.getMessage());
                        System.err.println(" sendToServer  Error:::   " + loginError);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<CommonError> call, Throwable t) {
                Log.e("errorratro", t.getMessage());
                System.err.println(" sendToServer  :::   failure");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("SYNC", "Data synced refresh UI 1290");
        Log.d("Analysis__", "getFarmsByRoute onResume");
        //   getFarmsByRoute();
        backPressed = false;

        checkRide();
        if (shouldLogout != null) {
            // shouldLogout.shouldLogout();
        }
    }

    private void sendRideStatus(int statusId, String statusName) {
        if (!sharedprefrenceManager.getToggleState()) {
            showLoading.show();
            System.err.println("Send Ride Status    " + sharedprefrenceManager.getDriverID() + "   " + sharedprefrenceManager.getVehicleId());
            Call<CommonError> call = apiService.postVehicleStatus(sharedprefrenceManager.getDriverIdInt(), sharedprefrenceManager.getVehicleIdInt(), statusId, statusName);
            call.enqueue(new Callback<CommonError>() {
                @Override
                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                    System.err.println("RESPONSE SEND RIDE STATUS:::::---   " + response);
                    showLoading.dismiss();
                    Log.e("RESPONSE  :::::   ", response + "");
                    if (response.code() == 200) {

                  /*  System.err.println("ROUTEID IN INTENT ONM FARM ROUTE LIST   " + routeid);
                    Intent intent = new Intent(FarmListRoute.this, MapsActivityNew.class);
                    intent.putExtra("sendS", routeid);
                    intent.putExtra("SCREEN", "FarmList");
                    startActivity(intent);
                    finish();*/
                    }

                }

                @Override
                public void onFailure(Call<CommonError> call, Throwable t) {
                    showLoading.dismiss();
                    Log.e("errorratro", t.getMessage());

                    System.err.println("Send Ride Status  Failure  ");

                }
            });
        }
    }

    // New code for getting forms by route name...
    public void getFarmsByRoute() {
        Log.d("FARMS__", "getFarmsByRoute called. shouldUpdateRouteAfterRefresh=" + shouldUpdateRouteAfterRefresh);
        
        // Refresh local variables from shared preferences
        route = sharedprefrenceManager.getRouteName();
        routeid = sharedprefrenceManager.getRouteId();
        date = sharedprefrenceManager.getDate();

        if (isOnlineMode() && !isInternetAvailable(FarmListRoute.this)) {
                showNoInternetRetryDialog(this::getFarmsByRoute);
                return;
            }
//        showLoading.show();
            Log.d("FARMS__", "getAllFarms called with routeid: $routeid and date: ${sharedprefrenceManager!!.getDate()}" + sharedprefrenceManager.getDate());

            String orderDateForFarmPriority = resolveOrderDateForApi();
            Call<BaseResponse<List<PriorityFarmData>>> call = apiService.getFarmsWithPriorityDateWise(route, sharedprefrenceManager.getOrgID(), sharedprefrenceManager.getParentId(), orderDateForFarmPriority, sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
            call.enqueue(new Callback<BaseResponse<List<PriorityFarmData>>>() {
                @Override
                public void onResponse(Call<BaseResponse<List<PriorityFarmData>>> call, Response<BaseResponse<List<PriorityFarmData>>> response) {
                    isAlreadyDone = false;
                    if (response.code() == 200) {
                        tvOffline.setVisibility(View.GONE);
//                    showLoading.dismiss();
                        //  start.setVisibility(View.VISIBLE);
                        System.err.println("getFarmsByRoute  " + response.body().getData().size());
                        List<PriorityFarmData> farms = response.body().getData()
                                .stream()
                                .filter(farm -> farm.getRemove() == 0)
                                .collect(Collectors.toList());

                        farmlist.clear();
                        farmlist.addAll(farms);
                        routeid = farms.get(0).getRouteID() + "";
                        if (farmlist.size() > 0) {
                            if (sharedprefrenceManager.isSyncMode()) {
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    AppDatabase db = AppDatabase.Companion.getDatabase(FarmListRoute.this);
                                    db.priorityFarmDao().clearFarms();
                                    db.priorityFarmDao().insertAll(farms);
                                });
                            }

                            tvDate.setText("Orders for " + farmlist.get(0).getOrderDate());
                            farmListAdapterByRote.notifyDataSetChanged();
                        }

                        System.err.println(" Farm List Route size  " + farmlist.size() + " ");
                        Log.e("Farms", "" + farmlist.size());

                        if (shouldUpdateRouteAfterRefresh) {
                            shouldUpdateRouteAfterRefresh = false;
                            callUpdateRouteApi();
                        }
                    } else
                        try {
                            isAlreadyDone = false;
                            showLoading.dismiss();

                            ivBack.performClick();
                            Toast.makeText(FarmListRoute.this, "Farms not added for selected date", Toast.LENGTH_SHORT).show();
                            return;

                        } catch (Exception e) {
                            isAlreadyDone = false;
                            showLoading.dismiss();
                            e.printStackTrace();
                        }
                    tvOffline.setVisibility(View.GONE);
                    getAllFarms();
                }

                @Override
                public void onFailure(Call<BaseResponse<List<PriorityFarmData>>> call, Throwable t) {
                    Log.e("FarmsFail", t.getMessage());
//                showLoading.dismiss();
                    System.err.println(" Farm List Error Failure ");
                    isAlreadyDone = false;
                    if (sharedprefrenceManager.isSyncMode()) {
                        loadFromDB();
                    } else {
                        showNoInternetRetryDialog(() -> FarmListRoute.this.getFarmsByRoute());
                    }

           /*     Toast.makeText(FarmListRoute.this,
                        "No Internet Connection",
                        Toast.LENGTH_SHORT).show();
                tvOffline.setVisibility(View.VISIBLE);*/
                }

            });


    }

    private void callUpdateRouteApi() {
        String routeId = sharedprefrenceManager.getRouteId();
        String rawDate = sharedprefrenceManager.getDate();
        String driverId = sharedprefrenceManager.getDriverID();
        String token = sharedprefrenceManager.getToken();

        if (routeId == null || driverId == null || token == null) return;

        String formattedDate = formatToMMDDYYYY(rawDate);

        apiService.updateRoute(routeId, formattedDate, driverId, token).enqueue(new Callback<CommonError>() {
            @Override
            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                if (response.isSuccessful()) {
                    Log.d("UpdateRoute", "Route update API success");
                } else {
                    Log.e("UpdateRoute", "Route update API error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CommonError> call, Throwable t) {
                Log.e("UpdateRoute", "Route update API failure: " + t.getMessage());
            }
        });
    }

    private void loadFromDB() {
        if (isOnlineMode()) {
            showNoInternetRetryDialog(this::getFarmsByRoute);
            return;
        }
        ivBack.setVisibility(GONE);
        title.setVisibility(GONE);
        Executors.newSingleThreadExecutor().execute(() -> {

            AppDatabase db = AppDatabase.Companion.getDatabase(FarmListRoute.this);
            String rawDate = sharedprefrenceManager.getDate();
            String formattedDate = formatToMMDDYYYY(rawDate);

            List<PriorityFarmData> offlineFarms = db.priorityFarmDao().getFarmsByDate(formattedDate);

            runOnUiThread(() -> {

                farmlist.clear();
                farmlist.addAll(offlineFarms);

                farmListAdapterByRote.notifyDataSetChanged();

                if (farmlist.size() > 0) {
                    tvDate.setText("Orders for " + farmlist.get(0).getOrderDate());
                }
//                Toast.makeText(FarmListRoute.this,
//                        "Showing offline data",
//                        Toast.LENGTH_SHORT).show();
            });

        });
    }

    private void errorDialog(String st) {


        if (errorDialog != null && errorDialog.isShowing()) {
            return; // dialog already visible → do nothing
        }

        errorDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setContentText(st);

        errorDialog.show();


    /*    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                //  .setTitleText("Oops...")
                .setContentText(st)
                .show();*/
    }

    private void postFarmsPriority() {
        showLoading.show();
        System.err.println("Farms Priority  Post size " + priorityFarmList.size());

        Call<BaseResponse<String>> call = apiService.updateFarmList(priorityFarmList);
        call.enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                showLoading.dismiss();
                if (response.code() == 200) {

                    //save.setVisibility(View.GONE);
                    //start.setVisibility(View.VISIBLE);
                    System.err.println("Farms Priority   ");
                    success("Success", response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                showLoading.dismiss();
                System.err.println("Farms Priority  failure ");

                Log.e("FarmsFail", t.getMessage());

            }

        });
    }

    // end..

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        //  mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onNoteListChanged(List<PriorityFarmData> farmData) {
        priorityFarmList.clear();
        for (int i = 0; i < farmData.size(); i++) {
            updateFarmData = new UpdateFarmData();
            updateFarmData.setRouteName(route);
            updateFarmData.setId(farmData.get(i).getId());
            updateFarmData.setFarmName(farmData.get(i).getFarmName());
            updateFarmData.setPriority(i + 1);
            priorityFarmList.add(i, updateFarmData);
            System.err.println("Farms Priority  failure get DATA " + sharedprefrenceManager.getRideId());

            if (sharedprefrenceManager.getRideId().equalsIgnoreCase("0") || sharedprefrenceManager.getRideId().equalsIgnoreCase("n")) {
                System.err.println("Set  OnNoteListChanged  RIDE ID 0 " + sharedprefrenceManager.getRideId());
                startDataSend.setRideId(0);
            } else {
                System.err.println("Set Else part in OnNoteListChanged   " + sharedprefrenceManager.getRideId());
                startDataSend.setRideId(sharedprefrenceManager.getRideIdInt());
            }
        }

        //start.setVisibility(View.GONE);
        //save.setVisibility(View.VISIBLE);

    }

    @Override
    public void addList(List<LiRoutePlannerDetail> liRoutePlannerDetails) {
        startDataSend.setLat(SkyLabLatitude + "");
        startDataSend.setLng(SkyLabLongitude + "");
        startDataSend.setAction("Departure");
        try {
            addresses = geocoder.getFromLocation(SkyLabLatitude, SkyLabLongitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            startDataSend.setCity(city);
            startDataSend.setAddress(address);
            startDataSend.setCountry(country);
            startDataSend.setState(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("AddList In FArm Route GETREIDE ID  and set  " + sharedprefrenceManager.getRideId());
        if (sharedprefrenceManager.getRideId().equals("") || sharedprefrenceManager.getRideId().equals("0")) {
            startDataSend.setRideId(0);
        } else {
            startDataSend.setRideId(sharedprefrenceManager.getRideIdInt());
        }

        startDataSend.setDriverId(sharedprefrenceManager.getDriverID());
        startDataSend.setRoutId(sharedprefrenceManager.getRouteId());
        Log.d("Meter__", "get car odometer is " + sharedprefrenceManager.getCarodometer());

        startDataSend.setStartOdometer(sharedprefrenceManager.getCarodometer());
        startDataSend.setUID(sharedprefrenceManager.getDriverID());
        startDataSend.setVehicleId(sharedprefrenceManager.getVehicleId());
        startDataSend.setLiRoutePlannerDetail(liRoutePlannerDetails);
    }

    private void success(String st1, String st2) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(st1)
                .setContentText(st2)
                .show();
    }

    @Override
    public void addFormEntry(List<String> formEntry) {
        entryformlist = new ArrayList<>();
        entryformlist.addAll(formEntry);
    }

    private boolean checkRide() {
        if (!sharedprefrenceManager.getToggleState()){
        Call<PermissionModel> call = apiService.showPermission(Integer.parseInt(sharedprefrenceManager.getDriverID()), sharedprefrenceManager.getToken());
        call.enqueue(new Callback<PermissionModel>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onResponse(Call<PermissionModel> call, Response<PermissionModel> response) {
                if (response.code() == 200) {
                    resultcame = true;
                    if (response.body().getData() != null) {

                        PermissionData pData = response.body().getData();
                        rideIdd = pData.getRideId();
                        routeIdd = pData.getRouteId();
                        parentIdd = pData.getParentId();
                        farmIdd = pData.getFarmId();
                        actionn = pData.getAction();
                        farmnamee = pData.getFarmName();
                        routeNamee = pData.getRouteName();
                        if (pData.getOrderDate() != null && !pData.getOrderDate().isEmpty()) {
                            String orderdate = formatToMMDDYYYY(pData.getOrderDate());
                            sharedprefrenceManager.setDate(orderdate);
                            sharedprefrenceManager.setImprovedDate(orderdate);
                        }

                        Log.d("Analysis__", "Ride is resumed :::" + isResumed);

                        if (parentIdd != 0 && rideIdd == 0) {
                            Log.d("Analysis__", "Inside line 1315");
                            backEnabled = true;
                            isResume = false;
                            previous = 1;
                        } else if (rideIdd != 0 && farmIdd == 0) {
                            Log.d("Analysis__", "Inside line 1321");
                            backEnabled = false;
                            isResume = false;
                            previous = 0;
                        } else if (rideIdd != 0 && farmIdd != 0 && actionn == 1) {
                            Log.d("Analysis__", "Inside line 1327");
                            backEnabled = false;
                            isResume = true;
                            previous = 0;
                        } else if (rideIdd != 0 && farmIdd != 0 && actionn == 0) {
                            Log.d("Analysis__", "Inside line 1332");
                            backEnabled = false;
                            isResume = false;
                            previous = 0;
                        } else {
                            Log.d("Analysis__", "Inside line 1337");
                            backEnabled = false;
                            previous = 0;
                        }
//                        if (pData.getShouldLogout().equals("True")){
//                            shouldLogout.shouldLogout();
//                        }

                    }


                } else {
                    Log.d("Analysis__", "inside else line 1289");
                    backEnabled = false;
                    previous = 0;
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        //  errorDialog(loginError.getMessage());
//                        System.err.println("In Select Car Show permission else part status not 200");
                        Log.e("Data  Show Permission", loginError.getMessage());

                    } catch (Exception e) {
                        backEnabled = false;
                        previous = 0;
                        e.printStackTrace();
                        Log.e("Data ExceptionShow P", e.toString());
                    }
                    resultcame = true;
                }
                if (backPressed) {
                    handleBackPress();
                }
            }

            @Override
            public void onFailure(Call<PermissionModel> call, Throwable t) {
                backEnabled = false;
                resultcame = true;
                previous = 0;

                handleOfflineRedirection();

                //  Toast.makeText(FarmListRoute.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        return backEnabled;
    }
        else

        {
            backEnabled = false;
            resultcame = true;
            previous = 0;

            handleOfflineRedirection();
            return backEnabled;
        }

}
    private void handleOfflineRedirection() {
        if (sharedprefrenceManager.isSyncMode()) {
                getFarmsByRoute();
         //   getAllFarms();
        } else {
            showNoInternetRetryDialog(() -> {
                checkRide();
            });
        }
    }

    private boolean isOnlineMode() {
        return sharedprefrenceManager != null && sharedprefrenceManager.isOnlineMode();
    }

    private void showNoInternetRetryDialog(Runnable retryAction) {
        new AlertDialog.Builder(this)
                .setTitle("No Internet")
                .setMessage("Internet is required in Online mode. Please check connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> {
                    dialog.dismiss();
                    if (retryAction != null) {
                        retryAction.run();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    @Override
    public void onItemClick(PriorityFarmData model) {

        loadBottomSheetDialogFragment = new LoadTemprature().instance(this,
                "" + model.getId(),
                route,
                routeid,
                false,
                this,
                model.getBegs(),
                model.getTmperature(),
                date);

        loadBottomSheetDialogFragment.show(getSupportFragmentManager(), loadBottomSheetDialogFragment.getTag());


    }

    @Override
    public void onClickSubmit(boolean clicked, @Nullable List<? extends LiRoutePlannerDetail> liRoutePlannerDetails, String comments, boolean sendMail) {
        sendRideStatus(0, "End");
        Log.d("Analysis__","getFarmsByRoute onClickSubmit");
        
        if (liRoutePlannerDetails != null && !liRoutePlannerDetails.isEmpty()) {
            // Update local farm data with temp/bags info if needed
        }

        if (isInternetAvailable(this)) {
            Log.d("FARMS__", "Line 1857 ");
            if (sharedprefrenceManager.isSyncMode()) {
                loadFromDB();
            }
            else {
                getFarmsByRoute();
            }

            if (sendMail) {
                // If we're not waiting for photos, send mail now
                if (!shouldWaitForUpload) {
                    FinishDataMail(String.valueOf(farmIdd), routeid, String.valueOf(rideIdd));
                }
            }

        } else {
            if (sharedprefrenceManager.isSyncMode())
            loadFromDB();
        }
    }


    @Override
    public void uploadImages(MultipartBody.Part img1, MultipartBody.Part img2) {
        shouldWaitForUpload=true;
        showLoading.show();
        Call<ResponseBody> call = apiService.uploadEndImage(sharedprefrenceManager.getParentId(), img1, img2);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showLoading.dismiss();
                if (response.code() == 200) {
                    //  Log.e("ta", response.body());
                    //startActivity(,StartActivity.class);
                    // finishCompleteRide();
                    FinishData(endodometer, totalmiles, endoil);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


    public void uploadStartImages(MultipartBody.Part img1, MultipartBody.Part img2, MultipartBody.Part img3, MultipartBody.Part img4, MultipartBody.Part img5, MultipartBody.Part img6, MultipartBody.Part img7, MultipartBody.Part img8) {
        Call<ResponseBody> call = apiService.uploadImage(sharedprefrenceManager.getToken(), sharedprefrenceManager.getParentId(), img1, img2, img3, img4, img5, img6, img7, img8);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Log.d("Upload__","Upload done");
                    //  Log.e("ta", response.body());
                    //startActivity(,StartActivity.class);
                    // finishCompleteRide();

                } else
                    try {
                        Log.d("Upload__","Upload in error");
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                       //   errorDialog(loginError.getMessage());
                        Log.e("Upload__", loginError.getMessage());
                    } catch (Exception e) {

                        e.printStackTrace();
                        Log.e("Upload__", e.toString());
                    }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Upload__","Upload in failure");
                Log.e("errorratro", t.getMessage());
            }
        });


    }

    public void FinishDataMail(String farmId, String routeId, String rideId ) {
        try {
            JsonObject request = new JsonObject();
            request.addProperty("ParentId", parentIdd);
            request.addProperty("FIRMID", farmId);
            request.addProperty("rideId", rideId+ "");
            request.addProperty("RouteId", routeId);

            Call<CommonError> call = apiService.rideFinishEmail(request,sharedprefrenceManager.getDriverID(),sharedprefrenceManager.getToken());
            call.enqueue(new Callback<CommonError>() {
                @Override
                public void onResponse(Call<CommonError> call, Response<CommonError> response) {

                    if (response.code() == 200) {

                        // finishCompleteRide();

                    } else {
                        try {

                            CommonError loginError = gson.fromJson(
                                    response.errorBody().string(),
                                    CommonError.class
                            );

                            Log.e("Data", loginError.getMessage());

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Data", e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonError> call, Throwable t) {
                    Log.e("errorratro", t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void finishData(String endodometer, String totalmiles, String endoil) {
        backPressed=false;
        this.endodometer=endodometer;
        this.totalmiles=totalmiles;
        this.endoil = endoil;
        if (shouldWaitForUpload){

        }else {
            FinishData(endodometer, totalmiles, endoil);
        }
    }

    @Override
    public void onReceiptSubmit(ReceiptResponse receiptDetails) {
        receiptDetails.setParentId(sharedprefrenceManager.getParentId());
        rcptId=System.currentTimeMillis()+"";
        receiptDetails.setRID(rcptId);
        //Log.d("Analysis__","data is :" +new Gson().toJson(receiptDetails));

        Call<CommonError> call = apiService.ReceiptDetails(receiptDetails,sharedprefrenceManager.getDriverID(),sharedprefrenceManager.getToken());
        call.enqueue(new Callback<CommonError>() {
            @Override
            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                if (response.code() == 200) {
                    //  //Log.d("Analysis__",new Gson().toJson(response));

                  //  rcptId = response.body().getData();
                    alReceipts.add(receiptDetails);
                    receiptAdapter.notifyDataSetChanged();
                    if (img1!=null) {
                        uploadRcpts();
                    }

                    //startActivity(,StartActivity.class);
                    // finishCompleteRide();
                } else
                    try {

                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        //  errorDialog(loginError.getMessage());
                        Log.e("Data", loginError.getMessage());
                    } catch (Exception e) {

                        e.printStackTrace();
                        Log.e("Data", e.toString());
                    }

            }

            @Override
            public void onFailure(Call<CommonError> call, Throwable t) {

                Log.e("errorratro", t.getMessage());
            }
        });

    }

    private void uploadRcpts() {
        showLoading.show();
        Call<ResponseBody> call = apiService.uploadReceiptImg(sharedprefrenceManager.getToken(),rcptId, img1, img2, img3,img4);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                showLoading.dismiss();
                if (response.code() == 200) {
                    img1=null;

                    //  Log.e("ta", response.body());
                    //startActivity(,StartActivity.class);
                    // finishCompleteRide();


                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        // Call your API here
                        if (isTextViewVisible(tvAddRcpt)) {
                            // or whatever your method is
                            getReceiptData();

                        }
                    }, 1000);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showLoading.dismiss();
                Log.e("errorratro", t.getMessage());
            }
        });
    }

    @Override
    public void onReceiptClick(int position, ReceiptResponse receiptDetails, boolean isImage) {

        //Log.d("Analysis__","Receipt id is"+receiptDetails.getID());

        if (isImage){
            showImageDialog(receiptDetails.getRecipt_Img());
        }else {
            rcptbottomSheetDialogFragment = new ReceiptDialog().instance(this, FarmListRoute.this::onReceiptSubmit, true, receiptDetails, FarmListRoute.this,FarmListRoute.this);
            rcptbottomSheetDialogFragment.show(getSupportFragmentManager(), rcptbottomSheetDialogFragment.getTag());
        }
    }
    private boolean isTextViewVisible(View view) {
        if (view == null || view.getVisibility() != VISIBLE) return false;

        Rect rect = new Rect();
        boolean isVisible = view.getGlobalVisibleRect(rect);

        // Optional: Make sure it's at least partially on screen
        return isVisible && rect.height() > 0 && rect.width() > 0;
    }
    @Override
    public void uploadReceiptImages(boolean isUpdate,MultipartBody.Part img1, MultipartBody.Part img2, MultipartBody.Part img3, MultipartBody.Part img4,
                                    String imgPath1, String imgPath2, String imgPath3, String imgPath4,ReceiptResponse receiptDetails) {
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
    }

    @Override
    public void onFarmClick(PriorityFarmData farmData, int shouldStart, String routeName) {
        //Log.d("Analysis__","Should Start is "+shouldStart);
        //Log.d("Analysis__","is Resume Start is "+isResume);

        //Log.d("Analysis__","is Resume Start is "+isResume);

                if (farmData.getIsCompleted() ==1 ){
                        if (farmData.getIsPhotoUploaded()==0){
                            bottomSheetDialogFragment =
                                    new PhotoDialog().instance(
                                            this,
                                            "" + farmData.getId(),
                                            farmData.getRideId(),
                                            routeid,
                                            parentIdd + "",
                                            this,
                                            farmData.getFarmName(),
                                            farmData.getOrderDate()
                                    );
                            bottomSheetDialogFragment.show(
                                    getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        }

                        Log.d("RIDEID___","Ride id is "+ farmData.getRideId());
                }
                else{
                    CountDownTimer timer = new CountDownTimer(3000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            if (resultcame) {
                                // Cancel timer so it doesn't trigger again
                                cancel();

                                Intent intent;
                                if (isResume) {
                                    intent = new Intent(FarmListRoute.this, FarmDetailActivity.class)
                                            .putExtra("FarmID", farmData.getId() + "")
                                            .putExtra("FarmName", farmData.getFarmName())
                                            .putExtra("RouteName", routeName)
                                            .putExtra("Routeid", "")
                                            .putExtra("actionn",actionn)
                                            .putExtra("farmidd",farmIdd+"")
                                            .putExtra("shouldstart", shouldStart)
                                            .putExtra("isResumeRide", true);
                                }
                                else {
                                    intent = new Intent(FarmListRoute.this, FarmDetailActivity.class)
                                            .putExtra("FarmID", farmData.getId() + "")
                                            .putExtra("FarmName", farmData.getFarmName())
                                            .putExtra("RouteName", routeName)
                                            .putExtra("Routeid", "")
                                            .putExtra("actionn",actionn)
                                            .putExtra("farmidd",farmIdd+"")
                                            .putExtra("shouldstart", shouldStart)
                                            .putExtra("orderDate", farmData.getOrderDate());
                                }

                              //  startActivity(intent);
                            }
                        }

                        public void onFinish() {
                            Log.d("DelayExample", "Executed after 3 seconds");
                        }
                    };
                    timer.start();
                }






    }



    @Override
    public void uploadReceiptUpdate(String id) {
        rcptId = id;
        uploadRcpts();
    }



    @Override
    public void onMailDone(Boolean value) {
        Log.d("Analysis__","getFarmsByRoute onMailDone");
        Log.d("FARMS__", "Line 2186 ");
        getFarmsByRoute();
    }

    @Override
    public void onPhotoUpload(boolean b, boolean sendMail) {
        Log.d("FarmListRoute", "onPhotoUpload: sendMail=" + sendMail);
        if (sendMail) {
            String targetFarmId = (farmIdd != 0) ? String.valueOf(farmIdd) : sharedprefrenceManager.getFID();
            if (targetFarmId != null && !targetFarmId.isEmpty() && !targetFarmId.equals("0")) {
                FinishDataMail(targetFarmId, routeid, String.valueOf(rideIdd));
            }
        }
        getFarmsByRoute();
    }


}
