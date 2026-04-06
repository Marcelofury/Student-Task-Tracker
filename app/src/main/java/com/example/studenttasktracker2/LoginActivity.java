package com.example.studenttasktracker2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister, tvForgotPassword;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Link XML components
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        sessionManager = new SessionManager(this);

        // Login Button
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation
            if (email.isEmpty()) {
                etEmail.setError("Enter email");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Valid email required");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter password");
                return;
            }

            try {
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);

                ApiClient.post("/api/auth/login", body, new ApiClient.Callback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        JSONObject user = response.optJSONObject("user");
                        if (user == null) {
                            Toast.makeText(LoginActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        long userId = user.optLong("id", -1);
                        String userName = user.optString("name", "");
                        String userEmail = user.optString("email", email);
                        sessionManager.saveUser(userId, userName, userEmail);

                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                Toast.makeText(LoginActivity.this, "Failed to build login request", Toast.LENGTH_SHORT).show();
            }
        });

        // Go to Register Screen
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        // Go to Forgot Password Screen
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }
}