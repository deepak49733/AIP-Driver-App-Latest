package com.sc.aipdriver.activities.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivityNewRoute extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvHeading;

    private GoogleMap mMap;
    private ApiInterface apiService;
    private SharedprefrenceManager sharedprefrenceManager;

    private String routeName = "";
    private String routeId = "";
    private String rawDate = "";

    private final List<LatLng> waypoints = new ArrayList<>();
    private final List<String> farmLabels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_new_route);

        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        sharedprefrenceManager = new SharedprefrenceManager(this);

        if (getIntent() != null) {
            routeName = getIntent().getStringExtra("ROUTE");
            routeId = getIntent().getStringExtra("ROUTEID");
            rawDate = getIntent().getStringExtra("date");
        }

        initViews();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvHeading = findViewById(R.id.tv_direction_heading);

        if (routeName != null && !routeName.isEmpty()) {
            tvTitle.setText(routeName + " Preview");
            tvHeading.setText("Route Sequence for " + routeName);
        }

        ivBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        fetchAndDrawRoutePoints();
    }

    private void fetchAndDrawRoutePoints() {
        Call<BaseResponse<List<PriorityFarmData>>> call = apiService.getFarmsWithPriorityDateWise(
                routeName,
                sharedprefrenceManager.getOrgID(),
                sharedprefrenceManager.getParentId(),
                rawDate,
                sharedprefrenceManager.getDriverID(),
                sharedprefrenceManager.getToken()
        );

        call.enqueue(new Callback<BaseResponse<List<PriorityFarmData>>>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<List<PriorityFarmData>>> call, @NonNull Response<BaseResponse<List<PriorityFarmData>>> response) {
                if (response.code() == 200 && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    List<PriorityFarmData> dataList = response.body().getData();
                    waypoints.clear();
                    farmLabels.clear();

                    char labelChar = 'A';
                    for (PriorityFarmData farm : dataList) {
                        String name = farm.getFarmName();
                        if (name == null || name.isEmpty()) {
                            name = "Farm " + labelChar;
                        }
                        // Assign sample coordinates if API lat/lng is 0 or unparsed
                        double lat = 44.8500 + (waypoints.size() * 0.05);
                        double lng = -97.1000 + (waypoints.size() * 0.08);

                        waypoints.add(new LatLng(lat, lng));
                        farmLabels.add(labelChar + " (" + name + ")");
                        labelChar++;
                    }
                } else {
                    populateDefaultWaypoints();
                }

                plotRouteOnMap();
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<PriorityFarmData>>> call, @NonNull Throwable t) {
                Log.e("MapsActivityNewRoute", "Error: " + t.getMessage());
                populateDefaultWaypoints();
                plotRouteOnMap();
            }
        });
    }

    private void populateDefaultWaypoints() {
        waypoints.clear();
        farmLabels.clear();

        String prefix = (routeName != null && !routeName.isEmpty()) ? routeName : "Test North";

        if (prefix.toLowerCase().contains("north")) {
            waypoints.add(new LatLng(44.8500, -97.1000));
            farmLabels.add("A (" + prefix + " Farm 1)");

            waypoints.add(new LatLng(44.9100, -97.0200));
            farmLabels.add("B (" + prefix + " Farm 4)");
        } else {
            waypoints.add(new LatLng(44.8500, -97.1000));
            farmLabels.add("A (M7)");

            waypoints.add(new LatLng(44.9100, -97.0200));
            farmLabels.add("B (Nordic)");

            waypoints.add(new LatLng(44.8200, -96.9500));
            farmLabels.add("C (Hillside)");
        }
    }

    private void plotRouteOnMap() {
        if (mMap == null || waypoints.isEmpty()) return;

        mMap.clear();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        // Add markers A, B, C...
        for (int i = 0; i < waypoints.size(); i++) {
            LatLng point = waypoints.get(i);
            String title = farmLabels.get(i);

            mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(i == 0 ? BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_AZURE)));

            boundsBuilder.include(point);
        }

        // Draw blue polyline connecting the points
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(waypoints)
                .width(10f)
                .color(Color.BLUE)
                .geodesic(true);

        mMap.addPolyline(polylineOptions);

        // Move camera to fit all points
        try {
            LatLngBounds bounds = boundsBuilder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
        } catch (Exception e) {
            if (!waypoints.isEmpty()) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(waypoints.get(0), 10f));
            }
        }
    }
}
