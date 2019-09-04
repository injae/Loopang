package com.treasure.loopang.ui.util

import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import com.treasure.loopang.audio.DurationCalculator
import com.treasure.loopang.ui.stringForTime

class ProgressControl {
    private var mSeekBar: SeekBar? = null
    private var mCurrentPosTimeText: TextView? = null
    private var mWholePosTimeText: TextView? = null

    var max: Int = 0
        set(max) {
            field = max
            mSeekBar?.max = field
        }
    var progress: Int = 0
    var duration: Int = 0
        set(duration) {
            field = duration
            mWholePosTimeText?.text = stringForTime(duration)
        }
    var position: Int = 0

    fun setView(seekBar: SeekBar? = null,
                currentPosTimeText: TextView? = null,
                wholePosTimeText: TextView? = null){
        mSeekBar = seekBar
        mCurrentPosTimeText = currentPosTimeText
        mWholePosTimeText = wholePosTimeText
    }

    fun updateTask() {
        mSeekBar?.progress = progress
        mCurrentPosTimeText?.text = stringForTime(position)
        Log.d("progressControl"," update ms: $position , duration: $duration, progress: $progress, seekBar is null: ${mSeekBar == null}")
    }

    fun setProgressUsingMs(ms: Int) {
        position = ms
        progress = DurationCalculator.convertMsToProgress(position, duration, this.max)
    }
}
