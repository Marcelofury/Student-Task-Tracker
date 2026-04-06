package com.example.studenttasktracker2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ListView lvSearchResults;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        etSearch = findViewById(R.id.etSearch);
        Button btnSearch = findViewById(R.id.btnSearch);
        lvSearchResults = findViewById(R.id.lvSearchResults);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Set button click
        btnSearch.setOnClickListener(v -> searchTasks());
    }

    private void searchTasks() {
        String query = etSearch.getText().toString().trim();

        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT title FROM tasks WHERE title LIKE ?",
                new String[]{"%" + query + "%"}
        );

        ArrayList<String> results = new ArrayList<>();

        while (cursor.moveToNext()) {
            String taskTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            results.add(taskTitle);
        }
        cursor.close();

        if (results.isEmpty()) {
            Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
        }

        // Display results
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                results
        );
        lvSearchResults.setAdapter(adapter);
    }
}