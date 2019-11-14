package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.listitem.CommunitySongItem
import org.w3c.dom.Text
import java.util.ArrayList


class CommunityUserPageAdapter : BaseAdapter() {

    private var listViewItemList = ArrayList<CommunitySongItem>()
    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view : View
        val context = parent.context
        val userPageViewHolder : ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.community_user_page_item, null)
            userPageViewHolder = ViewHolder()
            userPageViewHolder.songNameTextView= view.findViewById(com.treasure.loopang.R.id.myPageSongName) as TextView
            userPageViewHolder.productionDateTextView = view.findViewById(com.treasure.loopang.R.id.myPageProductionDate) as TextView

            view.tag =  userPageViewHolder
        }else{
            userPageViewHolder = convertView.tag as ViewHolder
            view = convertView
        }

       // userPageViewHolder.userNickNameTextView?.setText(listViewItemList.get(position).userNickName)
        userPageViewHolder.productionDateTextView?.setText( listViewItemList.get(position).productionDate.substring(0,10))
        userPageViewHolder.songNameTextView?.setText(listViewItemList.get(position).songName)

        val listViewItem = listViewItemList[position]

        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    fun addItem(userNickName: String, songName: String,  likedNum : Int, downloadNum :Int,songId : String, productionDate : String, trackInfo : String) {
        val item = CommunitySongItem()
        item.userNickName= userNickName
        item.songName = songName
        item.downloadNum = downloadNum
        item.likedNum = likedNum
        item.songId = songId
        item.productionDate =productionDate
        item.trackInfo = trackInfo
        listViewItemList.add(item)
    }

    private  class ViewHolder{
        var userNickNameTextView : TextView? = null
        var productionDateTextView : TextView? = null
        var songNameTextView :TextView? = null
    }
}