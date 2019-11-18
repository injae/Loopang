package com.treasure.loopang.ui

import android.graphics.drawable.Drawable
import com.treasure.loopang.audio.Metronome
import com.treasure.loopang.audio.Sound

object recorderConnector {
    var soundList: List<Sound>? = null
    var labelList: List<String>? = null
    var drawableList: List<Drawable>? = null
    var bpm: Long = 0
}