package com.treasure.loopang.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.treasure.loopang.R


class RealtimeVisualizerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val DEFAULT_NUM_COLUMNS = 200
        const val BAR = 0x1
        const val PIXEL = 0x2
        const val FADE = 0x3
    }

    private var mNumColumns = DEFAULT_NUM_COLUMNS
    private var mRenderColor: Int = 0
    private var mVisualizerType: Int = 0

    private var mCanvas: Canvas? = null
    private var mCanvasBitmap: Bitmap? = null
    private val mPaint: Paint = Paint()
    private val mFadePaint: Paint = Paint()
    private val mRect: Rect = Rect()

    private var mColumnWidth: Float = 0F
    private var mSpace: Float = 0F

    private val mHandler = Handler()

    init{
        val args = context.obtainStyledAttributes(attrs, R.styleable.realtime_visualizer_view)
        mNumColumns = args.getInteger(R.styleable.realtime_visualizer_view_numColumns, DEFAULT_NUM_COLUMNS)
        mRenderColor = args.getColor(R.styleable.realtime_visualizer_view_renderColor, Color.WHITE)
        mVisualizerType = args.getInt(R.styleable.realtime_visualizer_view_renderType, BAR)
        args.recycle()

        mPaint.color = mRenderColor
        mFadePaint.color = Color.argb(138, 255, 255, 255)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (w == 0 || h == 0) return

        mRect.set(0, 0, width, height)
        if ( mNumColumns > width )
            mNumColumns = DEFAULT_NUM_COLUMNS
        mColumnWidth = width.toFloat() / mNumColumns.toFloat()
        mSpace = mColumnWidth / 8F

        if(mCanvasBitmap == null) {
            mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            if (mCanvas == null) {
                mCanvas = Canvas(mCanvasBitmap!!)
            }
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(mCanvasBitmap!!, Matrix(), null)
    }

    fun claer(){
        if (mCanvas != null) {
            mCanvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            mHandler.post{ invalidate() }
        }
    }

    fun analyze(amplitude: Int) {
        if (mCanvas == null) return

        if((mVisualizerType and FADE) != 0) {
            mFadePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        } else {
            mCanvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }

        if((mVisualizerType and BAR) != 0) {
            drawBar(amplitude/100)
        }
        if ((mVisualizerType and PIXEL) != 0) {
            // drawPixel(amplitude)
        }

        Log.d("RVV TEST", "RealtimeVisualizerView.analyze($amplitude)")

        mHandler.post{ invalidate() }
    }

    private fun drawBar(amplitude: Int) {
        (0 until mNumColumns).forEach {
            val height = calcRandomHeight(amplitude)
            val startX = it * mColumnWidth + mSpace
            val endX = (it + 1) * mColumnWidth - mSpace

            val rect = createRectF(startX, endX, height)
            mCanvas?.drawRect(rect, mPaint)
        }
    }

    private fun calcRandomHeight(amplitude: Int) : Float {
        val randomAmplitude = Math.random() * amplitude + 1

        return randomAmplitude.toFloat()
    }

    private fun createRectF(_left: Float, _right: Float, _height: Float) : RectF {
        return RectF(_left, height.toFloat() - _height, _right, height.toFloat())
    }
}