package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.audio.LoopMusic
import com.treasure.loopang.listitem.CommunityShareItem
import kotlinx.android.synthetic.main.project_item.view.*
import java.util.ArrayList


class CommunityShareAdapter : BaseAdapter() {
    private var listViewItemList = ArrayList<CommunityShareItem>()
    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view : View
        val context = parent.context
        val ViewHolder : ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.project_item, null)
            ViewHolder = ViewHolder()
            ViewHolder.loopTitleView = view.findViewById(com.treasure.loopang.R.id.txt_title) as TextView
            ViewHolder.fileTypeView = view.txt_type as TextView
            ViewHolder.dateStringView = view.findViewById(com.treasure.loopang.R.id.txt_date) as TextView

            view.tag =  ViewHolder
        }else{
           ViewHolder = convertView.tag as ViewHolder
            view = convertView
        }

        ViewHolder.dateStringView?.setText(listViewItemList.get(position).dateString)
        ViewHolder.loopTitleView?.setText(listViewItemList.get(position).loopTitle)
        ViewHolder.fileTypeView?.setText(listViewItemList[position].fileType)

        val listViewItem = listViewItemList[position]
        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    fun addItem(loopTitle : String , dateString : String ) {
        val item = CommunityShareItem()
        //, filePath : String,childItem : MutableList<LoopMusic>
        item.loopTitle = loopTitle
       // item.childItems = childItem
        item.dateString = dateString
        //item.filePath =filePath

        listViewItemList.add(item)
    }

    fun addItem(loopMusic: LoopMusic) {
        val item = CommunityShareItem()
        item.loopTitle = loopMusic.name
        item.dateString = loopMusic.date
        item.extension = loopMusic.type
        if(loopMusic.child != null) item.fileType = "Project"
        else item.fileType = "Layer"
        item.childItems = loopMusic.child
        listViewItemList.add(item)
    }

    private  class ViewHolder{
        var loopTitleView : TextView? = null
        var dateStringView : TextView? = null
        var fileTypeView: TextView? = null
    }
}