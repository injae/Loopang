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
            if(item.categoryName == "The Newest 5") { additemByCategory( FeedAdapter, 0)}
            else if(item.categoryName== "Liked Top 5") { additemByCategory( FeedAdapter, 1) }
            else if(item.categoryName == "Download Top 5"){ additemByCategory( FeedAdapter, 2)}
        }

        communityFeedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
    }
    fun additemByCategory( FeedAdapter : CommunityFeedItemAdapter, category : Int){
        (activity as CommunityActivity).connector.feedResult?.let{
            for(i in 0.. (activity as CommunityActivity).connector.feedResult!!.feed[category].size-1) {
                FeedAdapter.addItem(
                    (activity as CommunityActivity).connector.feedResult!!.feed[category][i].owner,
                    (activity as CommunityActivity).connector.feedResult!!.feed[category][i].name,
                    (activity as CommunityActivity).connector.feedResult!!.feed[category][i].likes,
                    (activity as CommunityActivity).connector.feedResult!!.feed[category][i].downloads,
                    (activity as CommunityActivity).connector.feedResult!!.feed[category][i].id,
                    (activity as CommunityActivity).connector.feedResult!!.feed[category][i].updated_date
                )
            }
        }
    }
}