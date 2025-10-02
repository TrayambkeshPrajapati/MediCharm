package com.example.medicharm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicharm.database.Reminder;
import com.example.medicharm.database.ReminderDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private ImageView addTime, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addTime = findViewById(R.id.add_time);
        logoutBtn = findViewById(R.id.menu_btn); // renamed logically

        addTime.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> showLogoutPopup());

        loadReminders();
    }

    private void loadReminders() {
        new Thread(() -> {
            List<Reminder> reminders = ReminderDatabase.getInstance(getApplicationContext())
                    .reminderDao().getAllReminders();

            runOnUiThread(() -> {
                adapter = new ReminderAdapter(this, reminders, new ReminderAdapter.OnItemClickListener() {
                    @Override
                    public void onEdit(Reminder reminder) {
                        Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
                        intent.putExtra("reminderId", reminder.id);
                        startActivity(intent);
                    }

                    @Override
                    public void onDelete(Reminder reminder) {
                        new Thread(() -> {
                            ReminderDatabase.getInstance(getApplicationContext())
                                    .reminderDao().delete(reminder);
                            loadReminders();
                        }).start();
                    }
                });
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }

    private void showLogoutPopup() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finish();
                    Toast.makeText(MainActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
