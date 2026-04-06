package com.example.studenttasktracker2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    private ListView lvFilteredTasks;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Spinner spinnerFilter = findViewById(R.id.spinnerFilter);
        lvFilteredTasks = findViewById(R.id.lvFilteredTasks);
        dbHelper = new DatabaseHelper(this);

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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        if (filter.equals("All")) {
            cursor = db.rawQuery("SELECT title FROM tasks", null);
        } else {
            cursor = db.rawQuery("SELECT title FROM tasks WHERE status = ?", new String[]{filter});
        }

        ArrayList<String> taskList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            taskList.add(title);
        }
        cursor.close();

        if (taskList.isEmpty()) {
            Toast.makeText(this, "No tasks found for filter: " + filter, Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                taskList
        );
        lvFilteredTasks.setAdapter(listAdapter);
    }
}