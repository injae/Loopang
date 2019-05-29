package com.treasure.loopang.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack


abstract class Sound( val data       : MutableList<Short> = mutableListOf()
                    , val sampleRate : Int = 44100
                    , val inputChannel : Int = AudioFormat.CHANNEL_IN_MONO
                    , val outputChannel: Int = AudioFormat.CHANNEL_OUT_MONO
                    , val audioFormat  : Int = AudioFormat.ENCODING_PCM_16BIT
                    , val inputBufferSize : Int = AudioRecord.getMinBufferSize(sampleRate,inputChannel,audioFormat)
                    , val outputBufferSize: Int = AudioTrack.getMinBufferSize(sampleRate,outputChannel,audioFormat)) : Cloneable {

    abstract fun save(path: String)
    abstract fun load(path: String)
    public override fun clone(): Sound {
        return super.clone() as Sound
    }
}