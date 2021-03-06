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
    @POST("/auth/sign-up")
    fun sendSignUpInfo(@Field("email") email: String,
                       @Field("password") password: String,
                       @Field("name") name: String): Call<Result>

    @FormUrlEncoded
    @POST("/auth/login")
    fun sendLoginInfo(@Field("email") email: String,
                      @Field("password") password: String): Call<Result>

    @FormUrlEncoded
    @POST("/info/user")
    fun receiveUserInfo(@Field("token") accessToken: String): Call<ForUserInfo>

    @FormUrlEncoded
    @POST("/info/feed")
    fun receiveFeed(@Field("token") token: String): Call<FeedResult>

    @FormUrlEncoded
    @POST("/music/search")
    fun receiveSearch(@Field("token") token: String,
                      @Field("target") target: List<String>,
                      @Field("flag") flag: Int): Call<SearchResult>

    @GET("/auth/refresh")
    fun receiveTokens(): Call<Result>

    @Multipart
    @POST("/file/upload")
    fun sendFile(@Part("token") accessToken: String,
                 @Part("name") fileName: String, @Part("explanation") explanation: String,
                 @Part("tags") tags: List<String>, @Part file: MultipartBody.Part): Call<Result>

    @FormUrlEncoded
    @POST("/file/download")
    fun receiveFile(@Field("token") accessToken: String,
                    @Field("music_id") music_id: String,
                    @Field("preplay") preplay: Boolean): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/user/likes")
    fun requestLike(@Field("token") token: String,
                    @Field("music_id") music_id: String,
                    @Field("like") like: Boolean): Call<Result>
}