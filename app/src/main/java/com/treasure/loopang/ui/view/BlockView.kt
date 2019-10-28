package com.treasure.loopang.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.treasure.loopang.ui.util.TimeWrapper
import com.treasure.loopang.ui.util.WidthPerTime
import kotlin.math.round

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

    private val path: Path = Path()
    var cornerRadius = 0
        set(value) {
            if (cornerRadius != value) {
                field = value
                setPath()
                invalidate()
            }
        }

    var roundedCorners = 0
        set(value) {
            if (roundedCorners != value) {
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
        isFocusable = true
        isClickable = true
        setOnClickListener { bcListener?.onClickListener(layerId, blockId) }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.clipPath(path)
        canvas?.drawColor(blockColor)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setPath()
    }

    private fun setPath() {
        path.rewind()
        if ((cornerRadius >= 1f) && (roundedCorners != BlockLayerView.CORNER_NONE)) {
            Log.d("ConerRadius", "코너의 패스를 만듭니다. cornerRadius")
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
            invalidate()
        }
    }

    private fun isCornerRounded(corner: Int): Boolean {
        return (roundedCorners and corner) == corner
    }


    interface BlockControlListener {
        fun onClickListener(layerId: Int, blockId:Int)
    }
}

