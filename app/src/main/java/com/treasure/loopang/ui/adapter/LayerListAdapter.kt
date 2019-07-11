package com.treasure.loopang.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import com.treasure.loopang.R
import com.treasure.loopang.audiov2.Sound
import com.treasure.loopang.ui.item.LayerItem
import com.treasure.loopang.ui.view.LayerView
import kotlinx.android.synthetic.main.layer_item.view.*

class LayerListAdapter : BaseAdapter() {
    private val layerItemList: MutableList<LayerItem> = mutableListOf()
    private var nowPlayingLayer: LayerView? = null
    private var lastLayerNumber: Int = 1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val layerListHolder: LayerListHolder
        val layerItem: LayerItem = layerItemList[position]

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

        return view
    }

    override fun getItem(position: Int): Any {
        return if(layerItemList.isNotEmpty() && (0 <= position && position < layerItemList.size))
            layerItemList[position]
        else layerItemList[0]
    }

    override fun getItemId(position: Int): Long {
        return if(layerItemList.isNotEmpty() && (0 <= position && position < layerItemList.size))
            position.toLong()
        else 0
    }

    override fun getCount(): Int {
        return layerItemList.size
    }

    fun addLayer(sound: Sound){
        val layerItem = LayerItem(sound, "Layer ${lastLayerNumber++}")
        layerItemList.add(layerItem)
        notifyDataSetChanged()
    }

    fun onLayerClick(parent: AdapterView<*>, view: View, position: Int, id: Long){
        var layerPlayingState = false

        nowPlayingLayer?.let{ layerPlayingState = it.playState }
        if(layerPlayingState) {
            nowPlayingLayer?.let{ it.stop() }
        }
        nowPlayingLayer = view as LayerView
        nowPlayingLayer?.play()
    }

    fun onLayerLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long) : Boolean = false
}

class LayerListHolder{
    lateinit var layerView: LayerView
}