package com.treasure.loopang.audio

import android.media.AudioRecord
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun convertShortToByte(short: Short): ByteArray {
    val array = ByteArray(2)
    val byte = short.toInt()
    array[0] = (byte and 0xff).toByte()
    array[1] = ((byte shr 8) and 0xff).toByte()
    return array
}

fun convertShortArrayToByteArray(array: ShortArray): ByteArray {
    val byte_array = ByteArray(array.size * 2)
    var index = 0
    array.forEach {
        val bytes = convertShortToByte(it)
        byte_array[index]  = bytes[0]
        byte_array[index + 1]= bytes[1]
        index += 2
    }
    return byte_array
}

fun convertByteArrayToShortArray(array: ByteArray): ShortArray {
    return array.asList()
                .chunked(2)
                .map { (l, h) ->
                    var bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                    bb.put(array[0]).put(array[1]).getShort(0)
                }
                .toShortArray()
}

fun convertBytesToShort(array: ByteArray) : Short {
    var bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
    return bb.put(array[0]).put(array[1]).getShort(0)
}


object Stabilizer {
    var ns: NoiseSuppressor? = null
    var aec: AcousticEchoCanceler? = null
    var agc: AutomaticGainControl? = null

    fun stabilizeAudio(audioRecord: AudioRecord) {
        if(NoiseSuppressor.isAvailable()) {
            ns = NoiseSuppressor.create(audioRecord.audioSessionId)
            ns?.enabled = true
        }

        if(AcousticEchoCanceler.isAvailable()) {
            aec = AcousticEchoCanceler.create(audioRecord.audioSessionId)
            aec?.enabled = true
        }

        if(AutomaticGainControl.isAvailable()) {
            agc = AutomaticGainControl.create(audioRecord.audioSessionId)
            agc?.enabled = true
        }
    }
}