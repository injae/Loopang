package com.treasure.loopang.audio

import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

class EditableSound:
    SoundFlow<EditableSound> {
    var blocks: SoundBlocks
    var isMute: AtomicBoolean =
        AtomicBoolean(false)
    var isEnd: AtomicBoolean =
        AtomicBoolean(false)
    var effectorIndex: Int
    var sound: Sound


    constructor(sound: Sound) {
        this.sound = sound
        blocks = SoundBlocks(sound)
        effectorIndex = sound.addEffector {
            var start = blocks.point()
            if(!(!blocks.isRecording.get() && isEnd.get())) blocks += it.size
            var end = blocks.point()
            var curRange = blocks.location.makeFromIndex(start).expand(end-start)
            var retZero = false
            var remain = true
            if(!blocks.isRecording.get()) {
                while(remain) {
                    remain = false
                    if (!blocks.isEmpty()) {
                        Log.d(
                            "AudioTest",
                            "B(${blocks})::blocks[${blocks.location}] playing: [${curRange.startIndex()}:${curRange.endIndex()}]"
                        )
                        if (blocks.playedIndex + 1 <= blocks.size() -1) {
                            isEnd.set(false)
                            var btw = blocks.current().between(blocks.next())
                            Log.d(
                                "AudioTest",
                                "B(${blocks}):: between: [${btw.startIndex()}:${btw.endIndex()}]"
                            )
                            if(btw.isComplict(curRange)) {
                                var complict = btw.complictedRange(curRange)
                                Log.d(
                                    "AudioTest",
                                    "B(${blocks}):: complict: [${complict.startIndex()}:${complict.endIndex()}]"
                                )
                                var zeroStart = complict.startIndex() - start
                                var zeroEnd   = complict.endIndex()   - start
                                for ((index, _) in it.withIndex()) { if (zeroStart <= index && index < zeroEnd) it[index] = 0 }
                                curRange.expand(1)
                                if (blocks.next().isComplict(curRange)) { blocks++ }
                            }
                            else {
                                if (blocks.next().isComplict(curRange)) { blocks++; remain=true }
                                else {
                                    Log.d(
                                        "AudioTest",
                                        "B(${blocks}):: playing: [${curRange.startIndex()}:${curRange.endIndex()}]"
                                    )
                                }
                            }
                        } else {
                            if(curRange.isComplict(blocks.current())) {
                                isEnd.set(false)
                                var complict = curRange.complictedRange(blocks.current())
                                Log.d(
                                    "AudioTest",
                                    "B(${blocks}):: playable: [${complict.startIndex()}:${complict.endIndex()}]"
                                )
                                var nonZeroStart = complict.startIndex() - start
                                var nonZeroEnd   = complict.endIndex()   - start
                                Log.d(
                                    "AudioTest",
                                    "B(${blocks}):: none zero: [${nonZeroStart}:${nonZeroEnd}]"
                                )
                                if(nonZeroEnd != it.size) for ((index, _) in it.withIndex()) { if (!(nonZeroStart <= index && index < nonZeroEnd)) it[index] = 0 }
                            }
                            else {
                                retZero = true
                                isEnd.set(true)
                                callSuccess(this@EditableSound)
                                Log.d("AudioTest", "B(${blocks}):: stop")
                            }
                        }
                    }
                    else { retZero=true }
                }
            }
            else {
                Log.d(
                    "AudioTest",
                    "B(${blocks}):: record: ${curRange.startIndex()} ${curRange.endIndex()}"
                )
            }
            if(retZero) ShortArray(it.size, { 0 })
            else it
        }
    }

    fun play() {
        if(!isMute.get() && !isEnd.get()) {
            Log.d("AudioTest", " play index: ${blocks.point()}")
            sound.play()
        }
    }

    fun record(){
        if(!isMute.get()) blocks.record()
    }

    fun recordStop(limit:Int?)=blocks.stop(limit)
    fun seek(index:Int)=blocks.seek(index)


    fun stop() {
        if(!isMute.get()) sound.stop()
    }

    fun makeSound(): Sound {
        var maxLength = blocks.duration()
        blocks.seek(0)
        var export = Sound()
        sound.isMute.set(true)
        var save_effector = sound.addEffector {
            blocks.location.expand(it.size)
            var diff = blocks.point() - maxLength
            if(diff > 0)  {
                for((index, _) in it.withIndex()) { if(index > it.size - diff) it[index] = 0 }
                sound.stop()
            }
            sound.data.addAll(it.toMutableList())
            it
        }
        while(sound.isPlaying.get()) { sound.play() }
        sound.removeEffector(save_effector)
        blocks.seek(maxLength)
        return export
    }
}