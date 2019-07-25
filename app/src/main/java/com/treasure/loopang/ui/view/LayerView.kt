package com.treasure.loopang.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.treasure.loopang.R
import com.treasure.loopang.audiov2.Sound
import kotlinx.android.synthetic.main.layerview_layout.view.*
import kotlinx.coroutines.async

class LayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val waveformView: WaveformView
    private val indicatorView: IndicatorView
    private val layerLabelTextView: TextView

    var playState = false
    var sound: Sound? = null
        set(value) {
            value?.onStart { playState = true }
            value?.onStop {
                playState = false
                indicatorView.playbackRate = 0f
            }
            value?.let{
                waveformView.amplitudes = it.data
            }
            field = value
        }
    var layerLabel: String = "Layer #"
        set(value) {
            layer_label.text = value
            field = value
        }
    var muteState = false
        set(value) {
            if(value){
                waveformView.setBackgroundResource(R.color.colorWaveformMute)
            } else {
                waveformView.setBackgroundResource(R.color.colorWaveform)
            }
            field = value
        }
    var effectState = false

    init{
        val layoutInflater = getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater.inflate(R.layout.layerview_layout, this, false)
        addView(v)

        waveformView = v.waveform_view
        indicatorView = v.indicator_view
        layerLabelTextView = v.layer_label
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
    fun setLayerMuteBackgroundColor(muteState: Boolean){
        this.muteState = muteState

    }
}