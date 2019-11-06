package com.treasure.loopang.audio

import io.github.junyuecao.soundtouch.SoundTouch
import kotlin.experimental.and
import kotlin.experimental.or

class SoundEffector(private var channel: Int = 1, private var sampleRate: Int = 44100,
                    private var tempo: Double = 1.0, private var pitch: Double = 1.0,
                    private var st: SoundTouch? = null,
                    private var sourceBackup: MutableList<Pair<ByteArray, ByteArray>> = MutableList<Pair<ByteArray, ByteArray>>(0, { Pair(ByteArray(0, {0}), ByteArray(0, {0})) }),
                    private var volumeBackup: MutableList<Pair<ByteArray, ByteArray>> = MutableList<Pair<ByteArray, ByteArray>>(0, { Pair(ByteArray(0, {0}), ByteArray(0, {0})) })) {
    private fun preSetting() {
        st = SoundTouch()
        st?.setChannels(channel)
        st?.setSampleRate(sampleRate)
        st?.setTempo(tempo)
    }

    private fun setChannel(cn: Int) { st?.setChannels(cn) }
    private fun setSampleRate(sr: Int) { st?.setSampleRate(sr) }
    private fun setTempo(tp: Double) { st?.setTempo(tp) }
    private fun setPitch(pt: Double) { st?.setPitch(pt) }

    fun process(targetSource: ByteArray, preset: EffectorPresets): ByteArray {
        preSetting()
        when(preset) {
            EffectorPresets.SCARY -> { setPitch(0.5) }
            EffectorPresets.SINKING -> { setPitch(0.8) }
            EffectorPresets.EXCITING -> { setPitch(1.2) }
            EffectorPresets.FANTASTIC -> { setPitch(1.4) }
            EffectorPresets.NONE -> {
                return getSourceData(targetSource)

            }
        }
        st?.putSamples(targetSource, targetSource.size)

        val result = MutableList<Byte>(0, {0})
        var bufferSize: Int
        val tempBuffer = ByteArray(8192)

        do {
            bufferSize = st!!.receiveSamples(tempBuffer, 8192)
            if (bufferSize > 0) {
                for (i in 0 until bufferSize) {
                    result.add(tempBuffer[i])
                }
            }
        } while(bufferSize != 0)

        st?.release()
        st = null
        st = SoundTouch()

        sourceBackup?.add(Pair(targetSource, result.toByteArray().copyOfRange(0, (result.size / 4))))
        return result.toByteArray()
    }

    private fun getSourceData(processedData: ByteArray): ByteArray {
        var flag: Boolean
        var index = 0
        for(i in 0 until sourceBackup!!.size) {
            flag = true
            for(j in 0 until (processedData.size / 4)) {
                if(sourceBackup[i].second[j] != processedData[j]) flag = false
            }
            if(flag) {
                index = i
                break
            }
        }
        return sourceBackup[index].first
    }
}

enum class EffectorPresets { NONE, SCARY, SINKING, EXCITING, FANTASTIC }

fun volumeControl(sourceData: ByteArray, volume: Int): ByteArray {
    val modifiedData = ByteArray(sourceData.size)
    val realVolume = volume / 100.0f
    for(i in 0 until sourceData.size step 2) {
        var buf1 = sourceData[i + 1].toShort()
        var buf2 = sourceData[i].toShort()

        buf1 = ((buf1 and 0xff).toInt() shl 8).toShort()
        buf2 = (buf2 and 0xff)

        var temp = (buf1 or buf2)
        temp = (temp * realVolume).toShort()

        modifiedData[i] = temp.toByte()
        modifiedData[i + 1] = (temp.toInt() shr 8).toByte()
    }

    return modifiedData
}