package com.treasure.loopang

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.treasure.loopang.ui.adapter.LoopStationPagerAdapter
import androidx.drawerlayout.widget.DrawerLayout
import com.treasure.loopang.listitem.setMetronome
import com.treasure.loopang.ui.statusBarHeight
import kotlinx.android.synthetic.main.activity_recording.*
import kotlinx.android.synthetic.main.drawer.*
import com.treasure.loopang.ui.interfaces.IPageFragment
import android.util.DisplayMetrics
import com.treasure.loopang.audio.FileManager
import android.widget.Toast
import com.treasure.loopang.Database.DatabaseManager
import com.treasure.loopang.audio.LoopMusic
import com.treasure.loopang.communication.ASyncer
import com.treasure.loopang.communication.UserManager
import com.treasure.loopang.ui.fragments.RecordFragment
import com.treasure.loopang.ui.fragments.LoopManageFragment
import com.treasure.loopang.ui.recorderConnector

import kotlin.system.exitProcess

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Recording : AppCompatActivity()
    , LoopManageFragment.OnLoopManageListener {

    companion object {
        private const val FINISH_INTERVAL_TIME = 2000
        private const val RECORD_FRAGMENT = 0
        private const val LOOP_MANAGE_FRAGMENT = 1
    }
    private val mDecorView: View by lazy { window.decorView }
    private var mUiOption: Int = 0

    private var backPressedTime: Long = 0
    private var currentPage: Int = 0

   // lateinit var setEffectorFrag : setEffector
    lateinit var setMetronomeFrag : setMetronome
    lateinit var setting : setting
    lateinit var myPage: MyPage
    val fileManager : FileManager =FileManager()
    val loopList = fileManager.soundList()

    private val pagerAdapter by lazy { LoopStationPagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ASyncer(this).execute()

        //setEffectorFrag = setEffector()
        setMetronomeFrag = setMetronome()
        setting = setting()
        myPage = MyPage()

        // 화면을 세로로 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_recording)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        recording_main_layout.setPadding(0, statusBarHeight(this), 0, 0) //Padding for transparent status bar

        //Hide Bottom Soft Navigation Bar
        mUiOption = mDecorView.systemUiVisibility
        mUiOption = mUiOption or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        mUiOption = mUiOption or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        mDecorView.systemUiVisibility = mUiOption

        val dm = applicationContext.resources.displayMetrics

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        if(com.treasure.loopang.communication.UserManager.isLogined ==false) {
            btn_community.visibility=View.GONE
            drawer_login_btn.visibility=View.VISIBLE
            drawer_logout_btn.visibility = View.GONE
            btnDrawerLoginText.setText("Login")
        } else {
            btn_community.visibility=View.VISIBLE
            drawer_logout_btn.visibility = View.VISIBLE
            drawer_login_btn.visibility=View.GONE
            btnDrawerLoginText.setText("Logout")
        }
        drawer_login_btn.setOnClickListener {
            logInAndOut()
            Log.d("logiiiiiiiin","logincliked: " + com.treasure.loopang.communication.UserManager.isLogined)
        }
        drawer_logout_btn.setOnClickListener { logInAndOut()
            Log.d("logooooooout","logoutcliked: " + com.treasure.loopang.communication.UserManager.isLogined)
        }

        drawer_logout_btn.setOnClickListener {
            GlobalScope.launch { DatabaseManager.deletePassword(this@Recording) }
            startActivity(Intent(this, Login::class.java))
            finishAffinity()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.addDrawerListener(myDrawerListener)

        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out).replace(R.id.fragContainer, setMetronome()).commit()

        btn_setMetronome.setOnClickListener {
            getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out).replace(R.id.fragContainer, setMetronomeFrag).commit()
            //checkPresentFragAndReplaceFrag(btn_setMetronome)
        }

        btn_myPage.setOnClickListener {
            getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out).replace(R.id.fragContainer, myPage).commit()
        }
        btn_setting.setOnClickListener {
            getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out).replace(R.id.fragContainer, setting).commit()
            // checkPresentFragAndReplaceFrag(btn_setting)
        }
        btn_community.setOnClickListener {
            var userId : String? = null
            val intentToCommunity = Intent(this, CommunityActivity::class.java)

            intentToCommunity.putExtra("userId",userId)
            startActivity(intentToCommunity)
        }
        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(PageChangeListener())
        pager.setOnTouchListener { _, _ -> false}

        btn_open_final_storage.setOnClickListener {
            Log.d("aaaaaaaaaaaaa","aaaaaaaaaaaaaaaa")
            Log.d("Recording, FRA", "final recording button click")
            val fragment = (pagerAdapter.getItem(0) as RecordFragment)
            val sounds = fragment.loopStation.getMixer().sounds.map { it._sound }
            val intent = Intent(this, FinalRecordActivity::class.java)

            recorderConnector.soundList = sounds
            recorderConnector.labelList = fragment.loopStation.getLayerLabels().toList()
            startActivity(intent)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if( hasFocus ) {
            mDecorView.systemUiVisibility = mUiOption
        }
    }

    internal var myDrawerListener: DrawerLayout.DrawerListener = object : DrawerLayout.DrawerListener {

        override fun onDrawerClosed(drawerView: View) {
                //setEffectorFrag.adapter.positionMusicStop()
        }
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

        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            GlobalScope.launch {
                if(UserManager.isLogined && DatabaseManager.getToken(this@Recording) != null) {
                    DatabaseManager.deleteToken(this@Recording)
                    UserManager.isLogined = false
                }
            }
            finishAffinity()
            System.runFinalization()
            exitProcess(0)
        } else {

            backPressedTime = tempTime
            Toast.makeText(this, "One More pressed, Turn OFF", Toast.LENGTH_SHORT).show()
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
            val selectedFragment: IPageFragment = pagerAdapter.getItem(selectedPage) as IPageFragment
            val unselectedFragment: IPageFragment = pagerAdapter.getItem(prevPage) as IPageFragment
            selectedFragment.onSelected()
            unselectedFragment.onUnselected()
            this@Recording.currentPage = selectedPage
        }
    }

    override fun onImport(project: LoopMusic, newLoadFlag: Boolean) {
        val recordFragment = pagerAdapter.getItem(0) as RecordFragment
        recordFragment.loopStation.import(project, newLoadFlag, messageFlag = false)
    }

    override fun onClear() {
        val recordFragment = pagerAdapter.getItem(0) as RecordFragment
        recordFragment.loopStation.dropAllLayer(messageFlag = false)
    }
    fun logInAndOut(){
        GlobalScope.launch { DatabaseManager.deletePassword(this@Recording) }
        startActivity(Intent(this, Login::class.java))
        finishAffinity()
    }
}
