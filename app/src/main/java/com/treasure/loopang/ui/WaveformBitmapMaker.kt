package com.treasure.loopang.ui

import android.graphics.*
import android.util.Log
import com.treasure.loopang.ui.util.AudioAnalyzer
import com.treasure.loopang.ui.view.BlockLayerView
import kotlin.math.abs

class WaveformBitmapMaker {
    var width = 0
    var height = 0

    var amplitudes : List<Short>? = null

    var soundType: SoundType = SoundType.PCM16
    var drawType: DrawType = DrawType.LINE

    var lineWidth = 1
    var blockWidth = 3
    private var size = 0

    var maxSize = 0

    var backgroundColor: Int = 0
    var color: Int = 0

    var cornerRadius = 0
    var roundedCorners = 0
    private val path: Path = Path()


    private val mLinePaint: Paint = Paint()
    private val audioAnalyzer: AudioAnalyzer = AudioAnalyzer()

    companion object {
        const val CORNER_NONE: Int = 0
        const val CORNER_TOP_LEFT: Int = 1
        const val CORNER_TOP_RIGHT: Int = 2
        const val CORNER_BOTTOM_RIGHT: Int = 4
        const val CORNER_BOTTOM_LEFT: Int = 8
        const val CORNER_ALL: Int= 15

        val CORNERS: List<Int> = listOf(
            CORNER_TOP_LEFT,
            CORNER_TOP_RIGHT,
            CORNER_BOTTOM_RIGHT,
            CORNER_BOTTOM_LEFT)
    }

    fun make(): Bitmap {
        Log.d("WaveformBuilder", "width: $width, height: $height")
        val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap)
        var curX = 0f // start curX at middle

        audioAnalyzer.setMaxAmplitude(height)
        size = width / lineWidth
        expandList(maxSize)
        convertList()
        setPath()
        canvas.clipPath(path)

        mLinePaint.color = color
        canvas.drawColor(backgroundColor)

        // for each item in the _amplitudes ArrayList
        amplitudes?.forEach{ power ->
            val scaledHeight = audioAnalyzer.analyze(power.toInt()) // scale the power
            curX += lineWidth.toFloat() // increase X by LINE_WIDTH

            // draw a line representing this item in the _amplitudes ArrayList
            canvas.drawLine(
                curX, height.toFloat(), curX, height.toFloat() - scaledHeight.toFloat(), mLinePaint
            )
        }

        return bitmap
    }

    private  fun expandList(max: Int) {
        if (amplitudes!!.size >= max) return

        val newArray = (0 until max).map {
            if(it < amplitudes!!.size)
                amplitudes!![it]
            else 0
        }

        amplitudes = newArray
    }

    private fun convertList(){
        if(size == 0 && amplitudes!!.size == size) {
            return
        }
        amplitudes = if(amplitudes!!.size > size){
            // Sound.data size 가 너비보다 클 경우
            val n = amplitudes!!.size / size
            amplitudes!!.chunked(n).map{
                (it.fold(0) { acc, next-> acc + abs(next.toInt()) } / it.count())
                    .toShort()
            }.subList(0, size)
        } else {
            val n = size / amplitudes!!.size    //코틀린에서 정수형 나누기 시 자동내림.
            (0 until size).mapIndexed{ index, _ ->
                if(amplitudes!!.size * n > index)
                    amplitudes!!.flatMap{ power -> (0 until n).map{ abs(power.toInt()).toShort()}}[index]
                else amplitudes!!.last()
            }
        }
    }

    private fun isCornerRounded(corner: Int): Boolean {
        return (roundedCorners and corner) == corner
    }

    private fun setPath() {
        path.rewind()
        if ((cornerRadius >= 1f) && (roundedCorners != BlockLayerView.CORNER_NONE)) {
            val radii = FloatArray(8)

            for (i in 0..3) {
                if (isCornerRounded(BlockLayerView.CORNERS[i])) {
                    radii[2 * i] = cornerRadius.toFloat()
                    radii[2 * i + 1] = cornerRadius.toFloat()
                }
            }

            path.addRoundRect(
                RectF(0f, 0f, width.toFloat(), height.toFloat()),
                radii, Path.Direction.CW
            )
        }
    }


    enum class SoundType { PCM16, PCM32 }
}

    enum class DrawType { LINE, BLOCK }
