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
    var is_remove = false
    var removed_item = -1
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        //val holder : Holder

        Log.d("TESTTTTTT", "in ${position}, size: ${trackItemList.size}")
        //val trackItem = trackItemList[position]
        if (convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate( R.layout.tracklist_item, parent, false)
            //holder = Holder()
            //holder.trackName = view.track_name
            //holder.visualizerView = visualizerView

            //view.tag = holder
            if(!is_remove)  {
                var visualizerView = trackItemList[position].visualizerView
                view.visualizer_frame.addView(visualizerView)
            }
            else {
                Log.d("TESTTTTTT", "remove ${removed_item}, size: ${trackItemList.size}")
                is_remove = false
                var item = trackItemList[removed_item]
                view.visualizer_frame.removeView(item.visualizerView)
                trackItemList.remove(item)
                notifyDataSetChanged()
            }
        } else {
            view = convertView!!
            //holder = view.tag as Holder
        }
        //holder.trackName.text = trackItem.trackName
        //holder.visualizerView = trackItem.visualizerView

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
        is_remove = true
        removed_item = position
        notifyDataSetChanged()
    }

    inner class Holder{
        lateinit var trackName: TextView
        lateinit var visualizerView: VisualizerView
    }
}