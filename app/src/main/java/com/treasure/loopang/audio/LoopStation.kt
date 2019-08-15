package com.treasure.loopang.audio

import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import com.treasure.loopang.ui.view.RealtimeVisualizerView
import kotlin.math.abs

class LoopStation {
    private var linkedVisualizer: RealtimeVisualizerView? = null
    private val mMixer: Mixer = Mixer()
    private val mRecorder: Recorder = Recorder()
    private val mMetronome: Metronome = Metronome()

    private val mFileManager = FileManager()
    private val mDirectoryPath = mFileManager.looperSoundDir.absolutePath

    private var mLoopTitle: String = ""
    private val mLayerLabelList: MutableList<String> = mutableListOf()

    private var firstRecordFlag: Boolean = true
    private var whiteNoiseCheckFlag: Boolean = false

    private var mLoopStationEventListener: LoopStationEventListener? = null
    private var mLoopStationMessageListener: LoopStationMessageListener? = null

    private var mLastLayerNum: Int = 1

    private val mHandler: Handler = Handler()

    companion object{
        const val DEFAULT_LAYER_LABEL = "Layer "
        const val SAVE_SUCCESS = 0
        const val SAVE_ERROR_DUPLICATE_NAME = 2
        const val SAVE_ERROR_NONE_LAYER = 3
    }

    init { initRecorder() }

    fun setLoopStationEventListener(loopStationEventListener: LoopStationEventListener) {
        mLoopStationEventListener = loopStationEventListener
    }

    fun setLoopStationMessageListener(loopStationMessageListener: LoopStationMessageListener){
        mLoopStationMessageListener = loopStationMessageListener
    }

    fun linkVisualizer(visualizer: RealtimeVisualizerView) {
        this.linkedVisualizer = visualizer
    }

    private fun initRecorder(){
        mRecorder.apply{
            onSuccess {
                mHandler.post{ hideVisualizer() }
                addLayer(it.getSound())
                mLoopStationEventListener?.onRecordFinish()
                mHandler.post{ mLoopStationMessageListener?.onRecordFinish() }
            }
            onStart {
                mLoopStationEventListener?.onRecordStart()
                mHandler.post{ mLoopStationMessageListener?.onRecordStart() }
            }
            addEffector {
                linkedVisualizer?.analyze(
                    it.fold(0) { acc, next->
                        acc + abs(next.toInt())
                    } / it.size
                )
                it
            }
        }
    }

    fun recordStart(firstRecordMessageFlag: Boolean = true) {
        if(isFirstRecord()){
            if(firstRecordMessageFlag) mLoopStationMessageListener?.onFirstRecord()
            mLoopStationEventListener?.onFirstRecord()
            firstRecordFlag = false
        } else if (!whiteNoiseCheckFlag) {
            mLoopStationEventListener?.onFirstRecord()
        } else{
            when {
                (!isLooping() && !isEmpty()) -> {
                    mLoopStationMessageListener?.onRecordWithoutLoopingError()
                    return
                }

                isRecording() -> {
                    mLoopStationMessageListener?.onDuplicateRecordStartError()
                    return
                }

                mMixer.sounds.isNotEmpty() -> mRecorder.start(mMixer.sounds[0].data.size)
                else -> mRecorder.start()
            }
            showVisualizer()
            mLoopStationEventListener?.onRecordStart()
            mLoopStationMessageListener?.onRecordStart()
        }
    }

    fun recordStop(messageFlag: Boolean = true) {
        if(!isRecording()) {
            if(messageFlag) mLoopStationMessageListener?.onDuplicateRecordStopError()
            return
        }
        mRecorder.stop()
        mLoopStationEventListener?.onRecordStop()
        if(messageFlag) mLoopStationMessageListener?.onRecordStop()
    }

    fun loopStart(messageFlag: Boolean = true) {
        if(isLooping()) {
            mLoopStationMessageListener?.onDuplicateLoopStartError()
            return
        }
        mMixer.start()
        mLoopStationEventListener?.onLoopStart()
        if(messageFlag) {
            mLoopStationMessageListener?.onLoopStart()
        }
    }

    fun loopStop(messageFlag: Boolean = true) {
        if(!isLooping()) {
            if(messageFlag)
                mLoopStationMessageListener?.onDuplicateLoopStopError()
            return
        }
        mMixer.stop()
        mLoopStationEventListener?.onLoopStop()
        if(messageFlag) {
            mLoopStationMessageListener?.onLoopStop()
        }
    }

    fun addLayer(sound: Sound,
                 layerLabel: String = this.makeNotDuplicatedLabel(DEFAULT_LAYER_LABEL + mLastLayerNum++),
                 autoLoopStartFlag: Boolean = true,
                 loopStartMessageFlag: Boolean = false,
                 addLayerMessageFlag: Boolean = true) {
        this._addLayer(sound, layerLabel)
        if (!isLooping() && autoLoopStartFlag){
            loopStart(loopStartMessageFlag)
        }
        mLoopStationEventListener?.onLayerAdd(sound, layerLabel)
        if(addLayerMessageFlag) {
            mHandler.post{ mLoopStationMessageListener?.onLayerAdd() }
        }
    }

    private fun _addLayer(sound: Sound,
                          layerLabel: String) {
        mMixer.addSound(sound)
        mLayerLabelList.add(layerLabel)
    }

    fun muteLayer(position: Int,
                  messageFlag: Boolean = true): Boolean {
        Log.d("LayerFunctionTest", "muteLayer(position: $position)")
        val muteState = mMixer.switchMuteWithReturnState(position)
        val layerLabel = mLayerLabelList[position]
        if(muteState){
            mLoopStationEventListener?.onLayerMute(position)
            if(messageFlag) mLoopStationMessageListener?.onLayerMute(position, layerLabel)
        } else {
            mLoopStationEventListener?.onLayerMuteCancel(position)
            if(messageFlag) mLoopStationMessageListener?.onLayerMuteCancel(position, layerLabel)
        }
        return muteState
    }

    fun playLayer(position: Int) {

    }

    fun stopPlayingLayer(messageFlag: Boolean = true) {

    }

    fun dropLayer(position: Int) {
        Log.d("LayerFunctionTest", "dropLayer(position: $position)")
        val layerLabel: String = mLayerLabelList[position]
        mMixer.sounds.removeAt(position)
        mLayerLabelList.removeAt(position)
        mLoopStationEventListener?.onLayerDrop(position)
        mLoopStationMessageListener?.onLayerDrop(position, layerLabel)
    }

    fun dropAllLayer(messageFlag: Boolean = true) {
        Log.d("LayerFunctionTest", "dropAllLayer()")
        if(isEmpty()){
            if(messageFlag) mLoopStationMessageListener?.onDropAllLayerEmptyError()
            return
        }
        if(isRecording()){
            if(messageFlag) mLoopStationMessageListener?.onDropAllLayerDuringRecordingError()
            return
        }
        loopStop(messageFlag = false)
        mMixer.sounds.clear()
        mLayerLabelList.clear()
        mLastLayerNum = 1
        mLoopStationEventListener?.onLayerAllDrop()
        if(messageFlag) mLoopStationMessageListener?.onLayerAllDrop()
    }

    fun changeLayerLabel(position: Int,
                         layerLabel: String,
                         messageFlag: Boolean = false) {
        if(layerLabel == "") return
        val oldLayerLabel = mLayerLabelList[position]
        mLayerLabelList[position] = layerLabel
        mLoopStationEventListener?.onChangeLayerLabel(position, layerLabel, oldLayerLabel)
        if(messageFlag) mLoopStationMessageListener?.onChangeLayerLabel(position, layerLabel, oldLayerLabel)
    }

    fun setTitle(title: String,
                    messageFlag: Boolean = false) {
        if(title == "") return
        val oldTitle = mLoopTitle
        mLoopTitle = title
        mLoopStationEventListener?.onTitleChanged(title, oldTitle)
        if(messageFlag) mLoopStationMessageListener?.onTitleChanged(title, oldTitle)
    }

    fun stopAll() {
        recordStop(messageFlag = false)
        loopStop(messageFlag = false)
        stopPlayingLayer(messageFlag = false)
    }

    fun import(project: LoopMusic,
               newLoadFlag: Boolean = false,
               messageFlag: Boolean = false){
        // 기존 레이어들을 모두 드롭하고 새 프로젝트를 로드할지 결정하는 분기문.
        if(newLoadFlag) {
            mLoopTitle = project.name
            mLoopStationEventListener?.onTitleChanged(mLoopTitle, "")
            dropAllLayer(messageFlag = false)
        } else {
            if(getSounds().isEmpty()) return
        }
        // 프로젝트 파일 혹은 사운드 파일 여부에 따라 분기함.
        project.child?.let {
            it.forEach { child ->
                val sound = Sound().apply { load(child.path) }
                val layerLabel = this.makeNotDuplicatedLabel(child.name)
                this._addLayer(sound, layerLabel)
            }
        }.let {
            if(it == null) {
                val sound = Sound().apply{ load(project.path) }
                val layerLabel = DEFAULT_LAYER_LABEL + mLastLayerNum++
                this._addLayer(sound, layerLabel)
            }
        }
        mLoopStationEventListener?.onImport(mLoopTitle, newLoadFlag)
        if(messageFlag) mLoopStationMessageListener?.onImport(mLoopTitle, newLoadFlag)
    }

    fun export(loopTitle: String = mLoopTitle,
               fileType: String = ".pcm",
               allDropFlag: Boolean = false,
               mixFlag: Boolean = false): Int {
        if(isEmpty()) {
            mLoopStationMessageListener?.onSaveNoneLayerError()
            return SAVE_ERROR_NONE_LAYER
        }
        if(mFileManager.checkDuplication(loopTitle)) return SAVE_ERROR_DUPLICATE_NAME
        if (mixFlag || getSounds().size == 1){
            val fileLabel = "/$loopTitle.${fileType.toLowerCase()}"
            Sound(mMixer.mixSounds()).save(mDirectoryPath+fileLabel)
        } else {
            val children = mLayerLabelList.map{ LoopMusic(it) }
            children.forEach{ Log.d("export", "Saved Layer Name : ${it.name}") }
            mMixer.save(LoopMusic(name=loopTitle,type= fileType.toLowerCase(), child=children))
        }

        if(allDropFlag) dropAllLayer(messageFlag = false)

        return SAVE_SUCCESS
    }

    fun checkWhiteNoise(): Boolean {
        whiteNoiseCheckFlag = true
        return true
    }

    fun isRecording(): Boolean = mRecorder.isRecording.get()
    fun isLooping(): Boolean = mMixer.isLooping.get()
    fun isFirstRecord(): Boolean = firstRecordFlag
    fun isEmpty(): Boolean = mMixer.sounds.isEmpty()
    fun isNotLoopingAndEmpty(): Boolean = (!(this.isLooping()) && this.isEmpty())

    fun setMetronomeBpm(bpm: Long) {
        mMetronome.bpm = bpm
    }
    fun setOnMetronomeTik(task: MetronomeTask) {
        mMetronome.task = {
            task()
            Log.d("Metronome test", "tick")
        }
    }
    fun MetronomeStart() {}
    fun MetronomeStop() {}

    private fun showVisualizer() {
        linkedVisualizer?.let{
            if(it.visibility == View.GONE) {
                it.visibility = View.VISIBLE
            }
        }
    }

    private fun hideVisualizer() {
        linkedVisualizer?.let{
            if(it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
                it.clear()
            }
        }
    }

    private fun makeNotDuplicatedLabel(label: String): String {
        return if(checkLayerLabelDuplication(label)) {
            var num = 1
            var tempLabel = "${label}_${num++}"
            while(checkLayerLabelDuplication(tempLabel)) tempLabel = "${label}_${num++}"
            tempLabel
        } else label
    }

    private fun checkLayerLabelDuplication(label: String): Boolean {
        return (label in mLayerLabelList)
    }

    fun getMixer() : Mixer = mMixer
    fun getRecorder(): Recorder = mRecorder
    fun getSounds() : MutableList<MixerSound> = mMixer.sounds
    fun getLayerLabels() : MutableList<String> = mLayerLabelList

    interface LoopStationEventListener {
        fun onRecordStart() {}
        fun onRecordStop() {}
        fun onRecordFinish() {}
        fun onLoopStart() {}
        fun onLoopStop() {}
        fun onLoopFinish() {}
        fun onLayerAdd(sound: Sound, layerLabel: String) {}
        fun onLayerDrop(position: Int) {}
        fun onLayerAllDrop() {}
        fun onLayerPlay(position: Int) {}
        fun onLayerMute(position: Int) {}
        fun onLayerMuteCancel(position: Int) {}
        fun onChangeLayerLabel(position: Int, newLayerLabel: String, oldLayerLabel: String) {}
        fun onTitleChanged(newTitle: String, oldTitle: String) {}
        fun onFirstRecord(): Boolean = false

        fun onExport(loopTitle: String) {}
        fun onImport(loopTitle: String, newLoadFlag: Boolean) {}
    }

    interface LoopStationMessageListener {
        fun onDuplicateRecordStartError() {}
        fun onDuplicateRecordStopError() {}
        fun onDuplicateLoopStartError() {}
        fun onDuplicateLoopStopError() {}
        fun onRecordWithoutLoopingError() {}
        fun onSaveDuringRecordingError() {}
        fun onSaveNoneLayerError() {}
        fun onDropAllLayerDuringRecordingError() {}
        fun onDropAllLayerEmptyError() {}
        fun onStopLoopDuringRecordingError() {}
        fun onLoopStartWithoutLayerError() {}

        fun onFirstRecord() {}

        fun onRecordStart() {}
        fun onRecordStop() {}
        fun onRecordFinish() {}

        fun onLoopStart() {}
        fun onLoopStop() {}

        fun onLayerAdd() {}
        fun onLayerDrop(position: Int, layerLabel: String) {}
        fun onLayerAllDrop() {}
        fun onLayerPlay(position: Int, layerLabel: String) {}
        fun onLayerMute(position: Int, layerLabel: String) {}
        fun onLayerMuteCancel(position: Int, layerLabel: String) {}
        fun onChangeLayerLabel(position: Int, newLayerLabel: String, oldLayerLabel: String) {}
        fun onTitleChanged(newTitle: String, oldTitle: String) {}

        fun onExport(loopTitle: String) {}
        fun onImport(loopTitle: String, newLoadFlag: Boolean) {}
    }
}