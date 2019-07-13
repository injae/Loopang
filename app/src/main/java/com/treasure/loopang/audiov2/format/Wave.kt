package com.treasure.loopang.audiov2.format

import com.treasure.loopang.audiov2.convertShortArrayToByteArray

class Wave(private var info: FormatInfo = FormatInfo()) : IFormat {
    override fun info() = info
    override fun encord(data: MutableList<Short>) = getWavData(data)

    override fun decord(data: MutableList<Byte>): MutableList<Short> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getWavData(data: MutableList<Short>): MutableList<Byte> {
        val wavData = MutableList<Byte>(1,{0})

        // Wave file header insertion procedure
        insertString(wavData, "RIFF")
        insertInt(wavData, wavData.size)
        insertString(wavData,"WAVE")
        insertString(wavData, "fmt ")
        insertInt(wavData, 16)
        insertShort(wavData, 1)
        insertShort(wavData, 1)
        insertInt(wavData, info.sampleRate)
        insertInt(wavData, info.sampleRate * 2)
        insertShort(wavData, 2)
        insertShort(wavData, 16)

        // Audio data insertion procedure
        insertString(wavData, "data")
        insertInt(wavData, data.size)
        copyData(data, wavData)

        return wavData
    }

    private fun insertString(wavData: MutableList<Byte>, text: String) {
        text.forEach {
            wavData.add(it.toByte())
        }
    }

    private fun insertInt(wavData: MutableList<Byte>, number: Int) {
        wavData.add(number.toByte())
    }

    private fun insertShort(wavData: MutableList<Byte>, number: Short){
        wavData.add(number.toByte())
    }

    private fun copyData(pcmData: MutableList<Short>, wavData: MutableList<Byte>) {
        val temp = convertShortArrayToByteArray(pcmData.toShortArray())
        temp.forEach {
            wavData.add(it)
        }
    }
}