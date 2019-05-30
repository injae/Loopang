package com.treasure.loopang.audio

import android.media.AudioRecord
import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class Recorder( val sound: Sound
              , val bufferSize: Int = sound.inputBufferSize
              , val audioRecord: AudioRecord = sound.makeAudioRecord()){
    var isRecording = AtomicBoolean(true)
    lateinit var routine : Deferred<Unit>;

    fun start(maxSize: Int? = null) {
        Stabilizer.stabilizeAudio(audioRecord)
        audioRecord.startRecording()
        routine = async {
            val data = ShortArray(bufferSize)
            isRecording.set(true)
            while(isRecording.get()) {
                val size = audioRecord.read(data,0, bufferSize)
                data.forEach {
                    if(maxSize != null) {
                        if(maxSize > sound.data.size) { sound.data.add(it) }
                        else { isRecording.set(false) }
                    }
                    else sound.data.add(it)
                }
                //Log.d("AudioTest","record size: ${size}")
            }
            isRecording.set(false)
        }
        routine.start()
    }

    fun stop() : Sound {
        isRecording.set(false)
        audioRecord.stop()
        runBlocking { routine.await() }
        Log.d("AudioTest", "Recorder sound size: ${sound.data.size}")
        var export = Sound(sound.data, sound.sampleRate, sound.inputChannel, sound.outputChannel, sound.audioFormat, sound.inputBufferSize, sound.outputBufferSize)
        sound.data = mutableListOf()
        return export;
    }

    fun getSource() : Sound {
        Log.d("AudioTest", "Recorder sound size: ${sound.data.size}")
        var export = Sound(sound.data, sound.sampleRate, sound.inputChannel, sound.outputChannel, sound.audioFormat, sound.inputBufferSize, sound.outputBufferSize)
        sound.data = mutableListOf()
        return export;
    }
}