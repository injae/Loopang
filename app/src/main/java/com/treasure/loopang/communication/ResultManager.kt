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
                    "refreshed token" ->    code = SUCCESS_AUTH
                    "login" ->              code = SUCCESS_LOGIN
                    "sign up" ->            code = SUCCESS_SIGN_UP
                }
                accessToken = result.accessToken    // in case of SUCCESS_SIGN_UP, accessToken == ""
            }
            "fail" -> {
                when(result.message) {
                    "expired signature" ->                  code = EXPIRED_SIGNATURE
                    "invalid token" ->                      code = INVALID_TOKEN
                    "unregistered id or wrong password" ->  code = UNREG_OR_WRONG
                    "duplicate id" ->                       code = DUPLICATED_ID
                }
            }
            "error" -> code = ERROR
            else -> {}
        }
        return code
    }

    fun codeToString(code: Int): String {
        when(code) {
            SUCCESS_AUTH ->         return "SUCCESS_AUTH"
            SUCCESS_LOGIN ->        return "SUCCESS_LOGIN"
            SUCCESS_SIGN_UP ->      return "SUCCESS_SIGN_UP"
            DUPLICATED_ID ->        return "DUPLICATED_ID"
            UNREG_OR_WRONG ->       return "UNREG_OR_WRONG"
            EXPIRED_SIGNATURE ->    return "EXPIRED_SIGNATURE"
            INVALID_TOKEN ->        return "INVALID_TOKEN"
            ERROR ->                return "ERROR"
            else ->                 return ""
        }
    }
}