package com.treasure.loopang

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_community.*
import androidx.viewpager.widget.ViewPager
import com.treasure.loopang.adapter.CommunityPagerAdapter
import kotlinx.android.synthetic.main.community_feed.*

class CommunityActivity : AppCompatActivity() {
    var TrackFragmentsongId = ""
    var isTrackFragOpen : Boolean = false
    val transaction = supportFragmentManager.beginTransaction()
    //리스트 세개 필요함 1.전체 공유 큐,  2. 내가 좋아요 누른 곡 리스트, 3. 내가 공유한 곡 리스트
    var likedSongNum : Int = 0 //위에 리스트 만들면 리스트에 들어있는 아이템 개수로 변수 대체 가능
    // var likedSongList
    var userSharedTrackNum : Int = 0
    // var userSharedList
    var downloadNum : Int = 0
    var heartClikedNum : Int = 0
    var isCategorySelected :Boolean = false

    private var currentPage: Int = 0

     private val pagerAdapter by lazy { CommunityPagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

       // val intent = getIntent()
        //val userId = intent.extras!!.getString("userId")

        CommunityContainer.adapter = pagerAdapter
        CommunityContainer.addOnPageChangeListener(PageChangeListener())
        CommunityContainer.setOnTouchListener { _, _ -> false}
    }

    fun onFragmentChangedtoTrack(songId : String) {
        if(songId!=null) {
            TrackFragmentsongId = songId
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
