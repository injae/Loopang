package com.treasure.loopang.communication

import com.treasure.loopang.communication.API.LoopangNetwork
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response

class Connector(val DNS: String = "127.0.0.1", val port: Int = 46729,
                var retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("http://127.0.0.1/:${port}")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build(),
                var service: LoopangNetwork = retrofit.create(LoopangNetwork::class.java)) {

    fun sendSignUpInfo(json: JSONObject): Result {
        val call = service.sendSignUpInfo(json)
        val result = Result()
        call.enqueue(object : Callback<Result>{
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if(response.body() != null) {
                    result.status = response.body()!!.status
                    result.message = response.body()!!.message
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                // 실패했다는 토스트알림 넣기
            }
        })

        return result
    }
}