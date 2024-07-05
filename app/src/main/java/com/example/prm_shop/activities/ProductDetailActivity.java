package com.example.prm_shop.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.example.prm_shop.Adapter.CartAdapter;
import com.example.prm_shop.R;
import com.example.prm_shop.models.request.CartRequest;
import com.example.prm_shop.models.response.CartResponse;
import com.example.prm_shop.models.response.ProductResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.CartService;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends BaseActivity {

    private TextView unitInStock, productName, productPrice, productDescription, quantityTextView;
    private ImageView productImage;
    private Button addToCartButton, incrementButton, decrementButton;
    private RadioGroup sizeRadioGroup;
    private int quantity = 1;
    private ProductResponse product;

    private  int userId;

    private CartAdapter cartAdapter;
    private TextView textTotal;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        product = (ProductResponse) getIntent().getSerializableExtra("product");

        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        unitInStock = findViewById(R.id.product_unitInStock);
        productDescription = findViewById(R.id.product_description);
        quantityTextView = findViewById(R.id.quantity_text_view);
        productImage = findViewById(R.id.product_image);
        addToCartButton = findViewById(R.id.add_to_cart_button);
        incrementButton = findViewById(R.id.increment_button);
        decrementButton = findViewById(R.id.decrement_button);
        sizeRadioGroup = findViewById(R.id.size_radio_group);

        productName.setText(product.getProductName());
        productPrice.setText("Price: " + product.getUnitPrice());
        unitInStock.setText("Unit In Stock: " + product.getUnitsInStock());
        productDescription.setText("Description\n" + product.getDescription());
        Picasso.get().load(product.getImg()).into(productImage);

        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    quantityTextView.setText(String.valueOf(quantity));
                }
            }
        });

        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });


    }

    private void addToCart() {
        // Lấy token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "");

        // Kiểm tra xem token có tồn tại không
        if (!token.isEmpty()) {
            // Giải mã token để lấy memberId
            JWT jwt = new JWT(token);
            int memberId = jwt.getClaim("memberId").asInt(); // Đảm bảo "memberId" là key trong token của bạn

            // Lấy productId từ intent hoặc từ đâu đó
            int productId = product.getProductId(); // Thay thế bằng cách lấy productId từ object ProductResponse

            // Lấy size được chọn từ RadioGroup
            int checkedRadioButtonId = sizeRadioGroup.getCheckedRadioButtonId();
            String size = "";

            if (checkedRadioButtonId == -1) {
                // Nếu không có RadioButton nào được chọn
                Toast.makeText(this, "Please choose size.", Toast.LENGTH_SHORT).show();
                return; // Dừng phương thức addToCart() nếu không có size được chọn
            }else{
                if (checkedRadioButtonId == R.id.size_s) {
                    size = "S";
                } else if (checkedRadioButtonId == R.id.size_m) {
                    size = "M";
                } else if (checkedRadioButtonId == R.id.size_l) {
                    size = "L";
                } else if (checkedRadioButtonId == R.id.size_xl) {
                    size = "XL";
                } else if (checkedRadioButtonId == R.id.size_xxl) {
                    size = "XXL";
                }
            }
            // Lấy số lượng từ TextView quantity_text_view
            int quantity = Integer.parseInt(quantityTextView.getText().toString());

            // Tạo đối tượng CartRequest để gửi lên server
            CartRequest cartRequest = new CartRequest(memberId, productId, quantity, size);

            // Gọi API để thêm vào giỏ hàng
            CartService cartService = ApiClient.getRetrofitInstance().create(CartService.class);
            Call<Void> call = cartService.addToCart(cartRequest);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        fetchCartData();
                        Toast.makeText(ProductDetailActivity.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                // Đọc thông báo lỗi từ errorBody của response
                                String errorMessage = response.errorBody().string();
                                // Xử lý để chỉ lấy nội dung thông báo lỗi từ chuỗi JSON
                                JSONObject jsonObject = new JSONObject(errorMessage);
                                String message = jsonObject.getString("message");

                                // Hiển thị thông báo lỗi
                                Toast.makeText(ProductDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show();
        }
    }
    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("TOKEN", "");
    }
    public void fetchCartData() {
        CartService cartService = ApiClient.getRetrofitInstance().create(CartService.class);

        String token = getToken();

        // Check if token exists
        if (!token.isEmpty()) {
            // Decode token to get userId
            JWT jwt = new JWT(token);
            String userIdStr = jwt.getClaim("memberId").asString();
            if (userIdStr != null && !userIdStr.isEmpty()) {
                userId = Integer.parseInt(userIdStr);

                // Call API to get cart data
                Call<CartResponse> call = cartService.getCart(userId);
                call.enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful()) {
                            CartResponse cartResponse = response.body();
                            if (cartResponse != null && cartResponse.getItems() != null) {
                                cartAdapter.setItems(cartResponse.getItems());
                                textTotal.setText("Total: $" + cartResponse.getTotal());
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

            } else {
                Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show();
        }
    }

}
