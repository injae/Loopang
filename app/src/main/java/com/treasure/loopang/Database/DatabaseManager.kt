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

    fun insertEmail(context: Context, email: String) {
        val emailEntity = EmailEntity()
        emailEntity.email = email
        EmailDB.getInstance(context)?.emailDao()?.insertEmail(emailEntity)
    }
    fun getEmail(context: Context) = EmailDB.getInstance(context)?.emailDao()?.getEmail()?.email
    fun deleteEmail(context: Context) { EmailDB.getInstance(context)?.emailDao()?.deleteEmail() }

    fun insertPassword(context: Context, password: String) {
        val passwordEntity = PasswordEntity()
        passwordEntity.password = password
        PasswordDB.getInstance(context)?.passwordDao()?.insertPassword(passwordEntity)
    }
    fun getPassword(context: Context) = PasswordDB.getInstance(context)?.passwordDao()?.getPassword()?.password
    fun deletePassword(context: Context) { PasswordDB.getInstance(context)?.passwordDao()?.deletePassword() }
}