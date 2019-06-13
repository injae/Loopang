package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.treasure.loopang.R
import com.treasure.loopang.customview.VisualizerView
import com.treasure.loopang.listitem.TrackItem
import kotlinx.android.synthetic.main.tracklist_item.view.*

class TrackListAdapter (private val trackItemList: ArrayList<TrackItem>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val trackListHolder: TrackListHolder
        val trackItem               = trackItemList[position]

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate(
                R.layout.tracklist_item, parent, false)

            trackListHolder = TrackListHolder()
            trackListHolder.trackName = view.track_name
            trackListHolder.visualizerView = view.visualizer

            view.tag = trackListHolder
        } else {
            view = convertView
            trackListHolder = view.tag as TrackListHolder
        }

        trackListHolder.trackName.text = trackItem.trackName
        if(!trackItem.amplitudes.isEmpty())
            trackListHolder.visualizerView.amplitudes = trackItem.amplitudes
        trackListHolder.visualizerView.invalidate()

        return view
    }

    override fun getItem(position: Int): Any {
        return trackItemList[position]
    }

    override fun getCount(): Int {
        return trackItemList.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    fun addItem(item: TrackItem) {
        trackItemList.add(0,item)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        trackItemList.removeAt(position)
        notifyDataSetChanged()
    }

    inner class TrackListHolder{
        lateinit var trackName: TextView
        lateinit var visualizerView: VisualizerView
    }
}