package me.nontan.spajam_sweets_kun

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User
import me.nontan.spajam_sweets_kun.models.Token
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.GET

interface LoginService {
    @FormUrlEncoded
    @GET("login")
    fun login(
            @Field("user_id") user_id: String,
            @Field("password") password: String
    ): Call<Token>
}

