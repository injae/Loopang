package com.treasure.loopang.communication

import java.security.MessageDigest

fun parseMap(key: String, obj: String) = mapOf<String, String>(key to obj)
fun disassembleMap(map: Map<String, String>, key: String) = map.get(key)

fun makeSHA256(target: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digested = md.digest(target.toByteArray())
    return digested.fold("", { string, it -> string + "%02x".format(it) })
}