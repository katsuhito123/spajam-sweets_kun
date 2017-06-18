package me.nontan.spajam_sweets_kun.utilities

import me.nontan.spajam_sweets_kun.models.*
import retrofit2.Call
import retrofit2.http.*

interface LoginService {
    @POST("login")
    fun login(@Body body: LoginRequest): Call<AuthenticationResponse>

    @GET("shop/{id}")
    fun shop(@Path("id") shop_id: Int): Call<ShopResponse>

    @GET("shop/search")
    fun shopSearch(
            @Query("lat_min") lat_min: Double,
            @Query("lat_max") lat_max: Double,
            @Query("long_min") long_min: Double,
            @Query("long_max") long_max: Double): Call<ShopSearchResponse>

    @GET("review/search")
    fun reviewSearch(
            @Query("lat_min") lat_min: Double,
            @Query("lat_max") lat_max: Double,
            @Query("long_min") long_min: Double,
            @Query("long_max") long_max: Double): Call<ReviewSearchResponse>

    @POST("review/create")
    fun reviewCreate(@Header("Authorization") auth: String, @Body body: ReviewCreateRequest): Call<ReviewCreateResponse>

}

