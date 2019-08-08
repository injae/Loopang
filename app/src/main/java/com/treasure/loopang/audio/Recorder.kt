package com.treasure.loopang.audio

import com.treasure.loopang.audio.format.FormatInfo
import com.treasure.loopang.audio.format.IFormat
import com.treasure.loopang.audio.format.Pcm16
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
            var sampleCounter: Int = 0
            while(isRecording.get()) {
                info.inputAudio.read(buffer,0, buffer.size)
                buffer = effect(buffer)
                maxSize?.let { buffer.forEach { if(maxSize > data.size) data.add(it) else isRecording.set(false) } }
                            ?: buffer.forEach { data.add(it) }

                var currentSampleCount = data.chunked(info.sampleRate).count() - 1
                if(sampleCounter < currentSampleCount) {
                    sampleCounter = currentSampleCount
                    var minSample = (currentSampleCount-1)*info.sampleRate
                    var maxSample = (currentSampleCount*info.sampleRate)- 1
                    var sampleData = timeEffect(data.subList(minSample, maxSample).toShortArray())

                    var count: Int = 0
                    var scount: Int = minSample
                    do { data[scount++] = sampleData[count++] } while(scount > maxSample)
                }

            }
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
        return export
    }
}