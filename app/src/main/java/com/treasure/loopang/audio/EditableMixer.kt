package com.treasure.loopang.audio

import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean


class EditableSound: SoundFlow<EditableSound> {
    var blocks:SoundBlocks
    var isMute: AtomicBoolean = AtomicBoolean(false)
    var isEnd: AtomicBoolean = AtomicBoolean(false)
    var effectorIndex: Int
    var sound: Sound


    constructor(sound: Sound) {
        this.sound = sound
        blocks = SoundBlocks(sound)
        effectorIndex = sound.addEffector {
            var start = blocks.point()
            if(!(!blocks.isRecording.get() && isEnd.get())) blocks += it.size
            var end = blocks.point()
            var curRange = blocks.location.makeFromIndex(start).expand(end-start)
            var retZero = false
            var remain = true
            if(!blocks.isRecording.get()) {
                while(remain) {
                    remain = false
                    if (!blocks.isEmpty()) {
                        Log.d( "AudioTest", "B(${blocks})::blocks[${blocks.location}] playing: [${curRange.startIndex()}:${curRange.endIndex()}]" )
                        if (blocks.playedIndex + 1 <= blocks.size() -1) {
                            isEnd.set(false)
                            var btw = blocks.current().between(blocks.next())
                            Log.d( "AudioTest", "B(${blocks}):: between: [${btw.startIndex()}:${btw.endIndex()}]" )
                            if(btw.isComplict(curRange)) {
                                var complict = btw.complictedRange(curRange)
                                Log.d( "AudioTest", "B(${blocks}):: complict: [${complict.startIndex()}:${complict.endIndex()}]" )
                                var zeroStart = complict.startIndex() - start
                                var zeroEnd   = complict.endIndex()   - start
                                for ((index, _) in it.withIndex()) { if (zeroStart <= index && index < zeroEnd) it[index] = 0 }
                                curRange.expand(1)
                                if (blocks.next().isComplict(curRange)) { blocks++ }
                            }
                            else {
                                if (blocks.next().isComplict(curRange)) { blocks++; remain=true }
                                else { Log.d( "AudioTest", "B(${blocks}):: playing: [${curRange.startIndex()}:${curRange.endIndex()}]" ) }
                            }
                        } else {
                            if(curRange.isComplict(blocks.current())) {
                                isEnd.set(false)
                                var complict = curRange.complictedRange(blocks.current())
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
        if(!isMute.get() && !isEnd.get()) {
            Log.d("AudioTest"," play index: ${blocks.point()}")
            sound.play()
        }
    }

    fun record(){
        if(!isMute.get()) blocks.record()
    }

    fun recordStop(limit:Int?)=blocks.stop(limit)
    fun seek(index:Int)=blocks.seek(index)


    fun stop() {
        if(!isMute.get()) sound.stop()
    }

    fun makeSound(): Sound {
        var maxLength = blocks.duration()
        blocks.seek(0)
        var export = Sound()
        sound.isMute.set(true)
        var save_effector = sound.addEffector {
            blocks.location.expand(it.size)
            var diff = blocks.point() - maxLength
            if(diff > 0)  {
                for((index, _) in it.withIndex()) { if(index > it.size - diff) it[index] = 0 }
                sound.stop()
            }
            sound.data.addAll(it.toMutableList())
            it
        }
        while(sound.isPlaying.get()) { sound.play() }
        sound.removeEffector(save_effector)
        blocks.seek(maxLength)
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
            Log.d("AudioTest"," call Success: ${sound.blocks}")
        }
        sound.onStart{
        }
        sounds.add(sound)
    }

    fun setMute(index: Int, isMute: Boolean) { sounds[index].isMute.set(isMute)}

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
                sounds.mapIndexed{index, _ -> index }
                    .filter{ !sounds[it].blocks.isEmpty() }
                    .forEach { index ->
                        if( sounds[longIndex].blocks.duration()
                          < sounds[index].blocks.duration()) {
                            longIndex = index
                        }
                    }
                Log.d("AudioTest","longest index")
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
        sounds.forEach{ it.record() }

    }

    fun endBlock(limit:Int? = null) {
        makeBlocks.set(false)
        sounds.forEach{ it.recordStop(limit) }
    }

    fun seek(tenMs: Int) {
        sounds.forEach{
            it.seek(tenMs * sounds[0].sound.info.tenMsSampleRate)
        }
    }

    fun duration(): Int {
        var duration = 0
        sounds.filter{ !it.blocks.isEmpty() }
              .map{ it.blocks.durationMs() }
              .forEach { if(duration < it) duration = it }
        return duration
    }

    fun loopDuration()=sounds.last().blocks.durationMs()

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

