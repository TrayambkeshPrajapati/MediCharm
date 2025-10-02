package com.example.medicharm.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminder_table")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String medicineName;
    public String dosage;
    public String time;
    public boolean repeatDaily;
    public String notes;
}
