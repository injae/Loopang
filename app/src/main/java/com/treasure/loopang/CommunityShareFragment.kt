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

class CommunityShareFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_feed,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}