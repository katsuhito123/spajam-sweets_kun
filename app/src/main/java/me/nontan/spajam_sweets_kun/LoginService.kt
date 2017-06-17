package me.nontan.spajam_sweets_kun

import me.nontan.spajam_sweets_kun.models.Authentication
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService {
    @FormUrlEncoded
    @POST("login")
    fun login(
            @Field("user_id") user_id: String,
            @Field("password") password: String
    ): Call<Authentication>
}

