package com.treasure.loopang.audio

class SoundRange(var sound: Sound, var cycle: Int = 0, var start: Int = 0, var end: Int = 0, var repeat: Int = 0) {
    var soundLength: Int

    init {
        if(sound.data.isEmpty()) sound.data = ShortArray(size=sound.info.sampleRate).toMutableList()
        soundLength = sound.data.size
    }

    fun remove(index: Int) {
        var buf = subRange(index-startIndex())
        end = buf.end
        repeat = buf.repeat
    }

    fun size()=endIndex() - startIndex()

    fun startIndex()=cycle*soundLength + start

    fun endIndex()=((cycle*soundLength) + start) + ((repeat*soundLength) + end)

    fun startDuration(): Int {
        var sd = startIndex()
        if(sd == 0) return 0
        return sd / sound.info.tenMsSampleRate
    }

    fun endDuration()=endIndex() / sound.info.tenMsSampleRate

    fun isOver(other: SoundRange)=startIndex() > other.endIndex()

    fun isComplict(other: SoundRange): Boolean {
        var st = other.startIndex()
        var lt = other.endIndex()
        var si = startIndex()
        var ei = endIndex()
        return (si <= st && st <= ei)
            || (si <= lt && lt <= ei)
            || (st <= si && si <= lt)
            || (st <= ei && ei <= lt)
    }

    fun complictedRange(other: SoundRange): SoundRange {
        if(isComplict(other)) {
            var endFirst  = if(endIndex() < other.endIndex()) this else other
            var startLast = if(startIndex() <= other.startIndex()) other else this

            return startLast.subRange(endFirst.endIndex()-startLast.startIndex())
        }
        return other
    }

    fun makeFromIndex(index: Int)=
        SoundRange(sound, index / soundLength, index % soundLength)

    fun seekFront() { end = 0; repeat = 0 }


    fun subRange(index: Int): SoundRange {
        var bend = end; var brepeat = repeat
        seekFront()
        expand(index)
        var range = SoundRange(sound, cycle, start, end, repeat)
        end = bend; repeat = brepeat
        return range
    }


    fun overWrite(range: SoundRange): Boolean {
        if(isComplict(range)) {
            remove(range.startIndex())
            return true
        }
        else {
            return false
        }
    }

    fun between(range: SoundRange): SoundRange {
        var btw = nextRange()
        btw.expand(range.startIndex() - btw.endIndex())
        return btw
    }


    fun expand(size: Int): SoundRange {
        end += size
        repeat += (end / soundLength)
        end = (end % soundLength)
        return this
    }

    fun nextRange(): SoundRange {
        var eindex = endIndex()
        if(eindex == 0) return SoundRange(sound)
        return SoundRange(
            sound,
            cycle = eindex / soundLength,
            start = eindex % soundLength
        )
    }
}