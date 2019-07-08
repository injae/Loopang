package com.treasure.loopang.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.treasure.loopang.audiov2.Sound

class LayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    lateinit var sound: Sound
    lateinit var layerLabel: String
    var muteState = false
    var effectStart = false

    fun play() = Unit
    fun stop() = Unit
}