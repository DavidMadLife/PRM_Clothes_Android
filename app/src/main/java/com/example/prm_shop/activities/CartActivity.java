package com.example.prm_shop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.Adapter.CartAdapter;
import com.example.prm_shop.R;
import com.example.prm_shop.models.response.CartResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.CartService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_cart_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter();
        recyclerView.setAdapter(cartAdapter);

        // Gọi API để lấy thông tin giỏ hàng và cập nhật RecyclerView
        int memberId = 6; // Thay thế bằng memberId thực tế
        CartService cartService = ApiClient.getRetrofitInstance().create(CartService.class);

        Call<CartResponse> call = cartService.getCart(memberId);
        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    CartResponse cartResponse = response.body();
                    if (cartResponse != null && cartResponse.getItems() != null) {
                        cartAdapter.setItems(cartResponse.getItems());
                        Log.d("CartActivity", "Number of items in cart: " + cartResponse.getItems().size());
                    } else {
                        Log.e("CartActivity", "Cart response or items are null");
                    }
                } else {
                    Log.e("CartActivity", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e("CartActivity", "Error fetching cart: " + t.getMessage());
            }
        });

        /*Button btnCheckout = findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng click vào nút thanh toán
            }
        });*/
    }
}
