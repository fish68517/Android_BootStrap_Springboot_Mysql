package com.archive.app.reminders;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.archive.app.db.ScheduleContract;
import com.archive.app.db.ScheduleDbHelper;
import com.archive.app.reminders.IflytekTtsHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderService extends Service implements TextToSpeech.OnInitListener {

    private static final String TAG = "ReminderService";
    private static final String UTTERANCE_ID = "reminder_utterance";

    private TextToSpeech tts;
    private ScheduleDbHelper dbHelper;
    private String textToSpeak;
    private boolean isTtsInitialized = false;
    private boolean isFallbackActive = false;
    private IflytekTtsHelper iflytekHelper;
    private boolean isUseTTS = false;

    // --- 新增: 前台服务相关常量 ---
    private static final int FOREGROUND_NOTIFICATION_ID = 101;
    private static final String NOTIFICATION_CHANNEL_ID = "reminder_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this, this);
        dbHelper = new ScheduleDbHelper(this);
        // --- 新增: 创建通知渠道 ---
        createNotificationChannel();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null) {
            long scheduleId = intent.getLongExtra(ReminderManager.EXTRA_SCHEDULE_ID, -1);
            Log.d(TAG, "onStartCommand: scheduleId=" + scheduleId);
            if (scheduleId != -1) {
                loadScheduleAndPrepareSpeech(scheduleId);

                // --- 修改: 将服务提升到前台 ---
                // 创建一个简单的通知
                Notification notification = createNotification("正在准备日程提醒...");
                // 启动前台服务
                startForeground(FOREGROUND_NOTIFICATION_ID, notification);
                // ----------------------------
                loadScheduleAndPrepareSpeech(scheduleId);

            } else {
                Log.w(TAG, "Invalid schedule ID");
                stopSelf();
            }
        } else {
            Log.w(TAG, "Received null intent");
            stopSelf();
        }
        return START_NOT_STICKY;
    }


    // --- 新增: 创建通知渠道的方法 ---
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "日程提醒";
            String description = "用于播报日程提醒的通知";
            int importance = NotificationManager.IMPORTANCE_HIGH; // 使用 LOW 可以避免声音和振动
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // --- 新增: 创建通知的辅助方法 ---
    private Notification createNotification(String contentText) {
        // 为了简单，这里不设置点击通知后的跳转 Intent，在实际应用中最好设置
        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("日程提醒")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // 【重要】请替换为你自己的应用图标！
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    // --- 新增: 统一的停止服务方法 ---
    private void stopServiceAndForeground() {
        stopForeground(true); // 停止前台状态并移除通知
        stopSelf(); // 停止服务自身
    }

    private void loadScheduleAndPrepareSpeech(long scheduleId) {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(
                     ScheduleContract.ScheduleEntry.TABLE_NAME,
                     null,
                     ScheduleContract.ScheduleEntry._ID + " = ?",
                     new String[]{String.valueOf(scheduleId)},
                     null, null, null
             )) {

            if (cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_LOCATION));
                long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME));

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String timeStr = timeFormat.format(new Date(startTime));

                textToSpeak = "yhw 提醒您，您的日程 '" + title + "' 将于 " + timeStr + " 在 " + location + " 开始。";
                Log.d(TAG, "Loaded schedule: " + textToSpeak);
            }
        }


        Log.d(TAG, "Loaded isTtsInitialized: " + isTtsInitialized);
        if (textToSpeak != null && !textToSpeak.isEmpty()) {
            if (isTtsInitialized || !isUseTTS) {
                speak();
            }
        } else {
            Log.w(TAG, "No text to speak for schedule ID: " + scheduleId);
            stopSelf();
        }
    }

    private void speak() {
        Log.d(TAG, "Speaking enter: " + isUseTTS);
        if (!isUseTTS) {
            triggerFallbackTts();
            return;
        }
        Log.d(TAG, "Speaking enter: " + textToSpeak);
        if (tts != null && textToSpeak != null && !textToSpeak.isEmpty()) {
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);
            int result = tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID);
            Log.d(TAG, "Speaking result: " + result);
            if (result == TextToSpeech.ERROR) {
                Log.e(TAG, "Standard TTS speak() returned ERROR. Triggering fallback.");
                triggerFallbackTts();
            }
        } else {
            Log.e(TAG, "No text to speak or TTS not initialized.");
            if (tts != null) {
                stopSelf();
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (!isUseTTS) {
            return;
        }

        Log.d(TAG, "TTS Initialized." + status);
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true;
            tts.setLanguage(Locale.CHINESE);

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    if (UTTERANCE_ID.equals(utteranceId)) {
                        stopSelf();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e(TAG, "Standard TTS UtteranceProgressListener onError.");
                    if (UTTERANCE_ID.equals(utteranceId)) {
                        triggerFallbackTts();
                    }
                }
            });

            if (textToSpeak != null && !textToSpeak.isEmpty()) {
                speak();
            }
        } else {
            Log.e(TAG, "TTS Initialization failed. Triggering fallback.");
            /*TTS引擎本身的问题：TTS引擎本质上是手机里的一个独立应用。这个应用可能会出现以下问题：
            应用崩溃或无响应：TTS服务可能在后台崩溃了。
            数据损坏：TTS引擎的应用数据可能已损坏。
            版本过旧或不兼容：在一些老旧的设备上，TTS引擎的版本可能太低，无法正常工作。*/
            isTtsInitialized = true;
            triggerFallbackTts();
        }
    }

    private synchronized void triggerFallbackTts() {
        Log.d(TAG, "yhw triggerFallbackTts: isFallbackActive " + isFallbackActive);
        /*if (isFallbackActive) {
            return;
        }
        isFallbackActive = true;*/
        Log.i(TAG, "Standard TTS failed. Triggering fallback to iFlytek TTS.");

        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }

        if (textToSpeak == null || textToSpeak.isEmpty()) {
            Log.w(TAG, "No text to speak, stopping service.");
            stopSelf();
            return;
        }

        iflytekHelper = new IflytekTtsHelper();
        Log.d(TAG, "Triggering iFlytek TTS.");
        iflytekHelper.speak(textToSpeak, new IflytekTtsHelper.TtsCompletionListener() {
            @Override
            public void onComplete() {
                Log.d(TAG, "iFlytek TTS playback completed.");
                stopSelf();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "iFlytek TTS failed: " + error);
                stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (iflytekHelper != null) {
            iflytekHelper.shutdown();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 