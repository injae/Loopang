package com.treasure.loopang.ui.util

class WidthPerTime(var width: Int = 1, var ms: Int = 1) {
    fun set(width: Int, ms: Int) {
        this.width = width
        this.ms = ms
    }

    fun getWidth(ms: Int): Int {
        return (width.toFloat() * (ms.toFloat() / this.ms.toFloat())).toInt()
    }

    fun getTime(width: Int): Int {
        return (width.toFloat() / (this.width.toFloat() / this.ms.toFloat())).toInt()
    }
}