package com.treasure.loopang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ListView
import com.treasure.loopang.adapter.CommunityFeedItemAdapter
import com.treasure.loopang.listitem.CommunitySongItem
import kotlinx.android.synthetic.main.community_feed.*
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.treasure.loopang.adapter.CommunityFeedCategoryAdapter
import com.treasure.loopang.communication.ASyncer
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.listitem.CommunityFeedCategoryItem
import kotlinx.android.synthetic.main.activity_community.*
import java.util.*
import kotlin.collections.ArrayList
import android.widget.Toast

class CommunityFeedFragment : androidx.fragment.app.Fragment() {
    lateinit private var categoryItem : CommunityFeedCategoryItem
    lateinit var FeedAdapter : CommunityFeedItemAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_feed,container,false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if((activity as CommunityActivity).isTrackFragOpen == false && (activity as CommunityActivity).isCategorySelected == false) {
            communityFeedListView.visibility = View.GONE
            communityFeedCategoryListView.visibility = View.VISIBLE
            CategotyTextView.visibility=View.INVISIBLE
        }
        val CategoryAdapter: CommunityFeedCategoryAdapter = CommunityFeedCategoryAdapter()

        communityFeedCategoryListView.adapter = CategoryAdapter
        CategoryAdapter.addItem("The Newest 5")
        CategoryAdapter.addItem("Liked Top 5")
        CategoryAdapter.addItem("Download Top 5")

        communityFeedCategoryListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as CommunityFeedCategoryItem
            categoryItem = item
            (activity as CommunityActivity).isCategorySelected = true
            communityFeedListView.visibility = View.VISIBLE
            communityFeedCategoryListView.visibility = View.GONE
            CategotyTextView.visibility=View.VISIBLE
            feedAddItem(item)
            update()
            CategotyTextView.text=item.categoryName
        }

        communityFeedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as MusicListClass
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
    }
    fun feedAddItem(item : CommunityFeedCategoryItem){
        val FeedAdapter : CommunityFeedItemAdapter = CommunityFeedItemAdapter()
        communityFeedListView.adapter = FeedAdapter
        if(item.categoryName == "The Newest 5") {
            (activity as CommunityActivity).feedNewestList.forEach { FeedAdapter.addItem(it) }
        } else if(item.categoryName== "Liked Top 5") {
            (activity as CommunityActivity).feedLikeList.forEach{FeedAdapter.addItem(it)}
        } else if(item.categoryName == "Download Top 5"){
            (activity as CommunityActivity).feedDownloadList.forEach{FeedAdapter.addItem(it)}
        }
    }
    fun update(){
        Log.d("자동갱신테스트","update")
        if((activity as CommunityActivity).isCategorySelected == true && categoryItem.categoryName== "The Newest 5") {
            feedAddItem(categoryItem)
            Log.d("자동갱신테스트","update Newest")
            (activity as CommunityActivity).isTrackDataChanged = false //user 먼저 변경 하고 얘 들어와서 둘 다 해결하고 바꿔주는 것임
        } else if((activity as CommunityActivity).isCategorySelected == true && categoryItem.categoryName== "Liked Top 5") {
            feedAddItem(categoryItem)
            Log.d("자동갱신테스트","update Liked")
            (activity as CommunityActivity).isLikedDataChanged = false  //user 먼저 변경 하고 얘 들어와서 둘 다 해결하고 바꿔주는 것임
        } else if((activity as CommunityActivity).isCategorySelected == true && categoryItem.categoryName== "Download Top 5"){
            feedAddItem(categoryItem)
            Log.d("자동갱신테스트","update Download")
            (activity as CommunityActivity).isDownloadDataChanged = false
        }
    }
}
