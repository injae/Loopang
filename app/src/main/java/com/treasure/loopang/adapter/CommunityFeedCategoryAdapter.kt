package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.listitem.CommunityFeedCategoryItem
import java.util.ArrayList


class CommunityFeedCategoryAdapter : BaseAdapter() {

    private var listViewItemList = ArrayList<CommunityFeedCategoryItem>()
    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view : View
        val context = parent.context
        val categoryViewHolder : ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.community_feed_item_category, null)
            categoryViewHolder = ViewHolder()
            categoryViewHolder.categoryNameTextView = view.findViewById(com.treasure.loopang.R.id.FeedCategoryName) as TextView


            view.tag =  categoryViewHolder
        }else{
            categoryViewHolder = convertView.tag as ViewHolder
            view = convertView
        }

        categoryViewHolder.categoryNameTextView?.setText(listViewItemList.get(position).categoryName)

        val listViewItem = listViewItemList[position]
        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    fun addItem( categoryName : String) {
        val item = CommunityFeedCategoryItem()
        item.categoryName = categoryName
        listViewItemList.add(item)
    }

    private  class ViewHolder{
        var categoryNameTextView: TextView? = null
    }
}