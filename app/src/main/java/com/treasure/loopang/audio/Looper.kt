package com.treasure.loopang.audio

import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.schedule

class Looper {
    val recorder = Recorder(Sound())
    var mixer = Mixer()
    var maxSize : Int = 0
    var recordCount  : AtomicInteger = AtomicInteger(-1)
    var mixerCount : Int = -1

    fun recordAction() {
        if(mixerCount == -1) return;
        var currentCount = recordCount.get()+1
        recordCount.set(currentCount)
        Log.d("AudioTest", "RecordCount: ${currentCount}")
        when(currentCount){
            0 -> {
                runBlocking {
                    var sound = recorder.stop()
                    maxSize = sound.data.size
                    mixer.addSound(sound)
                }
                mixer.start()
            }
            1 -> {
                recorder.start(maxSize)
                launch {
                    recorder.routine.await()
                    mixer.addSound(recorder.getSource())
                    recordCount.set(0)
                }
            }
        }

    }

    fun mixerAction() {
        mixerCount++
        Log.d("AudioTest", "MixerCount: ${mixerCount}")
        when(mixerCount){
            0 -> {
                recorder.start()
            }
            1 -> {
                runBlocking { recorder.stop();  }
                mixer.stop()
            }
            2 -> {
                mixer.start()
                mixerCount = 0
            }
        }
    }

    fun checkRecordingState() :Boolean = recorder.isRecording.get()

}