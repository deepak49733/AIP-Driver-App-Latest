package com.sc.aipdriver.activities.services;

import static com.sc.aipdriver.activities.otherclasses.App.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.DriverLoginStatusResponse;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginUpdateService extends Service {

    private SharedprefrenceManager sharedprefrenceManager;
    private long timeRemaining = 10000; // 10 seconds
    private boolean isCounterCanceled = false;
    private ApiInterface apiService;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create notification channel for Android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Login Update Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        sharedprefrenceManager = new SharedprefrenceManager(this);
        apiService = ApiClient.getClient(this).create(ApiInterface.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check for location permissions if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("LoginUpdateService", "Location permission not granted");
            stopSelf(); // Stop the service if permission is not granted
            return START_NOT_STICKY;
        }

        // Create and start the foreground service notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Driver APP")
                .setContentText("Running")
                .setSmallIcon(R.drawable.ic_arrowright)
                .build();

        startForeground(1, notification);
        startTimer();
        return START_STICKY; // Use START_STICKY to keep the service running
    }

    private void startTimer() {
        final long[] seconds = {0};

        long millisInFuture = timeRemaining; // 10 seconds
        long countDownInterval = 1000; // 1 second

        new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                if (isCounterCanceled) {
                    cancel();
                } else {
                    seconds[0] = millisUntilFinished / 1000;
                    timeRemaining = millisUntilFinished;
                    sharedprefrenceManager.setTimeRemaining(timeRemaining + "");
                    Log.d("LoginUpdateService", "Time remaining: " + seconds[0]);
                }
            }

            public void onFinish() {
                Log.d("LoginUpdateService", "Timer finished, checking login status");
                timeRemaining = 10000; // Reset timer
                checkLoginStatus();
            }
        }.start();
    }

    private void checkLoginStatus() {
        Call<DriverLoginStatusResponse> call = apiService.getDriverLoginStatus("", "");
        call.enqueue(new Callback<DriverLoginStatusResponse>() {
            @Override
            public void onResponse(Call<DriverLoginStatusResponse> call, Response<DriverLoginStatusResponse> response) {
                if (response.code() == 200) {
                    Log.d("LoginUpdateService", "Login status checked successfully: " + response.body().getData());
                } else {
                    Log.e("LoginUpdateService", "Error checking login status: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DriverLoginStatusResponse> call, Throwable t) {
                Log.e("LoginUpdateService", "Error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}