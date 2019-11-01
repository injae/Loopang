package com.treasure.loopang.communication

import java.io.Serializable

data class User(var email: String = "", var password: String = "",
                var name: String = "", var encodedPassword: String = "",
                var trackList: List<MusicListClass> = List<MusicListClass>(0, { MusicListClass() }),
                var likedList: List<MusicListClass> = List<MusicListClass>(0, { MusicListClass() }))

sealed class defaultFrame(var status: String, var message: String)

data class ForUserInfo(var nickName: String = "", var trackList: List<MusicListClass>,
                       var likedList: List<MusicListClass>) : defaultFrame("","")

data class MusicListClass(var id: String = "", var name: String = "", var owner: String = "",
                          var updated_date: String = "", var downloads: Int = 0, var likes: Int = 0)

data class Result(var refreshToken: String = "", var accessToken: String = "") : defaultFrame("","")

data class FeedResult(var feed: List<List<MusicListClass>>) : defaultFrame("",""), Serializable

data class SearchResult(var searchResult: List<List<MusicListClass>>) : defaultFrame("",""), Serializable