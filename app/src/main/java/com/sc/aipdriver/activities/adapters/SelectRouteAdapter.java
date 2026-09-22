package com.sc.aipdriver.activities.adapters;

import android.app.Activity;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.interfaces.OnClickRoute;
import com.sc.aipdriver.activities.ui.FarmListRoute;
import com.sc.aipdriver.activities.models.RouteListData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;

import java.util.List;


import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by dev on 25/8/17.
 */

public class SelectRouteAdapter extends RecyclerView.Adapter<SelectRouteAdapter.MyViewHolder> {
    Activity activity;
    List<RouteListData> routeListDataList;
    SharedprefrenceManager sharedprefrenceManager;
    OnClickRoute onClickRoute;

    public SelectRouteAdapter(FragmentActivity activity, List<RouteListData> routeListDataList, OnClickRoute onClickRoute) {
        this.activity = activity;
        this.routeListDataList = routeListDataList;
        this.onClickRoute = onClickRoute;
        sharedprefrenceManager=new SharedprefrenceManager(activity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.selectrouteadap, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (routeListDataList.size() > 0) {
            final RouteListData routeListData = routeListDataList.get(position);
            holder.routename.setText("" + routeListData.getRouteName());
            System.err.println("In Select Route Adapter::::    ");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemView.setEnabled(false);
                    sharedprefrenceManager.setRouteId(routeListData.getId());
                    System.err.println( "ROUTE ID   IN ADAPTER  ::   "+sharedprefrenceManager.getRouteId());

                    onClickRoute.onClickRout(routeListData);
                    holder.itemView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.itemView.setEnabled(true);
                        }
                    }, 1000); // 1 second delay

//                    Intent intent = new Intent(activity, FarmListRoute.class);
//                    intent.putExtra("ROUTE", routeListData.getRouteName());
//                    intent.putExtra("ROUTEID", routeListData.getId().toString());
//                    intent.putExtra("SEQUENCE", "Start");
//                    intent.putExtra("isResumed", false);
//                    holder.itemView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            holder.itemView.setEnabled(true);
//                        }
//                    }, 1000); // 1 second delay
//                    sharedprefrenceManager.setRouteName(routeListData.getRouteName());
//                    activity.startActivity(intent);
//                    activity.finish();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return routeListDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView routename;

        public MyViewHolder(View itemView) {
            super(itemView);
          routename = itemView.findViewById(R.id.route_name);
        }
    }

    private void deliverdDialog(final int position) {
        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Semen delivered at farm123")
                .setConfirmText("Yes,delivered")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        notifyDataSetChanged();
                        sDialog.setTitleText("Successfully")
                                .setContentText("Semen deliverd...")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }
}
