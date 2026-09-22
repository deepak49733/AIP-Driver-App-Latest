package com.sc.aipdriver.activities.dialogs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.IsInternetAvailableKt;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.otherclasses.PreferenceUtils;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.otherclasses.ShouldLogout;
import com.sc.aipdriver.activities.ui.FarmListRoute;
import com.sc.aipdriver.activities.ui.SelectRoute;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatePickerFullScreenDialog extends DialogFragment {

    private static final String ARG_DATE = "arg_date";
    private static final String TITLE = "route";

    private DatePicker datePicker;

    private Button btnOk;
    private Button btnOkcal;
    private Button btnCancel;
    private TextView tvDateFor;
    private TextView tvChangeDate;
    private TextView tvTitle;
    ShouldLogout shouldLogout = null;
    ApiInterface apiService;
    private TextView tvCancel;
    ImageView ivCal;
    private RelativeLayout rlDateSelected;
    private RelativeLayout rlCal;


    private Calendar calendar;

    public interface OnDateSelectedListener {
        void onDateSelected(String date);
    }


    private OnDateSelectedListener listener;
    String selectedDate ="";
    String showDate ="";
    SharedprefrenceManager sharedprefrenceManager;

    public static DatePickerFullScreenDialog newInstance(String date, String route) {
        DatePickerFullScreenDialog dialog = new DatePickerFullScreenDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DATE, date);
        bundle.putString(TITLE, route);
        dialog.setArguments(bundle);

        Log.d("Analysis__","Inside date picker");
        return dialog;
    }

    public void setListener(OnDateSelectedListener listener) {

        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            getDialog().getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.WHITE)
            );
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.dialog_fullscreen_datepicker, container, false);
        datePicker = view.findViewById(R.id.datePicker);
        btnOk = view.findViewById(R.id.btn_ok);
        tvTitle = view.findViewById(R.id.tv_title);
        btnOkcal = view.findViewById(R.id.btnOkcal);
        sharedprefrenceManager = new SharedprefrenceManager(requireContext());
        shouldLogout = new ShouldLogout(requireContext(),sharedprefrenceManager);
        btnCancel = view.findViewById(R.id.btnCancel);
        tvDateFor = view.findViewById(R.id.tv_selecteddate);
        tvDateFor.setText(sharedprefrenceManager.getShowDate());
        tvChangeDate = view.findViewById(R.id.tv_changeDate);
        tvCancel = view.findViewById(R.id.tv_cancel);
        rlDateSelected = view.findViewById(R.id.rl_dated);
        rlCal = view.findViewById(R.id.rlCalendar);
        ivCal = view.findViewById(R.id.iv_calendar);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRide();
            }
        });
        if (getArguments() != null) {
            String dateStr = getArguments().getString(ARG_DATE);
            String route = getArguments().getString(TITLE);
      //      tvTitle.setText(route);
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "MM/dd/yyyy",
                            Locale.getDefault()
                    );

                    Date date = sdf.parse(changeDateString(dateStr));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    datePicker.updateDate(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    selectedDate = dateStr;
                    tvDateFor.setText(dateStr);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

//        btnCancel = view.findViewById(R.id.btnCancel);

        // ❗ Disable today & future dates
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        tvChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlDateSelected.setVisibility(GONE);
                rlCal.setVisibility(VISIBLE);
            }
        });
        ivCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlDateSelected.setVisibility(GONE);
                rlCal.setVisibility(VISIBLE);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlCal.setVisibility(GONE);
                rlDateSelected.setVisibility(VISIBLE);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IsInternetAvailableKt.isInternetAvailable(requireContext())) {
                    checkInternet();
                }
                else{
                if (listener != null) {
                    Log.d("SelectedDate__", selectedDate);

                    if (selectedDate.isEmpty()) {
                        if (!tvDateFor.getText().toString().trim().isEmpty()) {
                            String tvDate = tvDateFor.getText().toString();
                            String formattedDate = formatToMMDDYYYY(tvDate);
                            sharedprefrenceManager.setImprovedDate(formattedDate);
                            SimpleDateFormat inputFormat = new SimpleDateFormat("M/dd/yyyy", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
                            try {
                                Date datee = inputFormat.parse(tvDate);
                                selectedDate = outputFormat.format(datee);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                listener.onDateSelected(selectedDate);
                                dismiss();
                            }
                        }
                    }

                    listener.onDateSelected(selectedDate);
                }
                dismiss();
            }
            }
        });
//        datePicker.setMaxDate(calendar.getTimeInMillis());
        btnOkcal.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();
            showDate = month + "/" + day + "/" + year;
             selectedDate = year + "-" + month + "-" + day;
            sharedprefrenceManager.setShowDate(showDate);
            String formattedDate = formatToMMDDYYYY(showDate);
            sharedprefrenceManager.setImprovedDate(formattedDate);
            tvDateFor.setText(showDate+"");
            rlCal.setVisibility(GONE);
            rlDateSelected.setVisibility(VISIBLE);

        });
        return view;
    }
    private void checkInternet() {

            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No Internet Connection")
                    .setContentText("No offline data available. Please connect to the internet to continue.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        if (IsInternetAvailableKt.isInternetAvailable(requireActivity())) {

                        } else {
                            checkInternet();
                        }
                    })
                    .show();

    }
    public static String changeDateString(String date) {
        if (date == null) return "";

        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] parts = date.split("-");
            return parts[1] + "/" + parts[2] + "/" + parts[0];
        }
        return date;
    }
    private void cancelRide() {
        Call<BaseResponse> call = apiService.cancelRide(sharedprefrenceManager.getParentId(),sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == 200) {

                    shouldLogout.shouldLogout(true,requireContext());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e("Data  errorratro", t.getMessage());
                Toast.makeText(requireContext(), "No internt Connection", Toast.LENGTH_SHORT).show();
//                System.err.println("In Select Car Vehicle  Failure" );

            }
        });
    }
}
