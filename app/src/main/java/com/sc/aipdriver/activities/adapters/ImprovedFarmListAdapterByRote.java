package com.sc.aipdriver.activities.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.IsInternetAvailableKt;
import com.sc.aipdriver.activities.interfaces.ItemTouchHelperViewHolder;
import com.sc.aipdriver.activities.interfaces.OnClickImprovedFarm;
import com.sc.aipdriver.activities.interfaces.OnImprovedItemClick;
import com.sc.aipdriver.activities.models.ImprovedPriorityFarmData;
import com.sc.aipdriver.activities.otherclasses.SharedprefrenceManager;

import java.util.Collections;
import java.util.List;

public class ImprovedFarmListAdapterByRote extends RecyclerView.Adapter<ImprovedFarmListAdapterByRote.MyViewHolder> {

    Activity activity;
   SharedprefrenceManager sharedprefrenceManager;
    List<ImprovedPriorityFarmData> farmDataList;

    String routeName;
    String routeId;

    OnImprovedItemClick onItemClick;
    OnClickImprovedFarm onClickImprovedFarm;

    public ImprovedFarmListAdapterByRote(OnImprovedItemClick onItemClick,
                                         Activity activity,
                                         List<ImprovedPriorityFarmData> farmDataList,
                                         String routeName,
                                         String routeId,
                                         OnClickImprovedFarm onClickImprovedFarm,
                                         SharedprefrenceManager sharedprefrenceManager) {

        this.activity = activity;
        this.farmDataList = farmDataList;
        this.routeName = routeName;
        this.routeId = routeId;
        this.onItemClick = onItemClick;
        this.onClickImprovedFarm = onClickImprovedFarm;
        this.sharedprefrenceManager = sharedprefrenceManager;
        // ✅ IMPORTANT: Sort by priority
        Collections.sort(farmDataList, (a, b) ->
                Integer.compare(a.getPriority(), b.getPriority()));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.improveddfarmadap, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final ImprovedPriorityFarmData farmData = farmDataList.get(position);

        holder.title.setText(farmData.getFarmName());
        holder.loadDetail.setText("");

        // 🔥 RESET (VERY IMPORTANT)
        holder.btnStartRide.setVisibility(GONE);
        holder.btnPauseRide.setVisibility(GONE);
        holder.btnCompleteRide.setVisibility(GONE);
        holder.ivMaps.setVisibility(GONE);
        holder.btnStartRide.setEnabled(true);
        holder.btnPauseRide.setEnabled(true);
        holder.btnCompleteRide.setEnabled(true);
        holder.btnStartRide.setAlpha(1f);

        // =========================
        // ✅ STATE LOGIC
        // =========================
// ✅ COMPLETED
        // =========================
// ✅ STATE LOGIC (FIXED)
// =========================

// ✅ COMPLETED
        if (farmData.getIsCompleted() == 1) {

            holder.btnStartRide.setVisibility(GONE);
            holder.btnPauseRide.setVisibility(GONE);
            holder.ivMaps.setVisibility(GONE);
            holder.btnCompleteRide.setVisibility(VISIBLE);

            holder.btnCompleteRide.setText("Delivered");
            holder.btnCompleteRide.setEnabled(false);

            if (farmData.getIsPhotoUploaded() == 0) {
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
                holder.llRoot.setAlpha(0.5f);
                holder.tvPhotopending.setVisibility(VISIBLE);
            } else {
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                holder.llRoot.setAlpha(1f);
                holder.tvPhotopending.setVisibility(GONE);
            }

// ✅ ACTIVE RIDE (MOST IMPORTANT)
        }
        else if (farmData.getIsActiveRide() != null && farmData.getIsActiveRide() == 1) {

            holder.btnStartRide.setVisibility(GONE);
            holder.btnPauseRide.setVisibility(VISIBLE);
            if (sharedprefrenceManager.getIsPaused()==1){
                holder.btnPauseRide.setText("Resume");
                holder.ivMaps.setVisibility(GONE);

            }
            else {
                holder.ivMaps.setVisibility(VISIBLE);
                holder.btnPauseRide.setText("Pause");
                holder.ivMaps.setVisibility(VISIBLE);
            }
            holder.btnCompleteRide.setVisibility(VISIBLE);
            holder.btnCompleteRide.setText("Complete");
            holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
            holder.tvPhotopending.setVisibility(GONE);

            holder.ivMaps.setOnClickListener(v -> onClickImprovedFarm.onMapClick(farmData, routeName));

// ✅ NOT STARTED
        }
        else {

            holder.btnStartRide.setVisibility(VISIBLE);
            holder.btnPauseRide.setVisibility(GONE);
            holder.btnCompleteRide.setVisibility(GONE);
            holder.ivMaps.setVisibility(GONE);
            holder.btnCompleteRide.setText("Complete");
            holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
            holder.tvPhotopending.setVisibility(GONE);
            // 🔥 PRIORITY LOGIC
            boolean canStart;

            if (position == 0) {
                canStart = true;
            } else {
                ImprovedPriorityFarmData prev = farmDataList.get(position - 1);
                canStart = prev.getIsCompleted() == 1;
            }
            holder.btnStartRide.setEnabled(canStart);
            holder.btnStartRide.setAlpha(canStart ? 1f : 0.5f);
        }
  /*      if (farmData.getIsCompleted() == 1) {

            // ✅ COMPLETED
            if (farmData.getIsPhotoUploaded() == 0) {
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                holder.llRoot.setAlpha(0.5f);
            } else {
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                holder.llRoot.setAlpha(1f);
            }

            holder.btnCompleteRide.setVisibility(View.VISIBLE);
            holder.btnCompleteRide.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
            holder.btnCompleteRide.setText("Delivered");
            holder.btnCompleteRide.setEnabled(false);

        }
        else if (farmData.isVisited()) {

            // ✅ IN PROGRESS
            holder.title.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
            holder.btnPauseRide.setVisibility(View.VISIBLE);
            holder.btnCompleteRide.setVisibility(View.VISIBLE);

        }
        else {

            // ✅ NOT STARTED
            holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
            holder.btnStartRide.setVisibility(View.VISIBLE);

            // 🔥 PRIORITY LOGIC
            boolean canStart;

            if (position == 0) {
                // First item always enabled
                canStart = true;
            } else {
                ImprovedPriorityFarmData previousItem = farmDataList.get(position - 1);
                canStart = previousItem.getIsCompleted() == 1;
            }

            holder.btnStartRide.setEnabled(canStart);
            holder.btnStartRide.setAlpha(canStart ? 1f : 0.5f);
        }*/

        // =========================
        // ✅ CLICK EVENTS
        // =========================

        holder.title.setOnClickListener(view ->
                onClickImprovedFarm.onFarmNameClick(farmData, routeName));

        holder.btnStartRide.setOnClickListener(v -> {

            if (!holder.btnStartRide.isEnabled()) {
                Toast.makeText(activity, "Complete previous farm first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (sharedprefrenceManager.isOnlineMode() && !IsInternetAvailableKt.isInternetAvailable(activity)) {
                Toast.makeText(activity, "Internet is required in Online mode.", Toast.LENGTH_SHORT).show();
                return;
            }

            holder.btnStartRide.setVisibility(GONE);
holder.btnPauseRide.setVisibility(VISIBLE);
holder.btnCompleteRide.setVisibility(VISIBLE);

            holder.btnStartRide.setEnabled(false);

            onClickImprovedFarm.onStartRide(farmData, routeName);

            holder.btnStartRide.postDelayed(() ->
                    holder.btnStartRide.setEnabled(true), 1000);
        });

        holder.btnPauseRide.setOnClickListener(v -> {
            if (onItemClick != null) {
                if (holder.btnPauseRide.getText().toString().equals("Pause")) {
                    onClickImprovedFarm.onPauseRide(farmData, routeName);
                } else {
                    onClickImprovedFarm.onResumeRide(farmData, routeName);
                }
            }
        });

        holder.btnCompleteRide.setOnClickListener(v -> {
            if (farmData.getIsCompleted() != 1) {
                if (onItemClick != null) {
                    onClickImprovedFarm.onCompleteRide(farmData, routeName);
                }
            }
        });
        holder.llRoot.setOnClickListener(view ->{
            if (farmData.getIsCompleted()==1 && farmData.getIsPhotoUploaded()==0) {
                holder.llRoot.setEnabled(false);
                onClickImprovedFarm.onParentClick(farmData, routeName);
            }
            holder.llRoot.postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.llRoot.setEnabled(true);
                }
            }, 1000); // 1 second
        Log.e("farmName", farmData.getFarmName());
        });
    }

    @Override
    public int getItemCount() {
        return farmDataList.size();
    }

    // =========================
    // ✅ VIEW HOLDER
    // =========================

    public class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        LinearLayout llRoot;
        TextView title, loadDetail, tvPhotopending;
        ImageView ivMaps;
        Button btnStartRide, btnPauseRide, btnCompleteRide;

        public MyViewHolder(View itemView) {
            super(itemView);

            llRoot = itemView.findViewById(R.id.ll_root);
            title = itemView.findViewById(R.id.farmsname);
            tvPhotopending = itemView.findViewById(R.id.photopending);
            loadDetail = itemView.findViewById(R.id.loadDetail);
            ivMaps = itemView.findViewById(R.id.iv_map);

            btnStartRide = itemView.findViewById(R.id.btnStartRide);
            btnPauseRide = itemView.findViewById(R.id.btnPauseRide);
            btnCompleteRide = itemView.findViewById(R.id.btnCompleteRide);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}