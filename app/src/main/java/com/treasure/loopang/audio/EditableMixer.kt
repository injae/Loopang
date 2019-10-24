package com.treasure.loopang.audio

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


class SoundRange(var sound: Sound, var cycle: Int = 0, var start: Int = 0, var end: Int = 0, var repeat: Int = 0) {
    var soundLength = sound.data.size
    fun expand(size: Int) {
        end += size
        while(end >= soundLength) {
            end -= soundLength
            repeat += 1
        }
    }

    fun remove(start: Int) {
        var buf = SoundRange(sound, cycle, this.start)
        buf.expand(start-(cycle * this.start))
        end = buf.end
        repeat = buf.repeat
    }

    fun startIndex(): Int{
        return cycle*start
    }

    fun endIndex(): Int{
        return (cycle*start) + (end*repeat)
    }

    fun startDuration(): Int {
        return startIndex() / sound.info.tenMsSampleRate
    }

    fun endDuration(): Int {
        return  endIndex() / sound.info.tenMsSampleRate
    }

    fun isOverlap(other: SoundRange): Boolean{
        return cycle >= other.cycle
    }
}

class EditableSound {
    var isMute: Boolean = false
    var effectorIndex: Int
    var blocks: MutableList<SoundRange> = mutableListOf()
    var sound: Sound

    var editedRange: SoundRange
    var playedRange: SoundRange
    var playedIndex: Int

    var currentBlock: SoundRange? = null
    var isEdit = AtomicBoolean(false)

    constructor(sound: Sound) {
        this.sound = sound
        editedRange = SoundRange(sound, 0)
        playedRange = SoundRange(sound, 0)
        playedIndex = 0
        effectorIndex = sound.addEffector {
            if(isEdit.get()) editedRange.expand(it.size)
            it
        }
        sound.addTimeEffector {
            playedRange.expand(it.size)
            var compare =  playedRange.endIndex() - blocks[playedIndex].startIndex()
            if(compare > 0) {
                var zeroRange = it.size - compare
                for((index, data) in it.withIndex()) { if(index < zeroRange) it[index] = 0 }
            }
            else {
                compare = playedRange.endIndex() - blocks[playedIndex].endIndex()
                if(compare > 0) {
                    playedIndex++
                    var zeroRange = it.size - compare
                    for((index, data) in it.withIndex()) { if(index >= zeroRange) it[index] = 0 }
                }
                else if(compare == 0) playedIndex++
            }
            it
        }
    }

    fun play() {
        if(!isMute) sound.play()
    }

    fun seek(start: Int) {
        editedRange.remove(start)
        playedRange.remove(start)
    }

    fun startBlock() {
        if(!isMute) {
            sound.isMute.set(false)
            isEdit.set(true)
            currentBlock = SoundRange(sound, cycle=editedRange.repeat, start=editedRange.end)
            blocks = blocks.filter{ !it.isOverlap(currentBlock!!) }.toMutableList()
        }
    }

    fun endBlock() {
        if(!isMute) {
            sound.isMute.set(true)
            isEdit.set(false)
            currentBlock?.repeat = editedRange.repeat - currentBlock?.cycle!!
            currentBlock?.end = editedRange.end
            blocks.add(currentBlock!!)
        }
    }

    fun stop() {
        if(!isMute) sound.stop()
    }
}


class EditableMixer(var sounds: MutableList<EditableSound> = mutableListOf()) : SoundFlow<EditableMixer>() {
    var isLooping = AtomicBoolean(false)

    fun addSound(sound: Sound) {
        sounds.add(EditableSound(sound))
    }

    fun setMute(index: Int, isMute: Boolean) { sounds[index].isMute = isMute}

    fun start() {
        isLooping.set(true)
        callStart(this)
        launch { while(isLooping.get()) { sounds.map { async { it.play() } }.forEach{ it.await() }; callSuccess(this@EditableMixer) } }.start()
    }

    fun stop() {
        isLooping.set(false)
        callStop(this)
        sounds.forEach { it.stop() }
    }

    fun startBlock() {
        sounds.forEach{ it.startBlock() }

    }

    fun endBlock() {
        sounds.forEach{ it.endBlock() }
    }

    fun seek(tenMs: Int) {
        sounds.forEach{
            it.seek(tenMs * sounds[0].sound.info.tenMsSampleRate)
        }
    }

    fun duration(): Int {
        var duration = 0
        sounds.map{ it.blocks.last().endDuration() }.forEach {
            if(duration < it)  duration = it
        }
        return duration
    }

    fun stop(index: Int) { sounds[index].stop() }
}