package com.example.studenttasktracker2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDueDate;
    private Button btnSave;
    private SessionManager sessionManager;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        setupSession();
        setupListeners();
    }

    // Initialize UI
    private void initViews() {
        etTitle = findViewById(R.id.etTaskTitle);
        etDescription = findViewById(R.id.etTaskDescription);
        etDueDate = findViewById(R.id.etDueDate);
        btnSave = findViewById(R.id.btnSaveTask);
    }

    private void setupSession() {
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    // Setup listeners
    private void setupListeners() {

        etDueDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveTask());
    }

    // Date picker
    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    String date = day + "/" + (month + 1) + "/" + year;
                    etDueDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    // Save task
    private void saveTask() {

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            etTitle.setError(getString(R.string.error_title_required));
            etTitle.requestFocus();
            return;
        }

        if (dueDate.isEmpty()) {
            etDueDate.setError(getString(R.string.error_due_date_required));
            etDueDate.requestFocus();
            return;
        }

        if (userId == -1) {
            Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("userId", userId);
            body.put("title", title);
            body.put("description", description);
            body.put("due_date", dueDate);

            ApiClient.post("/api/tasks", body, new ApiClient.Callback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(AddTaskActivity.this, getString(R.string.task_saved), Toast.LENGTH_SHORT).show();
                    clearFields();
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(AddTaskActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            Toast.makeText(this, getString(R.string.task_failed), Toast.LENGTH_SHORT).show();
        }
    }

    // Clear inputs
    private void clearFields() {
        etTitle.setText("");
        etDescription.setText("");
        etDueDate.setText("");
    }
}