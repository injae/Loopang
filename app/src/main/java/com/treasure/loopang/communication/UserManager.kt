package com.treasure.loopang.communication

import org.json.JSONObject

object UserManager {
    private val user = User()
    var isLogined = false

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
        user.name = name
        user.password = makeSHA256(password)
    }

    /**
     * Function that must be called at the end of Sign-Up
     */
    fun makeEmptyUser() {
        user.email = ""
        user.password = ""
        user.name = ""
    }

    fun getUser() = user
}