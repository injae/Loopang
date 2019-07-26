package com.treasure.loopang.listitem

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
        adapter.addItem("드럼 1")
        adapter.addItem("드럼 2")
        adapter.addItem("드럼 3")
        adapter.addItem("심벌 1")
        adapter.addItem("심벌 2")
        adapter.addItem("심벌 3")
        adapter.addItem("피아노 1")
        adapter.addItem("피아노 2")
         adapter.addItem("피아노 3")
           adapter.addItem("베이스 1")
          adapter.addItem("베이스 2")
         adapter.addItem("배이스 3")

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        effectorListView.setOnItemClickListener { parent, view, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as EffectorListItem
            val title = item.title
            // TODO : use item data.
        }
    }
}
