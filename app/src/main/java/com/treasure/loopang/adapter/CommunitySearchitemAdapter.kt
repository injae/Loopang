package com.treasure.loopang.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.CommunitySearchFragment
import com.treasure.loopang.listitem.CommunitySongItem
import java.util.ArrayList

class CommunitySearchitemAdapter : BaseAdapter(), Filterable {

    private var listViewItemList = ArrayList<CommunitySongItem>()
    private var filteredItemList= listViewItemList

    var listFilter: Filter? = null

    override fun getFilter(): Filter {
        if (listFilter == null) {
            listFilter = ListFilter()
        }
        return listFilter!!
    }

    private inner class ListFilter : Filter() {
        override  protected fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint == null || constraint.length == 0) {
                results.values = listViewItemList
                results.count = listViewItemList.size
            } else {
                var itemList = ArrayList<CommunitySongItem>()

                for (item in listViewItemList) {
                    if (item.songName!!.toUpperCase().contains(constraint.toString().toUpperCase())) {
                        itemList.add(item)

                    }
                    if(item.userNickName!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                        itemList.add(item)
                    }
                }

                results.values = itemList
                results.count = itemList.size
            }
            return results
        }
        //protected
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            // update listview by filtered data list.
            filteredItemList = results.values as ArrayList<CommunitySongItem>

            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
    override fun getCount(): Int {
        return filteredItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val context = parent.context
        val SongItemViewHolder : ViewHolder

        var btnSortState: String? = null
        var isNowSearching : Boolean = false

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.community_search_ing_item, null)
            SongItemViewHolder = ViewHolder()

            if(btnSortState == "Song") {
                SongItemViewHolder.songNameTextView = view.findViewById(com.treasure.loopang.R.id.SearchSongName) as TextView
                SongItemViewHolder.userNickNameTextView = view.findViewById(com.treasure.loopang.R.id.SearchArtistName) as TextView
            }
            else if(btnSortState == "Artist"){
                SongItemViewHolder.userNickNameTextView = view.findViewById(com.treasure.loopang.R.id.userName) as TextView
            }
            view.tag = SongItemViewHolder
        }else{
            SongItemViewHolder = convertView.tag as ViewHolder //viewHolder = convertView!!.getTag() as ViewHolder
            view = convertView
        }

        SongItemViewHolder.userNickNameTextView?.setText(listViewItemList.get(position).userNickName)
        //if(btnSortState == "Artist")SongItemViewHolder.songNameTextView?.setText(listViewItemList.get(position).songName)

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            if(isNowSearching == true){
                view = inflater.inflate(com.treasure.loopang.R.layout.community_search_ing_item, parent, false)
            } else{
                if(btnSortState == "Song") {
                    view = inflater.inflate(com.treasure.loopang.R.layout.community_search_item, parent, false)
                }else if (btnSortState == "Artist"){
                    view = inflater.inflate(com.treasure.loopang.R.layout.community_search_item2, parent, false)
                }
            }
        }
        if(btnSortState == "Song") {
            val userNickNameTextView = view?.findViewById(com.treasure.loopang.R.id.SearchArtistName) as TextView
            val songNameTextView = view?.findViewById(com.treasure.loopang.R.id.SearchSongName) as TextView
        }else if (btnSortState == "Artist"){
            val userNickNameTextView = view?.findViewById(com.treasure.loopang.R.id.userName) as TextView
        }
        val listViewItem = filteredItemList[position]

        return view!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return filteredItemList[position]
    }

    fun addItem(userNickName: String, songName: String) {
        val item = CommunitySongItem()
        item.userNickName = userNickName
        item.songName = songName
        listViewItemList.add(item)
    }
    private  class ViewHolder{
        var userNickNameTextView : TextView ? = null
        var songNameTextView :  TextView ? = null
    }
}