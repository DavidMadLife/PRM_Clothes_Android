package com.example.prm_shop.network;

import com.example.prm_shop.models.request.OrderRequest;
import com.example.prm_shop.models.response.OrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderService {
    @POST("Oder/checkout")
    Call<Void> checkout(@Body OrderRequest orderRequest);

    @GET("Order/getOrderByMemberId/{id}")
    Call<List<OrderResponse>> getOrdersByMemberId(@Path("id") int id);

    @GET("Order/getOrderByStatusPending")
    Call<List<OrderResponse>> getOrderByStatusPending(@Query("keyword") String keyword, @Query("pageIndex") int pageIndex, @Query("pageSize") int pageSize);

    @GET("Order/getOrderByStatusConfirmed")
    Call<List<OrderResponse>> getOrderByStatusConfirmed(@Query("keyword") String keyword, @Query("pageIndex") int pageIndex, @Query("pageSize") int pageSize);

    @GET("Order/getOrderByStatusRejected")
    Call<List<OrderResponse>> getOrderByStatusRejected(@Query("keyword") String keyword, @Query("pageIndex") int pageIndex, @Query("pageSize") int pageSize);

    @PATCH("Order/confirmOrder/{id}")
    Call<Void> confirmOrder(@Path("id") int orderId);

    @PATCH("Order/rejectOrder/{id}")
    Call<Void> rejectOrder(@Path("id") int orderId);
}
