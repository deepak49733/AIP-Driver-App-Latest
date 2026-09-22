package com.sc.aipdriver.activities.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.models.CalendarDateModel;
import com.sc.aipdriver.databinding.ItemCalendarDateBinding;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CalendarDateAdapter extends RecyclerView.Adapter<CalendarDateAdapter.ViewHolder> {

    private List<CalendarDateModel> dateList;
    private OnDateClickListener listener;
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EE", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());

    public interface OnDateClickListener {
        void onDateClick(CalendarDateModel model, int position);
    }

    public CalendarDateAdapter(List<CalendarDateModel> dateList, OnDateClickListener listener) {
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarDateBinding binding = ItemCalendarDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarDateModel model = dateList.get(position);
        holder.binding.tvDayName.setText(dayFormat.format(model.getDate()).substring(0, 1));
        holder.binding.tvDate.setText(dateFormat.format(model.getDate()));

        holder.binding.flDateContainer.setSelected(model.isSelected());
        if (model.isSelected()) {
            holder.binding.tvDate.setTextColor(Color.WHITE);
        } else {
            holder.binding.tvDate.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.calendar_day_text));
        }

        holder.binding.indicatorMeeting.setVisibility(model.isHasMeeting() ? View.VISIBLE : View.GONE);
        holder.binding.indicatorEvent.setVisibility(model.isHasEvent() ? View.VISIBLE : View.GONE);
        holder.binding.indicatorShift.setVisibility(model.isHasShift() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateClick(model, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemCalendarDateBinding binding;

        public ViewHolder(ItemCalendarDateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
