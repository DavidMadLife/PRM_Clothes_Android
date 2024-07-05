package com.example.prm_shop.network;

import com.example.prm_shop.models.request.ProductRequest;
import com.example.prm_shop.models.response.ProductResponse;
import com.example.prm_shop.models.response.SearchResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PATCH;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {

    @GET("products")
    Call<List<ProductResponse>> getProducts();

    @Multipart
    @POST("products")
    Call<ProductResponse> createProduct(
            @Part("UnitsInStock") RequestBody unitsInStock,
            @Part("UnitPrice") RequestBody unitPrice,
            @Part("ProviderId") RequestBody providerId,
            @Part("Status") RequestBody status,
            @Part("Weight") RequestBody weight,
            @Part("CategoryId") RequestBody categoryId,
            @Part("ProductName") RequestBody productName,
            @Part("Description") RequestBody description,
            @Part MultipartBody.Part imgFile
    );

    @GET("products/search")
    Call<SearchResponse> searchProducts(
            @Query("productName") String productName,
            @Query("providerId") Integer providerId,
            @Query("unitPrice") Double unitPrice,
            @Query("weight") Double weight,
            @Query("categoryId") Integer categoryId,
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize
    );



    @Multipart
    @PUT("products/{id}")
    Call<ProductRequest> updateProduct(
            @Path("id") int id,
            @Part("CategoryId") RequestBody categoryId,
            @Part("ProviderId") RequestBody providerId,
            @Part("ProductName") RequestBody productName,
            @Part("Weight") RequestBody weight,
            @Part("UnitPrice") RequestBody unitPrice,
            @Part("UnitsInStock") RequestBody unitsInStock,
            @Part("Status") RequestBody status,
            @Part("Description") RequestBody description,
            @Part MultipartBody.Part imgFile
    );

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);

    @GET("products/{id}")
    Call<ProductResponse> getProductById(@Path("id") int productId);
}
