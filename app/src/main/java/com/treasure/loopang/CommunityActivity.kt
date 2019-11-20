package com.treasure.loopang

import android.graphics.Color
import android.os.Build.VERSION_CODES.P
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
import android.content.Intent
import android.view.KeyEvent
import android.widget.ListView
import android.widget.TableLayout
import androidx.core.app.NotificationCompat.getExtras
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.community_search_result.*

class CommunityActivity(var connector: Connector = Connector(), val likeList: MutableList<MusicListClass> = MutableList<MusicListClass>(0, { MusicListClass () })) : AppCompatActivity() {
    var isTrackAdded : Boolean = false
    lateinit var itt : MusicListClass
    var isTrackFragOpen : Boolean = false
    val transaction = supportFragmentManager.beginTransaction()
    var isCategorySelected : Boolean = false
    var isTableBtnClicked : Boolean = false
    var isButtonStateTag :Boolean = true
    var sharingFinish : Boolean = false
    private var currentPage: Int = 0
    private val pagerAdapter by lazy { CommunityPagerAdapter(supportFragmentManager) }
    private val mDecorView: View by lazy { window.decorView }
    private var mUiOption: Int = 0

    private var SelectedPage : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Hide Bottom Soft Navigation Bar
        if(intent.getStringExtra("from") == "Asyncer") connector.feedResult = intent.getSerializableExtra("feedResult") as FeedResult

        mUiOption = mDecorView.systemUiVisibility
        mUiOption = mUiOption or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        mUiOption = mUiOption or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        mDecorView.systemUiVisibility = mUiOption

        setContentView(R.layout.activity_community)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
/*    for(layer in com.treasure.loopang.communication.UserManager.getUser().likedList){
        likeList.add(layer)
    }*/
        likeList.addAll(com.treasure.loopang.communication.UserManager.getUser().likedList)
        for(i in likeList) {
            Log.d("qqqqqqqqqqqqqq", "" + i.name)
        }
        btn_feed.setImageDrawable(getResources().getDrawable(R.drawable.community_feedbtn))
        btn_feed.setBackgroundColor(resources.getColor(R.color.shared_comunity_bottom_button))
        btn_userpage.setImageDrawable(getResources().getDrawable(R.drawable.community_userpagebtn_ver_gray))
        btn_userpage.setBackgroundColor(Color.WHITE)
        btn_community_search.setImageDrawable(getResources().getDrawable(R.drawable.icon_search))
        btn_community_search.setBackgroundColor(Color.WHITE)

        CommunityContainer.adapter = pagerAdapter
        CommunityContainer.addOnPageChangeListener(PageChangeListener())
        CommunityContainer.setOnTouchListener { _, _ -> false}

        val shareActivityintent = intent
        val isSharingFinished = intent.extras.getString("finish")
        if(isSharingFinished == "true"){
            sharingFinish = true
            CommunityContainer.setCurrentItem(1)
        }

        btn_feed.setOnClickListener { CommunityContainer.setCurrentItem(0) }
        btn_userpage.setOnClickListener { CommunityContainer.setCurrentItem(1) }
        btn_community_search.setOnClickListener { CommunityContainer.setCurrentItem(2) }
    }
    fun onFragmentChangedtoTrack(songitem : MusicListClass) {
        itt = songitem
        if(itt.id!=null) {
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
            SelectedPage = selectedPage
            if(prevPage != selectedPage)
                onPageChanged()
            Log.d("ViewPagerTest", "onPageSelected : $selectedPage"+"and "+SelectedPage)
        }

        /* 페이지(플래그먼트)가 바뀌었을 때 처리 */
        private fun onPageChanged() {
            var isSelected : Boolean
            this@CommunityActivity.currentPage = selectedPage
            if(currentPage == 0){ // feedFrag
                btn_feed.setImageDrawable(getResources().getDrawable(R.drawable.community_feedbtn))
                btn_feed.setBackgroundColor(resources.getColor(R.color.shared_comunity_bottom_button))
                btn_userpage.setImageDrawable(getResources().getDrawable(R.drawable.community_userpagebtn_ver_gray))
                btn_userpage.setBackgroundColor(Color.WHITE)
                btn_community_search.setImageDrawable(getResources().getDrawable(R.drawable.icon_search))
                btn_community_search.setBackgroundColor(Color.WHITE)
            }
            else if(currentPage == 1){ // UserFrag
                btn_feed.setImageDrawable(getResources().getDrawable(R.drawable.community_feedbtn_ver_gray))
                btn_feed.setBackgroundColor(Color.WHITE)
                btn_userpage.setImageDrawable(getResources().getDrawable(R.drawable.community_userpagebtn))
                btn_userpage.setBackgroundColor(resources.getColor(R.color.shared_comunity_bottom_button))
                btn_community_search.setImageDrawable(getResources().getDrawable(R.drawable.icon_search))
                btn_community_search.setBackgroundColor(Color.WHITE)
            }
            else if( currentPage == 2){ //SearchFrag
                btn_feed.setImageDrawable(getResources().getDrawable(R.drawable.community_feedbtn_ver_gray))
                btn_feed.setBackgroundColor(Color.WHITE)
                btn_userpage.setImageDrawable(getResources().getDrawable(R.drawable.community_userpagebtn_ver_gray))
                btn_userpage.setBackgroundColor(Color.WHITE)
                btn_community_search.setImageDrawable(getResources().getDrawable(R.drawable.icon_search_white))
                btn_community_search.setBackgroundColor(resources.getColor(R.color.shared_comunity_bottom_button))
            }
        }
    }
    override fun onBackPressed() {
        if (isTrackFragOpen == true) {
            TrackFrame.visibility = View.GONE
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().remove(CommunityTrackFragment()).commit()
            fragmentManager.popBackStack()
            isTrackFragOpen = false
        }
        else if(isTrackFragOpen == false && isCategorySelected == true && SelectedPage == 0 ){
            isCategorySelected = false
            communityFeedCategoryListView.visibility = View.VISIBLE
            communityFeedListView.visibility= View.GONE
        }
        else if(isTrackFragOpen== false && isTableBtnClicked == true && isButtonStateTag == true && SelectedPage == 2){
            isTableBtnClicked = false
            community_search_tag_table.visibility = View.VISIBLE
            community_search_result_tag_listview.visibility = View.GONE
            community_search_result_user_listview.visibility = View.GONE
        }
        else if(sharingFinish ==true){
            val intent = Intent(this, Recording::class.java)
            startActivity(intent)
            sharingFinish = false
        }
        else super.onBackPressed()
    }
}
