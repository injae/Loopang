package com.treasure.loopang.audiov2

import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class Mixer(val sounds: MutableList<Sound> = mutableListOf()) : IPlayable {
    var isLooping = AtomicBoolean(true)

    fun addSound(sound: Sound) {
        //debug: sound.onSuccess { Log.d("MixerTest", "${sounds.count() + 1} Sound Success").toUInt() }
        //debug: sound.onStart   { Log.d("MixerTest", "${sounds.count() + 1} Sound Start").toUInt() }
        //debug: sound.onStop    { Log.d("MixerTest", "${sounds.count() + 1} Sound Stop").toUInt() }
        sounds.add(sound)
    }

    override fun start() {
        isLooping.set(true)
        launch {
            while(isLooping.get()) {
                val routine = async{ sounds[0].play() }
                sounds.filterIndexed{it, t -> it > 0}.forEach { async { it.play() } }
                routine.await()
            }
        }.start()
    }

    override fun stop() {
        isLooping.set(false)
        sounds.forEach { it.stop() }
    }

    fun stop(index: Int) { sounds[index].stop() }

    fun mixSounds() = sounds.map { it.data }.fold(MutableList<Short>(sounds[0].data.size, {0})) {
            acc, it -> acc.zip(it){ a, b -> (a + b).toShort() }.toMutableList() }
}