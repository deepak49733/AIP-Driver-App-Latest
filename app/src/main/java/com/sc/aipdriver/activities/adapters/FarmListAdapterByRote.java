package com.sc.aipdriver.activities.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.dialogs.LoadTemprature;
import com.sc.aipdriver.activities.interfaces.ItemTouchHelperAdapter;
import com.sc.aipdriver.activities.interfaces.ItemTouchHelperViewHolder;
import com.sc.aipdriver.activities.interfaces.OnClickFarm;
import com.sc.aipdriver.activities.interfaces.OnFarmListChangedListener;
import com.sc.aipdriver.activities.interfaces.OnItemClick;
import com.sc.aipdriver.activities.interfaces.OnStartDragListener;
import com.sc.aipdriver.activities.models.LiRoutePlannerDetail;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.ui.FarmDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by dev on 25/8/17.
 */

public class FarmListAdapterByRote extends RecyclerView.Adapter<FarmListAdapterByRote.MyViewHolder> implements ItemTouchHelperAdapter {
//public class FarmListAdapterByRote extends RecyclerView.Adapter<FarmListAdapterByRote.MyViewHolder>  {

    Activity activity;
    List<PriorityFarmData> farmDataList;
    List<LiRoutePlannerDetail> liRoutePlannerDetails;
    AdapterbyRoute adapterbyRoute;
    private OnStartDragListener mDragStartListener;
    private OnFarmListChangedListener mListChangedListener;
    String routeName;
    String routeId;
    String formid;
    String sequence;
    Boolean shouldSatrt=false;
    CheckFormEntry checkFormEntry;
    List<String> list;
    private long lastClickTime = 0L;
    OnItemClick onItemClick;
    OnClickFarm onClickFarm;
    MyViewHolder myViewHolder;
    int counter = 0;

    public FarmListAdapterByRote(OnItemClick onItemClick, Activity activity, List<PriorityFarmData> farmDataList, String routeName, String routeId, String sequence, AdapterbyRoute adapterbyRoute, CheckFormEntry checkFormEntry, OnStartDragListener dragLlistener,
                                 OnFarmListChangedListener listChangedListener,OnClickFarm onClickFarm) {
        this.activity = activity;
        this.farmDataList = farmDataList;
        this.routeName = routeName;
        this.routeId = routeId;
        this.sequence = sequence;
        this.adapterbyRoute = adapterbyRoute;
        this.mDragStartListener = dragLlistener;
        this.mListChangedListener = listChangedListener;
        this.checkFormEntry = checkFormEntry;
        this.onClickFarm = onClickFarm;
        this.onItemClick = onItemClick;
        liRoutePlannerDetails = new ArrayList<>();
        list = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.farmadap, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        myViewHolder = holder;
        if (farmDataList.size() > 0) {
            if (farmDataList.get(position).getRemove()==1){
                holder.llRoot.setVisibility(View.GONE);
            }else{
                holder.llRoot.setVisibility(View.VISIBLE);
            }
            final PriorityFarmData farmData = farmDataList.get(position);
            holder.title.setText(farmData.getFarmName());
            Log.d("Analysis__",farmData.getIsCompleted()+"" );
            if (farmData.getIsCompleted()==1){
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                holder.imgView.setText("Delivered");
                holder.title.setOnClickListener(null);
                holder.imgView.setOnClickListener(null);
            }else  if (farmData.getIsLoaded()==1){
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                holder.imgView.setText("Loaded");
                holder.title.setOnClickListener(null);
                holder.imgView.setOnClickListener(null);
            }
            else{
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(activity, R.color.orange));
                holder.imgView.setText("Load");
                holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int shouldStart=areAllItemsLoaded(farmDataList);
                        holder.title.setEnabled(false);
                        Log.d("Analysis__","Should Start is "+shouldStart);
                        onItemClick.onItemClick(farmData);
//                            onClickFarm.onFarmClick(farmData,shouldStart,routeName);
                        // new LoadTemprature(activity, FarmListAdapterByRote.this, ""+farmData.getId(), routeName).createDialog();
                        holder.title.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.title.setEnabled(true);
                            }
                        }, 1000); // 1 second
                    }
                });
                holder.imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int shouldStart=areAllItemsLoaded(farmDataList);
                        holder.title.setEnabled(false);
                        Log.d("Analysis__","Should Start is "+shouldStart);
                        onItemClick.onItemClick(farmData);
//                            onClickFarm.onFarmClick(farmData,shouldStart,routeName);
                        // new LoadTemprature(activity, FarmListAdapterByRote.this, ""+farmData.getId(), routeName).createDialog();
                        holder.title.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.title.setEnabled(true);
                            }
                        }, 1000); // 1 second
                    }
                });
            }
           /* if (farmData.getIsCompleted() != 0) {
                if (farmData.getIsPhotoUploaded()==0){
                    holder.title.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
                    holder.title.setAlpha(0.5f);

                }
                else{
                    holder.title.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                holder.title.setAlpha(1f);
                }
                holder.imgView.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Log.d("Analysis__", "Farm Data is " + new Gson().toJson(farmData));
                        //Log.d("Analysis__", "Farm Data ID is " + (farmData.getId()));
                           // onClickFarm.onFarmClick(farmData,0,routeName);
                    }
                });
            }
            else
            {
                holder.title.setBackgroundColor(ContextCompat.getColor(activity, R.color.orange));
                holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int shouldStart=areAllItemsLoaded(farmDataList);
                        holder.title.setEnabled(false);
                        Log.d("Analysis__","Should Start is "+shouldStart);
                        onClickFarm.onFarmClick(farmData,shouldStart,routeName);
                        // new LoadTemprature(activity, FarmListAdapterByRote.this, ""+farmData.getId(), routeName).createDialog();
                        holder.title.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.title.setEnabled(true);
                            }
                        }, 1000); // 1 second
                    }
                });
            }*/
            Log.e("farmane", "" + farmData.getFarmName());



//            if (sequence.equalsIgnoreCase("Change") && counter <= farmDataList.size()){
//                counter++;
//                new LoadTemprature(activity, FarmListAdapterByRote.this, ""+farmData.getId(), routeName,routeId).setFarmDetail();
//            }


            if (formid != null) {
                if (formid.equalsIgnoreCase("" + farmDataList.get(position).getId())) {
                    holder.title.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                    list.add(formid);
                    checkFormEntry.addFormEntry(list);
                }
            }
        }
    }

    public int areAllItemsLoaded(List<PriorityFarmData>someList) {
        for (PriorityFarmData item : someList) {
            if (item.getIsLoaded() == 0  && item.getIsCompleted() == 0) {
                return 0;
            }
        }
        return 1;
    }
    @Override
    public int getItemCount() {
        return farmDataList.size();
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        System.out.println("eueueuwieuwewuiew " + fromPosition + " -- " + toPosition);
        Collections.swap(farmDataList, fromPosition, toPosition);
        mListChangedListener.onNoteListChanged(farmDataList);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        farmDataList.remove(position);
        notifyItemRemoved(position);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        LinearLayout llRoot;
        TextView title;
        TextView imgView;


        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.farmsname);
            imgView = itemView.findViewById(R.id.loadDetail);
            llRoot = itemView.findViewById(R.id.ll_root);

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

    public void additem(String farmid, String semenbags, String temprature) {
        LiRoutePlannerDetail liRoutePlannerDetail = new LiRoutePlannerDetail();
        liRoutePlannerDetail.setFIRMID(farmid);
        liRoutePlannerDetail.setNumberOfBagsLoaded(semenbags);
        liRoutePlannerDetail.setTemperatureOfSemenLoaded(temprature);
        liRoutePlannerDetails.add(liRoutePlannerDetail);
        adapterbyRoute.addList(liRoutePlannerDetails);
    }

    public void formEntry(String farmid) {
        formid = farmid;
        notifyDataSetChanged();
    }

    public interface AdapterbyRoute {
        void addList(List<LiRoutePlannerDetail> liRoutePlannerDetails);
    }

    public interface CheckFormEntry {
        void addFormEntry(List<String> formEntry);
    }
}
