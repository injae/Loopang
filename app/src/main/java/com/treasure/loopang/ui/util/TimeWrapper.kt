package com.treasure.loopang.ui.util

class TimeWrapper(var ms: Int = 0) {
    var s = ms / 1000
    var hs = ms / 500
    var thms = ms / 200
    var ohms = ms / 100

    fun set(ms: Int) {
        this.ms = ms
        s = ms / 1000
        hs = ms / 500
        thms = ms / 200
        ohms = ms / 100
    }
}