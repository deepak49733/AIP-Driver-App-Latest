package com.sc.aipdriver.activities.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.models.RouteDriverScheduleItem;

import java.util.List;

public class CalendarAdapterNew extends RecyclerView.Adapter<CalendarAdapterNew.DayViewHolder> {

    public interface OnScheduleClickListener {
        void onScheduleClick(List<RouteDriverScheduleItem> items);
    }

    public static class CalendarDayModel {
        public String dayNumber;
        public boolean isBlank;
        public List<RouteDriverScheduleItem> scheduleItems;

        public CalendarDayModel(String dayNumber, boolean isBlank, List<RouteDriverScheduleItem> scheduleItems) {
            this.dayNumber = dayNumber;
            this.isBlank = isBlank;
            this.scheduleItems = scheduleItems;
        }
    }

    private final Context context;
    private final List<CalendarDayModel> dayList;
    private final OnScheduleClickListener clickListener;

    public CalendarAdapterNew(Context context, List<CalendarDayModel> dayList, OnScheduleClickListener clickListener) {
        this.context = context;
        this.dayList = dayList;
        this.clickListener = clickListener;
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

        holder.llRouteTags.removeAllViews();
        holder.itemView.setOnClickListener(null);

        if (model.isBlank) {
            holder.tvDayNumber.setText("");
            return;
        }

        holder.tvDayNumber.setText(model.dayNumber);

        if (model.scheduleItems != null && !model.scheduleItems.isEmpty()) {
            for (RouteDriverScheduleItem item : model.scheduleItems) {
                if (item == null) continue;

                String routeName = item.getRouteName() != null ? item.getRouteName().trim() : "";
                String driverName = item.getDriverName() != null ? item.getDriverName().trim() : "";

                String tag;
                if (!routeName.isEmpty() && !driverName.isEmpty()) {
                    tag = routeName + " - " + driverName;
                } else if (!routeName.isEmpty()) {
                    tag = routeName;
                } else {
                    tag = driverName;
                }

                if (tag.isEmpty()) continue;

                TextView tvTag = new TextView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 2, 0, 0);
                tvTag.setLayoutParams(params);
                tvTag.setBackgroundResource(R.drawable.bg_calendar_tag);
                tvTag.setTextColor(Color.WHITE);
                tvTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7.5f);
                tvTag.setTypeface(null, Typeface.BOLD);
                tvTag.setPadding(3, 2, 3, 2);
                tvTag.setMaxLines(3);
                tvTag.setEllipsize(TextUtils.TruncateAt.END);
                tvTag.setLineSpacing(0f, 0.9f);
                tvTag.setText(tag);

                tvTag.setOnClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onScheduleClick(model.scheduleItems);
                    }
                });

                holder.llRouteTags.addView(tvTag);
            }

            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null && !model.scheduleItems.isEmpty()) {
                    clickListener.onScheduleClick(model.scheduleItems);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dayList != null ? dayList.size() : 0;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNumber;
        LinearLayout llRouteTags;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
            llRouteTags = itemView.findViewById(R.id.ll_route_tags);
        }
    }
}
