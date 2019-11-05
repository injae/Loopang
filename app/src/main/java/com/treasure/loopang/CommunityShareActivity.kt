package com.treasure.loopang

import android.os.Bundle
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.treasure.loopang.adapter.CommunityFeedCategoryAdapter
import com.treasure.loopang.adapter.CommunityShareAdapter
import com.treasure.loopang.audio.FileManager
import com.treasure.loopang.listitem.CommunityFeedCategoryItem
import com.treasure.loopang.listitem.CommunityShareItem
import kotlinx.android.synthetic.main.activity_community_share.*
import kotlinx.android.synthetic.main.community_feed.*

class CommunityShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_share)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val fm = FileManager()

        val ShareAdapter: CommunityShareAdapter = CommunityShareAdapter()
        communityShareListView.adapter = ShareAdapter

        if(fm.soundList().size != 0) fm.soundList().forEach { ShareAdapter.addItem(it) }

        // 이제 프로젝트 클릭하면 레이어들 리스트 나오는 프래그먼트같은거 만들어야함

        communityShareListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                val item = parent.getItemAtPosition(position) as CommunityShareItem
            getSupportFragmentManager().beginTransaction().replace(R.id.shareContainer, CommunityShareFragment()).commit()
        }
    }
}


