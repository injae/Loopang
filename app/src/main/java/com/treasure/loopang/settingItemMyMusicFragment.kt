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
import kotlinx.android.synthetic.main.setting_my_music_login_x.*
import java.util.zip.Inflater

class settingItemMyMusicFragment :androidx.fragment.app.Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.setting_my_music,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("확인","My Music 들어왔다 축하 ㅠㅠ")
        var checkLogin : Boolean = false
        var userNickname : String = "user name"
        user_nickname.text = userNickname

        when(checkLogin){
            false -> {
                myPageLoginX.visibility = View.VISIBLE
                myPageLoginO.visibility =View.INVISIBLE
                layoutInflater.inflate(R.layout.setting_my_music_login_x, null)
            }
            true -> {
                myPageLoginX.visibility = View.INVISIBLE
                myPageLoginO.visibility =View.VISIBLE
                layoutInflater.inflate(R.layout.setting_my_music_login_o, null)

                /* edit Text로 아이디 비번 받고 아이디text와 비번 text 를 서버 정보랑 비교해서 맞으면 로그인 되야함 근데 그건 알아서 하시려나 아 구조나 만들자
                if(edit_id.text.length == 0)
                {

                }
                else { //text가 뭔가 써진거지 길이가 0이아니면 근데 이걸로하면 안돼ㅔ

                }*/
                BtnmyPageLogIn.setOnClickListener {
                    //로그인 버튼
                }
            }
        }


    }
}