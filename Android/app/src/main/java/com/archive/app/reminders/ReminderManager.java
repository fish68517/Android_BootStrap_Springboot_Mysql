package com.archive.app.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.archive.app.model.Schedule;

import java.util.Calendar;

public class ReminderManager {

    private static final String TAG = "ReminderManager";
    public static final String EXTRA_SCHEDULE_ID = "extra_schedule_id";

    private final Context context;
    private final AlarmManager alarmManager;

    public ReminderManager(Context context) {
        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setReminder(Schedule schedule) {
        if (schedule == null || schedule.getId() == null || schedule.getReminderType() <= 0) {
            return;
        }

        long reminderTime = calculateReminderTime(schedule.getStartTime().getTime(), schedule.getReminderType());

        if (reminderTime < System.currentTimeMillis()) {
            Log.w(TAG, "Reminder time is in the past for schedule ID: " + schedule.getId());
            return;
        }

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra(EXTRA_SCHEDULE_ID, schedule.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                schedule.getId().intValue(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
                } else {
                    // Fallback for Android 12+ if permission is not granted
                    alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
                    Log.w(TAG, "Missing SCHEDULE_EXACT_ALARM permission. Setting an inexact alarm instead.");
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            }
            Log.d(TAG, "Set reminder for schedule ID: " + schedule.getId() + " at " + new java.util.Date(reminderTime));
        }
    }

    public void cancelReminder(long scheduleId) {
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) scheduleId,
                intent,
                PendingIntent.FLAG_NO_CREATE | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d(TAG, "Cancelled reminder for schedule ID: " + scheduleId);
        }
    }

    private long calculateReminderTime(long startTime, int reminderType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);

        switch (reminderType) {
            case 1: // 提前5分钟
                calendar.add(Calendar.MINUTE, -1);
                break;
            case 2: // 提前15分钟
                calendar.add(Calendar.MINUTE, -15);
                break;
            case 3: // 提前30分钟
                calendar.add(Calendar.MINUTE, -30);
                break;

            default:
                return -1;
        }
        return calendar.getTimeInMillis();
    }
} 