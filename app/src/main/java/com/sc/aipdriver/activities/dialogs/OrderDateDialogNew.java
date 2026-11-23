package com.sc.aipdriver.activities.dialogs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sc.aipdriver.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OrderDateDialogNew extends DialogFragment {

    private static final String ARG_DATE = "arg_date";
    private static final String ARG_ROUTE = "arg_route";

    public interface OnDateSelectedListener {
        void onDateSelected(String date);
    }

    private DatePicker datePicker;
    private Button btnContinue;
    private Button btnOkcal;
    private Button btnCancel;
    private TextView tvDateSelected;
    private TextView tvBack;
    private LinearLayout rlCalRow;
    private ScrollView svContent;
    private RelativeLayout rlCal;

    private OnDateSelectedListener listener;
    private String selectedDate = "";
    private String showDate = "";

    public static OrderDateDialogNew newInstance(String date, String route) {
        OrderDateDialogNew dialog = new OrderDateDialogNew();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DATE, date);
        bundle.putString(ARG_ROUTE, route);
        dialog.setArguments(bundle);
        return dialog;
    }

    public void setListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_order_date_new, container, false);

        datePicker = view.findViewById(R.id.datePicker);
        btnContinue = view.findViewById(R.id.btn_continue);
        btnOkcal = view.findViewById(R.id.btnOkcal);
        btnCancel = view.findViewById(R.id.btnCancel);
        tvDateSelected = view.findViewById(R.id.tv_selecteddate);
        tvBack = view.findViewById(R.id.tv_back);
        rlCalRow = view.findViewById(R.id.rl_cal_row);
        svContent = view.findViewById(R.id.sv_content);
        rlCal = view.findViewById(R.id.rlCalendar);
        ImageView ivCal = view.findViewById(R.id.iv_calendar);

        // Compute default date: 1 day after current date (tomorrow)
        Calendar tomorrowCal = Calendar.getInstance();
        tomorrowCal.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat displaySdf = new SimpleDateFormat("M/d/yyyy", Locale.US);
        SimpleDateFormat apiSdf = new SimpleDateFormat("yyyy-M-d", Locale.US);

        showDate = displaySdf.format(tomorrowCal.getTime());
        selectedDate = apiSdf.format(tomorrowCal.getTime());

        // Allow driver to select current date (today) or future dates in DatePicker
        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);
        datePicker.setMinDate(todayCal.getTimeInMillis());

        // Set initial date picker date to tomorrow
        datePicker.updateDate(
                tomorrowCal.get(Calendar.YEAR),
                tomorrowCal.get(Calendar.MONTH),
                tomorrowCal.get(Calendar.DAY_OF_MONTH)
        );

        tvDateSelected.setText(showDate);

        // Header BACK button click -> dismiss dialog
        tvBack.setOnClickListener(v -> dismiss());

        // Open calendar picker
        View.OnClickListener openCalendarListener = v -> {
            svContent.setVisibility(GONE);
            rlCal.setVisibility(VISIBLE);
        };
        rlCalRow.setOnClickListener(openCalendarListener);
        ivCal.setOnClickListener(openCalendarListener);

        btnCancel.setOnClickListener(v -> {
            rlCal.setVisibility(GONE);
            svContent.setVisibility(VISIBLE);
        });

        btnOkcal.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();
            showDate = month + "/" + day + "/" + year;
            selectedDate = year + "-" + month + "-" + day;

            tvDateSelected.setText(showDate);
            rlCal.setVisibility(GONE);
            svContent.setVisibility(VISIBLE);
        });

        btnContinue.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateSelected(selectedDate);
            }
            dismiss();
        });

        return view;
    }
}
