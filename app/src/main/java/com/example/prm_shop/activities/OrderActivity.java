package com.example.prm_shop.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.prm_shop.Adapter.OrderAdapter;
import com.example.prm_shop.R;
import com.example.prm_shop.models.response.OrderResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.OrderService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private OrderService orderService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderAdapter = new OrderAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(orderAdapter);

        orderService = ApiClient.getRetrofitInstance().create(OrderService.class);

        String token = getToken();

        // Kiểm tra xem token có tồn tại không
        if (!token.isEmpty()) {
            // Giải mã token để lấy userId
            JWT jwt = new JWT(token);
            String userIdStr = jwt.getClaim("memberId").asString();
            if (userIdStr != null && !userIdStr.isEmpty()) {
                userId = Integer.parseInt(userIdStr);

                // Gọi service để lấy danh sách đơn hàng
                getOrderList(userId);
            } else {
                Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("TOKEN", "");
    }

    private void getOrderList(int userId) {
        Call<List<OrderResponse>> call = orderService.getOrdersByMemberId(userId);
        Log.d("OrderActivity", "Fetching orders for user ID: " + userId);
        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful()) {
                    List<OrderResponse> orders = response.body();
                    if (orders != null) {
                        orderAdapter.setData(orders);
                        Log.d("OrderActivity", "Orders loaded: " + orders.size());
                    } else {
                        Log.d("OrderActivity", "No orders found");
                        Toast.makeText(OrderActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("OrderActivity", "Failed to fetch orders");
                    Toast.makeText(OrderActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.e("OrderActivity", "Error fetching orders: " + t.toString());
                Toast.makeText(OrderActivity.this, "Error fetching orders: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
