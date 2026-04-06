package com.example.studenttasktracker2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    private ListView lvFilteredTasks;
    private SessionManager sessionManager;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Spinner spinnerFilter = findViewById(R.id.spinnerFilter);
        lvFilteredTasks = findViewById(R.id.lvFilteredTasks);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        // Load filter options into spinner (example: task status)
        ArrayList<String> filters = new ArrayList<>();
        filters.add("All");
        filters.add("Pending");
        filters.add("Completed");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filters
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        // Handle spinner selection
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = filters.get(position);
                loadFilteredTasks(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to all tasks
                loadFilteredTasks("All");
            }
        });
    }

    private void loadFilteredTasks(String filter) {
        ArrayList<String> taskList = new ArrayList<>();
        String path = "/api/tasks?userId=" + userId;
        if (!"All".equals(filter)) {
            path += "&status=" + filter;
        }

        ApiClient.get(path, new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray tasks = response.optJSONArray("tasks");
                if (tasks != null) {
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.optJSONObject(i);
                        if (task == null) continue;
                        taskList.add(task.optString("title", "Untitled"));
                    }
                }

                if (taskList.isEmpty()) {
                    Toast.makeText(FilterActivity.this, "No tasks found for filter: " + filter, Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter<String> listAdapter = new ArrayAdapter<>(FilterActivity.this, android.R.layout.simple_list_item_1, taskList);
                lvFilteredTasks.setAdapter(listAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(FilterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}