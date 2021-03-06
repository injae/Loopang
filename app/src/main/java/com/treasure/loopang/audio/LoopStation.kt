package com.treasure.loopang.audio

import android.os.Handler
import android.util.Log
import android.view.View
import com.treasure.loopang.ui.view.RealtimeVisualizerView
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs

class LoopStation {
    private val mDurationCalculator: DurationCalculator = DurationCalculator()
    private var linkedVisualizer: RealtimeVisualizerView? = null
    private val mMixer: Mixer = Mixer()
    private var mRecorder: Recorder? = null
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
    private val positionMutex: Mutex = Mutex(false)
    val positionContext: CoroutineContext = newSingleThreadContext("PositionContext")

    var duration: Int = 1   // mSeconds
        private set

    var position: Int = 0
        private set

    private var readDataSize: AtomicInteger = AtomicInteger(0)

    var loopMusic: LoopMusic? = null

    companion object{
        const val DEFAULT_LAYER_LABEL = "Layer "
        const val SAVE_SUCCESS = 0
        const val SAVE_ERROR_DUPLICATE_NAME = 2
        const val SAVE_ERROR_NONE_LAYER = 3
        private const val LIMIT_OF_DIFFERENCE_READ_DATA_SIZE = 100
    }

    init {
        initRecorder()
        initMixer()
    }

    fun setLoopStationEventListener(loopStationEventListener: LoopStationEventListener) {
        mLoopStationEventListener = loopStationEventListener
    }

    fun setLoopStationMessageListener(loopStationMessageListener: LoopStationMessageListener){
        mLoopStationMessageListener = loopStationMessageListener
    }

    fun linkVisualizer(visualizer: RealtimeVisualizerView) {
        this.linkedVisualizer = visualizer
    }

    fun initRecorder(){
        if(mRecorder == null) {
            mRecorder = Recorder()
        }
        mRecorder!!.apply{
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

    fun releaseRecorder() {
        if (mRecorder == null) return
        mRecorder!!.release()
        mRecorder = null
    }

    private fun initMixer(){
        mMixer.onSuccess {
            readDataSize.set(0)
            position = 0
            mLoopStationEventListener?.onPositionChanged(0)
        }
    }

    fun recordStart(firstRecordMessageFlag: Boolean = true) {
        /*if(isFirstRecord()){
            if(firstRecordMessageFlag) mLoopStationMessageListener?.onFirstRecord()
            mLoopStationEventListener?.onFirstRecord()
            firstRecordFlag = false
        } else if (!whiteNoiseCheckFlag) {
            mLoopStationEventListener?.onFirstRecord()
        } else{*/
            when {
                /*(!isLooping() && !isEmpty()) -> {
                    mLoopStationMessageListener?.onRecordWithoutLoopingError()
                    return
                }*/

                isRecording() -> {
                    mLoopStationMessageListener?.onDuplicateRecordStartError()
                    return
                }

                mMixer.sounds.isNotEmpty() -> mRecorder!!.start(mMixer.sounds[0].data.size)
                else -> mRecorder!!.start()
            }
            showVisualizer()
            mLoopStationEventListener?.onRecordStart()
            mLoopStationMessageListener?.onRecordStart()
        //}
    }

    fun recordStop(messageFlag: Boolean = true) {
        if(!isRecording()) {
            if(messageFlag) mLoopStationMessageListener?.onDuplicateRecordStopError()
            return
        }
        mRecorder!!.stop()
        mLoopStationEventListener?.onRecordStop()
        if(messageFlag) mLoopStationMessageListener?.onRecordStop()
    }

    fun loopStart(messageFlag: Boolean = true) {
        if(mMixer.sounds.size == 0) return
        if(isLooping()) {
            mLoopStationMessageListener?.onDuplicateLoopStartError()
            return
        }
        readDataSize.set(0)
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
                 autoLoopStartFlag: Boolean = false,
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
        if(mMixer.sounds.size == 1) {
            mDurationCalculator.sampleRate = sound.format.info().sampleRate
            duration =  mDurationCalculator.calculate(sound.data.size, DurationCalculator.BYTE)
            sound.addEffector {
                    updateReadDataSize(it.size)
                    it
            }
            mLoopStationEventListener?.onFirstLayerSaved(sound, layerLabel, duration)
        }
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
        loopMusic = null
        if(messageFlag) mLoopStationMessageListener?.onLayerAllDrop()
    }

    fun changeLayerLabel(position: Int,
                         layerLabel: String,
                         messageFlag: Boolean = false): Boolean {
        if(layerLabel == "") return false

        var isSuccess = false
        val oldLayerLabel = mLayerLabelList[position]
        if(layerLabel == oldLayerLabel) return isSuccess
        else if(checkLayerLabelDuplication(layerLabel)) return isSuccess

        isSuccess = true
        mLayerLabelList[position] = layerLabel
        mLoopStationEventListener?.onChangeLayerLabel(position, layerLabel, oldLayerLabel)
        if(messageFlag) mLoopStationMessageListener?.onChangeLayerLabel(position, layerLabel, oldLayerLabel)

        return isSuccess
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
            if(project.child != null) {
                mLoopTitle = project.name
                loopMusic = project
                mLoopStationEventListener?.onTitleChanged(mLoopTitle, "")
            }
            dropAllLayer(messageFlag = false)
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

    fun export(newFlag: Boolean,
               saveType: String ="Project",
               title: String = loopMusic?.name ?: "",
               fileType: String = loopMusic?.type ?: ".wav",
               splitFlag: Boolean = false,
               clearFlag: Boolean = false,
               overwriteFlag: Boolean = false ): Int{
        when(saveType){
            "Loop" -> {
                val baseFileName = "${title}_loop"
                val fileLabelList = mutableListOf<String>()
                if(splitFlag){
                    mLayerLabelList.map{ fileLabelList.add("${baseFileName}_$it.${fileType.toLowerCase()}") }
                } else fileLabelList.add("$baseFileName.${fileType.toLowerCase()}")

                if(!overwriteFlag)
                    fileLabelList.forEach { if(mFileManager.checkSoundDuplication(it)){ return SAVE_ERROR_DUPLICATE_NAME } }

                if(splitFlag){
                    mMixer.sounds.forEachIndexed { index, mixerSound -> mixerSound.save(mDirectoryPath + "/${fileLabelList[index]}") }
                } else {
                    Sound(mMixer.mixSounds()).save(mDirectoryPath+"/${fileLabelList[0]}") }
            }

            "Project" -> {
                val children = mLayerLabelList.map{ LoopMusic(it) }
                val tempLoopMusic = LoopMusic(name=title,type=fileType.toLowerCase(), child=children)
                if(!newFlag) { loopMusic?.let{ mFileManager.clearExistingProject(it) } }
                if(!overwriteFlag)
                    if(mFileManager.checkProjectDuplication(tempLoopMusic)) { return SAVE_ERROR_DUPLICATE_NAME }
                Log.d("SaveTest","Mixer Sound 수: ${mMixer.sounds.size}, config child 수: ${tempLoopMusic.child?.size ?: 0} ")
                mMixer.save(tempLoopMusic)
                loopMusic = tempLoopMusic
            }
        }
        if(clearFlag) dropAllLayer(false)
        return SAVE_SUCCESS
    }

    fun checkWhiteNoise(): Boolean {
        whiteNoiseCheckFlag = true
        return true
    }

    fun isRecording(): Boolean = mRecorder!!.isRecording.get()
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

    private fun updateReadDataSize(size: Int) {
        /*var temp = size
        while(temp < LIMIT_OF_DIFFERENCE_READ_DATA_SIZE) {
            temp -= LIMIT_OF_DIFFERENCE_READ_DATA_SIZE
            position.set(mDurationCalculator.calculate(readDataSize.addAndGet(LIMIT_OF_DIFFERENCE_READ_DATA_SIZE), DurationCalculator.BYTE))
            Log.d("LoopStation", "update readDataSize: $readDataSize")
        }
        if(temp != 0) {
            readDataSize.getAndAdd(temp)
            position.set(mDurationCalculator.calculate(readDataSize.addAndGet(LIMIT_OF_DIFFERENCE_READ_DATA_SIZE), DurationCalculator.BYTE))
        }*/
        /*CoroutineScope(Dispatchers.Default).launch {
            positionMutex.withLock{
                position = (mDurationCalculator.calculate(readDataSize.addAndGet(size), DurationCalculator.BYTE))
            }
        }*/
        /*CoroutineScope(positionContext).run {
            position = (mDurationCalculator.calculate(readDataSize.addAndGet(size), DurationCalculator.BYTE))
        }*/
        var temp = size
        while(temp < LIMIT_OF_DIFFERENCE_READ_DATA_SIZE) {
            temp -= LIMIT_OF_DIFFERENCE_READ_DATA_SIZE
            position = (mDurationCalculator.calculate(readDataSize.addAndGet(size), DurationCalculator.BYTE))
            mLoopStationEventListener?.onPositionChanged(position)
            // Log.d("LoopStation", "update readDataSize: $readDataSize")
        }
    }

    fun getMixer() : Mixer = mMixer
    fun getRecorder(): Recorder = mRecorder!!
    fun getSounds() : MutableList<MixerSound> = mMixer.sounds
    fun getLayerLabels() : MutableList<String> = mLayerLabelList
    fun hasSingleLayer(): Boolean = (mMixer.sounds.size == 1)

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
        fun onFirstLayerSaved(
            sound: Sound,
            layerLabel: String,
            duration: Int
        ) {}

        fun onExport(loopTitle: String) {}
        fun onImport(loopTitle: String, newLoadFlag: Boolean) {}
        fun onPositionChanged(position: Int) {}
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