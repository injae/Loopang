package com.treasure.loopang.communication

data class User( var email: String = "", var password: String = "",
                 var name: String = "", var encodedPassword: String = "",
                 var trackList: List<MusicListClass>? = null, var likedList: List<MusicListClass>? = null)

sealed class defaultFrame(var status: String, var message: String)

data class ForUserInfo(var nickName: String = "", var trackList: List<MusicListClass>,
                       var likedList: List<MusicListClass>) : defaultFrame("","")

data class MusicListClass(var id: String = "", var name: String = "", var owner: String = "",
                          var updated_date: String = "", var downloads: Int = 0)

data class Result(var refreshToken: String = "", var accessToken: String = "") : defaultFrame("","")

data class FeedResult(var newFeed: List<String>) : defaultFrame("","")

data class SearchResult(var searchResult: List<String>) : defaultFrame("","")