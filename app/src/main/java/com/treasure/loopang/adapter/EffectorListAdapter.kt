package com.treasure.loopang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.listitem.EffectorListItem
import android.media.MediaPlayer
import java.io.IOException
import android.widget.TextView
import com.treasure.loopang.listitem.setEffector


class EffectorListAdapter : BaseAdapter() {
    var playedPos : Int = -1

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private var listViewItemList = ArrayList<EffectorListItem>()

    // Adapter에 사용되는 데이터의 개수
    override fun getCount(): Int {
        return listViewItemList.size
    }

    fun allStop() {
        listViewItemList.forEach { it.isPlayingMusic = false; it.music?.stop() }
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view : View
        val context = parent.context
        val effectorViewHolder : ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(com.treasure.loopang.R.layout.effectorlist, null)
            effectorViewHolder = ViewHolder()
            effectorViewHolder.titleTextView = view.findViewById(com.treasure.loopang.R.id.name_musical_instrument) as TextView
            effectorViewHolder.playbackBtnView = view.findViewById(com.treasure.loopang.R.id.btn_playback) as ImageButton

            view.tag = effectorViewHolder
        }else{
            effectorViewHolder = convertView.tag as ViewHolder
            view = convertView
            //viewHolder.playbackBtnView?.setImageDrawable(null)
        }

        effectorViewHolder.titleTextView?.setText(listViewItemList.get(position).title)

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem = listViewItemList[position]

        listViewItem.music?.setLooping(true)

        if(listViewItem.isPlayingMusic){
            effectorViewHolder.playbackBtnView!!.setImageResource(com.treasure.loopang.R.drawable.icon_stop__musical_instrument)
        } else {
            effectorViewHolder.playbackBtnView!!.setImageResource(com.treasure.loopang.R.drawable.icon_play__musical_instrument)
        }

        view.findViewById<View>(com.treasure.loopang.R.id.btn_playback).setOnClickListener{

            if(listViewItemList[position].isPlayingMusic == true) {
                musicStop(position) //지금 누른 버튼의 노래 멈추기
            }
            else{ //지금 누른 버튼의 노래 재생을 하되
                if(playedPos == -1){
                    musicPlay(position)
                    playedPos = position
                }
                else if(playedPos != -1){
                    musicStop(playedPos)
                    musicPlay(position)
                    playedPos = position
                }
            }
            notifyDataSetChanged()
        }
        view.findViewById<View>(com.treasure.loopang.R.id.btn_add).setOnClickListener{
        }
        return view
    }
    fun musicStop(position: Int){
        listViewItemList[position].isPlayingMusic = false
        listViewItemList[position].music?.stop()
        try {
            listViewItemList[position].music?.prepare()
        } catch (e: IllegalStateException) {
                e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun musicPlay(position: Int){
        listViewItemList[position].isPlayingMusic = true
        listViewItemList[position].music?.start()
        Thread()
    }
    fun positionMusicStop(){
        for(position in listViewItemList.indices){
            if(listViewItemList[position].isPlayingMusic == true){
                musicStop(position) }
        }
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
        var playbackBtnView : ImageButton? = null
        var titleTextView :TextView? = null
    }

}

