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

    // Info Request Part
    val SUCCESS_INFO_REQUEST = 40
    val FAIL_INFO_REQUEST = 41

    // Sign Up Part
    val SUCCESS_SIGN_UP     = 50
    val DUPLICATED_EMAIL    = 51
    val DUPLICATED_NAME     = 52
    val WRONG_FORMAT        = 53

    // Login Part
    val SUCCESS_LOGIN       = 60
    val UNREG_OR_WRONG      = 61

    // Authorization Part
    val SUCCESS_AUTH        = 70
    val EXPIRED_SIGNATURE   = 71
    val INVALID_TOKEN       = 72

    // File Upload Part
    val SUCCESS_UPLOAD      = 80
    val NO_FILE_FOUND       = 81
    val IS_EXISTED          = 82

    // File Download Part
    val SUCCESS_DOWNLOAD    = 90

    // Feed Part
    val SUCCESS_FEED_REQUEST = 100
    val FAIL_FEED_REQUEST    = 101

    // Search PART
    val SUCCESS_SEARCH  = 110
    val FAIL_SEARCH     = 111

    // Error
    val CONNECTION_ERROR    = 400

    var accessToken: String = ""

    fun getCode(result: Result): Int {
        var code = 0
        when(result.status) {
            "success" -> {
                when(result.message) {
                    "refreshed token" ->    code = SUCCESS_AUTH
                    "login" ->              code = SUCCESS_LOGIN
                    "sign up" ->            code = SUCCESS_SIGN_UP
                    "Uploaded ${result.message.slice(IntRange(9, result.message.length - 1))}" -> code = SUCCESS_UPLOAD
                    "file download success" -> code = SUCCESS_DOWNLOAD
                    "user-info" -> code = SUCCESS_INFO_REQUEST
                    "success feed request" -> code = SUCCESS_FEED_REQUEST
                    "success search" -> code = SUCCESS_SEARCH
                }
                accessToken = result.accessToken    // in case of SUCCESS_SIGN_UP, accessToken == ""
            }
            "fail" -> {
                when(result.message) {
                    "expired signature" ->                  code = EXPIRED_SIGNATURE
                    "invalid token" ->                      code = INVALID_TOKEN
                    "unregistered id or wrong password" ->  code = UNREG_OR_WRONG
                    "duplicate email" ->                    code = DUPLICATED_EMAIL
                    "duplicate name" ->                     code = DUPLICATED_NAME
                    "No File Found" ->                      code = NO_FILE_FOUND
                    "Is Existed file: ${result.message.slice(IntRange(17, result.message.length - 1))}" -> code = IS_EXISTED
                    "failed login info request" ->          code = FAIL_INFO_REQUEST
                    "failed feed request" ->                code = FAIL_FEED_REQUEST
                    "failed search" ->                      code = FAIL_SEARCH
                }
            }
            "error" -> { code = CONNECTION_ERROR }
        }
        return code
    }

    fun codeToString(code: Int): String {
        when(code) {
            SUCCESS_AUTH ->         return "SUCCESS_AUTH"
            SUCCESS_LOGIN ->        return "SUCCESS_LOGIN"
            SUCCESS_SIGN_UP ->      return "SUCCESS_SIGN_UP"
            SUCCESS_UPLOAD ->       return "SUCCESS_UPLOAD"
            SUCCESS_DOWNLOAD ->     return "SUCCESS_DOWNLOAD"
            DUPLICATED_EMAIL ->     return "DUPLICATED_EMAIL"
            DUPLICATED_NAME ->      return "DUPLICATED_NAME"
            UNREG_OR_WRONG ->       return "UNREG_OR_WRONG"
            WRONG_FORMAT ->         return "WRONG_FORMAT"
            EXPIRED_SIGNATURE ->    return "EXPIRED_SIGNATURE"
            INVALID_TOKEN ->        return "INVALID_TOKEN"
            NO_FILE_FOUND ->        return "NO_FILE_FOUND"
            IS_EXISTED ->           return "IS_EXISTED"
            CONNECTION_ERROR ->     return "CONNECTION ERROR"
            SUCCESS_INFO_REQUEST -> return "SUCCESS_INFO_REQUEST"
            FAIL_INFO_REQUEST ->    return "FAIL_INFO_REQUEST"
            SUCCESS_FEED_REQUEST -> return "SUCCESS_FEED_REQUEST"
            FAIL_FEED_REQUEST ->    return "FAIL_FEED_REQUEST"
            SUCCESS_SEARCH ->       return "SUCCESS_SEARCH"
            FAIL_SEARCH ->          return "FAIL_SEARCH"
            else ->                 return "This is not code. It may be Case"
        }
    }
}