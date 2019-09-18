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

fun encodeYuni(target: String): String {
    var encodedTarget = ""
    for(chr in encodeBase64(encodeBase64(encodeBase64(target)))) {
        if(chr.isUpperCase()) encodedTarget += chr.toLowerCase()
        else if(chr.isLowerCase()) encodedTarget += chr.toUpperCase()
        else encodedTarget += chr
    }
    return encodedTarget
}
fun decodeYuni(target: String): String {
    var decodedTarget = ""
    for(chr in target) {
        if(chr.isUpperCase()) decodedTarget += chr.toLowerCase()
        else if(chr.isLowerCase()) decodedTarget += chr.toUpperCase()
        else decodedTarget += chr
    }
    return decodeBase64(decodeBase64(decodeBase64(decodedTarget)))
}