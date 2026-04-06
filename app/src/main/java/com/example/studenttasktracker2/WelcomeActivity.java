package com.example.studenttasktracker2;

import android.content.Intent;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnLoginWelcome = findViewById(R.id.btnLoginWelcome);
        TextView tvRegisterLink = findViewById(R.id.tvRegisterLink);
        TextView tvForgotPasswordWelcome = findViewById(R.id.tvForgotPasswordWelcome);

        btnLoginWelcome.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        tvRegisterLink.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        tvForgotPasswordWelcome.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }
}