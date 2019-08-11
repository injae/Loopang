package com.treasure.loopang.communication

import android.util.AtomicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

class TradeManager(connector: Connector) {
    private val inStream = connector.inStream
    private val outStream = connector.outStream

    fun receiveByte() = inStream?.read()?.toByte()
    fun receiveByteArray() = inStream?.readBytes()
    fun receiveString(): String {
        val data = ByteArray(inStream!!.available())
        inStream.read(data)
        return String(data)
    }
    fun receiveFile(): ByteArray {
        var tempData = ByteArray(0)
        GlobalScope.launch(Dispatchers.Default) {
            while(inStream?.available()?.compareTo(0) != 0) {
                val buffer = ByteArray(1024)
                inStream?.read(buffer)
                tempData += buffer
            }
        }
        return tempData
    }


    fun sendByte(target: Byte) {
        val temp = ByteArray(1, {target})
        outStream?.write(temp)
    }
    fun sendByteArray(target: ByteArray) { outStream?.write(target) }
    fun sendString(target: String) { outStream?.write(target.toByteArray()) }
    fun sendFile(path: String) {
        val file = AtomicFile(File(path))
        val fileInputStream = FileInputStream(file.baseFile)
        GlobalScope.launch(Dispatchers.Default) {
            while(fileInputStream.available() > 0) {
                val buffer = ByteArray(1024)
                fileInputStream.read(buffer)
                outStream?.write(buffer)
                outStream?.flush()
            }
        }
    }
}