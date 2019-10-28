package com.treasure.loopang

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_feed_item.*
import kotlinx.android.synthetic.main.community_track.*
import kotlinx.android.synthetic.main.community_track.view.*
import kotlinx.android.synthetic.main.setting_item_back.view.*

class CommunityTrackFragment: androidx.fragment.app.Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_track,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TrackInfoTextView.isEnabled = false
        trackInfoTextEdit.setEnabled(false)

        var StatePlaying : Boolean = false
        var heartState :Boolean = false
        var songMasteruserNickName : String = (activity as CommunityActivity).itt.userNickName
        var presentuserNickname : String = com.treasure.loopang.communication.UserManager.getUser().name
        Track_trackName.setText((activity as CommunityActivity).itt.songName)
        trackInfoDate.setText((activity as CommunityActivity).itt.productionDate)
        trackHeartClikedNum.setText((activity as CommunityActivity).itt.likedNum.toString())
        Track_artistName.setText(songMasteruserNickName)

        if(songMasteruserNickName == presentuserNickname) {
            trackInfoTextEdit.setEnabled(true); //사용자와 노래주인이 같으면 터치해서 info바꿀 수 있음
            trackInfoTextEdit.addTextChangedListener(object  : TextWatcher{
                override fun afterTextChanged(edit: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // var TrackInfo : String? = null 로 아이템 널어주기 ㅇㅇ
                }
            })
        }
        heartButton.setOnClickListener {
            if(heartState == false) {
                heartState = true
                (activity as CommunityActivity).itt.likedNum += 1
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart_clicked))
                trackHeartClikedNum.setText((activity as CommunityActivity).itt.likedNum.toString())
            }
            else {
                heartState = false
                (activity as CommunityActivity).itt.likedNum -= 1
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart))
                trackHeartClikedNum.setText((activity as CommunityActivity).itt.likedNum.toString())
            }
        }
        downloadButton.setOnClickListener {
            //사용자의 recording item으로 song이 들어가게 하는 기능 추가
            Log.d("download","download")
            (activity as CommunityActivity).itt.downloadNum += 1
            playNumText.setText((activity as CommunityActivity).itt.downloadNum.toString())
        }
        Track_btn_play.setOnClickListener {
            if(StatePlaying == false) {
                Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_pause))
                TrackBtnReplay.visibility = View.VISIBLE
                StatePlaying =true
                Log.d("pause btn replay o","pause btn > replay o")
            }
            else { //statePlaying == true
                Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_play))
                TrackBtnReplay.visibility = View.GONE
                StatePlaying = false
                Log.d("playBtn > replay x","playBtn > replay x")
            }
        }
        TrackBtnReplay.setOnClickListener { // 음원의 재생을 처음으로 돌리기 기능
            Log.d("replay Btn" , "replay btn")
        }
        track_btn_back.setOnClickListener {   activity!!.TrackFrame.visibility = View.GONE
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
            (activity as CommunityActivity).isTrackFragOpen = false
        }
    }
}