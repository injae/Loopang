package com.treasure.loopang.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.treasure.loopang.R

class MetronomeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var playState: Boolean = false

    private var mIsTik: Boolean = false

    private val mDefaultPaint: Paint = Paint()
    private val mTikPaint: Paint = Paint()

    private val mDefaultDrawable: Drawable = resources.getDrawable(R.drawable.metronome_default)
    private val mTikDrawable: Drawable = resources.getDrawable(R.drawable.metronome_tik)
    private val drawables = arrayOf(mTikDrawable, mDefaultDrawable)
    private val transition = TransitionDrawable(drawables)

    var bpm: Int = 0

    var onStart: () -> Unit = {}
    var onStop: () -> Unit = {}

    init{
        mDefaultPaint.color = resources.getColor(R.color.metronome_default, null)
        mTikPaint.color = resources.getColor(R.color.metronome_tik, null)
        background = mDefaultDrawable
        this.isClickable = true
        this.setOnClickListener{
            if(!playState){
                start()
            } else {
                stop()
            }
        }
    }

    private fun start(){
        playState = true
        onStart()
    }
    private fun stop(){
        playState = false
        clear()
        onStop()
    }
    fun tik(){
        /*mIsTik = true
        invalidate()
        Handler().postDelayed({invalidate()}, ((30/bpm)*1000).toLong())*/
        background = transition
        transition.startTransition(60000/bpm)
    }
    fun clear(){
        /*mIsTik = false
        invalidate()*/
        background = mDefaultDrawable
    }

   /* override fun onDraw(canvas: Canvas?) {
        canvas?.let{} ?: return
        val p: Paint

        if(mIsTik){
            mIsTik = !mIsTik
            p = mTikPaint
        } else {
            p = mDefaultPaint
        }

        canvas.drawOval(0f,0f,width.toFloat(),height.toFloat(), p)

        Log.d("MetronomeView", "\nonDraw()!\nleft,top,right,bottom: $left, $top, $right, $bottom\nx, y: $x, $y")
    }*/
}