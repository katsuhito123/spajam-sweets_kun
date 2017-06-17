package me.nontan.spajam_sweets_kun

import me.nontan.spajam_sweets_kun.models.Authentication
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("login")
    fun login(@Body body: LoginRequest): Call<Authentication>
}

