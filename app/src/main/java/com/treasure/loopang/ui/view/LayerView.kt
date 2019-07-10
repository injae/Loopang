package com.treasure.loopang.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.treasure.loopang.R
import com.treasure.loopang.audiov2.Sound
import kotlinx.android.synthetic.main.layerview_layout.view.*
import kotlinx.coroutines.async

class LayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var playState = false

    var sound: Sound? = null
        set(value) {
            value?.onStart { playState = true }
            value?.onStop {
                playState = false
                indicator_view.playbackRate = 0f
            }
            value?.let{
                waveform_view.amplitude = it.data
            }
            field = value
        }
    var layerLabel: String = "Layer #"
        set(value) {
            layer_label.text = value
            field = value
        }
    var muteState = false
    var effectState = false


    init{
        addView(View.inflate(context, R.layout.layerview_layout, this))
    }

    fun play(){
        if(playState) stop()
        async{
            sound?.play()
        }
    }
    fun stop(){
        if(!playState) return
        sound?.stop()
    }
}