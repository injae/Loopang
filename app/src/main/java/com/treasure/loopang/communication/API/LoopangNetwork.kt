package com.treasure.loopang.communication.API

import com.treasure.loopang.communication.Result
import retrofit2.Call
import org.json.JSONObject
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface LoopangNetwork {
    @POST("sign-up")
    fun sendSignUpInfo(@Field("info") info: JSONObject): Call<Result>

    @POST("login")
    fun sendLoginInfo(@Field("info") info: JSONObject): Call<Result>

    @GET("auth")
    fun receiveTokens(): Call<Result>
}