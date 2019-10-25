package com.treasure.loopang.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.treasure.loopang.audio.Effector
import kotlinx.android.synthetic.main.block_control_dialog.*

class BlockControlDialog(context: Context, effectorPresets: EffectorPresets) : Dialog(context) {
    private val blockControlTab by lazy { block_control_tab }
    private val volumeTab by lazy { volume_tab }
    private val effectTab by lazy { effect_tab }
    private val effectorList: List<EffectorPresets> = effectorPresets.getList

    var volume_max: Int = 100
    var volume: Int = 0

    private fun showVolumeTab () {
        volumeTab.visibility = View.VISIBLE
        effectTab.visibility = View.GONE
    }

    private fun showEffectTab() {
        volumeTab.visibility = View.GONE
        effectTab.visibility = View.VISIBLE
    }

    interfaece BlockControlListener() {
        fun onVolumeChanged(progress: Int, max: Int, layerId: Int, blockId: Int)
        fun onEffectChanged(effect: EffectorPresets, layerId: Int, blockId: Int)
    }
}