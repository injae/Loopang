package com.treasure.loopang.communication

import com.treasure.loopang.audiov2.convertShortArrayToByteArray

class FileTransfer(val tokener: Tokener) {
    fun transfer(file: MutableList<Short>) {
        val target = convertShortArrayToByteArray(file.toShortArray())
        tokener.outStream.write(target)
    }
}