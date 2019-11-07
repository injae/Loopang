package com.treasure.loopang.communication

object ResultManager {
    // Case
    val AUTH                = 1000
    val SIGN_UP             = 1001
    val LOGIN               = 1002
    val FILE_UPLOAD         = 1003
    val FILE_DOWNLOAD       = 1004
    val INFO_REQUEST        = 1005
    val FEED_REQUEST        = 1006
    val SEARCH_REQUEST      = 1007
    val REQUEST_LIKE_UP     = 1008
    val REQUEST_LIKE_DOWN   = 1009

    // SUCCESS OR FAIL
    val SUCCESS = 200
    val FAIL = 201

    // Error
    val CONNECTION_ERROR    = 400

    var accessToken: String = ""

    fun getCode(result: Result): Int {
        var code = 0
        when(result.status) {
            "success" -> {
                if(result.accessToken != "")
                    accessToken = result.accessToken
                code = SUCCESS
            }
            "fail" -> { code = FAIL }
            "error" -> { code = CONNECTION_ERROR }
        }
        return code
    }
}