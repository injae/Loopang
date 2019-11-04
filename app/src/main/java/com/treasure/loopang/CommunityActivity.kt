package com.treasure.loopang

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_community.*
import androidx.viewpager.widget.ViewPager
import com.treasure.loopang.adapter.CommunityPagerAdapter
import com.treasure.loopang.communication.Connector
import com.treasure.loopang.communication.FeedResult
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.communication.SearchResult
import com.treasure.loopang.listitem.CommunitySongItem
import kotlinx.android.synthetic.main.community_feed.*

class CommunityActivity(var connector: Connector = Connector()) : AppCompatActivity() {

    lateinit var itt : CommunitySongItem
    var isTrackFragOpen : Boolean = false
    val transaction = supportFragmentManager.beginTransaction()
    var isCategorySelected :Boolean = false
    private var currentPage: Int = 0
     private val pagerAdapter by lazy { CommunityPagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        connector.feedResult = intent.getSerializableExtra("feedResult") as FeedResult

        CommunityContainer.adapter = pagerAdapter
        CommunityContainer.addOnPageChangeListener(PageChangeListener())
        CommunityContainer.setOnTouchListener { _, _ -> false}
    }

    fun onFragmentChangedtoTrack(songitem : CommunitySongItem) {
        itt = songitem

        if(itt.songId!=null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.TrackFrame, CommunityTrackFragment()).commit()
            isTrackFragOpen = true
        }
    }

    inner class PageChangeListener: ViewPager.OnPageChangeListener {
        private var selectedPage: Int = 0
        private var scrollState: Int = 0
        private var prevPage: Int = 0

        /* 스크롤 시 드래깅 중인지 알려줌 */
        override fun onPageScrollStateChanged(state: Int) {
            when(state){
                ViewPager.SCROLL_STATE_SETTLING -> {
                    prevPage = selectedPage
                    Log.d("ViewPagerTest", "SCROLL_STATE_SETTLING")
                }
                ViewPager.SCROLL_STATE_DRAGGING -> {
                    Log.d("ViewPagerTest", "SCROLL_STATE_DRAGGING")
                }
                ViewPager.SCROLL_STATE_IDLE -> {
                    Log.d("ViewPagerTest", "SCROLL_STATE_IDLE")
                }
            }
            scrollState = state
        }

        /* 드래그하는 동안 계속 호출 */
        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

        /* 선택된 페이지 알려줌 */
        override fun onPageSelected(p0: Int) {


            selectedPage = p0
            if(prevPage != selectedPage)
                onPageChanged()
            Log.d("ViewPagerTest", "onPageSelected : $selectedPage")
        }

        /* 페이지(플래그먼트)가 바뀌었을 때 처리 */
        private fun onPageChanged() {
            this@CommunityActivity.currentPage = selectedPage
        }
      /*  private fun onFragmentChanged( songName: String, userName: String, likedNum: Int, downloadNum : Int){
            transaction.replace(R.id.TrackFrame, CommunityTrackFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            isTrackFragOpen = true
            //  getSupportFragmentManager().beginTransaction().replace(R.id.TrackFrame, CommunityTrackFragment()).commit();
        }*/
    }
    override fun onBackPressed() {
        if (isTrackFragOpen == true) {
            TrackFrame.visibility = View.GONE
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().remove(CommunityTrackFragment()).commit()
            fragmentManager.popBackStack()
            isTrackFragOpen = false
        }
        else if(isTrackFragOpen == false && isCategorySelected == true){
            communityFeedListView.visibility = View.VISIBLE
            communityFeedCategoryListView.visibility = View.GONE
            isCategorySelected = false
        }
        else {
             super.onBackPressed()
        }
    }
}
