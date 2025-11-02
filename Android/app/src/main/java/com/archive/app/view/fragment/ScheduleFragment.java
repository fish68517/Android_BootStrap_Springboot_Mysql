package com.archive.app.view.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.db.ScheduleContract;
import com.archive.app.db.ScheduleDbHelper;
import com.archive.app.model.Schedule;
import com.archive.app.reminders.ReminderManager;
import com.archive.app.view.activity.AddEditScheduleActivity;
import com.archive.app.view.adapter.ScheduleAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleFragment extends Fragment implements ScheduleAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<Schedule> scheduleList = new ArrayList<>();
    private ScheduleDbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheduleAdapter(getActivity(),scheduleList, this);
        recyclerView.setAdapter(adapter);

        dbHelper = new ScheduleDbHelper(getContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSchedules();
    }

    private void loadSchedules() {
        scheduleList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ScheduleContract.ScheduleEntry._ID,
                ScheduleContract.ScheduleEntry.COLUMN_NAME_TITLE,
                ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME,
                ScheduleContract.ScheduleEntry.COLUMN_NAME_LOCATION,
                ScheduleContract.ScheduleEntry.COLUMN_NAME_IS_COMPLETED,
                ScheduleContract.ScheduleEntry.COLUMN_NAME_REPEAT_MODE,
                ScheduleContract.ScheduleEntry.COLUMN_NAME_REMINDER_TYPE
        };

        Cursor cursor = db.query(
                ScheduleContract.ScheduleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME + " DESC"
        );

        while (cursor.moveToNext()) {
            Schedule schedule = new Schedule();
            schedule.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry._ID)));
            schedule.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_TITLE)));
            schedule.setStartTime(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME))));
            schedule.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_LOCATION)));
            schedule.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_IS_COMPLETED)) == 1);
            schedule.setRepeatMode(cursor.getInt(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_REPEAT_MODE)));
            schedule.setReminderType(cursor.getInt(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_REMINDER_TYPE)));
            scheduleList.add(schedule);
        }
        cursor.close();
        
        // 添加一些假数据用于测试
        if (scheduleList.isEmpty()) {
            addDummyData();
            loadSchedules(); // 重新加载以显示假数据
            return;
        }

        adapter.setSchedules(scheduleList);
    }
    
    private void addDummyData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ScheduleContract.ScheduleEntry.TABLE_NAME, null, null); // 清空旧数据
        
        Schedule schedule1 = new Schedule();
        schedule1.setTitle("团队会议");
        schedule1.setStartTime(new Date());
        schedule1.setLocation("会议室 A");
        schedule1.setCompleted(false);
        insertSchedule(schedule1);

        Schedule schedule2 = new Schedule();
        schedule2.setTitle("项目上线");
        schedule2.setStartTime(new Date(System.currentTimeMillis() + 3600 * 1000));
        schedule2.setLocation("线上");
        schedule2.setCompleted(false);
        insertSchedule(schedule2);
        
        Schedule schedule3 = new Schedule();
        schedule3.setTitle("完成报告");
        schedule3.setStartTime(new Date(System.currentTimeMillis() - 86400 * 1000));
        schedule3.setLocation("办公室");
        schedule3.setCompleted(true);
        insertSchedule(schedule3);
    }
    
    private void insertSchedule(Schedule schedule) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_TITLE, schedule.getTitle());
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME, schedule.getStartTime().getTime());
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_LOCATION, schedule.getLocation());
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_IS_COMPLETED, schedule.isCompleted() ? 1 : 0);
        db.insert(ScheduleContract.ScheduleEntry.TABLE_NAME, null, values);
    }

    @Override
    public void onItemClick(Schedule schedule) {
        Intent intent = new Intent(getActivity(), AddEditScheduleActivity.class);
        intent.putExtra(AddEditScheduleActivity.EXTRA_SCHEDULE_ID, schedule.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Schedule schedule) {
        new AlertDialog.Builder(getContext())
                .setTitle("删除日程")
                .setMessage("您确定要删除这个日程吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    deleteSchedule(schedule.getId());
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteSchedule(long id) {
        new ReminderManager(getContext()).cancelReminder(id);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                ScheduleContract.ScheduleEntry.TABLE_NAME,
                ScheduleContract.ScheduleEntry._ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        loadSchedules(); // Refresh the list
    }
} 