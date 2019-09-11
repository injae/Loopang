package com.treasure.loopang.communication

import android.os.Environment
import android.util.Log
import com.treasure.loopang.communication.API.LoopangNetwork
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*
import com.treasure.loopang.communication.ResultManager.accessToken
import okhttp3.*
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.FileOutputStream
import java.lang.Exception

class Connector(private val DNS: String = "https://ec2-3-15-172-177.us-east-2.compute.amazonaws.com",
                private val port: Int = 5000,
                private var retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("${DNS}:${port}")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUnsafeOkHttpClient())
                    .build(),
                private var service: LoopangNetwork = retrofit.create(LoopangNetwork::class.java),
                private var file: ByteArray? = null) {
    fun process(case: Int, user: User? = null, fileName: String = ""): Result{
        var call: Call<Result>? = null
        var fileCall: Call<ResponseBody>? = null
        var result: Result
        when(case) {
            ResultManager.AUTH -> { call = service.receiveTokens() }
            ResultManager.SIGN_UP -> { call = service.sendSignUpInfo(user!!.email, user.password, user.name) }
            ResultManager.LOGIN -> { call = service.sendLoginInfo(user!!.email, user.password) }
            ResultManager.FILE_UPLOAD -> { call = service.sendFile(accessToken, fileName, getMultiPartBody(fileName)) }
            ResultManager.FILE_DOWNLOAD -> { fileCall = service.receiveFile(accessToken, fileName) } }
        try {
            if(call != null)    // 파일 다운로드가 아닌경우
                result = call.execute().body()!!
            else {
                file = fileCall?.execute()?.body()?.bytes()
                val tempFile = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}/Loopang/tt.aac")
                val fos = FileOutputStream(tempFile)
                fos.write(file)
                result = getSuccessFileReceive()
            }
        }
        catch (e: Exception) {
            Log.d("OkHttp", "Error Cause : ${e}")
            result = getErrorResult()
        }
        return result
    }

    fun getFile() = file
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

private fun getErrorResult(): Result {
    val temp = Result()
    temp.status = "error"
    return temp
}

private fun getSuccessFileReceive(): Result {
    val temp = Result()
    temp.status = "success"
    temp.message = "file download success"
    return temp
}