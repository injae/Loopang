package com.treasure.loopang.audio

import android.media.AudioTrack
import android.media.AudioManager
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

class Player(var sound: Sound, val audio: AudioTrack = sound.makeAudioTrack()){
    var isPlaying = AtomicBoolean(false)
    var isLooping = AtomicBoolean(false)

    fun start() {
        if(!isPlaying.get()) {
            audio.play()
            val bufferSize = sound.outputBufferSize
            Thread{
                isPlaying.set(true)
                do {
                    val subLists = sound.data.size / bufferSize
                    var sliceSize = bufferSize
                    for (i in 0 until subLists) {
                        if(i == subLists-1) sliceSize = sound.data.size % bufferSize
                        val array = sound.data.subList(i* bufferSize,(i*bufferSize) + sliceSize).toShortArray()
                        Log.d("AudioTest","${i+sliceSize}")
                        audio.write(array,0, array.size)
                    }
                } while(isLooping.get())
                isPlaying.set(false)
            }.start()
        }
    }
    fun n_start() {
        if(!isPlaying.get()) {
            audio.play()
            val bufferSize = sound.outputBufferSize
                isPlaying.set(true)
                    val subLists = sound.data.size / bufferSize
                    var sliceSize = bufferSize
                    for (i in 0 until subLists) {
                        if(!isPlaying.get()) break;
                        if(i == subLists-1) sliceSize = sound.data.size % bufferSize
                        val array = sound.data.subList(i* bufferSize,(i*bufferSize) + sliceSize).toShortArray()
                        Log.d("AudioTest","${i+sliceSize}")
                        audio.write(array,0, array.size)
                    }
                isPlaying.set(false)
        }
    }


    fun stop() {
        if(isPlaying.get()) {
            isPlaying.set(false)
            isLooping.set(false)
            audio.stop()
            //audioTrack.release()
        }
    }
}