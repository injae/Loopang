package com.treasure.loopang.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
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

    private val mTikDrawable: Drawable = resources.getDrawable(R.drawable.metronome_tik)
    private val mDefaultDrawable: Drawable = resources.getDrawable(R.drawable.metronome_default)

    private val mDefaultPaint: Paint = Paint()
    private val mTikPaint: Paint = Paint()

    var onStart: () -> Unit = {}
    var onStop: () -> Unit = {}

    init{
        mDefaultPaint.color = resources.getColor(R.color.metronome_default, null)
        mTikPaint.color = resources.getColor(R.color.metronome_tik, null)
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
        onStop()
    }
    fun tik(){
        mIsTik = true
        invalidate()
    }
    fun clear(){
        mIsTik = false
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
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
    }
}