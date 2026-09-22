package com.sc.aipdriver.activities.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sc.aipdriver.R;
import com.sc.aipdriver.activities.models.TimelineEventModel;
import com.sc.aipdriver.activities.models.TimelineRowModel;
import com.sc.aipdriver.databinding.ItemTimelineBinding;
import com.sc.aipdriver.databinding.ViewTimelineEventBinding;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private List<TimelineRowModel> rowList;

    public TimelineAdapter(List<TimelineRowModel> rowList) {
        this.rowList = rowList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTimelineBinding binding = ItemTimelineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimelineRowModel rowModel = rowList.get(position);
        holder.binding.tvTime.setText(rowModel.getTime());
        holder.binding.llEventsContainer.removeAllViews();

        for (TimelineEventModel event : rowModel.getEvents()) {
            ViewTimelineEventBinding eventBinding = ViewTimelineEventBinding.inflate(
                    LayoutInflater.from(holder.itemView.getContext()), holder.binding.llEventsContainer, false);

            eventBinding.tvEventTitle.setText(event.getTitle());
            eventBinding.tvEventTime.setText(event.getStartTime() + "-" + event.getEndTime());

            switch (event.getType()) {
                case MEETING:
                    eventBinding.tvEventType.setText("MEETING");
                    eventBinding.vAccent.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.meeting_accent));
                    eventBinding.tvEventType.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.meeting_accent));
                    eventBinding.llEventBg.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.meeting_bg));
                    break;
                case EVENT:
                    eventBinding.tvEventType.setText("EVENT");
                    eventBinding.vAccent.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.event_accent));
                    eventBinding.tvEventType.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.event_accent));
                    eventBinding.llEventBg.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.event_bg));
                    break;
                case SHIFT:
                    eventBinding.tvEventType.setText("SHIFT");
                    eventBinding.vAccent.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.shift_accent));
                    eventBinding.tvEventType.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.shift_accent));
                    eventBinding.llEventBg.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.shift_bg));
                    break;
            }

            holder.binding.llEventsContainer.addView(eventBinding.getRoot());
        }
    }

    @Override
    public int getItemCount() {
        return rowList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTimelineBinding binding;

        public ViewHolder(ItemTimelineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
