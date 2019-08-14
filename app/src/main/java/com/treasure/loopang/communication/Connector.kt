package com.treasure.loopang.communication

import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket

class Connector(val DNS: String, val port: Int, var socket: Socket = Socket(),
                var inStream: InputStream? = null, var outStream: OutputStream? = null) {
    fun setSocket() {
        socket = Socket(InetAddress.getByName(DNS), port)
        inStream = socket.getInputStream()
        outStream = socket.getOutputStream()
    }
}