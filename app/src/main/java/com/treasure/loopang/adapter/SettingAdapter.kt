package com.treasure.loopang.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.treasure.loopang.R
import com.treasure.loopang.listitem.SettingItem

class SettingAdapter : BaseAdapter() {

    private var listViewItemList = ArrayList<SettingItem>()

    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val context = parent.context

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.settinglist_item, parent, false)
        }

        val iconImageView = view?.findViewById(R.id.settingListImage) as ImageView
        val titleTextView = view?.findViewById(R.id.settingListText) as TextView

        val listViewItem = listViewItemList[position]

        iconImageView.setImageDrawable(listViewItem.icon)
        titleTextView.setText(listViewItem.title)

        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    // 아이템 데이터
    fun addItem(icon: Drawable,  title: String) {
        val item = SettingItem()

        item.icon = icon
        item.title = title

        listViewItemList.add(item)
    }
}