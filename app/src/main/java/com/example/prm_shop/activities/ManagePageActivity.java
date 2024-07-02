package com.example.prm_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.R;
import com.example.prm_shop.models.response.RegisterUserResponse;
import com.example.prm_shop.models.response.StatisticsResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.StatisticService;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ManagePageActivity extends FooterActivity {

    private TextView tvTotalMembers, tvTotalProducts, tvTotalTransactions, tvTotalRevenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_page);

        tvTotalMembers = findViewById(R.id.tvTotalMembers);
        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);

        fetchStatistics();

        findViewById(R.id.imageUser).setOnClickListener(v -> {
            startActivity(new Intent(ManagePageActivity.this, UserListActivity.class));
        });

        findViewById(R.id.imageSetting).setOnClickListener(v -> {
            startActivity(new Intent(ManagePageActivity.this, SettingActivity.class));
            finish();
        });

        TextView tvTotalMembers = findViewById(R.id.tvTotalMembers);
        TextView tvTotalProducts = findViewById(R.id.tvTotalProducts);
        TextView tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
        TextView tvTotalRevenue = findViewById(R.id.tvTotalRevenue);

        // Set onClickListeners for each TextView
        tvTotalMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagePageActivity.this, UserListActivity.class);
                startActivity(intent);
            }
        });


        ImageView imageProduct = findViewById(R.id.imageProduct);
        imageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagePageActivity.this, ProductOptionActivity.class);
                startActivity(intent);
                finish();  // Optional: Finish current activity if needed
            }
        });

        ImageView imageOrder = findViewById(R.id.imageOrder);
        imageOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagePageActivity.this, OrderAdminActivity.class);
                startActivity(intent);
                finish();  // Optional: Finish current activity if needed
            }
        });


        // Set onClickListeners for other TextViews as needed
        tvTotalProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click, start new Activity if needed
            }
        });


        tvTotalTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click, start new Activity if needed
            }
        });

        tvTotalRevenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click, start new Activity if needed
            }
        });
    }

    private void fetchStatistics() {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        StatisticService statisticService = retrofit.create(StatisticService.class);

        Call<StatisticsResponse> call = statisticService.getStatistics();
        call.enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, Response<StatisticsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatisticsResponse statistics = response.body();
                    tvTotalMembers.setText("Total Members: " + statistics.getTotalMembers());
                    tvTotalProducts.setText("Total Products: " + statistics.getTotalProducts());
                    tvTotalTransactions.setText("Total Transactions: " + statistics.getTotalTransactions());
                    tvTotalRevenue.setText("Total Revenue: $" + statistics.getTotalRevenue());
                } else {
                    Toast.makeText(ManagePageActivity.this, "Failed to fetch statistics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                Toast.makeText(ManagePageActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
