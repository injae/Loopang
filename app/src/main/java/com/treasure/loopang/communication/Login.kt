package com.treasure.loopang.communication

data class User(var id: String = "", var password: String = "",
                var name: String = "", var email: String = "")

class Login(var user: User = User()) : UserInterface {
    private fun setID(id: String) { user.id = id }
    private fun setPassword(password: String) { user.password = password }

    fun setUserInfo(id: String, password: String) {
        setID(id)
        setPassword(password)
    }

    override fun makeUserHash() {
        user.id = makeSHA256(user.id)
        user.password = makeSHA256(user.password)
    }

    override fun getToken(): String {
        return JwtManager()
            .makeCustomPayload("public_id", user.id)
            .makeCustomPayload("password", user.password)
            .makeFinish()
    }
}