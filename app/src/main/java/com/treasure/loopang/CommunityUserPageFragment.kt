package com.treasure.loopang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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

        userPageTrackBtn.setOnClickListener {
            /*
             userPageTrackBtn.setBackgroundColor(FFFFFF)
            userPageLikedBtn.setBackgroundColor("@android:color/transparent")
            userPageLikedBtn.setTextColor()  FFFFFF
            userPageTrackBtn.setTextColor() "#737373"
            내가 공유한 음원들 리스트로 add item 만들어주기
            userPageItemAdapter.addItem()
            */
        }
        userPageLikedBtn.setOnClickListener{
           /*
           색깔 하기 넘 귀찮당 userPageTrackBtn.setBackgroundColor("@android:color/transparent")
            userPageLikedBtn.setBackgroundColor(FFFFFF)
            userPageLikedBtn.setTextColor()"#737373"
            userPageTrackBtn.setTextColor() FFFFFF
            liked 누른 음원들 리스트로 add item 만들어주기
            userPageItemAdapter.addItem()
            */

        }
        userPageListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as CommunitySongItem

            val userNickname = item.userNickName
            val songName = item.songName

            // Track Item 클릭 시 아이템의 Track Fragment로 이동
            (activity as CommunityActivity).onFragmentChanged(3,songName,userNickname)
        }

        //userNickname.setText() 닉네임 변수 넣어주기
       // userSharedTrackNumber.setText((activity as CommunityActivity).userSharedTrackNum)
       // likenumber.setText((activity as CommunityActivity).likedSongNum)  //(activity as CommunityActivity).likedSongNum

        addTrackButton.setOnClickListener {
            //myPage Track List 나오게해야하나? 이거 아직 고민중임 ㅇㅇ
        }
    }
}