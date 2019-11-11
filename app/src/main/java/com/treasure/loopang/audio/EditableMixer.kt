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
        var buf = subRange(index-startIndex())
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
        return ((cycle*soundLength) + start) + ((repeat*soundLength) + end)
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
        return startIndex() > other.endIndex()
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
            var endFirst  = if(endIndex() < other.endIndex()) this else other
            var startLast = if(startIndex() <= other.startIndex()) other else this

            return startLast.subRange(endFirst.endIndex()-startLast.startIndex())
        }
        return other
    }

    fun makeFromIndex(index: Int): SoundRange {
        return SoundRange(sound, index / soundLength, index % soundLength)
    }

    fun seekFront() { end = 0; repeat = 0 }


    fun subRange(index: Int): SoundRange {
        var bend = end; var brepeat = repeat
        seekFront()
        expand(index)
        var range = SoundRange(sound,cycle,start,end,repeat)
        end = bend; repeat = brepeat
        return range
    }


    fun overWrite(range: SoundRange): Boolean {
        if(isComplict(range)) {
            remove(range.startIndex())
            return true
        }
        else {
            return false
        }
    }

    fun between(range: SoundRange): SoundRange {
        var btw = nextRange()
        btw.expand(range.startIndex() - btw.endIndex())
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
    var isRecording: AtomicBoolean = AtomicBoolean(false)
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
            var start = playedRange.endIndex()
            playedRange.expand(it.size)
            var end = playedRange.endIndex()
            var curRange = playedRange.makeFromIndex(start).expand(end-start)
            var retZero = false
            if(!isRecording.get()) {
                if (blocks.isNotEmpty()) {
                    if (playedIndex + 1 <= blocks.size-1) {
                        var btw = blocks[playedIndex].between(blocks[playedIndex + 1])
                        if(btw.isComplict(curRange)) {
                            var complict = btw.complictedRange(curRange)
                            var zeroStart = complict.startIndex() - start
                            var zeroEnd = complict.endIndex() - start
                            for ((index, _) in it.withIndex()) { if (zeroStart <= index && index < zeroEnd) it[index] = 0 }
                            curRange.expand(1)
                            if (blocks[playedIndex + 1].isComplict(curRange)) { playedIndex += 1 }
                        }
                        else {
                            Log.d( "AudioTest", "B(${blocks}):: playing: [${curRange.startIndex()}:${curRange.endIndex()}]" )
                        }
                    } else {
                        if(curRange.isComplict(blocks[playedIndex])) {
                            var complict = curRange.complictedRange(blocks[playedIndex])
                            var nonZeroStart = complict.startIndex() - start
                            var nonZeroEnd = complict.endIndex() - start
                            if(nonZeroEnd != it.size) for ((index, _) in it.withIndex()) { if (!(nonZeroStart <= index && index < nonZeroEnd)) it[index] = 0 }
                        }
                        else { retZero = true; Log.d( "AudioTest", "B(${blocks[playedIndex]}):: stop" ) }
                    }
                } else { retZero=true }
            }
            else { Log.d( "AudioTest", "B(${blocks}):: record: ${curRange.startIndex()} ${curRange.endIndex()}" ) }
            if(retZero) ShortArray(it.size, { 0 })
            else it
        }
    }

    fun play() {
        if(!isMute) {
            Log.d("AudioTest"," play duration: ${playedRange.endDuration()}")
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
        Log.d("AudioTest","mixer seek index: ${playedRange.endIndex()}")
    }

    fun startBlock() {
        if(!isMute) {
            isRecording.set(true)
            var currentBlock = playedRange.nextRange()
            currentBlock.expand(1)
            blocks = blocks.filter{ !it.removeOver(currentBlock) }.toMutableList()
            blocks.forEach{ Log.d("AudioTest", "- none over: ${it.startIndex()} ${it.endIndex()}") }
            for((index, block) in blocks.withIndex()) {
                if(blocks[index].overWrite(currentBlock)) {
                    Log.d("AudioTest", "or block: ${blocks[index].startIndex()}:${blocks[index].endIndex()}")
                    Log.d("AudioTest", "cu block: ${currentBlock.startIndex()} ${currentBlock.endIndex()}")
                    currentBlockIndex = index
                }
            }
            if(currentBlockIndex == null) {
                blocks.add(currentBlock)
                currentBlockIndex = blocks.lastIndex
            }
        }
    }

    fun endBlock(limit:Int? = null) {
        if(!isMute) {
            isRecording.set(false)
            if(limit != null) { playedRange = playedRange.subRange(limit) }
            blocks[currentBlockIndex!!] = blocks[currentBlockIndex!!]
                .subRange(playedRange.endIndex()-blocks[currentBlockIndex!!].startIndex())
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
        //launch { while(isLooping.get()) { sounds.forEach{ if(!it.isRecording.get()) { isLooping.set(false)}}}}.start()
    }

    fun stop() {
        isLooping.set(false)
        callStop(this)
        sounds.forEach { it.stop() }
    }

    fun startBlock() {
        sounds.forEach{ it.startBlock() }

    }

    fun endBlock(limit:Int? = null) {
        sounds.forEach{ it.endBlock(limit) }
    }

    fun seek(tenMs: Int) {
        sounds.forEach{
            it.seek(tenMs * sounds[0].sound.info.tenMsSampleRate)
        }
    }

    fun duration(): Int {
        var duration = 0
        sounds.filter{ it.blocks.isNotEmpty() }
              .map{ it.blocks.last().endDuration() }
              .forEach { if(duration < it) duration = it }
        return duration
    }

    fun loopDuration(): Int{
        return sounds[0].playedRange.endDuration()
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

