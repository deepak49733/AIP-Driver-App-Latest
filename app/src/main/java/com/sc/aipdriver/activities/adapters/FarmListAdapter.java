package com.sc.aipdriver.activities.adapters;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;


import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by dev on 25/8/17.
 */

public class FarmListAdapter extends RecyclerView.Adapter<FarmListAdapter.MyViewHolder> {
    Activity activity;

    public FarmListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.farmlistadap, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

           holder.title.setText("AiPartners Farms"+position);
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   deliverdDialog(position);
               }
           });

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        public MyViewHolder(View itemView) {
            super(itemView);
           title =itemView.findViewById(R.id.farmname);
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
