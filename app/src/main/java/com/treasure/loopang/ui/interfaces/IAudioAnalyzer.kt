package com.treasure.loopang.ui.interfaces

interface IAudioAnalyzer {
    fun setBitRate(bitRate: Int)
    fun setMaxAmplitude(amplitude: Int)
    fun analyze(amplitude: Int): Int
}