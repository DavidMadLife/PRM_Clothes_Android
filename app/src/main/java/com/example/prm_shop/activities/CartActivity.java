package com.example.prm_shop.activities;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.prm_shop.Adapter.CartAdapter;
import com.example.prm_shop.R;
import com.example.prm_shop.models.request.CartRequest;
import com.example.prm_shop.models.request.RemoveItemRequest;
import com.example.prm_shop.models.response.CartItemResponse;
import com.example.prm_shop.models.response.CartResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.CartService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private int userId;
    private CartAdapter cartAdapter;
    private TextView textTotal;

    private TextView quantity_text_view;

    private RadioGroup sizeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_cart_items);
        textTotal = findViewById(R.id.text_total);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(new CartAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                // Lấy thông tin item cần xóa từ adapter
                CartItemResponse item = cartAdapter.getItems().get(position);
                // Gọi API để xóa item
                deleteCartItem(item.getProductId(), position);
            }

            @Override
            public void onUpdateClick(int position) {
                // TODO: Handle update item action
                //Toast.makeText(CartActivity.this, "Update clicked at position: " + position, Toast.LENGTH_SHORT).show();
                showUpdateDialog(position);
            }
        });
        recyclerView.setAdapter(cartAdapter);

        // Gọi API để lấy thông tin giỏ hàng và cập nhật RecyclerView

        CartService cartService = ApiClient.getRetrofitInstance().create(CartService.class);

        String token = getToken();

        // Kiểm tra xem token có tồn tại không
        if (!token.isEmpty()) {
            // Giải mã token để lấy userId
            JWT jwt = new JWT(token);
            String userIdStr = jwt.getClaim("memberId").asString();
            if (userIdStr != null && !userIdStr.isEmpty()) {
                userId = Integer.parseInt(userIdStr);

            } else {
                Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show();
        }

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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // Không hỗ trợ di chuyển
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    cartAdapter.showDeleteButton(position, true);
                    cartAdapter.showUpdateButton(position, true);// Hiển thị nút xóa khi vuốt sang trái
                } else if (direction == ItemTouchHelper.RIGHT) {
                    cartAdapter.showDeleteButton(position, false);
                    cartAdapter.showUpdateButton(position, false);// Ẩn nút xóa khi vuốt sang phải
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                // Thêm hiệu ứng cho hành động vuốt, nếu cần
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        Button btnClearCart = findViewById(R.id.btn_clear_cart);
        btnClearCart.setOnClickListener(v -> clearCart());
    }

    private String getToken() {
        // Lấy token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("TOKEN", "");
    }

    private void deleteCartItem(int productId, int position) {
        // Tạo đối tượng RemoveItemRequest để gửi lên server
        RemoveItemRequest request = new RemoveItemRequest(userId, productId);

        // Gọi API để xóa item khỏi giỏ hàng
        CartService cartService = ApiClient.getRetrofitInstance().create(CartService.class);
        Call<Void> call = cartService.removeCartItem(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa item khỏi RecyclerView
                    cartAdapter.removeItem(position);
                    Toast.makeText(CartActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error deleting item: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_quantity, null);
        builder.setView(dialogView);

        // Find views in the dialog layout
        quantity_text_view = dialogView.findViewById(R.id.quantity_text_view);
        sizeRadioGroup = dialogView.findViewById(R.id.size_radio_group); // Initialize sizeRadioGroup
        Button decrementButton = dialogView.findViewById(R.id.decrement_button);
        Button incrementButton = dialogView.findViewById(R.id.increment_button);

        decrementButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(quantity_text_view.getText().toString());
            if (currentQuantity > 1) {
                currentQuantity--;
                quantity_text_view.setText(String.valueOf(currentQuantity));
            }
        });

        // Set onClickListener for increment button
        incrementButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(quantity_text_view.getText().toString());
            currentQuantity++;
            quantity_text_view.setText(String.valueOf(currentQuantity));
        });

        builder.setPositiveButton("Update", (dialog, which) -> {
            int checkedRadioButtonId = sizeRadioGroup.getCheckedRadioButtonId();
            String size = "";

            if (checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please choose size.", Toast.LENGTH_SHORT).show();
                return;
            } else {
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

            String newQuantity = quantity_text_view.getText().toString().trim();
            updateCartItem(position, newQuantity, size);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateCartItem(int position, String newQuantity, String newSize) {
        CartItemResponse item = cartAdapter.getItems().get(position);

        // Kiểm tra nếu người dùng không thay đổi số lượng và kích thước, sử dụng giá trị hiện tại
        int quantity = newQuantity.isEmpty() ? item.getQuantity() : Integer.parseInt(newQuantity);
        String size = newSize.isEmpty() ? item.getSize() : newSize;

        // Tạo request để gửi lên server
        CartRequest request = new CartRequest(userId, item.getProductId(), quantity, size);

        // Gọi API để cập nhật item trong giỏ hàng
        CartService cartService = ApiClient.getRetrofitInstance().create(CartService.class);
        Call<Void> call = cartService.updateCartItem(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CartActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();

                    // Cập nhật lại dữ liệu trên RecyclerView
                    item.setQuantity(quantity);
                    item.setSize(size);
                    cartAdapter.notifyItemChanged(position);
                } else {
                    if (response.errorBody() != null) {
                        try {
                            // Đọc thông báo lỗi từ errorBody của response
                            String errorMessage = response.errorBody().string();
                            // Xử lý để chỉ lấy nội dung thông báo lỗi từ chuỗi JSON
                            JSONObject jsonObject = new JSONObject(errorMessage);
                            String message = jsonObject.getString("message");

                            // Hiển thị thông báo lỗi
                            Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CartActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CartActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error updating item: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCart() {
        CartService cartService = ApiClient.getRetrofitInstance().create(CartService.class);
        Call<Void> call = cartService.clearCart(userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa tất cả các mục trong RecyclerView và cập nhật giao diện
                    cartAdapter.clearItems();
                    textTotal.setText("Total: $0.00");
                    Toast.makeText(CartActivity.this, "Cart cleared successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Failed to clear cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error clearing cart: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
