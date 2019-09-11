package com.treasure.loopang.communication

object ResultManager {
    // Case
    val AUTH = 100
    val SIGN_UP = 101
    val LOGIN = 102
    val FILE_UPLOAD = 103
    val FILE_DOWNLOAD = 104

    // Sign Up Part
    val SUCCESS_SIGN_UP     = 50
    val DUPLICATED_ID       = 51
    val WRONG_FORMAT        = 52

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

    // Error
    val CONNECTION_ERROR    = 400
    val CANT_FIND_FILE      = 401
    val CANT_FIND_MUSIC     = 402

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
                }
                accessToken = result.accessToken    // in case of SUCCESS_SIGN_UP, accessToken == ""
            }
            "fail" -> {
                when(result.message) {
                    "expired signature" ->                  code = EXPIRED_SIGNATURE
                    "invalid token" ->                      code = INVALID_TOKEN
                    "unregistered id or wrong password" ->  code = UNREG_OR_WRONG
                    "duplicate id" ->                       code = DUPLICATED_ID
                    "No File Found" ->                      code = NO_FILE_FOUND
                    "Is Existed file: ${result.message.slice(IntRange(17, result.message.length - 1))}" -> code = IS_EXISTED
                }
            }
            "error" -> {
                when(result.message) {
                    "Can't find file" ->    code = CANT_FIND_FILE
                    "Can't find music" ->   code = CANT_FIND_MUSIC
                    else ->                 code = CONNECTION_ERROR
                }
            }
            else -> {}
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
            DUPLICATED_ID ->        return "DUPLICATED_ID"
            UNREG_OR_WRONG ->       return "UNREG_OR_WRONG"
            WRONG_FORMAT ->         return "WRONG_FORMAT"
            EXPIRED_SIGNATURE ->    return "EXPIRED_SIGNATURE"
            INVALID_TOKEN ->        return "INVALID_TOKEN"
            NO_FILE_FOUND ->        return "NO_FILE_FOUND"
            IS_EXISTED ->           return "IS_EXISTED"
            CONNECTION_ERROR ->     return "CONNECTION ERROR"
            CANT_FIND_FILE ->       return "CANT_FIND_FILE"
            CANT_FIND_MUSIC ->      return "CANT_FIND_MUSIC"
            else ->                 return "This is not code. It may be Case"
        }
    }
}