package com.sc.aipdriver.activities.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.IsInternetAvailableKt;
import com.sc.aipdriver.activities.dialogs.ShowLoading;
import com.sc.aipdriver.activities.fragments.AllNotification;
import com.sc.aipdriver.activities.fragments.PermissionFragment;
import com.sc.aipdriver.activities.fragments.SelectCar;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.otherclasses.Global;
import com.sc.aipdriver.activities.otherclasses.PreferenceUtils;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public class StartActivity extends AppCompatActivity implements PermissionFragment.GrantPermission {

    Button start;

    EditText beginingtime;

    EditText begningcarodometer;
    EditText begningoil;
    public static final int REQUEST_GALLERY = 100;
    public static final int REQUEST_CAMERA = 101;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    String beginingtimetext, begningcarodometertext,begningoiltext, endingcarodometertext, totalmilestext, endtimetext;
    SharedprefrenceManager sharedprefrenceManager;
    private int currentImageIndex = -1;
    ApiInterface apiService;
    Gson gson;
    ShowLoading showLoading;
    ImageView imgOne, imgTwo, imgThree, imgFour, imgFive, imgSix, imgSeven, imgEight;
    ArrayList<String> imagePaths = new ArrayList<>();
    Uri cameraImageUri;
    File photoFile;
    ImageView[] imageViews;
    ArrayList<Uri> imageUris = new ArrayList<>();
    private Uri currentPhotoUri;
    private String currentPhotoPath;
    private boolean checkImages;
    private String imgPath1="";
    private String imgPath2="";
    private String imgPath3="";
    private String imgPath4="";
    private String imgPath5="";
    private String imgPath6="";
    private String imgPath7="";
    private String imgPath8="";
    private Uri imgUri1;
    private Uri imgUri2;
    private Uri imgUri3;
    private Uri imgUri4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        start = findViewById(R.id.start);
        imgOne = findViewById(R.id.iv_img1);
        imgTwo = findViewById(R.id.iv_img2);
        imgThree = findViewById(R.id.iv_img3);
        imgFour = findViewById(R.id.iv_img4);
        imgFive = findViewById(R.id.iv_img5);
        imgSix = findViewById(R.id.iv_img6);
        imgSeven = findViewById(R.id.iv_img7);
        imgEight = findViewById(R.id.iv_img8);
        imageUris = new ArrayList<>(Arrays.asList(null, null, null, null));
        imagePaths = new ArrayList<>(Arrays.asList("", "", "", ""));
        beginingtime = findViewById(R.id.begningtime);
        begningcarodometer = findViewById(R.id.begningcarodometer);
        begningoil = findViewById(R.id.begningoil);
        sharedprefrenceManager = new SharedprefrenceManager(this);
        apiService = ApiClient.getClient(this).create(ApiInterface.class);
        gson = new GsonBuilder().setPrettyPrinting().create();
        showLoading = new ShowLoading(this);
        imgOne.setOnClickListener(v -> {
            currentImageIndex = 0;
            openCamera(); // or show dialog to choose
        });

        imgTwo.setOnClickListener(v -> {
            currentImageIndex = 1;
            openCamera();
        });

        imgThree.setOnClickListener(v -> {
            currentImageIndex = 2;
            openCamera();
        });

        imgFour.setOnClickListener(v -> {
            currentImageIndex = 3;
            openCamera();
        });
        imgFive.setOnClickListener(v -> {
            currentImageIndex = 4;
            openCamera();
        });
        imgSix.setOnClickListener(v -> {
            currentImageIndex = 5;
            openCamera();
        });
        imgSeven.setOnClickListener(v -> {
            currentImageIndex = 6;
            openCamera();
        });
        imgEight.setOnClickListener(v -> {
            currentImageIndex = 7;
            openCamera();
        });

        imageViews = new ImageView[]{
                imgOne,
                imgTwo,
                imgThree,
                imgFour,
                imgFive,
                imgSix,
                imgSeven,
                imgEight
        };
        // Initialize launchers
        initLaunchers();
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
        Date date = new Date();
        beginingtime.setText(sdf.format(date));

        start.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if (!IsInternetAvailableKt.isInternetAvailable(StartActivity.this)) {
                   checkInternet();
                }
                else{
                    start.setEnabled(false);
                    if (checkImages()){
                        Toast.makeText(StartActivity.this,"Please upload all the photos",Toast.LENGTH_SHORT).show();
                        start.setEnabled(true);
                    }else  {
                        if (valid()) {
                            sharedprefrenceManager.setStartOdoMeter(begningcarodometertext);
                            sharedprefrenceManager.setBegningOil(begningoiltext);
                            // Toast.makeText(StartActivity.this, begningcarodometertext, Toast.LENGTH_SHORT).show();
//                    showLoading.show();
                            Intent intent = new Intent(StartActivity.this, SelectRoute.class);
                            intent.putExtra("StatusID", "1");
                            startActivity(intent);
                            System.err.println("In Start Activity  " + sharedprefrenceManager.getDriverID() + "  " + sharedprefrenceManager.getVehicleId());

                        }
                        start.setEnabled(true);
                    }
                }


            }
        });
    }

    private void checkInternet() {
        runOnUiThread(() -> {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No Internet Connection")
                    .setContentText("No offline data available. Please connect to the internet to continue.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        if (IsInternetAvailableKt.isInternetAvailable(this)) {
                            recreate();
                        } else {
                            checkInternet();
                        }
                    })
                    .show();
        });
    }
    private boolean checkImages() {
            //Log.d("Analysis__","imageuri0 is "+imageUris.get(0));
            //Log.d("Analysis__","imageuri1 is "+imageUris.get(1));
            //Log.d("Analysis__","imageuri2 is "+imageUris.get(2));
            //Log.d("Analysis__","imageuri3 is "+imageUris.get(3));
        if(imageUris.get(0)==null||imageUris.get(1)==null||imageUris.get(2)==null||imageUris.get(3)==null){
            return false;
        }else{
            return true;
        }
    }

    private void initLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        handleSelectedImage(selectedImageUri);
                    }
                });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Use the URI you saved before launching camera
                        Uri uri = currentPhotoUri;
                        File file = new File(currentPhotoPath);
                        Utility.saveImageToGallery(this, file);

                        imageUris.add(uri);
                        imagePaths.add(currentPhotoPath);

                        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

                        if (currentImageIndex == 0) {
                            sharedprefrenceManager.setImage1(currentPhotoPath);
                            Glide.with(this)
                                    .load(currentPhotoPath) // or new File(currentPhotoPath)
                                    .into(imgOne);


                        } else if (currentImageIndex == 1) {
                            sharedprefrenceManager.setImage2(currentPhotoPath);
                            Glide.with(this)
                                    .load(currentPhotoPath) // or new File(currentPhotoPath)
                                    .into(imgTwo);
                        } else if (currentImageIndex == 2) {
                            sharedprefrenceManager.setImage3(currentPhotoPath);
                            Glide.with(this)
                                    .load(currentPhotoPath) // or new File(currentPhotoPath)
                                    .into(imgThree);
                        } else if (currentImageIndex == 3) {
                            sharedprefrenceManager.setImage4(currentPhotoPath);
                            Glide.with(this)
                                    .load(currentPhotoPath) // or new File(currentPhotoPath)
                                    .into(imgFour); // You were repeating imgThree
                        }else if (currentImageIndex == 4) {
                            sharedprefrenceManager.setImage5(currentPhotoPath);
                            Glide.with(this)
                                    .load(currentPhotoPath) // or new File(currentPhotoPath)
                                    .into(imgFive); // You were repeating imgThree
                        }else if (currentImageIndex == 5) {
                            sharedprefrenceManager.setImage6(currentPhotoPath);
                            Glide.with(this)
                                    .load(currentPhotoPath) // or new File(currentPhotoPath)
                                    .into(imgSix); // You were repeating imgThree
                        }else if (currentImageIndex == 6) {
                            sharedprefrenceManager.setImage7(currentPhotoPath);
                            Glide.with(this)
                                    .load(currentPhotoPath) // or new File(currentPhotoPath)
                                    .into(imgSeven); // You were repeating imgThree
                        }else if (currentImageIndex == 7) {
                            sharedprefrenceManager.setImage8(currentPhotoPath);
                            Glide.with(this)
                                    .load(currentPhotoPath) // or new File(currentPhotoPath)
                                    .into(imgEight); // You were repeating imgThree
                        }
                        sharedprefrenceManager.setIsStart("0");
                        sharedprefrenceManager.setHasImage("yes");
                    } else {
                        //Log.d("Analysis__", "Camera capture cancelled or failed");
                    }
                }
        );

    }

    private void openCamera() {

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Uri photoUri = FileProvider.getUriForFile(
                this,
                this.getPackageName() + ".fileprovider",
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
/*
    private void handleSelectedImage(Uri uri) {
        String path = getRealPathFromURI(uri);
        if (path != null) {
            imagePaths.add(path);
            Toast.makeText(this, "Image " + imagePaths.size() + " selected", Toast.LENGTH_SHORT).show();

            if (imagePaths.size() == 4) {
                savePathsToPrefs(imagePaths);
                Toast.makeText(this, "4 images saved", Toast.LENGTH_LONG).show();
                // Optionally start next activity here
            }
        }
    }
*/
private void handleSelectedImage(Uri uri) {
    String path = getRealPathFromURI(uri);
    if (path != null && imageUris.size() < 4) {
        imageUris.add(uri);
        imagePaths.add(path);

        // Set image to corresponding ImageView
      //  imageViews[imageUris.size() - 1].setImageURI(uri);

        if (imagePaths.size() == 4) {
            //savePathsToPrefs(imagePaths);
            Toast.makeText(this, "4 images selected and saved", Toast.LENGTH_SHORT).show();
        }
    }
}
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Log.d("Analysis__","Creating File "+imageFileName);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void savePathsToPrefs(List<String> paths) {

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < paths.size(); i++) {
            editor.putString("img" + (i + 1), paths.get(i));
        }
        editor.putString("isStart", "0");
        editor.apply();
    }

    void FromGallery() {
        // Gallery Intent
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri imageUri = null;

            if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
            } else if (requestCode == REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUri = saveImageAndGetUri(photo); // save bitmap to file and get URI
            }

            if (imageUri != null) {
                File file = new File(getRealPathFromURI(imageUri));
                uploadImage(file);
            }
        }
    }*/
/*    private void uploadImage(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/*"), file1);
        MultipartBody.Part img1 = MultipartBody.Part.createFormData("img1", file1.getName(), requestFile1);

        RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/*"), file2);
        MultipartBody.Part img2 = MultipartBody.Part.createFormData("img2", file2.getName(), requestFile2);

        RequestBody requestFile3 = RequestBody.create(MediaType.parse("image/*"), file3);
        MultipartBody.Part img3 = MultipartBody.Part.createFormData("img3", file3.getName(), requestFile3);

        RequestBody requestFile4 = RequestBody.create(MediaType.parse("image/*"), file4);
        MultipartBody.Part img4 = MultipartBody.Part.createFormData("img4", file4.getName(), requestFile4);

        RequestBody isStart = RequestBody.create(MediaType.parse("text/plain"), isStartValue);



        ApiInterface service = ApiClient.getClient(this).create(ApiInterface.class);
        Call<ResponseBody> call = service.uploadImage(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(StartActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StartActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(StartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    // Helper to save bitmap
    private Uri saveImageAndGetUri(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }
    // Helper to convert URI to file path
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }



    @Override
    public void isGrant(boolean isPermission, boolean isCancel) {

    }


    public interface FilePath {
        void selectedPath(Uri uri);
    }


    private boolean valid() {
        boolean check = true;
        beginingtimetext = beginingtime.getText().toString();
        begningcarodometertext = begningcarodometer.getText().toString();
        if (beginingtimetext.length() == 0) {
            begningoiltext="0";
        }else{
            begningoiltext = begningoil.getText().toString();
        }
        //sharedprefrenceManager.setStartOdoMeter(begningcarodometertext);
//        endingcarodometertext = endingcarodometer.getText().toString();
//        totalmilestext = totalmiles.getText().toString();

        if (beginingtimetext.length() == 0) {
            check = false;
            beginingtime.setError("Enter Beginning time");
        } else if (begningcarodometertext.length() == 0) {
            check = false;
            begningcarodometer.setError("Enter Beginning Odometer");
            Toast.makeText(StartActivity.this, "Enter Beginning Odometer", Toast.LENGTH_SHORT).show();
        }
//        else if (begningoiltext.length() == 0) {
//            check = false;
//            begningoil.setError("Enter Beginning Oil %");
//            Toast.makeText(StartActivity.this, "Enter Beginning Oil %", Toast.LENGTH_SHORT).show();
//        }
        else {
            sharedprefrenceManager.setStartOdoMeter(begningcarodometertext);
            sharedprefrenceManager.setBegningOil(begningoiltext);
        }
        PreferenceUtils.put(this, Global.CARODOMETER, begningcarodometertext);
        System.err.println("getCarmeter  " + sharedprefrenceManager.getCarodometer());
        // Toast.makeText(StartActivity.this, begningcarodometertext, Toast.LENGTH_SHORT).show();
//


        return check;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, AllNotification.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
