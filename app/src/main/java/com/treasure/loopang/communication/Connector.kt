package com.treasure.loopang.communication

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket

class Connector(val DNS: String, val port: Int, var socket: Socket = Socket(),
                var inStream: InputStream? = null, var outStream: OutputStream? = null) {
    private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun makeSocket() {
        socket = Socket(InetAddress.getByName(DNS), port)
        inStream = socket.getInputStream()
        outStream = socket.getOutputStream()
    }

    fun makeJwtHeader() {

    }

    fun makeJwtPayload() {

    }

    fun makeJwtSignature() {

    }

    fun verifyJws() {
        
    }
}