package com.example.medicharm;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicharm.database.Reminder;
import com.example.medicharm.database.ReminderDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private ImageView addTime, menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addTime = findViewById(R.id.add_time);
        menuBtn = findViewById(R.id.menu_btn);

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
                startActivity(intent);
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });

        loadReminders();
    }

    private void loadReminders() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Reminder> reminders = ReminderDatabase.getInstance(getApplicationContext())
                        .reminderDao().getAllReminders();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ReminderAdapter(MainActivity.this, reminders, new ReminderAdapter.OnItemClickListener() {
                            @Override
                            public void onEdit(Reminder reminder) {
                                Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
                                intent.putExtra("reminderId", reminder.id);
                                startActivity(intent);
                            }

                            @Override
                            public void onDelete(Reminder reminder) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ReminderDatabase.getInstance(getApplicationContext())
                                                .reminderDao().delete(reminder);
                                        loadReminders();
                                    }
                                }).start();
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }

    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}
