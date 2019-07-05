package com.treasure.loopang.audiov2.format

import android.media.*


interface IFormat {
    fun info() : FormatInfo
    fun encord(data: ShortArray): ByteArray
    fun decord(data: ByteArray): ShortArray
}

data class FormatInfo(
    val sampleRate : Int = 44100,
    val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT,

    val inputChannel :Int = AudioFormat.CHANNEL_IN_MONO,
    val inputBufferSize : Int = AudioRecord.getMinBufferSize(sampleRate,inputChannel,audioFormat),
    val inputAudio: AudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, inputChannel, audioFormat, inputBufferSize),

    val outputChannel: Int = AudioFormat.CHANNEL_OUT_MONO,
    val outputBufferSize: Int = AudioTrack.getMinBufferSize(sampleRate,outputChannel,audioFormat),
    val outputAudio: AudioTrack = AudioTrack( AudioManager.STREAM_MUSIC, sampleRate, outputChannel , audioFormat, outputBufferSize, AudioTrack.MODE_STREAM)
)

class NotSupportFormat : IFormat {
    override fun info() = FormatInfo()
    override fun encord(data: ShortArray): ByteArray { error("Not Supported File Format") }
    override fun decord(data: ByteArray): ShortArray { error("Not Supported File Format") }
}

fun formatFactory(path: String) : IFormat {
    return when(path.substringAfter('.',"")) {
    "pcm" -> Pcm16()
    else  -> NotSupportFormat()
    }
}

