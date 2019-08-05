package com.treasure.loopang.ui.interfaces

interface ILoopManager {
    fun play(position: Int)
    fun stop()
    fun addLoop(position: Int)
    fun setLoop(position: Int)
    fun clear()
    fun remove(position: Int)
    fun loadLoops()
}