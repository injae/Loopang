package com.treasure.loopang

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.ToggleButton

class FinalRecordActivity : AppCompatActivity() {
    val widthPerTime: WidthPerTime = WidthPerTime(ms=200, width=10)
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
    val muteFlagList: ArrayList<Boolean>? = null
    val blockLayerViewList: ArrayList<BlockLayerView>? = null

    var loopCanvas: Canvas? = null
    var loopDrawable: Drawable? = null
    val layerCanvasList: ArrayList<Canvas>? = null
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

    //record timeline
    var recordSeekBarLine: View? = null
    var recordSeekBarButton: SeekBar? = null

    //dialog
    val volumeControlDialog: VolumeControlDialog =  VolumeControlDialog()
    val blockControlDialog: BlockControlDialog = BlockControlDialog()
    val saveDialog: FinalSaveDialog = FinalSaveDialog()

    //activity event
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_record)
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

    //update view
    val updateRunnable: Runnable = Runnable {
        TODO("not implemented")
    }
}
