package com.treasure.loopang.audiov2.format

import com.treasure.loopang.audiov2.convertByteArrayToShortArray
import com.treasure.loopang.audiov2.convertShortArrayToByteArray


open class Pcm16(private var info: FormatInfo = FormatInfo()) : IFormat {
    override fun info() = info
    override fun encord(data: ShortArray) = convertShortArrayToByteArray(data)
    override fun decord(data: ByteArray) = convertByteArrayToShortArray(data)
}