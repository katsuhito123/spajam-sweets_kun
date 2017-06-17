package me.nontan.spajam_sweets_kun

import me.nontan.spajam_sweets_kun.models.AuthenticationResponse
import me.nontan.spajam_sweets_kun.models.LoginRequest
import me.nontan.spajam_sweets_kun.models.ShopSearchResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    @POST("login")
    fun login(@Body body: LoginRequest): Call<AuthenticationResponse>

    @GET("shop/search")
    fun shopSearch(
            @Query("lat_min") lat_min: Double,
            @Query("lat_max") lat_max: Double,
            @Query("long_min") long_min: Double,
            @Query("long_max") long_max: Double): Call<ShopSearchResponse>
}

