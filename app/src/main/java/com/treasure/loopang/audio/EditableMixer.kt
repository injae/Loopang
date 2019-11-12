package com.treasure.loopang.audio

import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


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

    fun isOver(other: SoundRange): Boolean{
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


class EditableSound: SoundFlow<EditableSound> {
    var isMute: Boolean = false
    var isEnd: AtomicBoolean = AtomicBoolean(false)
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
            if(!(!isRecording.get() && isEnd.get())) playedRange.expand(it.size)
            var end = playedRange.endIndex()
            var curRange = playedRange.makeFromIndex(start).expand(end-start)
            var retZero = false
            var remain = true
            if(!isRecording.get()) {
                while(remain) {
                    remain = false
                    if (blocks.isNotEmpty()) {
                        Log.d( "AudioTest", "B(${blocks})::blocks[${playedIndex}] playing: [${curRange.startIndex()}:${curRange.endIndex()}]" )
                        if (playedIndex + 1 <= blocks.size-1) {
                            isEnd.set(false)
                            var btw = blocks[playedIndex].between(blocks[playedIndex + 1])
                            Log.d( "AudioTest", "B(${blocks}):: between: [${btw.startIndex()}:${btw.endIndex()}]" )
                            if(btw.isComplict(curRange)) {
                                var complict = btw.complictedRange(curRange)
                                Log.d( "AudioTest", "B(${blocks}):: complict: [${complict.startIndex()}:${complict.endIndex()}]" )
                                var zeroStart = complict.startIndex() - start
                                var zeroEnd   = complict.endIndex()   - start
                                for ((index, _) in it.withIndex()) { if (zeroStart <= index && index < zeroEnd) it[index] = 0 }
                                curRange.expand(1)
                                if (blocks[playedIndex + 1].isComplict(curRange)) { playedIndex += 1 }
                            }
                            else {
                                if (blocks[playedIndex + 1].isComplict(curRange)) { playedIndex += 1; remain=true }
                                else { Log.d( "AudioTest", "B(${blocks}):: playing: [${curRange.startIndex()}:${curRange.endIndex()}]" ) }
                            }
                        } else {
                            if(curRange.isComplict(blocks[playedIndex])) {
                                isEnd.set(false)
                                var complict = curRange.complictedRange(blocks[playedIndex])
                                Log.d( "AudioTest", "B(${blocks}):: playable: [${complict.startIndex()}:${complict.endIndex()}]" )
                                var nonZeroStart = complict.startIndex() - start
                                var nonZeroEnd   = complict.endIndex()   - start
                                Log.d( "AudioTest", "B(${blocks}):: none zero: [${nonZeroStart}:${nonZeroEnd}]" )
                                if(nonZeroEnd != it.size) for ((index, _) in it.withIndex()) { if (!(nonZeroStart <= index && index < nonZeroEnd)) it[index] = 0 }
                            }
                            else {
                                retZero = true
                                isEnd.set(true)
                                callSuccess(this@EditableSound)
                                Log.d( "AudioTest", "B(${blocks}):: stop" )
                            }
                        }
                    }
                    else { retZero=true }
                }
            }
            else { Log.d( "AudioTest", "B(${blocks}):: record: ${curRange.startIndex()} ${curRange.endIndex()}" ) }
            if(retZero) ShortArray(it.size, { 0 })
            else it
        }
    }

    fun play() {
        if(!isMute && !isEnd.get()) {
            Log.d("AudioTest"," play index: ${playedRange.endIndex()}")
            sound.play()
        }
    }

    fun seek(index: Int) {
        playedRange.remove(index)
        var current = playedRange.nextRange()
        playedIndex = 0
        for((index, block) in blocks.withIndex()) {
                 if(block.isComplict(current)) playedIndex = index
            else if(!block.isOver(current))    playedIndex = index
        }
        Log.d("AudioTest","mixer seek index: ${playedRange.endIndex()}")
    }

    fun startBlock() {
        if(!isMute) {
            isRecording.set(true)
            var currentBlock = playedRange.nextRange()
            blocks = blocks.filter{ !it.isOver(currentBlock) }.toMutableList()
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
    var makeBlocks = AtomicBoolean(false)

    fun addSound(sound: Sound) { addSound(EditableSound(sound)) }

    fun addSound(sound: EditableSound) {
        sound.onSuccess {
            sound.stop()
        }
        sound.onStart{
        }
        sounds.add(sound)
    }

    fun setMute(index: Int, isMute: Boolean) { sounds[index].isMute = isMute}

    suspend fun replay(sound: EditableSound) {
        if(isLooping.get())  {
            if(!sound.isEnd.get()) {
                async {
                    sound.play()
                }.await()
                replay(sound)
            }
        }
    }


    fun start() {
        isLooping.set(true)
        callStart(this)
        sounds.forEach{it.isEnd.set(false)}
        launch {
            sounds.forEach{ launch{ replay(it) }.start() }
            callSuccess(this@EditableMixer)
        }.start()
        if(!makeBlocks.get()) {
            launch {
                var longIndex = 0
                sounds.mapIndexed{index, it -> index }
                    .filter{ sounds[it].blocks.isNotEmpty() }
                    .forEach { index ->
                        if( sounds[longIndex].blocks.last().endIndex()
                          < sounds[index].blocks.last().endIndex()) {
                            longIndex = index
                        }
                    }
                Log.d("AudioTest","logest index")
                while (isLooping.get()) {
                    if(sounds[longIndex].isEnd.get()) {
                        isLooping.set(false)
                        Log.d("AudioTest","play Stoped")
                    }
                }
            }.start()
        }
    }

    fun stop() {
        isLooping.set(false)
        sounds.forEach{ it.stop() }
        callStop(this)
    }

    fun startBlock() {
        makeBlocks.set(true)
        sounds.forEach{ it.startBlock() }

    }

    fun endBlock(limit:Int? = null) {
        makeBlocks.set(false)
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
        return sounds.last().playedRange.endDuration()
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

