package com.treasure.loopang.audiov2

import android.media.AudioRecord
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import java.io.*
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
                .map { (l, h) -> (l.toInt() + h.toInt().shl(8)).toShort() }
                .toShortArray()
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

fun PCMtoWAV(pcmFile: File, destPath: String) {
    val pcmData = ByteArray(pcmFile.length().toInt())
    val dis = DataInputStream(FileInputStream(pcmFile))
    dis.read(pcmData)
    val dos = DataOutputStream(FileOutputStream(destPath))

    dos.writeChars("RIFF")                      //  Chunk ID
    dos.writeInt(pcmFile.length().toInt() + 36) //  Chunk size
    dos.writeChars("WAVE")                      //  File format
    dos.writeChars("fmt ")                      //  Subchunk 1 ID
    dos.writeInt(16)                            //  Subchunk 1 size
    dos.writeShort(1)                           //  1 == PCM
    dos.writeShort(1)                           //  Number of channels
    dos.writeInt(44100)                         //  Sample rate
    dos.writeInt(44100 * 2)                     //  Byte rate
    dos.writeShort(2)                           //  Block align
    dos.writeShort(16)                          //  Bits per sample
    dos.writeChars("data")                      //  Subchunk 2 ID
    dos.writeInt(pcmData.size)                     //  Subchunk 2 size
    dos.write(pcmData)                             //  문제 발생시 Endian 변환 코드 넣어줘야함

    dos.close()
    dis.close()
}