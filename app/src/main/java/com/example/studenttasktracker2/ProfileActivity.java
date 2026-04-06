package com.example.studenttasktracker2;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvStudentId, tvCourse, tvMotivation;
    private int currentUserId = -1;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvCourse = findViewById(R.id.tvCourse);
        tvMotivation = findViewById(R.id.tvMotivation);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Load user profile
        loadUserProfile();

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
    }

    private void loadUserProfile() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query first user in the table
        Cursor cursor = db.rawQuery("SELECT id, name, email FROM users LIMIT 1", null);

        if (cursor.moveToFirst()) {
            // Get column values
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            currentUserId = userId;
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String studentId = "STU-" + userId;
            String course = "Not set";

            // Set values using string resources
            tvUserName.setText(getString(R.string.name_label, name));
            tvUserEmail.setText(getString(R.string.email_label, email));
            tvStudentId.setText(getString(R.string.student_id_label, studentId));
            tvCourse.setText(getString(R.string.course_label, course));
            tvMotivation.setText(getString(R.string.motivation_text, name));

        } else {
            Toast.makeText(this, R.string.no_user_data, Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void showEditProfileDialog() {
        if (currentUserId == -1) {
            Toast.makeText(this, "No user to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        final EditText inputName = new EditText(this);
        inputName.setHint("Full name");
        inputName.setText(tvUserName.getText().toString().replaceFirst("^Name:\\s*", ""));

        final EditText inputEmail = new EditText(this);
        inputEmail.setHint("Email");
        inputEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.setText(tvUserEmail.getText().toString().replaceFirst("^Email:\\s*", ""));

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);
        container.addView(inputName);
        container.addView(inputEmail);

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(container)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = inputName.getText().toString().trim();
                    String newEmail = inputEmail.getText().toString().trim();

                    if (newName.isEmpty()) {
                        Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                        Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean updated = dbHelper.updateUserProfile(currentUserId, newName, newEmail);
                    if (updated) {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                        loadUserProfile();
                    } else {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}