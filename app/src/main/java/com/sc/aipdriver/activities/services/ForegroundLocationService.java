package com.sc.aipdriver.activities.services;



import static com.sc.aipdriver.activities.otherclasses.App.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.ui.MapsActivityNew;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.RouteLog;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;


import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForegroundLocationService extends Service {

    String string = "";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    boolean isFinised = false;
    Location locationObject;
    Geocoder geocoder;
    List<Address> addresses;
    SharedprefrenceManager sharedprefrenceManager;
    ApiInterface apiService;
    Gson gson;
    Timer timer;
    private long timeRemaining = 30000;
    private String rideAction = "";
    Boolean isCounterCanceled = false;

    public class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            locationObject = location;
            //setLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        timer = new Timer( );
//        geocoder = new Geocoder(this, Locale.getDefault());
//        sharedprefrenceManager = new SharedprefrenceManager(this);
//        apiService = ApiClient.getClient(this).create(ApiInterface.class);
//        gson = new GsonBuilder().setPrettyPrinting().create();
//        initializeLocationManager();
//
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.PASSIVE_PROVIDER,
//                    LOCATION_INTERVAL,
//                    LOCATION_DISTANCE,
//                    mLocationListeners[0]
//            );
//        } catch (SecurityException ex) {
//        } catch (IllegalArgumentException ex) {
//        }
//    }

    private void setLocation(){

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                sendLog("Moving");
            }
        }, 5000, 30*1000);

    }

    private void sendLog(String status) {
        if (!isFinised) {
            try {
                RouteLog routeLog = new RouteLog();
                routeLog.setLat("" + locationObject.getLatitude());
                routeLog.setLng("" + locationObject.getLongitude());
                routeLog.setAction(status);

                try {

                    addresses = geocoder.getFromLocation(locationObject.getLatitude(), locationObject.getLongitude(), 1);
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
                            //callF1();
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

    private void callF1(){
       // Toast.makeText(this, "Finished", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Send Log Successfully " + locationObject.getLatitude() + " - " + locationObject.getLongitude() + " - " + sharedprefrenceManager.getUserId(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForegroundServiceSafely(); // ✅ FIRST LINE

        // Now do heavy work AFTER
        timer = new Timer();
        geocoder = new Geocoder(this, Locale.getDefault());
        sharedprefrenceManager = new SharedprefrenceManager(this);
        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        gson = new GsonBuilder().setPrettyPrinting().create();
        initializeLocationManager();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        LOCATION_INTERVAL,
                        LOCATION_DISTANCE,
                        mLocationListeners[0]
                );
            } catch (Exception e) {
                Log.e("LocationService", "Location error", e);
            }
        }
    }
    private void startForegroundServiceSafely() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(this, MapsActivityNew.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Get Live Route Map")
                .setContentText("Running")
                .setSmallIcon(R.drawable.ic_arrowright)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification); // ✅ EARLY CALL
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String tempTimeRemaining = intent.getStringExtra("remainingTime");
            if (tempTimeRemaining != null) {
                timeRemaining = Long.parseLong(tempTimeRemaining);
            }
            rideAction = intent.getStringExtra("rideAction");
        }

        // ❌ REMOVE THIS (already called in onCreate)
        // startForeground(1, notification);

        if (!"Resume Ride".equalsIgnoreCase(rideAction)) {
            startTimer();
        }

        return START_NOT_STICKY;
    }
    private void startTimer() {
        final long[] seconds = {0};

        long millisInFuture = timeRemaining; //30 seconds
        long countDownInterval = 1000; //1 second

        new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {

                if(isCounterCanceled)
                    cancel();
                else{
                    seconds[0] = millisUntilFinished / 1000;
                    timeRemaining = millisUntilFinished;
                    sharedprefrenceManager.setTimeRemaining(timeRemaining+"");
                    System.out.println("yueyuwyuewyuewyuew_Moving_Service " + seconds[0] + " -- " + millisUntilFinished);
//                    Toast.makeText(MapsActivityNew.this, seconds[0] +"", Toast.LENGTH_SHORT).show();
//                    if(seconds[0] == 0){
//                        System.out.println("yueyuwyuewyuewyuew_Moving");
//
//                    }
                }


            }
            public void onFinish() {
                System.out.println("yueyuwyuewyuewyuew_Moving_API_Service");
                timeRemaining = 30000;
                callF1();
                String status="Moving";
                sendLog(status);
                startTimer();
            }
        }.start();
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCounterCanceled = true;
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListeners[0]);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
