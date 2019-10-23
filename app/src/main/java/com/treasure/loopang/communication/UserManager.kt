package com.treasure.loopang.communication

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

    fun setInfo(nickName: String, trackList: List<MusicListClass>?, likedList: List<MusicListClass>?) {
        user.name = nickName
        user.trackList = trackList
        user.likedList = likedList
    }

    fun setEncodedPassword(encodedPassword: String) { user.encodedPassword = encodedPassword }

    /**
     * Function that must be called at the end of Sign-Up
     */
    fun makeEmptyUser() {
        user.email = ""
        user.password = ""
        user.name = ""
        user.trackList = null
        user.likedList = null
    }

    fun getUser() = user
}