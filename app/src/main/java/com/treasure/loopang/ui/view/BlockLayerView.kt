package com.treasure.loopang.ui.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.treasure.loopang.ui.util.WidthPerTime

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

    var layerEventListener: LayerEventListener? = null

    private val paint = Paint(Color.BLACK)

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d("BlockLayerView Draw", "Canvas is null? : ${(canvas == null)}")
        canvas?.drawColor(Color.WHITE)
        canvas?.drawLine(0f,0f, width.toFloat(), 0f, paint)
        canvas?.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint)
        canvas?.drawLine(0f, 0f, 0f, height.toFloat(), paint)
        canvas?.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        basicHeight = height
        basicWidth = width
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


