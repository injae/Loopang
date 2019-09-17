package com.treasure.loopang.Database

import androidx.room.*

@Dao
interface TokenDao {
    @Query("SELECT * FROM TokenTable LIMIT 1")
    fun getToken(): TokenEntity

    @Query("DELETE FROM TokenTable")
    fun deleteToken()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToken(token: TokenEntity)
}

@Dao
interface EmailDao {
    @Query("SELECT * FROM EmailTable LIMIT 1")
    fun getEmail(): EmailEntity

    @Query("DELETE FROM EmailTable")
    fun deleteEmail()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEmail(email: EmailEntity)
}

@Dao
interface PasswordDao {
    @Query("SELECT * FROM PasswordTable LIMIT 1")
    fun getPassword(): PasswordEntity

    @Query("DELETE FROM PasswordTable")
    fun deletePassword()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPassword(email: PasswordEntity)
}