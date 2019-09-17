package com.treasure.loopang.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TokenTable")
class TokenEntity(@PrimaryKey var refreshToken: String = "")
{ constructor(): this("") }

@Entity(tableName = "EmailTable")
class EmailEntity(@PrimaryKey var email: String = "")
{ constructor(): this("") }

@Entity(tableName = "PasswordTable")
class PasswordEntity(@PrimaryKey var password: String = "")
{ constructor(): this("") }