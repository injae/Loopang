package com.treasure.loopang.audio

import com.treasure.loopang.ui.interfaces.IFinalRecorder

class FinalRecorder : IFinalRecorder {
    var mixer = EditableMixer()

    override fun getRecordDuration(): Int {
        return mixer.duration()
    }

    override fun getLoopDuration(): Int {
        return mixer.sounds[0].sound.duration()
    }

    override fun getRecordPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLoopPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekToStart() { mixer.seek(0) }

    override fun seekToEnd() { mixer.seek(mixer.duration()) }

    override fun seekTo(ms: Int) { mixer.seek(ms) }

    override fun playStart() {
        mixer.start()
    }

    override fun playStop() {
        mixer.stop()
    }

    override fun recordStart() {
        mixer.startBlock()
    }

    override fun recordStop() {
        mixer.endBlock()
    }

    override fun export(title: String, soundFormat: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayStart(recordPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayStop(recordPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRecrodStart(recordPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRecordStop(recordPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRecording(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPlaying(): Boolean {
        return mixer.isLooping.get()
    }

    override fun setMute(layerId: Int, flag: Boolean) {
        mixer.setMute(layerId,flag)
    }

    override fun setOverwrite(flag: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setMetronome(flag: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setEffectToBlock(layerId: Int, blockId: Int, effect: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVolumeToBlock(layerId: Int, blockId: Int, volume: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVolumeToLayer(layerId: Int, volume: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setVolumeToLoop(volume: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEffectList(): List<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBlockList(): List<List<Any>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}