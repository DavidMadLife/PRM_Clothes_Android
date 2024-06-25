package com.example.prm_shop.network;

import com.example.prm_shop.models.request.LoginView;
import com.example.prm_shop.models.request.RegisterUserRequest;
import com.example.prm_shop.models.response.RegisterUserResponse;
import com.example.prm_shop.models.response.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MemberService {
    @POST("members/login")
    Call<TokenResponse> login(@Body LoginView loginView);
    @POST("members/register")
    Call<RegisterUserResponse> register(@Body RegisterUserRequest registerUserRequest);
}
