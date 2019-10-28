package com.treasure.loopang.ui.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.treasure.loopang.R
import com.treasure.loopang.ui.util.WidthPerTime
import android.graphics.RectF


class BlockLayerView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0):
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    var wpt: WidthPerTime = WidthPerTime()
    var graduationUnit: Int = 200 //ms
    // val blockExpandAnimation: Animation
    var amplitudesDrawable: Drawable? = null

    var currentBlock: BlockView? = null
    val blockViewList: ArrayList<BlockView> = arrayListOf()
    var blockColor: Int = 0

    var basicHeight: Int = 0
    var basicWidth: Int = 0

    var layerId: Int = 0
    var lastBlockId: Int = 0

    var blockBasicWidth: Int = 0
    var blockAnimator: ValueAnimator? = null
    private val blockAnimationListener: BlockAnimatorListener = BlockAnimatorListener()

    // var layerEventListener: LayerEventListener? = null

    private val paint = Paint(Color.BLACK)

    private val path: Path = Path()
    var cornerRadius = 0
    set(value) {
        if (field != value) {
            field = value
            setPath()
            invalidate()
        }
    }


    var roundedCorners = 0
    set(value) {
        if (field != value) {
            field = value
            setPath()
            invalidate()
        }
    }

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

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawColor(resources.getColor(R.color.blockLayerViewBackground, null))
        canvas?.drawLine(0f,0f, width.toFloat(), 0f, paint)
        canvas?.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint)
        canvas?.drawLine(0f, 0f, 0f, height.toFloat(), paint)
        canvas?.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), paint)

        canvas?.clipPath(path)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        basicHeight = height
        basicWidth = width
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setPath()
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

    private fun isCornerRounded(corner: Int): Boolean {
        return (roundedCorners and corner) == corner
    }


    private fun addBlock(blockView: BlockView, listener: BlockView.BlockControlListener? = null) {
        val start = blockView.startTime.ohms
        val duration = blockView.duration.ohms
        val param = LayoutParams(duration * wpt.width, LayoutParams.MATCH_PARENT)
        param.leftMargin = start * wpt.width
        blockView.wpt = wpt
        blockView.layoutParams = param
        blockView.amplitudeDrawable = amplitudesDrawable
        blockView.blockColor = blockColor
        blockView.bcListener = listener
        blockView.cornerRadius = 10
        blockView.roundedCorners = BlockView.CORNER_ALL
        this.addView(blockView)
        this.blockViewList.add(blockView)
        this.currentBlock = blockView

        Log.d("block", "[$layerId,${blockView.blockId}] width: ${param.width}, height: ${param.height}, start: $start, duration: $duration")
    }

    fun addBlock(blockId: Int = lastBlockId++, start: Int, duration: Int, listener: BlockView.BlockControlListener? = null) {
        val blockView = BlockView(context)
        blockView.startTime.set(start)
        blockView.duration.set(duration)
        blockView.layerId = layerId
        blockView.blockId = blockId
        addBlock(blockView, listener)
    }

    private fun deleteBlock(blockView: BlockView) {
        blockViewList.remove(blockView)
        removeView(blockView)
    }

    fun deleteBlock(blockId: Int) {
        var block: BlockView? = null
        for (blockView in blockViewList) {
            if (blockView.blockId == blockId) {
                block = blockView
                break
            }
        }
        block?.let { deleteBlock(it) }
    }

    fun clear(){
        removeAllViews()
        blockViewList.clear()
    }

    fun expandBlock() {
        if(currentBlock == null)
            return
        if (blockAnimator == null) {
            blockAnimator = ValueAnimator.ofInt(0, wpt.width)
            blockAnimator!!.apply{
                duration = wpt.ms.toLong()
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener {
                    val value = it.animatedValue as Int
                    val param = currentBlock!!.layoutParams as LayoutParams
                    param.width = blockBasicWidth + value
                    currentBlock!!.layoutParams = param
                    /*if(param.width + param.leftMargin > width*0.8f){
                        // expandSize()
                        layerEventListener?.onBlockExpand(this@BlockLayerView, param.width + param.leftMargin)
                    }*/
                }
                addListener(blockAnimationListener)
            }
        }
        blockBasicWidth = currentBlock!!.width
        blockAnimator!!.start()
    }

    fun stopExpandBlock() {
        if(currentBlock == null)
            return
        blockAnimator!!.cancel()
    }

    fun expandSize() {
        this.layoutParams = LinearLayout.LayoutParams(width + basicWidth, basicHeight)
    }

    fun mute(muteFlag: Boolean,recordFlag: Boolean, pos: Int = 0) {
        if(muteFlag){
            if(recordFlag) {
                stopExpandBlock()
                currentBlock = null
                Log.d("animation","[Running MUTE]Layer ID : $layerId")
            }
        } else {
            if(recordFlag) {
                addBlock(blockId = blockViewList.size, start = pos, duration = 0)
                expandBlock()
                Log.d("animation","[Running UN_MUTE]Layer ID : $layerId")
            }
        }
    }

    inner class BlockAnimatorListener: Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
            blockBasicWidth += wpt.width
            Log.d("animation", "onAnimationRepeat")
        }

        override fun onAnimationEnd(animation: Animator?) {

            Log.d("animation", "onAnimationEnd")
        }

        override fun onAnimationCancel(animation: Animator?) {

        }

        override fun onAnimationStart(animation: Animator?) {
        }

    }

    interface LayerEventListener{
        fun onBlockExpand(blockLayerView: BlockLayerView,lastPosition: Int)
    }
}


