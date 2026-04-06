package com.example.studenttasktracker2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ListView lvSearchResults;
    private SessionManager sessionManager;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        etSearch = findViewById(R.id.etSearch);
        Button btnSearch = findViewById(R.id.btnSearch);
        lvSearchResults = findViewById(R.id.lvSearchResults);

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        // Set button click
        btnSearch.setOnClickListener(v -> searchTasks());
    }

    private void searchTasks() {
        String query = etSearch.getText().toString().trim();

        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> results = new ArrayList<>();
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        ApiClient.get("/api/tasks?userId=" + userId + "&search=" + encoded, new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray tasks = response.optJSONArray("tasks");
                if (tasks != null) {
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.optJSONObject(i);
                        if (task == null) continue;
                        results.add(task.optString("title", "Untitled"));
                    }
                }

                if (results.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "No tasks found", Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, results);
                lvSearchResults.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}