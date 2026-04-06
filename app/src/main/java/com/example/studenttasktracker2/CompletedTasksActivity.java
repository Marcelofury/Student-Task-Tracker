package com.example.studenttasktracker2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CompletedTasksActivity extends AppCompatActivity {

    private ListView completedList;
    private SessionManager sessionManager;
    private long userId;
    private final ArrayList<Long> completedTaskIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        completedList = findViewById(R.id.completedList);
        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        loadCompletedTasks();

        btnDeleteAll.setOnClickListener(v -> deleteAllCompletedTasks());
    }

    /**
     * Load all completed tasks from database and display in ListView
     */
    private void loadCompletedTasks() {
        ArrayList<String> taskList = new ArrayList<>();
        completedTaskIds.clear();
        ApiClient.get("/api/tasks/completed?userId=" + userId, new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray tasks = response.optJSONArray("tasks");
                if (tasks != null) {
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.optJSONObject(i);
                        if (task == null) continue;
                        completedTaskIds.add(task.optLong("id"));
                        taskList.add(task.optString("title", "Untitled"));
                    }
                }

                if (taskList.isEmpty()) {
                    taskList.add("No completed tasks yet!");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(CompletedTasksActivity.this, android.R.layout.simple_list_item_1, taskList);
                completedList.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(CompletedTasksActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Delete all completed tasks using DatabaseHelper.deleteTask()
     */
    private void deleteAllCompletedTasks() {
        if (completedTaskIds.isEmpty()) {
            Toast.makeText(this, "No completed tasks to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        final int[] deletedCount = {0};
        final int total = completedTaskIds.size();

        for (Long taskId : new ArrayList<>(completedTaskIds)) {
            ApiClient.delete("/api/tasks/" + taskId, new ApiClient.Callback() {
                @Override
                public void onSuccess(JSONObject response) {
                    deletedCount[0]++;
                    if (deletedCount[0] == total) {
                        Toast.makeText(CompletedTasksActivity.this, "All completed tasks deleted!", Toast.LENGTH_SHORT).show();
                        loadCompletedTasks();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(CompletedTasksActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}