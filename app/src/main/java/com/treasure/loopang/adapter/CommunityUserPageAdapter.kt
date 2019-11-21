package com.treasure.loopang.adapter

import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.R
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.listitem.CommunitySongItem
import org.w3c.dom.Text
import java.util.ArrayList


class CommunityUserPageAdapter : BaseAdapter() {
    var btnSort = ""
    var listViewItemList = ArrayList<MusicListClass>()
    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view : View
        val context = parent.context
        val userPageViewHolder : ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.community_user_page_item, null)
            userPageViewHolder = ViewHolder()
            userPageViewHolder.songNameTextView= view.findViewById(com.treasure.loopang.R.id.myPageSongName) as TextView
            userPageViewHolder.productionDateTextView = view.findViewById(com.treasure.loopang.R.id.myPageProductionDate) as TextView
            /*레이어 삭제용 코드
            if(btnSort == "Track"){
                userPageViewHolder.btnForDelete = view.findViewById(com.treasure.loopang.R.id.layerListDeleteButton) as ImageButton
            }*/

            view.tag =  userPageViewHolder
        }else{
            userPageViewHolder = convertView.tag as ViewHolder
            view = convertView
        }

        userPageViewHolder.productionDateTextView?.setText(listViewItemList.get(position).updated_date.substring(0,10))
        userPageViewHolder.songNameTextView?.setText(listViewItemList.get(position).name)

        /*레이어 삭제 용 코드
        if(btnSort == "Track") {
            userPageViewHolder.btnForDelete!!.visibility = View.VISIBLE
        }
        userPageViewHolder.btnForDelete?.setOnClickListener {
           //이거 클릭하면 아래에서 빵긋 하고 나와서 삭제할거임? 이거 얘기 하기 ㅇㅇ
        }*/
        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

   fun addItem(music: MusicListClass, isBtnStateTrack:Boolean) {
       if(isBtnStateTrack) btnSort = "Track"
       else btnSort = "Liked"
       listViewItemList.add(music)
   }
    private  class ViewHolder{
       // var btnForDelete : ImageButton? = null
        var productionDateTextView : TextView? = null
        var songNameTextView :TextView? = null
    }
}