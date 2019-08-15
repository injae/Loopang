package com.treasure.loopang.communication

class SignUp(var user: User = User()) : UserInterface {
    private fun setID(id: String) { user.id = id }
    private fun setPassword(password: String) { user.password = password }
    private fun setName(name: String) { user.name = name }
    private fun setEmail(email: String) { user.email = email }

    fun setUserInfo(id: String, password: String, name: String, email: String) {
        setID(id)
        setPassword(password)
        setName(name)
        setEmail(email)
    }

    override fun makeUserHash() {
        user.id = makeSHA256(user.id)
        user.password = makeSHA256(user.password)
        user.name = makeSHA256(user.name)
        user.email = makeSHA256(user.email)
    }

    override fun getToken(): String {
        return JwtManager()
            .makeCustomPayload("id", user.id)
            .makeCustomPayload("password", user.password)
            .makeCustomPayload("name", user.name)
            .makeCustomPayload("email", user.email)
            .makeFinish()
    }
}