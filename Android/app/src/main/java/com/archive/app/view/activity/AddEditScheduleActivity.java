package com.archive.app.view.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.archive.app.R;
import com.archive.app.db.ScheduleContract;
import com.archive.app.db.ScheduleDbHelper;
import com.archive.app.model.Schedule;
import com.archive.app.reminders.ReminderManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditScheduleActivity extends AppCompatActivity {

    public static final String EXTRA_SCHEDULE_ID = "extra_schedule_id";

    private TextInputEditText editTextTitle;
    private TextInputEditText editTextLocation;
    private TextInputEditText editTextDescription;
    private Button buttonSave;
    private Button buttonSelectTime;
    private TextView textViewTime;
    private Spinner spinnerRepeatMode;
    private Spinner spinnerReminderType;


    private ScheduleDbHelper dbHelper;
    private long scheduleId = -1;
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_schedule);

        dbHelper = new ScheduleDbHelper(this);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextLocation = findViewById(R.id.edit_text_location);
        editTextDescription = findViewById(R.id.edit_text_description);
        buttonSave = findViewById(R.id.button_save);
        buttonSelectTime = findViewById(R.id.button_select_time);
        textViewTime = findViewById(R.id.text_view_time);
        spinnerRepeatMode = findViewById(R.id.spinner_repeat_mode);
        spinnerReminderType = findViewById(R.id.spinner_reminder_type);

        setupSpinners();
        updateDateTimeDisplay();

        if (getIntent().hasExtra(EXTRA_SCHEDULE_ID)) {
            scheduleId = getIntent().getLongExtra(EXTRA_SCHEDULE_ID, -1);
            if (scheduleId != -1) {
                loadScheduleData();
                spinnerRepeatMode.setEnabled(false); //  don't edit repeat mode for existing schedules
            }
        }

        buttonSelectTime.setOnClickListener(v -> showDateTimePicker());
        buttonSave.setOnClickListener(v -> saveSchedule());
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_modes, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeatMode.setAdapter(repeatAdapter);

        ArrayAdapter<CharSequence> reminderAdapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_types, android.R.layout.simple_spinner_item);
        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReminderType.setAdapter(reminderAdapter);
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                updateDateTimeDisplay();
            }, selectedDateTime.get(Calendar.HOUR_OF_DAY), selectedDateTime.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH), selectedDateTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        textViewTime.setText(sdf.format(selectedDateTime.getTime()));
    }


    private void loadScheduleData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ScheduleContract.ScheduleEntry.TABLE_NAME,
                null,
                ScheduleContract.ScheduleEntry._ID + " = ?",
                new String[]{String.valueOf(scheduleId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_TITLE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_LOCATION));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_DESCRIPTION));
            long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME));
            int reminderType = cursor.getInt(cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_NAME_REMINDER_TYPE));

            editTextTitle.setText(title);
            editTextLocation.setText(location);
            editTextDescription.setText(description);
            selectedDateTime.setTimeInMillis(startTime);
            spinnerReminderType.setSelection(reminderType);
            updateDateTimeDisplay();
        }
        cursor.close();
    }

    private void saveSchedule() {
        String title = editTextTitle.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        int repeatModePosition = spinnerRepeatMode.getSelectedItemPosition();
        int reminderTypePosition = spinnerReminderType.getSelectedItemPosition();

        if (title.isEmpty()) {
            Toast.makeText(this, "请输入日程标题", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            if (scheduleId == -1) {
                // Add new schedule(s)
                saveNewSchedule(db, title, location, description, repeatModePosition, reminderTypePosition);
            } else {
                // Update existing schedule
                updateExistingSchedule(db, title, location, description, reminderTypePosition);
            }
            db.setTransactionSuccessful();
            finish();
        } finally {
            db.endTransaction();
        }
    }

    private void saveNewSchedule(SQLiteDatabase db, String title, String location, String description, int repeatModePosition, int reminderTypePosition) {
        ContentValues values = new ContentValues();
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_TITLE, title);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_LOCATION, location);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_IS_COMPLETED, 0);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_IS_SYNCED, 0);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME, selectedDateTime.getTimeInMillis());
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_REPEAT_MODE, repeatModePosition);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_REMINDER_TYPE, reminderTypePosition);

        long newRowId = db.insert(ScheduleContract.ScheduleEntry.TABLE_NAME, null, values);

        System.out.println("repeatModePosition: " + repeatModePosition);
        System.out.println("reminderTypePosition: " + reminderTypePosition);
        System.out.println("newRowId: " + newRowId);
        if (newRowId != -1) {
            Toast.makeText(this, "日程已保存", Toast.LENGTH_SHORT).show();
            if (reminderTypePosition > 0) {
                Schedule schedule = new Schedule();
                schedule.setId(newRowId);
                schedule.setStartTime(selectedDateTime.getTime());
                schedule.setReminderType(reminderTypePosition);
                new ReminderManager(this).setReminder(schedule);
            }
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateExistingSchedule(SQLiteDatabase db, String title, String location, String description, int reminderTypePosition) {
        ContentValues values = new ContentValues();
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_TITLE, title);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_LOCATION, location);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_START_TIME, selectedDateTime.getTimeInMillis());
        values.put(ScheduleContract.ScheduleEntry.COLUMN_NAME_REMINDER_TYPE, reminderTypePosition);

        int count = db.update(
                ScheduleContract.ScheduleEntry.TABLE_NAME,
                values,
                ScheduleContract.ScheduleEntry._ID + " = ?",
                new String[]{String.valueOf(scheduleId)}
        );
        if (count > 0) {
            Toast.makeText(this, "日程已更新", Toast.LENGTH_SHORT).show();
            Schedule schedule = new Schedule();
            schedule.setId(scheduleId);
            schedule.setStartTime(selectedDateTime.getTime());
            schedule.setReminderType(reminderTypePosition);
            new ReminderManager(this).setReminder(schedule);
        } else {
            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
        }
    }
} 