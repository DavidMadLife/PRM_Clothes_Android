package com.example.prm_shop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
    private ProgressBar progressBar;

    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;

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
                resetPagination();
                searchProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                resetPagination();
                searchProducts(newText);
                return false;
            }
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE); // Initially hide progress bar

        // Initial load of all products
        searchProducts(null);

        // Add scroll listener to the RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize) {
                        loadMoreItems();
                    }
                }
            }
        });
    }

    private void resetPagination() {
        currentPage = 1;
        isLastPage = false;
    }

    private void searchProducts(String query) {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE); // Show progress bar

        Call<SearchResponse> call = productService.searchProducts(
                query,
                null,
                null,
                null,
                null,
                currentPage,
                pageSize
        );

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                isLoading = false;
                progressBar.setVisibility(View.GONE); // Hide progress bar

                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> products = response.body().getProducts();
                    if (currentPage == 1) {
                        productAdapter.setProductList(products);
                    } else {
                        productAdapter.addProducts(products);
                    }

                    if (products.size() < pageSize) {
                        isLastPage = true;
                    } else {
                        currentPage++;
                    }
                } else {
                    Log.e("ProductActivity", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                isLoading = false;
                progressBar.setVisibility(View.GONE); // Hide progress bar on failure
                Log.e("ProductActivity", "API call failed: " + t.getMessage());
            }
        });
    }

    private void loadMoreItems() {
        if (!isLoading && !isLastPage) {
            searchProducts(searchView.getQuery().toString());
        }
    }
}


