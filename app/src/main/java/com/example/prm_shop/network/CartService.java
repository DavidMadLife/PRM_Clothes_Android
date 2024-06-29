package com.example.prm_shop.network;

import com.example.prm_shop.models.request.CartRequest;
import com.example.prm_shop.models.request.RemoveItemRequest;
import com.example.prm_shop.models.response.CartResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartService {

    @GET("carts/{memberId}")
    Call<CartResponse> getCart(@Path("memberId") int memberId);

    @POST("carts/add")
    Call<Void> addToCart(@Body CartRequest cartRequest);

    @POST("carts/remove")
    Call<Void> removeCartItem(@Body RemoveItemRequest removeItemRequest);

    @POST("carts/update-quantity")
    Call<Void> updateCartItem(@Body CartRequest request);

    @POST("carts/clear/{memberId}")
    Call<Void> clearCart(@Path("memberId") int memberId);
}

