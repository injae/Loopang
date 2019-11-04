package com.treasure.loopang.communication

import java.io.Serializable

data class User(var email: String = "", var password: String = "",
                var name: String = "", var encodedPassword: String = "",
                var trackList: List<MusicListClass> = List<MusicListClass>(0, { MusicListClass() }),
                var likedList: List<MusicListClass> = List<MusicListClass>(0, { MusicListClass() }))

data class ForUserInfo(var status: String = "", var message: String = "",
                       var nickName: String = "", var trackList: List<MusicListClass>,
                       var likedList: List<MusicListClass>)

data class MusicListClass(var id: String = "", var name: String = "", var owner: String = "",
                          var updated_date: String = "", var downloads: Int = 0, var likes: Int = 0) : Serializable

data class Result(var status: String = "", var message: String = "",
                  var refreshToken: String = "", var accessToken: String = "")

data class FeedResult(var status: String = "", var message: String = "",
                      var likes_top: List<MusicListClass>, var download_top: List<MusicListClass>,
                      var recent_musics: List<MusicListClass>) : Serializable

data class SearchResult(var status: String = "", var message: String = "",
                        var searchResult: List<List<MusicListClass>>) : Serializable