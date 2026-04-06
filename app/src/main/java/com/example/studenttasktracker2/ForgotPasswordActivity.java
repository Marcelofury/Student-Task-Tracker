package com.example.studenttasktracker2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail, etNewPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            // Validation
            if (email.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("newPassword", newPass);

                ApiClient.post("/api/auth/forgot-password", body, new ApiClient.Callback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(ForgotPasswordActivity.this, "Password Reset Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                Toast.makeText(this, "Failed to build reset request", Toast.LENGTH_SHORT).show();
            }
        });
    }
}