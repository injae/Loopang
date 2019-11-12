package com.treasure.loopang.audio

import android.util.Log
import com.treasure.loopang.ui.interfaces.IFinalRecorder

class FinalRecorder : IFinalRecorder {
    var mixer = EditableMixer()
    var recorder = OverWritableRecorder()
    var effector = SoundEffector()
    var effectFlagList = mutableListOf<EffectorPresets>()
    var count = 0
    set(value) {
        if(effectFlagList.size != 0)
            effectFlagList.clear()
        (0 until value).forEach {
            effectFlagList.add(EffectorPresets.NONE)
        }
        field = value
    }


    override fun getBlockList(): List<List<SoundRange>> {
        var buf = mutableListOf(recorder.getBlock())
        buf.addAll(mixer.sounds.map{ it.blocks })
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
        val index = layerId - 1

        // 보컬 레코더는 적용안함.
        if(index ==  -1) return

        val sound = mixer.sounds[index].sound.data
        if(effectFlagList[index] == effect) return
        else if(effectFlagList[index] == EffectorPresets.NONE){
            // effector.presetControl(sound, effect)
        } else {
            // effector.presetControl(sound, EffectorPresets.NONE)
            // effector.presetControl(sound, effect)
        }
        effectFlagList[index] = effect
    }

    override fun getBlockList(layerId: Int): List<SoundRange> {
        return if(layerId == 0) recorder.getBlock() else mixer.sounds[layerId].blocks
    }

    override fun insertSounds(soundList: List<Sound>) {
        soundList.forEach { mixer.addSound(it) }
    }

    override fun getRecordDuration(): Int {
        mixer.addSound(recorder.getEditableSound())
        var duration = mixer.duration()
        mixer.sounds.removeAt(mixer.sounds.lastIndex)
        return duration
    }

    override fun getLoopDuration(): Int {
        mixer.addSound(recorder.getEditableSound())
        var duration = mixer.loopDuration()
        mixer.sounds.removeAt(mixer.sounds.lastIndex)
        return duration
    }

    override fun getRecordPosition(): Int {
        return recorder.playedRange.endDuration()
    }

    override fun getLoopPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekToStart() {
        Log.d("AudioTest", "seekToStart")
        mixer.seek(0)
        recorder.seek(0)
    }

    override fun seekToEnd() {
        Log.d("AudioTest", "seekToEnd")
        mixer.addSound(recorder.getEditableSound())
        var duration = mixer.duration()
        mixer.sounds.removeAt(mixer.sounds.lastIndex)
        mixer.seek(duration)
        recorder.seek(duration)
    }

    override fun seekTo(ms: Int) {
        mixer.seek(ms)
        recorder.seek(ms)
    }

    override fun playStart() {
        recorder.blocks.forEach{
            Log.d("AudioTest", "- recorder: ${it.startIndex()} ${it.endIndex()}")
        }
        mixer.sounds.map{ it.blocks }.forEach{
            it.forEach{
                Log.d("AudioTest", "- mixer: ${it.startIndex()} ${it.endIndex()}")
            }
        }
        mixer.addSound(recorder.getEditableSound())
        mixer.start()
    }

    override fun playStop() {
        mixer.stop()
        recorder.seek(mixer.loopDuration())
        mixer.seek(mixer.loopDuration())
        mixer.sounds.removeAt(mixer.sounds.lastIndex)
    }

    override fun recordStart() {
        mixer.seek(recorder.playedRange.endDuration())
        mixer.startBlock()
        if(!mixer.isLooping.get()) mixer.start()
        recorder.start()
    }

    override fun recordStop() {
        recorder.stop()
        mixer.stop()
        recorder.blocks.forEach{
            Log.d("AudioTest", "- recorder: ${it.startIndex()} ${it.endIndex()}")
        }
        mixer.endBlock(recorder.playedRange.endIndex())
        Log.d("AudioTest", "mixer edited: ${mixer.sounds[0].playedRange.endIndex()}")
        mixer.sounds.map{ it.blocks }.forEach{
            it.forEach{
                Log.d("AudioTest", "- mixer: ${it.startIndex()} ${it.endIndex()}")
            }
        }
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
        if(layerId == 0) recorder.isMute.set(flag) else mixer.setMute(layerId - 1,flag)
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