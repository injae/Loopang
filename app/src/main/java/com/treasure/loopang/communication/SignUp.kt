package com.treasure.loopang.communication

import org.json.JSONObject

class SignUp(private var user: User = User()) : UserInterface {
    fun setUserInfo(email: String, password: String, name: String) {
        user.email = email
        user.password = makeSHA256(password)
        user.name = name
    }

    override fun getJson() = JSONObject().apply {
        put("email", user.email)
        put("name", user.name)
        put("password", user.password)
    }
}