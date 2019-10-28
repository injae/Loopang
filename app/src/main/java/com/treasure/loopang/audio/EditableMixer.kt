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
        var target = other.startDuration()
        return (startDuration() <= target && target <= endDuration())
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

    fun expand(size: Int) {
        end += size
        repeat += (end / soundLength)
        end = (end % soundLength)
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
            playedRange.expand(it.size)
            if(blocks.isNotEmpty()) {
                var compare =  playedRange.endIndex() - blocks[playedIndex].startIndex()
                if(compare > 0) {
                    var zeroRange = it.size - compare
                    for((index, _) in it.withIndex()) { if(index < zeroRange) it[index] = 0 }
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
            else {
                ShortArray(it.size, { 0 })
            }
            it
        }
    }

    fun play() {
        if(!isMute) {
            sound.play()
        }
    }

    fun seek(index: Int) {
        playedRange.remove(index)
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

