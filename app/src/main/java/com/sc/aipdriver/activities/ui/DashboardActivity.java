package com.sc.aipdriver.activities.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.dialogs.MonthlyScheduleDialogNew;
import com.sc.aipdriver.activities.fragments.SelectCar;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.otherclasses.ShouldLogout;

public class DashboardActivity extends AppCompatActivity {

    private Button btnStartRoute;
    private Button btnRoutePlanning;
    private Button btnDriverSchedule;
    private Button btnLogout;
    private SharedprefrenceManager sharedprefrenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        RelativeLayout rootLayout = findViewById(R.id.root_dashboard);
        sharedprefrenceManager = new SharedprefrenceManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnStartRoute = findViewById(R.id.btn_start_route);
        btnRoutePlanning = findViewById(R.id.btn_route_planning);
        btnDriverSchedule = findViewById(R.id.btn_driver_schedule);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupClickListeners() {
        btnStartRoute.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SelectCar.class);
            startActivity(intent);
        });

        btnRoutePlanning.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SelectRouteNew.class);
            startActivity(intent);
        });

        btnDriverSchedule.setOnClickListener(v -> {
            MonthlyScheduleDialogNew dialog = MonthlyScheduleDialogNew.newInstance();
            dialog.show(getSupportFragmentManager(), "MonthlyScheduleDialogNew");
        });

        btnLogout.setOnClickListener(v -> {
            ShouldLogout shouldLogout = new ShouldLogout(DashboardActivity.this, sharedprefrenceManager);
            shouldLogout.shouldLogout(true, DashboardActivity.this);
        });
    }
}
