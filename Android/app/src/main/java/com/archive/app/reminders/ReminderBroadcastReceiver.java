// ReminderBroadcastReceiver.java

package com.archive.app.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build; // 引入 Build
import android.util.Log;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received onReceive enter: ");
        if (intent != null) {
            long scheduleId = intent.getLongExtra(ReminderManager.EXTRA_SCHEDULE_ID, -1);
            if (scheduleId != -1) {
                Log.d(TAG, "Received alarm for schedule ID: " + scheduleId);
                Intent serviceIntent = new Intent(context, ReminderService.class);
                serviceIntent.putExtra(ReminderManager.EXTRA_SCHEDULE_ID, scheduleId);

                // --- 核心修改点 ---
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d(TAG, "Starting foreground service.");
                    context.startForegroundService(serviceIntent);
                } else {
                    Log.d(TAG, "Starting service.");
                    context.startService(serviceIntent);
                }
                // ------------------

            } else {
                Log.w(TAG, "Received alarm with no schedule ID.");
            }
        }
    }
}