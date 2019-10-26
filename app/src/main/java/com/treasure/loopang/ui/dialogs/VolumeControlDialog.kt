package com.treasure.loopang.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.widget.SeekBar
import com.treasure.loopang.R
import kotlinx.android.synthetic.main.volume_control_drawer.*

class VolumeControlDialog(context: Context, var listener : VolumeControlDialogListener? = null ) : Dialog(context) {

    private val recordVolumeSeekBar : SeekBar by lazy { record_volume_seekbar }
    private val loopVolumeSeekBar : SeekBar by lazy { loop_volume_seekbar }

    var loopVolumeMax = 100
    var loopVolume = 50

    var recordVolumeMax = 100
    var recordVolume = 50

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setGravity(Gravity.START)
        setContentView(R.layout.volume_control_drawer)

        recordVolumeSeekBar.setOnSeekBarChangeListener(RecordSeekBarListener())
        loopVolumeSeekBar.setOnSeekBarChangeListener(LoopSeekBarListener())

        recordVolumeSeekBar.max = recordVolumeMax
        recordVolumeSeekBar.progress = recordVolume

        loopVolumeSeekBar.max = loopVolumeMax
        loopVolumeSeekBar.progress = loopVolume

        Log.d("VolumeControlDialog", "VolumeControlDialog.init()")
    }

    fun show(recordProgress:Int, recordMax:Int, loopProgress: Int, loopMax: Int){
        recordVolume = recordProgress
        recordVolumeMax = recordMax
        recordVolumeSeekBar.max = recordMax
        recordVolumeSeekBar.progress = recordProgress

        loopVolume = loopProgress
        loopVolumeMax = loopMax
        loopVolumeSeekBar.max = loopMax
        loopVolumeSeekBar.progress = loopProgress
        Log.d("VolumeControlDialog", "VolumeControlDialog.show(recordProgress:$recordProgress, recordMax:$recordMax, loopProgress: $loopProgress, loopMax: $loopMax)")
        this.show()
    }

    override fun show(){
        super.show()
        Log.d("VolumeControlDialog", "VolumeControlDialog.show()")
    }

    inner class RecordSeekBarListener : SeekBar.OnSeekBarChangeListener {
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            seekBar?.let {
                listener?.onRecordVolumeChanged(it.progress, it.max)
                Log.d("VolumeControlDialog", "RecordSeekBarListener.onStopTrackingTouch(volume_max: ${it.max}, volume: ${it.progress})")
            }
        }
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    }

    inner class LoopSeekBarListener : SeekBar.OnSeekBarChangeListener {
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            seekBar?.let {
                listener?.onLoopVolumeChanged(it.progress, it.max)
                Log.d("VolumeControlDialog", "LoopSeekBarListener.onStopTrackingTouch(volume_max: ${it.max}, volume: ${it.progress})")
            }
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
    }

    interface VolumeControlDialogListener {
        fun onRecordVolumeChanged(progress: Int, max: Int)
        fun onLoopVolumeChanged(progress: Int, max: Int)
    }
}