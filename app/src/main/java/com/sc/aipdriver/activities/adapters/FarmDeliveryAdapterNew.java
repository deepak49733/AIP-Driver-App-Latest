package com.sc.aipdriver.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.models.PriorityFarmData;
import com.sc.aipdriver.activities.ui.FarmDetailActivityNew;

import java.util.List;

public class FarmDeliveryAdapterNew extends RecyclerView.Adapter<FarmDeliveryAdapterNew.FarmViewHolder> {

    private final Context context;
    private final List<PriorityFarmData> farmDataList;
    private final String routeName;
    private final String routeId;

    public FarmDeliveryAdapterNew(Context context, List<PriorityFarmData> farmDataList, String routeName, String routeId) {
        this.context = context;
        this.farmDataList = farmDataList;
        this.routeName = routeName;
        this.routeId = routeId;
    }

    @NonNull
    @Override
    public FarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_farm_delivery_new, parent, false);
        return new FarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmViewHolder holder, int position) {
        PriorityFarmData farmData = farmDataList.get(position);
        String name = farmData.getFarmName();
        if (name == null || name.isEmpty()) {
            name = "Farm " + (position + 1);
        }

        String buttonText = name + " Delivery Details";
        holder.btnDeliveryDetails.setText(buttonText);

        holder.btnDeliveryDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, FarmDetailActivityNew.class);
            intent.putExtra("FarmID", String.valueOf(farmData.getId()));
            intent.putExtra("farmId", String.valueOf(farmData.getId()));
            intent.putExtra("FarmName", farmData.getFarmName());
            intent.putExtra("ROUTE", routeName);
            intent.putExtra("ROUTEID", routeId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return farmDataList != null ? farmDataList.size() : 0;
    }

    static class FarmViewHolder extends RecyclerView.ViewHolder {
        Button btnDeliveryDetails;

        public FarmViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDeliveryDetails = itemView.findViewById(R.id.btn_farm_delivery_details);
        }
    }
}
