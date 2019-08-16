package com.treasure.loopang.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tokens")
class TokenEntity( @PrimaryKey var refreshToken: String )
{ constructor(): this("") }