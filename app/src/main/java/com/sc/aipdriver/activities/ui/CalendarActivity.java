package com.sc.aipdriver.activities.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.adapters.CalendarDateAdapter;
import com.sc.aipdriver.activities.adapters.TimelineAdapter;
import com.sc.aipdriver.activities.models.CalendarDateModel;
import com.sc.aipdriver.activities.models.TimelineEventModel;
import com.sc.aipdriver.activities.models.TimelineRowModel;
import com.sc.aipdriver.databinding.ActivityCalendarBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private ActivityCalendarBinding binding;
    private List<CalendarDateModel> dateList = new ArrayList<>();
    private CalendarDateAdapter dateAdapter;
    private List<TimelineRowModel> timelineList = new ArrayList<>();
    private TimelineAdapter timelineAdapter;
    private Calendar currentCalendar = Calendar.getInstance();
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private boolean isMaximized = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        setupCalendar();
        setupTimeline();
        setupFilters();
    }

    private void initView() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        binding.btnExpandCollapse.setOnClickListener(v -> {
            isMaximized = !isMaximized;
            updateCalendarState();
        });

        binding.btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            setupCalendar();
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            setupCalendar();
        });
    }

    private void updateCalendarState() {
        if (isMaximized) {
            binding.tvExpandCollapse.setText("Minimize");
            binding.ivExpandIcon.setRotation(180);
            setupCalendar(); // Show all dates
        } else {
            binding.tvExpandCollapse.setText("Maximize");
            binding.ivExpandIcon.setRotation(0);
            showWeeklyView();
        }
    }

    private void setupCalendar() {
        binding.tvCurrentMonth.setText(monthFormat.format(currentCalendar.getTime()));
        dateList.clear();

        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int month = cal.get(Calendar.MONTH);
        
        // Add dates of current month
        while (cal.get(Calendar.MONTH) == month) {
            boolean isSelected = isSameDay(cal.getTime(), new Date());
            // Mocking indicators
            boolean hasMeeting = cal.get(Calendar.DAY_OF_MONTH) % 3 == 0;
            boolean hasEvent = cal.get(Calendar.DAY_OF_MONTH) % 5 == 0;
            boolean hasShift = cal.get(Calendar.DAY_OF_MONTH) % 7 == 0;
            
            dateList.add(new CalendarDateModel(cal.getTime(), isSelected, hasMeeting, hasEvent, hasShift));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        dateAdapter = new CalendarDateAdapter(dateList, (model, position) -> {
            for (CalendarDateModel d : dateList) d.setSelected(false);
            model.setSelected(true);
            dateAdapter.notifyDataSetChanged();
            // In a real app, you would filter timeline here based on date
        });

        binding.rvCalendarDates.setLayoutManager(new GridLayoutManager(this, 7));
        binding.rvCalendarDates.setAdapter(dateAdapter);
    }

    private void showWeeklyView() {
        // Show only current week or first 7 days for demo
        List<CalendarDateModel> weeklyList = new ArrayList<>();
        for (int i = 0; i < Math.min(7, dateList.size()); i++) {
            weeklyList.add(dateList.get(i));
        }
        dateAdapter = new CalendarDateAdapter(weeklyList, (model, position) -> {
             // Handle click
        });
        binding.rvCalendarDates.setAdapter(dateAdapter);
    }

    private void setupTimeline() {
        timelineList.clear();

        // 09:00
        List<TimelineEventModel> events9 = new ArrayList<>();
        events9.add(new TimelineEventModel("Heading ondsd...", "09:00", "09:20", TimelineEventModel.Type.MEETING));
        events9.add(new TimelineEventModel("this event nameed nasadas...", "09:00", "13:20", TimelineEventModel.Type.EVENT));
        timelineList.add(new TimelineRowModel("09:00", events9));

        // 10:00
        List<TimelineEventModel> events10 = new ArrayList<>();
        events10.add(new TimelineEventModel("this event nameed nasadas...", "10:30", "14:00", TimelineEventModel.Type.EVENT));
        timelineList.add(new TimelineRowModel("10:00", events10));

        // 11:00
        List<TimelineEventModel> events11 = new ArrayList<>();
        events11.add(new TimelineEventModel("Heading ondsd...", "10:45", "11:20", TimelineEventModel.Type.MEETING));
        events11.add(new TimelineEventModel("All drift van hu..", "11:31", "12:30", TimelineEventModel.Type.SHIFT));
        timelineList.add(new TimelineRowModel("11:00", events11));

        // Add more empty slots
        for (int i = 12; i <= 20; i++) {
            timelineList.add(new TimelineRowModel(String.format(Locale.getDefault(), "%02d:00", i), new ArrayList<>()));
        }

        timelineAdapter = new TimelineAdapter(timelineList);
        binding.rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTimeline.setAdapter(timelineAdapter);
    }

    private void setupFilters() {
        View.OnClickListener filterClick = v -> {
            binding.filterAll.setSelected(false);
            binding.filterMeetings.setSelected(false);
            binding.filterShifts.setSelected(false);
            binding.filterEvents.setSelected(false);

            v.setSelected(true);
            
            updateFilterColors((TextView) binding.filterAll);
            updateFilterColors((TextView) binding.filterMeetings);
            updateFilterColors((TextView) binding.filterShifts);
            updateFilterColors((TextView) binding.filterEvents);
        };

        binding.filterAll.setOnClickListener(filterClick);
        binding.filterMeetings.setOnClickListener(filterClick);
        binding.filterShifts.setOnClickListener(filterClick);
        binding.filterEvents.setOnClickListener(filterClick);

        binding.filterAll.setSelected(true);
        updateFilterColors((TextView) binding.filterAll);
    }

    private void updateFilterColors(TextView tv) {
        if (tv.isSelected()) {
            tv.setTextColor(getResources().getColor(R.color.filter_selected_text));
        } else {
            tv.setTextColor(getResources().getColor(R.color.filter_unselected_text));
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
