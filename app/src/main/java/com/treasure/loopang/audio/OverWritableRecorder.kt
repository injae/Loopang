package com.treasure.loopang.audio

import android.util.Log
import com.treasure.loopang.audio.format.FormatInfo
import com.treasure.loopang.audio.format.IFormat
import com.treasure.loopang.audio.format.Pcm16
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

class OverWritableRecorder (var format: IFormat = Pcm16(),
                            var info: FormatInfo =format.info(),
                            var data: MutableList<Short> = mutableListOf(),
                            var isRecording: AtomicBoolean = AtomicBoolean(false),
                            var blocks: MutableList<SoundRange> = mutableListOf(),
                            var currentBlock: SoundRange = SoundRange(Sound()),
                            var isMute: AtomicBoolean  = AtomicBoolean(false),
                            var limit: Int? = null
                            ): SoundFlow<OverWritableRecorder>() {
    lateinit var routine : Deferred<Unit>


    fun start() {
        Stabilizer.stabilizeAudio(info.inputAudio)
        info.inputAudio.startRecording()
        callStart(this)
        routine = async {
            isRecording.set(true)
            var buffer = ShortArray(info.inputBufferSize)
            var sampleCounter: Int = 0
            while(isRecording.get()) {
                info.inputAudio.read(buffer,0, buffer.size)
                if(isMute.get()) { buffer = ShortArray(buffer.size, { 0 }) }
                buffer = effect(buffer)
                limit?.let { buffer.forEach { if(limit!! > data.size) data.add(it) else isRecording.set(false) } }
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
            callSuccess(this@OverWritableRecorder)
        }
    }

    fun seek(tenMs: Int) {
        var index = tenMs*info.tenMsSampleRate
        currentBlock = SoundRange(Sound())
        currentBlock.cycle = index / currentBlock.sound.data.size
        currentBlock.start = index % currentBlock.sound.data.size
        blocks = blocks.filter{ !it.isOverlap(currentBlock) }.toMutableList()

        if(index >= data.size) {
            var zeroBuf = ShortArray(index -data.size, { 0 })
            data.addAll(zeroBuf.toMutableList())
        }
        else {
             data = data.subList(0, index)
        }
    }

    fun stop(limit:Int? = null){
        if(isRecording.get()) {
            if(limit != null) {
                Log.d("AudioTest","limit ${limit}")
                this.limit = limit
                while(isRecording.get()) { }
            }
            isRecording.set(false)
            info.inputAudio.stop()
            runBlocking { routine.await() }
            Log.d("AudioTest", "in data.size ${data.size}")
            currentBlock.expand(data.size)
            blocks.add(currentBlock)
            currentBlock = SoundRange(Sound())
            callStop(this)
        }
    }

    fun getSound(): Sound {
        var export = Sound(data)
        data = mutableListOf()
        return export
    }
}