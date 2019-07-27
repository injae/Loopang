package com.treasure.loopang.audiov2.format

import android.util.Log
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

        wavHeader[0] = 'R'.toByte()
        wavHeader[1] = 'I'.toByte()
        wavHeader[2] = 'F'.toByte()
        wavHeader[3] = 'F'.toByte()

        wavHeader[4] = ((pcmData.size + 36) and 0xff).toByte()
        wavHeader[5] = (((pcmData.size + 36) shr 8) and 0xff).toByte()
        wavHeader[6] = (((pcmData.size + 36) shr 16) and 0xff).toByte()
        wavHeader[7] = (((pcmData.size + 36) shr 24) and 0xff).toByte()

        wavHeader[8] = 'W'.toByte()
        wavHeader[9] = 'A'.toByte()
        wavHeader[10] = 'V'.toByte()
        wavHeader[11] = 'E'.toByte()
        wavHeader[12] = 'f'.toByte()
        wavHeader[13] = 'm'.toByte()
        wavHeader[14] = 't'.toByte()
        wavHeader[15] = ' '.toByte()

        wavHeader[16] = 16.toByte()
        wavHeader[17] = 0.toByte()
        wavHeader[18] = 0.toByte()
        wavHeader[19] = 0.toByte()
        wavHeader[20] = 1.toByte()
        wavHeader[21] = 0.toByte()
        wavHeader[22] = 1.toByte()
        wavHeader[23] = 0.toByte()

        wavHeader[24] = (44100 and 0xff).toByte()
        wavHeader[25] = ((44100 shr 8) and 0xff).toByte()
        wavHeader[26] = ((44100 shr 16) and 0xff).toByte()
        wavHeader[27] = ((44100 shr 24) and 0xff).toByte()

        wavHeader[28] = (88200 and 0xff).toByte()
        wavHeader[29] = ((88200 shr 8) and 0xff).toByte()
        wavHeader[30] = ((88200 shr 16) and 0xff).toByte()
        wavHeader[31] = ((88200 shr 24) and 0xff).toByte()

        wavHeader[32] = 2.toByte()
        wavHeader[33] = 0.toByte()
        wavHeader[34] = 16.toByte()
        wavHeader[35] = 0.toByte()

        wavHeader[36] = 'd'.toByte()
        wavHeader[37] = 'a'.toByte()
        wavHeader[38] = 't'.toByte()
        wavHeader[39] = 'a'.toByte()

        wavHeader[40] = (pcmData.size and 0xff).toByte()
        wavHeader[41] = ((pcmData.size shr 8) and 0xff).toByte()
        wavHeader[42] = ((pcmData.size shr 16) and 0xff).toByte()
        wavHeader[43] = ((pcmData.size shr 24) and 0xff).toByte()

        return copyData(wavHeader, pcmData)
    }

    private fun copyData(wavHeader: ByteArray, pcmData: ByteArray) = (wavHeader + pcmData).toMutableList()
}