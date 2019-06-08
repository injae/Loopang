package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.treasure.loopang.R
import com.treasure.loopang.listitem.SongItem
import kotlinx.android.synthetic.main.songlist_item.view.*

class SongListAdapter(private val songItemList: ArrayList<SongItem>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent?.context
        val holder: Holder
        val view: View
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.songlist_item, null)
            holder = Holder(view)

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as Holder
        }

        val songItem = songItemList[position]
        holder.songName.text = songItem.songName
        holder.songDate.text = songItem.dateString

        return view
    }

    override fun getItem(position: Int): Any {
        return songItemList[position]
    }

    override fun getItemId(position: Int): Long {
        /* 사용용도를 모르겠어서 0으로 설정 */
        return 0
    }

    override fun getCount(): Int {
        return songItemList.size
    }

    inner class Holder(view: View) {
        var songName = view.song_name
        var songDate = view.song_date
    }

}