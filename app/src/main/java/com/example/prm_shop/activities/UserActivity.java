package com.example.prm_shop.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.example.prm_shop.MainActivity;
import com.example.prm_shop.R;

public class UserActivity extends BaseActivity {

    private TextView managePageTextView;
    private ImageView logoutImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        managePageTextView = findViewById(R.id.managePageTextView);
        checkUserRole();

        managePageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToManagePage();
            }
        });

        // Find TextView for User Information section
        TextView userInfoTextView = findViewById(R.id.userInfoTextView);
        userInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to UserInformationActivity
                Intent intent = new Intent(UserActivity.this, UserInformationActivity.class);
                startActivity(intent);
            }
        });

        ImageView homeButton = findViewById(R.id.imageHome);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, ProductActivity.class);
                startActivity(intent);
                finish();  // Finish the current activity
            }
        });

        logoutImageView = findViewById(R.id.logoutImageView);

        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        TextView orderTextView = findViewById(R.id.orderTextView);
        orderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to OrderListActivity
                Intent intent = new Intent(UserActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkUserRole() {
        String token = getToken();

        if (!token.isEmpty()) {
            JWT jwt = new JWT(token);
            String role = jwt.getClaim("role").asString();

            if ("Admin".equalsIgnoreCase(role)) {
                managePageTextView.setVisibility(View.VISIBLE); // Show the button if the role is Admin
            } else {
                managePageTextView.setVisibility(View.GONE); // Hide the button for non-admin users
            }
        } else {
            Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("TOKEN", "");
    }

    private void navigateToManagePage() {
        Intent intent = new Intent(UserActivity.this, ManagePageActivity.class);
        startActivity(intent);
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform logout operations here
                        Toast.makeText(UserActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

                        // Clear token from SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("TOKEN");
                        editor.apply();

                        // Redirect to MainActivity
                        Intent intent = new Intent(UserActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                        startActivity(intent);
                        finish();  // Finish the current activity
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
