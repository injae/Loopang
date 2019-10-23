package com.treasure.loopang.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.treasure.loopang.listitem.MyPageItem
import android.R
import android.animation.ObjectAnimator


class MyPageAdapter : BaseAdapter() {

    private var listViewItemList = ArrayList<MyPageItem>()

    override fun getCount(): Int {
        return listViewItemList.size
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view : View
        val context = parent.context
        val myTrackViewHolder : ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.my_page_track_list, null)

            myTrackViewHolder = ViewHolder()
            myTrackViewHolder.songNameTextView = view.findViewById(com.treasure.loopang.R.id.myPageSongName) as TextView
            myTrackViewHolder.productionDateTextView = view.findViewById(com.treasure.loopang.R.id.myPageProductionDate) as TextView
          //  myTrackViewHolder.btnShareView= view.findViewById(com.treasure.loopang.R.id.myPageBtnShare) as ImageButton
            view.tag = myTrackViewHolder
        }else{
            myTrackViewHolder = convertView.tag as ViewHolder //viewHolder = convertView!!.getTag() as ViewHolder
            view = convertView
        }
        myTrackViewHolder.songNameTextView?.setText(listViewItemList.get(position).songName)
        myTrackViewHolder.productionDateTextView?.setText(listViewItemList.get(position).productionDate)

        val listViewItem = listViewItemList[position]

        return view
    }
    fun shareSong(){
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    fun addItem(songName:String,productionDate:String) {
        val item = MyPageItem()
        item.songName = songName
        item.productionDate = productionDate
        //item.btnShare = btnShare
        listViewItemList.add(item)
    }

    private  class ViewHolder{
        var songNameTextView : TextView? =null
        var productionDateTextView : TextView? = null
     //   var btnShareView : ImageButton? = null
    }

}

