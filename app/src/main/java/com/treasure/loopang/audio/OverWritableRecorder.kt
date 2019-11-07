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
                            var currentBlockIndex: Int? = null,
                            var playedRange: SoundRange = SoundRange(Sound()),
                            var isMute: AtomicBoolean  = AtomicBoolean(false),
                            var limit: Int? = null
                            ): SoundFlow<OverWritableRecorder>() {
    lateinit var routine : Deferred<Unit>


    fun start() {
        Stabilizer.stabilizeAudio(info.inputAudio)
        info.inputAudio.startRecording()
        callStart(this)
        startBlock()
        routine = async {
            isRecording.set(true)
            var buffer = ShortArray(info.inputBufferSize)
            var sampleCounter: Int = 0
            while(isRecording.get()) {
                info.inputAudio.read(buffer,0, buffer.size)
                if(isMute.get()) { buffer = ShortArray(buffer.size, { 0 }) }
                buffer = effect(buffer)
                var readCounter = 0
                limit?.let {
                    buffer.forEach { if(limit!! > data.size) { data.add(it); readCounter++ }
                                     else isRecording.set(false) } }
                 ?: buffer.forEach { data.add(it); readCounter++ }
                playedRange.expand(readCounter)

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

    fun startBlock() {
        if(!isMute.get()){
            var currentBlock = playedRange.nextRange()
            blocks = blocks.filter{ !it.removeOver(currentBlock) }.toMutableList()
            for((index, block) in blocks.withIndex()) {
                if(block.overWrite(currentBlock)) { currentBlockIndex = index }
            }
            if(currentBlockIndex == null) {
                blocks.add(currentBlock)
                currentBlockIndex = blocks.lastIndex
            }
        }
    }

    fun endBlock() {
        if(!isMute.get()) {
            var range = playedRange.endIndex() - blocks[currentBlockIndex!!].startIndex()
            blocks[currentBlockIndex!!].expand(range)
            currentBlockIndex = null
        }
    }

    fun seek(tenMs: Int) {
        var index = tenMs*info.tenMsSampleRate
        playedRange.remove(index)

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
            if(limit != null) { this.limit = limit } else { isRecording.set(false) }
            info.inputAudio.stop()
            runBlocking { routine.await() }
            endBlock()
            Log.d("AudioTest", "recorder recorded ${data.size}")
            Log.d("AudioTest", "recorder blocks:${blocks.size}")
            this.limit = null
            callStop(this)
        }
    }

    fun getSound(): Sound {
        var export = Sound(data)
        data = mutableListOf()
        return export
    }

    fun getEditableSound(): EditableSound {
        var voice = EditableSound(Sound(data))
        var range = SoundRange(voice.sound)
        range.expand(voice.sound.data.size)
        voice.blocks.add(range)
        return voice
    }

    fun getDumyBlocks(): EditableSound {
        var voice = EditableSound(getSound())
        voice.blocks = blocks
        return voice
    }
}