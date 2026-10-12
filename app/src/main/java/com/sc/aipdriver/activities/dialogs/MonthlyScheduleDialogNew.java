package com.sc.aipdriver.activities.dialogs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
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
import com.sc.aipdriver.activities.ui.MonthlyScheduleActivityNew;

import java.util.Calendar;

public class MonthlyScheduleDialogNew extends DialogFragment {

    private DatePicker datePicker;
    private Button btnContinue;
    private Button btnOkcal;
    private Button btnCancel;
    private TextView tvBack;
    private LinearLayout rlCalRow;
    private ScrollView svContent;
    private RelativeLayout rlCal;

    public static MonthlyScheduleDialogNew newInstance() {
        return new MonthlyScheduleDialogNew();
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
        View view = inflater.inflate(R.layout.dialog_monthly_schedule_new, container, false);

        datePicker = view.findViewById(R.id.datePicker);
        btnContinue = view.findViewById(R.id.btn_continue);
        btnOkcal = view.findViewById(R.id.btnOkcal);
        btnCancel = view.findViewById(R.id.btnCancel);
        tvBack = view.findViewById(R.id.tv_back);
        rlCalRow = view.findViewById(R.id.rl_cal_row);
        svContent = view.findViewById(R.id.sv_content);
        rlCal = view.findViewById(R.id.rlCalendar);
        ImageView ivCal = view.findViewById(R.id.iv_calendar);

        Calendar todayCal = Calendar.getInstance();
        datePicker.updateDate(
                todayCal.get(Calendar.YEAR),
                todayCal.get(Calendar.MONTH),
                todayCal.get(Calendar.DAY_OF_MONTH)
        );

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
            rlCal.setVisibility(GONE);
            svContent.setVisibility(VISIBLE);
        });

        // CONTINUE button click -> launch MonthlyScheduleActivityNew
        btnContinue.setOnClickListener(v -> {
            dismiss();
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MonthlyScheduleActivityNew.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
