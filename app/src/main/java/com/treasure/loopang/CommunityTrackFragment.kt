package com.treasure.loopang

import android.os.Bundle
import android.os.CpuUsageInfo
import android.text.Editable
import android.text.LoginFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.audio.DownloadChecker
import com.treasure.loopang.audio.FileManager
import com.treasure.loopang.audio.Sound
import com.treasure.loopang.audio.convertBytesToShort
import com.treasure.loopang.communication.*
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_recording.*
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
        (activity as CommunityActivity).musicLikesNum = (activity as CommunityActivity).itt.likes

        for(layer in (activity as CommunityActivity).likeList){
       // for(layer in (com.treasure.loopang.communication.UserManager.getUser().likedList)){
            if(layer.id == (activity as CommunityActivity).itt.id){
                trackHeartClikedNum.setText(layer.likes.toString())
                downloadNumText.setText(layer.downloads.toString())
                heartState = true
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart_clicked))
                break
            }else {
                heartState = false
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart))
            }
        }
        val songMasteruserNickName : String = (activity as CommunityActivity).itt.owner
        val presentuserNickname : String = com.treasure.loopang.communication.UserManager.getUser().name
        Track_trackName.setText((activity as CommunityActivity).itt.name)
        trackInfoDate.setText((activity as CommunityActivity).itt.updated_date.substring(0,10))
        trackInfoText.setText((activity as CommunityActivity).itt.explanation)
        trackHeartClikedNum.setText((activity as CommunityActivity).itt.likes.toString())
        downloadNumText.setText((activity as CommunityActivity).itt.downloads.toString())

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
            (activity as CommunityActivity).musicId = (activity as CommunityActivity).itt.id
            (activity as CommunityActivity).isLikedDataChanged = true
            val connector = Connector()
            if(heartState == true) {
                heartState = false
                (activity as CommunityActivity).itt.likes -=1
                (activity as CommunityActivity).musicLikesNum = (activity as CommunityActivity).itt.likes
                GlobalScope.launch { connector.process(ResultManager.REQUEST_LIKE_DOWN, null, null, null, (activity as CommunityActivity).itt.id) }
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart))
                trackHeartClikedNum.setText((activity as CommunityActivity).itt.likes.toString())

               for(item in (activity as CommunityActivity).likeList) {
                  // Log.d("sssssssss비교", "likeItem id: " + item.id + " itt id: " + (activity as CommunityActivity).itt.id)
                   if (item.id == (activity as CommunityActivity).itt.id) {
                       (activity as CommunityActivity).likeList.remove((activity as CommunityActivity).itt)
                       // Log.d("sssssssss삭제","삭제 했어!")
                   }
               }
                Log.d("sssssssssSize 채워진상태 > 빔",""+(activity as CommunityActivity).likeList.size )
                    for(i in 0.. (activity as CommunityActivity).likeList.size-1){
                        Log.d("sssssssssName 채워진상태 > 빔"," " + (activity as CommunityActivity).likeList[i].name + " " +(activity as CommunityActivity).likeList[i].likes )
                    }
            } else {
                heartState = true
                (activity as CommunityActivity).itt.likes +=1
                (activity as CommunityActivity).musicLikesNum = (activity as CommunityActivity).itt.likes
                GlobalScope.launch { connector.process(ResultManager.REQUEST_LIKE_UP, null, null, null, (activity as CommunityActivity).itt.id) }
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart_clicked))
                trackHeartClikedNum.setText((activity as CommunityActivity).itt.likes.toString())
                (activity as CommunityActivity).likeList.add((activity as CommunityActivity).itt)
                Log.d("sssssssssSize: 빈상태>채워짐",""+(activity as CommunityActivity).likeList.size )
                for(i in 0.. (activity as CommunityActivity).likeList.size-1){
                    Log.d("sssssssssName 빈상태>채워짐"," " + (activity as CommunityActivity).likeList[i].name + " " +(activity as CommunityActivity).likeList[i].likes )
                }
            }
        }

        downloadButton.setOnClickListener {
            (activity as CommunityActivity).isDownloadDataChanged = true
            downloadChecker.download((activity as CommunityActivity).itt.name, (activity as CommunityActivity).itt.id, activity!!)
            (activity as CommunityActivity).itt.downloads += 1
            downloadNumText.setText((activity as CommunityActivity).itt.downloads.toString())
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

    override fun onDestroy() {
        Log.d("자동갱신테스트", "destroy track fragment")
        if((activity as CommunityActivity).isLikedDataChanged == true) {
            (activity as CommunityActivity).feedLikeList.clear()
            (activity as CommunityActivity).feedLikeList.addAll((activity as CommunityActivity).connector.feedResult!!.likes_top)
        }
        if((activity as CommunityActivity).isDownloadDataChanged == true){
            (activity as CommunityActivity).feedDownloadList.clear()
            (activity as CommunityActivity).feedDownloadList.addAll((activity as CommunityActivity).connector.feedResult!!.download_top)
            //정렬
            var k : Int = 0
            var tmp : MusicListClass
            for(i in 4.. 0) {
                tmp = (activity as CommunityActivity).feedDownloadList[i]
                k = i + 1
                while (k <= 5 && (activity as CommunityActivity).feedDownloadList[k].downloads > tmp.downloads) {
                    (activity as CommunityActivity).feedDownloadList[k - 1] = (activity as CommunityActivity).feedDownloadList[k]
                    k += 1
                }
                (activity as CommunityActivity).feedDownloadList[k - 1] = tmp
            }

            for(item in (activity as CommunityActivity).feedDownloadList) {
                Log.d("sssssssssdownload", "item : " + item.name +" download: "+item.downloads)
            }
        }
        val userFragment = (activity as CommunityActivity).pagerAdapter.getItem(1) as CommunityUserPageFragment
        userFragment.update()
        val feedFragment = (activity as CommunityActivity).pagerAdapter.getItem(0) as CommunityFeedFragment
        feedFragment.update()
        super.onDestroy()
    }
}