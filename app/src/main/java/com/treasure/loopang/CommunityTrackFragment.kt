package com.treasure.loopang

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.audio.DownloadChecker
import com.treasure.loopang.audio.FileManager
import com.treasure.loopang.audio.Sound
import com.treasure.loopang.audio.convertBytesToShort
import com.treasure.loopang.communication.Connector
import com.treasure.loopang.communication.ResultManager
import com.treasure.loopang.communication.makeSHA256
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_track.*
import kotlinx.coroutines.*
import java.io.File

class CommunityTrackFragment(var sound: Sound? = null, val downloadChecker: DownloadChecker = DownloadChecker(),
                             var statePlaying: Boolean = false): androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_track,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TrackInfoTextView.isEnabled = false
        trackInfoText.setEnabled(false)

        var heartState = false
        val songMasteruserNickName : String = (activity as CommunityActivity).itt.userNickName
        val presentuserNickname : String = com.treasure.loopang.communication.UserManager.getUser().name
        Track_trackName.setText((activity as CommunityActivity).itt.songName)
        trackInfoDate.setText((activity as CommunityActivity).itt.productionDate.substring(0,10))
        trackHeartClikedNum.setText((activity as CommunityActivity).itt.likedNum.toString())
        playNumText.setText((activity as CommunityActivity).itt.downloadNum.toString())

        Track_artistName.setText(songMasteruserNickName)
        layerTag.setText("TAG: "+ "")

        val musicID = (activity as CommunityActivity).itt.songId
        setSound(musicID)

        if(songMasteruserNickName == presentuserNickname) {
            trackInfoText.setEnabled(true); //사용자와 노래주인이 같으면 터치해서 info바꿀 수 있음
            trackInfoText.addTextChangedListener(object  : TextWatcher{
                override fun afterTextChanged(edit: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // var TrackInfo : String? = null 로 아이템 널어주기 ㅇㅇ
                }
            })
        }
        //var likedNum = (activity as CommunityActivity).itt.likedNum
        heartButton.setOnClickListener {
            val connector = Connector()
            if(heartState == false) {
                heartState = true
                //likedNum +=1
                (activity as CommunityActivity).itt.likedNum +=1
                //itt의 likedNum 을 다시 가져가주기
                GlobalScope.launch { connector.process(ResultManager.REQUEST_LIKE_UP, null, null, null, (activity as CommunityActivity).itt.songId) }
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart_clicked))
                trackHeartClikedNum.setText((activity as CommunityActivity).itt.likedNum.toString())
            }
            else {
                heartState = false
                //likedNum -=1
                (activity as CommunityActivity).itt.likedNum -=1
                GlobalScope.launch { connector.process(ResultManager.REQUEST_LIKE_DOWN, null, null, null, (activity as CommunityActivity).itt.songId) }
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart))
                //itt의 likedNum 을 다시 가져가주기
                trackHeartClikedNum.setText((activity as CommunityActivity).itt.likedNum.toString())
            }
        }

        downloadButton.setOnClickListener {
            //사용자의 recording item으로 song이 들어가게 하는 기능 추가
            downloadChecker.download((activity as CommunityActivity).itt.songName, (activity as CommunityActivity).itt.songId, activity!!)
            (activity as CommunityActivity).itt.downloadNum += 1
            playNumText.setText((activity as CommunityActivity).itt.downloadNum.toString())
        }

        Track_btn_play.setOnClickListener {
            if(statePlaying == false) {
                if(sound != null) { soundPlay() }
                else { downloadChecker.download(null, musicID, activity!!, this) }
                Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_pause))
                TrackBtnReplay.visibility = View.VISIBLE
                statePlaying =true
            }
            else { //statePlaying == true
                soundStop()
                Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_play))
                TrackBtnReplay.visibility = View.GONE
                statePlaying = false
            }
        }

        TrackBtnReplay.setOnClickListener { // 이거 필요없으니 지우셈
            Log.d("replay Btn" , "replay btn")
        }

        track_btn_back.setOnClickListener {   activity!!.TrackFrame.visibility = View.GONE
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
            (activity as CommunityActivity).isTrackFragOpen = false
        }
    }

    fun setSound(musicID: String) {
        if(downloadChecker.cacheCheck(musicID)) {
            val ff = File(FileManager().looperCacheDir.path + '/' + makeSHA256(musicID))
            val ms = ff.readBytes().toMutableList().chunked(2).map{ it.toByteArray() }.flatMap { listOf(convertBytesToShort(it)) }.toMutableList()
            sound = Sound(ms)
        }
    }

    fun soundPlay() {
        GlobalScope.launch {
            sound?.play()
            CoroutineScope(Dispatchers.Main).launch {
                if(statePlaying) {
                    Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_play))
                    TrackBtnReplay.visibility = View.GONE
                    statePlaying = false
                }
            }
        }
    }

    private fun soundStop() { GlobalScope.launch { sound?.stop() } }
}