package com.treasure.loopang

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.constraintlayout.widget.ConstraintSet
import com.treasure.loopang.audio.EffectorPresets
import com.treasure.loopang.ui.dialogs.BlockControlDialog
import com.treasure.loopang.ui.dialogs.VolumeControlDialog
import com.treasure.loopang.ui.dpToPx
import com.treasure.loopang.ui.toast
import com.treasure.loopang.ui.util.TimeWrapper
import com.treasure.loopang.ui.util.WidthPerTime
import com.treasure.loopang.ui.view.BlockLayerView
import com.treasure.loopang.ui.view.BlockView
import com.treasure.loopang.ui.view.VerticalTextButton
import kotlinx.android.synthetic.main.activity_final_record.*

class FinalRecordActivity : AppCompatActivity() {
    private val wpt: WidthPerTime = WidthPerTime(width=5, ms=100)
    val recordDuration: TimeWrapper = TimeWrapper()
    val recordCurrentPosition: TimeWrapper = TimeWrapper()
    val loopDuration: TimeWrapper = TimeWrapper()
    val loopCurrentPosition: TimeWrapper = TimeWrapper()
    val startTimeWrapper: TimeWrapper = TimeWrapper()
    // val timeStringFormat
    var overwriteFlag: Boolean = false
    var metronomeFlag: Boolean = false
    var recordFlag: Boolean = false
    var playFlag: Boolean = false
    val blockLayerViewList: MutableList<BlockLayerView> = mutableListOf()
    val muteButtonList: MutableList<ToggleButton> = mutableListOf()

    var basicWidth = 0
    var basicHeight = 0

    var loopCanvas: Canvas? = null
    var loopDrawable: Drawable? = null
    private val layerCanvasList: ArrayList<Canvas?> = arrayListOf()
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
    var loopProgressBar: ProgressBar? = null
    var recordButton: ToggleButton? = null
    var recordStopButton: ImageButton? = null
    var overwriteButton: ToggleButton? = null
    var metronomeButton: ToggleButton? = null
    var openVCDButton: VerticalTextButton? = null

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
    private val volumeControlDialog: VolumeControlDialog by lazy { VolumeControlDialog(this, VCDListener()) }
    // val blockControlDialog: BlockControlDialog = BlockControlDialog()
    // val saveDialog: FinalSaveDialog = FinalSaveDialog()

    private var expandFlag = false

    //activity event
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_final_record)

        bindView()
        initModule()
        initView()
        initAfterInflation()
    }

    private fun initModule() {}

    private fun initView(){
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val blockControlWindow = blockControlDialog.window
        val volumeControlWindow = volumeControlDialog.window

        blockControlWindow?.setLayout((size.x * 0.25).toInt(), size.y)
        volumeControlWindow?.setLayout((size.x * 0.25).toInt(), size.y)

        recordButton!!.setOnClickListener {
            val btn = it as ToggleButton
            if(playFlag && !recordFlag) {
                Log.d("FRA, 재생중", "녹음 중 녹음 버튼 조작을 막습니다.")
                btn.isChecked = !btn.isChecked
            }
            if (btn.isChecked) {
                recordFlag = true
                muteButtonList.forEachIndexed { index, toggle ->
                    if (toggle.isChecked){
                        blockLayerViewList[index].addBlock(start = recordCurrentPosition.ms, duration = 0)
                        blockLayerViewList[index].expandBlock()
                    }
                }
            } else {
                recordFlag = false
                stopBlock()
                refreshView()
            }
        }
        recordStopButton!!.setOnClickListener {}

        toStartButton!!.setOnClickListener { recordSeekBarButton!!.progress = 0 }
        toEndButton!!.setOnClickListener { recordSeekBarButton!!.progress = wpt.getWidth(500) * recordDuration.hs}
        playButton!!.setOnClickListener {
            if(recordFlag) {
                Log.d("FRA, 녹음중", "녹음 중 재생 버튼 조작을 막습니다.")
                (it as ToggleButton).isChecked = !(it.isChecked)
            } else {
                if ((it as ToggleButton).isChecked) {
                    playFlag = true
                    //todo: 재생시 동작
                    clear()
                } else {
                    playFlag = false
                    //todo: 재생 정지시 동작
                }
            }
        }

        overwriteButton!!.setOnClickListener { overwriteFlag = (it as ToggleButton).isChecked }
        metronomeButton!!.setOnClickListener { metronomeFlag = (it as ToggleButton).isChecked}
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
                timeStampTxt!!.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        toStartButton!!.setOnClickListener{
            if(!recordFlag && !playFlag){
                recordSeekBarButton!!.progress = 0
                Log.d("FRA, 녹음중 혹은 재생중", "toStart 버튼 클릭")
            } else {
                Log.d("FRA, 녹음중 혹은 재생중", "녹음 혹은 재생 중 toStart 버튼 조작을 막습니다.")
            }
        }
        toEndButton!!.setOnClickListener{
            if(!recordFlag && !playFlag){
                recordSeekBarButton!!.progress = recordDuration.ms
                Log.d("FRA, 녹음중 혹은 재생중", "toEnd 버튼 클릭")
            } else {
                Log.d("FRA, 녹음중 혹은 재생중", "녹음 혹은 재생 중 toEnd 버튼 조작을 막습니다.")
            }
        }
        openVCDButton!!.setOnClickListener { volumeControlDialog.show() }
    }

    private fun initAfterInflation(){
        recordTimelineScrollView?.viewTreeObserver?.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                recordTimelineScrollView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)

                //레이어뷰 기본 너비/높이 지정.
                val temp = record_timeline_panel_scroll.width
                basicWidth = temp - (temp % wpt.width) + wpt.width
                basicHeight = dpToPx(this@FinalRecordActivity, 56f).toInt()

                //레이어를 담는 리니어 레이아웃 너비 초기화
                val param = layerListLinear!!.layoutParams as FrameLayout.LayoutParams
                param.width = basicWidth
                layerListLinear?.layoutParams = param

                //레이어뷰, 뮤트버튼 리스트에 초기화.
                initLayerList(5)

                //전체 시크바 MAX 초기화.
                recordSeekBarButton?.max = (basicWidth / wpt.width) * wpt.ms
            }
        })
    }

    private fun initLayerList(num: Int) {
        for(x in 0 until num){
            val blockLayerView = BlockLayerView(this@FinalRecordActivity)
            val buttonText = "button$x"
            val muteButton = MuteButtonBuilder(this)
                .basicHeight(basicHeight)
                .label(buttonText)
                .onMuteEvent { blockLayerViewList[x].mute(true, recordFlag, recordCurrentPosition.ms) }
                .onUnMuteEvnet { blockLayerViewList[x].mute(false, recordFlag, recordCurrentPosition.ms) }
                .build()

            //blockLayerView 초기화
            blockLayerView.layerId = x
            blockLayerView.wpt = wpt
            blockLayerView.blockColor = blockColorList[x%blockColorList.size]
            layerListLinear!!.addView(blockLayerView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, basicHeight))
            blockLayerViewList.add(blockLayerView)

            //muteButton 초기화
            mute_button_linear.addView(muteButton)
            muteButtonList.add(muteButton)
            // muteButton.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, basicHeight)

            //그림초기화
            layerCanvasList.add(null)
        }
    }

    private fun bindView() {
        playButton = btn_play
        toStartButton = btn_to_start
        toEndButton = btn_to_end
        recordButton = btn_record
        recordStopButton = btn_stop
        loopProgressBar = progress_loop
        overwriteButton = btn_overwrite
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
        blockLayerViewList[0].addBlock(0, 1000,1000)
        blockLayerViewList[2].addBlock(0, 1500,2000)
        blockLayerViewList[3].addBlock(0, 4000, 1200, BCListener())

        Log.d("FRA, 타임라인컨트롤", "refreshView()")
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

    private fun expandLayerLinear() {
        val param = layerListLinear!!.layoutParams as LinearLayout.LayoutParams
        param.width = param.width + basicWidth
        layerListLinear!!.layoutParams = param
        Log.d("FRA, 타임라인컨트롤", "expandLayerLinear(new width: ${param.width}, basicwidth: $basicWidth)")
    }

    private fun expandRecordSeekMax() {
        val prev = recordSeekBarButton!!.max
        recordSeekBarButton!!.max = prev + ((basicWidth / wpt.width) * wpt.ms)
        Log.d("FRA, 타임라인컨트롤", "expandRecordSeekMax(prev: $prev, new max: ${recordSeekBarButton!!.max})")
    }

    private fun showBlockControlDialog(layerId:Int, blockId: Int) {
        blockControlDialog.show(layerId, blockId)
        Log.d("FRA, 블록컨트롤", "showBlockControlDialog(layerId: $layerId, blockId: $blockId)")
    }

    private fun showRecordControlDialog() {
        blockControlDialog.show()
        Log.d("FRA, 볼류컨트롤", "showRecordControlDialog")
    }


    //open view
    private fun openVolumeControlDrawer() {}
    private fun openBlockControlDrawer() {}
    private fun openSaveDialog() {}

    //event callback
    //private fun onSeekBarMove() {}
    private fun onPlayButtonClick() {}
    private fun onToStartButtonClick() {}
    private fun onToEndButtonClick() {}
    private fun onRecordStartButtonClick() {}
    private fun onRecordPauseButtonClick() {}
    private fun onRecordStopButtonClick() {}
    private fun onOverwriteButtonClick(flag: Boolean) {}
    private fun onMetronomeButtonClick(flag: Boolean) {}
    private fun onLVCDOpen() {}
    private fun onBCDOpen(layerPosition: Int, blockPosition: Int) {}
    // private fun onBlockVolumeSeekBarMove(lp:Int, bp: Int) {}
    // private fun onBlockEffectChanged(lp:Imt, bp: Int) {}
    // private fun onLoopVolumeSeekBarMove(lp: Int, bp: Int) {}
    // private fun onRecordVolumeChanged(lp: Int, bp: Int) {}

    //update view
    val updateRunnable: Runnable = Runnable {
        recordSeekBarButton!!.progress = recordCurrentPosition.ms
        if (recordFlag){
            if(recordSeekBarButton!!.progress >= recordSeekBarButton!!.max * 0.8f) {
                expandRecordSeekMax()
                expandLayerLinear()
            }
        }
    }

    inner class BCDListener : BlockControlDialog.BlockControlListener {
        override fun onVolumeChanged(progress: Int, max: Int, layerId: Int, blockId: Int) {
            if(recordFlag || playFlag) {
                Log.d("FRA, BlockControl", "BCDListener.onVolumeChanged(재생 혹은 녹음 중이어서 이벤트를 받지 않음.)")
            } else {
                Log.d("FRA, BlockControl", "BCDListener.onVolumeChanged(progress: $progress, max: $max, layerId: $layerId, blockId: $blockId)")
                // TODO("블록 볼륨 바꾸기") //To change body of created functions use File | Settings | File Templates.
            }
        }

        override fun onEffectChanged(effect: EffectorPresets, layerId: Int, blockId: Int) {
            if(recordFlag || playFlag) {
                Log.d("FRA, BlockControl", "BCDListener.onEffectChanged(재생 혹은 녹음 중이어서 이벤트를 받지 않음.)")
            } else {
                Log.d("FRA, BlockControl", "BCDListener.onEffectChanged(effect: $effect.name, layerId: $layerId, blockId: $blockId)")
                // TODO("이펙트 바꾸기") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    inner class BCListener: BlockView.BlockControlListener {
        override fun onClickListener(layerId: Int, blockId: Int) {
            if (recordFlag || playFlag) {
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
            // TODO("볼륨 컨트롤 추가") //To change body of created functions use File | Settings | File Templates.
            Log.d("FRA, VolumeControl", "VCDListener.루프볼륨(progress: $progress, max: $max)")
        }

    }
}

class MuteButtonBuilder(val context: Context) {
    private var label: String = ""
    private var isChecked = true
    private var onMuteEvent: ()->Unit = {}
    private var onUnMuteEvent: ()->Unit = {}
    private var basicHeight: Int = 0

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
            setTextColor(resources.getColorStateList(R.color.mute_toggle_text_selector, null))
        }
    }
}
