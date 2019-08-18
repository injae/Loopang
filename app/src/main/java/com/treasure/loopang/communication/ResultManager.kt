package com.treasure.loopang.communication

object ResultManager {
    // Case
    val AUTH = 100
    val SIGN_UP = 101
    val LOGIN = 102

    // Sign Up Part
    val SUCCESS_SIGN_UP     = 50
    val DUPLICATED_ID       = 51

    // Login Part
    val SUCCESS_LOGIN       = 60
    val UNREG_OR_WRONG      = 61

    // Authorization Part
    val SUCCESS_AUTH        = 70
    val EXPIRED_SIGNATURE   = 71
    val INVALID_TOKEN       = 72

    val ERROR = 80

    var accessToken: String = ""
    var refreshToken: String = ""

    fun getCode(result: Result): Int {
        var code = 0
        when(result.status) {
            "success" -> {
                when(result.message) {
                    "refreshed token" -> {
                        code = SUCCESS_AUTH
                        accessToken = result.accessToken
                    }
                    "login" -> {
                        code = SUCCESS_LOGIN
                        //refreshToken = result.refreshToken
                        accessToken = result.accessToken
                    }
                    "sign up" -> code = SUCCESS_SIGN_UP
                }
            }

            "fail" -> {
                when(result.message) {
                    "expired signature" -> code = EXPIRED_SIGNATURE
                    "invalid token" -> code = INVALID_TOKEN
                    "unregistered id or wrong password" -> code = UNREG_OR_WRONG
                    "duplicate id" -> code = DUPLICATED_ID
                }
            }

            "error" -> code = ERROR

            else -> {}
        }
        return code
    }
}