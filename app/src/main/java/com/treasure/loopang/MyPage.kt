package com.treasure.loopang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.adapter.EffectorListAdapter
import com.treasure.loopang.adapter.MyPageAdapter
import com.treasure.loopang.listitem.EffectorListItem
import com.treasure.loopang.listitem.MyPageItem
import kotlinx.android.synthetic.main.my_page_frame.*
import kotlinx.android.synthetic.main.set_effector_frame.*

class MyPage : androidx.fragment.app.Fragment() {
    val adapter: MyPageAdapter =  MyPageAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.my_page_frame, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var loginState : Int = 0

        login_o.visibility=View.INVISIBLE
        login_x.visibility=View.VISIBLE

        if(loginState==1){
            login_o.visibility=View.VISIBLE
            login_x.visibility=View.INVISIBLE
        }
        mypagebtback.setOnClickListener{
            //fragment종료시키기 ^^,, 근데 휴대폰이 안떠서 잘 뜨는지도 모르겠어 ㅠㅠㅠㅠㅠㅠㅠ 몰라 뮨우ㅠㅁㄴ으ㅠㅠㅠ
            //finish()
        }

        MyPageListView.adapter =adapter

        //여기다가 만든 노래 데이터 추가하는 기능 넣어주면 됨.
      // adapter.addItem()

        MyPageListView.setOnItemClickListener { parent, view, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as MyPageItem
            val songName = item.songName
            val productionDate = item.productionDate

        }

    }
}