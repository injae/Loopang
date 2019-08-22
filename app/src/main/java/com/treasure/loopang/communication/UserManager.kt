package com.treasure.loopang.communication

import org.json.JSONObject

object UserManager {
    private val user = User()

    /**
     * Function to use when Login
     */
    fun setUser(email: String, password: String) {
        user.email = email
        user.password = makeSHA256(password)
    }

    /**
     *  Function to use when Sign-Up
     */
    fun setUser(email: String, password: String, name: String) {
        user.email = email
        user.password = makeSHA256(password)
        user.name = name
    }

    /**
     * Function that must be called at the end of Sign-Up
     */
    fun makeEmptyUser() {
        user.email = ""
        user.password = ""
        user.name = ""
    }

    fun getJson() = JSONObject().apply {
        put("email", user.email)
        put("password", user.password)
        put("name", user.name)
    }
}