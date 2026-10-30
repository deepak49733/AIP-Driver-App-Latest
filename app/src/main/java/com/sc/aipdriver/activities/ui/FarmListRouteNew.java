package com.sc.aipdriver.activities.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
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

    @Override
    protected void onResume() {
        super.onResume();
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
            tvRouteTitle.setText("");
        }

        String formattedDateDisplay = formatDisplayDate(rawDate);
        String ordersText = "Orders for " + formattedDateDisplay;
        tvOrdersDate.setText(ordersText);
    }

    private String formatDisplayDate(String inputDate) {
        if (inputDate == null || inputDate.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            return new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(cal.getTime());
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
                    List<PriorityFarmData> data = response.body().getData();
                    sortFarmsByPriority(data);
                    farmDataList.addAll(data);
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
                    List<PriorityFarmData> data = response.body().getData();
                    sortFarmsByPriority(data);
                    farmDataList.addAll(data);
                    adapter.notifyDataSetChanged();
                } else {
                    showNoDataAvailable();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<PriorityFarmData>>> call, @NonNull Throwable t) {
                Log.e("FarmListRouteNew", "Error fetching fallback farms: " + t.getMessage());
                showNoDataAvailable();
            }
        });
    }

    private void sortFarmsByPriority(List<PriorityFarmData> list) {
        if (list == null || list.isEmpty()) return;
        Collections.sort(list, (f1, f2) -> {
            int p1 = (f1 != null && f1.getPriority() != null) ? f1.getPriority() : Integer.MAX_VALUE;
            int p2 = (f2 != null && f2.getPriority() != null) ? f2.getPriority() : Integer.MAX_VALUE;
            return Integer.compare(p1, p2);
        });
    }

    private void showNoDataAvailable() {
        farmDataList.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(FarmListRouteNew.this, "No Farms Available", Toast.LENGTH_SHORT).show();
    }
}
