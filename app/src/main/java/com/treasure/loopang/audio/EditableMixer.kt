package com.treasure.loopang.audio


class SoundRange(var start: Int = 0, var end: Int = 0, var repeat: Int = 0) {

}

class EditableSound(var _sound: Sound) {
    var isMute: Boolean = false
    var blocks: MutableList<SoundRange> = mutableListOf()
    var isBuildBlock = false
    fun play() {
        if(!isMute) _sound.play()
    }
    fun startBlock() {

    }
    fun endBlock() {

    }
    fun stop() {
        if(!isMute) _sound.stop()
    }
}


class EditableMixer(var sounds: MutableList<EditableSound> = mutableListOf()) {

    fun addSound(sound: Sound) {
        sounds.add(EditableSound(sound))
    }

}