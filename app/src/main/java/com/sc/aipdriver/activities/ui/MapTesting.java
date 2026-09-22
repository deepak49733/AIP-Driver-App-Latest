package com.sc.aipdriver.activities.ui;

/*public class MapTesting extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, FinishDialog.SendData, TempratureDialogFragment.SendData {

    private static final int LOCATION_PERMISSION = 101;
    private GoogleMap mMap;
    ArrayList<LatLng> latLngslist;
    ApiInterface apiService;
    Gson gson;
    SharedprefrenceManager sharedprefrenceManager;
    String routeId;
    DeliveryData deliveryData;
    ArrayList<DeliveryData> DeliveryDataList;
    SweetAlertDialog pDialog;
    ShowLoading showLoading;
    Location location;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private List<Polyline> polylines;
    List<RoutePlannerDetail> routePlannerDetails;
    HashMap<String, RoutePlannerDetail> latLongDataHashMap;
    private Marker carMarker;
    BottomSheetDialogFragment bottomSheetDialogFragment;
    String farmid;
    private LatLngBounds.Builder builder;
    private LatLngBounds bounds;
    private Polyline polyline;
    private LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        latLngslist = new ArrayList<>();
        polylines = new ArrayList<>();
        routePlannerDetails = new ArrayList<>();
        latLongDataHashMap = new HashMap<>();
        DeliveryDataList = new ArrayList<>();

        showLoading = new ShowLoading(MapTesting.this);
        routeId = getIntent().getStringExtra("ROUTEID");
        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        gson = new GsonBuilder().setPrettyPrinting().create();
        sharedprefrenceManager = new SharedprefrenceManager(this);

        getFarmsRoute();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void getFarmsRoute() {
        showLoading.show();
        routePlannerDetails.clear();
        Call<BaseResponse<LatLongData>> call = apiService.getstartNewRide(routeId);
        call.enqueue(new Callback<BaseResponse<LatLongData>>() {
            @Override
            public void onResponse(Call<BaseResponse<LatLongData>> call, Response<BaseResponse<LatLongData>> response) {
                if (response.code() == 200) {
                    RoutePlannerDetail routePlannerDetail;
                    showLoading.dismiss();
                    Log.e("Data", "" + response.body().getMessage());
                    LatLongData latLongData = response.body().getData();
                    routePlannerDetail = new RoutePlannerDetail();
                    routePlannerDetail.setFarmLat(45.696146);
                    routePlannerDetail.setFarmLng(-95.923010);
                    routePlannerDetail.setId("00");
                    routePlannerDetail.setFarmName("SkyLab");
                    routePlannerDetails.add(routePlannerDetail);
                    routePlannerDetails.addAll(latLongData.getRoutePlannerDetail());
                    addToHashMap(routePlannerDetails);
                    notifyMap();
                } else
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        // errorDialog(loginError.getMessage());
                        Log.e("Data", loginError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<BaseResponse<LatLongData>> call, Throwable t) {
                Log.e("errorratro", t.getMessage());
            }


        });
    }

    private void addToHashMap(List<RoutePlannerDetail> routePlannerDetails) {
        for (int i = 0; i < routePlannerDetails.size(); i++) {
            RoutePlannerDetail routePlannerDetail = routePlannerDetails.get(i);
            latLongDataHashMap.put(routePlannerDetail.getId(), routePlannerDetail);
        }
    }

    private void notifyMap() {
        latLngslist.clear();
        for (int i = 0; i < routePlannerDetails.size(); i++) {
            RoutePlannerDetail routePlannerDetail = routePlannerDetails.get(i);
            LatLng lt = new LatLng(routePlannerDetail.getFarmLat(), routePlannerDetail.getFarmLng());
            latLngslist.add(lt);
        }

        if (latLngslist.size() > 0) {
            List<Marker> markersList = new ArrayList<Marker>();
            LatLng mylatlong = new LatLng(45.696146, -95.923010);
            MarkerOptions markeroption1 = new MarkerOptions().position(mylatlong).title("Sky Lab").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlong, 12));
            Marker m1 = mMap.addMarker(markeroption1);
            markersList.add(m1);

            for (int i = 0; i < latLngslist.size(); i++) {
                Marker marker;
                MarkerOptions markerOptions = new MarkerOptions().position(latLngslist.get(i)).title(routePlannerDetails.get(i).getFarmName()).snippet(routePlannerDetails.get(i).getId());
                marker = mMap.addMarker(markerOptions);
                markersList.add(marker);

//                builder = new LatLngBounds.Builder();
//                for (Marker m : markersList) {
//                    builder.include(m.getPosition());
//                }
//
//                bounds = builder.build();
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlong, 12));
//                mMap.animateCamera(cu);
                marker.showInfoWindow();

            }

            String url = getMapsApiDirectionsUrl();
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
        }
    }

    private String getMapsApiDirectionsUrl() {
        String url = "";
        String output = "";
        String parameters ="";

        String waypoints ="";
        String str_origin ="";
        String str_dest ="";

        str_origin = "origin=" + latLngslist.get(0).latitude + "," + latLngslist.get(0).longitude;

        str_dest = "destination=" + latLngslist.get(latLngslist.size()-1).latitude + "," + latLngslist.get(latLngslist.size()-1).longitude;

            for (int i = 1; i < latLngslist.size()-1; i++) {
                waypoints = waypoints + (waypoints.equals("") ? "" : "%7C") + latLngslist.get(i).latitude + "," + latLngslist.get(i).longitude;

            }

        waypoints = "waypoints=" + waypoints;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        output = "json";

        if(latLngslist.size()==2){
            parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        }else {
            parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + waypoints;
        }

        url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyDNgRKdWgZKO5PpvJSC1qSOw2Gj0AVCU9E";

        Location locationA=new Location("A");
        locationA.setLatitude(latLngslist.get(0).latitude);
        locationA.setLongitude(latLngslist.get(0).longitude);

        Location locationB = new Location("B");
        locationB.setLatitude(latLngslist.get(latLngslist.size()-1).latitude);
        locationB.setLongitude(latLngslist.get(latLngslist.size()-1).longitude);

//        LatLng From = new LatLng(latLngslist.get(0).latitude, latLngslist.get(0).longitude);
//        LatLng To = new LatLng(latLngslist.get(latLngslist.size()-1).latitude, latLngslist.get(latLngslist.size()-1).longitude);
//
        float distance = locationA.distanceTo(locationB)/1000;
//
//        int speedIs1KmMinute = 100;
//        float estimatedTimeInMin = distance / speedIs1KmMinute;

//        Toast.makeText(getApplicationContext(), "Distance - " + String.format("%.2f", distance) + "Km" + " : Duration -" + String.format("%.2f", estimatedTimeInMin) + "Min", Toast.LENGTH_SHORT).show();
         Toast.makeText(getApplicationContext(), "Distance - " + String.format("%.2f", distance) + "Km", Toast.LENGTH_SHORT).show();

        return url;
    }


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
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(7);
                polyLineOptions.color(Color.BLACK);
                polyline = mMap.addPolyline(polyLineOptions);
                polylines.add(polyline);
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        buildGoogleApiClient();
        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                if (location != null)
                    sendLog();
            }
        };

        // schedule the task to run starting now and then every hour...
//        timer.schedule(hourlyTask, 1000, 3000);

        timer.schedule(hourlyTask, 3000, 120000);
    }

    private void sendLog() {
        RouteLog routeLog = new RouteLog();
        routeLog.setAction("Moving");
        routeLog.setDriverId(sharedprefrenceManager.getUserId());
        routeLog.setLat("" + location.getLatitude());
        routeLog.setLng("" + location.getLongitude());
        routeLog.setRideId(routeId);
        routeLog.setRouteId(sharedprefrenceManager.getRouteId());
        routeLog.setUID(sharedprefrenceManager.getUserId());
        routeLog.setVehicleId(sharedprefrenceManager.getVehicleId());

        Call<CommonError> call = apiService.sendLog(routeLog);
        call.enqueue(new Callback<CommonError>() {
            @Override
            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                if (response.code() == 200) {
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
        if(marker.getSnippet()==null){

        }else {
            if (!marker.getSnippet().equalsIgnoreCase("00")) {
                RoutePlannerDetail routePlannerDetail = latLongDataHashMap.get(marker.getSnippet());
                farmid = routePlannerDetail.getFIRMID();
                Log.e("FARM_ID", farmid);
                bottomSheetDialogFragment = new TempratureDialogFragment().instance(routePlannerDetail, marker.getSnippet(), this);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                return false;
            }else {
                bottomSheetDialogFragment = new FinishDialog().instance(marker.getSnippet(), this);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                return false;
            }
        }
        return true;
    }

    private void sendFarmData(ArrayList<DeliveryData> deliveryDataList) {
        showLoading.show();
        Call<CommonError> call = apiService.sendFarmData(deliveryDataList);
        call.enqueue(new Callback<CommonError>() {
            @Override
            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                if (response.code() == 200) {
                    if (bottomSheetDialogFragment != null)
                        bottomSheetDialogFragment.dismiss();
                    showLoading.dismiss();
                    success("Success", "Data saved successfully");
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
                Log.e("errorratro", t.getMessage());
                showLoading.dismiss();
            }

        });
    }

    @Override
    public void datasave(String semencoolertemp, String noofbagsloaded, String onloadingtemp, String comment) {
        deliveryData = new DeliveryData();
        deliveryData.setId(farmid);
        deliveryData.setDriverId(sharedprefrenceManager.getUserId());
        deliveryData.setVehicleId(sharedprefrenceManager.getVehicleId());
        deliveryData.setRouteId(sharedprefrenceManager.getRouteId());
        deliveryData.setUID(sharedprefrenceManager.getUserId());
        deliveryData.setNumberOfBagsDelivered(noofbagsloaded);
        deliveryData.setTemperatureOfSemenDelivered(onloadingtemp);
        deliveryData.setCommentsDelivered(comment);
        if (location != null) {
            deliveryData.setFarmLat("" + location.getLatitude());
            deliveryData.setFarmLng("" + location.getLongitude());
        }
        deliveryData.setAction("Delivered");
        deliveryData.setFkrideid(routeId);
        deliveryData.setCustomerSeemanCoolarTemp(semencoolertemp);
        DeliveryDataList.add(0, deliveryData);
        sendFarmData(DeliveryDataList);
        Log.e("call", "yes all data save in database " + gson.toJson(deliveryData));

    }

    //@Override
    public void finishData(String endodometer, String totalmils) {
        sendFinshdata(endodometer);
    }

    private void sendFinshdata(String endodometer) {
//        showLoading.show();
//        Call<CommonError> call = apiService.finishRide(routeId, sharedprefrenceManager.getUserId(), "" + location.getLatitude(), "" + location.getLongitude(), endodometer, "end");
//        call.enqueue(new Callback<CommonError>() {
//            @Override
//            public void onResponse(Call<CommonError> call, Response<CommonError> response) {
//                if (response.code() == 200) {
//                    if (bottomSheetDialogFragment != null)
//                        bottomSheetDialogFragment.dismiss();
//                    showLoading.dismiss();
//                    Log.e("Datatrue", "" + response.body().getMessage());
////                    Toast.makeText(MapsActivityNew.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
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
//                Log.e("errorratro", t.getMessage());
//            }
//        });
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.e("location", "" + location);
        carMarker.remove();
        setCurrent(location);
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
        if (location != null && mMap != null) {
            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            carMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car)).position(userLocation).title("Driver").rotation(location.getBearing()));
        }

    }

    private void checkRuntimePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
        } else
            actionAfterPermissionGranted();

    }

    void actionAfterPermissionGranted() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);

        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setSmallestDisplacement(10);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        GetUserLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapTesting.this, 100);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    private void GetUserLocation() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            setCurrent(location);
        }
    }

    private void success(String st1, String st2) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(st1)
                .setContentText(st2)
                .show();
    }

}*/
