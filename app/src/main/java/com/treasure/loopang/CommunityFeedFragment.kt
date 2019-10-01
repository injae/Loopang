package com.treasure.loopang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.treasure.loopang.adapter.CommunityFeedItemAdapter
import com.treasure.loopang.adapter.settingItemNoticeListAdapter
import com.treasure.loopang.listitem.CommunityFeedItem
import com.treasure.loopang.listitem.settingItemNoticeListItem
import kotlinx.android.synthetic.main.community_feed.*
import kotlinx.android.synthetic.main.setting_notice.*

class CommunityFeedFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_feed,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val FeedListView: ListView = communityFeedListView
        val FeedAdapter: CommunityFeedItemAdapter = CommunityFeedItemAdapter()

        FeedListView.adapter = FeedAdapter
        // 전체 리스트 가져와서 add item >> (activity as CommunityActivity)  >> 전체 공유ㄴ된 음원 큐 아직 안만듬
//        FeedAdapter.addItem(


        FeedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as CommunityFeedItem

            val songName = item.songName
            val userNickName = item.userNickName
            val likedNum = item.likedNum
            val  playedNum = item.playedNum

            (activity as CommunityActivity).onFragmentChanged(1,songName,userNickName)
        }
    }
    fun addTrackItem(){
        //최신 공유된 track들 자동 additem 되도록 함수 만들기
    }
}