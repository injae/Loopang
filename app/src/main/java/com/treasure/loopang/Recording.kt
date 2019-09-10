package com.treasure.loopang

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.treasure.loopang.ui.adapter.LoopStationPagerAdapter
import androidx.drawerlayout.widget.DrawerLayout
import com.treasure.loopang.listitem.setEffector
import com.treasure.loopang.listitem.setMetronome
import com.treasure.loopang.ui.statusBarHeight
import kotlinx.android.synthetic.main.activity_recording.*
import kotlinx.android.synthetic.main.drawer.*
import android.widget.ImageButton
import com.treasure.loopang.ui.interfaces.IPageFragment
import android.util.DisplayMetrics
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Toast
import com.treasure.loopang.audio.FileManager
import kotlinx.android.synthetic.main.final_storage_.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Animation.AnimationListener
import com.treasure.loopang.audio.LoopMusic
import com.treasure.loopang.ui.fragments.RecordFragment
import com.treasure.loopang.ui.fragments.LoopManageFragment
import kotlin.system.exitProcess


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

    lateinit var setEffectorFrag : setEffector
    lateinit var setMetronomeFrag : setMetronome
    lateinit var setting : setting

    val fileManager : FileManager =FileManager()
    val loopList = fileManager.soundList()

    private val pagerAdapter by lazy { LoopStationPagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEffectorFrag = setEffector()
        setMetronomeFrag = setMetronome()
        setting = setting()

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

        var isPageOpen = false
        val translateUp : Animation = AnimationUtils.loadAnimation(this, R.anim.translate_up)
        val translateDown :Animation = AnimationUtils.loadAnimation(this, R.anim.translate_down)
        final_storage_layout.visibility = View.INVISIBLE
        btn_open_final_storage.visibility = View.VISIBLE

        translateDown.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(ani: Animation) {} override fun onAnimationRepeat(ani: Animation) {}
            override fun onAnimationEnd(ani: Animation) { //닫기
                isPageOpen = false
                //final_storage_layout.setVisibility(View.INVISIBLE);
                final_storage_layout.visibility = View.INVISIBLE
                btn_open_final_storage.visibility = View.VISIBLE
                Log.d("hello","내려가는 애니메이션~")
            }
        })
        translateUp.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(ani: Animation) {} override fun onAnimationRepeat(ani: Animation) {}
            override fun onAnimationEnd(ani: Animation) {
                isPageOpen = true
            }
        }) //  translateUp.setAnimationListener(SlidingAnimationListener())
        btn_open_final_storage.setOnClickListener{
            final_storage_layout.visibility = View.VISIBLE
            final_storage_layout.startAnimation(translateUp);
            btn_open_final_storage.visibility = View.INVISIBLE
        }
        val spinner: Spinner
        val arrayList: ArrayList<String>  //private val mLoopItemList = arrayListOf<LoopItem>()
        val arrayAdapter: ArrayAdapter<String>
        arrayList = ArrayList()
        for(i in loopList.indices) {
            arrayList.add(loopList[i].name)
        }
        arrayAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            arrayList
        )
        spinner = findViewById<View>(R.id.final_loop_name_spinner) as Spinner
        spinner.setAdapter(arrayAdapter)
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {}
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        })
        btn_final_play.setOnClickListener {
            Toast.makeText(applicationContext, "Loop 재생", Toast.LENGTH_SHORT).show()
            //recordFragment에서 들리는거랑 안들리는 거 구분해서 재생하는 애 여기다가 넣어주기?  Mixer
        }
        btn_final_record.setOnClickListener {
            Toast.makeText(applicationContext, "최종 녹음", Toast.LENGTH_SHORT).show()
            // slidingDrawer.lock()
            //녹음기능 여기다가 넣기
        }
        btn_final_stop.setOnClickListener {
            Toast.makeText(applicationContext, "최종 녹음을 저장합니다.", Toast.LENGTH_SHORT).show()
            //slidingDrawer.unlock()
            //최종 녹음
            final_storage_layout.startAnimation(translateDown); //녹음저장하고 닫거나
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
         //drawerLayout.setPadding(0, statusBarHeight(this), 0, 0) //Padding for transparent status bar
        drawerLayout.addDrawerListener(myDrawerListener)

        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out).replace(R.id.fragContainer, setMetronome()).commit()

        btn_setMetronome.setOnClickListener {
            checkPresentFragAndReplaceFrag(btn_setMetronome)
        }
        btn_setEffector.setOnClickListener{
            checkPresentFragAndReplaceFrag(btn_setEffector)
        }
        btn_setting.setOnClickListener {
            checkPresentFragAndReplaceFrag(btn_setting)
        }

        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(PageChangeListener())
        pager.setOnTouchListener { _, _ -> false}
    }

    fun checkPresentFragAndReplaceFrag(fragBtn : ImageButton){
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible) {
                when(fragBtn){
                    btn_setMetronome -> if( fragment is setEffector || fragment is setting){
                            getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out)
                                .replace(R.id.fragContainer, setMetronomeFrag)
                                .commit()
                        setEffectorFrag.adapter.positionMusicStop()
                        }
                    btn_setEffector -> if(fragment is setMetronome || fragment is setting) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                        .replace(R.id.fragContainer, setEffectorFrag )
                        .commit()
                }
                    btn_setting -> if(fragment is setMetronome || fragment is setEffector) {
                        getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations( R.anim.fade_in, 0, 0, R.anim.fade_out)
                            .replace(R.id.fragContainer, setting)
                            .commit()
                       setEffectorFrag.adapter.positionMusicStop()
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if( hasFocus ) {
            mDecorView.systemUiVisibility = mUiOption
        }
    }

    internal var myDrawerListener: DrawerLayout.DrawerListener = object : DrawerLayout.DrawerListener {

        override fun onDrawerClosed(drawerView: View) {
                setEffectorFrag.adapter.positionMusicStop()
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
}

