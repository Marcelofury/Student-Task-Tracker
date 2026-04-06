package com.example.studenttasktracker2;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";

    private TextView tvMonth, tvTaskCount;
    private DatabaseHelper dbHelper;

    private long selectedDateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendarView = findViewById(R.id.calendarView);
        tvMonth = findViewById(R.id.tvMonth);
        tvTaskCount = findViewById(R.id.tvTaskCount);
        Button btnReviewTasks = findViewById(R.id.btnReviewTasks);

        dbHelper = new DatabaseHelper(this);

        // Set initial date
        selectedDateMillis = calendarView.getDate();
        updateMonthText(selectedDateMillis);
        updateDueTasks(selectedDateMillis);

        // Listener for date change
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateMillis = getMillisFromDate(year, month, dayOfMonth);
            updateMonthText(selectedDateMillis);
            updateDueTasks(selectedDateMillis);
        });

        // Review button click
        btnReviewTasks.setOnClickListener(v -> Toast.makeText(this, "Feature to review tasks coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void updateMonthText(long dateMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonth.setText(sdf.format(new Date(dateMillis)));
    }

    @SuppressLint("SetTextI18n")
    private void updateDueTasks(long dateMillis) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        String dateString = sdf.format(new Date(dateMillis));

        int total = 0;
        try (Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) AS total FROM tasks WHERE due_date = ?",
                new String[]{dateString})) {

            if (cursor.moveToFirst()) {
                total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to query due tasks", e);
            Toast.makeText(this, "Error loading tasks", Toast.LENGTH_SHORT).show();
        }

        tvTaskCount.setText("Total Due Tasks Today: " + total);
    }

    private long getMillisFromDate(int year, int month, int day) {
        String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse date: " + dateStr, e);
            Toast.makeText(this, "Error parsing date", Toast.LENGTH_SHORT).show();
            return System.currentTimeMillis();
        }
    }
}