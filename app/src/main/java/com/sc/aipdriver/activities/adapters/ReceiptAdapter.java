package com.sc.aipdriver.activities.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.interfaces.ItemTouchHelperAdapter;
import com.sc.aipdriver.activities.interfaces.ItemTouchHelperViewHolder;
import com.sc.aipdriver.activities.interfaces.OnFarmListChangedListener;
import com.sc.aipdriver.activities.interfaces.OnItemClick;
import com.sc.aipdriver.activities.interfaces.OnReceiptClick;
import com.sc.aipdriver.activities.interfaces.OnStartDragListener;
import com.sc.aipdriver.activities.models.LiRoutePlannerDetail;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.models.ReceiptDetails;
import com.sc.aipdriver.activities.models.ReceiptResponse;
import com.sc.aipdriver.activities.ui.FarmDetailActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by dev on 25/8/17.
 */

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.MyViewHolder>  {
//public class FarmListAdapterByRote extends RecyclerView.Adapter<FarmListAdapterByRote.MyViewHolder>  {

    Activity activity;
    ArrayList<ReceiptResponse> alReceipts;



    OnReceiptClick onReceiptClick;
    MyViewHolder myViewHolder;
    int counter = 0;

    public ReceiptAdapter(OnReceiptClick onReceiptClick, Activity activity, ArrayList<ReceiptResponse> receiptDetails ) {
        this.activity = activity;
        this.onReceiptClick = onReceiptClick;
        this.alReceipts = receiptDetails;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.receiptlayout, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        myViewHolder = holder;
        if (alReceipts.size() > 0) {
            ReceiptResponse model = alReceipts.get(position);
            counter = position+1;
            holder.imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.imgView.setEnabled(false);
                    if (model.getRecipt_Img()!=null)
                    if (model.getRecipt_Img().length()>3) {
                        onReceiptClick.onReceiptClick(position, model, true);
                    }
                    holder.imgView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.imgView.setEnabled(true);
                        }
                    }, 1500); // 1 second delay
                }
            });
            holder.tvRcpt.setText("Receipt "+counter);
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.root.setEnabled(false);
                        onReceiptClick.onReceiptClick(position,model,false);

                        holder.root.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.root.setEnabled(true);
                            }
                        }, 1500); // 1 second delay
                    }
                });

            Object imageSource = model.getRecipt_Img();
            if (imageSource != null && !((String) imageSource).startsWith("http") && !((String) imageSource).isEmpty()) {
                imageSource = new File((String) imageSource);
            }

            Glide.with(activity)
                        .load(imageSource)
                    .placeholder(R.drawable.ic_doc)// or new File(currentPhotoPath)
                    .into(holder.imgView);
        }
    }

    @Override
    public int getItemCount() {
        return alReceipts.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        TextView tvRcpt;
        ImageView imgView;
        CardView root;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvRcpt = itemView.findViewById(R.id.tv_receipt);
            root = itemView.findViewById(R.id.root);
            imgView = itemView.findViewById(R.id.car_image);


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
