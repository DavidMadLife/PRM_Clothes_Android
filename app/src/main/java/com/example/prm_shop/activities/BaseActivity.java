package com.example.prm_shop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
        ImageView imageUser = findViewById(R.id.imageUser);
        if (imageUser != null) {
            imageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, UserActivity.class);
                    startActivity(intent);
                }
            });
            imageUser.setImageResource(R.drawable.user_normal);
            imageUser.setImageTintList(createColorStateList(R.color.black));
        }

        ImageView imageHome = findViewById(R.id.imageHome);
        if (imageHome != null) {
            imageHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, ProductActivity.class);
                    startActivity(intent);
                }
            });
            imageHome.setImageResource(R.drawable.home_product);
            imageHome.setImageTintList(createColorStateList(R.color.black));
        }

        ImageView imageMap = findViewById(R.id.imageMap);
        if (imageMap != null) {
            imageMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, MapActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupHeader() {
        ImageView imageViewCart = findViewById(R.id.imageViewCart);
        if (imageViewCart != null) {
            imageViewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            });
            imageViewCart.setImageResource(R.drawable.shopping_cart);
            imageViewCart.setImageTintList(createColorStateList(R.color.black));
        }

        ImageView imageMessage = findViewById(R.id.imageViewMessage);
        if (imageMessage != null) {
            imageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, AdminChatActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private ColorStateList createColorStateList(int colorRes) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{}
        };
        int[] colors = new int[]{
                ContextCompat.getColor(this, colorRes),
                ContextCompat.getColor(this, colorRes)
        };
        return new ColorStateList(states, colors);
    }
}
