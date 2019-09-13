package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.treasure.loopang.listitem.settingItemNoticeListItem

class settingItemNoticeListAdapter : BaseAdapter() {

    private var listViewItemList = ArrayList<settingItemNoticeListItem>()

    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view : View
        val context = parent.context
        val NoticeViewHolder : ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.setting_notice_item, null)
            NoticeViewHolder = ViewHolder()
            NoticeViewHolder.noticeDateTextView = view.findViewById(com.treasure.loopang.R.id.notice_date) as TextView
            NoticeViewHolder.noticeTitleTextView = view.findViewById(com.treasure.loopang.R.id.notice_title) as TextView
            view.tag = NoticeViewHolder
        }else{
            NoticeViewHolder = convertView.tag as ViewHolder //viewHolder = convertView!!.getTag() as ViewHolder
            view = convertView
        }

        val listViewItem = listViewItemList[position]

        NoticeViewHolder.noticeTitleTextView?.setText(listViewItemList.get(position).noticeTitle)
        NoticeViewHolder.noticeDateTextView?.setText(listViewItemList.get(position).date)

        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    fun addItem(date: String, noticeContent: String, noticeTitle :String) {
        val item = settingItemNoticeListItem()
        item.date = date
        item.noticeContent = noticeContent
        item.noticeTitle = noticeTitle

        listViewItemList.add(item)
    }

    private  class ViewHolder{
        var noticeDateTextView : TextView? = null
        var noticeTitleTextView : TextView? = null
        var noticeContentTextView : TextView? = null
    }
}

