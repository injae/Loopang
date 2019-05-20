package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.treasure.loopang.R
import com.treasure.loopang.listitem.TrackItem

class TrackListAdapter (val trackItemList: ArrayList<TrackItem>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent?.getContext()
        val view: View = LayoutInflater.from(context).inflate(R.layout.tracklist_item, null)

        /* trackItemList의 Item에서 tracklist_item 각 위젯에 매칭 */
       val trackItem = trackItemList[position]

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
}