package com.example.prm_shop.network;

import com.example.prm_shop.models.request.LoginView;
import com.example.prm_shop.models.request.RegisterUserRequest;
import com.example.prm_shop.models.request.UpdateUserRequest;
import com.example.prm_shop.models.response.RegisterUserResponse;
import com.example.prm_shop.models.response.TokenResponse;
import com.example.prm_shop.models.response.UpdateUserResponse;
import com.example.prm_shop.models.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MemberService {
    @POST("members/login")
    Call<TokenResponse> login(@Body LoginView loginView);
    @POST("members/register")
    Call<RegisterUserResponse> register(@Body RegisterUserRequest registerUserRequest);
    @GET("members/{id}")
    Call<UserResponse> getUserById(@Path("id") int id);
    @PUT("members/update")
    Call<UpdateUserResponse> updateUser(@Query("id") int userId, @Body UpdateUserRequest request);


}
