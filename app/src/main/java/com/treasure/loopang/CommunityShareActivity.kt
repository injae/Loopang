package com.treasure.loopang

import android.os.Bundle
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.treasure.loopang.adapter.CommunityFeedCategoryAdapter
import com.treasure.loopang.adapter.CommunityShareAdapter
import com.treasure.loopang.listitem.CommunityFeedCategoryItem
import com.treasure.loopang.listitem.CommunityShareItem
import kotlinx.android.synthetic.main.activity_community_share.*
import kotlinx.android.synthetic.main.community_feed.*

class CommunityShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_share)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val ShareAdapter: CommunityShareAdapter = CommunityShareAdapter()
        communityShareListView.adapter = ShareAdapter

        ShareAdapter.addItem("d","d")
        ShareAdapter.addItem("b","b")
        ShareAdapter.addItem("a","a")
        //ShareAdapter.addItem("","","","")
        communityShareListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                val item = parent.getItemAtPosition(position) as CommunityShareItem
        }
    }
}


