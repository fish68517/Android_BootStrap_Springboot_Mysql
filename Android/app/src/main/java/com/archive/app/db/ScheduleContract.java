package com.archive.app.db;

import android.provider.BaseColumns;

public final class ScheduleContract {

    private ScheduleContract() {}

    public static class ScheduleEntry implements BaseColumns {
        public static final String TABLE_NAME = "schedules";
        public static final String COLUMN_NAME_SERVER_ID = "server_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_END_TIME = "end_time";
        public static final String COLUMN_NAME_REMINDER_TYPE = "reminder_type";
        public static final String COLUMN_NAME_VOICE_THEME = "voice_theme";
        public static final String COLUMN_NAME_IS_COMPLETED = "is_completed";
        public static final String COLUMN_NAME_IS_SYNCED = "is_synced";
        public static final String COLUMN_NAME_REPEAT_MODE = "repeat_mode";
    }
} 