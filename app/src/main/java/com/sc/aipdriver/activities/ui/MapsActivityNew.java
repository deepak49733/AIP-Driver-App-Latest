package com.sc.aipdriver.activities.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.directions.route.AbstractRouting;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.maps.android.ui.IconGenerator;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.adapters.ContactAdapter;
import com.sc.aipdriver.activities.dialogs.LoadTemprature;
import com.sc.aipdriver.activities.dialogs.ShowLoading;
import com.sc.aipdriver.activities.fragments.FinishDialog;
import com.sc.aipdriver.activities.fragments.SelectCar;
import com.sc.aipdriver.activities.fragments.TempratureDialogFragment;
import com.sc.aipdriver.activities.fragments.TutsPlusBottomSheetDialogFragment;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.ContactModel;
import com.sc.aipdriver.activities.models.DeliveryData;
import com.sc.aipdriver.activities.models.LatLongData;
import com.sc.aipdriver.activities.models.RouteLog;
import com.sc.aipdriver.activities.models.RoutePlannerDetail;
import com.sc.aipdriver.activities.otherclasses.GeoTask;
import com.sc.aipdriver.activities.otherclasses.HttpConnection;
import com.sc.aipdriver.activities.otherclasses.PathJSONParser;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.services.ForegroundLocationService;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivityNew extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, RoutingListener, GoogleMap.OnMarkerClickListener, View.OnClickListener, FinishDialog.SendData,FinishDialog.UploadImages, TempratureDialogFragment.SendData, GeoTask.Geo {

    private final Handler handler1 = new Handler();
    private final Handler handler2 = new Handler();
    long diffMinutes = 0;
    Runnable runnable, runnable1;
    boolean isPaused = false;
    boolean isFinised = false;
    Boolean isCounterCanceled = false;
    private static final int LOCATION_PERMISSION = 101;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    private GoogleMap mMap;
    ArrayList<LatLng> latLngslist;
    ArrayList<LatLng> pointsList;
    ArrayList<LatLng> points = new ArrayList<>();
    ApiInterface apiService;
    Gson gson;
    int LOCATION_PERMISSION_REQUEST_CODE=1001;
    boolean isSentBefore = false;
    Date lastSent, current;
    String farmid;
    boolean isFromRecents = false;
    int timedelay;
    SharedprefrenceManager sharedprefrenceManager;
    String routeId, screenn, sendData, RideId;

    DeliveryData deliveryData;
    ArrayList<DeliveryData> DeliveryDataList;
    SweetAlertDialog pDialog;
    ShowLoading showLoading;
    Location location;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private List<Polyline> polylines;
    List<RoutePlannerDetail> alDeliveryPendingFarms;
    HashMap<String, RoutePlannerDetail> latLongDataHashMap;
    private Marker carMarker;
    BottomSheetDialogFragment bottomSheetDialogFragment;
    CountDownTimer cTimer = null;

    private long timeRemaining = 30000;

    //  Client remove comment on that time to send apk

//     private final double SkyLabLatitude=45.696146;
//    private final double SkyLabLongitude=-95.923010;

    // end

    // demo rout
     private final double SkyLabLatitude = 28.440766;
     private final double SkyLabLongitude = 77.070499;

    // Muzaffarnagar
    //   private final double SkyLabLatitude = 29.4727;
    // private final double SkyLabLongitude = 77.7085;

    // Sec 23 Ansal Plaza
    // private final double SkyLabLatitude = 28.5115;
    // private final double SkyLabLongitude = 77.0420;


    // client route Skylab
   // private final double SkyLabLatitude = 45.69632;
   // private final double SkyLabLongitude = -95.923088;

    // end
    private LatLngBounds.Builder builder;
    private LatLngBounds bounds;
    private Polyline polyline;
    private LatLng userLocation;
    List<Marker> markersList;

    int disValue = 0;
    int durValue = 0;

    String sendlog = "true";


    Button pause;


    Button time;


    Button change_sequence;


    Button cancel_ride;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;

    int Seconds, Minutes, MilliSeconds;

    Geocoder geocoder;
    List<Address> addresses;
    List<Address> addresses1;

    private final double degreesPerRadian = 180.0 / Math.PI;

    Timer timer, timer1;

    Bitmap bmScreen;

    Dialog screenDialog;
    static final int ID_SCREENDIALOG = 1;

    Button btnScreenDialog_OK;
    TextView tv_contact, tv_cellphone, tv_tbags, tv_temp, tv_createdDate, tv_bagdeliver, tv_temseamadelever, tv_deliverydate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Log.d("Analysis__","Inside maps oncreate");
        pause = findViewById(R.id.pause);
        time = findViewById(R.id.time);
        change_sequence = findViewById(R.id.change_sequence);
        cancel_ride = findViewById(R.id.cancel_ride);

        latLngslist = new ArrayList<>();
        polylines = new ArrayList<>();
        alDeliveryPendingFarms = new ArrayList<>();
        latLongDataHashMap = new HashMap<>();
        DeliveryDataList = new ArrayList<>();

        showLoading = new ShowLoading(MapsActivityNew.this);
        routeId = getIntent().getStringExtra("ROUTEID");
        RideId = getIntent().getStringExtra("RIDEID");
        screenn = getIntent().getStringExtra("SCREEN");
        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        if (getIntent().hasExtra("sendLog")) {
            sendlog = getIntent().getStringExtra("sendLog");
        }
        //Log.d("Analysis__","Got data maps oncreate");
        gson = new GsonBuilder().setPrettyPrinting().create();
        //Log.d("Analysis__","Got data maps oncreate"+gson);
        sharedprefrenceManager = new SharedprefrenceManager(this);

//        setUpMap();
//        displayLocationSettingsRequest(this);

        handler = new Handler();
        geocoder = new Geocoder(this, Locale.getDefault());
        getFarmsRoute();

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Log.d("Analysis__","clicked on "+pause.getText());

                showPauseResumeDialog();
            }
        });
        cancel_ride.setOnClickListener(this);
        change_sequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MapsActivityNew.this, FarmListRoute.class);
                intent.putExtra("ROUTE", sharedprefrenceManager.getRouteName());
                intent.putExtra("SEQUENCE", "Change");
                startActivity(intent);
            }
        });

    }


    @Override
    public void onBackPressed() {
        //        new AlertDialog.Builder(this)
        //                .setTitle("Alert")
        //                .setMessage("You can not go back unless you finish or cancel the ride.")
        //
        //                // Specifying a listener allows you to take an action before dismissing the dialog.
        //                // The dialog is automatically dismissed when a dialog button is clicked.
        //                .setPositiveButton(android.R.string.ok, null)
        //
        //                // A null listener allows the button to dismiss the dialog and take no further action.
        //                //.setNegativeButton(null,null)
        //                .setIcon(android.R.drawable.ic_dialog_alert)
        //                .show();
        super.onBackPressed();
        showAlertDialog();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
// TODO Auto-generated method stub

        screenDialog = null;
        switch (id) {
            case (ID_SCREENDIALOG):
                screenDialog = new Dialog(this);
                screenDialog.setContentView(R.layout.dialog);

                tv_contact = (TextView) screenDialog.findViewById(R.id.tv_contact);
                tv_cellphone = (TextView) screenDialog.findViewById(R.id.tv_cellphone);
                tv_tbags = (TextView) screenDialog.findViewById(R.id.tv_bags);
                tv_temp = (TextView) screenDialog.findViewById(R.id.tv_temp);
                tv_createdDate = (TextView) screenDialog.findViewById(R.id.tv_createdDate);
                tv_bagdeliver = (TextView) screenDialog.findViewById(R.id.tv_bagdeliver);
                tv_temseamadelever = (TextView) screenDialog.findViewById(R.id.tv_temseamadelever);
                tv_deliverydate = (TextView) screenDialog.findViewById(R.id.tv_deliverydate);

                btnScreenDialog_OK = (Button) screenDialog.findViewById(R.id.okdialogbutton);
                btnScreenDialog_OK.setOnClickListener(btnScreenDialog_OKOnClickListener);
        }
        return screenDialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
// TODO Auto-generated method stub
        switch (id) {
            case (ID_SCREENDIALOG):
                dialog.setTitle("Captured Screen");
                break;
        }
    }

    private Button.OnClickListener btnScreenDialog_OKOnClickListener
            = new Button.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            screenDialog.dismiss();
        }
    };

    private void setUpMap() {
        //Log.d("Analysis__","line 356");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        //Log.d("Analysis__","line 358");
        mapFragment.getMapAsync(this);
        //Log.d("Analysis__","line 360");
        if (isCounterCanceled) {
            //Log.d("Analysis__","line 362");
            timeRemaining = timeRemaining - 1000;
            isCounterCanceled = false;
            startTimer();
        }

        //startTimer();
        //mMap.setMyLocationEnabled(true);
//        System.err.println("MAp Setting up....");

    }

    void startTimer() {
        final long[] seconds = {0};

        long millisInFuture = timeRemaining; //30 seconds
        long countDownInterval = 1000; //1 second

        new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {

                if (isCounterCanceled)
                    cancel();
                else {
                    seconds[0] = millisUntilFinished / 1000;
                    timeRemaining = millisUntilFinished;
                    System.out.println("yueyuwyuewyuewyuew_Moving " + seconds[0] + " -- " + millisUntilFinished);
//                    Toast.makeText(MapsActivityNew.this, seconds[0] +"", Toast.LENGTH_SHORT).show();
//                    if(seconds[0] == 0){
//                        System.out.println("yueyuwyuewyuewyuew_Moving");
//
//                    }
                }


            }

            public void onFinish() {
                System.out.println("yueyuwyuewyuewyuew_Moving_API");
                timeRemaining = 30000;
                //Toast.makeText(MapsActivityNew.this, "Finished", Toast.LENGTH_SHORT).show();
                sendLog("Moving");
               // updateRouteStatus();
                checkArrivedStatus(true);
                startTimer();
            }
        }.start();
//        cTimer.start();
    }

    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    private void getFarmsRoute() {
        System.err.println("Getting Farm Routes  ");
        Call<BaseResponse<LatLongData>> call = apiService.getstartNewRide(sharedprefrenceManager.getRideId(),sharedprefrenceManager.getDriverID(),sharedprefrenceManager.getToken());
        call.enqueue(new Callback<BaseResponse<LatLongData>>() {
            @Override
            public void onResponse(Call<BaseResponse<LatLongData>> call, Response<BaseResponse<LatLongData>> response) {
//                System.err.println("MAPS ACTIVITY getFarmsRoute   " + response + "    ----    " + response.body());
                if (response.code() == 200) {
                    alDeliveryPendingFarms.clear();
                    latLngslist.clear();
                    System.err.println("Got Farm Routes  ");
                    LatLongData latLongData = response.body().getData();
//                    RoutePlannerDetail routePlannerDetail1;
//                    routePlannerDetail1 = new RoutePlannerDetail();
//                    routePlannerDetail1.setFarmLat(location.getLatitude());
//                    routePlannerDetail1.setFarmLng(location.getLongitude());
//                    routePlannerDetail1.setId("000");
//                    routePlannerDetail1.setFarmName(sharedprefrenceManager.getUsername());
//                    routePlannerDetail1.setPriority(100);
//                    alDeliveryPendingFarms.add(routePlannerDetail1);

                    for (int i = 0; i < latLongData.getRoutePlannerDetail().size(); i++) {
                        RoutePlannerDetail routePlannerDetail3 = latLongData.getRoutePlannerDetail().get(i);
                        sharedprefrenceManager.setRideIdString(routePlannerDetail3.getFKDriverRideMainId());
//                        if (routePlannerDetail3.getDeleveryTime().equals("")) {
//                            alDeliveryPendingFarms.add(routePlannerDetail3);
//                        }
                    }
                    alDeliveryPendingFarms.addAll(latLongData.getRoutePlannerDetail());
                    String destinations = "";

                    // comment code
                    for (int i = alDeliveryPendingFarms.size() - 1; i >= 0; i--) {
                        if (!alDeliveryPendingFarms.get(i).getDeleveryTime().equals("")) {
                            alDeliveryPendingFarms.remove(i);
                        }
//                        else{
//                            destinations += destinations+alDeliveryPendingFarms.get(i).getFarmLat()+","+alDeliveryPendingFarms.get(i).getFarmLng()+"|";
//                        }
                    }


//                    for(int i = 0 ; i < alDeliveryPendingFarms.size(); i++){
//                        System.out.println("eueiwueuieiuweiuweiuewe " + alDeliveryPendingFarms.get(i).getFarmLat() + " -- " + alDeliveryPendingFarms.get(i).getFarmLng());
//
//                        if(alDeliveryPendingFarms.get(i).getFarmName().equalsIgnoreCase("AG CENTRAL DAIRY")){
//                            alDeliveryPendingFarms.get(i).setFarmLat(28.5036);
//                            alDeliveryPendingFarms.get(i).setFarmLng(77.0973);
//
////                            alDeliveryPendingFarms.get(i).setFarmLat(28.5036);
////                            alDeliveryPendingFarms.get(i).setFarmLng(77.0973);
//                        }
//                        if(alDeliveryPendingFarms.get(i).getFarmName().equalsIgnoreCase("M1")){
//                            alDeliveryPendingFarms.get(i).setFarmLat(28.8344);
//                            alDeliveryPendingFarms.get(i).setFarmLng(77.5699);
//                        }
//                        if(alDeliveryPendingFarms.get(i).getFarmName().equalsIgnoreCase("Columbus Meet")){
//                            alDeliveryPendingFarms.get(i).setFarmLat(28.9845);
//                            alDeliveryPendingFarms.get(i).setFarmLng(77.7064);
//                        }
//                    }

                    String origin = location.getLatitude() + "," + location.getLongitude();
                    for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {
                        System.out.println("uuuieuiuwuwuiewuieuwieuwew1 " + alDeliveryPendingFarms.get(i).getFarmName());
                        System.out.println("uuuieuiuwuwuiewuieuwieuwew1 " + alDeliveryPendingFarms.get(i).getFarmLat() + " -- " + alDeliveryPendingFarms.get(i).getFarmLng());
                        destinations += alDeliveryPendingFarms.get(i).getFarmLat() + "," + alDeliveryPendingFarms.get(i).getFarmLng() + "|";
                    }

                    origin += "|" + destinations;

                    RoutePlannerDetail routePlannerDetail;
                    routePlannerDetail = new RoutePlannerDetail();
                    routePlannerDetail.setFarmLat(SkyLabLatitude);
                    routePlannerDetail.setFarmLng(SkyLabLongitude);
//                    routePlannerDetail.setFarmLat(demoLat);
//                    routePlannerDetail.setFarmLng(demoLong);
                    routePlannerDetail.setId("00");
                    routePlannerDetail.setDeleveryTime("");
                    routePlannerDetail.setFarmName("SkyLab");
                    routePlannerDetail.setPriority(0);
                    alDeliveryPendingFarms.add(routePlannerDetail);
                    addToHashMap(alDeliveryPendingFarms);
                    destinations += SkyLabLatitude + "," + SkyLabLongitude;
                    //notifyMap();

                    //System.out.println("uuuieuiuwuwuiewuieuwieuwew " + destinations);


                    for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {
                        RoutePlannerDetail routePlannerDetail2 = alDeliveryPendingFarms.get(i);
                        //comment code
                        //if (routePlannerDetail2.getDeleveryTime().equals("")) {
                        LatLng lt = new LatLng(routePlannerDetail2.getFarmLat(), routePlannerDetail2.getFarmLng());
                        latLngslist.add(lt);
                        //}
                    }

                    //String origin = "28.5055131,77.0365805";
                    //final String destination = "28.5036,77.097|28.8344,77.5699|28.9845,77.7064|29.4727,77.7085";


                    System.out.println("ewuiewuiewuiuiwueiwuiwuwi " + origin);
                    System.out.println("ewuiewuiewuiuiwueiwuiwuwi1 " + destinations);

                  //  String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + destinations + "&mode=driving&units=imperial&key=AIzaSyBKuDPXLD9BVKhISPpUd4fyyenbbJGgZRo";

                    // String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=28.5062962,77.0376629%7C28.5064629,77.0378195%7C28.5070022,77.0380991%7C&destinations=28.5064629,77.0378195%7C28.5070022,77.0380991%7C28.5115,77.0420&mode=walking&units=imperial&key=AIzaSyBKuDPXLD9BVKhISPpUd4fyyenbbJGgZRo";


                    new GeoTask(MapsActivityNew.this, "set").execute("url", "set");


                    // this is the marker generator part.
//                    if (latLngslist.size() > 0) {
//                        markersList = new ArrayList<Marker>();
//                        for(int i = 0; i < alDeliveryPendingFarms.size(); i++) {
//
//                            MarkerOptions markerOptions;
//                            try {
//
//                                if (alDeliveryPendingFarms.get(i).getPriority() == 100) {
//                                    markerOptions = new MarkerOptions().position(latLngslist.get(i)).title(alDeliveryPendingFarms.get(i).getFarmName()).snippet("Driver Name");
//                                    System.err.println(alDeliveryPendingFarms.get(i).getFarmName() + "  Adding Driver Marker on Map" + "379 LN");
//
////                                    System.err.println("Priority 100 " + markerOptions.getTitle());
//                                } else {
//                                    System.err.println(alDeliveryPendingFarms.get(i).getFarmName() + "  Adding MArker on Map in else part " + "383 LN " + alDeliveryPendingFarms.get(i).getFarmName());
//
//                                    LatLng origin = new LatLng(28.5055218,77.0366137);
//                                    LatLng dest = new LatLng(28.7718,77.5075);
//                                    String url = getDirectionsUrl(origin, dest);
//                                    System.out.println("ewuiuiewuiewuiewuewew " + url);
//
//                                   // DownloadTask downloadTask = new DownloadTask();
//
//                                    // Start downloading json data from Google Directions API
//                                    // downloadTask.execute(url);
//
//                                    markerOptions = new MarkerOptions().position(latLngslist.get(i)).title(alDeliveryPendingFarms.get(i).getFarmName()).snippet("Priority " + alDeliveryPendingFarms.get(i).getPriority()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerIconWithLabel(getApplicationContext(), alDeliveryPendingFarms.get(i).getFarmName(), "55 km")));
//                                    //  System.err.println("Priority  " + markerOptions.getTitle());
//
//
//                                  //markerOptions.snippet("Location " + results[0]);
//                                }
//                                //mMap.addMarker(markerOptions);
//                                markersList.add(mMap.addMarker(markerOptions));
//                               // mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
//                               //     markersList.get(i).showInfoWindow();
//                            } catch (ArrayIndexOutOfBoundsException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                    LatLng mylatlong = new LatLng(SkyLabLatitude, SkyLabLongitude);
////                    LatLng mylatlong = new LatLng(demoLat, demoLong);
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlong, 12));
//
//
//                    drawRoutes();

//                    String url = getMapsApiDirectionsUrl();
//                    ReadTask downloadTask = new ReadTask();
//                    downloadTask.execute(url);


                } else
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        // errorDialog(loginError.getMessage());
                        System.err.println("");
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

    @Override
    public void setDistanceTime(List<String> distanceTime) {
        if (latLngslist.size() > 0) {
            markersList = new ArrayList<Marker>();

            for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {

                MarkerOptions markerOptions;
                try {
                    if (alDeliveryPendingFarms.get(i).getPriority() == 100) {
                        markerOptions = new MarkerOptions().position(latLngslist.get(i)).title(alDeliveryPendingFarms.get(i).getFarmName()).snippet("Driver Name");
                        System.err.println(alDeliveryPendingFarms.get(i).getFarmName() + "  Adding Driver Marker on Map" + "379 LN");

//                                    System.err.println("Priority 100 " + markerOptions.getTitle());
                    } else {
                        System.err.println(alDeliveryPendingFarms.get(i).getFarmName() + "  Adding MArker on Map in else part " + "383 LN " + alDeliveryPendingFarms.get(i).getFarmName());

//                        LatLng origin = new LatLng(28.5055218,77.0366137);
//                        LatLng dest = new LatLng(28.7718,77.5075);
//                        String url = getDirectionsUrl(origin, dest);
//                        System.out.println("ewuiuiewuiewuiewuewew " + url);

                        // DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        // downloadTask.execute(url);

                        String distanceTimeString[] = distanceTime.get(i).split(",");
                        markerOptions = new MarkerOptions().position(latLngslist.get(i)).title(alDeliveryPendingFarms.get(i).getFarmName()).snippet("Priority " + alDeliveryPendingFarms.get(i).getPriority()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerIconWithLabel(getApplicationContext(), distanceTimeString[1], distanceTimeString[0])));


                        //  System.err.println("Priority  " + markerOptions.getTitle());


                        //markerOptions.snippet("Location " + results[0]);
                    }
                    //mMap.addMarker(markerOptions);
                    markersList.add(mMap.addMarker(markerOptions));


                    // mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    //     markersList.get(i).showInfoWindow();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }

        //LatLng mylatlong = new LatLng(SkyLabLatitude, SkyLabLongitude);
        LatLng mylatlong = new LatLng(location.getLatitude(), location.getLongitude());
//                    LatLng mylatlong = new LatLng(demoLat, demoLong);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlong, 12));

        drawRoutes();
    }

    private void updateRouteStatus() {
        String origin = location.getLatitude() + "," + location.getLongitude();
        String destinations = "";

        if (alDeliveryPendingFarms.size() > 0) {
            for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {
                destinations += alDeliveryPendingFarms.get(i).getFarmLat() + "," + alDeliveryPendingFarms.get(i).getFarmLng() + "|";
            }

            origin += "|" + destinations;

            String url = "";
                   // "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + destinations + "&mode=driving&units=imperial&key=AIzaSyBKuDPXLD9BVKhISPpUd4fyyenbbJGgZRo";
            new GeoTask(MapsActivityNew.this, "update").execute(url, "update");
        }

    }

    @Override
    public void updateDistanceTime(List<String> distanceTime) {

        if (markersList.size() == distanceTime.size() && distanceTime.size() == alDeliveryPendingFarms.size()) {
            for (int i = 0; i < markersList.size(); i++) {

                MarkerOptions markerOptions;

                Marker marker = markersList.get(i);
                markersList.get(i).remove();
                markersList.set(i, marker);

                String distanceTimeString[] = distanceTime.get(i).split(",");
                markerOptions = new MarkerOptions().position(markersList.get(i).getPosition()).title(alDeliveryPendingFarms.get(i).getFarmName()).snippet("Priority " + alDeliveryPendingFarms.get(i).getPriority()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerIconWithLabel(getApplicationContext(), distanceTimeString[1], distanceTimeString[0])));

                mMap.addMarker(markerOptions);
            }
        }


    }

    public Bitmap getMarkerIconWithLabel(Context mContext, String title, String label) {
        IconGenerator iconGenerator = new IconGenerator(mContext);
        View markerView = LayoutInflater.from(mContext).inflate(R.layout.view_custom_marker, null);
        ImageView imgMarker = markerView.findViewById(R.id.img_marker);
        TextView tvTitle = markerView.findViewById(R.id.tv_title);
        TextView tvLabel = markerView.findViewById(R.id.tv_label);

        imgMarker.setImageResource(R.drawable.marker_icon_google);

        tvTitle.setText(title);
        tvLabel.setText(label);
        iconGenerator.setContentView(markerView);
        iconGenerator.setBackground(null);

        return iconGenerator.makeIcon(label);
    }

    private void drawRoutes() {
        try {
            latLngslist.add(0, new LatLng(location.getLatitude(), location.getLongitude()));

//            for(int i = 0 ; i < latLngslist.size(); i++){
//                System.out.println("weuieuiuwuiuieweuwuew " + latLngslist.get(i).latitude + " -- " + latLngslist.get(i).longitude);
//            }

            if (latLngslist.size() > 2) {
                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.WALKING)
                        .withListener(MapsActivityNew.this)
                        .alternativeRoutes(true)
                      //  .key("AIzaSyBKuDPXLD9BVKhISPpUd4fyyenbbJGgZRo")
                        .waypoints(latLngslist)
                        .optimize(true)
                        .build();
                routing.execute();
            } else {
                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.WALKING)
                        .withListener(MapsActivityNew.this)
                        .alternativeRoutes(true)
                       // .key("AIzaSyBKuDPXLD9BVKhISPpUd4fyyenbbJGgZRo")
                        .waypoints(latLngslist)
                        .optimize(false)
                        .build();
                routing.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToHashMap(List<RoutePlannerDetail> alDeliveryPendingFarms) {
        System.err.println("Adding in to  HashMap  " + "461 LN");
        for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {
            RoutePlannerDetail routePlannerDetail = alDeliveryPendingFarms.get(i);
            if (alDeliveryPendingFarms.get(i).getPriority() == 100) {
                latLongDataHashMap.put("Driver Name", routePlannerDetail);
            } else {
                latLongDataHashMap.put("Priority " + alDeliveryPendingFarms.get(i).getPriority(), routePlannerDetail);
                // System.err.println(" Priority ADded in Hashmap"   +   alDeliveryPendingFarms.get(i).getPriority() +"   Farm Name Added        "+routePlannerDetail.getFarmName()+  "Farm Priority   "+  routePlannerDetail.getPriority());
            }
        }
        System.err.println("Priority set in to  HashMap  " + "471 LN");
    }

    @Override
    public void onClick(View view) {
        if (view == cancel_ride) {
           // cancelRide();
            showCancelDialog();
        }
    }

    @Override
    public void uploadImages(MultipartBody.Part img1, MultipartBody.Part img2) {

    }

    @Override
    public void finishData(String endodometer, String totalmiles, String endoil) {
        try {
            addresses1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses1.get(0).getAddressLine(0);
            String city = addresses1.get(0).getLocality();
            String state = addresses1.get(0).getAdminArea();
            String country = addresses1.get(0).getCountryName();

            showLoading.show();
            Call<CommonError> call = apiService.finishRide(sharedprefrenceManager.getRideId(), sharedprefrenceManager.getDriverID(), "" + location.getLatitude(), "" + location.getLongitude(), endodometer, address, state, city, country, "End", totalmiles,sharedprefrenceManager.getDriverID(),sharedprefrenceManager.getToken());
            call.enqueue(new Callback<CommonError>() {
                @Override
                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                    if (response.code() == 200) {
                        //  Log.e("ta", response.body());
                        isPaused = false;
                        showLoading.dismiss();

//                    timer.cancel();
//                    timer.purge();
//                    timer = null;
//
//                    timer1.cancel();
//                    timer1.purge();
//                    timer1 = null;

                        handler1.removeCallbacks(runnable);
                        handler2.removeCallbacks(runnable1);

                        finishCompleteRide();

                        if (bottomSheetDialogFragment != null) {
                            bottomSheetDialogFragment.dismiss();
                        }

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
                    Log.e("errorratro", t.getMessage());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void cancelRide() {
//        try {
//            sendLog("Cancel");
//            isPaused = false;
//            showLoading.show();
////            System.err.println(sharedprefrenceManager.getDriverID() + "------    " + sharedprefrenceManager.getVehicleId() + "");
//
//            Call<CommonError> call = apiService.postVehicleStatus(Integer.parseInt(sharedprefrenceManager.getDriverID()), Integer.parseInt(sharedprefrenceManager.getVehicleId()), 0, "Finish");
//            call.enqueue(new Callback<CommonError>() {
//                @Override
//                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
//                    try {
//                        if (response.code() == 200) {
//                            sharedprefrenceManager.setRideId(0);
//                            showLoading.dismiss();
//                            //                                timer.cancel();
////                                timer.purge();
////                                timer = null;
////                                timer1.cancel();
////                                timer1.purge();
////                                timer1 = null;
//                            try {
//                                handler1.removeCallbacks(runnable);
//                                handler2.removeCallbacks(runnable1);
//                            } catch (Exception e) {
//                                System.err.println("OnCancel ::::" + "Handler Exception Block");
//                                e.printStackTrace();
//                                showLoading.dismiss();
//                                System.err.println("OnCancel ::::" + sharedprefrenceManager.getRideId());
//
//                            }
////                                System.err.println("OnCancel ::::" + sharedprefrenceManager.getRideId());
//                            Log.e("OnCancel", sharedprefrenceManager.getRideId());
//                            System.err.println("OnCancel ::::" + "Ride Cancelled !");
//                            Toast.makeText(MapsActivityNew.this, "Ride Canceled", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(MapsActivityNew.this, SelectCar.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            System.err.println("Cancel Ride....Error");
//                            showLoading.dismiss();
//                            CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                            //  errorDialog(loginError.getMessage());
//                            Log.e("Data", loginError.getMessage());
//
//                        }
//                    } catch (Exception e) {
//                        showLoading.dismiss();
//                        e.printStackTrace();
////                            System.err.println("MAp Activity....Exception");
//                        Log.e("Data", e.toString());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<CommonError> call, Throwable t) {
//                    showLoading.dismiss();
//                    Log.e("errorratro", t.getMessage());
////                        System.err.println("MAp Activity....Failure");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);

                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {

            points.clear();
            PolylineOptions polyLineOptions = null;

            String disvalue = "";
            String durvalue = "";

            int finalDis = 0;
            int finalDur = 0;

            int distance1 = 0;

            int hours = 0;
            int minutes = 0;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {
                        disvalue = (String) point.get("distance");
                        continue;
                    } else if (j == 1) {
                        durvalue = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(3);
                polyLineOptions.color(Color.RED);
                polyline = mMap.addPolyline(polyLineOptions);
                polylines.add(polyline);

                finalDis = Integer.parseInt(disvalue) + disValue;
                finalDur = Integer.parseInt(durvalue) + durValue;

                distance1 = Math.round(finalDis / 1608);

                hours = Math.round(finalDur / (60 * 60));
                minutes = Math.round(((finalDur / 1000) % ((60 * 60))));
            }

            success1("Total Distance : " + distance1 + " miles" + "\n\nTotal Time : " + hours + " hours " + minutes + " minutes");
        }
    }

    //OnMapReady
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.d("Analysis__","line 943");
        mMap = googleMap;

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

        // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //mMap.setMyLocationEnabled(true);
        //  mMap.getMyLocation(location.getBearing());


        //  mMap.getUiSettings().setMapToolbarEnabled(true);
        // mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.setOnMarkerClickListener(this);
        buildGoogleApiClient();
        //Log.d("Analysis__","line 967");
        System.err.println("In map Ready isFinished " + isFinised);
        if (sendlog.equalsIgnoreCase("true")) {
            //Log.d("Analysis__","Maps location is "+location);
            if (location != null) {
                sendLog("Start");
                //sendLog("Moving");
                sendlog = "false";
            }
        }
        if (!isFinised) {
            Runnable runnable = new Runnable() {
                public void run() {


//                     current = Calendar.getInstance().getTime();
//                     System.err.println(current);
//                     if (lastSent!=null) {
//                         long diff = current.getTime() - lastSent.getTime();
//                          diffMinutes = diff / (60 * 1000) % 60;
//                     }

                    // Do the stuff
                    //
//                    System.err.println("Diiference ====  "+diffMinutes);
//                    Toast.makeText(MapsActivityNew.this, "Time Interval "+diffMinutes, Toast.LENGTH_SHORT).show();
                    if ((location != null && !isPaused)) {
//                        if (diffMinutes>=2||diffMinutes==0) {
                        //  sendLog("Moving");
                        checkArrivedStatus(true);
//                        }
                    }

                    handler1.postDelayed(this, 120000);
                }
            };
            runnable.run();


            runnable1 = new Runnable() {
                public void run() {
                    //
                    // Do the stuff
                    //
                    if (location != null) {
                        checkArrivedStatus(false);
                    }
                    handler2.postDelayed(this, 120000);
                }
            };
            runnable1.run();
//

//            timer = new Timer();
//        TimerTask hourlyTask = new TimerTask() {
//            @Override
//            public void run() {
//                sendLog("Moving");
//                checkArrivedStatus(true);
//
//            }
//        };
//        timer.schedule(hourlyTask, 120000, 120000);
////
////
//        timer1 = new Timer();
//        TimerTask hourlyTask1 = new TimerTask() {
//            @Override
//            public void run() {
//                if (location != null) {
//                    checkArrivedStatus(false);
//                }
//            }
//        };
//        timer1.schedule(hourlyTask1, 120000, 120000);
            //Log.d("Analysis__","Line 1041");
        }
    }

    private void checkArrivedStatus(boolean isMoving) {

        for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {

            if (alDeliveryPendingFarms.get(i).getPriority() != 100) {

                CalculationByDistance(alDeliveryPendingFarms.get(i).getFarmLat(), alDeliveryPendingFarms.get(i).getFarmLng(), isMoving);

            }
        }

    }

    private void startNavigation(LatLng latLng) {

        // Create a Uri from an intent string. Use the result to create an Intent.
//                Uri gmmIntentUri = Uri.parse("google.navigation:q=28.4497,77.0705,+28.4591,77.0726,+28.610000,77.232150");
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude);


// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    private void CalculationByDistance(double lat, double lng, final boolean isMoving) {
        System.err.println(" InSide CalculationByDistance   " + "  780LN");
        int Radius = 6371; // radius of earth in Km
        double lat1 = lat;
        double lat2 = userLocation.latitude;
        double lon1 = lng;
        double lon2 = userLocation.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double valueResult = Radius * c;
        final double meter = valueResult * 1000;

        if (!isMoving) {
            timedelay = 3000;
            Handler handler = new Handler(Looper.getMainLooper());
            System.err.println(" InSide Handler   " + "  799LN");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (meter <= 100.0) {
                        Toast.makeText(MapsActivityNew.this, "Arrived", Toast.LENGTH_SHORT).show();
                        timedelay = 3000;
                        // sendLog("Arrived");
                    }
                }
            }, timedelay);
        }
    }

    private void sendLog(String status) {
        System.err.println("In Send Log " + "  812 LN");
        System.err.println("Is Finished " + isFinised + "  813 LN");
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
                Call<CommonError> call = apiService.sendLog(routeLog,sharedprefrenceManager.getToken());
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

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.e("error", "" + e.toString());
        System.err.println("Caught   Routing Failure  " + "   914 LN");
        //Toast.makeText(MapsActivityNew.this, e.getMessage(), Toast.LENGTH_LONG).show();
        Toast.makeText(this, " Routing Failure", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRoutingStart() {
        // Toast.makeText(this, " Routing Start", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinised) {
            timeRemaining = timeRemaining - 1000;
            isCounterCanceled = true;
            startService();
        }
        if (pause.getText().toString().equalsIgnoreCase("Resume Ride")) {
            mMap.clear();
        }
    }


    @Override
    public void onRoutingSuccess(ArrayList<com.directions.route.Route> route, int j) {
        //  Toast.makeText(this, " Routing Success", Toast.LENGTH_LONG).show();
        pointsList = new ArrayList<>();
        pointsList.clear();
        pointsList = (ArrayList<LatLng>) route.get(0).getPoints();
        for (int i = 0; i < route.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.BLUE);
            polyOptions.geodesic(true);
            Log.d("Route Path   ", "onRoutingSuccess: " + route.get(i).getPoints());
            polyOptions.addAll(route.get(i).getPoints());
            polyline = mMap.addPolyline(polyOptions);

            polylines.add(polyline);
            route.get(i).setName("farm");

            disValue = route.get(i).getDistanceValue();
            durValue = route.get(i).getDurationValue();
        }
        for (int k = 0; k < latLngslist.size() - 1; k++) {
//            DrawArrowHead(mMap, new LatLng(latLngslist.get(k).latitude, latLngslist.get(k).longitude), new LatLng(latLngslist.get(k + 1).latitude, latLngslist.get(k + 1).longitude));
        }

    }

    @Override
    public void onRoutingCancelled() {
        Toast.makeText(this, " Routing Cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_temp) {
            BottomSheetDialogFragment bottomSheetDialogFragment = new TempratureDialogFragment();
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            return true;
        }
        if (id == R.id.action_farmlist) {
            BottomSheetDialogFragment bottomSheetDialogFragment = new TutsPlusBottomSheetDialogFragment();
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        String MarkerTitle = marker.getTitle();

        try {
            if (MarkerTitle.equals("Driver")) {

                bottomSheetDialogFragment = new FinishDialog().instance(marker.getSnippet(), MapsActivityNew.this,MapsActivityNew.this);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

            } else {
                showInfoDialog(marker, MarkerTitle);
            }
//            showNavigationDialog(marker, MarkerTitle);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void
    showInfoDialog(final Marker marker, final String MarkerTitle) {
        try {
            System.err.println("InShow Info Dialog  ");
            final Dialog dialog = new Dialog(MapsActivityNew.this, cn.pedant.SweetAlert.R.style.MyDialog);
            dialog.setContentView(R.layout.info_dialog);

            final RoutePlannerDetail routePlannerDetail = latLongDataHashMap.get(marker.getSnippet());

            ArrayList<ContactModel> alContacts = new ArrayList<>();
            // set the custom dialog components - text, image and button
            if (routePlannerDetail.getAlContacts() != null)
                alContacts.addAll(routePlannerDetail.getAlContacts());
            RecyclerView rvContacts = dialog.findViewById(R.id.rv_contacts);

            rvContacts.hasFixedSize();
            rvContacts.setLayoutManager(new LinearLayoutManager(this));
            RelativeLayout contactInfo = dialog.findViewById(R.id.rl);
            ContactAdapter contactAdapter = new ContactAdapter(this, alContacts);
            rvContacts.setAdapter(contactAdapter);
            TextView farmName = (TextView) dialog.findViewById(R.id.txt_farmName);
            ImageView imgCancel = (ImageView) dialog.findViewById(R.id.img_cancel);
            ImageView imgEdit = dialog.findViewById(R.id.img_edit);
            farmName.setText(routePlannerDetail.getFarmName());

            if (!MarkerTitle.equals("SkyLab")) {
                contactInfo.setVisibility(View.VISIBLE);
                imgEdit.setVisibility(View.VISIBLE);
            } else {
                contactInfo.setVisibility(View.GONE);
                imgEdit.setVisibility(View.GONE);
            }

//            imgEdit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    new LoadTemprature(MapsActivityNew.this, MapsActivityNew.this, "" +routePlannerDetail.getFIRMID(),"", sharedprefrenceManager.getRouteName()).createDialog();
//
////                    createDialog(routePlannerDetail.getFarmName());
//                }
//            });
//            contact.setText(routePlannerDetail.getCellPhone());
            imgCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            Button Yes = (Button) dialog.findViewById(R.id.btn_yes);
            Yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();

                    startNavigation(new LatLng(routePlannerDetail.getFarmLat(), routePlannerDetail.getFarmLng()));


                    if (MarkerTitle.equals("SkyLab")) {
                        //System.err.println("MarkerClick"+ MarkerTitle);
                        bottomSheetDialogFragment = new FinishDialog().instance(marker.getSnippet(), MapsActivityNew.this, MapsActivityNew.this);
                        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

//                            return true;
                    } else if (MarkerTitle.equals("Driver")) {
                        System.err.println("FALSE DRIVER CLICKED");
//                            return false;
                    } else {


                        if (routePlannerDetail.getDeleveryTime().equals("")) {
                            farmid = routePlannerDetail.getFIRMID();

                            Log.e("FARM_ID", farmid);
                            bottomSheetDialogFragment = new TempratureDialogFragment().instance(routePlannerDetail, marker.getSnippet(), MapsActivityNew.this);
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//                                return true;
                        } else {
                            System.err.println("In ELSE PART");
                        }
                    }
                }
            });

            Button No = (Button) dialog.findViewById(R.id.btn_no);
            // if button is clicked, close the custom dialog
            No.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (MarkerTitle.equals("SkyLab")) {
                        //System.err.println("MarkerClick"+ MarkerTitle);
                        bottomSheetDialogFragment = new FinishDialog().instance(marker.getSnippet(), MapsActivityNew.this, MapsActivityNew.this);
                        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

//                            return true;
                    } else if (MarkerTitle.equals("Driver")) {
                        System.err.println("FALSE DRIVER CLICKED");
//                            return false;
                    } else {
                        System.err.println("Snippet   " + marker.getSnippet());

                        if (routePlannerDetail.getDeleveryTime().equals("")) {
                            farmid = routePlannerDetail.getFIRMID();

//                            Log.e("FARM_ID", farmid);
                            bottomSheetDialogFragment = new TempratureDialogFragment().instance(routePlannerDetail, marker.getSnippet(), MapsActivityNew.this);
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//                                return true;
                        } else {
                            System.err.println("In ELSE PART");
                        }
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog() {
        try {
            final Dialog dialog = new Dialog(MapsActivityNew.this, cn.pedant.SweetAlert.R.style.MyDialog);
            dialog.setContentView(R.layout.ride_alert_dialog);
            Button btnOk = dialog.findViewById(R.id.btn_ok);

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showCancelDialog() {
        try {
            final Dialog dialog = new Dialog(MapsActivityNew.this, cn.pedant.SweetAlert.R.style.MyDialog);
            dialog.setContentView(R.layout.pauseresumedialog);
            Button btnOk = dialog.findViewById(R.id.btn_ok);
            Button btnNo = dialog.findViewById(R.id.btn_no);
            final TextView txtView = dialog.findViewById(R.id.txt_pause_resume);
            txtView.setText("Do You Want to Cancel Ride");

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        try {
                          //  sendLog("Cancel");
                            isPaused = false;
                            showLoading.show();
            System.err.println(sharedprefrenceManager.getDriverID() + "------    " + sharedprefrenceManager.getVehicleId() + "");

                            Call<CommonError> call = apiService.postVehicleStatus(sharedprefrenceManager.getDriverIdInt(), sharedprefrenceManager.getVehicleIdInt(), 0, "Finish");
                            call.enqueue(new Callback<CommonError>() {
                                @Override
                                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                                    try {
                                        if (response.code() == 200) {
                                            sharedprefrenceManager.setRideId(0);
                                            showLoading.dismiss();
                                            //                                timer.cancel();
//                                timer.purge();
//                                timer = null;
//                                timer1.cancel();
//                                timer1.purge();
//                                timer1 = null;
                                            try {
                                                handler1.removeCallbacks(runnable);
                                                handler2.removeCallbacks(runnable1);
                                            } catch (Exception e) {
                                                System.err.println("OnCancel ::::" + "Handler Exception Block");
                                                e.printStackTrace();
                                                showLoading.dismiss();
                                                System.err.println("OnCancel ::::" + sharedprefrenceManager.getRideId());

                                            }
//                                System.err.println("OnCancel ::::" + sharedprefrenceManager.getRideId());
                                            Log.e("OnCancel", sharedprefrenceManager.getRideId());
                                            System.err.println("OnCancel ::::" + "Ride Cancelled !");
                                            Toast.makeText(MapsActivityNew.this, "Ride Canceled", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(MapsActivityNew.this, SelectCar.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            System.err.println("Cancel Ride....Error");
                                            showLoading.dismiss();
                                            CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                                            //  errorDialog(loginError.getMessage());
                                            Log.e("Data", loginError.getMessage());

                                        }
                                    } catch (Exception e) {
                                        showLoading.dismiss();
                                        e.printStackTrace();
//                            System.err.println("MAp Activity....Exception");
                                        Log.e("Data", e.toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<CommonError> call, Throwable t) {
                                    showLoading.dismiss();
                                    Log.e("errorratro", t.getMessage());
//                        System.err.println("MAp Activity....Failure");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }






            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showPauseResumeDialog() {
        try {
            //Log.d("Analysis__","Showing dialog");
            final Dialog dialog = new Dialog(MapsActivityNew.this, cn.pedant.SweetAlert.R.style.MyDialog);
            dialog.setContentView(R.layout.pauseresumedialog);
            Button btnOk = dialog.findViewById(R.id.btn_ok);
            Button btnNo = dialog.findViewById(R.id.btn_no);
            final TextView txtView = dialog.findViewById(R.id.txt_pause_resume);
            if (pause.getText().toString().equalsIgnoreCase("Pause Ride")) {
                txtView.setText(R.string.pause_ride);
            } else if (pause.getText().toString().equalsIgnoreCase("Resume Ride")) {
                txtView.setText(getResources().getString(R.string.resume_ride));
            }
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pause.getText().toString().equalsIgnoreCase("Pause Ride")) {
                        sendLog("Pause");
                        isPaused = true;
                        isCounterCanceled = true;
//                    StartTime = SystemClock.uptimeMillis();
//                    handler.postDelayed(runnable, 0);
                        pause.setText("Resume Ride");
                        pause.setTextColor(getResources().getColor(R.color.green));
                        dialog.dismiss();

                    } else if (pause.getText().toString().equalsIgnoreCase("Resume Ride")) {
                        sendLog("Start");
                        isPaused = false;
                        isCounterCanceled = false;
                        startTimer();
//                    TimeBuff += MillisecondTime;
                        pause.setText("Pause Ride");
                        pause.setTextColor(getResources().getColor(R.color.colorAccent));
                        dialog.dismiss();
                    }

                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delivery Data
    private void sendFarmData(JsonObject jsonObject) {
        showLoading.show();
        System.err.println(jsonObject + " ");
        Call<CommonError> call = apiService.sendFarmData(jsonObject,sharedprefrenceManager.getToken());
        call.enqueue(new Callback<CommonError>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                //  isCounterCanceled = false;
                if (response.code() == 200) {
                    if (bottomSheetDialogFragment != null)
                        bottomSheetDialogFragment.dismiss();

                    showLoading.dismiss();
                    success("Success", "Delivered Successfully");
//                    getFarmsRoute();


                    Log.e("Datatrue", "" + response.body().getMessage());
                } else
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        //  errorDialog(loginError.getMessage());
                        Log.e("Data", loginError.getMessage());
                        showLoading.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Data", e.toString());
                    }
            }

            @Override
            public void onFailure(Call<CommonError> call, Throwable t) {
                //  isCounterCanceled = false;
                Log.e("errorratro", t.getMessage());
                showLoading.dismiss();
            }

        });
    }
// Error Free on 02_08_2019

    @Override
    public void datasave(String semencoolertemp, String noofbagsloaded, String onloadingtemp, String comment) {
        isCounterCanceled = true;
        deliveryData = new DeliveryData();
        JsonObject postData = new JsonObject();
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            postData.addProperty("id", Integer.parseInt(farmid));
            postData.addProperty("FK_DriverRide_Main_Id", sharedprefrenceManager.getRideId());
            postData.addProperty("FIRMID", Integer.parseInt(farmid));
            postData.addProperty("NumberOfBagsDelivered", noofbagsloaded);
            postData.addProperty("CommentsDelivered", comment);
            postData.addProperty("TemperatureOfSemenDelivered", onloadingtemp);
            postData.addProperty("Action", "Delivered");
            if (location != null) {
                postData.addProperty("FarmLat", location.getLatitude());
                postData.addProperty("FarmLng", location.getLongitude());
            }
            postData.addProperty("DriverId", sharedprefrenceManager.getDriverIdInt());
            postData.addProperty("VehicleId", sharedprefrenceManager.getVehicleIdInt());
            postData.addProperty("UID", sharedprefrenceManager.getDriverIdInt());
            postData.addProperty("CustomerSeemanCoolarTemp", semencoolertemp);
            postData.addProperty("Address", address);
            postData.addProperty("State", state);
            postData.addProperty("City", city);

            postData.addProperty("Country", country);

        } catch (IOException e) {
            e.printStackTrace();
        }
        sendFarmData(postData);
    }



    public void FinishData(String endodometer, String totalmiles) {
//        sendLog("End");
        try {
            addresses1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses1.get(0).getAddressLine(0);
            String city = addresses1.get(0).getLocality();
            String state = addresses1.get(0).getAdminArea();
            String country = addresses1.get(0).getCountryName();

            showLoading.show();
            Call<CommonError> call = apiService.finishRide(sharedprefrenceManager.getRideId(), sharedprefrenceManager.getDriverID(), "" + location.getLatitude(), "" + location.getLongitude(), endodometer, address, state, city, country, "End", totalmiles,sharedprefrenceManager.getDriverID(),sharedprefrenceManager.getToken());
            call.enqueue(new Callback<CommonError>() {
                @Override
                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                    if (response.code() == 200) {
                        //  Log.e("ta", response.body());
                        isPaused = false;
                        showLoading.dismiss();

//                    timer.cancel();
//                    timer.purge();
//                    timer = null;
//
//                    timer1.cancel();
//                    timer1.purge();
//                    timer1 = null;

                        handler1.removeCallbacks(runnable);
                        handler2.removeCallbacks(runnable1);

                        finishCompleteRide();

                        if (bottomSheetDialogFragment != null) {
                            bottomSheetDialogFragment.dismiss();
                        }

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
                    Log.e("errorratro", t.getMessage());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finishCompleteRide() {
        //sendLog("End");

        showLoading.show();
        Call<CommonError> call = apiService.postVehicleStatus(Integer.parseInt(sharedprefrenceManager.getUserId()), Integer.parseInt(sharedprefrenceManager.getVehicleId()), 0, "Finish");
        call.enqueue(new Callback<CommonError>() {
            @Override
            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                if (response.code() == 200) {
                    showLoading.dismiss();

                    sharedprefrenceManager.setRideId(0);

                    success("Alert!", "Ride finished");


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


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(MapsActivityNew.this, "Location Changed", Toast.LENGTH_SHORT).show();
        // getFarmsRoute();
        System.err.println("In Location Changed    " + "  1394 LN");
        this.location = location;
        //Log.d("Analysis__","Location is"+location);
        //Toast.makeText(MapsActivityNew.this, "Location Changed " + location.getLatitude() + " -- " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        Log.e("location", "" + location);
        try {
            if (carMarker != null)
                carMarker.remove();
            setCurrent(location);
        } catch (Exception e) {

            e.printStackTrace();
        }


        if (sendlog.equalsIgnoreCase("true")) {
            if (location != null) {
                sendLog("Start");
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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkRuntimePermission();

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

    private void setCurrent(Location location) {
        System.err.println("In Setting Location     " + "  1435 LN");
        if (location != null && mMap != null) {
            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            carMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car)).position(userLocation).title("Driver").rotation(location.getBearing()));
        }

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
                            status.startResolutionForResult(MapsActivityNew.this, 100);

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

    private void GetUserLocation() {
        System.err.println("In GetUserLocation    " + "  1493 LN");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(3000);
            mLocationRequest.setFastestInterval(3000);
            mLocationRequest.setSmallestDisplacement(10);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            this.location = loc;
            setCurrent(location);
            // get all farms here...
            if (location != null) {
                if (sendlog.equals("true")) {
                    sendLog("Start");
                    sendlog = "false";
                }
                getFarmsRoute();
            }
        }
    }

    private void success(String st1, final String st2) {
        System.err.println("Is Finished from dialog " + isFinised);
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(st1)
                .setContentText(st2)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismiss();

                        try {
                            handler1.removeCallbacks(runnable);
                            handler2.removeCallbacks(runnable1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (st2.equalsIgnoreCase("Delivered Successfully")) {
                            mMap.clear();
                            setUpMap();
                            displayLocationSettingsRequest(MapsActivityNew.this);
                            //                            startActivity(new Intent (MapsActivityNew.this,MapsActivityNew.class));
//                            finish();
                        } else {
                            isFinised = true;
                            Intent intent = new Intent(MapsActivityNew.this, SelectCar.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        System.err.println("Is Finished from dialog " + isFinised);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();

    }

    private void success1(String st1) {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Route Info")
                .setContentText(st1)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.err.println("Destroyed");
        try {
            isCounterCanceled = true;
            isFinised = true;
            handler1.removeCallbacks(runnable);
            handler2.removeCallbacks(runnable1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private double GetBearing(LatLng from, LatLng to) {
        double lat1 = from.latitude * Math.PI / 180.0;
        double lon1 = from.longitude * Math.PI / 180.0;
        double lat2 = to.latitude * Math.PI / 180.0;
        double lon2 = to.longitude * Math.PI / 180.0;

        // Compute the angle.
        double angle = -Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2), Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        if (angle < 0.0)
            angle += Math.PI * 2.0;

        // And convert result to degrees.
        angle = angle * degreesPerRadian;

        return angle;
    }

    private void displayLocationSettingsRequest(final Context context) {
        System.err.println("MAp Setting up....In GOOGLE API CLIENT");
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivityNew.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService();
        isCounterCanceled = false;
        System.out.println("yueyuwyuewyuewyuew_Moving_Resume " + sharedprefrenceManager.getTimeRemaining());
        //Log.d("Analysis__","line 2092");

        if (!sharedprefrenceManager.getTimeRemaining().equalsIgnoreCase("")) {
            timeRemaining = Long.parseLong(sharedprefrenceManager.getTimeRemaining());
            sharedprefrenceManager.setTimeRemaining("");
            //Log.d("Analysis__","line 2097");
        }
        System.out.println("ewuieiueuewue " + pause.getText().toString());
        if (!pause.getText().toString().equalsIgnoreCase("Resume Ride")) {
            //Log.d("Analysis__","line 2101");
            startTimer();
        }
        System.err.println("In Resume " + "  1632 LN");
        try {
            //Log.d("Analysis__","line 2106");
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                //Log.d("Analysis__","line 2108");
                getFarmsRoute();
                if (location != null) {
                    //Log.d("Analysis__","line 2111");
                    carMarker.remove();
                    setCurrent(location);
                    System.err.println("In onResume()  map already setted up");
                }

            } else {
                System.err.println("In onResume()  else part map setting up");
                //Log.d("Analysis__","line 2119");
            //    setUpMap();
                displayLocationSettingsRequest(MapsActivityNew.this);
            }
        } catch (Exception e) {
            //Log.d("Analysis__","line 2124");
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    actionAfterPermissionGranted();
                }
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "";
                //"https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBKuDPXLD9BVKhISPpUd4fyyenbbJGgZRo";

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskDemo parserTask = new ParserTaskDemo();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTaskDemo extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                //DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                //routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            System.out.println("eewieioeoiewioewioewieow " + distance + " -- " + duration);

            // Drawing polyline in the Google Map for the i-th route
        }
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundLocationService.class);
        serviceIntent.putExtra("remainingTime", timeRemaining + "");
        serviceIntent.putExtra("rideAction", pause.getText().toString() + "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            } else {
                // Start your foreground service here
                startForegroundService(serviceIntent);
            }

        }
        //Log.d("Analysis__","2317");
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundLocationService.class);
        stopService(serviceIntent);
    }

}
