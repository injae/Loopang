package com.treasure.loopang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.setting_my_music.*
import kotlinx.android.synthetic.main.setting_my_music_login_o.*
import kotlinx.android.synthetic.main.setting_my_music_login_o.view.*
import kotlinx.android.synthetic.main.setting_my_music_login_x.*
import java.util.zip.Inflater

class settingItemMyMusicFragment :androidx.fragment.app.Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.setting_my_music,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var checkLogin : Boolean = false
        var userNickname : String = "user name" //변수 바꿔라 ~!!~!~!~!~!
        var useridEmail : String = " 000000@gmail.com"
        my_music_user_nickname.text = userNickname

        when(checkLogin){
            false -> {
                layoutInflater.inflate(R.layout.setting_my_music_login_x, null)

                my_music_user_nickname.setText(userNickname)
                myMusicUserEmail.setText(useridEmail)

            }
            true -> {
                layoutInflater.inflate(R.layout.setting_my_music_login_o, null)
                //로그인 기능 여기다 추가
/*
                my_music_login_button.setOnClickListener {
                  //  onLoginButtonClick()
                }*/
            }
        }



    }
}