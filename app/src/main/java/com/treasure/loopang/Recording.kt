package com.treasure.loopang

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import com.treasure.loopang.adapter.SongPagerAdapter
import kotlinx.android.synthetic.main.activity_recording.*

class Recording : AppCompatActivity() {
    private val pagerAdapter by lazy { SongPagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        /*
        // fragment test
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_frame, SongManageFragment())
            .commit()
        */

        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(PageChangeListener())
        pager.setOnTouchListener { _, _ -> false}
    }

    inner class PageChangeListener: ViewPager.OnPageChangeListener {
        private var selectedPage: Int = 0
        private var scrollState: Int = 0
        private var prevPage: Int = 0

        /* 페이지(플래그먼트)가 바뀌었을 때 처리 */
        private fun processWhenPageChanged() {
            Log.d("ViewPagerText", "페이지가 바뀌었어요!")
        }

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
                processWhenPageChanged()
            Log.d("ViewPagerTest", "onPageSelected : $selectedPage")
        }
    }
}

