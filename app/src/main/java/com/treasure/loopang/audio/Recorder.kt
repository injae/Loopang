package com.treasure.loopang.audio

import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

class Recorder( val sound: Sound
              , val bufferSize: Int = sound.inputBufferSize
              , val audioRecord: AudioRecord = AudioRecord( MediaRecorder.AudioSource.MIC, sound.sampleRate
                                                          , sound.inputChannel ,sound.audioFormat, sound.inputBufferSize)){
    var isRecording = AtomicBoolean(true)

    fun start() {
        Stabilizer.stabilizeAudio(audioRecord)
        audioRecord.startRecording()
        Thread{
            val data = ShortArray(bufferSize)
            isRecording.set(true)
            while(isRecording.get()) {
                val size = audioRecord.read(data,0, bufferSize)
                Log.d("AudioTest","record size: ${size}")
                data.forEach { sound.data.add(it) }
            }
            isRecording.set(false)
        }.start()
    }

    fun stop() : Sound {
        isRecording.set(false)
        audioRecord.stop()
        Log.d("AudioTest", "Recorder sound size: ${sound.data.size}")
        var export = Sound(sound.data, sound.sampleRate, sound.inputChannel, sound.outputChannel, sound.audioFormat, sound.inputBufferSize, sound.outputBufferSize)
        sound.data = mutableListOf()
        return export;
        //audioRecord.release()
    }
}