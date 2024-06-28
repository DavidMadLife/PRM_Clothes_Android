package com.example.prm_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupFooter();
        setupHeader();
    }

    private void setupFooter() {
        // This assumes the layout includes the footer with id imageUser
        ImageView imageUser = findViewById(R.id.imageUser);
        if (imageUser != null) {
            imageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, UserActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupHeader() {
        // This assumes the layout includes the footer with id imageUser
        ImageView imageViewCart = findViewById(R.id.imageViewCart);
        if (imageViewCart != null) {
            imageViewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
