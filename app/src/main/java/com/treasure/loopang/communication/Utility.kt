package com.treasure.loopang.communication

import android.util.Base64
import java.security.MessageDigest

fun parseMap(key: String, obj: String) = mapOf<String, String>(key to obj)
fun disassembleMap(map: Map<String, String>, key: String) = map.get(key)

fun makeSHA256(target: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digested = md.digest(target.toByteArray())
    return digested.fold("", { string, it -> string + "%02x".format(it) })
}

fun encodeBase64(target: String) = Base64.encodeToString(target.toByteArray(), Base64.DEFAULT)
fun decodeBase64(target: String) = String(Base64.decode(target.toByteArray(), Base64.DEFAULT))