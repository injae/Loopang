package com.treasure.loopang.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.treasure.loopang.R
import com.treasure.loopang.listitem.TrackItem

class TrackListAdapter (private val trackItemList: ArrayList<TrackItem>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder : Holder
        val view : View

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate(
                R.layout.tracklist_item, parent, false)

            holder = Holder(view)
            /* 아이템 생겼을 때 별도 처리*/
            /*예시
            holder.tx1 = convertview.xxxx1 as TextView
            holder.tx2 = convertview.xxxx2 as TextView
            holder.img1 = convertview.R.id.yyyy1 as ImagView*/

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as Holder
        }

        /* trackItemList의 Item에서 tracklist_item 각 위젯에 매칭 */
        val trackItem = trackItemList[position]

        /* 아이템 생겼을 때 별도 처리 */

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

    inner class Holder(view : View)
}