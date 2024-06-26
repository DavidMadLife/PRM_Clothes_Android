package com.example.prm_shop.network;

import com.example.prm_shop.models.response.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductService {
    @GET("products")
    Call<List<ProductResponse>> getProducts();
}
