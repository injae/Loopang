package com.treasure.loopang.Database

import android.content.Context

object DatabaseManager {
    fun insertToken(context: Context, refreshToken: String) {
        val tokenEntity = TokenEntity()
        tokenEntity.refreshToken = refreshToken
        TokenDB.getInstance(context)?.tokenDao()?.insertToken(tokenEntity)
    }

    fun getToken(context: Context) = TokenDB.getInstance(context)?.tokenDao()?.getToken()?.refreshToken

    fun deleteToken(context: Context) { TokenDB.getInstance(context)?.tokenDao()?.deleteToken() }
}