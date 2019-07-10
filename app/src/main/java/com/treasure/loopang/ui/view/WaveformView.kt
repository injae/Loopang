package com.treasure.loopang.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var _amplitudes: MutableList<Short>? = null   // _amplitudes for line lengths
    var amplitudes: MutableList<Short>? = null
        set(value) {
            value?.let{
                if(_width != 0 && _height != 0){
                    _amplitudes = convertList(it)
                    mHandler.post { invalidate() }
                }
                field = value
            }
        }

    private var _width: Int = 0 // _width of this View
    private var _height: Int = 0 // _height of this View
    private var _size: Int = 0 // _amplitudes size

    private val mLinePaint: Paint = Paint() // specifies line drawing characteristics
    private val mHandler = Handler()

    init {
        // create Paint for lines
        mLinePaint.color = Color.WHITE // set color to green
        mLinePaint.strokeWidth = LINE_WIDTH.toFloat() // set stroke _width
    }

    // called when the dimensions of the View change
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w == 0 || h == 0) return

        _width = w // new _width of this View
        _height = h // new _height of this View
        _size = _width / LINE_WIDTH

        amplitudes?.let{
            _amplitudes = convertList(it)
        }
        mHandler.post { invalidate() }
    }

    // clear all _amplitudes to prepare for a new visualization
    fun clear() {
        _amplitudes?.clear()
    }

    // draw the visualizer with scaled lines representing the _amplitudes
    public override fun onDraw(canvas: Canvas) {
        var curX = 0f // start curX at middle

        // for each item in the _amplitudes ArrayList
        _amplitudes?.forEach{ power ->
            val scaledHeight = power / LINE_SCALE // scale the power
            curX += LINE_WIDTH.toFloat() // increase X by LINE_WIDTH

            // draw a line representing this item in the _amplitudes ArrayList
            canvas.drawLine(
                curX, _height.toFloat(), curX, _height.toFloat() - scaledHeight.toFloat(), mLinePaint
            )
        }
    }

    private fun convertList(data: MutableList<Short>): MutableList<Short> {
        if(data.size == _size) {
            return data
        }
        return if(data.size > _size){
            // Sound.data size 가 너비보다 클 경우
            val n = data.size / _size
            data.chunked(n).map{
                (it.fold(0) { acc, next-> acc + next} / it.count())
                    .toShort()
            }.subList(0, _size)
                .toMutableList()
        } else {
            val n = _size / data.size    //코틀린에서 정수형 나누기 시 자동내림.
            (0 until _size).mapIndexed{ index, _ ->
                if(data.size * n > index)
                    data.flatMap{ power -> (0 until n).map{power}}[index]
                else data.last()
            }.toMutableList()
        }
    }

    companion object {
        private const val LINE_WIDTH = 3 // _width of visualizer lines
        private const val LINE_SCALE = 10 // scales visualizer lines
    }
}