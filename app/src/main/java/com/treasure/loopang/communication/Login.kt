package com.treasure.loopang.communication

data class User(var id: String = "", var password: String = "",
                var name: String = "", var email: String = "")

class Login(val connector: Connector) {
    private val user = User()

    fun setUserInfo(id: String, password: String, name: String, email: String) {
        setID(id)
        setPassword(password)
        setName(name)
        setEmail(email)
    }
    private fun setID(id: String) { user.id = id }
    private fun setPassword(password: String) { user.password = password }
    private fun setName(name: String) { user.name = name }
    private fun setEmail(email: String) { user.email = email }

    fun makeUserHash() {
        user.id = makeSHA256(user.id)
        user.password = makeSHA256(user.password)
        user.name = makeSHA256(user.name)
        user.email = makeSHA256(user.email)
    }
}