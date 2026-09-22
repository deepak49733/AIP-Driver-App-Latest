package com.sc.aipdriver.activities.ui;

import static com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.IsInternetAvailableKt;
import com.sc.aipdriver.activities.adapters.SelectRouteAdapter;
import com.sc.aipdriver.activities.dialogs.DatePickerFullScreenDialog;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.interfaces.OnClickRoute;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.ParentResponse;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.models.RouteListData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.otherclasses.ShouldLogout;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectRoute extends AppCompatActivity implements OnClickRoute {

    RecyclerView recyclerView;
    SelectRouteAdapter selectRouteAdapter;
    ApiInterface apiService;
    Gson gson;
    String existingDate;
    SharedprefrenceManager sharedprefrenceManager;
    List<RouteListData> routeListDataList;
    String statusID;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);
        LinearLayout rootView = findViewById(R.id.root);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });
        recyclerView = findViewById(R.id.recyclerview);
        logout = findViewById(R.id.logout);
        routeListDataList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectRouteAdapter = new SelectRouteAdapter(this,routeListDataList,this);
        recyclerView.setAdapter(selectRouteAdapter);
        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        gson = new GsonBuilder().setPrettyPrinting().create();
        sharedprefrenceManager = new SharedprefrenceManager(this);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShouldLogout shouldLogout = new ShouldLogout(SelectRoute.this,sharedprefrenceManager);
                shouldLogout.shouldLogout(true,SelectRoute.this);
            }
        });
        if (!IsInternetAvailableKt.isInternetAvailable(SelectRoute.this)) {
            checkInternet();
        } else{
            getRoute();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing → back button disabled
            }
        });
    }
    private void getRoute() {
        Call<BaseResponse<List<RouteListData>>> call = apiService.getListRouters(sharedprefrenceManager.getOrgID(),sharedprefrenceManager.getDriverID(),sharedprefrenceManager.getToken());
        call.enqueue(new Callback<BaseResponse<List<RouteListData>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<RouteListData>>> call, Response<BaseResponse<List<RouteListData>>> response) {
                if (response.code() == 200) {

                    routeListDataList.addAll(response.body().getData());
                    selectRouteAdapter.notifyDataSetChanged();
                } else
                    try {
                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        //  errorDialog(loginError.getMessage());
                        Log.e("Data", loginError.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<RouteListData>>> call, Throwable t) {
                Log.e("errorratro", t.getMessage());
                System.err.println("In Get Route ::::  Failure  ");
            }

        });
    }

    @Override
    public void onClickRout(RouteListData routeListData) {

        applyRouteMode(routeListData);

        // For SYNC mode (mode 1), no internet check needed - use offline data
        if (routeListData.getMode() == 1) {
            getFarmsByRoute(routeListData.getRouteName(), routeListData.getId().toString());
        } else if (!IsInternetAvailableKt.isInternetAvailable(SelectRoute.this)) {
            // For ONLINE mode (mode 0), internet is required
            checkInternet();
        } else {
            getFarmsByRoute(routeListData.getRouteName(), routeListData.getId().toString());
        }

    }

    private void applyRouteMode(RouteListData routeListData) {
        if (routeListData == null) {
            return;
        }
        if (routeListData.getMode() == 1) {
            sharedprefrenceManager.setAppMode(SharedprefrenceManager.MODE_SYNC);
            Log.d("MODE__", "Route mode=1 -> SYNC mode");
        } else {
            sharedprefrenceManager.setAppMode(SharedprefrenceManager.MODE_ONLINE);
            Log.d("MODE__", "Route mode=0 -> ONLINE mode");
        }
    }
    private void checkInternet() {
        runOnUiThread(() -> {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
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
                    .show();
        });
    }

    private void getParentId(RouteListData routeListData) {
        Call<ParentResponse>call = apiService.getParentId(routeListData.getId()+"", sharedprefrenceManager.getDriverID());
        call.enqueue(new Callback<ParentResponse>() {
            @Override
            public void onResponse(Call<ParentResponse> call, Response<ParentResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    sharedprefrenceManager.setParentId(response.body().getData());
                    getFarmsByRoute(routeListData.getRouteName(),routeListData.getId().toString());
                    }


                else if (response.code()== 400){

                }
            }

            @Override
            public void onFailure(Call<ParentResponse> call, Throwable t) {
                Log.e("FarmsFail", t.getMessage());
                System.err.println(" Farm List Error Failure ");

            }

        });
    }

    // New code for getting forms by route name...
    private void getFarmsByRoute(String route, String routeId) {
        Call<BaseResponse<List<PriorityFarmData>>> call = apiService.getFarmsWithPriority(route, sharedprefrenceManager.getOrgID(), sharedprefrenceManager.getParentId(),sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
        call.enqueue(new Callback<BaseResponse<List<PriorityFarmData>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<PriorityFarmData>>> call, Response<BaseResponse<List<PriorityFarmData>>> response) {
                if (response.code() == 200) {
                    //  start.setVisibility(View.VISIBLE);
                   existingDate =  response.body().getData().get(0).getOrderDate();
                    Log.d("SelectedDate__","existingDate  when no farms is "+existingDate);
                    DatePickerFullScreenDialog dialog =
                            DatePickerFullScreenDialog.newInstance(existingDate,route);
                    dialog.setListener(date -> {

                        Log.d("SelectedDate__","selected date from cal when farms available is :"+date);
                        Intent intent = new Intent(SelectRoute.this, FarmListRoute.class);
                        intent.putExtra("ROUTE", route);
                        intent.putExtra("ROUTEID", routeId);
                        intent.putExtra("SEQUENCE", "Start");
                        intent.putExtra("isResumed", false);
                        intent.putExtra("date", date);
                        sharedprefrenceManager.setDate(formatToMMDDYYYY(date));
                        sharedprefrenceManager.setRouteName(route);
                        startActivity(intent);
                    });
                    dialog.show(getSupportFragmentManager(), "DatePicker");
                }
                else if (response.code()== 400){

                    Calendar calendar = Calendar.getInstance();
                    String todayDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US)
                            .format(calendar.getTime());
                    Log.d("SelectedDate__","Today date when no farms is "+todayDate);
                    DatePickerFullScreenDialog dialog =
                            DatePickerFullScreenDialog.newInstance(todayDate,route);
                    dialog.setListener(date -> {
                        Log.d("SelectedDate__","selected date from cal when no farms is :"+date);
                        Intent intent = new Intent(SelectRoute.this, FarmListRoute.class);
                        intent.putExtra("ROUTE", route);
                        intent.putExtra("ROUTEID", routeId);
                        intent.putExtra("SEQUENCE", "Start");
                        intent.putExtra("isResumed", false);
                        intent.putExtra("date", date);
                        sharedprefrenceManager.setDate(formatToMMDDYYYY(date));
                        sharedprefrenceManager.setRouteName(route);
                        startActivity(intent);
                    });
                    dialog.show(getSupportFragmentManager(), "DatePicker");

                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<PriorityFarmData>>> call, Throwable t) {
                Log.e("FarmsFail", t.getMessage());
                System.err.println(" Farm List Error Failure ");

            }

        });
    }
}
