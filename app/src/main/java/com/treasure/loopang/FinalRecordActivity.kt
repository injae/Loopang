package com.treasure.loopang

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.constraintlayout.widget.ConstraintSet
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.treasure.loopang.audio.EffectorPresets
import com.treasure.loopang.audio.FinalRecorder
import com.treasure.loopang.audio.Metronome
import com.treasure.loopang.ui.dialogs.BlockControlDialog
import com.treasure.loopang.ui.dialogs.VolumeControlDialog
import com.treasure.loopang.ui.dpToPx
import com.treasure.loopang.ui.recorderConnector
import com.treasure.loopang.ui.util.WidthPerTime
import com.treasure.loopang.ui.view.*
import kotlinx.android.synthetic.main.activity_final_record.*
import kotlinx.android.synthetic.main.activity_final_record.btn_stop
import kotlinx.android.synthetic.main.activity_final_record.metronome_view
import kotlinx.android.synthetic.main.dialog_final_save.*

class FinalRecordActivity : AppCompatActivity() {
    private val metronome : Metronome = Metronome()
    private var seekBarAnimator: ValueAnimator? = null
    private var seekBarAnimationListener = SeekBarAnimatorListener()
    private var tempProgress: Int = 0

    private var buttonLabelList: List<String>? = null
    private var backPressedTime: Long = 0

    private val wpt: WidthPerTime = WidthPerTime(width=50, ms=10)
    val finalRecorderUnit: Int = 10 // ms

    var metronomeFlag: Boolean = false
   /* var recordFlag: Boolean = false
    var playFlag: Boolean = false*/
    val blockLayerViewList: MutableList<BlockLayerView> = mutableListOf()
    val muteButtonList: MutableList<ToggleButton> = mutableListOf()

    var basicWidth = 0
    var basicHeight = 0
    var expandSize = 0

    private val layerBitmapList: ArrayList<Bitmap> = arrayListOf()
    private val blockColorList: ArrayList<Int> by lazy {
        arrayListOf(
            resources.getColor(R.color.block_color1, null),
            resources.getColor(R.color.block_color2, null),
            resources.getColor(R.color.block_color3, null),
            resources.getColor(R.color.block_color4, null)
        )
    }
    // val muteButtonSelector
    // val recordButtonSelector
    // val toolBoxButtonSelector
    // val vocalButtonSelector

    //toolbox
    var playButton: ToggleButton? = null
    var toStartButton: ImageButton? = null
    var toEndButton: ImageButton? = null
    var recordButton: ToggleButton? = null
    var recordStopButton: ImageButton? = null
    var metronomeButton: ToggleButton? = null
    var openVCDButton: ImageButton? = null

    //control panel
    var timeStampTxt: TextView? = null
    var muteButtonLinearLayout: LinearLayout? = null

    //record timeline
    var recordTimelineScrollView: HorizontalScrollView? = null
    var recordSeekBarLine: View? = null
    var recordSeekBarButton: SeekBar? = null
    var layerListLinear: LinearLayout? = null

    //dialog
    private val blockControlDialog: BlockControlDialog by lazy { BlockControlDialog(this, BCDListener()) }
    private val volumeControlDialog: VolumeControlDialog by lazy { VolumeControlDialog(this, VCDListener(),recorderConnector.soundList!!.map{it.data}) }

    private val finalRecorder : FinalRecorder = FinalRecorder()
    private var num = 0

    companion object {
        private const val FINISH_INTERVAL_TIME = 2000
    }

    //activity event
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_final_record)

        Log.d("FRA, recorderConnector","recorderConnector.soundList[size: ${recorderConnector.soundList!!.size}]")

        getSounds()
        bindView()
        initModule()
        initView()
        initAfterInflation()
        openVCDButton!!.bringToFront()
    }

    private fun initModule() {
        metronome.task = { metronome_view.tik() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(){
        if (seekBarAnimator == null) {
            seekBarAnimator = ValueAnimator.ofInt(0, 10)
            seekBarAnimator!!.apply {
                duration = 10L * finalRecorderUnit
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener {
                    val value = it.animatedValue as Int
                    recordSeekBarButton!!.progress = tempProgress + value
                }
                addListener(seekBarAnimationListener)
            }
        }

        recordButton!!.setOnClickListener {
            val btn = it as ToggleButton
            if(finalRecorder.isPlaying() && !finalRecorder.isRecording()) {
                Log.d("FRA, 재생중", "녹음 중 녹음 버튼 조작을 막습니다.")
                btn.isChecked = !btn.isChecked
            } else {
                if (btn.isChecked) {
                    if(finalRecorder.isRecording()) return@setOnClickListener

                    muteButtonList.forEachIndexed { index, toggle ->
                        if (toggle.isChecked){
                            blockLayerViewList[index].addBlock(start = finalRecorder.getRecordPosition(), duration = 0)
                            blockLayerViewList[index].expandBlock()
                        }
                    }
                    recordSeekBarButton!!.isEnabled = false
                    finalRecorder.recordStart()
                    seekBarAnimator!!.start()
                    Thread(updateRecordRunnable).start()
                } else {
                    stopBlock()
                    Log.d("FRA, 녹음중", "녹음종료, stopBlock")
                    finalRecorder.recordStop()
                    Log.d("FRA, 녹음중", "녹음종료, finalRecorder.recordStop")
                    // recordDuration = finalRecorder.getRecordDuration()
                    seekBarAnimator!!.cancel()
                    recordSeekBarButton!!.isEnabled = true
                    refreshView()
                }
            }
        }
        recordStopButton!!.setOnClickListener {
            if(!(finalRecorder.isRecording() || finalRecorder.isPlaying())){
                showSaveDialog()
            }
        }
        playButton!!.setOnClickListener {
            if(finalRecorder.isRecording()) {
                Log.d("FRA, 녹음중", "녹음 중 재생 버튼 조작을 막습니다.")
                (it as ToggleButton).isChecked = !(it.isChecked)
            } else {
                if ((it as ToggleButton).isChecked) {
                    if(finalRecorder.getRecordDuration() == 0 || finalRecorder.isPlaying()) {
                        it.isChecked = !(it.isChecked)
                        return@setOnClickListener
                    }
                    finalRecorder.playStart()
                    seekBarAnimator!!.start()
                    Thread(updatePlayRunnaable).start()

                } else {
                    //todo: 재생 정지시 동작
                    finalRecorder.playStop()
                    seekBarAnimator!!.cancel()
                }
            }
        }

        metronomeButton!!.setOnClickListener {
            metronomeFlag = (it as ToggleButton).isChecked
            if(it.isChecked){
                metronome.excute()
            } else {
                metronome.cancle()
                metronome_view.clear()
            }
        }

        recordSeekBarLine!!.bringToFront()
        recordSeekBarButton!!.bringToFront()
        recordSeekBarButton!!.setOnSeekBarChangeListener( object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // progress line 이 progress button 을 따라다니도록 함.
                seekBar?.let {
                    val leftMargin = (it.width * (progress.toFloat() / it.max)).toInt()
                    ConstraintSet().apply {
                        clone(record_timeline_panel)
                        setMargin(recordSeekBarLine!!.id, ConstraintSet.START, leftMargin)
                    }.applyTo(record_timeline_panel)
                }
                // time stamp 변경
                timeStampTxt!!.text = progress.toString() // String.format("%02d : %02d", ((progress.toFloat() / 1000) % 60).toInt(), ((progress.toFloat() / 1000*60) % 60).toInt() )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let{
                    if(it.progress > finalRecorder.getRecordDuration()) {
                        it.progress = finalRecorder.getRecordPosition()
                    }
                    finalRecorder.seekTo(it.progress)
                }
            }
        })
        recordSeekBarButton!!.setOnTouchListener { v, event ->
            when (event.action){
                MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            v.onTouchEvent(event)
            true
        }

        toStartButton!!.setOnClickListener{
            if(!finalRecorder.isRecording() && !finalRecorder.isPlaying()){
                recordSeekBarButton!!.progress = 0
                finalRecorder.seekToStart()
                recordTimelineScrollView?.smoothScrollTo(0,0)
                Log.d("FRA, 녹음중 혹은 재생중", "toStart 버튼 클릭")
            } else {
                Log.d("FRA, 녹음중 혹은 재생중", "녹음 혹은 재생 중 toStart 버튼 조작을 막습니다.")
            }
        }
        toEndButton!!.setOnClickListener{
            if(!finalRecorder.isRecording() && !finalRecorder.isPlaying()){
                recordSeekBarButton!!.progress = finalRecorder.getRecordDuration()
                recordTimelineScrollView?.smoothScrollTo((finalRecorder.getRecordDuration()*wpt.width/10 - (recordTimelineScrollView!!.width / 2f)).toInt(),0)
                finalRecorder.seekToEnd()
                Log.d("FRA, 녹음중 혹은 재생중", "toEnd 버튼 클릭")
            } else {
                Log.d("FRA, 녹음중 혹은 재생중", "녹음 혹은 재생 중 toEnd 버튼 조작을 막습니다.")
            }
        }
        openVCDButton!!.setOnClickListener { showVolumeControlDialog() }
    }


    private fun initAfterInflation(){
        recordTimelineScrollView?.viewTreeObserver?.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                recordTimelineScrollView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)

                //레이어뷰 기본 너비/높이 지정.
                val temp = record_timeline_panel_scroll.width
                basicWidth = temp - (temp % wpt.width) + wpt.width
                basicHeight = dpToPx(this@FinalRecordActivity, 56f).toInt()
                expandSize = basicWidth

                //레이어를 담는 리니어 레이아웃 너비 초기화
                val param = layerListLinear!!.layoutParams as FrameLayout.LayoutParams
                param.width = basicWidth
                layerListLinear?.layoutParams = param

                //레이어뷰, 뮤트버튼 리스트에 초기화.
                initLayerList(num)

                //전체 시크바 MAX 초기화.
                recordSeekBarButton?.max = ((basicWidth.toFloat() / wpt.width) * wpt.ms).toInt()

                setupScrolling()

                //다이얼로그 크기 동적지정
                val display = windowManager.defaultDisplay
                val size = Point()
                val volumeControlWindow = volumeControlDialog.window
                val blockControlWindow = blockControlDialog.window

                display.getSize(size)
                volumeControlWindow?.setLayout((size.x * 0.25).toInt(), size.y)
                blockControlWindow?.setLayout((size.x * 0.25).toInt(), size.y)
            }
        })
    }

    private fun initLayerList(num: Int) {
        for(x in 0 until num){
            val blockLayerView = BlockLayerView(this@FinalRecordActivity)
            val buttonText = buttonLabelList!![x]
            val muteButton = MuteButtonBuilder(this)
                .basicHeight(basicHeight)
                .label(buttonText)
                .onMuteEvent {
                    blockLayerViewList[x].mute(true, finalRecorder.isRecording(), finalRecorder.getRecordPosition())
                    finalRecorder.setMute(x, true)
                }
                .onUnMuteEvnet {
                    blockLayerViewList[x].mute(false, finalRecorder.isRecording(), finalRecorder.getRecordPosition())
                    finalRecorder.setMute(x, false)
                }
                .topMargin(10)
                .build()

            //blockLayerView 초기화
            blockLayerView.layerId = x
            blockLayerView.wpt = wpt
            blockLayerView.blockColor = blockColorList[x%blockColorList.size]
            layerListLinear!!.addView(blockLayerView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, basicHeight))
            blockLayerView.cornerRadius = 20
            blockLayerView.roundedCorners = BlockLayerView.CORNER_ALL
            blockLayerView.top = 10
            blockLayerViewList.add(blockLayerView)

            //muteButton 초기화
            mute_button_linear.addView(muteButton)
            muteButtonList.add(muteButton)
            // muteButton.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, basicHeight)
        }
    }

    private fun bindView() {
        playButton = btn_play
        toStartButton = btn_to_start
        toEndButton = btn_to_end
        recordButton = btn_record
        recordStopButton = btn_stop
        metronomeButton = btn_metronome
        recordSeekBarButton = whole_progress_seekbar
        recordTimelineScrollView = record_timeline_panel_scroll
        timeStampTxt = txt_record_timestamp
        muteButtonLinearLayout = mute_button_linear
        layerListLinear = layer_list_linear
        recordSeekBarLine = record_progress_line
        openVCDButton = btn_open_volume_cotrol
    }

    private fun refreshView(){
        // todo: 여기에 블록들을 가시화하는 코드를 작성.
        val blockList = finalRecorder.getBlockList()
        clear()

        Log.d("RRA, 녹음중", "refreshView(), blockList.size: ${blockList.size}")
        blockList.forEachIndexed { li, list ->
            list.forEachIndexed { bi, soundRange ->
                val start = soundRange.startDuration()
                val duration = soundRange.endDuration() - start
                blockLayerViewList[li].addBlock(bi,start,duration, BCListener())
                Log.d("RRA, 녹음중", "refreshView(), 블록 리스트에서 가져오기, li: $li, bi: $bi, $start: start, duration: $duration")
            }
        }

        Log.d("FRA, 타임라인컨트롤", "refreshView()")
    }

    private fun setupScrolling() {
        val scrollManager = ScrollManager()

        scrollManager.addScrollClient(mute_button_scroll as ScrollNotifier)
        scrollManager.addScrollClient(layer_list_scroll as ScrollNotifier)

    }

    private fun stopBlock(){
        blockLayerViewList.forEach{
            it.stopExpandBlock()
        }
    }

    private fun clear(){
        blockLayerViewList.forEach {
            it.clear()
        }
        Log.d("FRA, 타임라인컨트롤", "clear()")
    }

    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            finish()
        } else {
            backPressedTime = tempTime
            Toast.makeText(this, "One More pressed, Final Record will be closed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun expandLayerLinear() {
        val param = layerListLinear!!.layoutParams as FrameLayout.LayoutParams
        param.width = param.width + basicWidth
        runOnUiThread{
            layerListLinear!!.layoutParams = param
        }
        Log.d("FRA, 타임라인컨트롤", "expandLayerLinear(new width: ${param.width}, basicwidth: $basicWidth)")
    }

    private fun expandRecordSeekMax() {
        val prev = recordSeekBarButton!!.max
        recordSeekBarButton!!.max = (prev + ((basicWidth.toFloat() / wpt.width) * wpt.ms)).toInt()
        Log.d("FRA, 타임라인컨트롤", "expandRecordSeekMax(prev: $prev, new max: ${recordSeekBarButton!!.max})")
    }

    private fun checkToExpandSize(): Boolean {
        if(expandSize - ((recordSeekBarButton!!.progress.toFloat() / wpt.ms) * wpt.width) <= basicWidth * 0.2) {
            if(recordSeekBarButton!!.width + basicWidth > expandSize){
                expandSize += basicWidth
                return true
            }
        }
        return false
    }

    private fun showBlockControlDialog(layerId:Int, blockId: Int) {
        if(layerId == 0) return
        blockControlDialog.show(layerId, blockId, 50, finalRecorder.getEffectFlag(layerId))
        Log.d("FRA, 블록컨트롤", "showBlockControlDialog(layerId: $layerId, blockId: $blockId)")
    }

    private fun showVolumeControlDialog() {
        volumeControlDialog.show()
        Log.d("FRA, 볼륨컨트롤", "showVolumeControlDialog")
    }

    private fun showSaveDialog() {
        MaterialDialog(this).show {
            title(R.string.title_save)
            noAutoDismiss()
            cornerRadius(16f)
            cancelable(false)
            customView(R.layout.dialog_final_save, horizontalPadding = true)
            positiveButton(R.string.btn_save) {
                // callback on positive button click
                val title = this.edit_music_title.text.toString()
                val soundFormat: String  = this.spinner_sound_format_type.selectedItem.toString()

                finalRecorder.export(title, soundFormat)
                it.dismiss()
            }
            negativeButton(R.string.btn_cancel) {
                it.dismiss()
            }

            lifecycleOwner(this@FinalRecordActivity)
        }

    }

    private fun getSounds() {
        // 아무 레이어도 없을 때 리턴.
        if(recorderConnector.soundList!!.isEmpty()) {
            finish()
        }

        finalRecorder.insertSounds(recorderConnector.soundList!!)
        buttonLabelList = listOf("Vocal") + recorderConnector.labelList!!
        num = recorderConnector.soundList!!.size + 1
        metronome.bpm = recorderConnector.bpm
        metronome_view.bpm =  metronome.bpm.toInt()
        /*recorderConnector.soundList!!.forEach {
            WaveformBitmapMaker.
        }*/
    }

    //update view
    private val updateRecordRunnable: Runnable = Runnable {

        while(finalRecorder.isRecording()) {
            if(checkToExpandSize()) {
                expandRecordSeekMax()
                expandLayerLinear()
                Log.d("FRA, 녹음중", "리니어레이아웃과 시크바 맥스를 EXPAND 합니다.")
            }
            // recordTimelineScrollView?.smoothScrollTo((finalRecorder.getRecordPosition()*wpt.width/10 - (recordTimelineScrollView!!.width / 2f)).toInt(),0)

            Log.d("FRA, 녹음중", "recordFlag: ${finalRecorder.isRecording()}, recordCurrentPosition.ms : ${finalRecorder.getRecordPosition()}")

            SystemClock.sleep(50)
        }
    }

   private val updatePlayRunnaable: Runnable = Runnable {
        while(finalRecorder.isPlaying()) {
            // recordCurrentPosition = finalRecorder.getRecordPosition()
            //recordSeekBarButton!!.progress = finalRecorder.getRecordPosition()
            // playFlag = finalRecorder.isPlaying()
           //  recordTimelineScrollView?.smoothScrollTo((finalRecorder.getRecordPosition()*wpt.width/10 - (recordTimelineScrollView!!.width / 2f)).toInt(),0)
           /* if(finalRecorder.getRecordPosition() >= finalRecorder.getRecordDuration()){
                finalRecorder.playStop()
                playFlag = false
                break
            }*/

            Log.d("FRA, 재생중", "playFlag: ${finalRecorder.isRecording()}, recordCurrentPosition.ms : ${finalRecorder.getRecordPosition()}")

            SystemClock.sleep(50)
        }
       runOnUiThread{
           seekBarAnimator!!.cancel()
           playButton!!.isChecked = !playButton!!.isChecked
       }
    }


    //listener
    inner class BCDListener : BlockControlDialog.BlockControlListener {
        override fun onVolumeChanged(progress: Int, max: Int, layerId: Int, blockId: Int) {
            if(finalRecorder.isRecording() || finalRecorder.isPlaying()) {
                Log.d("FRA, BlockControl", "BCDListener.onVolumeChanged(재생 혹은 녹음 중이어서 이벤트를 받지 않음.)")
            } else {
                Log.d("FRA, BlockControl", "BCDListener.onVolumeChanged(progress: $progress, max: $max, layerId: $layerId, blockId: $blockId)")
                finalRecorder.setVolumeToLayer(layerId, ((progress.toFloat() / max) * 100).toInt())
            }
        }

        override fun onEffectChanged(effect: EffectorPresets, layerId: Int, blockId: Int) {
            if(finalRecorder.isRecording() || finalRecorder.isPlaying()) {
                Log.d("FRA, BlockControl", "BCDListener.onEffectChanged(재생 혹은 녹음 중이어서 이벤트를 받지 않음.)")
            } else {
                Log.d("FRA, BlockControl", "BCDListener.onEffectChanged(effect: $effect.name, layerId: $layerId, blockId: $blockId)")
                finalRecorder.setEffectToBlock(layerId, blockId, effect)
            }
        }
    }

    inner class BCListener: BlockView.BlockControlListener {
        override fun onClickListener(layerId: Int, blockId: Int) {
            if (finalRecorder.isRecording() || finalRecorder.isPlaying()) {
                Log.d("FRA, BlockControl", "BCListener.onClickListener(블록이 재생중 혹은 녹음 중이어서 이벤트를 받지 않습니다.)")
            } else {
                showBlockControlDialog(layerId, blockId)
                Log.d("FRA, BlockControl", "BCListener.onClickListener(layerId: $layerId, blockId: $blockId)")
            }
        }
    }

    inner class VCDListener: VolumeControlDialog.VolumeControlDialogListener {
        override fun onRecordVolumeChanged(progress: Int, max: Int) {
            // TODO("볼륨 컨트롤 추가") //To change body of created functions use File | Settings | File Templates.
            Log.d("FRA, VolumeControl", "VCDListener.레코드볼륨(progress: $progress, max: $max)")
        }

        override fun onLoopVolumeChanged(progress: Int, max: Int) {
            finalRecorder.setVolumeToLoop(((progress.toFloat() / max) * 100).toInt())
            Log.d("FRA, VolumeControl", "VCDListener.루프볼륨(progress: $progress, max: $max)")
        }

    }

    inner class SeekBarAnimatorListener: Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
            tempProgress += wpt.ms
        }

        override fun onAnimationEnd(animation: Animator?) {}

        override fun onAnimationCancel(animation: Animator?) {
            var position
            
            if(finalRecorder.isRecording()) {
                position = finalRecorder.getRecordPosition()
            } else if(finalRecorder.isPlaying()){
                position = finalRecorder.getLoopPosition()
            } else return
            
            recordSeekBarButton!!.progress = position

            Log.d("animation", "onAnimationCancel, position: $position")
        }

        override fun onAnimationStart(animation: Animator?) {
            tempProgress = recordSeekBarButton!!.progress
        }

    }

}

class MuteButtonBuilder(val context: Context) {
    private var label: String = ""
    private var isChecked = true
    private var onMuteEvent: ()->Unit = {}
    private var onUnMuteEvent: ()->Unit = {}
    private var basicHeight: Int = 0
    private var topMargin: Int = 0

    fun label(label: String): MuteButtonBuilder {
        this.label = label
        return this
    }
    fun onMuteEvent(event: ()->Unit): MuteButtonBuilder {
        this.onMuteEvent = event
        return this
    }
    fun onUnMuteEvnet(event: ()->Unit): MuteButtonBuilder {
        this.onUnMuteEvent = event
        return this
    }
    fun basicHeight(h: Int): MuteButtonBuilder {
        this.basicHeight = h
        return this
    }
    fun topMargin(px: Int): MuteButtonBuilder {
        this.topMargin = px
        return this
    }
    fun build(): ToggleButton {
        return ToggleButton(context).apply{
            setBackgroundResource(R.drawable.mute_toggle_selector)
            textOn = label
            textOff = label
            text = label
            isChecked = this@MuteButtonBuilder.isChecked
            setOnClickListener{
                if (this.isChecked) onUnMuteEvent()
                else onMuteEvent()
            }
            Log.d("muteButtonBuild", "lable: $label, height: $height")
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, basicHeight)
            top = this@MuteButtonBuilder.topMargin
            setTextColor(resources.getColorStateList(R.color.mute_toggle_text_selector, null))
        }
    }
}
