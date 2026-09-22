package com.sc.aipdriver.activities.dialogs;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.fragments.FinishDialog;
import com.sc.aipdriver.activities.interfaces.OnClickSubmit;
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData;
import com.sc.aipdriver.activities.models.LiRoutePlannerDetail;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.otherclasses.Global;
import com.sc.aipdriver.activities.otherclasses.PreferenceUtils;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.otherclasses.Utils;
import com.sc.aipdriver.activities.room.AppDatabase;
import com.sc.aipdriver.activities.room.FarmTempBegEntity;
import com.sc.aipdriver.activities.ui.MapsActivityNew;
import com.sc.aipdriver.activities.adapters.FarmListAdapterByRote;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.BaseResponse;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.TempBags;
import com.sc.aipdriver.activities.room.PhotoSyncDao;
import com.sc.aipdriver.activities.room.PhotoSyncEntity;
import com.sc.aipdriver.activities.ui.Utility;
import com.sc.aipdriver.activities.worker.SyncScheduler;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by dev on 24/1/18.
 */

public class LoadTemprature extends BottomSheetDialogFragment {
    Activity activity;
    Dialog dialog;

    Button submit;
    private String imgPath1 = "";
    private String imgPath2 = "";
    private String imgPath3 = "";
    private String imgPath4 = "";
    private Uri imgUri1;
    private Uri imgUri2;
    private Uri imgUri3;
    private Uri imgUri4;
    private String bagsLoaded;
    private String tempLoaded;
    EditText temprature;
    EditText tempratureRef;
    EditText comments;
    ApiInterface apiService2;
    TextInputLayout til;
    String fId = "";
    EditText semenbags;
    LinearLayout uploadLL1, uploadLL2;
    FarmListAdapterByRote farmListAdapterByRote;
    MapsActivityNew mapsActivityNew;
    String farmid;
    String routeName;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    String orderDate = "";
    ShowLoading showLoading;
    ApiInterface apiService;
    private int currentImageIndex = -1;
    Gson gson;
    TempBags tempBags;
    SharedprefrenceManager sharedprefrenceManager;
    int id;
    boolean bagsTemp = false;
    boolean isFromMap = false;
    boolean isNew = false;
    String routeId = "";
    ArrayList<TempBags> tempBagsArrayList;
    boolean checkExisting = false;
    boolean shouldWait = false;

    ArrayList<LiRoutePlannerDetail> liRoutePlannerDetails = new ArrayList<>();

    OnClickSubmit onClickSubmit;
    double laodedTemp = 0.0;
    int loadedBeg = 0;
    ImageView[] imageViews;
    ImageView imgOne, imgTwo, imgThree, imgFour;
    TextView tvTitle, tvUpload, tvSkip;
    ArrayList<Uri> imageUris = new ArrayList<>();
    ArrayList<String> imagePaths = new ArrayList<>();
    private Uri currentPhotoUri;
    private String currentPhotoPath;

    private boolean valid() {
        if (semenbags.getText().toString().isEmpty()) {
            semenbags.setError("Enter quantity");
            return false;
        } else if (semenbags.getText().toString().equals("0")) {
            Toast.makeText(activity, "Number of quantity should be greater than 0", Toast.LENGTH_SHORT).show();

            return false;
        } else if (temprature.getText().toString().isEmpty()) {
            temprature.setError("Enter Temp");
            return false;
        } else
            return true;
    }

    private void success(String st1, String st2, Runnable onOk) {
        try {
            SweetAlertDialog sd = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE);
            sd.setTitleText(st1);
            sd.setContentText(st2);
            sd.setConfirmText("OK");
            sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                    if (onOk != null) {
                        onOk.run();
                    }
                }
            });
            sd.show();

        } catch (Exception e) {

        }
    }

    private void getData() {
        Call<BaseResponse<List<TempBags>>> call = apiService.getTempBags(routeName, Integer.parseInt(farmid), sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
        call.enqueue(new Callback<BaseResponse<List<TempBags>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<TempBags>>> call, Response<BaseResponse<List<TempBags>>> response) {
                if (response.code() == 200) {

                    if (response.body().getData().size() > 0) {
                        id = response.body().getData().get(0).getID();
                        bagsLoaded = response.body().getData().get(0).getBeg().toString();
                        //Log.d("Analysis__","number of bags from api "+bagsLoaded);
                        tempLoaded = response.body().getData().get(0).getTemp().toString();
//                        semenbags.setText("" + response.body().getData().get(0).getBeg());
//                        temprature.setText("" + response.body().getData().get(0).getTemp());
//                        semenbags.setSelection(semenbags.getText().length());
//                        temprature.setSelection(temprature.getText().length());
                        bagsTemp = true;
                    }
                } else
                    try {


                        // getFarmTempBagsOffline(farmid,orderDate);

                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                        Log.e("FarmsError", loginError.getMessage());
//                            Toast.makeText(activity, loginError.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<TempBags>>> call, Throwable t) {
                Log.e("FarmsFail", t.getMessage());
                // Only fall back to local DB in Sync mode; Online mode has no local storage
                if (sharedprefrenceManager.isSyncMode()) {
                    getFarmTempBagsOffline(farmid, orderDate);
                } else {
                    Toast.makeText(activity, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void getFarmTempBagsOffline(String farmId, String date) {

        new Thread(() -> {

            AppDatabase db = AppDatabase.Companion.getDatabase(requireContext());

            PriorityFarmData farm =
                    db.priorityFarmDao().getFarmTempBags(farmId + "", date);


            if (farm != null) {


                bagsLoaded = farm.getBegs() + "";
                //Log.d("Analysis__","number of bags from api "+bagsLoaded);
                tempLoaded = farm.getTmperature() + "";
                bagsTemp = true;
            }


        }).start();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Log.d("Analysis__", "Creating File " + imageFileName);
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openCamera() {

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Uri photoUri = FileProvider.getUriForFile(
                activity,
                activity.getPackageName() + ".fileprovider",
                photoFile
        );

        currentPhotoUri = photoUri; // Save for use after capture
        currentPhotoPath = photoFile.getAbsolutePath(); // Save path for SharedPreferences

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraLauncher.launch(cameraIntent);
    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void showImagePickerDialog() {

        String[] options = {"Camera", "Gallery"};

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }

                })
                .show();
    }

    public BottomSheetDialogFragment instance(Activity activity, String farmid, String routeName, String routeId, boolean checkExisting, OnClickSubmit onClickSubmit, int begs, double temprature, String orderDate) {
        this.activity = activity;
        this.farmListAdapterByRote = farmListAdapterByRote;
        this.farmid = farmid;
        this.routeId = routeId;
        this.routeName = routeName;
        this.checkExisting = checkExisting;
        this.onClickSubmit = onClickSubmit;
        // Regex to check yyyy-M-d format (single-digit month/day)
        String regex = "\\d{4}-\\d{1,2}-\\d{1,2}";
        if (orderDate != null && orderDate.matches(regex)) {
            try {
                // Parse the original date
                LocalDate date = LocalDate.parse(orderDate, DateTimeFormatter.ofPattern("yyyy-M-d"));

                // Format as MM/dd/yyyy
                this.orderDate = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            } catch (DateTimeParseException e) {
                // If parsing fails, keep original
                this.orderDate = orderDate;
            }
        } else {
            // If not in yyyy-M-d format, keep original
            this.orderDate = orderDate;
        }
        loadedBeg = begs;
        laodedTemp = temprature;

        Log.d("LOGIN__", "orderDate  is : " + orderDate + "Id is " + routeId + " fid is " + fId);
        fId = farmid;
        //Log.d("Analysis__", "looooo farmId is " + farmid);
        if (sharedprefrenceManager == null) {
            sharedprefrenceManager = new SharedprefrenceManager(activity);
        }
        //Log.d("Analysis__", "looooo fid is setting in db " + fId);
        sharedprefrenceManager.setFID(fId);
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        Uri uri = result.getData().getData();
                        File file = getFileFromUri(uri);

                        if (file == null) {
                            Toast.makeText(requireContext(), "Unable to read image", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String path = file.getAbsolutePath();

                        if (currentImageIndex == 0) {

                            imgUri1 = uri;
                            imgPath1 = path;

                            Glide.with(this)
                                    .load(uri)
                                    .into(imgOne);

                        } else if (currentImageIndex == 1) {

                            imgUri2 = uri;
                            imgPath2 = path;

                            Glide.with(this)
                                    .load(uri)
                                    .into(imgTwo);

                        } else if (currentImageIndex == 2) {

                            imgUri3 = uri;
                            imgPath3 = path;

                            Glide.with(this)
                                    .load(uri)
                                    .into(imgThree);

                        } else if (currentImageIndex == 3) {

                            imgUri4 = uri;
                            imgPath4 = path;

                            Glide.with(this)
                                    .load(uri)
                                    .into(imgFour);
                        }

                        Log.d("UPLOAD_DEBUG", "Selected image path: " + path);
                    }
                }
        );
        isNew = true;
        return this;
    }

    private File getFileFromUri(Uri uri) {
        try {

            File file = new File(requireContext().getCacheDir(),
                    "upload_" + System.currentTimeMillis() + ".jpg");

            InputStream inputStream = requireContext()
                    .getContentResolver()
                    .openInputStream(uri);

            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;

            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }

            outputStream.close();
            inputStream.close();

            return file;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.sumbittemprature_dialog, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            showLoading = new ShowLoading(activity);
            apiService = ApiClient.getClient(activity).create(ApiInterface.class);
            apiService2 = ApiClient.getHttp1Client(getActivity()).create(ApiInterface.class);
            gson = new GsonBuilder().setPrettyPrinting().create();
            //dialog = new Dialog(activity);
            if (sharedprefrenceManager == null) {
                sharedprefrenceManager = new SharedprefrenceManager(activity);
            }
            submit = contentView.findViewById(R.id.submit);
            tvTitle = contentView.findViewById(R.id.title_load);
            tvUpload = contentView.findViewById(R.id.tv_upload);
            tvSkip = contentView.findViewById(R.id.tv_skip);
            til = contentView.findViewById(R.id.tempreftil);
            tempratureRef = contentView.findViewById(R.id.tempratureRef);
            comments = contentView.findViewById(R.id.comments);
            temprature = contentView.findViewById(R.id.temprature);
            semenbags = contentView.findViewById(R.id.semenbags);
            uploadLL1 = contentView.findViewById(R.id.upload_id_ll);
            uploadLL2 = contentView.findViewById(R.id.upload_id_ll2);

            imgOne = contentView.findViewById(R.id.iv_img1);
            imgTwo = contentView.findViewById(R.id.iv_img2);
            imgThree = contentView.findViewById(R.id.iv_img3);
            imgFour = contentView.findViewById(R.id.iv_img4);

            til.setVisibility(View.GONE);
            if (checkExisting) {
                tvTitle.setText(R.string.simentemp_offload);
                til.setVisibility(View.VISIBLE);
                comments.setVisibility(VISIBLE);
                temprature.setHint(getString(R.string.offloading));
                tvUpload.setVisibility(VISIBLE);
                tvUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvUpload.setVisibility(GONE);
                        tvSkip.setVisibility(VISIBLE);
                        uploadLL1.setVisibility(VISIBLE);
                        uploadLL2.setVisibility(VISIBLE);
                    }
                });
                tvSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvUpload.setVisibility(VISIBLE);
                        tvSkip.setVisibility(GONE);
                        uploadLL1.setVisibility(GONE);
                        uploadLL2.setVisibility(GONE);
                    }
                });

                semenbags.setHint(getString(R.string.semenbagsdel));
                getData();
            } else {
                tvTitle.setText(R.string.simentemp_onload);
                uploadLL1.setVisibility(View.GONE);
                uploadLL2.setVisibility(View.GONE);
                comments.setVisibility(GONE);
                semenbags.setHint(getString(R.string.semenbags));
                temprature.setHint(getString(R.string.onloading));
            }
            if (loadedBeg != 0) {
                semenbags.setText(loadedBeg + "");
                temprature.setText(laodedTemp + "");
            }
            submit.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              submit.setEnabled(false);
                                              if (valid()) {

                                                  if (checkExisting) {
                                                      if (tvSkip.getVisibility() == VISIBLE) {
                                                          if (imgPath1.length() > 2 || imgPath2.length() > 2 || imgPath3.length() > 2 || imgPath4.length() > 2) {
                                                              shouldWait = true;
                                                              uploadPhotos();
                                                          } else {
                                                              Toast.makeText(requireContext(), "Kindly upload photo or click skip", Toast.LENGTH_SHORT).show();
                                                              submit.setEnabled(true);
                                                              return;
                                                          }
                                                      }
                                                  }
                                                  dismiss();

                                                  int finalBags = Integer.parseInt(semenbags.getText().toString());
                                                  String finalTemp = temprature.getText().toString();
                                                  String finalRefTemp = tempratureRef.getText().toString();
                                                  AppDatabase db = AppDatabase.Companion.getDatabase(activity);
                                                  String currentDate = sharedprefrenceManager.getDate();
                                                  String rawDate = sharedprefrenceManager.getDate();

                                                  String formattedDate = formatToMMDDYYYY(rawDate);
                                                  boolean shouldQueueOffline = sharedprefrenceManager.isSyncMode();
                                                  // In Online mode do not persist any load/photo state locally.
                                                  if (!checkExisting) {
                                                      new Thread(() -> {

                                                          FarmTempBegEntity entity = new FarmTempBegEntity();
                                                          entity.farmId = Integer.parseInt(fId);
                                                          entity.beg = finalBags;
                                                          entity.temp = finalTemp;
                                                          entity.coolerTemp = finalRefTemp;
                                                          entity.routeName = routeName;
                                                          entity.ParentId = (Integer.valueOf(sharedprefrenceManager.getParentId()));
                                                          entity.driverId = sharedprefrenceManager.getDriverID();
                                                          entity.Token = sharedprefrenceManager.getToken();
                                                          entity.orderDate = formattedDate;
                                                          entity.isImproved = checkExisting;

                                                          if (shouldQueueOffline) {
                                                              entity.isSynced = false;
                                                              db.farmTempBegDao().insert(entity);
                                                              SyncScheduler.INSTANCE.runImmediateSync(activity);
                                                          }

                                                          if (shouldQueueOffline) {
                                                              db.priorityFarmDao().updateFarmTempBags(
                                                                      entity.farmId,
                                                                      formattedDate,
                                                                      finalBags,
                                                                      Double.parseDouble(finalTemp),
                                                                      1,
                                                                      1);

                                                              db.improvedPriorityFarmDao().updateImprovedFarmTempBags(
                                                                      Integer.parseInt(fId),
                                                                      currentDate,
                                                                      finalBags,
                                                                      Double.parseDouble(finalTemp),
                                                                      1,
                                                                      1,
                                                                      0
                                                              );
                                                          }

                                                      }).start();
                                                  }

                                                  if (isNew) {
                                                      LiRoutePlannerDetail liRoutePlannerDetail = new LiRoutePlannerDetail();
                                                      liRoutePlannerDetail.setFIRMID(farmid);
                                                      liRoutePlannerDetail.setNumberOfBagsLoaded(semenbags.getText().toString());
                                                      liRoutePlannerDetail.setTemperatureOfSemenLoaded(temprature.getText().toString());
                                                      liRoutePlannerDetail.setCustomerSeemanCoolarTemp(tempratureRef.getText().toString());
                                                      liRoutePlannerDetails.add(liRoutePlannerDetail);
                                                  } else {
                                                      farmListAdapterByRote.additem(farmid, semenbags.getText().toString(), temprature.getText().toString());
                                                  }

                                                  if (bagsTemp == false) {
                                                      Log.d("Analysis__", "Updating temp bags for farmId Line 628" + fId);
                                                      showLoading.show();
                                                      tempBags = new TempBags();
                                                      tempBagsArrayList = new ArrayList<>();
                                                      tempBags.setID(0);
                                                      tempBags.setRouteName(routeName);

                                                      if (farmid.length() > 0)
                                                          tempBags.setFarmID(Integer.parseInt(fId));
                                                      tempBags.setBeg(Integer.parseInt(semenbags.getText().toString()));
                                                      tempBags.setTemp(temprature.getText().toString());
                                                      tempBags.setParentId(Integer.valueOf(sharedprefrenceManager.getParentId()));
                                                      tempBagsArrayList.add(0, tempBags);
                                                      if (checkExisting) {
                                                          tempBags.setRefTem(tempratureRef.getText().toString().trim());
                                                      }
//                                                      if (!sharedprefrenceManager.getToggleState()) {

                                                      if (sharedprefrenceManager.isSyncMode()) {
                                                          showLoading.dismiss();
                                                          if (tvSkip.getVisibility() == VISIBLE) {
                                                              onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), false);
                                                          } else {
                                                              onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), true);
                                                          }
                                                          success("Success", "Information is saved and will be synced.", null);
                                                      } else {
                                                          Call<CommonError> call = apiService.sendTempBags(tempBagsArrayList, sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
                                                          call.enqueue(new Callback<CommonError>() {
                                                              @Override
                                                              public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                                                                  new Thread(() -> {
                                                                      db.farmTempBegDao().markSynced(
                                                                              Integer.parseInt(fId),
                                                                              formattedDate
                                                                      );
                                                                  }).start();

                                                                  showLoading.dismiss();
                                                                  if (response.code() == 200) {
                                                                      bagsTemp = true;
                                                                      success("Success", response.body().getData(), () -> {
                                                                          if (tvSkip.getVisibility() == VISIBLE) {
                                                                              // ✅ If photos are pending, don't trigger email here.
                                                                              // Let onPhotoUpload handle it to prevent duplicates.
                                                                              onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), false);
                                                                          } else {
                                                                              onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), true);
                                                                          }
                                                                      });
                                                                  } else
                                                                      try {
                                                                          CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                                                                          Toast.makeText(activity, loginError.getMessage(), Toast.LENGTH_LONG).show();
                                                                          if (tvSkip.getVisibility() == VISIBLE) {
                                                                              onClickSubmit.onClickSubmit(false, liRoutePlannerDetails, comments.getText().toString().trim(), false);
                                                                          } else {
                                                                              onClickSubmit.onClickSubmit(false, liRoutePlannerDetails, comments.getText().toString().trim(), false);
                                                                          }
                                                                      } catch (IOException e) {
                                                                          e.printStackTrace();
                                                                      }
                                                              }

                                                              @Override
                                                              public void onFailure(Call<CommonError> call, Throwable t) {
                                                                  showLoading.dismiss();
                                                                  Log.e("FarmsFail", t.getMessage());
                                                                  Toast.makeText(activity, "No internet connection. Please check your connection and retry.", Toast.LENGTH_LONG).show();

                                                                  if (tvSkip.getVisibility() == VISIBLE) {
                                                                      onClickSubmit.onClickSubmit(false, liRoutePlannerDetails, comments.getText().toString().trim(), false);
                                                                  } else {
                                                                      onClickSubmit.onClickSubmit(false, liRoutePlannerDetails, comments.getText().toString().trim(), true);
                                                                  }
                                                              }
                                                          });
                                                      }

//                                                      else{
//                                                          showLoading.dismiss();
//                                                          onClickSubmit.onClickSubmit(true, liRoutePlannerDetails,comments.getText().toString().trim());
//                                                      }

                                                  }
                                                  else {
                                                      Log.d("Analysis__", "Updating temp bags for farmId Line 742" + fId);
                                                      showLoading.show();
                                                      tempBags = new TempBags();
                                                      tempBagsArrayList = new ArrayList<>();
                                                      tempBags.setID(id);
                                                      tempBags.setRouteName(routeName);
                                                      tempBags.setFarmID(Integer.parseInt(fId));
                                                      //tempBags.setRouteId(Integer.parseInt(routeId));
                                                      tempBags.setBeg(Integer.parseInt(semenbags.getText().toString()));
                                                      tempBags.setTemp(temprature.getText().toString());
                                                      tempBagsArrayList.add(0, tempBags);
                                                      if (checkExisting) {
                                                          tempBags.setBeg(Integer.valueOf(semenbags.getText().toString()));
                                                          tempBags.setRefTem(tempratureRef.getText().toString().trim());
                                                      }

                                                      Call<CommonError> call = apiService.updateTempBags(tempBagsArrayList, sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
                                                      call.enqueue(new Callback<CommonError>() {
                                                          @Override
                                                          public void onResponse(Call<CommonError> call, Response<CommonError> response) {
                                                              if (response.code() == 200) {
                                                                  showLoading.dismiss();
                                                                  success("Success", response.body().getData(), () -> {
                                                                      if (tvSkip.getVisibility() == VISIBLE) {
                                                                          onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), false);

                                                                      } else {
                                                                          onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), true);
                                                                      }
                                                                  });
                                                                  if (isFromMap) {

                                                                  } else {
                                                                      try {
                                                                          farmListAdapterByRote.formEntry(farmid);
                                                                          farmListAdapterByRote.notifyDataSetChanged();
                                                                      } catch (Exception e) {
                                                                          e.printStackTrace();
                                                                      }
                                                                  }
                                                              }
                                                              else
                                                                  try {
                                                                      if (tvSkip.getVisibility() == VISIBLE) {
                                                                          // ✅ If photos are pending, don't trigger email here.
                                                                          onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), false);

                                                                      } else {
                                                                          onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), true);

                                                                      }
                                                                      showLoading.dismiss();
                                                                      CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                                                                      // Log.e("FarmsError", loginError.getMessage());
                                                                      Toast.makeText(activity, loginError.getMessage(), Toast.LENGTH_LONG).show();
                                                                  } catch (IOException e) {
                                                                      e.printStackTrace();
                                                                  }
                                                          }

                                                          @Override
                                                          public void onFailure(Call<CommonError> call, Throwable t) {
                                                              showLoading.dismiss();
                                                              Log.e("FarmsFail", t.getMessage());


                                                              if (sharedprefrenceManager.isSyncMode()) {
                                                                  if (tvSkip.getVisibility() == VISIBLE) {
                                                                      // ✅ If photos are pending, don't trigger email here. 
                                                                      // Let onPhotoUpload handle it to prevent duplicates.
                                                                      onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), false);
                                                                  } else {
                                                                      onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(), true);
                                                                  }
                                                              }

                                                          }

                                                      });

//                                                      else {
//                                                          showLoading.dismiss();
//                                                          if(tvSkip.getVisibility()==VISIBLE){
//                                                              onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(),false);
//
//                                                          }else {
//                                                              onClickSubmit.onClickSubmit(true, liRoutePlannerDetails, comments.getText().toString().trim(),true);
//
//                                                          }                                                        }
                                                  }

                                              }
                                              submit.setEnabled(true);

                                          }
                                      }
            );
            cameraLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            // Use the URI you saved before launching camera
                            Uri uri = currentPhotoUri;
                            File file = new File(currentPhotoPath);
                            Utility.saveImageToGallery(requireContext(), file);
                            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

                            if (currentImageIndex == 0) {
                                imgPath1 = currentPhotoPath;
                                imgUri1 = uri;
                                Glide.with(this)
                                        .load(currentPhotoPath) // or new File(currentPhotoPath)
                                        .into(imgOne);

                            } else if (currentImageIndex == 1) {
                                imgPath2 = currentPhotoPath;
                                imgUri2 = uri;
                                Glide.with(this)
                                        .load(currentPhotoPath) // or new File(currentPhotoPath)
                                        .into(imgTwo);
                            } else if (currentImageIndex == 2) {
                                imgPath3 = currentPhotoPath;
                                imgUri3 = uri;
                                Glide.with(this)
                                        .load(currentPhotoPath) // or new File(currentPhotoPath)
                                        .into(imgThree);

                            } else if (currentImageIndex == 3) {
                                imgPath4 = currentPhotoPath;
                                imgUri4 = uri;
                                Glide.with(this)
                                        .load(currentPhotoPath) // or new File(currentPhotoPath)
                                        .into(imgFour);

                            }
                        } else {
                            //Log.d("Analysis__", "Camera capture cancelled or failed");
                        }
                    }
            );
            imgOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentImageIndex = 0;
                    showImagePickerDialog();
                }
            });
            imgTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentImageIndex = 1;
                    showImagePickerDialog();
                }
            });
            imgThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentImageIndex = 2;
                    showImagePickerDialog();
                }
            });
            imgFour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentImageIndex = 3;
                    showImagePickerDialog();
                }
            });
        }
    }

    private void queuePhoto(AppDatabase db, String path, int index) {
        PhotoSyncEntity photo = new PhotoSyncEntity();
        photo.imagePath = path;
        photo.apiType = "FARM_END"; // Assuming these are receipt/delivery photos
        photo.farmId = fId;

        String finalRideId = sharedprefrenceManager.getRideId();
        if (finalRideId == null || finalRideId.equals("0") || finalRideId.isEmpty()) {
            String formattedDate = formatToMMDDYYYY(this.orderDate);
            if (formattedDate == null || formattedDate.isEmpty()) {
                formattedDate = formatToMMDDYYYY(sharedprefrenceManager.getDate());
            }
            ImprovedPriorityFarmData farm = db.improvedPriorityFarmDao().getFarmByIdAndDate(Integer.parseInt(fId), formattedDate);
            if (farm != null && farm.getRideId() != null && !farm.getRideId().equals("0") && !farm.getRideId().isEmpty()) {
                finalRideId = farm.getRideId();
            }
        }

        photo.rideId = finalRideId;
        photo.driverId = sharedprefrenceManager.getDriverID();
        photo.Token = sharedprefrenceManager.getToken();
        photo.imageIndex = index;
        db.photoSyncDao().insert(photo);

        String formattedDate = formatToMMDDYYYY(this.orderDate);
        if (formattedDate == null || formattedDate.isEmpty()) {
            formattedDate = formatToMMDDYYYY(sharedprefrenceManager.getDate());
        }

        ImprovedPriorityFarmData farm = db.improvedPriorityFarmDao().getFarmByIdAndDate(Integer.parseInt(fId), formattedDate);

        if (farm != null) {

            farm.setIsLoaded(1);
            farm.setIsCompleted(1);
            farm.setIsPhotoUploaded(1);
            // update using @Update
            db.improvedPriorityFarmDao().updateImprovedFarm(farm);
            PriorityFarmData farmp = db.priorityFarmDao().getFarmByIdAndDate(Integer.parseInt(fId), formattedDate);

            if (farmp != null) {
                // modify fields
                farmp.setIsPhotoUploaded(1);
                farmp.setIsLoaded(1);
                farmp.setIsCompleted(1);
                // update using @Update
                db.priorityFarmDao().updateFarm(farmp);
            }

        }
    }

    private void uploadPhotos() {
        //Log.d("Analysis__", "Sending images");
        File file1 = null;
        File file2 = null;
        File file3 = null;
        File file4 = null;
        MultipartBody.Part img1 = null;
        MultipartBody.Part img2 = null;
        MultipartBody.Part img3 = null;
        MultipartBody.Part img4 = null;

        if (imgPath1.length() > 2) {
            //Log.d("Analysis__", "Image1 " + imgPath1);
            file1 = new File(imgPath1);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file1 = Utils.compressImageFile(getContext(), file1);
            }
            img1 = prepareFilePart("Image1", file1);
        }

        if (imgPath2.length() > 2) {
            //Log.d("Analysis__", "Image2 " + imgPath2);
            file2 = new File(imgPath2);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file2 = Utils.compressImageFile(getContext(), file2);
            }
            img2 = prepareFilePart("Image2", file2);
        }
        if (imgPath3.length() > 2) {
            //Log.d("Analysis__", "Image2 " + imgPath2);
            file3 = new File(imgPath3);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file3 = Utils.compressImageFile(getContext(), file3);
            }
            img3 = prepareFilePart("Image3", file3);
        }
        if (imgPath4.length() > 2) {
            //Log.d("Analysis__", "Image2 " + imgPath4);
            file4 = new File(imgPath4);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file4 = Utils.compressImageFile(getContext(), file4);
            }
            img4 = prepareFilePart("Image4", file4);
        }
        uploadImages(img1, img2, img3, img4);
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public void FinishDataMail() {
        try {

            JsonObject request = new JsonObject();
            request.addProperty("ParentId", sharedprefrenceManager.getParentId());
            request.addProperty("FIRMID", farmid);
            request.addProperty("rideId", sharedprefrenceManager.getRideId());
            Log.d("LOGIN__", "IN API ROUTE ID IS " + routeId);
            request.addProperty("RouteId", routeId);
            Call<CommonError> call = apiService2.rideFinishEmail(request, sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken());
            call.enqueue(new Callback<CommonError>() {
                @Override
                public void onResponse(Call<CommonError> call, Response<CommonError> response) {

                    if (response.code() == 200) {

                        // finishCompleteRide();

                    } else {
                        try {

                            CommonError loginError = gson.fromJson(
                                    response.errorBody().string(),
                                    CommonError.class
                            );

                            Log.e("Data", loginError.getMessage());

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Data", e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonError> call, Throwable t) {
                    Log.e("errorratro", t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void uploadImages(MultipartBody.Part img1, MultipartBody.Part img2, MultipartBody.Part img3, MultipartBody.Part img4) {
        if (sharedprefrenceManager.isSyncMode()) {
            AppDatabase db = AppDatabase.Companion.getDatabase(activity);
            new Thread(() -> {
                if (imgPath1.length() > 2) queuePhoto(db, imgPath1, 1);
                if (imgPath2.length() > 2) queuePhoto(db, imgPath2, 2);
                if (imgPath3.length() > 2) queuePhoto(db, imgPath3, 3);
                if (imgPath4.length() > 2) queuePhoto(db, imgPath4, 4);

                SyncScheduler.INSTANCE.runImmediateSync(activity);

                activity.runOnUiThread(() -> {
                    if (tvSkip.getVisibility() == VISIBLE) {
                        onClickSubmit.onPhotoUpload(false, true);
                    } else {
                        onClickSubmit.onPhotoUpload(true, false);
                    }
                });
            }).start();
        }
        else {
            Call<ResponseBody> call = apiService.uploadFarmEndImage(sharedprefrenceManager.getToken(), sharedprefrenceManager.getRideId(), img1, img2, img3, img4);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        //  Log.e("ta", response.body());
                        //startActivity(,StartActivity.class);
                        // finishCompleteRide();
                        if (tvSkip.getVisibility() == VISIBLE) {
                            onClickSubmit.onPhotoUpload(false, true);
                        } else {
                            onClickSubmit.onPhotoUpload(true, false);
                        }
                    } else
                        try {

                            CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                            //  errorDialog(loginError.getMessage());
                            Log.e("Data", loginError.getMessage());
                        } catch (Exception e) {

                            e.printStackTrace();
                            Log.e("Data", e.toString());
                        }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("errorratro", t.getMessage());
                    // In Sync mode, queue images for offline upload

                    activity.runOnUiThread(() ->
                            Toast.makeText(activity, "Photo upload failed. Please check your connection.", Toast.LENGTH_SHORT).show()
                    );

                }
            });
        }

    }
}
