package com.sc.aipdriver.activities.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.adapters.CalendarAdapterNew;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MonthlyScheduleActivityNew extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvLastUpdated;
    private TextView tvMonthYear;
    private RecyclerView rvCalendarGrid;

    private List<CalendarAdapterNew.CalendarDayModel> calendarDays;
    private CalendarAdapterNew adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_schedule_new);

        initViews();
        setupListeners();
        setupCalendarData();
    }

    private void initViews() {
        tvBack = findViewById(R.id.tv_back);
        tvLastUpdated = findViewById(R.id.tv_last_updated);
        tvMonthYear = findViewById(R.id.tv_month_year);
        rvCalendarGrid = findViewById(R.id.rv_calendar_grid);
    }

    private void setupListeners() {
        tvBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void setupCalendarData() {
        // Display current date / timestamp for Last Updated
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdfLastUpdated = new SimpleDateFormat("M/d/yyyy h:mma 'CST'", Locale.US);
        String lastUpdatedStr = "Last Updated: " + sdfLastUpdated.format(now.getTime());
        tvLastUpdated.setText(lastUpdatedStr);

        // Display current month / year
        SimpleDateFormat sdfMonthYear = new SimpleDateFormat("MMMM yyyy", Locale.US);
        tvMonthYear.setText(sdfMonthYear.format(now.getTime()));

        calendarDays = new ArrayList<>();
        rvCalendarGrid.setLayoutManager(new GridLayoutManager(this, 7));

        // Get first day of month and total days
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 = Sunday
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add blank cells for padding before day 1
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarDays.add(new CalendarAdapterNew.CalendarDayModel("", true, null));
        }

        // Populate days 1 to maxDays
        for (int day = 1; day <= maxDays; day++) {
            List<String> tags = new ArrayList<>();

            // Add sample route tags on day 1 and day 29 matching screenshot
            if (day == 1) {
                tags.add("North Route: Driver");
                tags.add("West Route: Another");
            } else if (day == 29) {
                tags.add("South Route: Action");
            }

            calendarDays.add(new CalendarAdapterNew.CalendarDayModel(String.valueOf(day), false, tags));
        }

        adapter = new CalendarAdapterNew(this, calendarDays);
        rvCalendarGrid.setAdapter(adapter);
    }
}
