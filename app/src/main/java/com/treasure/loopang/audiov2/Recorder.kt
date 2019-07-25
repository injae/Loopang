package com.treasure.loopang.audiov2

import android.util.Log
import com.treasure.loopang.audio.Stabilizer
import com.treasure.loopang.audiov2.format.FormatInfo
import com.treasure.loopang.audiov2.format.IFormat
import com.treasure.loopang.audiov2.format.Pcm16
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

class Recorder(var format: IFormat  = Pcm16(),
               var info: FormatInfo =format.info(),
               var data: MutableList<Short> = mutableListOf(),
               var isRecording: AtomicBoolean = AtomicBoolean(false)) : SoundFlow<Recorder>() {
    lateinit var routine : Deferred<Unit>;

    fun start(maxSize: Int? = null) {
        Stabilizer.stabilizeAudio(info.inputAudio)
        info.inputAudio.startRecording()
        callStart(this)
        routine = async {
            isRecording.set(true)
            var buffer = ShortArray(info.inputBufferSize)
            while(isRecording.get()) {
                info.inputAudio.read(buffer,0, buffer.size)
                buffer = effect(buffer)
                maxSize?.let { buffer.forEach { if(maxSize > data.size) data.add(it) else isRecording.set(false) } }
                            ?: buffer.forEach { data.add(it) }
            }
            data = data.chunked(info.sampleRate).map{ effect(it.toShortArray()) }.flatMap { it.toList() }.toMutableList()
            callSuccess(this@Recorder)
        }
    }

    fun stop() : Sound {
        if(isRecording.get()) {
            isRecording.set(false)
            info.inputAudio.stop()
            runBlocking { routine.await() }
            callStop(this)
        }
        return getSound()
    }

    fun getSound(): Sound {
        var export = Sound(data)
        data = mutableListOf()
        Log.d("SoundTest", "sound to time ${export.to_time()}")
        return export
    }
}