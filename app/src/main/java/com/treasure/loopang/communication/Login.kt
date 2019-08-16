package com.treasure.loopang.communication

import org.json.JSONObject

class Login(private var user: User = User()) : UserInterface {
    fun setUserInfo(email: String, password: String) {
        user.email = email
        user.password = makeSHA256(password)
    }

    override fun getJson() = JSONObject().apply {
        put("email", user.email)
        put("password", user.password)
    }
}