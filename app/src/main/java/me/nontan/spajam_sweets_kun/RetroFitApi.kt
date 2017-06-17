package me.nontan.spajam_sweets_kun

import android.telecom.Call
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User

interface RetroFitApi {
    @FormUrlEncoded
    @GET("login")
    fun login(
            @Field("user_id") user_id: String,
            @Field("password") password: String
    ): Call<Token>
}

