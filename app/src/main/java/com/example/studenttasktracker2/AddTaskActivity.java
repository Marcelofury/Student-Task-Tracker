package com.example.studenttasktracker2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDueDate;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        setupDatabase();
        setupListeners();
    }

    // Initialize UI
    private void initViews() {
        etTitle = findViewById(R.id.etTaskTitle);
        etDescription = findViewById(R.id.etTaskDescription);
        etDueDate = findViewById(R.id.etDueDate);
        btnSave = findViewById(R.id.btnSaveTask);
    }

    // Initialize database
    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
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

        // Insert into database
        boolean isInserted = dbHelper.insertTask(title, description, dueDate);

        if (isInserted) {

            Toast.makeText(this, getString(R.string.task_saved), Toast.LENGTH_SHORT).show();

            clearFields();

            finish(); // back to dashboard

        } else {

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