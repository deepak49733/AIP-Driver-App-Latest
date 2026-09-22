package com.sc.aipdriver.activities.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;


class MainActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


   /**
    * WebChromeClient subclass handles UI-related calls
    * Note: think chrome as in decoration, not the Chrome browser
    */
   public class GeoWebChromeClient extends WebChromeClient {
       @Override
       public void onGeolocationPermissionsShowPrompt(String origin,
                                                      GeolocationPermissions.Callback callback) {
           // Always grant permission since the app itself requires location
           // permission and the user has therefore already granted it
           System.out.println("eewewuieuieuieuiewiuewuiewuiew");
           callback.invoke(origin, true, false);
       }
   }

   private WebView webView;
   LocationManager locationManager;
   ProgressBar mProgress;
   Location appLocationService;
   String latitude, longitude;
   private static final int REQUEST_LOCATION = 1;
   private ValueCallback<Uri> mUploadMessage;
   public ValueCallback<Uri[]> uploadMessage;
   public static final int REQUEST_SELECT_FILE = 100;
   private final static int FILECHOOSER_RESULTCODE = 1;
   private static final int LOCATION_PERMISSION = 101;
 

   private LocationRequest mLocationRequest;
   private GoogleApiClient mGoogleApiClient;


   @RequiresApi(api = Build.VERSION_CODES.M)
   @SuppressLint("SetJavaScriptEnabled")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If there's no internet, redirect to ImprovedFarmList so user can work offline
        try {
            if (!com.sc.aipdriver.activities.IsInternetAvailableKt.isInternetAvailable(this)) {
                Intent intent = new Intent(this, com.sc.aipdriver.activities.ui.ImprovedFarmList.class);
                startActivity(intent);
                finish();
                return;
            }
        } catch (Exception ex) {
            // If network check fails for any reason, continue to main UI as a fallback
            ex.printStackTrace();
        }

        setContentView(com.sc.aipdriver.R.layout.activity_main);
       webView = findViewById(com.sc.aipdriver.R.id.webView);
       mProgress = findViewById(com.sc.aipdriver.R.id.progres);
       //buildGoogleApiClient();

       //getLocation();
       //check();


//       webView.setInitialScale(1);
//       webView.getSettings().setLoadWithOverviewMode(true);
//       webView.getSettings().setUseWideViewPort(true);
//       webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//       webView.setScrollbarFadingEnabled(false);
//
//       webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//       webView.getSettings().setBuiltInZoomControls(true);
//       webView.setWebViewClient(new GeoWebViewClient());
//       // Below required for geolocation
//       webView.getSettings().setJavaScriptEnabled(true);
//       webView.getSettings().setGeolocationEnabled(true);
//       //webView.setWebChromeClient(new GeoWebChromeClient());
//
//       webView.setWebChromeClient(new WebChromeClient(){
//           public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
//               // callback.invoke(String origin, boolean allow, boolean remember);
//               callback.invoke(origin, true, false);
//           }
//       });
//
//       webView.getSettings().setAppCacheEnabled(true);
//       webView.getSettings().setDatabaseEnabled(true);
//       webView.getSettings().setDomStorageEnabled(true);
//
//       webView.getSettings().setGeolocationDatabasePath("/data/data/testWebClient");
//
//       webView.loadUrl("https://crm.smartcapita.com");



        //checkRuntimePermission();

       // webView.loadUrl("https://justdial.com");

       webView.setWebChromeClient(new WebChromeClient()
       {
           // For 3.0+ Devices (Start)
           // onActivityResult attached before constructor
           protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
           {
               mUploadMessage = uploadMsg;
               Intent i = new Intent(Intent.ACTION_GET_CONTENT);
               i.addCategory(Intent.CATEGORY_OPENABLE);
               i.setType("image/*");
               startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
           }


           // For Lollipop 5.0+ Devices
           public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)
           {
               if (uploadMessage != null) {
                   uploadMessage.onReceiveValue(null);
                   uploadMessage = null;
               }

               uploadMessage = filePathCallback;

               Intent intent = null;
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   intent = fileChooserParams.createIntent();
               }
               try
               {
                   startActivityForResult(intent, REQUEST_SELECT_FILE);
               } catch (ActivityNotFoundException e)
               {
                   uploadMessage = null;
                   return false;
               }
               return true;
           }

           //For Android 4.1 only
           protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
           {
               mUploadMessage = uploadMsg;
               Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
               intent.addCategory(Intent.CATEGORY_OPENABLE);
               intent.setType("image/*");
               startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
           }

           protected void openFileChooser(ValueCallback<Uri> uploadMsg)
           {
               mUploadMessage = uploadMsg;
               Intent i = new Intent(Intent.ACTION_GET_CONTENT);
               i.addCategory(Intent.CATEGORY_OPENABLE);
               i.setType("image/*");
               startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
           }
       });


//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setGeolocationDatabasePath("/data/data/testWebClient");
//
//
//        //webView.setWebChromeClient(new GeoWebChromeClient());
//
//        webView.loadUrl("http://justdial.com");
//

//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setGeolocationEnabled(true);
//        // getLocation();
//        check();
//        webSettings.setGeolocationDatabasePath(getApplicationContext().getFilesDir().getPath());
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.setWebViewClient(new WebViewClient());
//        webView.setWebChromeClient(new GeoWebChromeClient());
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setSupportMultipleWindows(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setDefaultTextEncodingName("utf-8");
//        webView.getSettings().setDomStorageEnabled(true);
//
//       // webSettings.setPluginsEnabled(true);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setPluginState(WebSettings.PluginState.ON);
//        //webView.loadUrl("https://crm.smartcapita.com");
//        webView.loadUrl("https://justdial.com");
//        webView.goBack();
//        checkConnection();
//        webView.setWebChromeClient(new WebChromeClient() {
//            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
//                callback.invoke(origin, true, false);
//            }
//        });
   }


   protected synchronized void buildGoogleApiClient() {
       mGoogleApiClient = new GoogleApiClient.Builder(this)
               .addConnectionCallbacks(this)
               .addOnConnectionFailedListener(this)
               .addApi(LocationServices.API)
               .build();
       mGoogleApiClient.connect();
   }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkRuntimePermission();
    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    public class GeoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // When user clicks a hyperlink, load in the existing WebView
            view.loadUrl(url);
            return true;
        }
    }


    @Override
   public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }


    }

   private void checkRuntimePermission() {
       // Here, thisActivity is the current activity
       if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMISSION);
       } else
           actionAfterPermissionGranted();

   }

   private void actionAfterPermissionGranted() {
       System.err.println("In actionAfterPermissionGranted    " + "  1453 LN");
       mLocationRequest = LocationRequest.create();
       mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       mLocationRequest.setInterval(3000);
       mLocationRequest.setFastestInterval(3000);
       mLocationRequest.setSmallestDisplacement(10);

       LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
       PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
       result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
           @Override
           public void onResult(LocationSettingsResult locationSettingsResult) {

               final Status status = locationSettingsResult.getStatus();

               switch (status.getStatusCode()) {
                   case LocationSettingsStatusCodes.SUCCESS:
                       // All location settings are satisfied. The client can initialize location
                       // requests here.
                       //GetUserLocation();
                       break;
                   case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                       try {
                           // Show the dialog by calling startResolutionForResult(),
                           // and check the result in onActivityResult().
                           status.startResolutionForResult(MainActivity.this, 100);

                       } catch (IntentSender.SendIntentException e) {
                           // Ignore the error.
                       }
                       break;
                   case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                       break;
               }
           }
       });
   }


   @Override
   public void onLocationChanged(@NonNull Location location) {
   //    Toast.makeText(this, "Current Location" + location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_SHORT).show();
       //  Toast.makeText(this, location.getLatitude()+ location.getLongitude(), Toast.LENGTH_SHORT).show();
       //locationText.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());

   }

   @Override
   public void onStatusChanged(String provider, int status, Bundle extras) {

   }

   @Override
   public void onProviderEnabled(@NonNull String provider) {

   }

   @Override
   public void onProviderDisabled(@NonNull String provider) {

   }

   @Override
   public void onPointerCaptureChanged(boolean hasCapture) {

   }

   public class WebViewClient extends android.webkit.WebViewClient {
       @Override
       public void onPageStarted(WebView view, String url, Bitmap favicon) {
           super.onPageStarted(view, url, favicon);
       }

       @Override
       public boolean shouldOverrideUrlLoading(WebView view, String url) {
           view.loadUrl(url);
           return true;
       }

       @Override
       public void onPageFinished(WebView view, String url) {
           super.onPageFinished(view, url);
           mProgress.setVisibility(View.GONE);
       }
   }

   @Override
   public void onBackPressed() {
       super.onBackPressed();
   }


   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
       if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
           this.webView.goBack();
           return true;
       }

       return super.onKeyDown(keyCode, event);

   }

   public void checkConnection() {
       webView.setWebViewClient(new WebViewClient() {
           public void onReceivedError(final WebView webView, int errorCode, String description, String failingUrl) {
               try {
                   webView.stopLoading();
               } catch (Exception e) {
               }

               if (webView.canGoBack()) {
                   webView.goBack();
               }

               webView.loadUrl("about:blank");
               AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
               alertDialog.setTitle("Connection Problem");
               alertDialog.setMessage("Check your internet connection and try again.");
               alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       finish();
                       startActivity(getIntent());

                   }
               });

               alertDialog.show();
               super.onReceivedError(webView, errorCode, description, failingUrl);
           }
       });
   }

   @RequiresApi(api = Build.VERSION_CODES.M)
   void getLocation() {
     /*  try {
           locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
           Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           if (locationGPS != null) {
               double lat = locationGPS.getLatitude();
               double longi = locationGPS.getLongitude();
               latitude = String.valueOf(lat);
               longitude = String.valueOf(longi);
               //showLocation.setText("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
           } else {
               Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
           }

       }
       catch(SecurityException e) {
           e.printStackTrace();
       }*/

       try {
           if (ActivityCompat.checkSelfPermission(
                   MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                   MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
           } else {
               Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               if (locationGPS != null) {
                   double lat = locationGPS.getLatitude();
                   double longi = locationGPS.getLongitude();
                   latitude = String.valueOf(lat);
                   longitude = String.valueOf(longi);
                   //         Toast.makeText(this, "Current Location" + locationGPS.getLatitude() + " " + locationGPS.getLongitude(), Toast.LENGTH_SHORT).show();
                   //    showLocation.setText("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
               } else {
                   Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

  /* public void check(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
           OnGPS();
       } else {
           getLocation();
       }
   }*/

   private void OnGPS() {
       final AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
           }
       }).setNegativeButton("No", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
           }
       });
       final AlertDialog alertDialog = builder.create();
       alertDialog.show();
   }

   @RequiresApi(api = Build.VERSION_CODES.M)
   public void check() {
       locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
           OnGPS();
       } else {
           getLocation();
       }
   }



//    public static class GeoWebChromeClient extends android.webkit.WebChromeClient {
//        @Override
//        public void onGeolocationPermissionsShowPrompt(final String origin,
//                                                       final GeolocationPermissions.Callback callback) {
//            // Always grant permission since the app itself requires location
//            // permission and the user has therefore already granted it
//            callback.invoke(origin, true, false);
//
//
//        }
//    }

}


