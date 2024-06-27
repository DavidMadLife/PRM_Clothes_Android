package com.example.prm_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.R;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


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
                Intent intent = new Intent(UserActivity.this, BlankActivity.class);
                startActivity(intent);
                finish();  // Finish the current activity
            }
        });
    }
}
