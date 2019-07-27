package com.treasure.loopang.listitem

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.treasure.loopang.R
import com.treasure.loopang.adapter.EffectorListAdapter
import kotlinx.android.synthetic.main.set_effector_frame.*

class setEffector : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_effector_frame,container,false);

    }
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter: EffectorListAdapter
        adapter = EffectorListAdapter()

        // 리스트뷰 참조 및 Adapter달기
        effectorListView.adapter =adapter

        //아이템 추가
       //music = MediaPlayer.create(context, com.treasure.loopang.R.raw.soap)
       //        music.setLooping(true)

       adapter.addItem("생방송", MediaPlayer.create(context, com.treasure.loopang.R.raw.live_broadcast))
       adapter.addItem("니소식",MediaPlayer.create(context, com.treasure.loopang.R.raw.your_news))
       adapter.addItem("soap",MediaPlayer.create(context, com.treasure.loopang.R.raw.soap))
       adapter.addItem("연인",MediaPlayer.create(context, com.treasure.loopang.R.raw.lover))
       adapter.addItem("야생화",MediaPlayer.create(context, com.treasure.loopang.R.raw.a_wild_flower))
       adapter.addItem("초록빛",MediaPlayer.create(context, com.treasure.loopang.R.raw.green_color))
       adapter.addItem("123", MediaPlayer.create(context, com.treasure.loopang.R.raw.live_broadcast))
       adapter.addItem("456",MediaPlayer.create(context, com.treasure.loopang.R.raw.your_news))
       adapter.addItem("789",MediaPlayer.create(context, com.treasure.loopang.R.raw.soap))
       adapter.addItem("101",MediaPlayer.create(context, com.treasure.loopang.R.raw.lover))
       adapter.addItem("112",MediaPlayer.create(context, com.treasure.loopang.R.raw.a_wild_flower))
       adapter.addItem("131",MediaPlayer.create(context, com.treasure.loopang.R.raw.green_color))

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        effectorListView.setOnItemClickListener { parent, view, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as EffectorListItem
            val title = item.title
            // TODO : use item data.
        }
    }
}
