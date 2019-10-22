package com.treasure.loopang.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.treasure.loopang.ui.util.TimeWrapper
import com.treasure.loopang.ui.util.WidthPerTime

class BlockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var layerId: Int = 0
    var blockId: Int = 0

    var wpt: WidthPerTime = WidthPerTime()
    var startTime: TimeWrapper = TimeWrapper()
    var endTime: TimeWrapper = TimeWrapper()
    var duration: TimeWrapper = TimeWrapper()

    var bcListener: BlockControlListener? = null

    var amplitudeCanvas: Canvas? = null
    var amplitudeDrawable: Drawable? = null

    var blockColor: Int = 0
    var blockBackgroundPaint: Paint? = null
    var blockRectPaint: Paint? = null

    init {
        isFocusable = true
        isClickable = true
        setOnClickListener { bcListener?.onClickListener(layerId, blockId) }
    }

    override fun onDraw(canvas: Canvas?) {
        setBackgroundColor(blockColor)
    }

    interface BlockControlListener {
        fun onClickListener(layerId: Int, blockId:Int)
    }
}

