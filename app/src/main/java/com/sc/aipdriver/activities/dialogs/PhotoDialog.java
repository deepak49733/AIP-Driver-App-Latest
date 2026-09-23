package com.sc.aipdriver.activities.dialogs;

import static com.sc.aipdriver.activities.IsInternetAvailableKt.isInternetAvailable;
import static com.sc.aipdriver.activities.ui.FarmListRoute.formatToMMDDYYYY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.adapters.FarmListAdapterByRote;
import com.sc.aipdriver.activities.dialogs.ShowLoading;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.interfaces.OnClickSubmit;
import com.sc.aipdriver.activities.interfaces.OnMailDone;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.otherclasses.Utils;
import com.sc.aipdriver.activities.room.AppDatabase;
import com.sc.aipdriver.activities.room.FarmTempBegEntity;
import com.sc.aipdriver.activities.room.PhotoSyncEntity;
import com.sc.aipdriver.activities.room.RideSyncEntity;
import com.sc.aipdriver.activities.ui.ImprovedFarmDetailActivity;
import com.sc.aipdriver.activities.ui.ImprovedFarmList;
import com.sc.aipdriver.activities.ui.MapsActivityNew;
import com.sc.aipdriver.activities.worker.SyncScheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoDialog extends BottomSheetDialogFragment {

    Activity activity;
    Dialog dialog;

    Button submit;
    String imgPath1 = "";
    String imgPath2 = "";
    String imgPath3 = "";
    String imgPath4 = "";
    Uri imgUri1;
    Uri imgUri2;
    Uri imgUri3;
    Uri imgUri4;
    String rideId;

    String fId = "";

    LinearLayout uploadLL1, uploadLL2;
    String farmid;
    String parentId;

    OnMailDone onMailDone;
    String farmName;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;


    ShowLoading showLoading;
    ApiInterface apiService;
    ApiInterface apiService2;
    private int currentImageIndex = -1;
    Gson gson = new GsonBuilder().setLenient().create();

    SharedprefrenceManager sharedprefrenceManager;
    String routeId = "";
    String orderDate = "";

    ImageView imgOne, imgTwo, imgThree, imgFour, ivClose;
    TextView tvTitle;
    private Uri currentPhotoUri;
    private String currentPhotoPath;

    public PhotoDialog instance(Activity activity, String farmid, String rideId, String routeId, String parentId, OnMailDone onMailDone, String farmName, String orderDate) {
        this.activity = activity;
        this.farmid = farmid;
        this.fId = farmid;
        this.rideId = rideId;
        this.routeId = routeId;
        this.parentId = parentId;
        this.onMailDone = onMailDone;
        this.farmName = farmName;
        this.orderDate = orderDate;
        showLoading = new ShowLoading(activity);
        apiService = ApiClient.getClient(activity).create(ApiInterface.class);
        apiService2 = ApiClient.getHttp1Client(activity).create(ApiInterface.class);
        if (sharedprefrenceManager == null) {
            sharedprefrenceManager = new SharedprefrenceManager(activity);
        }
        return this;
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
        View contentView = View.inflate(getContext(), R.layout.photo_dialog, null);
        dialog.setContentView(contentView);
        if (sharedprefrenceManager == null) {
            sharedprefrenceManager = new SharedprefrenceManager(activity);
        }
        submit = contentView.findViewById(R.id.submit);
        tvTitle = contentView.findViewById(R.id.title_load);
        uploadLL1 = contentView.findViewById(R.id.upload_id_ll);
        uploadLL2 = contentView.findViewById(R.id.upload_id_ll2);
        imgOne = contentView.findViewById(R.id.iv_img1);
        imgTwo = contentView.findViewById(R.id.iv_img2);
        imgThree = contentView.findViewById(R.id.iv_img3);
        imgFour = contentView.findViewById(R.id.iv_img4);
        ivClose = contentView.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setEnabled(false);
                if (imgPath1.length() > 2 || imgPath2.length() > 2 || imgPath3.length() > 2 || imgPath4.length() > 2) {
                    uploadPhotos();
                }else{
                    Toast.makeText(requireContext(),"Kindly upload photo or click skip",Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                    return;
                }
                // Don't re-enable here, let the upload finish enable it or dismiss dialog
            }
        }
        );

        tvTitle.setText(farmName);
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

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        File file = getFileFromUri(uri);
                        if (file != null) {
                            String path = file.getAbsolutePath();
                            if (currentImageIndex == 0) {
                                imgPath1 = path;
                                imgUri1 = uri;
                                Glide.with(this).load(file).into(imgOne);
                            } else if (currentImageIndex == 1) {
                                imgPath2 = path;
                                imgUri2 = uri;
                                Glide.with(this).load(file).into(imgTwo);
                            } else if (currentImageIndex == 2) {
                                imgPath3 = path;
                                imgUri3 = uri;
                                Glide.with(this).load(file).into(imgThree);
                            } else if (currentImageIndex == 3) {
                                imgPath4 = path;
                                imgUri4 = uri;
                                Glide.with(this).load(file).into(imgFour);
                            }
                        }
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri uri = currentPhotoUri;
                        String path = currentPhotoPath;
                        if (currentImageIndex == 0) {
                            imgPath1 = path;
                            imgUri1 = uri;
                            Glide.with(this).load(new File(path)).into(imgOne);
                        } else if (currentImageIndex == 1) {
                            imgPath2 = path;
                            imgUri2 = uri;
                            Glide.with(this).load(new File(path)).into(imgTwo);
                        } else if (currentImageIndex == 2) {
                            imgPath3 = path;
                            imgUri3 = uri;
                            Glide.with(this).load(new File(path)).into(imgThree);
                        } else if (currentImageIndex == 3) {
                            imgPath4 = path;
                            imgUri4 = uri;
                            Glide.with(this).load(new File(path)).into(imgFour);
                        }
                    }
                }
        );
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        galleryLauncher.launch(intent);
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Select Image From");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private File getFileFromUri(Uri uri) {
        try {
            File file = new File(requireContext().getCacheDir(), "upload_" + System.currentTimeMillis() + ".jpg");
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
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

    private void uploadPhotos() {
        File file1 = null, file2 = null, file3 = null, file4 = null;
        MultipartBody.Part img1 = null, img2 = null, img3 = null, img4 = null;

        if (imgPath1.length() > 2) {
            file1 = new File(imgPath1);
            file1 = Utils.compressImageFile(getContext(), file1);
            img1 = prepareFilePart("Image1", file1);
        }
        if (imgPath2.length() > 2) {
            file2 = new File(imgPath2);
            file2 = Utils.compressImageFile(getContext(), file2);
            img2 = prepareFilePart("Image2", file2);
        }
        if (imgPath3.length() > 2) {
            file3 = new File(imgPath3);
            file3 = Utils.compressImageFile(getContext(), file3);
            img3 = prepareFilePart("Image3", file3);
        }
        if (imgPath4.length() > 2) {
            file4 = new File(imgPath4);
            file4 = Utils.compressImageFile(getContext(), file4);
            img4 = prepareFilePart("Image4", file4);
        }
        uploadImages(img1, img2, img3, img4);
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
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
                
                updateFarmPhotoState(() -> {
                    activity.runOnUiThread(() -> {
                        // Re-trigger email queueing so SyncWorker sends an updated email after photos
                        String finalRideId = rideId;
                        if (finalRideId == null || finalRideId.equals("0") || finalRideId.isEmpty()) {
                            finalRideId = parentId;
                        }
                        if (finalRideId == null || finalRideId.equals("0") || finalRideId.isEmpty()) {
                            finalRideId = sharedprefrenceManager.getRideId();
                        }

                        if (activity instanceof ImprovedFarmList) {
                            ((ImprovedFarmList) activity).FinishDataMail(farmid, routeId, finalRideId, true);
                        } else if (activity instanceof com.sc.aipdriver.activities.ui.FarmListRoute) {
                            ((com.sc.aipdriver.activities.ui.FarmListRoute) activity).FinishDataMail(farmid, routeId, finalRideId);
                        } else if (activity instanceof OnClickSubmit) {
                            ((OnClickSubmit) activity).onPhotoUpload(true, true);
                        }
                        if (onMailDone != null) onMailDone.onMailDone(true);
                        dismiss();
                    });
                });
            }).start();
        } else {
            if (isInternetAvailable(activity)) {
                showLoading.show();
                // Ensure ShowLoading is not cancelable during upload
                // (Assuming ShowLoading implementation allows setting it, if not we ignore)

                new Thread(() -> {
                    // Fetch accurate rideId from DB if possible
                    String actualRideId = rideId;
                    AppDatabase db = AppDatabase.Companion.getDatabase(activity);
                    String date = formatToMMDDYYYY(orderDate);
                    if (date == null || date.isEmpty()) date = formatToMMDDYYYY(sharedprefrenceManager.getDate());
                    
                    ImprovedPriorityFarmData farm = db.improvedPriorityFarmDao().getFarmByIdAndDate(Integer.parseInt(fId), date);
                    if (farm != null && farm.getRideId() != null && !farm.getRideId().equals("0") && !farm.getRideId().isEmpty()) {
                        actualRideId = farm.getRideId();
                        Log.d("PhotoDialog", "Found accurate rideId in DB: " + actualRideId);
                    }

                    if (actualRideId == null || actualRideId.equals("0") || actualRideId.isEmpty()) {
                        actualRideId = parentId;
                    }
                    if (actualRideId == null || actualRideId.equals("0") || actualRideId.isEmpty()) {
                        actualRideId = sharedprefrenceManager.getRideId();
                    }

                    final String finalRideIdToUse = actualRideId;
                    Log.d("PhotoDialog", "Uploading to rideId: " + finalRideIdToUse);

                    activity.runOnUiThread(() -> {
                        Call<ResponseBody> call = apiService.uploadFarmEndImage(sharedprefrenceManager.getToken(), finalRideIdToUse, img1, img2, img3, img4);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                showLoading.dismiss();
                                if (response.code() == 200) {
                                    Log.d("PhotoDialog", "Upload successful");
                                    updateFarmPhotoState(() -> {
                                        if (activity instanceof ImprovedFarmList) {
                                            ((ImprovedFarmList) activity).FinishDataMail(farmid, routeId, finalRideIdToUse, true);
                                        } else {
                                            FinishDataMail();
                                        }
                                        if (onMailDone != null) onMailDone.onMailDone(true);
                                        dismiss();
                                    });
                                } else {
                                    submit.setEnabled(true);
                                    Toast.makeText(activity, "Upload failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                showLoading.dismiss();
                                submit.setEnabled(true);
                                Log.e("PhotoDialog", "Upload failure: " + t.getMessage());
                                Toast.makeText(activity, "Upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }).start();
            } else {
                submit.setEnabled(true);
                Toast.makeText(requireContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateFarmPhotoState(Runnable onComplete) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.Companion.getDatabase(activity);
            String date = formatToMMDDYYYY(orderDate);
            if (date == null || date.isEmpty()) date = formatToMMDDYYYY(sharedprefrenceManager.getDate());
            int farmId = Integer.parseInt(fId);

            ImprovedPriorityFarmData farm = db.improvedPriorityFarmDao().getFarmByIdAndDate(farmId, date);
            if (farm != null) {
                farm.setIsPhotoUploaded(1);
                db.improvedPriorityFarmDao().updateImprovedFarm(farm);
            }
            PriorityFarmData farmp = db.priorityFarmDao().getFarmByIdAndDate(farmId, date);
            if (farmp != null) {
                farmp.setIsPhotoUploaded(1);
                db.priorityFarmDao().updateFarm(farmp);
            }
            if (onComplete != null) new Handler(Looper.getMainLooper()).post(onComplete);
        }).start();
    }

    private void queuePhoto(AppDatabase db, String path, int index) {
        PhotoSyncEntity photo = new PhotoSyncEntity();
        photo.imagePath = path;
        photo.apiType = "FARM_END";
        
        // Ensure we use the best possible rideId
        String finalRideId = rideId;
        if (finalRideId == null || finalRideId.equals("0") || finalRideId.isEmpty()) {
            finalRideId = sharedprefrenceManager.getRideId();
        }
        if (finalRideId == null || finalRideId.equals("0") || finalRideId.isEmpty()) {
            String date = formatToMMDDYYYY(orderDate);
            if (date == null || date.isEmpty()) date = formatToMMDDYYYY(sharedprefrenceManager.getDate());
            ImprovedPriorityFarmData farm = db.improvedPriorityFarmDao().getFarmByIdAndDate(Integer.parseInt(fId), date);
            if (farm != null && farm.getRideId() != null && !farm.getRideId().equals("0") && !farm.getRideId().isEmpty()) {
                finalRideId = farm.getRideId();
            }
        }
        if (finalRideId == null || finalRideId.equals("0") || finalRideId.isEmpty()) {
            finalRideId = parentId;
        }
        
        photo.rideId = finalRideId;
        photo.farmId = fId; // Very important for SyncWorker mapping
        photo.driverId = sharedprefrenceManager.getDriverID();
        photo.Token = sharedprefrenceManager.getToken();
        photo.imageIndex = index;
        db.photoSyncDao().insert(photo);
    }

    public void FinishDataMail() {
        try {
            String finalRideId = (rideId == null || rideId.equals("0")) ? parentId : rideId;
            if (finalRideId == null || finalRideId.equals("0")) finalRideId = sharedprefrenceManager.getRideId();

            JsonObject request = new JsonObject();
            request.addProperty("ParentId", parentId);
            request.addProperty("FIRMID", farmid);
            request.addProperty("rideId", finalRideId);
            request.addProperty("RouteId", routeId);
            
            apiService2.rideFinishEmail(request, sharedprefrenceManager.getDriverID(), sharedprefrenceManager.getToken()).enqueue(new Callback<CommonError>() {
                @Override public void onResponse(Call<CommonError> call, Response<CommonError> response) {}
                @Override public void onFailure(Call<CommonError> call, Throwable t) {}
            });
        } catch (Exception e) { e.printStackTrace(); }
    }
}
