package com.example.prm_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.R;
import com.example.prm_shop.Adapter.ProductManageAdapter;
import com.example.prm_shop.models.response.ProductResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.ProductService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductManageActivity extends FooterActivity implements ProductManageAdapter.ProductManageListener {

    private static final int UPDATE_PRODUCT_REQUEST = 1;

    private RecyclerView recyclerView;
    private ProductManageAdapter adapter;
    private ProductService productService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductManageAdapter(this, this);
        recyclerView.setAdapter(adapter);

        // Initialize Retrofit service
        productService = ApiClient.getRetrofitInstance().create(ProductService.class);

        // Load products
        loadProducts();
    }

    private void loadProducts() {
        Call<List<ProductResponse>> call = productService.getProducts();
        call.enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.isSuccessful()) {
                    List<ProductResponse> productList = response.body();
                    adapter.setProductList(productList);
                } else {
                    Toast.makeText(ProductManageActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                Toast.makeText(ProductManageActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProductUpdate(ProductResponse product) {
        Intent intent = new Intent(ProductManageActivity.this, UpdateProductActivity.class);
        intent.putExtra("product", product);
        startActivityForResult(intent, UPDATE_PRODUCT_REQUEST);
    }

    @Override
    public void onProductDelete(ProductResponse product) {
        deleteProduct(product.getProductId());
    }

    private void deleteProduct(int productId) {
        Call<Void> call = productService.deleteProduct(productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductManageActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    loadProducts(); // Refresh product list after deletion
                } else {
                    Toast.makeText(ProductManageActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductManageActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            loadProducts(); // Refresh product list after update
        }
    }
}
