package me.nontan.spajam_sweets_kun

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val sharedAPIInstance = Retrofit
        .Builder()
        .baseUrl("https://private.turenar.xyz/sweetskun/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(LoginService::class.java)