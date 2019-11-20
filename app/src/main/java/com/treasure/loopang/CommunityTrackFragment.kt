package com.treasure.loopang

import android.os.Bundle
import android.os.CpuUsageInfo
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
import com.treasure.loopang.communication.decodeUTF_8
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
        (activity as CommunityActivity).isTrackFragOpen = true
        TrackInfoTextView.isEnabled = false
        trackInfoText.setEnabled(false)
        var heartState : Boolean = false

        for(layer in (activity as CommunityActivity).likeList){
       // for(layer in (com.treasure.loopang.communication.UserManager.getUser().likedList)){
            if(layer.id == (activity as CommunityActivity).itt.id){
                heartState = true
                Log.d("hhhhhhhhhhh","heart in list")
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart_clicked))
                break
            }else {
                heartState = false
                Log.d("hhhhhhhhhhh","heart isnt in list, item's heart state is "+ heartState)
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart))
            }
        }
        val songMasteruserNickName : String = (activity as CommunityActivity).itt.owner
        val presentuserNickname : String = com.treasure.loopang.communication.UserManager.getUser().name
        Track_trackName.setText((activity as CommunityActivity).itt.name)
        trackInfoDate.setText((activity as CommunityActivity).itt.updated_date.substring(0,10))
        trackInfoText.setText((activity as CommunityActivity).itt.explanation)
        trackHeartClikedNum.setText((activity as CommunityActivity).itt.likes.toString())
        playNumText.setText((activity as CommunityActivity).itt.downloads.toString())

        Track_artistName.setText(songMasteruserNickName)
        var Tag = ""
        (activity as CommunityActivity).itt.tags
        for(tag in (activity as CommunityActivity).itt.tags!!){
            decodeUTF_8(tag)
            if(Tag == "") Tag = tag
            else Tag = Tag + ", " + tag
        }
        layerTag.setText("TAG: "+ Tag)

        val musicID = (activity as CommunityActivity).itt.id
        setSound(musicID)

       /* if(songMasteruserNickName == presentuserNickname) {
            trackInfoText.setEnabled(true);
            trackInfoText.addTextChangedListener(object  : TextWatcher{
                override fun afterTextChanged(edit: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }
            })
        }*/

        heartButton.setOnClickListener {
            (activity as CommunityActivity).isLikedDataChanged = true
            val connector = Connector()
            if(heartState == true) {
                heartState = false
                (activity as CommunityActivity).itt.likes -=1
                GlobalScope.launch { connector.process(ResultManager.REQUEST_LIKE_DOWN, null, null, null, (activity as CommunityActivity).itt.id) }
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart))
                trackHeartClikedNum.setText((activity as CommunityActivity).itt.likes.toString())
                (activity as CommunityActivity).likeList.remove((activity as CommunityActivity).itt)
            } else {
                heartState = true
                (activity as CommunityActivity).itt.likes +=1
                GlobalScope.launch { connector.process(ResultManager.REQUEST_LIKE_UP, null, null, null, (activity as CommunityActivity).itt.id) }
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart_clicked))
                trackHeartClikedNum.setText((activity as CommunityActivity).itt.likes.toString())
                (activity as CommunityActivity).likeList.add((activity as CommunityActivity).itt)
            }
        }

        downloadButton.setOnClickListener {
            downloadChecker.download((activity as CommunityActivity).itt.name, (activity as CommunityActivity).itt.id, activity!!)
            (activity as CommunityActivity).itt.downloads += 1
            playNumText.setText((activity as CommunityActivity).itt.downloads.toString())
        }

        Track_btn_play.setOnClickListener {
            if(statePlaying == false) {
                if(sound != null) { soundPlay() }
                else { downloadChecker.download(null, musicID, activity!!, this) }
                Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_pause))
                statePlaying =true
            } else {
                soundStop()
                Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_play))
                statePlaying = false
            }
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
                    statePlaying = false
                }
            }
        }
    }

    private fun soundStop() { GlobalScope.launch { sound?.stop() } }
}