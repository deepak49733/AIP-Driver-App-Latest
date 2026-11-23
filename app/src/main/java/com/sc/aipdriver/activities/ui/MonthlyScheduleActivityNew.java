package com.sc.aipdriver.activities.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.adapters.CalendarAdapterNew;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.RouteDriverScheduleData;
import com.sc.aipdriver.activities.models.RouteDriverScheduleItem;
import com.sc.aipdriver.activities.models.RouteDriverScheduleRequest;
import com.sc.aipdriver.activities.models.RouteDriverScheduleResponse;
import com.sc.aipdriver.activities.models.VehicleListData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonthlyScheduleActivityNew extends AppCompatActivity implements CalendarAdapterNew.OnScheduleClickListener {

    private static final String TAG = "MonthlyScheduleNew";

    private TextView tvBack;
    private TextView tvLastUpdated;
    private TextView tvMonthYear;
    private ImageView ivPrevMonth;
    private ImageView ivNextMonth;
    private RecyclerView rvCalendarGrid;
    private ProgressBar pbLoading;

    private CalendarAdapterNew adapter;
    private SharedprefrenceManager sharedprefrenceManager;
    private ApiInterface apiService;
    private final Map<String, String> vehicleImageMap = new HashMap<>();

    private int selectedYear;
    private int selectedMonth; // 1-indexed (1 = Jan, 12 = Dec)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_schedule_new);

        sharedprefrenceManager = new SharedprefrenceManager(this);

        Calendar now = Calendar.getInstance();
        selectedYear = getIntent().getIntExtra("selected_year", now.get(Calendar.YEAR));
        selectedMonth = getIntent().getIntExtra("selected_month", now.get(Calendar.MONTH) + 1);

        initViews();
        setupListeners();

        rvCalendarGrid.setLayoutManager(new GridLayoutManager(this, 7));

        fetchScheduleAndRender();
    }

    private void initViews() {
        View rlHeader = findViewById(R.id.rl_header);
        com.sc.aipdriver.activities.otherclasses.StatusBarUtils.applyStatusBarPadding(rlHeader);

        tvBack = findViewById(R.id.tv_back);
        tvLastUpdated = findViewById(R.id.tv_last_updated);
        tvMonthYear = findViewById(R.id.tv_month_year);
        ivPrevMonth = findViewById(R.id.iv_prev_month);
        ivNextMonth = findViewById(R.id.iv_next_month);
        rvCalendarGrid = findViewById(R.id.rv_calendar_grid);
        pbLoading = findViewById(R.id.pb_loading);
    }

    private void setupListeners() {
        tvBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        ivPrevMonth.setOnClickListener(v -> {
            selectedMonth--;
            if (selectedMonth < 1) {
                selectedMonth = 12;
                selectedYear--;
            }
            fetchScheduleAndRender();
        });

        ivNextMonth.setOnClickListener(v -> {
            selectedMonth++;
            if (selectedMonth > 12) {
                selectedMonth = 1;
                selectedYear++;
            }
            fetchScheduleAndRender();
        });
    }

    public void fetchScheduleAndRender() {
        fetchVehicleList();

        Calendar displayCal = Calendar.getInstance();
        displayCal.set(Calendar.YEAR, selectedYear);
        displayCal.set(Calendar.MONTH, selectedMonth - 1);
        displayCal.set(Calendar.DAY_OF_MONTH, 1);

        SimpleDateFormat sdfMonthYear = new SimpleDateFormat("MMMM yyyy", Locale.US);
        tvMonthYear.setText(sdfMonthYear.format(displayCal.getTime()));

        if (pbLoading != null) {
            pbLoading.setVisibility(View.VISIBLE);
        }

        int orgId = 2;
        try {
            String orgStr = sharedprefrenceManager.getOrgID();
            if (orgStr != null && !orgStr.isEmpty()) {
                int parsed = Integer.parseInt(orgStr);
                if (parsed > 0) {
                    orgId = parsed;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing orgId", e);
        }

        RouteDriverScheduleRequest request = new RouteDriverScheduleRequest(orgId, selectedYear, selectedMonth);
        String token = sharedprefrenceManager.getToken();

        ApiInterface apiService = ApiClient.getClient(this).create(ApiInterface.class);
        apiService.getRouteDriverSchedule(request, token).enqueue(new Callback<RouteDriverScheduleResponse>() {
            @Override
            public void onResponse(@NonNull Call<RouteDriverScheduleResponse> call, @NonNull Response<RouteDriverScheduleResponse> response) {
                if (pbLoading != null) {
                    pbLoading.setVisibility(View.GONE);
                }

                RouteDriverScheduleData scheduleData = null;
                if (response.isSuccessful() && response.body() != null) {
                    scheduleData = response.body().getData();
                }

                if (scheduleData != null) {
                    String lastUpdated = scheduleData.getLastUpdatedDate();
                    if (lastUpdated != null && !lastUpdated.trim().isEmpty()) {
                        tvLastUpdated.setText("Last Updated: " + lastUpdated);
                    } else {
                        tvLastUpdated.setText("Last Updated: --");
                    }

                    renderCalendar(scheduleData.getData());
                } else {
                    tvLastUpdated.setText("Last Updated: --");
                    renderCalendar(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RouteDriverScheduleResponse> call, @NonNull Throwable t) {
                if (pbLoading != null) {
                    pbLoading.setVisibility(View.GONE);
                }
                Log.e(TAG, "Failed to load driver schedule", t);
                Toast.makeText(MonthlyScheduleActivityNew.this, "Failed to load schedule", Toast.LENGTH_SHORT).show();
                renderCalendar(null);
            }
        });
    }

    private void fetchVehicleList() {
        if (apiService == null) {
            apiService = ApiClient.getClient(this).create(ApiInterface.class);
        }
        String orgId = sharedprefrenceManager.getOrgID();
        String driverId = sharedprefrenceManager.getDriverID();
        String token = sharedprefrenceManager.getToken();

        if (orgId == null || orgId.isEmpty() || driverId == null || driverId.isEmpty() || token == null) return;

        apiService.getListVehicle(orgId, driverId, token).enqueue(new Callback<BaseResponse<List<VehicleListData>>>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse<List<VehicleListData>>> call, @NonNull Response<BaseResponse<List<VehicleListData>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    vehicleImageMap.clear();
                    for (VehicleListData vehicle : response.body().getData()) {
                        if (vehicle == null) continue;
                        String vName = vehicle.getVehicleName();
                        String vImg = vehicle.getUserImage();
                        if (vImg == null || vImg.trim().isEmpty()) {
                            vImg = vehicle.getVehicleImage();
                        }
                        if (vName != null && !vName.trim().isEmpty() && vImg != null && !vImg.trim().isEmpty()) {
                            vehicleImageMap.put(vName.trim().toLowerCase(), vImg.trim());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<VehicleListData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch vehicle list for images: " + t.getMessage());
            }
        });
    }

    private void renderCalendar(List<RouteDriverScheduleItem> items) {
        Map<Integer, List<RouteDriverScheduleItem>> dayItemsMap = new HashMap<>();

        if (items != null) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            for (RouteDriverScheduleItem item : items) {
                if (item == null || item.getScheduleDate() == null) continue;
                try {
                    Date date = sdfDate.parse(item.getScheduleDate());
                    if (date != null) {
                        Calendar itemCal = Calendar.getInstance();
                        itemCal.setTime(date);

                        int itemYear = itemCal.get(Calendar.YEAR);
                        int itemMonth = itemCal.get(Calendar.MONTH) + 1;
                        int dayNum = itemCal.get(Calendar.DAY_OF_MONTH);

                        if (itemYear == selectedYear && itemMonth == selectedMonth) {
                            List<RouteDriverScheduleItem> list = dayItemsMap.get(dayNum);
                            if (list == null) {
                                list = new ArrayList<>();
                                dayItemsMap.put(dayNum, list);
                            }
                            list.add(item);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing item date: " + item.getScheduleDate(), e);
                }
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, selectedYear);
        cal.set(Calendar.MONTH, selectedMonth - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 = Sunday
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<CalendarAdapterNew.CalendarDayModel> calendarDays = new ArrayList<>();

        // Add padding blank cells before day 1
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarDays.add(new CalendarAdapterNew.CalendarDayModel("", true, null));
        }

        // Add days of month
        for (int day = 1; day <= maxDays; day++) {
            List<RouteDriverScheduleItem> dayItems = dayItemsMap.get(day);
            calendarDays.add(new CalendarAdapterNew.CalendarDayModel(String.valueOf(day), false, dayItems));
        }

        adapter = new CalendarAdapterNew(this, calendarDays, this);
        rvCalendarGrid.setAdapter(adapter);
    }

    @Override
    public void onScheduleClick(List<RouteDriverScheduleItem> items) {
        showScheduleDetailsDialog(items);
    }

    private void showScheduleDetailsDialog(List<RouteDriverScheduleItem> items) {
        if (items == null || items.isEmpty()) return;

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_schedule_details);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        TextView tvDialogTitle = dialog.findViewById(R.id.tv_dialog_title);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        Button btnClose = dialog.findViewById(R.id.btn_close_dialog);
        LinearLayout llItemsContainer = dialog.findViewById(R.id.ll_items_container);

        if (items.size() > 1) {
            tvDialogTitle.setText("Schedule Details (" + items.size() + " Routes)");
        } else {
            tvDialogTitle.setText("Schedule Details");
        }

        llItemsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < items.size(); i++) {
            RouteDriverScheduleItem item = items.get(i);
            if (item == null) continue;

            View itemView = inflater.inflate(R.layout.item_schedule_detail_popup, llItemsContainer, false);

            TextView tvItemBadge = itemView.findViewById(R.id.tv_item_badge);
            TextView tvRouteName = itemView.findViewById(R.id.tv_detail_route_name);
            TextView tvDriverName = itemView.findViewById(R.id.tv_detail_driver_name);
            TextView tvVehicleName = itemView.findViewById(R.id.tv_detail_vehicle_name);
            TextView tvScheduleDate = itemView.findViewById(R.id.tv_detail_schedule_date);
            ImageView ivVehicleImage = itemView.findViewById(R.id.iv_vehicle_image);

            String badgeText = "Route #" + (i + 1) + ": " + (item.getRouteName() != null ? item.getRouteName() : "");
            tvItemBadge.setText(badgeText);

            tvRouteName.setText(item.getRouteName() != null && !item.getRouteName().isEmpty() ? item.getRouteName() : "N/A");
            tvDriverName.setText(item.getDriverName() != null && !item.getDriverName().isEmpty() ? item.getDriverName() : "N/A");
            tvVehicleName.setText(item.getVehicleName() != null && !item.getVehicleName().isEmpty() ? item.getVehicleName() : "N/A");
            tvScheduleDate.setText(item.getScheduleDate() != null && !item.getScheduleDate().isEmpty() ? item.getScheduleDate() : "N/A");

            String vehicleImg = item.getVehicleImage();
            if (vehicleImg == null || vehicleImg.trim().isEmpty()) {
                String vName = item.getVehicleName();
                if (vName != null && !vName.trim().isEmpty()) {
                    vehicleImg = vehicleImageMap.get(vName.trim().toLowerCase());
                }
            }
            String fullImgUrl = getSanitizedImageUrl(vehicleImg);

            if (!fullImgUrl.isEmpty()) {
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_launcher_)
                        .error(R.drawable.ic_launcher_);

                try {
                    Glide.with(getApplicationContext())
                            .load(fullImgUrl)
                            .apply(options)
                            .into(ivVehicleImage);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading vehicle image: " + e.getMessage());
                }

                ivVehicleImage.setOnClickListener(v -> showFullScreenImage(fullImgUrl));
            } else {
                ivVehicleImage.setImageResource(R.drawable.ic_launcher_);
                ivVehicleImage.setOnClickListener(null);
            }

            llItemsContainer.addView(itemView);
        }

        if (ivClose != null) {
            ivClose.setOnClickListener(v -> dialog.dismiss());
        }
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private Dialog fullScreenDialog;

    private String getSanitizedImageUrl(String path) {
        if (path == null || path.trim().isEmpty()) return "";
        path = path.trim();
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path.replace(" ", "%20").replace("(", "%28").replace(")", "%29");
        }
        String baseUrl = ApiClient.BASE_URL;
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            path = path.substring(1);
        }
        path = path.replace(" ", "%20").replace("(", "%28").replace(")", "%29");
        return baseUrl + path;
    }

    private void showFullScreenImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) return;

        String fullUrl = getSanitizedImageUrl(imagePath);
        if (fullUrl.isEmpty()) return;

        if (fullScreenDialog != null && fullScreenDialog.isShowing()) {
            fullScreenDialog.dismiss();
        }

        fullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        fullScreenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_full_screen_image, null);
        fullScreenDialog.setContentView(dialogView);

        if (fullScreenDialog.getWindow() != null) {
            fullScreenDialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            fullScreenDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        ImageView ivFullImage = dialogView.findViewById(R.id.iv_full_image);
        ImageView ivCloseDialog = dialogView.findViewById(R.id.iv_close_dialog);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_launcher_)
                .error(R.drawable.ic_launcher_);

        try {
            Glide.with(getApplicationContext())
                    .load(fullUrl)
                    .apply(options)
                    .into(ivFullImage);
        } catch (Exception e) {
            Log.e(TAG, "Error loading full screen image with Glide: " + e.getMessage());
        }

        ivCloseDialog.setOnClickListener(v -> fullScreenDialog.dismiss());
        dialogView.setOnClickListener(v -> fullScreenDialog.dismiss());

        fullScreenDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (fullScreenDialog != null && fullScreenDialog.isShowing()) {
            fullScreenDialog.dismiss();
        }
        super.onDestroy();
    }
}
