package com.treasure.loopang.communication

data class User( var email: String = "", var password: String = "",
                 var name: String = "", var encodedPassword: String = "",
                 var trackList: List<String>? = null, var likedList: List<String>? = null)

sealed class defaultFrame(var status: String, var message: String)

data class ForUserInfo(var nickName: String = "", var trackList: List<String>,
                       var likedList: List<String>) : defaultFrame("","")

data class Result(var refreshToken: String = "", var accessToken: String = "") : defaultFrame("","")

data class FeedResult(var newFeed: List<String>) : defaultFrame("","")

data class SearchResult(var searchResult: List<String>) : defaultFrame("","")