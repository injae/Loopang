package com.treasure.loopang.listitem

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.adapter.EffectorListAdapter
import kotlinx.android.synthetic.main.set_effector_frame.*
import android.util.Log

class setEffector : androidx.fragment.app.Fragment() {
    val adapter: EffectorListAdapter =  EffectorListAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.set_effector_frame,container,false);
    }
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 리스트뷰 참조 및 Adapter달기
        effectorListView.adapter =adapter

        //아이템 추가
       //music = MediaPlayer.create(context, com.treasure.loopang.R.raw.soap)
       //        music.setLooping(true)

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        effectorListView.setOnItemClickListener { parent, view, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as EffectorListItem
            val title = item.title
            val music = item.music
            val isPlayingMusic = item.isPlayingMusic
            // TODO : use item data.
        }
    }
}
