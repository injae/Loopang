package com.treasure.loopang.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.listitem.CommunitySongItem
import java.util.ArrayList

class CommunitySearchitemAdapter : BaseAdapter() {
    var btnSortState : String = "Song"//test용으로 초기화

    private var listViewItemList = ArrayList<CommunitySongItem>()

    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val context = parent.context
        val SongItemViewHolder : ViewHolder

        var btnSortState: String? = null

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.community_search_item, null)
            SongItemViewHolder = ViewHolder()
            SongItemViewHolder.songNameTextView = view.findViewById(com.treasure.loopang.R.id.SearchSongName) as TextView
            SongItemViewHolder.userNickNameTextView = view.findViewById(com.treasure.loopang.R.id.SearchArtistName) as TextView
            view.tag = SongItemViewHolder
        }else{
            SongItemViewHolder = convertView.tag as ViewHolder //viewHolder = convertView!!.getTag() as ViewHolder
            view = convertView
        }

        SongItemViewHolder.userNickNameTextView?.setText(listViewItemList.get(position).userNickName)
        SongItemViewHolder.songNameTextView?.setText(listViewItemList.get(position).songName)


            val userNickNameTextView = view?.findViewById(com.treasure.loopang.R.id.SearchArtistName) as TextView
            val songNameTextView = view?.findViewById(com.treasure.loopang.R.id.SearchSongName) as TextView
        val listViewItem = listViewItemList[position]

        return view!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }
    fun addItem(userNickName: String, songName: String,songId : String) {
        val item = CommunitySongItem()
        item.userNickName = userNickName
        item.songName = songName
        item.songId = songId
        listViewItemList.add(item)
    }
    private  class ViewHolder{
        var userNickNameTextView : TextView ? = null
        var songNameTextView :  TextView ? = null
    }
}