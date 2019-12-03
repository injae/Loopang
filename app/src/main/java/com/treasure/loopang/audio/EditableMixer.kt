package com.treasure.loopang.audio

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean


interface IEditableMixerItem {
    fun seek(tenMs: Int)
    fun record()
    fun play()
    fun stop()
    fun export() : MutableList<Short>
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


    fun start(wait:Boolean=false ) {
        isLooping.set(true)
        callStart(this)
        sounds.forEach{it.isEnd.set(false)}
        launch {
            sounds.map{
                var thread = launch{ replay(it) }
                thread.start()
                thread
            }.forEach{
                it.join()
            }
            callSuccess(this@EditableMixer)
        }.start()
        if(!makeBlocks.get()) {
             var job= launch {
                var longIndex = 0
                sounds.mapIndexed{index, _ -> index }
                    .filter{ !sounds[it].blocks.isEmpty() }
                    .forEach { index ->
                        if( sounds[longIndex].blocks.duration()
                          < sounds[index].blocks.duration()) {
                            longIndex = index
                        }
                    }
                while (isLooping.get()) {
                    if(sounds[longIndex].isEnd.get()) {
                        isLooping.set(false)
                        Log.d("AudioTest","play Stoped")
                        callStop(this@EditableMixer)
                    }
                }
            }
            job.start()
            if(wait) runBlocking { job.join() }
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
        var buffer = sounds.map{ Pair(it, it.addGenerator()) }
        start(true)
        var mixed = buffer.map{
            while(!it.first.isEnd.get()) {}
            //it.first.removeGenerator()
            it.second  }.map { it.data }
                         .fold(MutableList<Short>(sounds[sounds.lastIndex].sound.data.size) {0}) {
                                    acc, it -> acc.zip(it){ a, b -> (a + b).toShort() }.toMutableList() }
        buffer.forEach{ it.first.removeGenerator() }
        return mixed
    }

    fun save(path: String) {
       Sound(mixSounds()).save(path)
    }
}

