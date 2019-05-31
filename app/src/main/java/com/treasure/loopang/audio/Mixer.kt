package com.treasure.loopang.audio

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class Mixer(val sounds: MutableList<Player> = mutableListOf()) {
    var isPlaying = AtomicBoolean(true)

    fun addSound(sound: Sound) {
        sounds.add(Player(sound))
    }

    fun start() {
        isPlaying.set(true)
        launch {
            while(isPlaying.get()) {
                val routine = async{ sounds[0].n_start() }
                sounds.filterIndexed{it, t -> it > 0}.forEach {
                    async { it.n_start() }
                }
                routine.await()
            }
        }.start()
    }

    fun stop() {
        isPlaying.set(false)
        sounds.forEach { it.stop() }
    }
    fun stop(index: Int) { sounds[index].stop() }

    fun mixSounds() = sounds.map { it.sound.data }.fold(MutableList<Short>(sounds[0].sound.data.size, {0})) {
            acc, it -> acc.zip(it){ a, b -> (a + b).toShort() }.toMutableList() }
}