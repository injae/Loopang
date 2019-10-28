package com.treasure.loopang.audio

import com.treasure.loopang.ui.interfaces.IFinalRecorder

class FinalRecorder : IFinalRecorder {
    var mixer = EditableMixer()
    var recorder = OverWritableRecorder()

    override fun getBlockList(): List<List<SoundRange>> {
        var buf = mixer.sounds.map{ it.blocks }.toMutableList()
        buf.add(recorder.blocks)
        return buf
    }

    override fun onPlayStart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayStop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRecordStart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRecordStop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setEffectToBlock(layerId: Int, blockId: Int, effect: EffectorPresets) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBlockList(layerId: Int): List<SoundRange> {
        return if(layerId == 0) recorder.blocks else mixer.sounds[layerId].blocks
    }

    override fun insertSounds(soundList: List<Sound>) {
        soundList.forEach { mixer.addSound(it) }
    }

    override fun getRecordDuration(): Int {
        return mixer.duration()
    }

    override fun getLoopDuration(): Int {
        return mixer.sounds[0].sound.duration()
    }

    override fun getRecordPosition(): Int {
        return mixer.currentPositioin()
    }

    override fun getLoopPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekToStart() {
        mixer.seek(0)
        recorder.seek(0)
    }

    override fun seekToEnd() {
        mixer.seek(mixer.duration())
        recorder.seek(mixer.duration())
    }

    override fun seekTo(ms: Int) {
        mixer.seek(ms)
        recorder.seek(ms)
    }

    override fun playStart() {
        mixer.addSound(recorder.getSound())
        mixer.start()
    }

    override fun playStop() {
        mixer.stop()
        mixer.sounds.removeAt(mixer.sounds.size - 1)
    }

    override fun recordStart() {
        mixer.startBlock()
        if(!mixer.isLooping.get()) mixer.start()
        recorder.start()
    }

    override fun recordStop() {
        mixer.endBlock()
        mixer.stop()
        recorder.stop()
    }

    override fun export(title: String, soundFormat: String): Boolean {
        mixer.save(title+soundFormat)
        var voice = EditableSound(recorder.getSound())
        voice.blocks.add(SoundRange(voice.sound, 0, voice.sound.data.size))
        mixer.sounds.add(voice)
        return true
    }

    override fun isRecording(): Boolean {
        return recorder.isRecording.get()
    }

    override fun isPlaying(): Boolean {
        return mixer.isLooping.get()
    }

    override fun setMute(layerId: Int, flag: Boolean) {
        if(layerId == 0) recorder.isMute.set(flag) else mixer.setMute(layerId,flag)
    }

    override fun setOverwrite(flag: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setMetronome(flag: Boolean) {
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
}