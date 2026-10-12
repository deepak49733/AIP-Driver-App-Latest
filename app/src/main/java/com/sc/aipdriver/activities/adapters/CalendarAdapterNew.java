package com.sc.aipdriver.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;

import java.util.List;

public class CalendarAdapterNew extends RecyclerView.Adapter<CalendarAdapterNew.DayViewHolder> {

    public static class CalendarDayModel {
        public String dayNumber;
        public boolean isBlank;
        public List<String> routeTags;

        public CalendarDayModel(String dayNumber, boolean isBlank, List<String> routeTags) {
            this.dayNumber = dayNumber;
            this.isBlank = isBlank;
            this.routeTags = routeTags;
        }
    }

    private final Context context;
    private final List<CalendarDayModel> dayList;

    public CalendarAdapterNew(Context context, List<CalendarDayModel> dayList) {
        this.context = context;
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day_new, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        CalendarDayModel model = dayList.get(position);

        if (model.isBlank) {
            holder.tvDayNumber.setText("");
            holder.tvRouteTag1.setVisibility(View.GONE);
            holder.tvRouteTag2.setVisibility(View.GONE);
            return;
        }

        holder.tvDayNumber.setText(model.dayNumber);

        if (model.routeTags != null && !model.routeTags.isEmpty()) {
            holder.tvRouteTag1.setVisibility(View.VISIBLE);
            holder.tvRouteTag1.setText(model.routeTags.get(0));

            if (model.routeTags.size() > 1) {
                holder.tvRouteTag2.setVisibility(View.VISIBLE);
                holder.tvRouteTag2.setText(model.routeTags.get(1));
            } else {
                holder.tvRouteTag2.setVisibility(View.GONE);
            }
        } else {
            holder.tvRouteTag1.setVisibility(View.GONE);
            holder.tvRouteTag2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dayList != null ? dayList.size() : 0;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNumber;
        TextView tvRouteTag1;
        TextView tvRouteTag2;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
            tvRouteTag1 = itemView.findViewById(R.id.tv_route_tag_1);
            tvRouteTag2 = itemView.findViewById(R.id.tv_route_tag_2);
        }
    }
}
