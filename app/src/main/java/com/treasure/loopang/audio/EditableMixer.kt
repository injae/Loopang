package com.treasure.loopang.audio

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean


class SoundRange( var sound: Sound, var cycle: Int = 0, var start: Int = 0, var end: Int = 0, var repeat: Int = 0) {
    var soundLength: Int

    init {
        if(sound.data.isEmpty()) sound.data = ShortArray(size=sound.info.sampleRate).toMutableList()
        soundLength = sound.data.size
    }

    fun remove(index: Int) {
        var buf = SoundRange(sound, cycle, start)
        buf.expand(index - startIndex())
        end = buf.end
        repeat = buf.repeat
    }

    fun size(): Int{
        return endIndex() - startIndex()
    }

    fun startIndex(): Int{
        return cycle*soundLength + start
    }

    fun endIndex(): Int{
        return (cycle*soundLength) + start + (repeat*soundLength) + end
    }

    fun startDuration(): Int {
        var sd = startIndex()
        if(sd == 0) return 0
        return sd / sound.info.tenMsSampleRate
    }

    fun endDuration(): Int {
        return  endIndex() / sound.info.tenMsSampleRate
    }

    fun removeOver(other: SoundRange): Boolean{
        return startDuration() >= other.startDuration()
    }

    fun isComplict(other: SoundRange): Boolean {
        var st = other.startIndex()
        var lt = other.endIndex()
        var si = startIndex()
        var ei = endIndex()
        return (si <= st && st <= ei)
            || (si <= lt && lt <= ei)
            || (st <= si && si <= lt)
            || (st <= ei && ei <= lt)
    }

    fun complictedRange(other: SoundRange): SoundRange {
        if(isComplict(other)) {
            if(endIndex() > other.endIndex()) {
                var range = other.subRange(startIndex())
                range.seekFront()
                range.expand(endIndex())
                return range
            }
            else {
                var range = subRange(other.startIndex())
                range.seekFront()
                range.expand(other.endIndex())
                return range
            }
        }
        return other
    }

    fun makeFromIndex(index: Int): SoundRange {
        return SoundRange(sound, index / soundLength, index % soundLength)
    }

    fun seekFront() { end = 0; repeat = 0 }


    fun subRange(index: Int): SoundRange {
        var bend = end; var brepeat = repeat
        end = 0; repeat = 0
        expand(index)
        var range = nextRange()
        end = bend; repeat = brepeat
        return nextRange()
    }


    fun overWrite(range: SoundRange): Boolean {
        if(isComplict(range)) {
            remove(range.startDuration())
            return true
        }
        else {
            return false
        }
    }

    fun between(range: SoundRange): SoundRange {
        var btw = nextRange()
        btw.expand(range.startIndex() - endIndex())
        return btw
    }


    fun expand(size: Int): SoundRange {
        end += size
        repeat += (end / soundLength)
        end = (end % soundLength)
        return this
    }

    fun nextRange(): SoundRange {
        var eindex = endIndex()
        if(eindex == 0) return SoundRange(sound)
        return SoundRange( sound , cycle=eindex/soundLength , start=eindex%soundLength)
    }
}


class EditableSound {
    var isMute: Boolean = false
    var effectorIndex: Int
    var blocks: MutableList<SoundRange> = mutableListOf()
    var sound: Sound

    var playedRange: SoundRange
    var playedIndex: Int
    var currentBlockIndex: Int? = null

    constructor(sound: Sound) {
        this.sound = sound
        playedRange = SoundRange(sound, 0)
        playedIndex = 0
        effectorIndex = sound.addEffector {
            var start = playedRange.startIndex()
            playedRange.expand(it.size)
            var end = playedRange.endIndex()
            var curRange = playedRange.makeFromIndex(start).expand(end)
            if(blocks.isNotEmpty()) {
                if(blocks.size > 1) {
                    var btw = blocks[playedIndex].between(blocks[playedIndex+1])
                    if(btw.isComplict(curRange)) {
                        var complict = btw.complictedRange(curRange)
                        Log.d("AudioTest","complict: ${complict.startDuration()} ${complict.endDuration()}")
                        var zeroStart = complict.startIndex() - start
                        var zeroEnd = complict.endIndex() - end
                        for((index, buf) in it.withIndex()) {
                            if(!(zeroStart <= index && index <= zeroEnd)) it[index] = 0
                        }
                        //btw zero
                        if(blocks[playedIndex+1].isComplict(playedRange)) {
                            playedIndex++
                        }
                    }
                }
                else {
                    var complict =blocks[playedIndex].complictedRange(curRange)
                    Log.d("AudioTest","complict: ${complict.startDuration()} ${complict.endDuration()}")
                    var zeroStart = complict.startIndex() - start
                    var zeroEnd = complict.endIndex() - end
                    for((index, buf) in it.withIndex()) {
                        if(!(zeroStart <= index && index <= zeroEnd)) it[index] = 0
                    }
                }
                it
            }
            else {
                ShortArray(it.size, { 0 })
            }
            it
        }
    }

    fun play() {
        if(!isMute) {
            Log.d("AudioTest"," seek duration: ${playedRange.endDuration()}")
            sound.play()
        }
    }

    fun seek(index: Int) {
        playedRange.remove(index)
        var current = playedRange.nextRange()
        playedIndex = 0
        for((index, block) in blocks.withIndex()) {
            if(block.isComplict(current)) playedIndex = index
        }
        Log.d("AudioTest"," seek duration: ${playedRange.endDuration()}")
    }

    fun startBlock() {
        if(!isMute) {
            sound.isMute.set(false)
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
        if(!isMute) {
            sound.isMute.set(true)
            var range = playedRange.endIndex() - blocks[currentBlockIndex!!].startIndex()
            blocks[currentBlockIndex!!].expand(range)
            currentBlockIndex = null
        }
    }

    fun stop() {
        if(!isMute) sound.stop()
    }

    fun makeSound(): Sound {
        var maxLength = playedRange.endIndex()
        seek(0)
        var export = Sound()
        sound.isMute.set(true)
        var save_effector = sound.addEffector {
            playedRange.expand(it.size)
            var diff = playedRange.endIndex() - maxLength
            if(diff > 0)  {
                for((index, data) in it.withIndex()) { if(index > it.size - diff) it[index] = 0 }
                sound.stop()
            }
            sound.data.addAll(it.toMutableList())
            it
        }
        while(sound.isPlaying.get()) { sound.play() }
        sound.removeEffector(save_effector)
        seek(maxLength)
        return export
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

    fun currentPositioin(): Int {
        return sounds[0].playedRange.endDuration()
    }


    fun duration(): Int {
        var duration = 0
        sounds.filter{ it.blocks.isNotEmpty() }
              .map{ it.blocks.last().endDuration() }
              .forEach { if(duration < it) duration = it }
        return duration
    }

    fun stop(index: Int) { sounds[index].stop() }

    fun mixSounds(): MutableList<Short> {
        var musics = sounds.map{ async { it.makeSound() } }
                           .map{ runBlocking {  it.await() } }
        return musics.map { it.data }
                     .fold(MutableList<Short>(musics[0].data.size) {0}) { acc, it ->
                         acc.zip(it){ a, b -> (a + b).toShort() }.toMutableList() }
    }

    fun save(path: String) {
       Sound(mixSounds()).save(path)
    }
}

