package com.example.prm_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.R;

public class ManagePageActivity extends AppCompatActivity {

    private ImageView imageUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_page);

        imageUser = findViewById(R.id.imageUser);
        imageUser.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePageActivity.this, UserListActivity.class);
            startActivity(intent);
        });

        ImageView imageSetting = findViewById(R.id.imageSetting);
        imageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagePageActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();  // Optional: Finish current activity if needed
            }
        });


    }
}
