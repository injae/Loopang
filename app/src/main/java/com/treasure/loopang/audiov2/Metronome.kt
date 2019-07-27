package com.treasure.loopang.audiov2

import java.util.*
import kotlin.concurrent.schedule


typealias MetronomeTask = () -> Unit

class Metronome(var timer: Timer = Timer(),
                var task: MetronomeTask = {} ) {
    var bpm: Long = 40
        set(value) {
            field = 1000 / value
            excute()
        }
    fun excute() { timer.schedule(bpm) { task() } }
    fun cancle() { timer.cancel() }
}