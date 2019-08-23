package com.treasure.loopang.communication.API

import com.treasure.loopang.communication.Result
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface LoopangNetwork {
    @FormUrlEncoded
    @POST("/sign-up")
    fun sendSignUpInfo(@Field("email") email: String,
                       @Field("name") name: String,
                       @Field("password") password: String): Call<Result>

    @FormUrlEncoded
    @POST("/login")
    fun sendLoginInfo(@Field("email") email: String,
                      @Field("password") password: String): Call<Result>

    @GET("/auth")
    fun receiveTokens(): Call<Result>

    @Multipart
    @POST("/upload")
    fun sendFile(@Part("token") accessToken: String,
                 @Part("name") fileName: String,
                 @Part file: MultipartBody.Part): Call<Result>
}