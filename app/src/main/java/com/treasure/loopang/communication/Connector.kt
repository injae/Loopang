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
import retrofit2.Callback
import retrofit2.Response
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
                private var file: ByteArray? = null, var feedResult: FeedResult? = null, var searchResult: SearchResult? = null) {
    // 없는거는 알아서 무시되는거 같으니깐 Result 클래스에 멤버 싹다 때려박고 그냥 그거 하나로 다하면 call 갯수 줄일수 있을듯
    fun process(case: Int, user: User? = null, fileName: String? = null, searchData: String? = null, musicID: String? = null): Result{
        var call: Call<Result>? = null
        var fileCall: Call<ResponseBody>? = null
        var infoCall: Call<ForUserInfo>? = null
        var feedCall: Call<FeedResult>? = null
        var searchCall: Call<SearchResult>? = null
        var result = Result()
        when(case) {
            ResultManager.AUTH -> { call = service.receiveTokens() }
            ResultManager.SIGN_UP -> { call = service.sendSignUpInfo(user!!.email, user.password, user.name) }
            ResultManager.LOGIN -> { call = service.sendLoginInfo(user!!.email, user.password) }
            ResultManager.FILE_UPLOAD -> { call = service.sendFile(accessToken, fileName!!, getMultiPartBody(fileName)) }
            ResultManager.FILE_DOWNLOAD -> { fileCall = service.receiveFile(accessToken, fileName!!) }
            ResultManager.INFO_REQUEST -> { infoCall = service.receiveUserInfo(accessToken) }
            ResultManager.FEED_REQUEST -> { feedCall = service.receiveFeed(accessToken) }
            ResultManager.SEARCH_REQUEST -> { searchCall = service.receiveSearch(searchData!!) }
            ResultManager.REQUEST_LIKE_UP -> { call = service.requestLike(accessToken, musicID!!, true) }
            ResultManager.REQUEST_LIKE_DOWN -> { call = service.requestLike(accessToken, musicID!!, false) }}
        try {
            if(call != null) { // 파일다운로드, 유저인포 요청이 아닐경우
                if(case != ResultManager.REQUEST_LIKE_UP && case != ResultManager.REQUEST_LIKE_DOWN) {
                    result = call.execute().body()!!
                }
                else {  // 좋아요 요청일때는 비동기로 처리
                    call.enqueue(object : Callback<Result>{
                        override fun onResponse(call: Call<Result>, response: Response<Result>) { TODO("Just Request") }
                        override fun onFailure(call: Call<Result>, t: Throwable) { TODO("Just Request") }
                    })
                }
            }
            else if(fileCall != null) { // 파일다운로드 일경우
                file = fileCall.execute().body()?.bytes()
                val tempFile = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}/Loopang/${fileName}")
                val fos = FileOutputStream(tempFile)
                fos.write(file)
                result = getSuccessFileReceive()
            }
            else if(infoCall != null) { // 유저인포 요청일경우
                val infoTemp = infoCall.execute().body()
                result = getInfoRequest(infoTemp!!)
            }
            else if(feedCall != null) { // 피드 요청인경우
                feedResult = feedCall.execute().body()
                result = getFeedRequest(feedResult!!)
            }
            else if(searchCall != null) { // 검색 요청인경우
                searchResult = searchCall.execute().body()
                result = getSearchRequest(searchResult!!)
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

private fun getInfoRequest(userInfo: ForUserInfo): Result {
    val temp = Result()
    if(userInfo.status == "success") { UserManager.setInfo(userInfo.nickName, userInfo.trackList, userInfo.likedList) }
    temp.status = userInfo.status
    temp.message = userInfo.message
    return temp
}

private fun getFeedRequest(feedResult: FeedResult): Result {
    val temp = Result()
    temp.status = feedResult.status
    temp.message = feedResult.message
    return temp
}

private fun getSearchRequest(searchResult: SearchResult): Result {
    val temp = Result()
    temp.status = searchResult.status
    temp.message = searchResult.message
    return temp
}