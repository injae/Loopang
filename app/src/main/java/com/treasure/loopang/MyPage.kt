package com.treasure.loopang

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.treasure.loopang.adapter.MyPageAdapter
import com.treasure.loopang.listitem.MyPageItem
import kotlinx.android.synthetic.main.my_page_frame.*
import kotlinx.android.synthetic.main.my_page_track_list.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout


class MyPage : androidx.fragment.app.Fragment() {

    val adapter: MyPageAdapter =  MyPageAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(com.treasure.loopang.R.layout.my_page_frame, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var loginState : Boolean = false
        var userNickname : String = "user name" //변수 바꿔라 ~!!~!~!~!~!
        var useridEmail : String = " 000000@gmail.com"

        login_o.visibility=View.GONE
        login_x.visibility=View.VISIBLE

        if(loginState==true){
            login_o.visibility=View.VISIBLE
            login_x.visibility=View.GONE
        }


        // var shareButton : ImageButton = view.findViewById<ImageButton>(com.treasure.loopang.R.id.myPageBtnShare)
        MyPageListView.adapter =adapter

        var i : Int = 0;
        var checkLastItem :Boolean =false

        /*
        while(true){  // 리스트 아이템이 끝날 때 까지

            var songName : String = getSongName(i)
            var productionDate :String = getProductionDate(i)
        //리스트 아이템의 songName 과 productionDate 추가해주기
            adapter.addItem(songName,productionDate)

            i++
        }
*/
        adapter.addItem("song 1","yy:mm:dd:tt:mm:ss")
        adapter.addItem("song 2","yy:mm:dd:tt:mm:ss")
        adapter.addItem("song 3","yy:mm:dd:tt:mm:ss")
        //여기다가 만든 노래 데이터 추가하는 기능 넣어주면 됨.
      // adapter.addItem()


        MyPageListView.setOnItemClickListener { parent, view, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as MyPageItem
            val songName = item.songName
            val productionDate = item.productionDate
         //   val shareBtn = item.btnShare

        /*    shareBtn!!.setOnClickListener {
                val animTransUp : Animation = AnimationUtils.loadAnimation(context, com.treasure.loopang.R.anim.translate_up)
                val animTransDown : Animation = AnimationUtils.loadAnimation(context, com.treasure.loopang.R.anim.translate_down)
                TranslateView(animTransUp,false,view)


                view.findViewById<View>(com.treasure.loopang.R.id.shareViewBtn_ok).setOnClickListener {
                    TranslateView(animTransDown,true,view)

                }
                view.findViewById<View>(com.treasure.loopang.R.id.shareViewBtn_cancel).setOnClickListener {
                    TranslateView(animTransDown,true,view)
                    }
                }*/
            }
        }
    fun TranslateView(direction : Animation, isViewVisible : Boolean,view: View){
        if(!isViewVisible) view.findViewById<View>(com.treasure.loopang.R.id.checkView).visibility = View.VISIBLE
        else view.findViewById<View>(com.treasure.loopang.R.id.checkView).visibility = View.INVISIBLE
        view.findViewById<View>(com.treasure.loopang.R.id.checkView).startAnimation(direction)
    }

    /*
    fun  getSongName( i: Int):String{
        var MySongList = listOf<String>("안","녕","하","세","요")
        if()
        return MySongList[i]
    }
    fun getProductionDate() : String {

        return
    }*/
}



/*  listViewItem.btnShare!!.setOnClickListener {
            ObjectAnimator.ofFloat(view.findViewById<View>(com.treasure.loopang.R.id.checkView), "translationY", -100f).apply {
                duration = 300
                start()
            }

            view.findViewById<View>(com.treasure.loopang.R.id.checkView).visibility=View.VISIBLE

        }*/