package com.treasure.loopang.communication

import android.os.Environment
import com.treasure.loopang.communication.API.LoopangNetwork
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*
import java.util.concurrent.atomic.AtomicReference

class Connector(private val DNS: String = "https://ec2-3-15-172-177.us-east-2.compute.amazonaws.com",
                private val port: Int = 5000,
                private var retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("${DNS}:${port}")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUnsafeOkHttpClient())
                    .build(),
                private var service: LoopangNetwork = retrofit.create(LoopangNetwork::class.java),
                var result: AtomicReference<Result> = AtomicReference()) {
    fun process(case: Int, json: JSONObject? = null, fileName: String = "") {
        var call: Call<Result>? = null

        when(case) {
            ResultManager.AUTH -> { call = service.receiveTokens() }
            ResultManager.SIGN_UP -> { call = service.sendSignUpInfo(json?.get("email").toString(), json?.get("name").toString(), json?.get("password").toString()) }
            ResultManager.LOGIN -> { call = service.sendLoginInfo(json?.get("email").toString(), json?.get("password").toString()) }
            ResultManager.FILE_UPLOAD -> { call = service.sendFile(ResultManager.accessToken, fileName, getMultiPartBody(fileName)) }
        }
        result.set(call?.execute()?.body())
    }
}

private fun getUnsafeOkHttpClient(): OkHttpClient {
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) { }
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) { }
        override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
    })

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())
    val sslSocketFactory = sslContext.socketFactory
    val logger = HttpLoggingInterceptor()
    logger.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient.Builder()
        .addInterceptor(logger)
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }.build()
}

private fun getMultiPartBody(fileName: String) = MultipartBody.Part
    .createFormData("file", fileName, RequestBody.create(MediaType.parse("audio/*"),
    File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}/Loopang/${fileName}")))