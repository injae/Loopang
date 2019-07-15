package com.treasure.loopang.audiov2.format

import android.media.*


interface IFormat {
    fun info() : FormatInfo
    fun encord(data: MutableList<Short>): MutableList<Byte>
    fun decord(data: MutableList<Byte>): MutableList<Short>

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
    override fun decord(data: MutableList<Byte>): MutableList<Short> {
        error("Not Support Format")
    }

    override fun encord(data: MutableList<Short>): MutableList<Byte> {
        error("Not Support Format")
    }

    override fun info() = FormatInfo()
}

fun formatFactory(path: String) : IFormat {
    return when (path.substringAfter('.',"")) {
        "pcm" -> Pcm16()
        "wav"-> Wave()
        else  -> NotSupportFormat()
    }
}

