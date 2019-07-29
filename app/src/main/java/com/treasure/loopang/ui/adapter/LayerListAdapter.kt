package com.treasure.loopang.ui.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val mHandler = Handler()

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
        layerListHolder.layerView.muteState = layerItem.muteState

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
        mHandler.post { notifyDataSetChanged() }
    }

    fun dropLayer(position: Int){
        stopNowLayer()
        layerItemList.removeAt(position)
        // mHandler.post { notifyDataSetChanged() }
    }

    fun dropAllLayer() {
        stopNowLayer()
        layerItemList.clear()
        mHandler.post { notifyDataSetChanged() }
    }

    fun playLayer(view: View){
        stopNowLayer()
        nowPlayingLayer = view.layer_view as LayerView
        nowPlayingLayer?.play()
    }

    fun switchLayerMuteState(position: Int){
        // Log.d("LayerListAdapter", "layerItemList[$position].muteState: ${layerItemList[position].muteState}")
        layerItemList[position].muteState = !layerItemList[position].muteState
        notifyDataSetChanged()
    }
    fun setLayerMuteState(position: Int, muteState: Boolean){
        layerItemList[position].muteState = muteState
        notifyDataSetChanged()
    }

    fun stopNowLayer(){
        var layerPlayingState = false

        nowPlayingLayer?.let{ layerPlayingState = it.playState } ?: return
        if(layerPlayingState) {
            nowPlayingLayer?.stop()
        }
    }
}

class LayerListHolder{
    lateinit var layerView: LayerView
}