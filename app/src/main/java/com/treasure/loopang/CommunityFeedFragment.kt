package com.treasure.loopang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.treasure.loopang.adapter.CommunityFeedItemAdapter
import com.treasure.loopang.listitem.CommunitySongItem
import kotlinx.android.synthetic.main.community_feed.*
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.treasure.loopang.adapter.CommunityFeedCategoryAdapter
import com.treasure.loopang.communication.ASyncer
import com.treasure.loopang.listitem.CommunityFeedCategoryItem
import kotlinx.android.synthetic.main.activity_community.*
import java.util.*
import kotlin.collections.ArrayList

class CommunityFeedFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_feed,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if((activity as CommunityActivity).isTrackFragOpen == false && (activity as CommunityActivity).isCategorySelected == false) {
            communityFeedListView.visibility = View.GONE
            communityFeedCategoryListView.visibility = View.VISIBLE
            Log.d("aaaaaaaaaaaaaaaaaaaa","Category는"+(activity as CommunityActivity).isCategorySelected +"Track은"+(activity as CommunityActivity).isTrackFragOpen)
        }
        else if((activity as CommunityActivity).isTrackFragOpen == false && (activity as CommunityActivity).isCategorySelected == true){
            Log.d("bbbbbbbbbbbbbb","Category는"+(activity as CommunityActivity).isCategorySelected +"Track은"+(activity as CommunityActivity).isTrackFragOpen)
        }

        val CategoryAdapter: CommunityFeedCategoryAdapter = CommunityFeedCategoryAdapter()

        communityFeedCategoryListView.adapter = CategoryAdapter

        CategoryAdapter.addItem("The Newest 5")
        CategoryAdapter.addItem("Liked Top 5")
        CategoryAdapter.addItem("Download Top 5")


        communityFeedCategoryListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as CommunityFeedCategoryItem
            (activity as CommunityActivity).isCategorySelected = true

            communityFeedListView.visibility = View.VISIBLE
            communityFeedCategoryListView.visibility = View.GONE

            val FeedAdapter : CommunityFeedItemAdapter = CommunityFeedItemAdapter()
            communityFeedListView.adapter = FeedAdapter

            if(item.categoryName == "The Newest 5") {
                /*FeedAdapter.addItem("1","2",0,1,"dd")
                FeedAdapter.addItem("ㅇㅁ나어","ㄷㅈㄷ2",0,1,"dㄴd")
                FeedAdapter.addItem("1ㄴㅁ","2ㅂㅂ",1,4,"dㅇd")
                FeedAdapter.addItem("12323","2ㅇㅁㄴㅇㅁ",5,6,"ddㅁ")
                FeedAdapter.addItem("14324","2ㄴㅁㅇㅇ",6,8,"ddㅂ")
                for (i in 0..com.treasure.loopang.communication.UserManager.getUser().likedList.size-1) {
            FeedAdapter.addItem(
                com.treasure.loopang.communication.UserManager.getUser().likedList[i].owner,
                com.treasure.loopang.communication.UserManager.getUser().likedList[i].name,
                com.treasure.loopang.communication.UserManager.getUser().likedList[i].id
            )}*/
            }
            else if(item.categoryName== "Liked Top 5"){
            }
            else if(item.categoryName == "Download Top 5"){
            }
        }

        communityFeedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
    }
}