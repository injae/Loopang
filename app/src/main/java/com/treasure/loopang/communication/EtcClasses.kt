package com.treasure.loopang.communication

data class Result(var status: String = "", var message: String = "",
                   var refreshToken: String = "", var accessToken: String = "")

data class User( var email: String = "", var password: String = "",
                 var name: String = "", var encodedPassword: String = "")