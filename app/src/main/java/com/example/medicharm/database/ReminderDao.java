package com.example.medicharm.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM reminder_table")
    List<Reminder> getAllReminders();

    @Query("SELECT * FROM reminder_table WHERE id = :id LIMIT 1")
    Reminder getReminderById(int id);

    // ✅ Delete all reminders
    @Query("DELETE FROM reminder_table")
    void deleteAll();
}
