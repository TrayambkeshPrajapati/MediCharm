package com.example.medicharm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicharm.database.Reminder;
import com.example.medicharm.database.ReminderDatabase;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    private EditText editMedicineName, editDosage;
    private TimePicker timePicker;
    private Button btnSaveReminder;
    private int reminderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        editMedicineName = findViewById(R.id.editMedicineName);
        editDosage = findViewById(R.id.editDosage);
        timePicker = findViewById(R.id.timePicker);
        btnSaveReminder = findViewById(R.id.btnSaveReminder);

        timePicker.setIs24HourView(true);

        // Check if editing an existing reminder
        reminderId = getIntent().getIntExtra("reminderId", -1);
        if (reminderId != -1) {
            new Thread(() -> {
                Reminder reminder = ReminderDatabase.getInstance(getApplicationContext())
                        .reminderDao().getReminderById(reminderId);
                runOnUiThread(() -> {
                    if (reminder != null) {
                        editMedicineName.setText(reminder.medicineName);
                        editDosage.setText(reminder.dosage);
                        String[] timeParts = reminder.time.split(":");
                        timePicker.setHour(Integer.parseInt(timeParts[0]));
                        timePicker.setMinute(Integer.parseInt(timeParts[1]));
                    }
                });
            }).start();
        }

        btnSaveReminder.setOnClickListener(v -> saveReminder());
    }

    private void saveReminder() {
        String medicine = editMedicineName.getText().toString().trim();
        String dosage = editDosage.getText().toString().trim();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        if (medicine.isEmpty()) {
            Toast.makeText(this, "Enter medicine name", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            ReminderDatabase db = ReminderDatabase.getInstance(getApplicationContext());
            Reminder reminder;
            if (reminderId != -1) {
                reminder = db.reminderDao().getReminderById(reminderId);
                reminder.medicineName = medicine;
                reminder.dosage = dosage;
                reminder.time = formatTime(hour, minute);
                db.reminderDao().update(reminder);
            } else {
                reminder = new Reminder();
                reminder.medicineName = medicine;
                reminder.dosage = dosage;
                reminder.time = formatTime(hour, minute);
                reminder.repeatDaily = true;
                reminder.notes = "";
                db.reminderDao().insert(reminder);
            }

            // Set alarm
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            setAlarm(calendar, reminder.medicineName, reminder.dosage);

            runOnUiThread(() -> {
                Toast.makeText(this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private String formatTime(int hour, int minute) {
        return hour + ":" + (minute < 10 ? "0" + minute : minute);
    }

    private void setAlarm(Calendar calendar, String medicine, String dosage) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int requestCode = (int) System.currentTimeMillis(); // unique ID for PendingIntent
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicineName", medicine);
        intent.putExtra("dosage", dosage);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
