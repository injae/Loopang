package com.treasure.loopang.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.View

class IndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mIndicatorPaint: Paint = Paint()
    private val mHandler: Handler = Handler()

    private var _width: Int = 0
    private var _height: Int = 0

    var playbackRate : Float = 0f
        set(value) {
            field = when {
                value > 100 -> 100f
                value < 0 -> 0f
                else -> value
            }
            mHandler.post { invalidate() }
        }

    companion object {
        private const val LINE_WIDTH = 2F
    }

    init {
        mIndicatorPaint.style = Paint.Style.STROKE
        mIndicatorPaint.strokeWidth = LINE_WIDTH
        mIndicatorPaint.isAntiAlias = true
        mIndicatorPaint.color = Color.RED
    }

    override fun onDraw(canvas: Canvas?) {
        val x = _width * playbackRate / 100
        val y = _height.toFloat()

        canvas?.drawLine(x, 0f, x, y, mIndicatorPaint)
    }
}