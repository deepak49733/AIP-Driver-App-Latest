package com.sc.aipdriver.activities.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.adapters.FarmDeliveryAdapterNew;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmListRouteNew extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvRouteTitle;
    private TextView tvOrdersDate;
    private RecyclerView recyclerViewFarms;
    private Button btnDisplayFarmPaths;

    private ApiInterface apiService;
    private SharedprefrenceManager sharedprefrenceManager;
    private List<PriorityFarmData> farmDataList;
    private FarmDeliveryAdapterNew adapter;

    private String routeName = "";
    private String routeId = "";
    private String rawDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_list_route_new);

        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        sharedprefrenceManager = new SharedprefrenceManager(this);

        if (getIntent() != null) {
            routeName = getIntent().getStringExtra("ROUTE");
            routeId = getIntent().getStringExtra("ROUTEID");
            rawDate = getIntent().getStringExtra("date");
        }

        initViews();
        setupHeaderAndSubtitle();
        setupListeners();

        farmDataList = new ArrayList<>();
        recyclerViewFarms.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FarmDeliveryAdapterNew(this, farmDataList, routeName, routeId);
        recyclerViewFarms.setAdapter(adapter);

        fetchFarms();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvRouteTitle = findViewById(R.id.tv_route_title);
        tvOrdersDate = findViewById(R.id.tv_orders_date);
        recyclerViewFarms = findViewById(R.id.recyclerview_farms);
        btnDisplayFarmPaths = findViewById(R.id.btn_display_farm_paths);
    }

    private void setupHeaderAndSubtitle() {
        if (routeName != null && !routeName.isEmpty()) {
            tvRouteTitle.setText(routeName);
        } else {
            tvRouteTitle.setText("Test North");
        }

        String formattedDateDisplay = formatDisplayDate(rawDate);
        String ordersText = "Orders for " + formattedDateDisplay;
        tvOrdersDate.setText(ordersText);
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

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        btnDisplayFarmPaths.setOnClickListener(v -> showFarmPathsExplanationDialog());
    }

    private void showFarmPathsExplanationDialog() {
        String explanationText = getString(R.string.farm_paths_explanation);

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Notice")
                .setContentText(explanationText)
                .setConfirmText("OK")
                .setConfirmClickListener(dialog -> {
                    dialog.dismissWithAnimation();
                    openMapPreview();
                })
                .show();
    }

    private void openMapPreview() {
        Intent intent = new Intent(FarmListRouteNew.this, MapsActivityNewRoute.class);
        intent.putExtra("ROUTE", routeName);
        intent.putExtra("ROUTEID", routeId);
        intent.putExtra("date", rawDate);
        startActivity(intent);
    }

    private void fetchFarms() {
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
                    farmDataList.clear();
                    farmDataList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    fetchFarmsFallback();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<PriorityFarmData>>> call, @NonNull Throwable t) {
                Log.e("FarmListRouteNew", "Error fetching FarmINS: " + t.getMessage());
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
                    farmDataList.clear();
                    farmDataList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    populateSampleFarms();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<PriorityFarmData>>> call, @NonNull Throwable t) {
                Log.e("FarmListRouteNew", "Error fetching fallback farms: " + t.getMessage());
                populateSampleFarms();
            }
        });
    }

    private void populateSampleFarms() {
        farmDataList.clear();

        String prefix = (routeName != null && !routeName.isEmpty()) ? routeName : "Test North";

        if (prefix.toLowerCase().contains("north")) {
            PriorityFarmData farm1 = new PriorityFarmData();
            farm1.setId(101);
            farm1.setFarmName(prefix + " Farm 4");
            farm1.setIsINS("Yes");
            farm1.setInstruction("Sample deadline is 1-1:30pm M/W/F. Notify John Zierke if deadline is expected to not be met due to weather or delays.");

            PriorityFarmData farm2 = new PriorityFarmData();
            farm2.setId(102);
            farm2.setFarmName(prefix + " Farm 1");
            farm2.setIsINS("No");

            PriorityFarmData farm3 = new PriorityFarmData();
            farm3.setId(103);
            farm3.setFarmName(prefix + " Farm 3");
            farm3.setIsINS("No");

            PriorityFarmData farm4 = new PriorityFarmData();
            farm4.setId(104);
            farm4.setFarmName(prefix + " Farm 2");
            farm4.setIsINS("No");

            farmDataList.add(farm1);
            farmDataList.add(farm2);
            farmDataList.add(farm3);
            farmDataList.add(farm4);
        } else {
            PriorityFarmData farm1 = new PriorityFarmData();
            farm1.setId(101);
            farm1.setFarmName("M7");
            farm1.setIsINS("Yes");
            farm1.setInstruction("Sample deadline is 1-1:30pm M/W/F. Notify John Zierke if deadline is expected to not be met due to weather or delays.");

            PriorityFarmData farm2 = new PriorityFarmData();
            farm2.setId(102);
            farm2.setFarmName("Nordic");
            farm2.setIsINS("No");

            PriorityFarmData farm3 = new PriorityFarmData();
            farm3.setId(103);
            farm3.setFarmName("Hillside");
            farm3.setIsINS("No");

            farmDataList.add(farm1);
            farmDataList.add(farm2);
            farmDataList.add(farm3);
        }

        adapter.notifyDataSetChanged();
    }
}
