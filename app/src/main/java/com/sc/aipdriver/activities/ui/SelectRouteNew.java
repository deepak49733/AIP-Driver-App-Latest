package com.sc.aipdriver.activities.ui;

import static com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.IsInternetAvailableKt;
import com.sc.aipdriver.activities.adapters.SelectRouteAdapter;
import com.sc.aipdriver.activities.dialogs.OrderDateDialogNew;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.interfaces.OnClickRoute;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.RouteListData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectRouteNew extends AppCompatActivity implements OnClickRoute {

    private RecyclerView recyclerView;
    private SelectRouteAdapter selectRouteAdapter;
    private ApiInterface apiService;
    private Gson gson;
    private SharedprefrenceManager sharedprefrenceManager;
    private List<RouteListData> routeListDataList;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route_new);

        recyclerView = findViewById(R.id.recyclerview);
        btnBack = findViewById(R.id.btn_back_route);

        routeListDataList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectRouteAdapter = new SelectRouteAdapter(this, routeListDataList, this);
        recyclerView.setAdapter(selectRouteAdapter);

        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        gson = new GsonBuilder().setPrettyPrinting().create();
        sharedprefrenceManager = new SharedprefrenceManager(this);

        // Bottom << BACK button click -> finish activity
        btnBack.setOnClickListener(v -> finish());

        // System back button -> finish activity
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        if (!IsInternetAvailableKt.isInternetAvailable(SelectRouteNew.this)) {
            checkInternet();
        } else {
            getRoute();
        }
    }

    private void getRoute() {
        Call<BaseResponse<List<RouteListData>>> call = apiService.getListRouters(
                sharedprefrenceManager.getOrgID(),
                sharedprefrenceManager.getDriverID(),
                sharedprefrenceManager.getToken()
        );
        call.enqueue(new Callback<BaseResponse<List<RouteListData>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<RouteListData>>> call, Response<BaseResponse<List<RouteListData>>> response) {
                if (response.code() == 200 && response.body() != null && response.body().getData() != null) {
                    routeListDataList.addAll(response.body().getData());
                    selectRouteAdapter.notifyDataSetChanged();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                            Log.e("SelectRouteNew", "Error: " + loginError.getMessage());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<RouteListData>>> call, Throwable t) {
                Log.e("SelectRouteNew", "Failure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onClickRout(RouteListData routeListData) {
        if (routeListData == null) return;

        applyRouteMode(routeListData);

        String routeName = routeListData.getRouteName();
        String routeId = routeListData.getId().toString();

        OrderDateDialogNew dialog = OrderDateDialogNew.newInstance("", routeName);
        dialog.setListener(selectedDate -> {
            Intent intent = new Intent(SelectRouteNew.this, FarmListRouteNew.class);
            intent.putExtra("ROUTE", routeName);
            intent.putExtra("ROUTEID", routeId);
            intent.putExtra("SEQUENCE", "Start");
            intent.putExtra("isResumed", false);
            intent.putExtra("date", selectedDate);
            sharedprefrenceManager.setDate(formatToMMDDYYYY(selectedDate));
            sharedprefrenceManager.setRouteName(routeName);
            startActivity(intent);
        });
        dialog.show(getSupportFragmentManager(), "OrderDateDialogNew");
    }

    private void applyRouteMode(RouteListData routeListData) {
        if (routeListData == null) return;
        if (routeListData.getMode() == 1) {
            sharedprefrenceManager.setAppMode(SharedprefrenceManager.MODE_SYNC);
        } else {
            sharedprefrenceManager.setAppMode(SharedprefrenceManager.MODE_ONLINE);
        }
    }

    private void checkInternet() {
        runOnUiThread(() -> new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No Internet Connection")
                .setContentText("No offline data available. Please connect to the internet to continue.")
                .setConfirmText("OK")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    if (IsInternetAvailableKt.isInternetAvailable(this)) {
                        recreate();
                    } else {
                        checkInternet();
                    }
                })
                .show());
    }
}
