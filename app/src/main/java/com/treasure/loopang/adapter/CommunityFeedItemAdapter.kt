package com.treasure.loopang.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.listitem.CommunityFeedItem
import com.treasure.loopang.listitem.EffectorListItem
import com.treasure.loopang.listitem.SettingItem
import java.io.IOException
import java.util.ArrayList


class CommunityFeedItemAdapter : BaseAdapter(){
    private var listViewItemList = ArrayList<CommunityFeedItem>()
    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view : View
        val context = parent.context
        val feedViewHolder : ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.community_feed_item, null)
            feedViewHolder = ViewHolder()
            feedViewHolder.userNickNameTextView = view.findViewById(com.treasure.loopang.R.id.FeedUserNickName) as TextView
            feedViewHolder.songNameTextView = view.findViewById(com.treasure.loopang.R.id.FeedSongName) as TextView
            feedViewHolder.likedNumTextView = view.findViewById(com.treasure.loopang.R.id.heartNumTextView) as TextView
            feedViewHolder.playedNumTextView = view.findViewById(com.treasure.loopang.R.id.playedNumTextView) as TextView

            view.tag = feedViewHolder
        }else{
            feedViewHolder = convertView.tag as ViewHolder //viewHolder = convertView!!.getTag() as ViewHolder
            view = convertView
        }
        feedViewHolder.userNickNameTextView?.setText(listViewItemList.get(position).userNickName)
        feedViewHolder.songNameTextView?.setText(listViewItemList.get(position).songName)
        feedViewHolder.likedNumTextView?.setText(listViewItemList.get(position).likedNum)
        feedViewHolder.playedNumTextView?.setText(listViewItemList.get(position).playedNum)

        val listViewItem = listViewItemList[position]

        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    fun addItem(userNickName: String, songName: String,  likedNum : String, playedNum : String) {
        val item = CommunityFeedItem()

        item.userNickName= userNickName
        item.songName = songName
        item.likedNum = likedNum
        item.playedNum = playedNum

        listViewItemList.add(item)
    }

    private  class ViewHolder{
        var userNickNameTextView : TextView ? = null
        var songNameTextView :  TextView ? = null
        var likedNumTextView :  TextView ? = null
        var playedNumTextView : TextView ? = null
    }
}