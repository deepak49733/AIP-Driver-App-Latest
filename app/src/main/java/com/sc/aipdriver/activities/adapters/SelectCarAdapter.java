package com.sc.aipdriver.activities.adapters;

import android.app.Activity;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.models.DriverPermissionModel;
import com.sc.aipdriver.activities.ui.StartActivity;
import com.sc.aipdriver.activities.dialogs.ShowLoading;
import com.sc.aipdriver.activities.interfaces.ApiClient;
import com.sc.aipdriver.activities.interfaces.ApiInterface;
import com.sc.aipdriver.activities.models.CommonError;
import com.sc.aipdriver.activities.models.PermissionModel;
import com.sc.aipdriver.activities.models.VehicleListData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dev on 25/8/17.
 */

public class SelectCarAdapter extends RecyclerView.Adapter<SelectCarAdapter.MyViewHolder> {
    Activity activity;
    List<VehicleListData> vehicleListDataList;
    SharedprefrenceManager sharedprefrenceManager;

    ApiInterface apiService;
    Gson gson;
    ShowLoading showLoading;

    public SelectCarAdapter(FragmentActivity activity, List<VehicleListData> vehicleListDataList) {
        this.activity = activity;
        this.vehicleListDataList = vehicleListDataList;
        sharedprefrenceManager = new SharedprefrenceManager(activity);
        apiService = ApiClient.getClient(activity).create(ApiInterface.class);
        gson = new GsonBuilder().setPrettyPrinting().create();
        showLoading = new ShowLoading(activity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.selectcar, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (vehicleListDataList.size() > 0) {
            final VehicleListData vehicleListData = vehicleListDataList.get(position);
            holder.carname.setText("" + vehicleListData.getVehicleName());
            holder.carnumber.setText("" + vehicleListData.getVinNumber());
            Glide.with(activity)
                    .load(vehicleListData.getUserImage()) // or new File(currentPhotoPath)
                    .placeholder(R.drawable.ic_car)
                    .into(holder.imgView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLoading.show();
                    sharedprefrenceManager.setVehicleId(vehicleListData.getId());
                    String s= String.valueOf(vehicleListData.getId());
                 //   Log.e("Value", "driverid" + sharedprefrenceManager.getDriverID() + " vechicleid" + vehicleListData.getId());
                 //   System.err.println("PERMISSION:::::    " + sharedprefrenceManager.getDriverID() + " vechicleid" + vehicleListData.getId() + "");

                    Call<DriverPermissionModel> call = apiService.driverPermission(sharedprefrenceManager.getDriverIdInt(), vehicleListData.getId());
                    call.enqueue(new Callback<DriverPermissionModel>() {
                        @Override
                        public void onResponse(Call<DriverPermissionModel> call, Response<DriverPermissionModel> response) {
                            System.err.println("In ADAPTER " + response.body().getData());
                            if (response.code() == 200) {
                                showLoading.dismiss();
                                if (response.body().getData() == 0) {
                                    System.err.println("In ADAPTER if DATA 0 " + response.body().getData());

                                    activity.startActivity(new Intent(activity, StartActivity.class));
                                } else {
                                    Toast.makeText(activity, "Already using this Vehicle", Toast.LENGTH_SHORT).show();
                                }
                            } else
                                try {
                                    showLoading.dismiss();
                                    CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                                    //  errorDialog(loginError.getMessage());
                                    Log.e("Data", loginError.getMessage());
                                } catch (Exception e) {
                                    showLoading.dismiss();
                                    e.printStackTrace();
                                    Log.e("Data", e.toString());
                                }
                        }

                        @Override
                        public void onFailure(Call<DriverPermissionModel> call, Throwable t) {
                            showLoading.dismiss();
                            Log.e("errorratro", t.getMessage());
                        }
                    });

                }
            });


            holder.imageButtonArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLoading.show();
                    sharedprefrenceManager.setVehicleId(vehicleListData.getId());
                    Log.e("Value", "driverid" + sharedprefrenceManager.getDriverID() + " vechicleid" + vehicleListData.getId());
                    System.err.println("PERMISSION:::::    " + sharedprefrenceManager.getDriverID() + " vechicleid" + vehicleListData.getId() + "");

                    Call<DriverPermissionModel> call = apiService.driverPermission(sharedprefrenceManager.getDriverIdInt(), vehicleListData.getId());
                    call.enqueue(new Callback<DriverPermissionModel>() {
                        @Override
                        public void onResponse(Call<DriverPermissionModel> call, Response<DriverPermissionModel> response) {
                            System.err.println("In ADAPTER " + response.body().getData());
                            if (response.code() == 200) {
                                showLoading.dismiss();
                                if (response.body().getData() == 0) {
                                    System.err.println("In ADAPTER if DATA 0 " + response.body().getData());

                                    activity.startActivity(new Intent(activity, StartActivity.class));
                                } else {
                                    Toast.makeText(activity, "Already using this Vehicle", Toast.LENGTH_SHORT).show();
                                }
                            } else
                                try {
                                    showLoading.dismiss();
                                    CommonError loginError = gson.fromJson(response.errorBody().string(), CommonError.class);
                                    //  errorDialog(loginError.getMessage());
                                    Log.e("Data", loginError.getMessage());
                                } catch (Exception e) {
                                    showLoading.dismiss();
                                    e.printStackTrace();
                                    Log.e("Data", e.toString());
                                }
                        }

                        @Override
                        public void onFailure(Call<DriverPermissionModel> call, Throwable t) {
                            showLoading.dismiss();
                            Log.e("errorratro", t.getMessage());
                        }
                    });

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return vehicleListDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView carname;

        TextView carnumber;

        ImageButton imageButtonArrow;
        CircleImageView imgView;


        public MyViewHolder(View itemView) {
            super(itemView);
         carname = itemView.findViewById(R.id.carname);
         carnumber = itemView.findViewById(R.id.carnumber);
         imageButtonArrow = itemView.findViewById(R.id.imageButtonArrow);
         imgView = itemView.findViewById(R.id.car_image);
        }

    }


}
