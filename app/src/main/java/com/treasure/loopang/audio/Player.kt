package com.treasure.loopang.audio

import android.media.AudioTrack
import android.media.AudioManager
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

class Player( val sound: Sound
            , val bufferSize: Int = sound.outputBufferSize
            , val audioTrack : AudioTrack = AudioTrack( AudioManager.STREAM_MUSIC, sound.sampleRate, sound.outputChannel
                                                      , sound.audioFormat, sound.outputBufferSize, AudioTrack.MODE_STREAM)){
    var isPlaying = AtomicBoolean(false)
    var isLooping = AtomicBoolean(false)

    fun start() {
        if(sound.data != null && !isPlaying.get()) {
            audioTrack.play()
            Thread{
                isPlaying.set(true)
                do {
                    val subLists = sound.data!!.size / bufferSize
                    var sliceSize = bufferSize
                    for (i in 0 until subLists) {
                        if(i == subLists-1) sliceSize = sound.data!!.size % bufferSize
                        val array = sound.data!!.subList(i* bufferSize,(i*bufferSize) + sliceSize).toShortArray()
                        Log.d("AudioTest","${i+sliceSize}")
                        audioTrack.write(array,0, array.size)
                    }
                } while(isLooping.get())
                isPlaying.set(false)
            }.start()
        }
    }

    fun stop() {
        if(isPlaying.get()) {
            isPlaying.set(false)
            audioTrack.stop()
            //audioTrack.release()
        }
    }
}