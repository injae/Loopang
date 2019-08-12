package com.treasure.loopang.audio

import android.os.Handler
import android.util.Log
import android.view.View
import com.treasure.loopang.ui.view.RealtimeVisualizerView
import kotlin.math.abs

class LoopStation {
    private var linkedVisualizer: RealtimeVisualizerView? = null
    private val mMixer: Mixer = Mixer()
    private val mRecorder: Recorder = Recorder()

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
        if(isRecording() || !isLooping()) return
        if(isFirstRecord()){
            if(firstRecordMessageFlag) mLoopStationMessageListener?.onFirstRecord()
            firstRecordFlag = mLoopStationEventListener?.onFirstRecord() ?: true
        }
        else{
            if(mMixer.sounds.isNotEmpty()) { mRecorder.start(mMixer.sounds[0].data.size) }
            else { mRecorder.start() }
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
                 loopStartMessageFlag: Boolean = false,
                 addLayerMessageFlag: Boolean = true) {
        mMixer.addSound(sound)
        mLayerLabelList.add(DEFAULT_LAYER_LABEL + mLastLayerNum++)
        if (!isLooping()){
            loopStart(loopStartMessageFlag)
        }
        if(addLayerMessageFlag) {
            mLoopStationMessageListener?.onAddLayer()
        }
    }

    fun muteLayer(position: Int,
                  messageFlag: Boolean = true) {
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
        loopStop(messageFlag = false)
        mMixer.sounds.clear()
        mLayerLabelList.clear()
        mLastLayerNum = 1
        mLoopStationEventListener?.onLayerAllDrop()
        if(messageFlag) mLoopStationMessageListener?.onLayerAllDrop()
    }

    fun stopAll() {
        recordStop(messageFlag = false)
        loopStop(messageFlag = false)
        stopPlayingLayer(messageFlag = false)
    }

    fun import(project: LoopMusic, mixFlag: Boolean = false){

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

        if (mixFlag){
            val fileLabel = "/$loopTitle.${fileType.toLowerCase()}"
            Sound(mMixer.mixSounds()).save(mDirectoryPath+fileLabel)
        } else {
            val children = (1..mMixer.sounds.size).map { LoopMusic("$it") }
            mMixer.save(LoopMusic(name=loopTitle,type= fileType.toLowerCase(), child=children))
        }

        if(allDropFlag) dropAllLayer(messageFlag = false)

        return SAVE_SUCCESS
    }

    fun checkWhiteNoise(): Boolean {
        return true
    }

    fun isRecording(): Boolean = mRecorder.isRecording.get()
    fun isLooping(): Boolean = mMixer.isLooping.get()
    fun isFirstRecord(): Boolean = firstRecordFlag
    fun isEmpty(): Boolean = mMixer.sounds.isEmpty()
    fun isNotLoopingAndEmpty(): Boolean = (!(this.isLooping()) && this.isEmpty())

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


    abstract inner class LoopStationEventListener {
        open fun onRecordStart() {}
        open fun onRecordStop() {}
        open fun onRecordFinish() {}
        open fun onLoopStart() {}
        open fun onLoopStop() {}
        open fun onLoopFinish() {}
        open fun onAddLayer() {}
        open fun onLayerDrop(position: Int) {}
        open fun onLayerAllDrop() {}
        open fun onLayerPlay(position: Int) {}
        open fun onLayerMute(position: Int) {}
        open fun onLayerMuteCancel(position: Int) {}
        open fun onFirstRecord(): Boolean = false
    }

    abstract inner class LoopStationMessageListener {
        open fun onDuplicateRecordStartError() {}
        open fun onDuplicateRecordStopError() {}
        open fun onDuplicateLoopStartError() {}
        open fun onDuplicateLoopStopError() {}
        open fun onRecordWithoutLoopingError() {}
        open fun onSaveDuringRecordingError() {}
        open fun onSaveNoneLayerError() {}

        open fun onFirstRecord() {}

        open fun onRecordStart() {}
        open fun onRecordStop() {}
        open fun onRecordFinish() {}

        open fun onLoopStart() {}
        open fun onLoopStop() {}

        open fun onAddLayer() {}
        open fun onLayerDrop(position: Int, layerLabel: String) {}
        open fun onLayerAllDrop() {}
        open fun onLayerPlay(position: Int, layerLabel: String) {}
        open fun onLayerMute(position: Int, layerLabel: String) {}
        open fun onLayerMuteCancel(position: Int, layerLabel: String) {}

        open fun onExport(loopTitle: String) {}
        open fun onImport(loopTitle: String) {}
    }
}