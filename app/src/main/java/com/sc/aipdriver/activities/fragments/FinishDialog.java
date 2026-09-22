package com.sc.aipdriver.activities.fragments;

import static android.app.Activity.RESULT_OK;

import static com.sc.aipdriver.activities.IsInternetAvailableKt.isInternetAvailable;

import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.otherclasses.Global;
import com.sc.aipdriver.activities.otherclasses.PreferenceUtils;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.otherclasses.Utils;
import com.sc.aipdriver.activities.room.AppDatabase;
import com.sc.aipdriver.activities.room.PhotoSyncEntity;
import com.sc.aipdriver.activities.ui.Utility;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by dev on 5/12/17.
 */
public class FinishDialog extends BottomSheetDialogFragment {

    Button finish;

    EditText endingcarodometer;
    private Uri currentPhotoUri;
    private String currentPhotoPath;
    private String imgPath1="";
    private String imgPath2="";
    private Uri imgUri1;
    private Uri imgUri2;
    EditText startingcarodometer;
int total=0;
    private ActivityResultLauncher<Intent> cameraLauncher;
    EditText totalmiles;
    ImageView imgOne, imgTwo;
    EditText endtime,endoil;
    private int currentImageIndex = -1;
    String startingcarodometertext, endingcarodometertext, totalmilestext, endoiltext,endtimetext;
    SendData sendData;
    UploadImages uploadImages;
    String priority;
    SharedprefrenceManager sharedprefrenceManager;

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
        View contentView = View.inflate(getContext(), R.layout.finishdialog, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            finish = contentView.findViewById(R.id.SubmitFinish);
            endingcarodometer = contentView.findViewById(R.id.endingcarodometer);
            endoil = contentView.findViewById(R.id.endingoil);
            startingcarodometer = contentView.findViewById(R.id.startingcarodometer);
            totalmiles = contentView.findViewById(R.id.miles);
            endtime = contentView.findViewById(R.id.endtime);
            imgOne =contentView.findViewById(R.id.iv_img1);
            imgTwo = contentView.findViewById(R.id.iv_img2);
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
                                imgUri1=uri;
                                Glide.with(this)
                                        .load(currentPhotoPath) // or new File(currentPhotoPath)
                                        .into(imgOne);

                            } else if (currentImageIndex == 1) {
                                imgPath2 = currentPhotoPath;
                                imgUri2=uri;
                                Glide.with(this)
                                        .load(currentPhotoPath) // or new File(currentPhotoPath)
                                        .into(imgTwo);

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
                    openCamera();
                }
            });
            imgTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentImageIndex = 1;
                    openCamera();
                }
            });
            endingcarodometer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (endingcarodometer.getText().toString().trim().length()>0 && startingcarodometer.getText().toString().trim().length()>0) {
                        total = Integer.parseInt(endingcarodometer.getText().toString()) - Integer.parseInt(startingcarodometer.getText().toString());
                        totalmiles.setText(total + "");
                    }
                }
            });
            try {
                DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
                Date date = new Date();
                endtime.setText(sdf.format(date));
                System.err.println("time " + date);
//                PreferenceUtils.getString(getContext(), Global.CARODOMETER);
//                startingcarodometertext = sharedprefrenceManager.getCarodometer();
                if (sharedprefrenceManager == null) {
                    sharedprefrenceManager = new SharedprefrenceManager(requireActivity());
                }
                startingcarodometer.setText(sharedprefrenceManager.getCarodometer());
                System.err.println("carodomerter " + sharedprefrenceManager.getCarodometer());
                // Toast.makeText(getContext(), startingcarodometertext, Toast.LENGTH_SHORT).show();
                // sharedPreferences.getRideId();
                finish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (valid()) {
                            if (imgPath1.length()>2||imgPath2.length()>2){
                                uploadPhotos();

                                if (sharedprefrenceManager.isSyncMode()) {
                                    new Thread(() -> {
                                        AppDatabase db = AppDatabase.Companion.getDatabase(requireContext());
                                        if (imgPath1.length() > 2) queuePhoto(db, imgPath1, 1);
                                        if (imgPath2.length() > 2) queuePhoto(db, imgPath2, 2);
                                    }).start();
                                }
                            }
                            sendData.finishData(endingcarodometer.getText().toString(), totalmiles.getText().toString(), endoil.getText().toString());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void queuePhoto(AppDatabase db, String path, int index) {
        PhotoSyncEntity photo = new PhotoSyncEntity();
        photo.imagePath = path;
        photo.apiType = "FARM_END";
        photo.rideId = sharedprefrenceManager.getRideId();
        photo.driverId = sharedprefrenceManager.getDriverID();
        photo.Token = sharedprefrenceManager.getToken();
        photo.imageIndex = index;
        db.photoSyncDao().insert(photo);
    }

    private void uploadPhotos() {
        //Log.d("Analysis__", "Sending images");
        File file1 = null;
        File file2 = null;
        MultipartBody.Part img1 = null;
        MultipartBody.Part img2 = null;

        if (imgPath1.length()>2) {
            //Log.d("Analysis__","Image1 "+imgPath1);
            file1 = new File(imgPath1);
            //  to WebP under 100 KB
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file1  = Utils.compressImageFile(getContext(),file1);
            }else{
                file1 = new File(imgPath1);
            }

            img1 = prepareFilePart("Image1", file1);
        }

        if (imgPath2.length()>2){
            file2 = new File(imgPath2);
            //Log.d("Analysis__","Image2 "+imgPath2);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file2  = Utils.compressImageFile(getContext(),file2);
            }
            img2 = prepareFilePart("Image2", file2);
        }
            if (isInternetAvailable(requireContext())) {
                uploadImages.uploadImages(img1, img2);
            }

    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
    private void openCamera() {

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Uri photoUri = FileProvider.getUriForFile(
                getContext(),
                getActivity().getPackageName() + ".fileprovider",
                photoFile
        );

        currentPhotoUri = photoUri; // Save for use after capture
        currentPhotoPath = photoFile.getAbsolutePath(); // Save path for SharedPreferences

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraLauncher.launch(cameraIntent);



      /*  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            //Log.d("Analysis__","exceptio is "+e.getMessage());
            e.printStackTrace();
        }*/
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Log.d("Analysis__","Creating File "+imageFileName);
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    private boolean valid() {
        boolean check = false;
        endingcarodometertext = endingcarodometer.getText().toString();
        totalmilestext = totalmiles.getText().toString();
        endtimetext = endtime.getText().toString();

        if (endingcarodometer.length() == 0)
            endingcarodometer.setError("Enter ending car Odometer");
        else if (totalmilestext.length() == 0)
            totalmiles.setError("Enter Total Miles");
        else if (endtimetext.length() == 0)
            endtime.setError("End Time Empty");
        else
            check = true;

        return check;
    }

    public BottomSheetDialogFragment instance(String snippet, SendData sendData, UploadImages uploadImages) {
        this.priority = snippet;
        this.sendData = sendData;
        this.uploadImages = uploadImages;
        return this;
    }

    public interface SendData {
        void finishData(String endodometer, String totalmiles, String endoil);
    }

    public interface UploadImages {
        void uploadImages(MultipartBody.Part img1, MultipartBody.Part img2);
    }


}

