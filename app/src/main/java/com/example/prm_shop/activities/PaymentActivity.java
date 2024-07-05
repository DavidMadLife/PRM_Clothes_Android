package com.example.prm_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.R;

public class PaymentActivity extends AppCompatActivity {

    private TextView textViewCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_successfull);

        textViewCountdown = findViewById(R.id.textViewCountdown);

        // Đếm ngược 5 giây và chuyển hướng
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                textViewCountdown.setText("Redirect after " + millisUntilFinished / 1000 + " seconds");
            }

            public void onFinish() {
                // Chuyển hướng về trang lịch sử đơn hàng
                Intent intent = new Intent(PaymentActivity.this, ProductActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
