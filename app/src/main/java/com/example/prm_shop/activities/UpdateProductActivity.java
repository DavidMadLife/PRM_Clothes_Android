package com.example.prm_shop.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.R;
import com.example.prm_shop.models.response.ProductResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.ProductService;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProductActivity extends AppCompatActivity {

    private EditText productNameInput, productDescriptionInput, productPriceInput, productStockInput;
    private Button updateButton;
    private ProductResponse productToUpdate;
    private ProductService productService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_update);

        // Initialize UI elements
        productNameInput = findViewById(R.id.edit_text_product_name);
        productDescriptionInput = findViewById(R.id.edit_text_product_description);
        productPriceInput = findViewById(R.id.edit_text_product_price);
        productStockInput = findViewById(R.id.edit_text_product_stock);
        updateButton = findViewById(R.id.button_update_product);

        // Get product data from intent
        productToUpdate = (ProductResponse) getIntent().getSerializableExtra("product");

        // Set initial values in UI
        if (productToUpdate != null) {
            productNameInput.setText(productToUpdate.getProductName());
            productDescriptionInput.setText(productToUpdate.getDescription());
            productPriceInput.setText(String.valueOf(productToUpdate.getUnitPrice()));
            productStockInput.setText(String.valueOf(productToUpdate.getUnitsInStock()));
        }

        // Initialize Retrofit service
        productService = ApiClient.getRetrofitInstance().create(ProductService.class);

        // Handle update button click
        updateButton.setOnClickListener(view -> updateProduct());
    }

    private void updateProduct() {
        // Get updated values from UI
        String productName = productNameInput.getText().toString().trim();
        String description = productDescriptionInput.getText().toString().trim();
        int price = Integer.parseInt(productPriceInput.getText().toString().trim());
        int stock = Integer.parseInt(productStockInput.getText().toString().trim());

        // Make API call to update product without changing image
        Call<Void> call = productService.updateProduct(
                productToUpdate.getProductId(),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stock)),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price)),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(productToUpdate.getProviderId())),
                RequestBody.create(MediaType.parse("text/plain"), productToUpdate.getStatus()),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(productToUpdate.getWeight())),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(productToUpdate.getCategoryId())),
                RequestBody.create(MediaType.parse("text/plain"), productName),
                RequestBody.create(MediaType.parse("text/plain"), description)
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, navigate back or refresh product list
                    finish();
                } else {
                    Toast.makeText(UpdateProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UpdateProductActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
