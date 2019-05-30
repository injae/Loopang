package com.treasure.loopang.audio

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean


class Mixer(val sounds: MutableList<Player> = mutableListOf()) {
    var isPlaying = AtomicBoolean(true)

    fun addSound(sound: Sound) {
        sounds.add(Player(sound))
    }

    fun start() {
        launch {
            while(isPlaying.get()) {
                async{ sounds[0].n_start() }.await()
                sounds.filterIndexed{it, t -> it > 0}.forEach {
                    async { it.n_start() }
                }
            }
        }.start()
    }

    fun stop() {
        isPlaying.set(false)
        sounds.forEach { it.stop() } }
    fun stop(index: Int) { sounds[index].stop() }
}