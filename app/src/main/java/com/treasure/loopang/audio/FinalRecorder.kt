package com.treasure.loopang.audio

import android.util.Log
import com.treasure.loopang.ui.interfaces.IFinalRecorder

class FinalRecorder : IFinalRecorder {
    var mixer = EditableMixer()
    var recorder = OverWritableRecorder()
    var effector = SoundEffector()
    var effectFlagList = mutableListOf<EffectorPresets>()
    val soundDir = FileManager().looperSoundDir.absolutePath

    override fun getBlockList(): List<List<SoundRange>> {
        var buf = mutableListOf(recorder.getBlock())
        buf.addAll(mixer.sounds.map{ it.blocks.list })
        return buf
    }

    override fun setEffectToBlock(layerId: Int, blockId: Int, effect: EffectorPresets) {
        if(layerId == 0) return

        val index = layerId - 1
        val sound = mixer.sounds[index].sound.data
        var newData: MutableList<Short>

        if(effectFlagList[index] == effect) return

        if(effectFlagList[index] == EffectorPresets.NONE){
            newData = effector.presetControl(sound, effect)
        } else {
            newData = effector.presetControl(sound, EffectorPresets.NONE)
            if(effect != EffectorPresets.NONE)
                newData = effector.presetControl(newData, effect)
        }

        effectFlagList[index] = effect
        mixer.sounds[index].sound.data  = newData
    }

    override fun getBlockList(layerId: Int): List<SoundRange> {
        return if(layerId == 0) recorder.getBlock() else mixer.sounds[layerId].blocks.list
    }

    override fun insertSounds(soundList: List<Sound>) {
        soundList.forEach { mixer.addSound(it) }
        initEffectFlagList(soundList.size)
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
        return recorder.blocks.pointMs()
    }

    override fun getLoopPosition(): Int {
        return mixer.sounds[0].blocks.playedIndex / mixer.sounds[0].sound.info.tenMsSampleRate
    }

    override fun seekToStart() {
        Log.d("AudioTest", "seekToStart")
        mixer.seek(0)
        recorder.seek(0)
    }

    override fun seekToEnd() {
        var duration = recorder.blocks.durationMs()
        Log.d("AudioTest", "seekToEnd: ${duration}")
        mixer.seek(duration)
        recorder.seek(duration)
    }

    override fun seekTo(ms: Int) {
        mixer.seek(ms)
        recorder.seek(ms)
    }

    override fun playStart() {
        recorder.blocks.list.forEach{
            Log.d("AudioTest", "- recorder: ${it.startIndex()} ${it.endIndex()}")
        }
        mixer.sounds.map{ it.blocks.list }.forEach{
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
        mixer.seek(recorder.blocks.pointMs())
        mixer.startBlock()
        if(!mixer.isLooping.get()) mixer.start()
        recorder.start()
    }

    override fun recordStop() {
        recorder.stop()
        mixer.stop()
        recorder.blocks.list.forEach{
            Log.d("AudioTest", "- recorder: ${it.startIndex()} ${it.endIndex()}")
        }
        mixer.endBlock(recorder.blocks.point())
        Log.d("AudioTest", "mixer edited: ${mixer.sounds[0].blocks.point()}")
        mixer.sounds.map{ it.blocks.list }.forEach{
            it.forEach{
                Log.d("AudioTest", "- mixer: ${it.startIndex()} ${it.endIndex()}")
            }
        }
    }

    override fun export(title: String, soundFormat: String): Boolean {
        mixer.save("$soundDir/$title.$soundFormat")
        var voice = EditableSound(recorder.getSound())
        voice.blocks.list.add(SoundRange(voice.sound, 0, voice.sound.data.size))
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
        if(layerId  ==  0) return // 보컬 레코더는 적용안함.

        val index = layerId - 1
        val sound = mixer.sounds[index].sound.data

        // 이펙터에 적용
        val newData = effector.volumeControl(sound, volume)

        // 다시 믹서에 넣어줌
        mixer.sounds[index].sound.data  = newData
    }

    override fun setVolumeToLoop(volume: Int) {
        val sounds = mixer.sounds

        sounds.forEach {
            val sound = it.sound.data
            val newData = effector.volumeControl(sound, volume)
            it.sound.data = newData
        }
    }

    private fun initEffectFlagList(num: Int) {
        if(effectFlagList.size != 0)
            effectFlagList.clear()
        (0 until num).forEach {
            effectFlagList.add(EffectorPresets.NONE)
        }
    }

    fun getEffectFlag(layerId: Int): EffectorPresets {
        if(layerId == 0) return EffectorPresets.NONE
        val index = layerId - 1
        return effectFlagList[index]
    }
}