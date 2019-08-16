package com.treasure.loopang.Database

import androidx.room.*

@Dao
interface TokenDao {
    @Query("SELECT * FROM Tokens")
    fun getAllTokens(): List<TokenEntity>

    @Query("SELECT * FROM Tokens LIMIT 1")
    fun getToken(): TokenEntity

    @Query("DELETE FROM Tokens")
    fun DeleteToken()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToken(token: TokenEntity)
}