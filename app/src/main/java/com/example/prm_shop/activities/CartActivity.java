package com.example.prm_shop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.example.prm_shop.Api.CreateOrder;
import com.example.prm_shop.R;
import com.example.prm_shop.models.request.CartRequest;
import com.example.prm_shop.models.request.OrderRequest;
import com.example.prm_shop.models.request.RemoveItemRequest;
import com.example.prm_shop.models.response.CartItemResponse;
import com.example.prm_shop.models.response.CartResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.CartService;
import com.example.prm_shop.network.OrderService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;
//import vn.momo.momo_partner.AppMoMoLib;

public class CartActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private int userId;
    private CartAdapter cartAdapter;
    private TextView textTotal;

    private TextView quantity_text_view;

    private RadioGroup sizeRadioGroup;


/*    private String total_amount = "10000";
    private String total_fee = "0";
    private String merchantName = "Demo SDK";
    private String merchantCode = "SCB01";
    private String description = "Thanh toán đơn hàng từ ứng dụng PRM Shop";*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);


        recyclerView = findViewById(R.id.recycler_cart_items);
        textTotal = findViewById(R.id.text_total);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(new CartAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {

                CartItemResponse item = cartAdapter.getItems().get(position);

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
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    cartAdapter.showDeleteButton(position, true);
                    cartAdapter.showUpdateButton(position, true);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    cartAdapter.showDeleteButton(position, false);
                    cartAdapter.showUpdateButton(position, false);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        Button btnClearCart = findViewById(R.id.btn_clear_cart);
        btnClearCart.setOnClickListener(v -> clearCart());

        Button btnCheckout = findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView textTotal = findViewById(R.id.text_total);

                String totalText = textTotal.getText().toString();
                String totalAmountStr = totalText.replace("Total: $", "").replace(",", "").trim();
                double totalAmount = Double.parseDouble(totalAmountStr);

                CreateOrder orderApi = new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(String.valueOf((int)totalAmount));
                    Log.d("Amount", String.valueOf((int)totalAmount));
                    /*lblZpTransToken.setVisibility(View.VISIBLE);*/
                    String code = data.getString("returncode");
                    Log.d( "code",code);
                    Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

                    if (code.equals("1")) {
                        /*lblZpTransToken.setText("zptranstoken");
                        txtToken.setText(data.getString("zptranstoken"));*/
                        //IsDone();
                        String token = data.getString("zptranstoken");

                        Log.d("zptranstoken", token);
                        ZaloPaySDK.getInstance().payOrder(CartActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {

                                Log.d("ordeRequest", String.valueOf(userId));

                                OrderRequest orderRequest = new OrderRequest();
                                orderRequest.setMemberId(userId);


                                OrderService orderService = ApiClient.getRetrofitInstance().create(OrderService.class);
                                Call<Void> call = orderService.checkout(orderRequest);
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.isSuccessful()){
                                            Toast.makeText(CartActivity.this, "Checkout successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Toast.makeText(CartActivity.this, "Checkout failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {

                                    }
                                });

                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {

                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {

                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("TOKEN", "");
    }

    private void deleteCartItem(int productId, int position) {
        RemoveItemRequest request = new RemoveItemRequest(userId, productId);

        CartService cartService = ApiClient.getRetrofitInstance().create(CartService.class);
        Call<Void> call = cartService.removeCartItem(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
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

        int quantity = newQuantity.isEmpty() ? item.getQuantity() : Integer.parseInt(newQuantity);
        String size = newSize.isEmpty() ? item.getSize() : newSize;

        CartRequest request = new CartRequest(userId, item.getProductId(), quantity, size);

        CartService cartService = ApiClient.getRetrofitInstance().create(CartService.class);
        Call<Void> call = cartService.updateCartItem(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CartActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();

                    item.setQuantity(quantity);
                    item.setSize(size);
                    cartAdapter.notifyItemChanged(position);
                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorMessage = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorMessage);
                            String message = jsonObject.getString("message");

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    //Fail momo
    /*private void requestPayment() {
        String amount = textTotal.getText().toString().replace("Total: $", "");
        if (amount.isEmpty() || amount.equals("$0.00")) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("amount", amount);
        eventValue.put("orderId", "order_001");
        eventValue.put("requestId", String.valueOf(System.currentTimeMillis()));
        eventValue.put("orderLabel", "Giao dịch qua ứng dụng PRM Shop");

        //client Optional - not required for transaction processing
        eventValue.put("requestType", "captureMoMoWallet");
        eventValue.put("transId", "your_transId_123213213");
        eventValue.put("extraData", "merchantName=;merchantId=" + merchantCode);

        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null && data.getIntExtra("status", -1) == 0) {
                // Thanh toán thành công, gọi API Checkout
                checkoutOrder();
            } else {
                Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    /*private void checkoutOrder() {
        // Tạo request để gửi lên server
        OrderRequest orderRequest = new OrderRequest(userId);

        // Gọi API để thực hiện checkout
        OrderService apiService = ApiClient.getRetrofitInstance().create(OrderService.class);
        Call<Void> call = apiService.checkout(orderRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xử lý khi checkout thành công, ví dụ hiển thị thông báo
                    Toast.makeText(CartActivity.this, "Checkout successful", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý khi checkout không thành công
                    Toast.makeText(CartActivity.this, "Checkout failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Xử lý khi gặp lỗi trong quá trình gọi API
                Toast.makeText(CartActivity.this, "Error during checkout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}
