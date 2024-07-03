package com.example.prm_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.R;

public class FooterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupFooter();
    }

    private void setupFooter() {
        ImageView imageHome = findViewById(R.id.imageHome);
        if (imageHome != null) {
            imageHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FooterActivity.this, ManagePageActivity.class);
                    startActivity(intent);
                }
            });
        }

        ImageView imageUser = findViewById(R.id.imageUser);
        if (imageUser != null) {
            imageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FooterActivity.this, UserListActivity.class);
                    startActivity(intent);
                }
            });
        }

        ImageView imageProduct = findViewById(R.id.imageProduct);
        if (imageProduct != null) {
            imageProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FooterActivity.this, ProductOptionActivity.class);
                    startActivity(intent);
                }
            });
        }

        ImageView imageOrder = findViewById(R.id.imageOrder);
        if (imageOrder != null) {
            imageOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FooterActivity.this, OrderActivity.class);
                    startActivity(intent);
                }
            });
        }

        ImageView imageSetting = findViewById(R.id.imageSetting);
        if (imageSetting != null) {
            imageSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FooterActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
