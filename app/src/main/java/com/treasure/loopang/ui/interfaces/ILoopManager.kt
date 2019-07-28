package com.treasure.loopang.ui.interfaces

interface ILoopManager {
    fun play(position: Int)
    fun stop()
    fun addLoop()
    fun clear()
    fun remove(position: Int)
    fun loadLoops()
}