package com.treasure.loopang.audiov2.format

import com.treasure.loopang.audiov2.convertByteArrayToShortArray
import com.treasure.loopang.audiov2.convertShortArrayToByteArray


open class Pcm16(private var info: FormatInfo = FormatInfo()) : IFormat {
    override fun info() = info
    override fun decord(data: MutableList<Byte>): MutableList<Short> {
        return data.chunked(info.inputBufferSize).flatMap { convertByteArrayToShortArray(it.toByteArray()).toList() }.toMutableList()
    }

    override fun encord(data: MutableList<Short>): MutableList<Byte> {
        return data.chunked(info.outputBufferSize).flatMap { convertShortArrayToByteArray(it.toShortArray()).toList()}.toMutableList()
    }

}

