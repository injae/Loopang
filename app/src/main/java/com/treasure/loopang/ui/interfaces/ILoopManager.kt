package com.treasure.loopang.ui.interfaces

import com.treasure.loopang.audiov2.Sound

interface ILoopManager {
    fun play(position: Int)
    fun stop()
    fun addLoop(sound: Sound)
    fun setLoop(sound: Sound)
    fun clear()
    fun remove(position: Int)
    fun loadLoops()
}