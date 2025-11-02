package com.archive.app.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.Schedule;
import com.archive.app.tts.TTSActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> scheduleList;

    private static Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Schedule schedule);
        void onItemLongClick(Schedule schedule);
    }

    public ScheduleAdapter(Context context,List<Schedule> scheduleList, OnItemClickListener listener) {
        this.scheduleList = scheduleList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.bind(schedule, listener);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void setSchedules(List<Schedule> schedules) {
        this.scheduleList = schedules;
        notifyDataSetChanged();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView location;
        TextView status;
        TextView tts;
        TextView repeatMode;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_title);
            time = itemView.findViewById(R.id.text_view_time);
            location = itemView.findViewById(R.id.text_view_location);
            status = itemView.findViewById(R.id.text_view_status);
            tts = itemView.findViewById(R.id.tts);
            repeatMode = itemView.findViewById(R.id.text_view_repeat_mode);
        }

        public void bind(final Schedule schedule, final OnItemClickListener listener) {
            title.setText(schedule.getTitle());
            time.setText("时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(schedule.getStartTime()));
            location.setText("地点: " + schedule.getLocation());
            if (schedule.isCompleted()) {
                status.setText("状态: 已完成");
                status.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                status.setText("状态: 未完成");
                status.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            }

            if (schedule.getRepeatMode() > 0) {
                repeatMode.setVisibility(View.VISIBLE);
                String[] repeatModes = itemView.getContext().getResources().getStringArray(R.array.repeat_modes);
                if (schedule.getRepeatMode() < repeatModes.length) {
                    repeatMode.setText("重复: " + repeatModes[schedule.getRepeatMode()]);
                }
            } else {
                repeatMode.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(schedule));
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(schedule);
                return true;
            });

            tts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TTSActivity.class);
                    intent.putExtra("TTS_Bean", schedule);
                    context.startActivity(intent);
                }
            });
        }
    }
} 