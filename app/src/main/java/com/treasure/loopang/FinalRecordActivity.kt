package com.treasure.loopang

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.constraintlayout.widget.ConstraintSet
import com.treasure.loopang.ui.dpToPx
import com.treasure.loopang.ui.util.TimeWrapper
import com.treasure.loopang.ui.util.WidthPerTime
import com.treasure.loopang.ui.view.BlockLayerView
import kotlinx.android.synthetic.main.activity_final_record.*

class FinalRecordActivity : AppCompatActivity() {
    private val wpt: WidthPerTime = WidthPerTime(width=40, ms=200)
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
    // val volumeControlDialog: VolumeControlDialog =  VolumeControlDialog()
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
        recordButton!!.setOnClickListener {
            if ((it as ToggleButton).isChecked) {
                muteButtonList.forEachIndexed { index, toggle ->
                    if (toggle.isChecked){
                        recordFlag = true
                        blockLayerViewList[index].addBlock(start = recordCurrentPosition.ms, duration = 0)
                        blockLayerViewList[index].expandBlock()
                    } else {
                        recordFlag = false
                    }
                }
            } else {
                stopBlock()
                refreshView()
            }
        }
        recordStopButton!!.setOnClickListener {}

        toStartButton!!.setOnClickListener { recordSeekBarButton!!.progress = 0 }
        toEndButton!!.setOnClickListener { recordSeekBarButton!!.progress = wpt.getWidth(500) * recordDuration.hs}
        playButton!!.setOnClickListener {
            if ((it as ToggleButton).isChecked) {
                //todo: 재생시 동작
                clear()
            } else {
                //todo: 재생 정지시 동작

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
                timeStampTxt!!.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun initAfterInflation(){
        recordTimelineScrollView?.viewTreeObserver?.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                recordTimelineScrollView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)

                //레이어뷰 기본 너비/높이 지정.
                basicWidth = record_timeline_panel_scroll.width
                basicHeight = dpToPx(this@FinalRecordActivity, 56f).toInt()

                //레이어뷰, 뮤트버튼 리스트에 초기화.
                initLayerList(5)

                //전체 시크바 MAX 초기화.
                recordSeekBarButton?.max = wpt.getTime(basicWidth)
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
            blockLayerView.layerEventListener = object: BlockLayerView.LayerEventListener{
                override fun onBlockExpand(blockLayerView: BlockLayerView, lastPosition: Int) {
                    if((layerListLinear!!.width * 0.8 < lastPosition) && !expandFlag) {
                        expandFlag = true
                        blockLayerViewList.forEach {
                            it.expandSize()
                        }
                        expandFlag = false
                    }
                }
            }
            layer_list_linear.addView(blockLayerView, LinearLayout.LayoutParams(basicWidth, basicHeight))
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
    }

    private fun refreshView(){
        // todo: 여기에 블록들을 가시화하는 코드를 작성.
        /*blockLayerViewList[0].addBlock(0, 1000,1000)
        blockLayerViewList[2].addBlock(0, 1500,2000)
        blockLayerViewList[3].addBlock(0, 4000, 1200, object: BlockView.BlockControlListener{
            override fun onClickListener(layerId: Int, blockId: Int) {
                toast("lid: $layerId, bid: $blockId")
            }
        })*/
    }

    private fun expandBlock(){
        blockLayerViewList.forEach{
            it.expandBlock()
        }
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
                if (this.isChecked) onMuteEvent()
                else onUnMuteEvent()
            }
            Log.d("muteButtonBuild", "lable: $label, height: $height")
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, basicHeight)
            setTextColor(resources.getColorStateList(R.color.mute_toggle_text_selector, null))
        }
    }
}
