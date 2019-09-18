package com.treasure.loopang.audio

import android.util.Log

class DurationCalculator {
    var sampleRate = 1

    fun calculate(size: Int, type: Int): Int{
        val result = (((size*1000).toFloat()/ sampleRate)).toInt()
        Log.d("seekBarUpdate", "calculator(size: $size) -> result: $result ")
        return result
    }

    companion object{
        const val BYTE = 8
        const val BIT = 1

        fun convertMsToProgress ( msPosition: Int, msDuration: Int, max: Int ): Int = ((msPosition.toFloat() / msDuration) * max).toInt()
        fun convertProgressToMs ( progress: Int, max: Int, msDuration: Int):Int = ((progress.toFloat() / max) * msDuration).toInt()
    }
}