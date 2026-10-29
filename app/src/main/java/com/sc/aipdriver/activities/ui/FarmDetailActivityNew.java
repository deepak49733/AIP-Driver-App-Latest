package com.sc.aipdriver.activities.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.ApiResponse;
import com.sc.aipdriver.activities.models.FarmInfo;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmDetailActivityNew extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvFarmName;
    private TextView tvOfficePhone;
    private TextView tvEmergencyContact;
    private TextView tvAlternateContact;
    private TextView tvAddress;
    private TextView tvInstructions;
    private LinearLayout llExpandInstructions;
    private ImageView ivFarmImage;
    private ImageView ivDropLocationImage;

    private ApiInterface apiService;
    private SharedprefrenceManager sharedprefrenceManager;

    private String farmId = "";
    private String farmName = "";
    private String fullInstructionsText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_detail_new);

        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        sharedprefrenceManager = new SharedprefrenceManager(this);

        if (getIntent() != null) {
            farmId = getIntent().getStringExtra("FarmID");
            if (farmId == null || farmId.isEmpty()) {
                farmId = getIntent().getStringExtra("farmId");
            }
            farmName = getIntent().getStringExtra("FarmName");
            if (getIntent().hasExtra("Instruction")) {
                String ins = getIntent().getStringExtra("Instruction");
                if (ins != null && !ins.trim().isEmpty() && !ins.equalsIgnoreCase("yes") && !ins.equalsIgnoreCase("no")) {
                    fullInstructionsText = ins.trim();
                }
            }
        }

        initViews();
        setupListeners();
        populateInitialData();
        fetchFarmDetails();
    }

    private void initViews() {
        tvBack = findViewById(R.id.tv_back);
        tvFarmName = findViewById(R.id.tv_farm_name);
        tvOfficePhone = findViewById(R.id.tv_office_phone);
        tvEmergencyContact = findViewById(R.id.tv_emergency_contact);
        tvAlternateContact = findViewById(R.id.tv_alternate_contact);
        tvAddress = findViewById(R.id.tv_address);
        tvInstructions = findViewById(R.id.tv_instructions);
        llExpandInstructions = findViewById(R.id.ll_expand_instructions);
        ivFarmImage = findViewById(R.id.iv_farm_image);
        ivDropLocationImage = findViewById(R.id.iv_drop_location_image);
    }

    private void setupListeners() {
        tvBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        llExpandInstructions.setOnClickListener(v -> showFullInstructionsDialog());
    }

    private void populateInitialData() {
        if (farmName != null && !farmName.isEmpty()) {
            tvFarmName.setText(farmName);
        } else {
            tvFarmName.setText("");
        }

        if (fullInstructionsText != null && !fullInstructionsText.trim().isEmpty()) {
            tvInstructions.setText(fullInstructionsText);
        } else {
            fullInstructionsText = "";
            tvInstructions.setText("");
        }
    }

    private void showFullInstructionsDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Full Instructions")
                .setContentText(fullInstructionsText)
                .setConfirmText("OK")
                .setConfirmClickListener(dialog -> dialog.dismissWithAnimation())
                .show();
    }

    private void fetchFarmDetails() {
        if (farmId == null || farmId.isEmpty()) return;

        Call<ApiResponse> call = apiService.getFarmInfo(sharedprefrenceManager.getRideId(), farmId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200 && response.body() != null && response.body().getData() != null) {
                    FarmInfo info = response.body().getData();
                    bindFarmInfo(info);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("FarmDetailActivityNew", "Error fetching farm info: " + t.getMessage());
            }
        });
    }

    private void bindFarmInfo(FarmInfo info) {
        if (info == null) return;

        tvFarmName.setText(info.getFirmName() != null ? info.getFirmName() : (farmName != null ? farmName : ""));
        tvOfficePhone.setText(info.getOfficePhone() != null ? info.getOfficePhone() : "");
        tvEmergencyContact.setText(info.getFarmEmergencyNo() != null ? info.getFarmEmergencyNo() : "");
        tvAlternateContact.setText(info.getFarmEmergencyNo2nd() != null ? info.getFarmEmergencyNo2nd() : "");
        tvAddress.setText(info.getAddress() != null ? info.getAddress() : "");

        String instructions = info.getInstruction();
        if (instructions == null || instructions.trim().isEmpty() || instructions.equalsIgnoreCase("yes") || instructions.equalsIgnoreCase("no")) {
            instructions = fullInstructionsText;
        }

        if (instructions != null && !instructions.trim().isEmpty() && !instructions.equalsIgnoreCase("yes") && !instructions.equalsIgnoreCase("no")) {
            fullInstructionsText = instructions.trim();
            tvInstructions.setText(fullInstructionsText);
        } else {
            fullInstructionsText = "";
            tvInstructions.setText("");
        }

        if (info.getUserImage1() != null && !info.getUserImage1().trim().isEmpty()) {
            String imageUrl = ApiClient.BASE_URL + info.getUserImage1();
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_).error(R.drawable.ic_launcher_).into(ivFarmImage);
        } else {
            ivFarmImage.setImageResource(R.drawable.ic_launcher_);
        }

        if (info.getSemenDropLocationImg1() != null && !info.getSemenDropLocationImg1().trim().isEmpty()) {
            String dropUrl = ApiClient.BASE_URL + info.getSemenDropLocationImg1();
            Picasso.get().load(dropUrl).placeholder(R.drawable.ic_launcher_).error(R.drawable.ic_launcher_).into(ivDropLocationImage);
        } else {
            ivDropLocationImage.setImageResource(R.drawable.ic_launcher_);
        }
    }
}
