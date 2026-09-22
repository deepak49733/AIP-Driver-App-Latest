package com.sc.aipdriver.activities.adapters;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;

import at.blogc.android.views.ExpandableTextView;



/**
 * Created by dev on 25/8/17.
 */

public class AllNotificationAdapter extends RecyclerView.Adapter<AllNotificationAdapter.MyViewHolder> {

    Activity activity;

    public AllNotificationAdapter(FragmentActivity activity) {
        this.activity = activity;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.allnotificationadap, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
holder.datetime.setText("date: 12/24/2017  Time : 10:35 AM");
           holder.msg.setText("AiPartners has launched this Mobile " +
                   "application for sow farm managers to order product online, " +
                   "making the ordering process convenient, accurate and easy. " +
                    " orders and stopped orders from the application.");
holder.msg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (holder.msg.isExpanded())
            holder.msg.collapse();
        else holder.msg.expand();
    }
});
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        ExpandableTextView msg;
        TextView datetime;



        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            msg = itemView.findViewById(R.id.msg);
            datetime = itemView.findViewById(R.id.datetime);

        }
    }
}
