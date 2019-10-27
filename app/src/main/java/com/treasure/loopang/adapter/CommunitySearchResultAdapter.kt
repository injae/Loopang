package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.listitem.EffectorListItem
import android.media.MediaPlayer
import java.io.IOException
import android.widget.TextView
import com.treasure.loopang.listitem.CommunitySongItem

class CommunitySearchResultAdapter : BaseAdapter() {

    private var listViewItemList = ArrayList<CommunitySongItem>()

    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view : View
        val context = parent.context
        val SearchResultViewHolder : ViewHolder

        var btnSortState: String? = "Song" //test 용으로 song으로 초기화함

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.community_search_item, null)
            SearchResultViewHolder = ViewHolder()

            SearchResultViewHolder.songNameTextView = view.findViewById(com.treasure.loopang.R.id.SearchSongName) as TextView
            SearchResultViewHolder.userNickNameTextView = view.findViewById(com.treasure.loopang.R.id.SearchArtistName) as TextView

            view.tag = SearchResultViewHolder
        }else{
            SearchResultViewHolder = convertView.tag as ViewHolder //viewHolder = convertView!!.getTag() as ViewHolder
            view = convertView
        }

        SearchResultViewHolder.userNickNameTextView?.setText(listViewItemList.get(position).userNickName)
        SearchResultViewHolder.songNameTextView?.setText((listViewItemList.get(position).songName))

        val listViewItem = listViewItemList[position]

        return view
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

