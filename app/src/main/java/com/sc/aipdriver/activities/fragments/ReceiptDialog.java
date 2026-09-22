package com.sc.aipdriver.activities.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.models.ReceiptDetails;
import com.sc.aipdriver.activities.models.ReceiptResponse;
import com.sc.aipdriver.activities.otherclasses.Global;
import com.sc.aipdriver.activities.otherclasses.PreferenceUtils;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.sc.aipdriver.activities.otherclasses.Utils;
import com.sc.aipdriver.activities.ui.Utility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Created by dev on 5/12/17.
 */
public class ReceiptDialog extends BottomSheetDialogFragment {

    Button submit;

    EditText fueling;
    private Uri currentPhotoUri;
   private Activity activity;
    private String currentPhotoPath;
    private String imgPath1="";
    private String imgPath2="";
    private String imgPath3 = "";
    private String imgPath4 = "";
    private Uri imgUri1;
    private Uri imgUri2;
    private Uri imgUri3;
    private Uri imgUri4;
    EditText gallons;
    EditText pricegallon;
    EditText totalcost;
    EditText washcost;
    boolean isViewing = false;
    ImageView imgOne, imgTwo, imgThree, imgFour;
    ReceiptResponse receiptDetails=null;

int total=0;
    private ActivityResultLauncher<Intent> cameraLauncher;
    EditText totalmiles;

    EditText endtime,endoil;
    private int currentImageIndex = -1;
    String startingcarodometertext, endingcarodometertext, totalmilestext, endoiltext,endtimetext;
    OnReceiptSubmit onReceiptSubmit;
    OnReceiptUpdate onReceiptUpdate;
    UploadReceiptImages uploadImages;
    ReceiptResponse modell;
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
        View contentView = View.inflate(getContext(), R.layout.receipt_input_dialog, null);
        dialog.setContentView(contentView);
        modell = new ReceiptResponse();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            submit = contentView.findViewById(R.id.SubmitReceipt);
            gallons = contentView.findViewById(R.id.gallons);
            pricegallon = contentView.findViewById(R.id.pricegallon);
            totalcost = contentView.findViewById(R.id.totalcost);
            washcost = contentView.findViewById(R.id.washcost);
            fueling = contentView.findViewById(R.id.fueling);

            imgOne = contentView.findViewById(R.id.iv_img1);
            imgTwo = contentView.findViewById(R.id.iv_img2);
            imgThree = contentView.findViewById(R.id.iv_img3);
            imgFour = contentView.findViewById(R.id.iv_img4);
            if (isViewing){
                gallons.setEnabled(false);
                pricegallon.setEnabled(false);
                totalcost.setEnabled(false);
                washcost.setEnabled(false);
                fueling.setEnabled(false);
                submit.setVisibility(View.GONE);
                gallons.setText(receiptDetails.getGallon());
                pricegallon.setText(receiptDetails.getPricePgallon());
                fueling.setText(receiptDetails.getFuelOdometer());
                washcost.setText(receiptDetails.getWashCost());
                totalcost.setText(receiptDetails.getFuelCost());
                modell = receiptDetails;
                if (receiptDetails.getRecipt_Img()!=null) {
                    if (receiptDetails.getRecipt_Img().length()>4) {
                        Object imageSource = receiptDetails.getRecipt_Img();
                        if (!((String) imageSource).startsWith("http")) {
                            imageSource = new File((String) imageSource);
                        }
                        Glide.with(this)
                                .load(imageSource)
                                .placeholder(R.drawable.ic_doc) // or new File(currentPhotoPath)
                                .into(imgOne);
                    }
                }
            }else{
                gallons.setEnabled(true);
                pricegallon.setEnabled(true);
                totalcost.setEnabled(true);
                washcost.setEnabled(true);
                fueling.setEnabled(true);
                submit.setVisibility(View.VISIBLE);
            }
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submit.setEnabled(false);
                    ReceiptResponse model = new ReceiptResponse();

                    model.setFuelOdometer(fueling.getText().toString().trim());
                    model.setGallon(gallons.getText().toString().trim());
                    model.setPricePgallon(pricegallon.getText().toString().trim());
                    model.setFuelCost(totalcost.getText().toString().trim());
                    model.setWashCost(washcost.getText().toString().trim());
//                    model.setParentId(sharedprefrenceManager.getParentId());
                    onReceiptSubmit.onReceiptSubmit(model);
                    Log.d("Analysis__","Image1 "+imgPath1);
                    if (imgPath1.length()>2){
                        Log.d("Analysis__","Upload "+imgPath1);
                        uploadPhotos(false,model);
                    }
                    dismiss();
                }
            });
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
                            if (submit.getVisibility() == View.GONE){
                                    uploadPhotos(true,modell);
                                    onReceiptUpdate.uploadReceiptUpdate(receiptDetails.getID());

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
            imgThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentImageIndex = 2;
                    openCamera();
                }
            });
            imgFour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentImageIndex = 3;
                    openCamera();
                }
            });
        }
    }

    private void uploadPhotos(boolean isUpdate,ReceiptResponse model) {
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
            file1 = new File(imgPath1);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file1 = Utils.compressImageFile(getContext(), file1);
            }
            img1 = prepareFilePart("Image1", file1);
        }
        if (imgPath2.length() > 2) {
            file2 = new File(imgPath2);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file2 = Utils.compressImageFile(getContext(), file2);
            }
            img2 = prepareFilePart("Image2", file2);
        }
        if (imgPath3.length() > 2) {
            file3 = new File(imgPath3);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file3 = Utils.compressImageFile(getContext(), file3);
            }
            img3 = prepareFilePart("Image3", file3);
        }
        if (imgPath4.length() > 2) {
            file4 = new File(imgPath4);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                file4 = Utils.compressImageFile(getContext(), file4);
            }
            img4 = prepareFilePart("Image4", file4);
        }

        uploadImages.uploadReceiptImages(isUpdate, img1, img2, img3, img4, imgPath1, imgPath2, imgPath3, imgPath4, model);

        imgPath1 = "";
        imgPath2 = "";
        imgPath3 = "";
        imgPath4 = "";
        imgPath2="";
        imgPath3="";
        imgPath4="";

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
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Log.d("Analysis__","Creating File "+imageFileName);
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    public BottomSheetDialogFragment instance(Activity activity, OnReceiptSubmit onReceiptSubmit, Boolean isViewing, ReceiptResponse receiptDetails, UploadReceiptImages uploadImages,OnReceiptUpdate onReceiptUpdate) {
        this.activity = activity;
        this.activity = activity;
        this.onReceiptSubmit = onReceiptSubmit;
        this.isViewing = isViewing;
        this.receiptDetails = receiptDetails;
        this.onReceiptUpdate=onReceiptUpdate;
        this.uploadImages = uploadImages;
        return this;
    }

    public interface OnReceiptSubmit {
        void onReceiptSubmit(ReceiptResponse receiptDetails);
    }

    public interface OnReceiptUpdate {
        void uploadReceiptUpdate(String id);
    }

    public interface UploadReceiptImages {
        void uploadReceiptImages(boolean isUpdate, MultipartBody.Part img1, MultipartBody.Part img2, MultipartBody.Part img3, MultipartBody.Part img4,
                                 String imgPath1, String imgPath2, String imgPath3, String imgPath4,ReceiptResponse receiptResponse);
    }


}

