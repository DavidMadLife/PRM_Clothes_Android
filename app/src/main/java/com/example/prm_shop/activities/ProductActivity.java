package com.example.prm_shop.activities;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.widget.SearchView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.Adapter.ProductAdapter;
import com.example.prm_shop.R;
import com.example.prm_shop.models.response.ProductResponse;
import com.example.prm_shop.models.response.SearchResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.ProductService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private SearchView searchView;
    private ProductService productService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        productAdapter = new ProductAdapter(this, null);
        recyclerView.setAdapter(productAdapter);

        productService = ApiClient.getRetrofitInstance().create(ProductService.class);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText);
                return false;
            }
        });

        // Initial load of all products
        searchProducts(null);
    }

    private void searchProducts(String query) {
        Call<SearchResponse> call = productService.searchProducts(
                query,
                null,
                null,
                null,
                null,
                1,  // pageIndex
                10  // pageSize
        );

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> products = response.body().getProducts();
                    productAdapter.setProductList(products);
                } else {
                    Log.e("ProductActivity", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Log.e("ProductActivity", "API call failed: " + t.getMessage());
            }
        });
    }
}
