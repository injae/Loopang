package com.treasure.loopang.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.listitem.SettingItem
import android.widget.*
import java.util.*


class SettingAdapter : BaseAdapter(),Filterable{

    private var listViewItemList = ArrayList<SettingItem>()
    private var filteredItemList= listViewItemList   //필터링 된 데이터 저장 하기위한 리스트 //최초에 전체 리스트 보유

    var listFilter: Filter? = null

    override fun getFilter(): Filter {
        if (listFilter == null) {
            listFilter = ListFilter()
        }
        return listFilter!! //원래는 !!없었어..
    }

    private inner class ListFilter : Filter() {
        override  protected fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint == null || constraint.length == 0) {
                results.values = listViewItemList
                results.count = listViewItemList.size
            } else {
                var itemList = ArrayList<SettingItem>()

                for (item in listViewItemList) {
                    if (item.title!!.toUpperCase().contains(constraint.toString().toUpperCase()))
                        itemList.add(item)
                }

                results.values = itemList
                results.count = itemList.size
            }
            return results
        }
        //protected
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            // update listview by filtered data list.
            filteredItemList = results.values as ArrayList<SettingItem>

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

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(com.treasure.loopang.R.layout.settinglist_item, parent, false)
        }

        val iconImageView = view?.findViewById(com.treasure.loopang.R.id.settingListImage) as ImageView
        val titleTextView = view?.findViewById(com.treasure.loopang.R.id.settingListText) as TextView

        val listViewItem = filteredItemList[position]

        iconImageView.setImageDrawable(listViewItem.icon)
        titleTextView.setText(listViewItem.title)

        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
/*
    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }*/
    override fun getItem(position: Int): Any {
        return filteredItemList[position]
    }
    // 아이템 데이터
    fun addItem(icon: Drawable,  title: String) {
        val item = SettingItem()

        item.icon = icon
        item.title = title

        listViewItemList.add(item)
    }
}