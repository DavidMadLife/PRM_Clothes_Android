package com.example.prm_shop.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.MainActivity;
import com.example.prm_shop.R;


public class SettingActivity extends AppCompatActivity {

    private TextView homePageView;
    private ImageView logoutImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        homePageView = findViewById(R.id.homePageView);
        logoutImageView = findViewById(R.id.logoutImageView);

        homePageView.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, ProductActivity.class);
            startActivity(intent);
            finish();  // Finish the current activity
        });

        logoutImageView.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform logout operations here
                    Toast.makeText(SettingActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

                    // Clear token from SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("TOKEN");
                    editor.apply();

                    // Redirect to MainActivity
                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                    startActivity(intent);
                    finish();  // Finish the current activity
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
