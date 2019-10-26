package com.treasure.loopang.ui.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar

class VerticalSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SeekBar(context, attrs, defStyleAttr) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    @Synchronized
    override fun setProgress(progress: Int)  // it is necessary for calling setProgress on click of a button
    {
        super.setProgress(progress)
        onSizeChanged(width, height, 0, 0)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(c: Canvas) {
        c.rotate(-90f)
        c.translate(-height.toFloat(), 0f)

        super.onDraw(c)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                progress = (max - (max * event.y / height.toFloat()).toInt())
                onSizeChanged(width, height, 0, 0)
            }

            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return true
    }
}
