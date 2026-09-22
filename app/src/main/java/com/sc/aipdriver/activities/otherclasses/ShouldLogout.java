package com.sc.aipdriver.activities.otherclasses;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.fragments.SelectCar;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.PermissionData;
import com.sc.aipdriver.activities.models.PermissionModel;
import com.sc.aipdriver.activities.ui.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShouldLogout {
    ApiInterface apiService;
    private Context context;
    private SharedprefrenceManager sharedprefrenceManager;
    public ShouldLogout(Context context,SharedprefrenceManager sharedprefrenceManager) {
        this.context = context;
        this.sharedprefrenceManager = sharedprefrenceManager;
        apiService = ApiClient.getClient(context).create(ApiInterface.class);

    }

    public void driverLogoutIfRequired(boolean performlogout,Context context) {
        Call<PermissionModel> call = apiService.shouldLogoutt(Integer.parseInt(sharedprefrenceManager.getDriverID()),sharedprefrenceManager.getToken());
        call.enqueue(new Callback<PermissionModel>() {
            @Override
            public void onResponse(Call<PermissionModel> call, Response<PermissionModel> response) {
                if (response.code() == 200) {

                    if (response.body().getData()!=null)
                    {
                        if (performlogout){
                            logout(context);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PermissionModel> call, Throwable t) {
                Log.e("errorratro", t.getMessage());
                System.err.println("In Select Car Show permission Failure");
                if (performlogout){
                    logout(context);
                }
            }
        });
    }
    public boolean shouldLogout(boolean performlogout, Context context){

        Call<PermissionModel> call = apiService.shouldLogout(Integer.parseInt(sharedprefrenceManager.getDriverID()),sharedprefrenceManager.getToken());
        call.enqueue(new Callback<PermissionModel>() {
            @Override
            public void onResponse(Call<PermissionModel> call, Response<PermissionModel> response) {
                if (response.code() == 200) {

                    if (response.body().getData()!=null)
                    {
                        PermissionData pData = response.body().getData();
                        if (performlogout){
                           // logout(context);
                        }

                    }
                }
                driverLogoutIfRequired(performlogout,context);
            }

            @Override
            public void onFailure(Call<PermissionModel> call, Throwable t) {
                Log.e("errorratro", t.getMessage());
                System.err.println("In Select Car Show permission Failure");
                driverLogoutIfRequired(performlogout,context);
            }
        });


        return true;
    }

    public void logout(Context context) {

        sharedprefrenceManager.clearAll();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        finishAffinity((Activity) context); // clears all previous activities
        Toast.makeText(context, R.string.logged_out, Toast.LENGTH_SHORT).show();

//        context.startActivity(new Intent(context, LoginActivity.class));
//        Toast.makeText(context, R.string.logged_out,Toast.LENGTH_SHORT).show();
//        ((Activity)context).finish();
    }
}
