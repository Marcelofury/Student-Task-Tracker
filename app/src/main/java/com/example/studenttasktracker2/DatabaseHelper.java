package com.example.studenttasktracker2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TaskTracker.db";
    private static final int DB_VERSION = 2;

    // USERS TABLE
    private static final String TABLE_USERS = "users";

    // TASKS TABLE
    private static final String TABLE_TASKS = "tasks";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        // Tasks Table
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "due_date TEXT, " +
                "status TEXT DEFAULT 'Pending')";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

        onCreate(db);
    }

    // =========================
    // USER METHODS
    // =========================

    public boolean insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("email", email);
        values.put("password", password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=?",
                new String[]{email, password}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE email=?",
                new String[]{email}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int rowsUpdated = db.update(TABLE_USERS, values, "email=?", new String[]{email});
        return rowsUpdated > 0;
    }

    public boolean updateUserProfile(int userId, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);

        try {
            int rowsUpdated = db.update(TABLE_USERS, values, "id=?", new String[]{String.valueOf(userId)});
            return rowsUpdated > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // =========================
    // TASK METHODS
    // =========================

    // Insert Task
    public boolean insertTask(String title, String description, String dueDate) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("description", description);
        values.put("due_date", dueDate);
        values.put("status", "Pending");

        long result = db.insert(TABLE_TASKS, null, values);
        return result != -1;
    }

    // Get All Tasks
    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TASKS + " ORDER BY id DESC", null);
    }

    // Get Completed Tasks
    public Cursor getCompletedTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_TASKS + " WHERE status='Completed'",
                null
        );
    }

    // Update Task Status (Mark Complete)
    public boolean updateTaskStatus(int taskId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", status);

        int result = db.update(TABLE_TASKS, values, "id=?", new String[]{String.valueOf(taskId)});
        return result > 0;
    }

    // Delete Task
    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS, "id=?", new String[]{String.valueOf(taskId)});
        return result > 0;
    }
}