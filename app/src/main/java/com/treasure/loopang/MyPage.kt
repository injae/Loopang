package com.treasure.loopang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.adapter.MyPageAdapter
import com.treasure.loopang.listitem.MyPageItem
import kotlinx.android.synthetic.main.my_page_frame.*
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.setting_my_music_login_x.*

class MyPage : androidx.fragment.app.Fragment() {

    val adapter: MyPageAdapter =  MyPageAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(com.treasure.loopang.R.layout.my_page_frame, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var userNickname : TextView = view.findViewById(com.treasure.loopang.R.id.my_page_user_nickname)
        var useridEmail : TextView = view.findViewById(com.treasure.loopang.R.id.my_page_user_id_email)

        if( com.treasure.loopang.communication.UserManager.isLogined == true) {
            login_o.visibility=View.VISIBLE
            login_x.visibility=View.GONE
            //my_page_sign_in_button.setOnClickLister{
            var loginEmailView : EditText = view.findViewById(com.treasure.loopang.R.id.edit_login_id)
            var loginPasswordView : EditText = view.findViewById(com.treasure.loopang.R.id.edit_login_password)
            my_page_sign_in_button.setOnClickListener {

            }
            my_page_sign_up_button.setOnClickListener {

            }
        }else{
            login_o.visibility = View.GONE
            login_x.visibility = View.VISIBLE
           userNickname.setText( com.treasure.loopang.communication.UserManager.getUser().name)
            useridEmail.setText(com.treasure.loopang.communication.UserManager.getUser().email)
        }

     /*   com.treasure.loopang.communication.UserManager.getUser().email
        com.treasure.loopang.communication.UserManager.getUser().name*/

        MyPageListView.adapter =adapter

        var i : Int = 0;
        var checkLastItem :Boolean =false

        adapter.addItem("song 1","yy:mm:dd:tt:mm:ss")
        adapter.addItem("song 2","yy:mm:dd:tt:mm:ss")
        adapter.addItem("song 3","yy:mm:dd:tt:mm:ss")
        //여기다가 만든 노래 데이터 추가하는 기능 넣어주면 됨.

        MyPageListView.setOnItemClickListener { parent, view, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as MyPageItem
            val songName = item.songName
            val productionDate = item.productionDate
                //노래 들려줘라 ㅎㅅㅎ
            }
        }
    fun TranslateView(direction : Animation, isViewVisible : Boolean,view: View){
        if(!isViewVisible) view.findViewById<View>(com.treasure.loopang.R.id.checkView).visibility = View.VISIBLE
        else view.findViewById<View>(com.treasure.loopang.R.id.checkView).visibility = View.INVISIBLE
        view.findViewById<View>(com.treasure.loopang.R.id.checkView).startAnimation(direction)
    }
}
