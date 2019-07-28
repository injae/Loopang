package com.treasure.loopang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.listitem.EffectorListItem
import kotlinx.android.synthetic.main.effectorlist.view.*
import android.media.MediaPlayer
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import android.widget.TextView



class EffectorListAdapter : BaseAdapter() {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private var listViewItemList = ArrayList<EffectorListItem>()

    // Adapter에 사용되는 데이터의 개수
    override fun getCount(): Int {
        return listViewItemList.size
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val context = parent.context

        val viewHolder : RecyclerView.ViewHolder

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(com.treasure.loopang.R.layout.effectorlist, parent, false)

            /*    viewHolder = ViewHolder()
            val imageBtn = viewHolder.imageBtnView?.findViewById<ImageButton>(com.treasure.loopang.R.id.btn_playback) as ImageButton
            //viewHolder.imageBtnView = convertView!!.findViewById(com.treasure.loopang.R.id.btn_playback) as ImageButton
            //var ImageBtnView = viewHolder.findViewById(com.treasure.loopang.R.id.btn_playback) as ImageButton
            view.tag = viewHolder
        }else{
            viewHolder = convertView!!.getTag() as ViewHolder
        }*/
        }


        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        val titleTextView = view?.findViewById(com.treasure.loopang.R.id.name_musical_instrument) as TextView

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem = listViewItemList[position]

        // 아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(listViewItem.title)

        listViewItem.music?.setLooping(true)

        view.findViewById<View>(com.treasure.loopang.R.id.btn_playback).setOnClickListener{
            //다른 노래 재생중인지 체크하기 isPlayingMusic
            for(position in listViewItemList.indices) {
                if (listViewItemList[position].isPlayingMusic == true) {
                    listViewItemList[position].music?.stop()
                    //position의 버튼에 어떻게 접근할까...??? 이미지 바꿔줘야 하는데 ㅋㅋ
                }
            }
            if (listViewItem.music!!.isPlaying()) {
                // 재생중이면 노래 정지
                listViewItem.isPlayingMusic = false
                 view.btn_playback.setImageResource(com.treasure.loopang.R.drawable.icon_play__musical_instrument)
                //viewHolder.imageBtnView!!.setImageResource(com.treasure.loopang.R.drawable.icon_play__musical_instrument)
                listViewItem.music?.stop()
                try {
                    listViewItem.music?.prepare()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                listViewItem.music?.seekTo(0)
            } else {
                // 재생중이 아니면 재생
                listViewItem.isPlayingMusic= true
                view.btn_playback.setImageResource(com.treasure.loopang.R.drawable.icon_stop__musical_instrument)
                //viewHolder.imageBtnView!!.setImageResource(com.treasure.loopang.R.drawable.icon_stop__musical_instrument)
                listViewItem.music?.start()
                Thread()
            }
        }
        view.findViewById<View>(com.treasure.loopang.R.id.btn_add).setOnClickListener{
            //recording 에 저장된 음원 추가되게 하는 기능 여기다 추가
        }

        return view
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    // 아이템 데이터
    fun addItem(title: String, music: MediaPlayer,  isPlayingMusic :Boolean) {
        val item = EffectorListItem()
        item.title = title
        item.music = music
        item.isPlayingMusic = isPlayingMusic

        listViewItemList.add(item)
    }

    private  class ViewHolder{
        var imageBtnView : ImageButton? = null
    }

}
