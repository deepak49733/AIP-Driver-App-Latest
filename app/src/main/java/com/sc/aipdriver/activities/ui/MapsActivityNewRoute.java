package com.sc.aipdriver.activities.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.otherclasses.HttpConnection;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

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
    private final List<String> farmNames = new ArrayList<>();
    private final List<Integer> farmPriorities = new ArrayList<>();

    private final int[] markerColors = new int[]{
            Color.parseColor("#E53935"), // Red
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#1E88E5"), // Blue
            Color.parseColor("#FB8C00"), // Orange
            Color.parseColor("#8E24AA"), // Purple
            Color.parseColor("#00ACC1")  // Cyan
    };

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
        View rlHeader = findViewById(R.id.rl_header);
        com.sc.aipdriver.activities.otherclasses.StatusBarUtils.applyStatusBarPadding(rlHeader);

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

    private String formatApiDate(String inputDate) {
        if (inputDate == null || inputDate.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());
        }
        try {
            SimpleDateFormat inputFormat;
            if (inputDate.contains("-")) {
                inputFormat = new SimpleDateFormat("yyyy-M-d", Locale.US);
            } else {
                inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            }
            Date date = inputFormat.parse(inputDate);
            if (date != null) {
                return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputDate;
    }

    private String formatDisplayDate(String inputDate) {
        if (inputDate == null || inputDate.isEmpty()) {
            return "10/12/2026";
        }
        try {
            SimpleDateFormat inputFormat;
            if (inputDate.contains("-")) {
                inputFormat = new SimpleDateFormat("yyyy-M-d", Locale.US);
            } else {
                inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            }
            Date date = inputFormat.parse(inputDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputDate;
    }

    private void fetchAndDrawRoutePoints() {
        String apiDate = formatApiDate(rawDate);
        Call<BaseResponse<List<PriorityFarmData>>> call = apiService.getFarmINS(
                routeName,
                sharedprefrenceManager.getOrgID(),
                apiDate,
                sharedprefrenceManager.getDriverID(),
                sharedprefrenceManager.getToken()
        );

        call.enqueue(new Callback<BaseResponse<List<PriorityFarmData>>>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<List<PriorityFarmData>>> call, @NonNull Response<BaseResponse<List<PriorityFarmData>>> response) {
                if (response.code() == 200 && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    processFarmData(response.body().getData());
                } else {
                    fetchFarmsFallback();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<PriorityFarmData>>> call, @NonNull Throwable t) {
                Log.e("MapsActivityNewRoute", "Error fetching FarmINS: " + t.getMessage());
                fetchFarmsFallback();
            }
        });
    }

    private void fetchFarmsFallback() {
        String formattedDate = formatDisplayDate(rawDate);
        Call<BaseResponse<List<PriorityFarmData>>> call = apiService.getFarmsWithPriorityDateWise(
                routeName,
                sharedprefrenceManager.getOrgID(),
                sharedprefrenceManager.getParentId(),
                formattedDate,
                sharedprefrenceManager.getDriverID(),
                sharedprefrenceManager.getToken()
        );

        call.enqueue(new Callback<BaseResponse<List<PriorityFarmData>>>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<List<PriorityFarmData>>> call, @NonNull Response<BaseResponse<List<PriorityFarmData>>> response) {
                if (response.code() == 200 && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    processFarmData(response.body().getData());
                } else {
                    waypoints.clear();
                    farmNames.clear();
                    farmPriorities.clear();
                    Toast.makeText(MapsActivityNewRoute.this, "No valid farm coordinates available", Toast.LENGTH_SHORT).show();
                    plotRouteOnMap();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<PriorityFarmData>>> call, @NonNull Throwable t) {
                Log.e("MapsActivityNewRoute", "Error fetching fallback: " + t.getMessage());
                waypoints.clear();
                farmNames.clear();
                farmPriorities.clear();
                Toast.makeText(MapsActivityNewRoute.this, "Failed to load route coordinates", Toast.LENGTH_SHORT).show();
                plotRouteOnMap();
            }
        });
    }

    private void sortFarmsByPriority(List<PriorityFarmData> list) {
        if (list == null || list.isEmpty()) return;
        Collections.sort(list, (f1, f2) -> {
            int p1 = (f1 != null && f1.getPriority() != null) ? f1.getPriority() : Integer.MAX_VALUE;
            int p2 = (f2 != null && f2.getPriority() != null) ? f2.getPriority() : Integer.MAX_VALUE;
            if (p1 != p2) {
                return Integer.compare(p1, p2);
            } else {
                String name1 = (f1 != null && f1.getFarmName() != null) ? f1.getFarmName() : "";
                String name2 = (f2 != null && f2.getFarmName() != null) ? f2.getFarmName() : "";
                return name1.compareToIgnoreCase(name2);
            }
        });
    }

    private void processFarmData(List<PriorityFarmData> dataList) {
        waypoints.clear();
        farmNames.clear();
        farmPriorities.clear();

        if (dataList != null && !dataList.isEmpty()) {
            sortFarmsByPriority(dataList);

            int index = 1;
            for (PriorityFarmData farm : dataList) {
                if (farm == null) continue;

                String name = farm.getFarmName();
                if (name == null || name.trim().isEmpty()) {
                    name = "Farm " + index;
                }

                int priority = (farm.getPriority() != null) ? farm.getPriority() : index;

                double lat = 0.0;
                double lng = 0.0;
                try {
                    if (farm.getLatitude() != null && !farm.getLatitude().trim().isEmpty()) {
                        lat = Double.parseDouble(farm.getLatitude().trim());
                    }
                    if (farm.getLongitude() != null && !farm.getLongitude().trim().isEmpty()) {
                        lng = Double.parseDouble(farm.getLongitude().trim());
                    }
                } catch (Exception e) {
                    Log.e("MapsActivityNewRoute", "Error parsing farm lat/lng: " + e.getMessage());
                }

                if (lat != 0.0 && lng != 0.0) {
                    Log.d("MapsActivityNewRoute", "Real API Farm Waypoint [" + index + "]: " + name + " (Priority: " + priority + ") -> Lat: " + lat + ", Lng: " + lng);
                    waypoints.add(new LatLng(lat, lng));
                    farmNames.add(name);
                    farmPriorities.add(priority);
                    index++;
                }
            }
        }

        if (waypoints.isEmpty()) {
            Toast.makeText(MapsActivityNewRoute.this, "No valid farm coordinates available", Toast.LENGTH_SHORT).show();
        }

        plotRouteOnMap();
    }

    private BitmapDescriptor getMarkerIconWithLabel(String seqStr, String farmName, int circleColor) {
        String fullLabel = seqStr + " (" + farmName + ")";

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#111111"));
        textPaint.setTextSize(26f);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(fullLabel, 0, fullLabel.length(), textBounds);

        int textWidth = textBounds.width() + 24;
        int textHeight = textBounds.height() + 16;

        int pinRadius = 36;
        int pinDiameter = pinRadius * 2;

        int bitmapWidth = Math.max(pinDiameter + 16, textWidth + 16);
        int bitmapHeight = pinDiameter + textHeight + 16;

        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float centerX = bitmapWidth / 2f;

        // 1. Draw Pin Circle
        Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setColor(circleColor);
        canvas.drawCircle(centerX, pinRadius + 4, pinRadius - 2, paintCircle);

        Paint paintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBorder.setColor(Color.WHITE);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(5f);
        canvas.drawCircle(centerX, pinRadius + 4, pinRadius - 2, paintBorder);

        // Sequence Letter/Number inside Pin Circle
        Paint paintPinText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPinText.setColor(Color.WHITE);
        paintPinText.setTextSize(32f);
        paintPinText.setTypeface(Typeface.DEFAULT_BOLD);
        paintPinText.setTextAlign(Paint.Align.CENTER);

        Rect pinTextBounds = new Rect();
        paintPinText.getTextBounds(seqStr, 0, seqStr.length(), pinTextBounds);
        float pinTextY = (pinRadius + 4) + pinTextBounds.height() / 2f - pinTextBounds.bottom;
        canvas.drawText(seqStr, centerX, pinTextY, paintPinText);

        // 2. Draw Label Box below Pin
        float labelTop = pinDiameter + 8f;
        float labelLeft = centerX - textWidth / 2f;
        float labelRight = centerX + textWidth / 2f;
        float labelBottom = labelTop + textHeight;

        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.argb(245, 255, 255, 255));
        RectF rectF = new RectF(labelLeft, labelTop, labelRight, labelBottom);
        canvas.drawRoundRect(rectF, 8f, 8f, bgPaint);

        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(Color.parseColor("#CCCCCC"));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2f);
        canvas.drawRoundRect(rectF, 8f, 8f, strokePaint);

        // Draw Label Text inside Box
        float textY = labelTop + textHeight / 2f + textBounds.height() / 2f - textBounds.bottom;
        canvas.drawText(fullLabel, centerX, textY, textPaint);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private String buildDirectionsUrl() {
        if (waypoints.size() < 2) return "";

        LatLng origin = waypoints.get(0);
        LatLng dest = waypoints.get(waypoints.size() - 1);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://maps.googleapis.com/maps/api/directions/json?");
        urlBuilder.append("origin=").append(origin.latitude).append(",").append(origin.longitude);
        urlBuilder.append("&destination=").append(dest.latitude).append(",").append(dest.longitude);

        if (waypoints.size() > 2) {
            urlBuilder.append("&waypoints=");
            for (int i = 1; i < waypoints.size() - 1; i++) {
                if (i > 1) urlBuilder.append("|");
                urlBuilder.append(waypoints.get(i).latitude).append(",").append(waypoints.get(i).longitude);
            }
        }

        urlBuilder.append("&mode=driving");
        urlBuilder.append("&key=").append(getString(R.string.google_key));
        return urlBuilder.toString();
    }

    private String buildLegDirectionsUrl(LatLng p1, LatLng p2) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://maps.googleapis.com/maps/api/directions/json?");
        urlBuilder.append("origin=").append(p1.latitude).append(",").append(p1.longitude);
        urlBuilder.append("&destination=").append(p2.latitude).append(",").append(p2.longitude);
        urlBuilder.append("&mode=driving");
        urlBuilder.append("&key=").append(getString(R.string.google_key));
        return urlBuilder.toString();
    }

    private List<LatLng> decodePolylinePoints(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void parseRouteLegSteps(JSONObject routeObj, List<LatLng> outPoints) {
        try {
            JSONArray legs = routeObj.optJSONArray("legs");
            if (legs != null) {
                for (int l = 0; l < legs.length(); l++) {
                    JSONObject leg = legs.getJSONObject(l);
                    JSONArray steps = leg.optJSONArray("steps");
                    if (steps != null) {
                        for (int s = 0; s < steps.length(); s++) {
                            JSONObject step = steps.getJSONObject(s);
                            JSONObject polylineObj = step.optJSONObject("polyline");
                            if (polylineObj != null) {
                                String points = polylineObj.optString("points", "");
                                if (!points.isEmpty()) {
                                    outPoints.addAll(decodePolylinePoints(points));
                                }
                            }
                        }
                    }
                }
            }
            if (outPoints.isEmpty()) {
                JSONObject overviewPoly = routeObj.optJSONObject("overview_polyline");
                if (overviewPoly != null) {
                    String points = overviewPoly.optString("points", "");
                    if (!points.isEmpty()) {
                        outPoints.addAll(decodePolylinePoints(points));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("MapsActivityNewRoute", "Error parsing route steps: " + e.getMessage());
        }
    }

    private String buildOsrmDirectionsUrl() {
        if (waypoints.size() < 2) return "";

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://router.project-osrm.org/route/v1/driving/");

        for (int i = 0; i < waypoints.size(); i++) {
            if (i > 0) urlBuilder.append(";");
            urlBuilder.append(waypoints.get(i).longitude).append(",").append(waypoints.get(i).latitude);
        }

        urlBuilder.append("?overview=full&geometries=geojson");
        return urlBuilder.toString();
    }

    private void drawRoadDirectionsPath() {
        if (waypoints.size() < 2) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            List<LatLng> allRoadPoints = new ArrayList<>();
            HttpConnection http = new HttpConnection();

            // 1. Try Google Directions API first
            String multiUrl = buildDirectionsUrl();
            if (!multiUrl.isEmpty()) {
                try {
                    String data = http.readUrl(multiUrl);
                    if (data != null && !data.isEmpty()) {
                        JSONObject jObject = new JSONObject(data);
                        JSONArray jRoutes = jObject.optJSONArray("routes");
                        if (jRoutes != null && jRoutes.length() > 0) {
                            parseRouteLegSteps(jRoutes.getJSONObject(0), allRoadPoints);
                        }
                    }
                } catch (Exception e) {
                    Log.e("MapsActivityNewRoute", "Error fetching Google directions: " + e.getMessage());
                }
            }

            // 2. If Google Directions API fails or returns REQUEST_DENIED, call OSRM Driving Road API
            if (allRoadPoints.isEmpty()) {
                String osrmUrl = buildOsrmDirectionsUrl();
                if (!osrmUrl.isEmpty()) {
                    try {
                        String data = http.readUrl(osrmUrl);
                        if (data != null && !data.isEmpty()) {
                            JSONObject jObject = new JSONObject(data);
                            JSONArray jRoutes = jObject.optJSONArray("routes");
                            if (jRoutes != null && jRoutes.length() > 0) {
                                JSONObject route0 = jRoutes.getJSONObject(0);
                                JSONObject geometry = route0.optJSONObject("geometry");
                                if (geometry != null) {
                                    JSONArray coords = geometry.optJSONArray("coordinates");
                                    if (coords != null) {
                                        for (int i = 0; i < coords.length(); i++) {
                                            JSONArray pt = coords.getJSONArray(i);
                                            double lng = pt.getDouble(0);
                                            double lat = pt.getDouble(1);
                                            allRoadPoints.add(new LatLng(lat, lng));
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("MapsActivityNewRoute", "Error fetching OSRM driving directions: " + e.getMessage());
                    }
                }
            }

            // 3. Render 100% turn-by-turn driving road route on Google Maps
            if (!allRoadPoints.isEmpty()) {
                runOnUiThread(() -> {
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(allRoadPoints)
                            .width(16f)
                            .color(Color.parseColor("#0D47A1"))
                            .jointType(JointType.ROUND)
                            .startCap(new RoundCap())
                            .endCap(new RoundCap())
                            .geodesic(true);
                    mMap.addPolyline(polylineOptions);
                });
            }
        });
    }

    private void plotRouteOnMap() {
        if (mMap == null) return;

        mMap.clear();

        if (waypoints.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        // Add custom markers with permanent Priority & Farm Name Labels (e.g. 1 (Test North Farm 1), 10 (SPRINGDALE COLONY)...)
        for (int i = 0; i < waypoints.size(); i++) {
            LatLng point = waypoints.get(i);
            String name = farmNames.get(i);
            int priorityVal = (i < farmPriorities.size() && farmPriorities.get(i) != null) 
                    ? farmPriorities.get(i) : (i + 1);
            String priorityStr = String.valueOf(priorityVal);

            int color = markerColors[i % markerColors.length];

            mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title(name)
                    .icon(getMarkerIconWithLabel(priorityStr, name, color)));

            boundsBuilder.include(point);
        }

        // Draw real road directions path with ETA Badges
        drawRoadDirectionsPath();

        // Move camera to fit all markers nicely with adequate padding
        try {
            LatLngBounds bounds = boundsBuilder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        } catch (Exception e) {
            if (!waypoints.isEmpty()) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(waypoints.get(0), 4f));
            }
        }
    }
}
