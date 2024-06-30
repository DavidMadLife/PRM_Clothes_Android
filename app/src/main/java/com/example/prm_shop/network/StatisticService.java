package com.example.prm_shop.network;

import com.example.prm_shop.models.response.StatisticsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StatisticService {
    @GET("statistics/totals")
    Call<StatisticsResponse> getStatistics();
}
