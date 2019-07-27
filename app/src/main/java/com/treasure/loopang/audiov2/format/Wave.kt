package com.treasure.loopang.audiov2.format

import com.treasure.loopang.audiov2.convertByteArrayToShortArray
import com.treasure.loopang.audiov2.convertShortArrayToByteArray

class Wave(private var info: FormatInfo = FormatInfo()) : IFormat {
    override fun info() = info
    override fun encord(data: MutableList<Short>) = getWavData(data)
    override fun decord(data: MutableList<Byte>) = data.chunked(info.inputBufferSize).flatMap { convertByteArrayToShortArray(it.toByteArray()).toList() }.toMutableList()

    private var index = 0
    private fun getWavData(data: MutableList<Short>): MutableList<Byte> {
        val wavHeader = ByteArray(44)
        val pcmData = convertShortArrayToByteArray(data.toShortArray())

        insertString(wavHeader, "RIFF")
        insertInt(wavHeader, (36 + pcmData.size))
        insertString(wavHeader,"WAVE")
        insertString(wavHeader, "fmt ")
        insertInt(wavHeader, 16)
        insertShort(wavHeader, 1)
        insertShort(wavHeader, 1)
        insertInt(wavHeader, 44100)
        insertInt(wavHeader, (44100 * 2))
        insertShort(wavHeader, 2)
        insertShort(wavHeader, 16)
        insertString(wavHeader, "data")
        insertInt(wavHeader, pcmData.size)
        index = 0

        return copyData(wavHeader, pcmData)
    }

    private fun insertString(wavHeader: ByteArray, text: String) {
        text.forEach { wavHeader[index++] = it.toByte() }
    }

    private fun insertInt(wavHeader: ByteArray, number: Int) {
        wavHeader[index++] = number.toByte()
    }

    private fun insertShort(wavHeader: ByteArray, number: Short) {
        wavHeader[index++] = number.toByte()
    }

    private fun copyData(wavHeader: ByteArray, pcmData: ByteArray) = (wavHeader + pcmData).toMutableList()
}