package com.treasure.loopang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.community_feed_item.*
import kotlinx.android.synthetic.main.community_track.*
import kotlinx.android.synthetic.main.community_track.view.*

class CommunityTrackFragment: androidx.fragment.app.Fragment() {
    var PlayedNum : Int = 0
    var StatePlaying : Boolean = false
    var heartState : Boolean = false
    var heartClikedNum : Int = 0
    var checkSongMaster : Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_track,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var songMasteruserNickName : String? = null   //변수들 나중에 다 change
        Track_artistName.setText(songMasteruserNickName)

        var trackName : String? = null //변수들 나중에 다 change
        Track_trackName.setText(trackName)

        var songInfoDate : String? = null //이건 String이 맞는지도 모르겠다 일단 이렇게 해놓자 ^^
        trackInfoDate.setText(songInfoDate)

        var informationText: String = ""
        trackInfoText.setText(informationText)

        var presentuserNickname : String? = null //변수들 나중에 다 change
        if(songMasteruserNickName == presentuserNickname){
            //edit text 가능하게 넣어줘라~~~~
        }

        heartButton.setOnClickListener {
            if(heartState == false) {
                heartState = true
                heartClikedNum += 1
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart_clicked))
            }
            else {
                heartState = false
                heartClikedNum -=1
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart))
            }
        }
        downloadButton.setOnClickListener {
            //사용자의 recording item으로 song이 들어가게 하는 기능 추가
        }

        if(StatePlaying == true) {
            Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_pause))
            TrackBtnReplay.visibility = View.VISIBLE
        }
        else {
            Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_play))
            TrackBtnReplay.visibility = View.INVISIBLE
        }
        TrackBtnReplay.setOnClickListener {
            // 음원의 재생을 처음으로 돌리기 기능
        }

        Track_btn_play.setOnClickListener {
            if(StatePlaying == true) StatePlaying =false
            else {
                StatePlaying = true
            }
            //끝까지 들어야지 재생횟수 주는거야? 그거 고민좀 해봐 ㅇㅇ PlayedNum  +=1

            playNumText.setText(PlayedNum.toString())
            //재생 되는 기능 추가
        }

        track_btn_back.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
        }
    }
}