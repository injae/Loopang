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
        buf.addAll(mixer.sounds.map{ it.blocks.list })
        return buf
    }

    override fun setEffectToBlock(layerId: Int, blockId: Int, effect: EffectorPresets) {
        val index = layerId - 1

        // 보컬 레코더는 적용안함.
        if(index ==  -1) return

        val sound = mixer.sounds[index].sound.data
        if(effectFlagList[index] == effect) return
        else if(effectFlagList[index] == EffectorPresets.NONE){
            // var newData = effector.presetControl(sound, effect)
        } else {
            // newData = effector.presetControl(sound, EffectorPresets.NONE)
            // newData = effector.presetControl(newData, effect)
        }
        effectFlagList[index] = effect
        // mixer.sounds[index].sound.data  = newData
    }

    override fun getBlockList(layerId: Int): List<SoundRange> {
        return if(layerId == 0) recorder.getBlock() else mixer.sounds[layerId].blocks.list
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
        return recorder.blocks.point()
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
        mixer.save(title+soundFormat)
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
        val editedVolume = volume.toFloat() / 100

        // 이펙터에 적용
        // val newData = effector.volumeControl(sound, editedVolume)

        // 다시 믹서에 넣어줌
        // mixer.sounds[index].sound.data  = newData
    }

    override fun setVolumeToLoop(volume: Int) {
        val sounds = mixer.sounds
        val editedVolume = volume.toFloat() / 100

        sounds.forEach {
            val sound = it.sound.data
            // val newData = effector.volumeControl(sound, editedVolume)
            // it.sound.data = newData
        }
    }
}