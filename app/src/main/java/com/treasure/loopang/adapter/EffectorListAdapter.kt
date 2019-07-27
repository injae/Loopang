package com.treasure.loopang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.listitem.EffectorListItem
import kotlinx.android.synthetic.main.effectorlist.view.*
import android.media.MediaPlayer
import com.treasure.loopang.audio.Music
import java.io.IOException


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

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(com.treasure.loopang.R.layout.effectorlist, parent, false)
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        val titleTextView = view?.findViewById(com.treasure.loopang.R.id.name_musical_instrument) as TextView

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem = listViewItemList[position]

        // 아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(listViewItem.title)

        //얘네를 이제 매개변수로 받아와서 music하나하나를 설정해줘야지..
        /*val music: MediaPlayer
        music = MediaPlayer.create(context, com.treasure.loopang.R.raw.soap)
        music.setLooping(true)*/
        listViewItem.music?.setLooping(true)

        var NumOfClick: Int = 0
        view.findViewById<View>(com.treasure.loopang.R.id.btn_playback).setOnClickListener{
            NumOfClick = 1 - NumOfClick;
            if (listViewItem.music!!.isPlaying()) {
                // 재생중이면 노래 정지 //일시정지?pause()
                view.btn_playback.setImageResource(com.treasure.loopang.R.drawable.icon_play__musical_instrument)
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
                view.btn_playback.setImageResource(com.treasure.loopang.R.drawable.icon_stop__musical_instrument)
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
    fun addItem(title: String, music: MediaPlayer) {
        val item = EffectorListItem()
        item.title = title
        item.music = music

        listViewItemList.add(item)
    }
}