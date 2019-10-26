package com.treasure.loopang.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import com.google.android.material.tabs.TabLayout
import com.treasure.loopang.R
import com.treasure.loopang.audio.EffectorPresets

import kotlinx.android.synthetic.main.block_control_dialog.*
import kotlinx.android.synthetic.main.block_effect_control_tab.*
import kotlinx.android.synthetic.main.block_volume_tab.*
import kotlinx.android.synthetic.main.effect_item.*
import kotlinx.android.synthetic.main.effect_item.view.*

class BlockControlDialog(context: Context, listener: BlockControlListener) : Dialog(context)
    , TabLayout.OnTabSelectedListener
    , SeekBar.OnSeekBarChangeListener
    , AdapterView.OnItemClickListener {

    private val blockControlTab by lazy { block_control_tab }
    private val volumeTab by lazy { volume_tab }
    private val effectTab by lazy { effect_tab }
    private val volumeSeekBar by lazy { block_volume_seekbar }
    private val effectListView by lazy { block_effect_listview }

    private val effectors = EffectorPresets.values()

    var layerId: Int = 0
    var blockId: Int = 0

    var volumeMax: Int = 100
    var volume: Int = volumeMax / 2
        set(value) {
            field = if (value > volumeMax) {
                volumeMax
            } else {
                value
            }
        }
    var effect: EffectorPresets = EffectorPresets.NONE

    var blockControlListener : BlockControlListener? = null



    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.END)
        setContentView(R.layout.block_control_dialog)

        effectListView.adapter = ListAdapter(context, effectors)
        volumeSeekBar.max = volumeMax
        volumeSeekBar.setOnSeekBarChangeListener(this)
        blockControlTab.setOnTabSelectedListener(this)
        effectListView.onItemClickListener = this
        blockControlListener = listener

        Log.d("BlockControlDialog", "BlockControlDialog.init()")
    }

    fun show(layerId: Int, blockId: Int, volume:Int, effect: EffectorPresets) {
        Log.d("BlockControlDialog", "BlockControlDialog.show(layerId: $layerId, blockId: $blockId, volume: $volume, effect: ${effect.name})")
        this.layerId = layerId
        this.blockId = blockId
        this.volume = volume
        this.effect = effect
        this.show()
    }

    fun show(layerId: Int, blockId: Int) {
        Log.d("BlockControlDialog", "BlockControlDialog.show(layerId: $layerId, blockId: $blockId)")
        this.layerId = layerId
        this.blockId = blockId
        this.show()
    }

    private fun showVolumeTab () {
        Log.d("BlockControlDialog", "BlockControlDialog.showVolumeTab()")
        volumeTab.visibility = View.VISIBLE
        effectTab.visibility = View.GONE
    }

    private fun showEffectTab() {
        Log.d("BlockControlDialog", "BlockControlDialog.showEffectTab()")
        volumeTab.visibility = View.GONE
        effectTab.visibility = View.VISIBLE
    }

    /* 탭 스위칭 리스너 구현 */
    // 이미 선택된 상태의 tab이 사용자에 의해 다시 선택됨.
    override fun onTabReselected(p0: TabLayout.Tab?) {}

    // tab의 상태가 선택 상태에서 선택되지 않음으로 변경됨.
    override fun onTabUnselected(p0: TabLayout.Tab?) {}

    // tab의 상태가 선택되지 않음에서 선택 상태로 변경됨.
    override fun onTabSelected(p0: TabLayout.Tab?) {
        p0?.let{
            Log.d("BlockControlDialog", "BlockControlDialog.onTabSelected(layerId: $layerId, blockId: $blockId, tagText: ${it.text})")
            when (it.text) {
                context.resources.getString(R.string.volume_tab_str) -> showVolumeTab()
                context.resources.getString(R.string.effect_tab_str) -> showEffectTab()
            }
        }
    }

    /* 시크바 이벤트 리스너 구현 */
    // 시크바를 움직이고 있을 때
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
    // 시크바를 클릭했을 때
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    // 시크바를 놓았을 때
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.let{
            Log.d("BlockControlDialog", "BlockControlDialog.onStopTrackingTouch(layerId: $layerId, blockId: $blockId, voluemax: ${it.max}, volume: ${it.progress})")
            blockControlListener?.onVolumeChanged(it.progress, it.max, layerId, blockId)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val temp = effectors[position]
        if(temp == EffectorPresets.NONE && effect == EffectorPresets.NONE) {
            Log.d("BlockControlDialog", "BlockControlDialog.onItemClick(기존 이펙트가 NONE 이어서 NONE 처리를 받지 않음)")
        } else {
            effect = effectors[position]
            blockControlListener?.onEffectChanged(effect, layerId, blockId)
        }
        Log.d("BlockControlDialog", "BlockControlDialog.onItemClick(layerId: $layerId, blockId: $blockId, preEffect: $effect, nextEffect: $temp)")
    }

    /* 블록 컨트롤 다이얼로그 콜백 */
    interface BlockControlListener {
        fun onVolumeChanged(progress: Int, max: Int, layerId: Int, blockId: Int)
        fun onEffectChanged(effect: EffectorPresets, layerId: Int, blockId: Int)
    }

    private inner class ListAdapter(
        context: Context,
        effectList: Array<EffectorPresets>
    ) : ArrayAdapter<EffectorPresets>(context, 0,effectList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view: View? = convertView
            val effect: EffectorPresets? = getItem(position)

            if (view == null){
                view = LayoutInflater.from(context).inflate(R.layout.effect_item, parent, false)
            }
            view!!.effect_label.text = effect?.name ?: "effect"

            if (effect == this@BlockControlDialog.effect)
                view.background= (context.resources.getDrawable(R.drawable.block_effect_item_background_s))
            else view.background= (context.resources.getDrawable(R.drawable.block_effect_item_background))

            return view
        }
    }
}