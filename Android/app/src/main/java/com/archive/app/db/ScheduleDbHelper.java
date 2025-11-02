// ScheduleDbHelper.java
package com.archive.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleDbHelper extends SQLiteOpenHelper {
    // 数据库版本号 +1，用于触发 onUpgrade
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Schedule.db";

    // 原有的 Schedule 表的 SQL
    private static final String SQL_CREATE_SCHEDULE_TABLE =
            "CREATE TABLE " + ScheduleContract.ScheduleEntry.TABLE_NAME + " (" +
                    ScheduleContract.ScheduleEntry._ID + " INTEGER PRIMARY KEY," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_SERVER_ID + " INTEGER," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_TITLE + " TEXT," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_LOCATION + " TEXT," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME + " INTEGER," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_END_TIME + " INTEGER," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_REMINDER_TYPE + " INTEGER," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_VOICE_THEME + " TEXT," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_IS_COMPLETED + " INTEGER," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_REPEAT_MODE + " INTEGER DEFAULT 0," +
                    ScheduleContract.ScheduleEntry.COLUMN_NAME_IS_SYNCED + " INTEGER)";

    private static final String SQL_DELETE_SCHEDULE_TABLE =
            "DROP TABLE IF EXISTS " + ScheduleContract.ScheduleEntry.TABLE_NAME;

    // --- 新增 User 表的 SQL ---
    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserContract.UserEntry.COLUMN_NAME_USERNAME + " TEXT UNIQUE," + // 用户名通常是唯一的
                    UserContract.UserEntry.COLUMN_NAME_PASSWORD + " TEXT)";

    private static final String SQL_DELETE_USER_TABLE =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        // 创建两个表
        db.execSQL(SQL_CREATE_SCHEDULE_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + ScheduleContract.ScheduleEntry.TABLE_NAME +
                    " ADD COLUMN " + ScheduleContract.ScheduleEntry.COLUMN_NAME_REPEAT_MODE + " INTEGER DEFAULT 0");
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}