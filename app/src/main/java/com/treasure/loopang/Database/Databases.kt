package com.treasure.loopang.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TokenEntity::class], version = 3)
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

@Database(entities = [EmailEntity::class], version = 2)
abstract class EmailDB : RoomDatabase() {
    abstract fun emailDao(): EmailDao

    companion object {
        private var INSTANCE: EmailDB? = null

        fun getInstance(context: Context): EmailDB? {
            if(INSTANCE == null) {
                synchronized(EmailDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        EmailDB::class.java, "email.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }

    fun destroyInstance() { INSTANCE = null }
}

@Database(entities = [PasswordEntity::class], version = 1)
abstract class PasswordDB : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao

    companion object {
        private var INSTANCE: PasswordDB? = null

        fun getInstance(context: Context): PasswordDB? {
            if(INSTANCE == null) {
                synchronized(PasswordDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        PasswordDB::class.java, "password.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }

    fun destroyInstance() { INSTANCE = null }
}