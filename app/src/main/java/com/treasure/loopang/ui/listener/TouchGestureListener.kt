package com.treasure.loopang.ui.listener

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class TouchGestureListener : GestureDetector.OnGestureListener{
    companion object {
        private const val SWIPE_THRESHOLD = 100    // 스와이프 진단을 위한 위치차 임계치
        private const val SWIPE_VELOCITY_THRESHOLD = 100   // 스와이프 진단을 위한 속도 임계치
    }

    var onSingleTap: () -> Boolean = { false }
    var onSwipeToUp: () -> Boolean = { false }
    var onSwipeToDown: () -> Boolean = { false }
    var onSwipeToRight: () -> Boolean = { false }
    var onSwipeToLeft: () -> Boolean = { false }

    override fun onShowPress(e: MotionEvent?) = Unit

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        // Log.d("TouchGestureTest", "\nSingle Tap Up!\nisSwiping: $isSwiping")
        return onSingleTap()
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if(e1 == null || e2 == null) return false
        val diffY: Float = e2.y - e1.y
        val diffX: Float = e2.x - e1.x
        if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            return if (diffX > 0) onSwipeToRight()
            else onSwipeToLeft()
        }
        if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            return if (diffY > 0) onSwipeToDown()
            else onSwipeToUp()
        }

        return false
    }

    override fun onDown(e: MotionEvent?): Boolean = true
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = false
    override fun onLongPress(e: MotionEvent?) = Unit
}