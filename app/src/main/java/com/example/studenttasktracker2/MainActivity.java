
package com.example.studenttasktracker2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView taskListView;
    private DatabaseHelper dbHelper;
    private final ArrayList<Integer> taskIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        taskListView = findViewById(R.id.taskListView);

        Button btnAddTask = findViewById(R.id.btnAddTask);
        Button btnCompletedTasks = findViewById(R.id.btnCompletedTasks);
        Button btnSearchTasks = findViewById(R.id.btnSearchTasks);
        Button btnCalendar = findViewById(R.id.btnCalendar);
        Button btnTaskFilter = findViewById(R.id.btnTaskFilter);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnLogout = findViewById(R.id.btnLogout);

        dbHelper = new DatabaseHelper(this);

        // Load tasks from DB
        loadTasks();

        // Tap a task to mark it completed
        taskListView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            if (position < 0 || position >= taskIds.size()) {
                return;
            }
            int taskId = taskIds.get(position);
            boolean updated = dbHelper.updateTaskStatus(taskId, "Completed");
            if (updated) {
                Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show();
                loadTasks();
            } else {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
            }
        });

        // Button click listeners
        btnAddTask.setOnClickListener(v -> startActivity(new Intent(this, AddTaskActivity.class)));

        btnCompletedTasks.setOnClickListener(v -> startActivity(new Intent(this, CompletedTasksActivity.class)));

        btnSearchTasks.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));

        btnCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));

        btnTaskFilter.setOnClickListener(v -> startActivity(new Intent(this, FilterActivity.class)));

        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            // Go back to LoginActivity and clear task stack
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Load all tasks from the database and display in ListView
     */
    private void loadTasks() {
        ArrayList<String> taskTitles = new ArrayList<>();
        taskIds.clear();
        Cursor cursor = dbHelper.getAllTasks();

        if (cursor.moveToFirst()) {
            do {
                int taskId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                if ("Completed".equalsIgnoreCase(status)) {
                    continue;
                }
                taskIds.add(taskId);
                taskTitles.add(title);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskTitles);
        taskListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh task list when returning to dashboard
        loadTasks();
    }
}