package com.treasure.loopang.ui.util

import com.treasure.loopang.ui.interfaces.IAudioAnalyzer

class AudioAnalyzer(private var bitRate: Int = Short.MAX_VALUE.toInt() / 2
                    , private var maxAmplitude: Int = 0) : IAudioAnalyzer {

    override fun setBitRate(bitRate: Int) {
        this.bitRate = bitRate
    }

    override fun setMaxAmplitude(amplitude: Int) {
        this.maxAmplitude = amplitude
    }

    override fun analyze(amplitude: Int): Int {
        if(maxAmplitude == 0 || bitRate == 0) return 0
        val divisor: Int = bitRate / maxAmplitude
        return amplitude / divisor
    }
}