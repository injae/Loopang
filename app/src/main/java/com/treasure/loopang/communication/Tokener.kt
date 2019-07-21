package com.treasure.loopang.communication

import java.io.BufferedInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket

class Tokener(val dns: String = "DNS넣으면됌", val port: Int = 38298, var socket: Socket,
              var outStream: OutputStream, var inStream: InputStream) {

    fun connectSocket() {
        socket = Socket(getIpAddress(), port)
        outStream = socket.getOutputStream()
        inStream = socket.getInputStream()
    }

    fun getAccessToken() {

    }

    fun getRefreshToken() {

    }

    fun checkConnection() {

    }

    fun getIpAddress() = InetAddress.getByName(dns)
}