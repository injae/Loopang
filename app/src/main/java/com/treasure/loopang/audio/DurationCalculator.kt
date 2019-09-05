package com.treasure.loopang.audio

class DurationCalculator {
    var sampleRate = 1

    fun calculate(size: Int, type: Int): Int = ((size.toFloat() / sampleRate)*1000).toInt()

    companion object{
        const val BYTE = 8
        const val BIT = 1

        fun convertMsToProgress ( msPosition: Int, msDuration: Int, max: Int ): Int = ((msPosition.toFloat() / msDuration) * max).toInt()
        fun convertProgressToMs ( progress: Int, max: Int, msDuration: Int):Int = ((progress.toFloat() / max) * msDuration).toInt()
    }
}