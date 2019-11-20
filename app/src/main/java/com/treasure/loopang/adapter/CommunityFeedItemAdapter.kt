package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.listitem.CommunitySongItem
import java.util.ArrayList


class CommunityFeedItemAdapter : BaseAdapter(){
    private var likeNum :Int? = null
    lateinit var likeList : MutableList<MusicListClass>
    private var listViewItemList = ArrayList<MusicListClass>()
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
            feedViewHolder.downloadNumTextView = view.findViewById(com.treasure.loopang.R.id.playedNumTextView) as TextView

            view.tag = feedViewHolder
        }else{
            feedViewHolder = convertView.tag as ViewHolder //viewHolder = convertView!!.getTag() as ViewHolder
            view = convertView
        }
        for(i in 0 .. likeList.size-1){
            if(listViewItemList.get(position).id == likeList[i].id)
                likeNum = likeList[i].likes
        }
        feedViewHolder.userNickNameTextView?.setText(listViewItemList.get(position).owner)
        feedViewHolder.songNameTextView?.setText(listViewItemList.get(position).name)
        feedViewHolder.likedNumTextView?.setText(likeNum.toString())
        feedViewHolder.downloadNumTextView?.setText(listViewItemList.get(position).downloads.toString())

        val listViewItem = listViewItemList[position]

        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    fun addItem(music: MusicListClass,likes : MutableList<MusicListClass>) {
        likeList = likes
        listViewItemList.add(music)
    }

    private  class ViewHolder{
        var userNickNameTextView : TextView ? = null
        var songNameTextView :  TextView ? = null
        var downloadNumTextView : TextView? = null
         var likedNumTextView : TextView? = null
    }
}