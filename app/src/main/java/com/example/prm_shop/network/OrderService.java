package com.example.prm_shop.network;

import com.example.prm_shop.models.request.OrderRequest;
import com.example.prm_shop.models.response.OrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderService {
    @POST("Oder/checkout")
    Call<Void> checkout(@Body OrderRequest orderRequest);

    @GET("Order/getOrderByMemberId/{id}")
    Call<List<OrderResponse>> getOrdersByMemberId(@Path("id") int id);

}
