package com.treasure.loopang.audiov2

import android.os.Handler
import android.util.Log

typealias MetronomeTask = () -> Unit

class Note(var child: Int, var parent: Int) {
    fun bpm(bpm: Long) =
        when(parent) {
               2 -> bpm * 2
               4 -> bpm
               8 -> bpm / 2
              16 -> bpm / 4
              32 -> bpm / 8
              64 -> bpm / 16
             128 -> bpm / 32
            else -> error("unknown note")
        }
}

class Metronome( var task: MetronomeTask = {}){

    var note: Note = Note(1,4)
        set(value) {
            field = value
            bpm = bpm
        }
    var bpm: Long = 60
        set(value) {
            field = value
            cancle()
            excute()
        }
    private var timer: Handler = Handler()
    private var runnable = object: Runnable {
        override fun run() {
            task()
            timer.postDelayed(this, 60000/note.bpm(bpm))
            Log.d("d","bpm: "+ 60000/note.bpm(bpm))
        }
    }

    fun excute() {
        timer.post(runnable)
    }

    fun cancle() { timer.removeCallbacks(runnable) }
}