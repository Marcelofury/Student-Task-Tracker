
package com.example.studenttasktracker2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView taskListView;
    private SessionManager sessionManager;
    private long userId;
    private final ArrayList<Long> taskIds = new ArrayList<>();

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

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = sessionManager.getUserId();

        // Load tasks from backend
        loadTasks();

        // Tap a task to mark it completed
        taskListView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            if (position < 0 || position >= taskIds.size()) {
                return;
            }
            long taskId = taskIds.get(position);
            try {
                JSONObject body = new JSONObject();
                body.put("status", "Completed");
                ApiClient.patch("/api/tasks/" + taskId + "/status", body, new ApiClient.Callback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(MainActivity.this, "Task marked as completed", Toast.LENGTH_SHORT).show();
                        loadTasks();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
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
            sessionManager.clear();
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
        ApiClient.get("/api/tasks?userId=" + userId, new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray tasks = response.optJSONArray("tasks");
                if (tasks != null) {
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.optJSONObject(i);
                        if (task == null) continue;
                        String status = task.optString("status", "Pending");
                        if ("Completed".equalsIgnoreCase(status)) {
                            continue;
                        }
                        taskIds.add(task.optLong("id"));
                        taskTitles.add(task.optString("title", "Untitled"));
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, taskTitles);
                taskListView.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, taskTitles);
                taskListView.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh task list when returning to dashboard
        loadTasks();
    }
}