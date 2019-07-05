package com.treasure.loopang.audiov2

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class BasicPlayer(var sound: Sound) : IPlayable {
    var isLooping = AtomicBoolean(true)

    override fun start() {
        isLooping.set(true)
        launch { while (isLooping.get()) { val routine = async { sound.play() }.await() } }.start()
    }

    override fun stop() {
        isLooping.set(false)
        sound.stop()
    }

}