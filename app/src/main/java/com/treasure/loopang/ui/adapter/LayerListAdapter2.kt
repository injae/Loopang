package com.treasure.loopang.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.treasure.loopang.R
import com.treasure.loopang.audio.MixerSound
import com.treasure.loopang.audio.Sound
import com.treasure.loopang.ui.item.LayerItem
import com.treasure.loopang.ui.view.LayerView
import kotlinx.android.synthetic.main.layer_item.view.*

class LayerListAdapter2 : BaseAdapter() {
    private var mLayerItemList: MutableList<LayerItem> = mutableListOf()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val layerListHolder: LayerListHolder
        val layerItem: LayerItem = mLayerItemList[position]

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate(
                R.layout.layer_item, parent, false
            )

            layerListHolder = LayerListHolder()
            layerListHolder.layerView = view.layer_view

            view.tag = layerListHolder
        } else {
            view = convertView
            layerListHolder = view.tag as LayerListHolder
        }

        layerListHolder.layerView.sound = layerItem.sound
        layerListHolder.layerView.layerLabel = layerItem.layerLabel
        layerListHolder.layerView.muteState = layerItem.muteState

        return view
    }

    override fun getItem(position: Int): Any {
        return if(mLayerItemList.isNotEmpty() && (0 <= position && position < mLayerItemList.size))
            mLayerItemList[position]
        else mLayerItemList[0]
    }

    override fun getItemId(position: Int): Long {
        return if(mLayerItemList.isNotEmpty() && (0 <= position && position < mLayerItemList.size))
            position.toLong()
        else 0
    }

    override fun getCount(): Int {
        return mLayerItemList.size
    }

    fun addLayer(sound: Sound, layerLabel:String){
        val layerItem = LayerItem(sound, layerLabel)
        this._addLayer(layerItem)
    }

    private fun _addLayer(layerItem: LayerItem) {
        mLayerItemList.add(layerItem)
        notifyDataSetChanged()
    }

    fun dropLayer(position: Int){
        mLayerItemList.removeAt(position)
    }

    fun dropAllLayer() {
        mLayerItemList.clear()
        notifyDataSetInvalidated()
    }

    fun switchLayerMuteState(position: Int, view: View) {
        val layerView = view as LayerView
        val muteState = !mLayerItemList[position].muteState
        layerView.muteState = muteState
        mLayerItemList[position].muteState = muteState
    }

    fun setLayerMuteState(position: Int, view: View, muteState: Boolean){
        val layerView = view as LayerView
        mLayerItemList[position].muteState = muteState
        layerView.muteState = muteState
    }

    fun setLayerItemList(layerItemList: MutableList<LayerItem>) {
        mLayerItemList = layerItemList
        notifyDataSetInvalidated()
    }

    fun setLayerItemList(sounds: MutableList<MixerSound>,
                         layerLabelList: MutableList<String>) {
        setLayerItemList(makeLayerItemList(sounds, layerLabelList))
    }

    companion object{
        fun makeLayerItemList(sounds: MutableList<MixerSound>,
                              layerLabelList: MutableList<String>): MutableList<LayerItem> {
            return mutableListOf<LayerItem>().apply {
                sounds.zip(layerLabelList).forEach {
                    add(LayerItem(it.first._sound, it.second))
                }
            }
        }
    }

    inner class LayerListHolder{
        lateinit var layerView: LayerView
    }
}