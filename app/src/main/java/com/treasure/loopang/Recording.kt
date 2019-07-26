package com.treasure.loopang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.treasure.loopang.adapter.SongPagerAdapter
import com.treasure.loopang.listitem.setEffector
import com.treasure.loopang.listitem.setMetronome
import kotlinx.android.synthetic.main.activity_recording.*
import kotlinx.android.synthetic.main.drawer.*

class Recording : AppCompatActivity() {
    private val FINISH_INTERVAL_TIME = 2000
    private var backPressedTime: Long = 0

    private val pagerAdapter by lazy { SongPagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.addDrawerListener(myDrawerListener)

       // supportFragmentManager.beginTransaction().replace(R.id.fragContainer, setMetronome()).commit()
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out).replace(R.id.fragContainer, setMetronome()).commit()
        btn_setMetronome.setOnClickListener {
            getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out)
                .replace(R.id.fragContainer, setMetronome())
                .commit()
        }
        btn_setEffector.setOnClickListener{

            getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out)
                .replace(R.id.fragContainer, setEffector())
                .commit()
        }
        btn_setting.setOnClickListener {
            getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out)
                .replace(R.id.fragContainer, setting())
                .commit()

        }

        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(PageChangeListener())
        pager.setOnTouchListener { _, _ -> false}
    }
    internal var myDrawerListener: DrawerLayout.DrawerListener = object : DrawerLayout.DrawerListener {

        override fun onDrawerClosed(drawerView: View) {}
        override fun onDrawerOpened(drawerView: View) {}

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            //txtPrompt.setText("onDrawerSlide: " + String.format("%.2f", slideOffset))
        }

        override fun onDrawerStateChanged(newState: Int) {
            val state: String
            when (newState) {
                DrawerLayout.STATE_IDLE -> state = "STATE_IDLE"
                DrawerLayout.STATE_DRAGGING -> state = "STATE_DRAGGING"
                DrawerLayout.STATE_SETTLING -> state = "STATE_SETTLING"
                else -> state = "unknown!"
            }
        }
    }
    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (intervalTime in 0..FINISH_INTERVAL_TIME)
            finishAffinity()
        else {
            backPressedTime = tempTime
            Toast.makeText(this, "One More pressed, Turn OFF", Toast.LENGTH_SHORT).show()
        }
    }


    inner class PageChangeListener: androidx.viewpager.widget.ViewPager.OnPageChangeListener {
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
                androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING -> {
                    prevPage = selectedPage
                    Log.d("ViewPagerTest", "SCROLL_STATE_SETTLING")
                }
                androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING -> {
                    Log.d("ViewPagerTest", "SCROLL_STATE_DRAGGING")
                }
                androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE -> {
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

