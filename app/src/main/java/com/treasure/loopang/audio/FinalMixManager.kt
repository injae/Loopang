package com.treasure.loopang.audio


class FinalMixManager(var recorder: Recorder, var mixer: Mixer) {
    var effectorNum = hashMapOf<Int,Int>()
    fun start() {
        mixer.sounds.forEach { it.isMute = true }
        mixer.sounds[0].isMute = false
        recorder.start()
        mixer.start()
    }

    fun stop() {
        recorder.stop()
        mixer.stop()
        removeAll()
    }

    fun add(index: Int) {
         effectorNum[index] = mixer.sounds[index]._sound.addEffector {
             var effIndex = recorder.addTimeEffector { data ->
                 data.map { mutableListOf(it) }
                     .fold(MutableList<Short>(data.size) {0}) {
                     acc, it -> acc.zip(it) { a, b -> (a + b).toShort() }.toMutableList() }.toShortArray()
             }
             recorder.addEffector { recorder.removeTimeEffector(effIndex); it; }
             it
         }
    }

    fun remove(index: Int) {
        effectorNum[index]?.let { mixer.sounds[index]._sound.removeEffector(it) }
    }

    fun removeAll() {
        effectorNum.forEach { remove(it.value) }
    }
}