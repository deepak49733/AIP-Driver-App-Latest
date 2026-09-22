package com.sc.aipdriver.activities.ui;//package com.studlink.dev.activities;
//
//import android.Manifest;
//import android.annotation.TargetApi;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Rect;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.StrictMode;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.support.design.widget.BottomSheetDialogFragment;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.webkit.WebStorage;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.directions.route.AbstractRouting;
//import com.directions.route.RouteException;
//import com.directions.route.Routing;
//import com.directions.route.RoutingListener;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.PendingResult;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.LocationSettingsResult;
//import com.google.android.gms.location.LocationSettingsStatusCodes;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import com.studlink.dev.aipdriverapp.R;
//import com.studlink.dev.dialogs.ShowLoading;
//import com.studlink.dev.fragments.FinishDialog;
//import com.studlink.dev.fragments.SelectCar;
//import com.studlink.dev.fragments.TempratureDialogFragment;
//import com.studlink.dev.fragments.TutsPlusBottomSheetDialogFragment;
//import com.studlink.dev.interfaces.ApiClient;
//import com.studlink.dev.interfaces.ApiInterface;
//import com.studlink.dev.models.BaseResponse;
//import com.studlink.dev.models.CommonError;
//import com.studlink.dev.models.DeliveryData;
//import com.studlink.dev.models.LatLongData;
//import com.studlink.dev.models.RouteLog;
//import com.studlink.dev.models.RoutePlannerDetail;
//import com.studlink.dev.otherclasses.HttpConnection;
//import com.studlink.dev.otherclasses.PathJSONParser;
//import com.studlink.dev.otherclasses.SharedprefrenceManager;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import cn.pedant.SweetAlert.SweetAlertDialog;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//
//public class MapsActivityNew extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
//        OnMapReadyCallback, RoutingListener, GoogleMap.OnMarkerClickListener, View.OnClickListener, FinishDialog.SendData, TempratureDialogFragment.SendData {
//    ArrayList<LatLng> points = new ArrayList<>();
//    private final Handler handler1 = new Handler();
//    private final Handler handler2 = new Handler();
//    Runnable runnable,runnable1;
//    private static final int LOCATION_PERMISSION = 101;
//    private static final int REQUEST_CHECK_SETTINGS = 10;
//    private GoogleMap mMap;
//    ArrayList<LatLng> latLngslist;
//    ArrayList<LatLng> pointsList;
//    ApiInterface apiService;
//    Gson gson;
//    String farmid;
//    int timedelay;
//    SharedprefrenceManager sharedprefrenceManager;
//    String routeId, screen, sendData, RideId;
//    boolean isFinised= false;
//    DeliveryData deliveryData;
//    ArrayList<DeliveryData> DeliveryDataList;
//    SweetAlertDialog pDialog;
//    ShowLoading showLoading;
//    Location location;
//    private LocationRequest mLocationRequest;
//    private GoogleApiClient mGoogleApiClient;
//    private List<Polyline> polylines;
//    List<RoutePlannerDetail> alDeliveryPendingFarms;
//    HashMap<String, RoutePlannerDetail> latLongDataHashMap;
//    private Marker carMarker;
//    BottomSheetDialogFragment bottomSheetDialogFragment;
//
//    private LatLngBounds.Builder builder;
//    private LatLngBounds bounds;
//    private Polyline polyline;
//    private LatLng userLocation;
//    List<Marker> markersList;
//    int disValue = 0;
//    int durValue = 0;
//
//    @BindView(R.id.pause)
//    Button pause;
//    @BindView(R.id.time)
//    Button time;
//
//    @BindView(R.id.cancel_ride)
//    Button cancel_ride;
//
//    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
//    Handler handler;
//    int Seconds, Minutes, MilliSeconds;
//
//    Geocoder geocoder;
//    List<Address> addresses;
//    List<Address> addresses1;
//
//    private final double degreesPerRadian = 180.0 / Math.PI;
//
//    Timer timer, timer1;
//
//    Bitmap bmScreen;
//
//    Dialog screenDialog;
//    static final int ID_SCREENDIALOG = 1;
//
//    Button btnScreenDialog_OK;
//    TextView tv_contact, tv_cellphone, tv_tbags, tv_temp, tv_createdDate, tv_bagdeliver, tv_temseamadelever, tv_deliverydate;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        
//
//        latLngslist = new ArrayList<>();
//        polylines = new ArrayList<>();
//        alDeliveryPendingFarms = new ArrayList<>();
//        latLongDataHashMap = new HashMap<>();
//        DeliveryDataList = new ArrayList<>();
//
//        showLoading = new ShowLoading(MapsActivityNew.this);
//        routeId = getIntent().getStringExtra("ROUTEID");
//        RideId = getIntent().getStringExtra("RIDEID");
//        screen = getIntent().getStringExtra("SCREEN");
//        apiService = ApiClient.getClient(this).create(ApiInterface.class);
//
//        gson = new GsonBuilder().setPrettyPrinting().create();
//        sharedprefrenceManager = new SharedprefrenceManager(this);
////        sharedprefrenceManager.setRideId(Integer.parseInt(routeId));
//        // sharedprefrenceManager.setFarm_ID(Integer.parseInt(farmid));
//        setUpMap();
//        // sendrequest();
//        displayLocationSettingsRequest(this);
//
//        handler = new Handler();
//
//        geocoder = new Geocoder(this, Locale.getDefault());
//
//        pause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (pause.getText().toString().equalsIgnoreCase("Pause Ride")) {
//                    sendLog("Pause");
////                    StartTime = SystemClock.uptimeMillis();
////                    handler.postDelayed(runnable, 0);
//                    pause.setText("Start Ride");
//                    pause.setTextColor(getResources().getColor(R.color.green));
//
//
//                } else if (pause.getText().toString().equalsIgnoreCase("Start Ride")) {
//                    sendLog("Start");
////                    TimeBuff += MillisecondTime;
//                    pause.setText("Pause Ride");
//                    pause.setTextColor(getResources().getColor(R.color.colorAccent));
//
//                }
//
//            }
//        });
//
//        cancel_ride.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                sendLog("Cancel");
//
//                showLoading.show();
//                System.err.println(sharedprefrenceManager.getDriverID() + "------    " + sharedprefrenceManager.getVehicleId() + "");
//
//                Call<CommonError> call = apiService.postVehicleStatus(Integer.parseInt(sharedprefrenceManager.getDriverID()), Integer.parseInt(sharedprefrenceManager.getVehicleId()), 0, "Finish");
//                System.err.println(sharedprefrenceManager.getUserId() + "------    " + sharedprefrenceManager.getVehicleId() + "");
//
//                call.enqueue(new Callback<CommonError>() {
//                    @Override
//                    public void onResponse(Call<CommonError> call, Response<CommonError> response) {
//                        try {
//                            if (response.code() == 200) {
//                                sharedprefrenceManager.setRideId(0);
//
//                                showLoading.dismiss();
//
////                                timer.cancel();
////                                timer.purge();
////                                timer = null;
////                                timer1.cancel();
////                                timer1.purge();
////                                timer1 = null;
//                                try {
//                                    handler1.removeCallbacks(runnable);
//                                    handler2.removeCallbacks(runnable1);
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
////                                System.err.println("OnCancel ::::" + sharedprefrenceManager.getRideId());
//                                Log.e("OnCancel", sharedprefrenceManager.getRideId());
//
//                                Toast.makeText(MapsActivityNew.this, "Ride Canceled", Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(MapsActivityNew.this, SelectCar.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                finish();
//                            } else {
//                                showLoading.dismiss();
//                                CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                                //  errorDialog(loginError.getMessage());
//                                Log.e("Data", loginError.getMessage());
////                                System.err.println("MAp Activity....Error");
//                            }
//                        } catch (Exception e) {
//                            showLoading.dismiss();
//                            e.printStackTrace();
////                            System.err.println("MAp Activity....Exception");
//                            Log.e("Data", e.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<CommonError> call, Throwable t) {
//                        showLoading.dismiss();
//                        Log.e("errorratro", t.getMessage());
////                        System.err.println("MAp Activity....Failure");
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    protected Dialog onCreateDialog(int id) {
//// TODO Auto-generated method stub
//
//        screenDialog = null;
//        switch (id) {
//            case (ID_SCREENDIALOG):
//                screenDialog = new Dialog(this);
//                screenDialog.setContentView(R.layout.dialog);
//
//                tv_contact = (TextView) screenDialog.findViewById(R.id.tv_contact);
//                tv_cellphone = (TextView) screenDialog.findViewById(R.id.tv_cellphone);
//                tv_tbags = (TextView) screenDialog.findViewById(R.id.tv_bags);
//                tv_temp = (TextView) screenDialog.findViewById(R.id.tv_temp);
//                tv_createdDate = (TextView) screenDialog.findViewById(R.id.tv_createdDate);
//                tv_bagdeliver = (TextView) screenDialog.findViewById(R.id.tv_bagdeliver);
//                tv_temseamadelever = (TextView) screenDialog.findViewById(R.id.tv_temseamadelever);
//                tv_deliverydate = (TextView) screenDialog.findViewById(R.id.tv_deliverydate);
//
//                btnScreenDialog_OK = (Button) screenDialog.findViewById(R.id.okdialogbutton);
//                btnScreenDialog_OK.setOnClickListener(btnScreenDialog_OKOnClickListener);
//        }
//        return screenDialog;
//    }
//
//    @Override
//    protected void onPrepareDialog(int id, Dialog dialog) {
//// TODO Auto-generated method stub
//        switch (id) {
//            case (ID_SCREENDIALOG):
//                dialog.setTitle("Captured Screen");
//                break;
//        }
//    }
//
//    private Button.OnClickListener btnScreenDialog_OKOnClickListener
//            = new Button.OnClickListener() {
//
//        @Override
//        public void onClick(View arg0) {
//            // TODO Auto-generated method stub
//            screenDialog.dismiss();
//        }
//    };
//
//    private void setUpMap() {
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        //mMap.setMyLocationEnabled(true);
////        System.err.println("MAp Setting up....");
//
//    }
//
//    /*private void sendrequest(final Marker marker) {
//        Call<BaseResponse<List<FormDetails>>> call = apiService.getFarmsInformation(farmid,routeId);
//        call.enqueue(new Callback<BaseResponse<List<FormDetails>>>() {
//
//            @Override
//            public void onResponse(Call<BaseResponse<List<FormDetails>>> call, Response<BaseResponse<List<FormDetails>>> response) {
//                if (response.code() == 200) {
//
//                    String  contact=response.body().getData().get(0).getContactName();
//                    String cellphone=response.body().getData().get(0).getCellPhone();
//                    String bags=response.body().getData().get(0).getNumberOfBagsDelivered();
//                    String temp=response.body().getData().get(0).getTemperatureOfSemenDelivered();
//                    String createdDate=response.body().getData().get(0).getCreatedDate();
//                    String bagdeliver =response.body().getData().get(0).getNumberOfBagsDelivered();
//                    String temseamadelever=response.body().getData().get(0).getTemperatureOfSemenDelivered();
//                    String deliverydate=response.body().getData().get(0).getDeliveryDate();
//
//
//                    tv_contact.setText(" " + contact);
//                    tv_cellphone.setText(" " + cellphone);
//                    tv_tbags.setText(" " + bags);
//                    tv_temp.setText(" " + temp);
//                    tv_createdDate.setText(" " + createdDate);
//                    tv_bagdeliver.setText(" " + bagdeliver);
//                    tv_temseamadelever.setText(" " + temseamadelever);
//                    tv_deliverydate.setText(" " + deliverydate);
//                } else
//                    try {
//
// //uncomment dialog animation
//                   //     pDialog.dismissWithAnimation();
//                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//
//                        Log.e("Data", loginError.getMessage());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse<List<FormDetails>>> call, Throwable t) {
//                Log.e("errorratro", t.getMessage());
//            }
//        });
//    }
//*/
//
//    private void getFarmsRoute() {
////        System.err.println("ROute ID  " + routeId + "RIDE ID" + RideId);
//        Call<BaseResponse<LatLongData>> call = apiService.getstartNewRide(sharedprefrenceManager.getRideId());
//        call.enqueue(new Callback<BaseResponse<LatLongData>>() {
//            @Override
//            public void onResponse(Call<BaseResponse<LatLongData>> call, Response<BaseResponse<LatLongData>> response) {
////                System.err.println("MAPS ACTIVITY getFarmsRoute   " + response + "    ----    " + response.body());
//                if (response.code() == 200) {
//                    alDeliveryPendingFarms.clear();
//                    latLngslist.clear();
//                    LatLongData latLongData = response.body().getData();
////                    RoutePlannerDetail routePlannerDetail1;
////                    routePlannerDetail1 = new RoutePlannerDetail();
////                    routePlannerDetail1.setFarmLat(location.getLatitude());
////                    routePlannerDetail1.setFarmLng(location.getLongitude());
////                    routePlannerDetail1.setId("000");
////                    routePlannerDetail1.setFarmName(sharedprefrenceManager.getUsername());
////                    routePlannerDetail1.setPriority(100);
////                    alDeliveryPendingFarms.add(routePlannerDetail1);
//
//                    for (int i = 0; i < latLongData.getRoutePlannerDetail().size(); i++) {
//                        RoutePlannerDetail routePlannerDetail3 = latLongData.getRoutePlannerDetail().get(i);
//                        sharedprefrenceManager.setRideId(Integer.parseInt(routePlannerDetail3.getFKDriverRideMainId()));
//                        if (routePlannerDetail3.getDeleveryTime().equals("")) {
//                            alDeliveryPendingFarms.add(routePlannerDetail3);
//                        }
//                    }
//                    alDeliveryPendingFarms.addAll(latLongData.getRoutePlannerDetail());
//
//                    for (int i = alDeliveryPendingFarms.size()-1; i>=0;i--){
//                        if (!alDeliveryPendingFarms.get(i).getDeleveryTime().equals("")){
//                            alDeliveryPendingFarms.remove(i);
//                        }
//                    }
//                    RoutePlannerDetail routePlannerDetail;
//                    routePlannerDetail = new RoutePlannerDetail();
////                    routePlannerDetail.setFarmLat(45.696146);
////                    routePlannerDetail.setFarmLng(-95.923010);
//                    routePlannerDetail.setFarmLat(28.440766);
//                    routePlannerDetail.setFarmLng(77.070499);
//                    routePlannerDetail.setId("00");
//                    routePlannerDetail.setDeleveryTime("");
//                    routePlannerDetail.setFarmName("SkyLab");
//                    routePlannerDetail.setPriority(0);
//                    alDeliveryPendingFarms.add(routePlannerDetail);
//
//                    addToHashMap(alDeliveryPendingFarms);
//                    //notifyMap();
//
//                    for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {
//                        RoutePlannerDetail routePlannerDetail2 = alDeliveryPendingFarms.get(i);
//                        if (routePlannerDetail2.getDeleveryTime().equals("")) {
//                            LatLng lt = new LatLng(routePlannerDetail2.getFarmLat(), routePlannerDetail2.getFarmLng());
//                            latLngslist.add(lt);
//                        }
//                    }
//
//
//                    if (latLngslist.size() > 0) {
//                        markersList = new ArrayList<Marker>();
//                        for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {
//
//                            MarkerOptions markerOptions;
//                            try {
//
//                                if (alDeliveryPendingFarms.get(i).getPriority() == 100) {
//                                    markerOptions = new MarkerOptions().position(latLngslist.get(i)).title(alDeliveryPendingFarms.get(i).getFarmName()).snippet("Driver Name");
////                                    System.err.println("Priority 100 " + markerOptions.getTitle());
//                                } else {
//
//                                    markerOptions = new MarkerOptions().position(latLngslist.get(i)).title(alDeliveryPendingFarms.get(i).getFarmName()).snippet("Priority " + alDeliveryPendingFarms.get(i).getPriority());
////                                        System.err.println("Priority  " + markerOptions.getTitle());
//                                }
//                                markersList.add(mMap.addMarker(markerOptions));
//                                markersList.get(i).showInfoWindow();
//                            }catch (ArrayIndexOutOfBoundsException e){
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
////                    LatLng mylatlong = new LatLng(45.696146, -95.923010);
//                    LatLng mylatlong = new LatLng(28.440766, 77.070499);
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlong, 12));
//
//
//                    drawRoutes();
//
////                    String url = getMapsApiDirectionsUrl();
////                    ReadTask downloadTask = new ReadTask();
////                    downloadTask.execute(url);
//
//
//                } else
//                    try {
//                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                        // errorDialog(loginError.getMessage());
//                        System.err.println("");
//                        Log.e("Data Get Farmslist", loginError.getMessage());
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse<LatLongData>> call, Throwable t) {
//                Log.e("errorratro", t.getMessage());
////                System.err.println("MAPS ACTIVITY getFarmsRoute  Failure ");
//
//            }
//        });
//    }
//
//    private void drawRoutes() {
//        try {
//            latLngslist.add(0,new LatLng(location.getLatitude(),location.getLongitude()));
//
//            if (latLngslist.size()>2) {
//                Routing routing = new Routing.Builder()
//                        .travelMode(AbstractRouting.TravelMode.DRIVING)
//                        .withListener(MapsActivityNew.this)
//                        .alternativeRoutes(true)
//                        .key("AIzaSyBKuDPXLD9BVKhISPpUd4fyyenbbJGgZRo")
//                        .waypoints(latLngslist)
//                        .optimize(true)
//                        .build();
//                routing.execute();
//            }
//            else {
//                Routing routing = new Routing.Builder()
//                        .travelMode(AbstractRouting.TravelMode.DRIVING)
//                        .withListener(MapsActivityNew.this)
//                        .alternativeRoutes(true)
//                        .key("AIzaSyBKuDPXLD9BVKhISPpUd4fyyenbbJGgZRo")
//                        .waypoints(latLngslist)
//                        .optimize(false)
//                        .build();
//                routing.execute();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void addToHashMap(List<RoutePlannerDetail> alDeliveryPendingFarms) {
//        for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {
//            RoutePlannerDetail routePlannerDetail = alDeliveryPendingFarms.get(i);
//            if (alDeliveryPendingFarms.get(i).getPriority() == 100) {
//                latLongDataHashMap.put("Driver Name", routePlannerDetail);
//            } else {
//                latLongDataHashMap.put("Priority " + alDeliveryPendingFarms.get(i).getPriority(), routePlannerDetail);
//            }
//        }
//        System.err.println("ADded in Hashmap");
//    }
//
//    private void notifyMap() {
//        for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {
//            RoutePlannerDetail routePlannerDetail = alDeliveryPendingFarms.get(i);
//            sharedprefrenceManager.setRideId(Integer.parseInt((alDeliveryPendingFarms.get(alDeliveryPendingFarms.size() - 1)).getFKDriverRideMainId()));
//            LatLng lt = new LatLng(routePlannerDetail.getFarmLat(), routePlannerDetail.getFarmLng());
//            latLngslist.add(lt);
//        }
//
//        if (latLngslist.size() > 0) {
//            markersList = new ArrayList<Marker>();
//
//            for (int i = 0; i < alDeliveryPendingFarms.size(); i++) {
//                MarkerOptions markerOptions;
//                if (alDeliveryPendingFarms.get(i).getPriority() == 100) {
//                    markerOptions = new MarkerOptions().position(latLngslist.get(i)).title(alDeliveryPendingFarms.get(i).getFarmName()).snippet("Driver Name");
//                } else {
//                    markerOptions = new MarkerOptions().position(latLngslist.get(i)).title(alDeliveryPendingFarms.get(i).getFarmName()).snippet("Priority " + alDeliveryPendingFarms.get(i).getPriority());
//                }
//                markersList.add(mMap.addMarker(markerOptions));
//                markersList.get(i).showInfoWindow();
//            }
//
////            LatLng mylatlong = new LatLng(45.696146, -95.923010);
//            LatLng mylatlong = new LatLng(28.440766, 77.070499);
//            MarkerOptions markeroption1 = new MarkerOptions().position(mylatlong).title("Sky Lab").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
//            markersList.add(mMap.addMarker(markeroption1));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlong, 12));
//
//            try {
//                Routing routing = new Routing.Builder()
//                        .travelMode(AbstractRouting.TravelMode.DRIVING)
//                        .withListener(MapsActivityNew.this)
//                        .alternativeRoutes(false)
////                        .key("AIzaSyDNgRKdWgZKO5PpvJSC1qSOw2Gj0AVCU9E")
//                        .key("AIzaSyBKuDPXLD9BVKhISPpUd4fyyenbbJGgZRo")
//                        .waypoints(latLngslist)
//                        .optimize(true)
//                        .build();
//                routing.execute();
//            } catch (Exception e) {
//
//            }
//
//            String url = getMapsApiDirectionsUrl();
//            ReadTask downloadTask = new ReadTask();
//            downloadTask.execute(url);
//        }
//
//    }
//
//    private String getMapsApiDirectionsUrl() {
//        String url = "";
//        String output = "";
//        String parameters = "";
//
//        String str_origin = "";
//        String str_dest = "";
//
//        str_origin = "origin=" + latLngslist.get(0).latitude + "," + latLngslist.get(0).longitude;
//
//        str_dest = "destination=" + latLngslist.get(latLngslist.size() - 1).latitude + "," + latLngslist.get(latLngslist.size() - 1).longitude;
//
//        String sensor = "sensor=false";
//        String mode = "mode=driving";
//        output = "json";
//
//        parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
//        url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyDNgRKdWgZKO5PpvJSC1qSOw2Gj0AVCU9E";
//
//        DrawArrowHead(mMap, new LatLng(latLngslist.get(0).latitude, latLngslist.get(0).longitude), new LatLng(latLngslist.get(latLngslist.size() - 1).latitude, latLngslist.get(latLngslist.size() - 1).longitude));
//
//        return url;
//    }
//
//    @Override
//    public void onClick(View view) {
//
//    }
//
//
//    private class ReadTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... url) {
//            String data = "";
//            try {
//                HttpConnection http = new HttpConnection();
//                data = http.readUrl(url[0]);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            new ParserTask().execute(result);
//        }
//    }
//
//    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
//            JSONObject jObject;
//            List<List<HashMap<String, String>>> routes = null;
//            try {
//                jObject = new JSONObject(jsonData[0]);
//                PathJSONParser parser = new PathJSONParser();
//                routes = parser.parse(jObject);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
//
//            points.clear();
//            PolylineOptions polyLineOptions = null;
//
//            String disvalue = "";
//            String durvalue = "";
//
//            int finalDis = 0;
//            int finalDur = 0;
//
//            int distance1 = 0;
//
//            int hours = 0;
//            int minutes = 0;
//
//            // traversing through routes
//            for (int i = 0; i < routes.size(); i++) {
//                points = new ArrayList<LatLng>();
//                polyLineOptions = new PolylineOptions();
//
//                List<HashMap<String, String>> path = routes.get(i);
//
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap<String, String> point = path.get(j);
//
//                    if (j == 0) {
//                        disvalue = (String) point.get("distance");
//                        continue;
//                    } else if (j == 1) {
//                        durvalue = (String) point.get("duration");
//                        continue;
//                    }
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//                    points.add(position);
//                }
//
//                polyLineOptions.addAll(points);
//                polyLineOptions.width(3);
//                polyLineOptions.color(Color.RED);
//                polyline = mMap.addPolyline(polyLineOptions);
//                polylines.add(polyline);
//
//                finalDis = Integer.parseInt(disvalue) + disValue;
//                finalDur = Integer.parseInt(durvalue) + durValue;
//
//                distance1 = Math.round(finalDis / 1608);
//
//                hours = Math.round(finalDur / (60 * 60));
//                minutes = Math.round(((finalDur / 1000) % ((60 * 60))));
//            }
//
//            success1("Total Distance : " + distance1 + " miles" + "\n\nTotal Time : " + hours + " hours " + minutes + " minutes");
//        }
//    }
//
//    //OnMapReady
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        mMap = googleMap;
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
////                startIn
//
//
//
//            }
//        });
//        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }*/
//
//        // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        //mMap.setMyLocationEnabled(true);
//        //  mMap.getMyLocation(location.getBearing());
//
//
//        //  mMap.getUiSettings().setMapToolbarEnabled(true);
//        // mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
//        mMap.setOnMarkerClickListener(this);
//        buildGoogleApiClient();
//        System.err.println("In map Ready isFinished "+isFinised);
//        if (!isFinised){
//            Runnable runnable = new Runnable() {
//                public void run() {
//                    //
//                    // Do the stuff
//                    //
//                    if (location != null) {
//                        sendLog("Moving");
//                        checkArrivedStatus(true);
//                    }
//
//                    handler1.postDelayed(this, 120000);
//                }
//            };
//            runnable.run();
//
//
//            runnable1 = new Runnable() {
//                public void run() {
//                    //
//                    // Do the stuff
//                    //
//                    if (location != null)
//                        checkArrivedStatus(false);
//                    handler2.postDelayed(this, 120000);
//                }
//            };
//            runnable1.run();
////
//
////            timer = new Timer();
////        TimerTask hourlyTask = new TimerTask() {
////            @Override
////            public void run() {
////                sendLog("Moving");
////                checkArrivedStatus(true);
////
////            }
////        };
////        timer.schedule(hourlyTask, 120000, 120000);
//////
//////
////        timer1 = new Timer();
////        TimerTask hourlyTask1 = new TimerTask() {
////            @Override
////            public void run() {
////                if (location != null) {
////                    checkArrivedStatus(false);
////                }
////            }
////        };
////        timer1.schedule(hourlyTask1, 120000, 120000);
//        }
//    }
//
//    private void checkArrivedStatus(boolean isMoving){
//        for(int i=0; i<alDeliveryPendingFarms.size(); i++) {
//
//            if (alDeliveryPendingFarms.get(i).getPriority() != 100) {
//
//                CalculationByDistance(alDeliveryPendingFarms.get(i).getFarmLat(), alDeliveryPendingFarms.get(i).getFarmLng(), isMoving);
//
//            }
//        }
//    }
//
//
//    private void startNavigation(LatLng latLng){
//
//        // Create a Uri from an intent string. Use the result to create an Intent.
////                Uri gmmIntentUri = Uri.parse("google.navigation:q=28.4497,77.0705,+28.4591,77.0726,+28.610000,77.232150");
//        Uri gmmIntentUri = Uri.parse("google.navigation:q=Connaught+Place,+New+Delhi,Delhi");
//
//// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//// Make the Intent explicit by setting the Google Maps package
//        mapIntent.setPackage("com.google.android.apps.maps");
//
//// Attempt to start an activity that can handle the Intent
//        if (mapIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(mapIntent);
//        }
//
//
//    }
//
//
//    private void CalculationByDistance(double lat, double lng, final boolean isMoving){
//        int Radius = 6371; // radius of earth in Km
//        double lat1 = lat;
//        double lat2 = userLocation.latitude;
//        double lon1 = lng;
//        double lon2 = userLocation.longitude;
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLon = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
//                + Math.cos(Math.toRadians(lat1))
//                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
//                * Math.sin(dLon / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        double valueResult = Radius * c;
//        final double meter = valueResult*1000;
//
//        if (!isMoving) {
//            timedelay = 3000;
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (meter <= 100.0) {
//                        Toast.makeText(MapsActivityNew.this, "Arrived", Toast.LENGTH_SHORT).show();
//                        timedelay=3000;
//                        // sendLog("Arrived");
//                    }
//                }
//            },timedelay);
//        }
//    }
//
//    private void sendLog(String status) {
//        if (!isFinised){
//            System.err.println("Sending Logs  while isFinished   "  + isFinised);
//            try {
//                RouteLog routeLog = new RouteLog();
//                if (status.equalsIgnoreCase("Moving")) {
//                    routeLog.setAction("Moving");
//                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
//                } else if (status.equalsIgnoreCase("Start")) {
//                    routeLog.setAction("Start");
//                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
//                } else if (status.equalsIgnoreCase("Pause")) {
//                    routeLog.setAction("Pause");
//                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
//                } else if (status.equalsIgnoreCase("Arrived")) {
//                    routeLog.setAction("Arrived");
//                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
//                } else if (status.equalsIgnoreCase("Cancel")) {
//                    routeLog.setAction("Cancel");
//                    isFinised=true;
//                    routeLog.setLat("" + 28.440766);
//                    routeLog.setLng("" + 77.070499);
////            routeLog.setLat("" + 45.696146);
////            routeLog.setLng("" + -95.923010);
//                } else if (status.equalsIgnoreCase("End")) {
//                    routeLog.setAction("End");
//                    routeLog.setLat("" + location.getLatitude());
//                    routeLog.setLng("" + location.getLongitude());
//                }
//
//                try {
//
//                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                    String address = addresses.get(0).getAddressLine(0);
//                    String city = addresses.get(0).getLocality();
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//
//                    routeLog.setCity(city);
//                    routeLog.setAddress(address);
//                    routeLog.setCountry(country);
//                    routeLog.setState(state);
//                    //sendrequest(markersList);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                routeLog.setDriverId(sharedprefrenceManager.getUserId());
//                routeLog.setRideId(sharedprefrenceManager.getRideId());
//                routeLog.setRouteId(sharedprefrenceManager.getRouteId());
//                routeLog.setUID(sharedprefrenceManager.getUserId());
//                routeLog.setVehicleId(sharedprefrenceManager.getVehicleId());
//
//                Call<CommonError> call = apiService.sendLog(routeLog);
//                call.enqueue(new Callback<CommonError>() {
//                    @Override
//                    public void onResponse(Call<CommonError> call, Response<CommonError> response) {
//                        if (response.code() == 200) {
//                            Log.e("Datatrue", "" + response.body().getMessage());
//                        } else
//                            try {
//                                CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                                //  errorDialog(loginError.getMessage());
//                                Log.e("Data", loginError.getMessage());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                Log.e("Data", e.toString());
//                            }
//                    }
//
//                    @Override
//                    public void onFailure(Call<CommonError> call, Throwable t) {
//                        Log.e("errorratro", t.getMessage());
//                    }
//
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override  public void onRoutingFailure(RouteException e) {
//        Log.e("error", "" + e.toString());
//        System.err.println("Caught");
//        Toast.makeText(MapsActivityNew.this, e.getMessage(), Toast.LENGTH_LONG).show();
//
//    }
//
//    @Override  public void onRoutingStart() {
//
//    }
//
//    @Override  public void onRoutingSuccess(ArrayList<com.directions.route.Route> route, int j) {
//        pointsList = new ArrayList<>();
//        pointsList.clear();
//        pointsList= (ArrayList<LatLng>) route.get(0).getPoints();
//        for (int i = 0; i < route.size(); i++) {
//            PolylineOptions polyOptions = new PolylineOptions();
//            polyOptions.color(Color.BLUE);
//            polyOptions.geodesic(true);
//            Log.d("Route Path   ", "onRoutingSuccess: "+route.get(i).getPoints());
//            polyOptions.addAll(route.get(i).getPoints());
//            polyline = mMap.addPolyline(polyOptions);
//
//            polylines.add(polyline);
//            route.get(i).setName("farm");
//
//            disValue = route.get(i).getDistanceValue();
//            durValue = route.get(i).getDurationValue();
//        }
//        for (int k = 0; k < latLngslist.size()-1; k++) {
//            DrawArrowHead(mMap, new LatLng(latLngslist.get(k).latitude, latLngslist.get(k).longitude), new LatLng(latLngslist.get(k + 1).latitude, latLngslist.get(k + 1).longitude));
//        }
//
//    }
//
//    @Override
//    public void onRoutingCancelled() {
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_temp) {
//            BottomSheetDialogFragment bottomSheetDialogFragment = new TempratureDialogFragment();
//            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//            return true;
//        }
//        if (id == R.id.action_farmlist) {
//            BottomSheetDialogFragment bottomSheetDialogFragment = new TutsPlusBottomSheetDialogFragment();
//            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    @Override
//    public boolean onMarkerClick(Marker marker) {
////        String MarkerTitle="";
////        if (marker.getTitle()!=null)
//        String MarkerTitle= marker.getTitle();
//        System.err.println("Marker Clicked  "+ marker.getTitle());
//        try {
//
//            if (MarkerTitle.equals("SkyLab")) {
//                //System.err.println("MarkerClick"+ MarkerTitle);
//                bottomSheetDialogFragment = new FinishDialog().instance(marker.getSnippet(), this);
//                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//
//                return true;
//            } else if (MarkerTitle.equals("Driver")) {
//                System.err.println("FALSE DRIVER CLICKED");
//                return false;
//            } else {
//                System.err.println("Snippet   "+marker.getSnippet());
//                RoutePlannerDetail routePlannerDetail = latLongDataHashMap.get(marker.getSnippet());
//                if (routePlannerDetail.getDeleveryTime().equals("")) {
//                    farmid = routePlannerDetail.getFIRMID();
//                    Log.e("FARM_ID", farmid);
//                    bottomSheetDialogFragment = new TempratureDialogFragment().instance(routePlannerDetail, marker.getSnippet(), this);
//                    bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
//                    return true;
//                }
//                else{
//                    System.err.println("In ELSE PART");
//                }
//
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
//    }
//
////    private void sendFarmData(ArrayList<DeliveryData> deliveryDataList) {
////        showLoading.show();
////        Call<CommonError> call = apiService.sendFarmData(deliveryDataList);
////        call.enqueue(new Callback<CommonError>() {
////            @Override
////            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
////                if (response.code() == 200) {
////                    if (bottomSheetDialogFragment != null)
////                        bottomSheetDialogFragment.dismiss();
////
////                    showLoading.dismiss();
////                    success("Success", "Delivered successfully");
////                    Log.e("Datatrue", "" + response.body().getMessage());
////                } else
////                    try {
////                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
////                        //  errorDialog(loginError.getMessage());
////                        Log.e("Data", loginError.getMessage());
////                        showLoading.dismiss();
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                        Log.e("Data", e.toString());
////                    }
////            }
////
////            @Override
////            public void onFailure(Call<CommonError> call, Throwable t) {
////                Log.e("errorratro", t.getMessage());
////                showLoading.dismiss();
////            }
////
////        });
////    }
//
//
//    // Delivery Data
//    private void sendFarmData(JsonObject jsonObject) {
//        showLoading.show();
//        System.err.println(jsonObject + " ");
//        Call<CommonError> call = apiService.sendFarmData(jsonObject);
//        call.enqueue(new Callback<CommonError>() {
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
//                if (response.code() == 200) {
//                    if (bottomSheetDialogFragment != null)
//                        bottomSheetDialogFragment.dismiss();
//
//                    showLoading.dismiss();
//                    success("Success", "Delivered Successfully");
////                    getFarmsRoute();
//                    startActivity(new Intent (MapsActivityNew.this,MapsActivityNew.class));
//                    finish();
//
//                    Log.e("Datatrue", "" + response.body().getMessage());
//                } else
//                    try {
//                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                        //  errorDialog(loginError.getMessage());
//                        Log.e("Data", loginError.getMessage());
//                        showLoading.dismiss();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e("Data", e.toString());
//                    }
//            }
//
//            @Override
//            public void onFailure(Call<CommonError> call, Throwable t) {
//                Log.e("errorratro", t.getMessage());
//                showLoading.dismiss();
//            }
//
//        });
//    }
//// Error Free on 02_08_2019
//
//
//    //    @Override
////    public void datasave(String semencoolertemp, String noofbagsloaded, String onloadingtemp, String comment) {
////        deliveryData = new DeliveryData();
////
////        deliveryData.setId(farmid);
////        deliveryData.setDriverId(sharedprefrenceManager.getUserId());
////        deliveryData.setVehicleId(sharedprefrenceManager.getVehicleId());
////        deliveryData.setRouteId(sharedprefrenceManager.getRouteId());
////        try {
////            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
////            String address = addresses.get(0).getAddressLine(0);
////            String city = addresses.get(0).getLocality();
////            String state = addresses.get(0).getAdminArea();
////            String country = addresses.get(0).getCountryName();
////            deliveryData.setCountry(country);
////            deliveryData.setState(state);
////            deliveryData.setCity(city);
////            deliveryData.setAddress(address);
////        }
////        catch (IOException e)
////        {
////            e.printStackTrace();
////        }
////
////        deliveryData.setNumberOfBagsDelivered(noofbagsloaded);
////        deliveryData.setTemperatureOfSemenDelivered(onloadingtemp);
////        deliveryData.setCommentsDelivered(comment);
////        if (location != null) {
////            deliveryData.setFarmLat("" + location.getLatitude());
////            deliveryData.setFarmLng("" + location.getLongitude());
////        }
////        deliveryData.setAction("Delivered");
////        deliveryData.setFkrideid(routeId);
////        deliveryData.setCustomerSeemanCoolarTemp(semencoolertemp);
////
////
////        DeliveryDataList.add(0, deliveryData);
////        sendFarmData(DeliveryDataList);
////    }
//    @Override
//    public void datasave(String semencoolertemp, String noofbagsloaded, String onloadingtemp, String comment) {
//        deliveryData = new DeliveryData();
//        JsonObject postData= new JsonObject();
//        try {
//            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            String address = addresses.get(0).getAddressLine(0);
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            postData.addProperty("id",Integer.parseInt(farmid));
//            postData.addProperty("FK_DriverRide_Main_Id",sharedprefrenceManager.getRideId());
//            postData.addProperty("FIRMID",Integer.parseInt(farmid));
//            postData.addProperty("NumberOfBagsDelivered",noofbagsloaded);
//            postData.addProperty("CommentsDelivered",comment);
//            postData.addProperty("TemperatureOfSemenDelivered",onloadingtemp);
//            postData.addProperty("Action","Delivered");
//            if (location != null) {
//                postData.addProperty("FarmLat", location.getLatitude());
//                postData.addProperty("FarmLng", location.getLongitude());
//            }
//            postData.addProperty("DriverId",Integer.parseInt(sharedprefrenceManager.getDriverID()));
//            postData.addProperty("VehicleId",Integer.parseInt(sharedprefrenceManager.getVehicleId()));
//            postData.addProperty("UID",Integer.parseInt(sharedprefrenceManager.getDriverID()));
//            postData.addProperty("CustomerSeemanCoolarTemp",semencoolertemp);
//            postData.addProperty("Address",address);
//            postData.addProperty("State",state);
//            postData.addProperty("City",city);
//
//            postData.addProperty("Country",country);
//
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//        sendFarmData(postData);
//    }
//
//    @Override
//    public void finishData(String endodometerreading) {
//
//        FinishData(endodometerreading);
//    }
//
//    public void FinishData(String endodometer) {
////        sendLog("End");
//        try {
//            addresses1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            String address = addresses1.get(0).getAddressLine(0);
//            String city = addresses1.get(0).getLocality();
//            String state = addresses1.get(0).getAdminArea();
//            String country = addresses1.get(0).getCountryName();
//
//            showLoading.show();
//            Call<CommonError> call = apiService.finishRide(sharedprefrenceManager.getRideId(), sharedprefrenceManager.getDriverID(), "" + location.getLatitude(), "" + location.getLongitude(), endodometer, address, state, city, country, "End");
//            call.enqueue(new Callback<CommonError>() {
//                @Override
//                public void onResponse(Call<CommonError> call, Response<CommonError> response) {
//                    if (response.code() == 200) {
//                        //  Log.e("ta", response.body());
//
//                        showLoading.dismiss();
//
////                    timer.cancel();
////                    timer.purge();
////                    timer = null;
////
////                    timer1.cancel();
////                    timer1.purge();
////                    timer1 = null;
//
//                        handler1.removeCallbacks(runnable);
//                        handler2.removeCallbacks(runnable1);
//
//                        finishCompleteRide();
//
//                        if (bottomSheetDialogFragment != null) {
//                            bottomSheetDialogFragment.dismiss();
//                        }
//
//                    } else
//                        try {
//                            showLoading.dismiss();
//                            CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                            //  errorDialog(loginError.getMessage());
//                            Log.e("Data", loginError.getMessage());
//                        } catch (Exception e) {
//                            showLoading.dismiss();
//                            e.printStackTrace();
//                            Log.e("Data", e.toString());
//                        }
//                }
//
//                @Override
//                public void onFailure(Call<CommonError> call, Throwable t) {
//                    Log.e("errorratro", t.getMessage());
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void drawMarker(LatLng point){
//
//        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.endcap)).position(userLocation));
//// Adding marker on the Google Map
//    }
//    private void finishCompleteRide() {
//        //sendLog("End");
//
//        showLoading.show();
//        Call<CommonError> call = apiService.postVehicleStatus(Integer.parseInt(sharedprefrenceManager.getUserId()), Integer.parseInt(sharedprefrenceManager.getVehicleId()), 0, "Finish");
//        call.enqueue(new Callback<CommonError>() {
//            @Override
//            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
//                if (response.code() == 200) {
//                    showLoading.dismiss();
//
//                    sharedprefrenceManager.setRideId(0);
//
//                    success("Alert!", "Ride finished");
//
//
//                } else
//                    try {
//                        showLoading.dismiss();
//                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                        //  errorDialog(loginError.getMessage());
//                        Log.e("Data", loginError.getMessage());
//                    } catch (Exception e) {
//                        showLoading.dismiss();
//                        e.printStackTrace();
//                        Log.e("Data", e.toString());
//                    }
//            }
//
//            @Override
//            public void onFailure(Call<CommonError> call, Throwable t) {
//                showLoading.dismiss();
//                Log.e("errorratro", t.getMessage());
//            }
//        });
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        // getFarmsRoute();
//
//        this.location = location;
//        Log.e("location", "" + location);
//        carMarker.remove();
//        setCurrent(location);
//
////        new checkRoute().execute();
//
//    }
//
//
//    class checkRoute extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected void onPreExecute()
//        {
//
//            super.onPreExecute();
//        }
//        @Override
//        protected String doInBackground(Void... arg0)
//        {
//            for (int i =0;i<pointsList.size();i++){
//                if (pointsList.get(i).latitude==location.getLatitude() && pointsList.get(i).longitude==location.getLongitude()){
//                    System.err.println("background");
//                }
//                else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MapsActivityNew.this, "", Toast.LENGTH_SHORT).show();
//                            drawRoutes();
//                        }
//                    });
//
//                }
//            }
//            //Record method
//            return "";
//        }
//
//        @Override
//        protected void onPostExecute(String result)
//        {
//            super.onPostExecute(result);
//
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        checkRuntimePermission();
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        buildGoogleApiClient();
//    }
//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }
//
//    private void setCurrent(Location location) {
//        if (location != null && mMap != null) {
//            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//            carMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car)).position(userLocation).title("Driver").rotation(location.getBearing()));
//        }
//
//    }
//
//    private void checkRuntimePermission() {
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
//        } else
//            actionAfterPermissionGranted();
//
//    }
//
//    private void actionAfterPermissionGranted() {
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(3000);
//        mLocationRequest.setFastestInterval(3000);
//        mLocationRequest.setSmallestDisplacement(10);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
//        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult locationSettingsResult) {
//
//                final Status status = locationSettingsResult.getStatus();
//
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // All location settings are satisfied. The client can initialize location
//                        // requests here.
//                        GetUserLocation();
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult(MapsActivityNew.this, 100);
//
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//
//                        break;
//                }
//            }
//        });
//    }
//
//    private void GetUserLocation() {
//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            mMap.setMyLocationEnabled(true);
//            mLocationRequest = LocationRequest.create();
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            mLocationRequest.setInterval(3000);
//            mLocationRequest.setFastestInterval(3000);
//            mLocationRequest.setSmallestDisplacement(10);
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            this.location = loc;
//            setCurrent(location);
//            // get all farms here...
//            if(location!=null) {
//                getFarmsRoute();
//            }
//        }
//    }
//
//    private void success(String st1, String st2) {
//        System.err.println("Is Finished from dialog "+isFinised);
//        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
//                .setTitleText(st1)
//                .setContentText(st2)
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//
//                        sweetAlertDialog.dismiss();
//                        isFinised=true;
//                        try {
//                            handler1.removeCallbacks(runnable);
//                            handler2.removeCallbacks(runnable1);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        Intent intent = new Intent(MapsActivityNew.this, SelectCar.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//
//                        System.err.println("Is Finished from dialog "+isFinised);
//
//
//                    }
//                })
//                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        sweetAlertDialog.dismiss();
//                    }
//                })
//                .show();
//
//    }
//
//    private void success1(String st1) {
//        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
//                .setTitleText("Route Info")
//                .setContentText(st1)
//                .show();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        System.err.println("Destroyed");
//        try {
//            isFinised=true;
//            handler1.removeCallbacks(runnable);
//            handler2.removeCallbacks(runnable1);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    private void DrawArrowHead(GoogleMap mMap, LatLng from, LatLng to) {
//        // obtain the bearing between the last two points
//        double bearing = GetBearing(from, to);
//
//        // round it to a multiple of 3 and cast out 120s
//        double adjBearing = Math.round(bearing / 3) * 3;
//        while (adjBearing >= 120) {
//            adjBearing -= 120;
//        }
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        // Get the corresponding triangle marker from Google
//        URL url;
//        Bitmap image = null;
//
//        try {
//            url = new URL("http://www.google.com/intl/en_ALL/mapfiles/dir_" + String.valueOf((int)adjBearing) + ".png");
//            try {
//                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } catch (MalformedURLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        if (image != null){
//            // Anchor is ratio in range [0..1] so value of 0.5 on x and y will center the marker image on the lat/long
//            float anchorX = 0.5f;
//            float anchorY = 0.5f;
//
//            int offsetX = 0;
//            int offsetY = 0;
//
//            // images are 24px x 24px
//            // so transformed image will be 48px x 48px
//
//            //315 range -- 22.5 either side of 315
//            if (bearing >= 292.5 && bearing < 335.5){
//                offsetX = 24;
//                offsetY = 24;
//            }
//            //270 range
//            else if (bearing >= 247.5 && bearing < 292.5){
//                offsetX = 24;
//                offsetY = 12;
//            }
//            //225 range
//            else if (bearing >= 202.5 && bearing < 247.5){
//                offsetX = 24;
//                offsetY = 0;
//            }
//            //180 range
//            else if (bearing >= 157.5 && bearing < 202.5){
//                offsetX = 12;
//                offsetY = 0;
//            }
//            //135 range
//            else if (bearing >= 112.5 && bearing < 157.5){
//                offsetX = 0;
//                offsetY = 0;
//            }
//            //90 range
//            else if (bearing >= 67.5 && bearing < 112.5){
//                offsetX = 0;
//                offsetY = 12;
//            }
//            //45 range
//            else if (bearing >= 22.5 && bearing < 67.5){
//                offsetX = 0;
//                offsetY = 24;
//            }
//            //0 range - 335.5 - 22.5
//            else {
//                offsetX = 12;
//                offsetY = 24;
//            }
//
//            Bitmap wideBmp;
//            Canvas wideBmpCanvas;
//            Rect src, dest;
//
////             Create larger bitmap 4 times the size of arrow head image
//            wideBmp = Bitmap.createBitmap(image.getWidth() * 2, image.getHeight() * 2, image.getConfig());
//
//            wideBmpCanvas = new Canvas(wideBmp);
//
//            src = new Rect(0, 0, image.getWidth(), image.getHeight());
//            dest = new Rect(src);
//            dest.offset(offsetX, offsetY);
//
//            wideBmpCanvas.drawBitmap(image, src, dest, null);
//
//            mMap.addMarker(new MarkerOptions()
//                    .position(to)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.endcap))
//                    .anchor(anchorX, anchorY)
//                    .rotation((float) bearing)
//                    .flat(true));
//
//            mMap.addMarker(new MarkerOptions()
//                    .position(from)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.endcap))
//                    .anchor(anchorX, anchorY)
//                    .rotation((float) bearing)
//                    .flat(true));
//        }
//    }
//
//    private double GetBearing(LatLng from, LatLng to) {
//        double lat1 = from.latitude * Math.PI / 180.0;
//        double lon1 = from.longitude * Math.PI / 180.0;
//        double lat2 = to.latitude * Math.PI / 180.0;
//        double lon2 = to.longitude * Math.PI / 180.0;
//
//        // Compute the angle.
//        double angle = - Math.atan2( Math.sin( lon1 - lon2 ) * Math.cos( lat2 ), Math.cos( lat1 ) * Math.sin( lat2 ) - Math.sin( lat1 ) * Math.cos( lat2 ) * Math.cos( lon1 - lon2 ) );
//
//        if (angle < 0.0)
//            angle += Math.PI * 2.0;
//
//        // And convert result to degrees.
//        angle = angle * degreesPerRadian;
//
//        return angle;
//    }
//
//    private void displayLocationSettingsRequest(final Context context) {
//        System.err.println("MAp Setting up....In GOOGLE API CLIENT");
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
//                .addApi(LocationServices.API).build();
//        googleApiClient.connect();
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(3000);
//        locationRequest.setFastestInterval(3000);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        Log.i("TAG", "All location settings are satisfied.");
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
//
//                        try {
//                            // in onActivityResult().
//                            status.startResolutionForResult(MapsActivityNew.this, REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException e) {
//                            Log.i("TAG", "PendingIntent unable to execute request.");
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
//                        break;
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
//            getFarmsRoute();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case LOCATION_PERMISSION: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    actionAfterPermissionGranted();
//                }
//            }
//        }
//    }
//}
////