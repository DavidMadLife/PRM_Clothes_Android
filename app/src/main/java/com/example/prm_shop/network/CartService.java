package com.example.prm_shop.network;

import com.example.prm_shop.models.response.CartResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CartService {

    @GET("api/carts/{memberId}")
    Call<CartResponse> getCart(@Path("memberId") int memberId);
}

