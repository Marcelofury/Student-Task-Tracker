package com.example.studenttasktracker2;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CompletedTasksActivity extends AppCompatActivity {

    private ListView completedList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        completedList = findViewById(R.id.completedList);
        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);
        dbHelper = new DatabaseHelper(this);

        loadCompletedTasks();

        btnDeleteAll.setOnClickListener(v -> deleteAllCompletedTasks());
    }

    /**
     * Load all completed tasks from database and display in ListView
     */
    private void loadCompletedTasks() {
        ArrayList<String> taskList = new ArrayList<>();
        Cursor cursor = dbHelper.getCompletedTasks(); // Use DatabaseHelper method

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                taskList.add(title);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (taskList.isEmpty()) {
            taskList.add("No completed tasks yet!");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                taskList
        );
        completedList.setAdapter(adapter);
    }

    /**
     * Delete all completed tasks using DatabaseHelper.deleteTask()
     */
    private void deleteAllCompletedTasks() {
        Cursor cursor = dbHelper.getCompletedTasks(); // Get all completed tasks
        int deletedCount = 0;

        if (cursor.moveToFirst()) {
            do {
                int taskId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                boolean deleted = dbHelper.deleteTask(taskId); // Use helper method
                if (deleted) deletedCount++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (deletedCount > 0) {
            Toast.makeText(this, "All completed tasks deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No completed tasks to delete.", Toast.LENGTH_SHORT).show();
        }

        loadCompletedTasks(); // refresh list
    }
}