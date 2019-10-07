package com.treasure.loopang

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListView
import com.treasure.loopang.adapter.CommunityFeedItemAdapter
import com.treasure.loopang.adapter.CommunityUserPageAdapter
import com.treasure.loopang.adapter.settingItemNoticeListAdapter
import com.treasure.loopang.listitem.CommunitySongItem
import com.treasure.loopang.listitem.settingItemNoticeListItem
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_user_page.*
import kotlinx.android.synthetic.main.setting_notice.*

class CommunityUserPageFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.community_user_page,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPageListView: ListView = userpageListView
        val userPageItemAdapter: CommunityUserPageAdapter = CommunityUserPageAdapter()

        userPageListView.adapter = userPageItemAdapter

        //이거 track liked 따라서  additem달라지니까 함수로 구현해야함
        userPageItemAdapter.addItem("userName","songName1")
        userPageItemAdapter.addItem("userName","songName1")
        userPageItemAdapter.addItem("userName","songName1")
        userPageItemAdapter.addItem("userName","songName1")


        userPageTrackBtn.setOnClickListener {
            Log.d("Track Btn","Track Btn Cliked")
            userPageTrackBtn.setTextColor(Color.argb(200,115,115,115))
            userPageTrackBtn.setBackgroundColor(Color.WHITE)
            userPageLikedBtn.setBackgroundColor(Color.argb(0,0,0,0))
            userPageLikedBtn.setTextColor(Color.WHITE)
            //내가 공유한 음원들 리스트로 add item 만들어주기
            //userPageItemAdapter.addItem()
        }
        userPageLikedBtn.setOnClickListener{
            Log.d("Liked btn","Liked Btn Cliked")
            userPageLikedBtn.setTextColor(Color.argb(200,115,115,115))
            userPageLikedBtn.setBackgroundColor(Color.WHITE)
            userPageTrackBtn.setBackgroundColor(Color.argb(0,0,0,0))
            userPageTrackBtn.setTextColor(Color.WHITE)
            // liked 누른 음원들 리스트로 add item 만들어주기
          //  userPageItemAdapter.addItem()
        }
        //addTrackButton.setOnClickListener {  나중에 넣어 ^^}
        userPageListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as CommunitySongItem

            val songName = item.songName
            val userNickName = item.userNickName
            val downloadNum = item.downloadNum
            val likedNum = item.likedNum

            var trackFrame : FrameLayout = activity!!.TrackFrame
            trackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChanged(songName,userNickName ,0, 0) //likednum,downloadnum 넣어주기 ㅇㅇ
        }

        //userNickname.setText() 닉네임 변수 넣어주기
        //userSharedTrackNumber.setText((activity as CommunityActivity).userSharedTrackNum)
        //likenumber.setText((activity as CommunityActivity).likedSongNum)  //(activity as CommunityActivity).likedSongNum

        addTrackButton.setOnClickListener {
            //myPage Track List 나오게해야하나? 이거 아직 고민중임 ㅇㅇ
        }
    }
}