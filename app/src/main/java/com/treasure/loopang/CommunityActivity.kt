package com.treasure.loopang

import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_community.*
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.treasure.loopang.adapter.CommunityPagerAdapter
import com.treasure.loopang.audio.LoopMusic
import com.treasure.loopang.ui.fragments.RecordFragment
import com.treasure.loopang.ui.interfaces.IPageFragment
import kotlinx.android.synthetic.main.activity_recording.*


class CommunityActivity : AppCompatActivity() {
 /*   private val communityViewPager: ViewPager = CommunityContainer
    var adapter = CommunityPagerAdapter(supportFragmentManager)
*/
    //리스트 세개 필요함 1.전체 공유 큐,  2. 내가 좋아요 누른 곡 리스트, 3. 내가 공유한 곡 리스트
    var likedSongNum : Int = 0 //위에 리스트 만들면 리스트에 들어있는 아이템 개수로 변수 대체 가능
    // var likedSongList

    var userSharedTrackNum : Int = 0
    // var userSharedList

    private var currentPage: Int = 0

     private val pagerAdapter by lazy { CommunityPagerAdapter(supportFragmentManager) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val intent = getIntent()
        val userId = intent.extras!!.getString("userId")

        CommunityContainer.adapter = pagerAdapter
        CommunityContainer.addOnPageChangeListener(PageChangeListener())
        CommunityContainer.setOnTouchListener { _, _ -> false}

    }

    fun onFragmentChanged(songName: String?, userNickName: String?, i: Int, i1: Int) {
            getSupportFragmentManager().beginTransaction().replace(R.id.TrackFrame, CommunityTrackFragment()).commit();
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
        private fun onFragmentChanged( songName: String, userName: String, likedNum: Int, downloadNum : Int){
            getSupportFragmentManager().beginTransaction().replace(R.id.TrackFrame, CommunityTrackFragment()).commit();
        }

    }


}
