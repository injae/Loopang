package com.treasure.loopang.communication.API

import com.treasure.loopang.communication.FeedResult
import com.treasure.loopang.communication.ForUserInfo
import com.treasure.loopang.communication.Result
import com.treasure.loopang.communication.SearchResult
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface LoopangNetwork {
    @FormUrlEncoded
    @POST("/sign-up")
    fun sendSignUpInfo(@Field("email") email: String,
                       @Field("password") password: String,
                       @Field("name") name: String): Call<Result>

    @FormUrlEncoded
    @POST("/login")
    fun sendLoginInfo(@Field("email") email: String,
                      @Field("password") password: String): Call<Result>

    @FormUrlEncoded
    @POST("/user-info")
    fun receiveUserInfo(@Field("token") accessToken: String): Call<ForUserInfo>

    @FormUrlEncoded
    @POST("/feed")
    fun receiveFeed(@Field("token") token: String): Call<FeedResult>

    @FormUrlEncoded
    @POST("/search-music")
    fun receiveSearch(@Field("token") token: String,
                      @Field("target") target: String): Call<SearchResult>

    @GET("/auth")
    fun receiveTokens(): Call<Result>

    @Multipart
    @POST("/upload")
    fun sendFile(@Part("token") accessToken: String,
                 @Part("name") fileName: String,
                 @Part file: MultipartBody.Part): Call<Result>

    @FormUrlEncoded
    @POST("/download")
    fun receiveFile(@Field("token") accessToken: String,
                    @Field("name") fileName: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/request-like")
    fun requestLike(@Field("token") token: String,
                    @Field("music_id") music_id: String,
                    @Field("like") like: Boolean): Call<Result>
}