package com.treasure.loopang.audiov2

import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class Mixer(val sounds: MutableList<Sound> = mutableListOf()) : SoundFlow<Mixer>(), IPlayable {
    var isLooping = AtomicBoolean(false)

    fun addSound(sound: Sound) {
        sound.onSuccess { Log.d("SoundTest", "${sounds.count() + 1} Sound Success") } //debug
        sound.onStart   { Log.d("SoundTest", "${sounds.count() + 1} Sound Start") }   //debug
        sound.onStop    { Log.d("SoundTest", "${sounds.count() + 1} Sound Stop") }    //debug
        sounds.add(sound)
    }

    fun setMute(index: Int, isMute: Boolean) { sounds[index].onStart {  it.isPlaying.set(isMute) }}

    override fun start() {
        isLooping.set(true)
        callStart(this)
        launch { while(isLooping.get()) { sounds.map { async { it.play() } }.forEach{ it.await() }; callSuccess(this@Mixer) } }.start()
    }

    override fun stop() {
        isLooping.set(false)
        callStop(this)
        sounds.forEach { it.stop() }
    }

    fun stop(index: Int) { sounds[index].stop() }

    fun mixSounds() =
        sounds.map { it.data }
              .fold(MutableList<Short>(sounds[0].data.size, {0})) {
                    acc, it -> acc.zip(it){ a, b -> (a + b).toShort() }.toMutableList() }

    fun mixSounds(filteredIndex: Int) =
        sounds.map { it.data }
              .filterIndexed{ index, _ -> index != filteredIndex }
              .fold(MutableList<Short>(sounds[0].data.size, {0})) {
                    acc, it -> acc.zip(it){ a, b -> (a + b).toShort() }.toMutableList() }
}