package com.treasure.loopang.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TokenDao {
    @Query("SELECT * FROM Tokens")
    fun getAllTokens(): List<TokenEntity>

    @Insert
    fun insertToken(token: String)

    @Update
    fun updateToken(token: String)

    @Query("DELETE FROM Tokens")
    fun DeleteToken()

    @Query("SELECT refreshToken FROM Tokens WHERE refreshToken = :token")
    fun getToken(token: String)
}