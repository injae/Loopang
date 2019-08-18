package com.treasure.loopang.communication

import com.treasure.loopang.communication.API.LoopangNetwork
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response

class Connector(val DNS: String = "http://127.0.0.1", val port: Int = 46729,
                var retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("${DNS}/:${port}")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build(),
                var service: LoopangNetwork = retrofit.create(LoopangNetwork::class.java)) {

    fun getResult(case: Int, json: JSONObject? = null): Result {
        var call: Call<Result>? = null
        val result = Result()
        when(case) {
            ResultManager.AUTH -> { call = service.receiveTokens() }
            ResultManager.SIGN_UP -> { call = service.sendSignUpInfo(json!!) }
            ResultManager.LOGIN -> { call = service.sendLoginInfo(json!!) }
        }

        call?.enqueue(object : Callback<Result> {
            override fun onFailure(call: Call<Result>, t: Throwable) { }
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if(response.body() != null) {
                    result.status = response.body()!!.status
                    result.message = response.body()!!.message
                    result.refreshToken = response.body()!!.refreshToken
                    result.accessToken = response.body()!!.accessToken
                }
            }
        })
        return result
    }
}