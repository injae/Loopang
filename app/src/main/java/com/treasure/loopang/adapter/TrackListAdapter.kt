package com.treasure.loopang.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.TextView
import com.treasure.loopang.R
import com.treasure.loopang.customview.VisualizerView
import com.treasure.loopang.listitem.TrackItem
import kotlinx.android.synthetic.main.tracklist_item.view.*

class TrackListAdapter (private val trackItemList: ArrayList<TrackItem>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val holder : Holder

        if (convertView == null) {
            val visualizerView = trackItemList[position].visualizerView

            view = LayoutInflater.from(parent?.context).inflate(
                R.layout.tracklist_item, parent, false)

            view.visualizer_frame.addView(visualizerView)

            holder = Holder()

            holder.trackName = view.track_name
            holder.visualizerView = visualizerView

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as Holder
        }

        /* trackItemList의 Item에서 tracklist_item 각 위젯에 매칭 */
        val trackItem = trackItemList[position]
        holder.trackName.text = trackItem.trackName
        holder.visualizerView = trackItem.visualizerView

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

    inner class Holder{
        lateinit var trackName: TextView
        lateinit var visualizerView: VisualizerView
    }
}