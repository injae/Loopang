package com.treasure.loopang.audiov2.format

import com.treasure.loopang.audiov2.convertBytesToShort
import com.treasure.loopang.audiov2.convertShortArrayToByteArray


open class Pcm16(private var info: FormatInfo = FormatInfo()) : IFormat {
    override fun info() = info
    override fun decord(data: MutableList<Byte>): MutableList<Short> {
        return data.chunked(2).map{ it.toByteArray() }.flatMap { listOf(convertBytesToShort(it)) }.toMutableList()
    }

    override fun encord(data: MutableList<Short>): MutableList<Byte> {
        return convertShortArrayToByteArray(data.toShortArray()).toMutableList()
    }

}

