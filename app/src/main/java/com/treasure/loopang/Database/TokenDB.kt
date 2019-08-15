package com.treasure.loopang.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TokenEntity::class], version = 1)
abstract class TokenDB : RoomDatabase() {
    abstract fun tokenDao(): TokenDao

    companion object {
        private var INSTANCE: TokenDB? = null

        fun getInstance(context: Context): TokenDB? {
            if(INSTANCE == null) {
                synchronized(TokenDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        TokenDB::class.java, "tokens.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }

    fun destroyInstance() { INSTANCE = null }
}