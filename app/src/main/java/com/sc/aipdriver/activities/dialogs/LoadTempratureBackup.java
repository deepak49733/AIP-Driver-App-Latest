//package com.sc.aipdriver.activities.dialogs;
//
//import static android.app.Activity.RESULT_OK;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Environment;
//import android.os.Handler;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.core.content.FileProvider;
//
//import com.google.android.material.textfield.TextInputLayout;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.sc.aipdriver.R;
//import com.sc.aipdriver.activities.adapters.FarmListAdapterByRote;
//import com.sc.aipdriver.activities.interfaces.ApiClient;
//import com.sc.aipdriver.activities.interfaces.ApiInterface;
//import com.sc.aipdriver.activities.interfaces.OnClickSubmit;
//import com.sc.aipdriver.activities.models.BaseResponse;
//import com.sc.aipdriver.activities.models.CommonError;
//import com.sc.aipdriver.activities.models.LiRoutePlannerDetail;
//import com.sc.aipdriver.activities.models.TempBags;
//import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
//import com.sc.aipdriver.activities.ui.MapsActivityNew;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import cn.pedant.SweetAlert.SweetAlertDialog;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//
///**
// * Created by dev on 24/1/18.
// */
//
//public class LoadTempratureBackup {
//    Activity activity;
//    Dialog dialog;
//
//    Button submit;
//
//    EditText temprature;
//    EditText tempratureRef;
//    TextInputLayout til;
//
//    EditText semenbags;
//    LinearLayout uploadLL1,uploadLL2;
//    FarmListAdapterByRote farmListAdapterByRote;
//    MapsActivityNew mapsActivityNew;
//    String farmid;
//    String routeName;
//    private ActivityResultLauncher<Intent> galleryLauncher;
//    private ActivityResultLauncher<Intent> cameraLauncher;
//    String fId="";
//    ShowLoading showLoading;
//    ApiInterface apiService;
//    private int currentImageIndex = -1;
//    Gson gson;
//    TempBags tempBags;
//    SharedprefrenceManager sharedprefrenceManager;
//    int id;
//    boolean bagsTemp = false;
//    boolean isFromMap = false;
//    boolean isNew = false;
//    String routeId="";
//    ArrayList<TempBags> tempBagsArrayList;
//    boolean checkExisting = false;
//    ArrayList<LiRoutePlannerDetail> liRoutePlannerDetails=new ArrayList<>();
//
//    OnClickSubmit onClickSubmit;
//    ImageView[] imageViews;
//    ImageView imgOne, imgTwo, imgThree, imgFour;
//    ArrayList<Uri> imageUris = new ArrayList<>();
//    ArrayList<String> imagePaths = new ArrayList<>();
//    private Uri currentPhotoUri;
//    private String currentPhotoPath;
//    public LoadTempratureBackup(Activity activity, String farmid, String routeName, String routeId, boolean checkExisting, OnClickSubmit onClickSubmit, ActivityResultLauncher<Intent> cameraLauncherr){
//        this.activity=activity;
//        this.farmListAdapterByRote=farmListAdapterByRote;
//        this.farmid=farmid;
//        this.routeId=routeId;
//        this.routeName=routeName;
//        this.checkExisting=checkExisting;
//        this.onClickSubmit=onClickSubmit;
//        this.cameraLauncher = cameraLauncherr;
//        //Log.d("Analysis__","Routename is : "+routeName +"Id is "+routeId +" fid is "+fId);
//        fId=farmid;
//        //Log.d("Analysis__","looooo farmId is "+farmid);
//        if (sharedprefrenceManager==null) {
//            sharedprefrenceManager = new SharedprefrenceManager(activity);
//        }
//        //Log.d("Analysis__","looooo fid is setting in db "+fId);
//        sharedprefrenceManager.setFID(fId);
//        isNew=true;
//    }
//
//    public void createDialog() {
//        showLoading = new ShowLoading(activity);
//        apiService = ApiClient.getClient(this).create(ApiInterface.class);
//        gson = new GsonBuilder().setPrettyPrinting().create();
//        dialog = new Dialog(activity);
//        if (sharedprefrenceManager==null) {
//            sharedprefrenceManager = new SharedprefrenceManager(activity);
//        }
//        dialog.setContentView(R.layout.sumbittemprature_dialog);
//       submit = dialog.findViewById(R.id.submit);
//       til = dialog.findViewById(R.id.tempreftil);
//       tempratureRef = dialog.findViewById(R.id.tempratureRef);
//       temprature = dialog.findViewById(R.id.temprature);
//       semenbags = dialog.findViewById(R.id.semenbags);
//        uploadLL1 = dialog.findViewById(R.id.upload_id_ll);
//        uploadLL2 = dialog.findViewById(R.id.upload_id_ll2);
//
//        imgOne = dialog.findViewById(R.id.iv_img1);
//        imgTwo = dialog.findViewById(R.id.iv_img2);
//        imgThree = dialog.findViewById(R.id.iv_img3);
//        imgFour = dialog.findViewById(R.id.iv_img4);
//        dialog.setCancelable(true);
//        dialog.show();
//        til.setVisibility(View.GONE);
//        if (checkExisting){
//            til.setVisibility(View.VISIBLE);
//            uploadLL1.setVisibility(View.VISIBLE);
//            uploadLL2.setVisibility(View.VISIBLE);
//            getData();
//        }else{
//            uploadLL1.setVisibility(View.GONE);
//            uploadLL2.setVisibility(View.GONE);
//        }
//        submit.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//        if (valid()) {
//            dialog.dismiss();
//
//            if (isNew){
//                LiRoutePlannerDetail liRoutePlannerDetail=new LiRoutePlannerDetail();
//                liRoutePlannerDetail.setFIRMID(farmid);
//                liRoutePlannerDetail.setNumberOfBagsLoaded(semenbags.getText().toString());
//                liRoutePlannerDetail.setTemperatureOfSemenLoaded(temprature.getText().toString());
//                liRoutePlannerDetail.setCustomerSeemanCoolarTemp(tempratureRef.getText().toString());
//                liRoutePlannerDetails.add(liRoutePlannerDetail);
//            }
//            else {
//                farmListAdapterByRote.additem(farmid, semenbags.getText().toString(), temprature.getText().toString());
//            }
//
//            if (bagsTemp == false) {
//                showLoading.show();
//                tempBags = new TempBags();
//                tempBagsArrayList = new ArrayList<>();
//                tempBags.setID(0);
//                tempBags.setRouteName(routeName);
////                if (routeId.length()>0)
////                tempBags.setRouteId(Integer.parseInt(routeId));
//                if (farmid.length()>0)
//                tempBags.setFarmID(Integer.parseInt(fId));
//                tempBags.setBeg(Integer.parseInt(semenbags.getText().toString()));
//                tempBags.setTemp(Integer.parseInt(temprature.getText().toString()));
//                tempBags.setParentId(Integer.valueOf(sharedprefrenceManager.getParentId()));
//                tempBagsArrayList.add(0, tempBags);
//                if (checkExisting){
//                    tempBags.setRefTem(tempratureRef.getText().toString().trim());
//                }
//                Call<CommonError> call = apiService.sendTempBags(tempBagsArrayList);
//                call.enqueue(new Callback<CommonError>() {
//                    @Override
//                    public void onResponse(Call<CommonError> call, Response<CommonError> response) {
//                        if (response.code() == 200) {
//                            showLoading.dismiss();
//                            bagsTemp = true;
//
//                            success("Success", response.body().getData());
//                            onClickSubmit.onClickSubmit(true,liRoutePlannerDetails);
//                        } else
//                            try {
//                                showLoading.dismiss();
//                                CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                                Log.e("FarmsError", loginError.getMessage());
//                                Toast.makeText(activity, loginError.getMessage(), Toast.LENGTH_LONG).show();
//                                onClickSubmit.onClickSubmit(false,liRoutePlannerDetails);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                    }
//
//                    @Override
//                    public void onFailure(Call<CommonError> call, Throwable t) {
//                        showLoading.dismiss();
//                        Log.e("FarmsFail", t.getMessage());
//                        onClickSubmit.onClickSubmit(false,liRoutePlannerDetails);
//                    }
//
//                });
//            }
//            else {
//                showLoading.show();
//                tempBags = new TempBags();
//                tempBagsArrayList = new ArrayList<>();
//                tempBags.setID(id);
//                tempBags.setRouteName(routeName);
//                tempBags.setFarmID(Integer.parseInt(fId));
//                //tempBags.setRouteId(Integer.parseInt(routeId));
//                tempBags.setBeg(Integer.parseInt(semenbags.getText().toString()));
//                tempBags.setTemp(Integer.parseInt(temprature.getText().toString()));
//                tempBagsArrayList.add(0, tempBags);
//                if (checkExisting){
//                    tempBags.setRefTem(tempratureRef.getText().toString().trim());
//                }
//                Call<CommonError> call = apiService.updateTempBags(tempBagsArrayList);
//                call.enqueue(new Callback<CommonError>() {
//                    @Override
//                    public void onResponse(Call<CommonError> call, Response<CommonError> response) {
//                        if (response.code() == 200) {
//                            showLoading.dismiss();
//                            success("Success", response.body().getData());
//                            onClickSubmit.onClickSubmit(true,liRoutePlannerDetails);
//
//                            if (isFromMap){
//
//                            }
//                            else {
//                                try {
//                                    farmListAdapterByRote.formEntry(farmid);
//                                    farmListAdapterByRote.notifyDataSetChanged();
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//                            }
//                        } else
//                            try {
//                                onClickSubmit.onClickSubmit(false,liRoutePlannerDetails);
//                                showLoading.dismiss();
//                                CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                                Log.e("FarmsError", loginError.getMessage());
//                                Toast.makeText(activity, loginError.getMessage(), Toast.LENGTH_LONG).show();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                    }
//
//                    @Override
//                    public void onFailure(Call<CommonError> call, Throwable t) {
//                        showLoading.dismiss();
//                        Log.e("FarmsFail", t.getMessage());
//                        onClickSubmit.onClickSubmit(false,liRoutePlannerDetails);
//                    }
//
//                });
//              }
//
//            }
//          }
//        }
//      );
//    }
//
//
//
//
//    private boolean valid() {
//        if (semenbags.getText().toString().length()==0||temprature.getText().toString().length()==0) {
//            temprature.setError("Onloading Bags");
//            semenbags.setError("Semen Bags");
//            return false;
//        } else
//            return true;
//    }
//
//    private void success(String st1, String st2) {
//        try {
//            SweetAlertDialog sd = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE);
//            sd.setTitleText(st1);
//            sd.setContentText(st2);
//            sd.show();
//
//            // Dismiss after 3 seconds (3000 milliseconds)
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (sd.isShowing()) {
//                        sd.dismissWithAnimation();
//                    }
//                }
//            }, 3000);
//        }catch (Exception e){
//
//        }
//    }
//
//    private void getData(){
//        Call<BaseResponse<List<TempBags>>> call = apiService.getTempBags(routeName, Integer.parseInt(farmid));
//        call.enqueue(new Callback<BaseResponse<List<TempBags>>>() {
//            @Override
//            public void onResponse(Call<BaseResponse<List<TempBags>>> call, Response<BaseResponse<List<TempBags>>> response) {
//                if (response.code() == 200) {
//
//                    if (response.body().getData().size()>0) {
//                        id = response.body().getData().get(0).getID();
//                        semenbags.setText("" + response.body().getData().get(0).getBeg());
//                        temprature.setText("" + response.body().getData().get(0).getTemp());
//                        semenbags.setSelection(semenbags.getText().length());
//                        temprature.setSelection(temprature.getText().length());
//                        bagsTemp = true;
//                    }
//                } else
//                    try {
//                        CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
//                        Log.e("FarmsError", loginError.getMessage());
////                            Toast.makeText(activity, loginError.getMessage(), Toast.LENGTH_LONG).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse<List<TempBags>>> call, Throwable t) {
//                Log.e("FarmsFail", t.getMessage());
//            }
//
//        });
//    }
//    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        //Log.d("Analysis__","Creating File "+imageFileName);
//        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        return File.createTempFile(imageFileName, ".jpg", storageDir);
//    }
//
//    private void openCamera() {
//
//        File photoFile = null;
//        try {
//            photoFile = createImageFile();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        Uri photoUri = FileProvider.getUriForFile(
//                activity,
//                activity.getPackageName() + ".fileprovider",
//                photoFile
//        );
//
//        currentPhotoUri = photoUri; // Save for use after capture
//        currentPhotoPath = photoFile.getAbsolutePath(); // Save path for SharedPreferences
//
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//        cameraLauncher.launch(cameraIntent);
//
//    }
//    private void initLaunchers() {
//        cameraLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == RESULT_OK) {
//                        // Use the URI you saved before launching camera
//                        Uri uri = currentPhotoUri;
//                        File file = new File(currentPhotoPath);
//
//                        imageUris.add(uri);
//                        imagePaths.add(currentPhotoPath);
//
//                        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
//
//                        if (currentImageIndex == 0) {
//                            sharedprefrenceManager.setImage1(currentPhotoPath);
//                            imgOne.setImageBitmap(bitmap);
//                        } else if (currentImageIndex == 1) {
//                            sharedprefrenceManager.setImage2(currentPhotoPath);
//                            imgTwo.setImageBitmap(bitmap);
//                        } else if (currentImageIndex == 2) {
//                            sharedprefrenceManager.setImage3(currentPhotoPath);
//                            imgThree.setImageBitmap(bitmap);
//                        } else if (currentImageIndex == 3) {
//                            sharedprefrenceManager.setImage4(currentPhotoPath);
//                            imgFour.setImageBitmap(bitmap); // You were repeating imgThree
//                        }else if (currentImageIndex == 4) {
//                            sharedprefrenceManager.setImage5(currentPhotoPath);
//                            imgFour.setImageBitmap(bitmap); // You were repeating imgThree
//                        }else if (currentImageIndex == 5) {
//                            sharedprefrenceManager.setImage6(currentPhotoPath);
//                            imgFour.setImageBitmap(bitmap); // You were repeating imgThree
//                        }else if (currentImageIndex == 6) {
//                            sharedprefrenceManager.setImage7(currentPhotoPath);
//                            imgFour.setImageBitmap(bitmap); // You were repeating imgThree
//                        }else if (currentImageIndex == 7) {
//                            sharedprefrenceManager.setImage8(currentPhotoPath);
//                            imgFour.setImageBitmap(bitmap); // You were repeating imgThree
//                        }
//                        sharedprefrenceManager.setIsStart("0");
//                        sharedprefrenceManager.setHasImage("yes");
//                    } else {
//                        //Log.d("Analysis__", "Camera capture cancelled or failed");
//                    }
//                }
//        );*/
//
//    }
//
//}
