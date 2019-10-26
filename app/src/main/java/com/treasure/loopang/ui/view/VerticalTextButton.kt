package com.treasure.loopang.ui.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.widget.Button

class VerticalTextButton(context: Context?, attrs: AttributeSet?) : Button(context, attrs) {
    private var topDown = false

    init {
        val tempGravity = gravity
        if(Gravity.isVertical(tempGravity) && (tempGravity and Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            gravity = ((tempGravity and Gravity.HORIZONTAL_GRAVITY_MASK) or Gravity.TOP)
            topDown = true
        } else {
            topDown = false
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        val textPaint = paint
        textPaint.color = currentTextColor
        textPaint.drawableState = drawableState

        canvas.save()

        if (topDown) {
            canvas.translate(width.toFloat(), 0f)
            canvas.rotate(90f)
        } else {
            canvas.translate(0f, height.toFloat())
            canvas.rotate(-90f)
        }

        canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())
        layout.draw(canvas)
        canvas.restore()
    }
}