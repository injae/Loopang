package com.treasure.loopang.audio

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class BasicPlayer(var sound: Sound) : IPlayable {
    var isLooping = AtomicBoolean(true)

    override fun start() {
        isLooping.set(true)
        launch { while (isLooping.get()) { async { sound.play() }.await() } }.start()
    }

    override fun stop() {
        isLooping.set(false)
        sound.stop()
    }

}