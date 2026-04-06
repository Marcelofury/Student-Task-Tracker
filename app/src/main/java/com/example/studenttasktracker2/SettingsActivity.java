package com.example.studenttasktracker2;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchNotifications, switchReminders, switchSound, switchDarkMode, switchBackup;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TaskTrackerSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize switches
        switchNotifications = findViewById(R.id.switchNotifications);
        switchReminders = findViewById(R.id.switchReminders);
        switchSound = findViewById(R.id.switchSound);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchBackup = findViewById(R.id.switchBackup);

        // Load SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadSettings();

        // Set listeners
        setSwitchListener(switchNotifications, "notifications");
        setSwitchListener(switchReminders, "reminders");
        setSwitchListener(switchSound, "sound");
        setSwitchListener(switchDarkMode, "darkMode");
        setSwitchListener(switchBackup, "backup");
    }

    private void setSwitchListener(@SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw, String key) {
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting(key, isChecked);
            Toast.makeText(SettingsActivity.this,
                    sw.getText() + " set to " + (isChecked ? "ON" : "OFF"),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void saveSetting(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void loadSettings() {
        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications", true));
        switchReminders.setChecked(sharedPreferences.getBoolean("reminders", true));
        switchSound.setChecked(sharedPreferences.getBoolean("sound", true));
        switchDarkMode.setChecked(sharedPreferences.getBoolean("darkMode", false));
        switchBackup.setChecked(sharedPreferences.getBoolean("backup", true));
    }
}