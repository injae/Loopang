package com.treasure.loopang

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class SongListAdapter(val songItemList: ArrayList<SongItem>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent?.getContext()
        val view: View = LayoutInflater.from(context).inflate(R.layout.songlist_item, null)

        /* songItemList의 Item에서 songlist_item 각 위젯에 매칭 */
        val songItem = songItemList[position]

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

}