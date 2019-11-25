package com.treasure.loopang

import android.graphics.Color
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
import kotlinx.android.synthetic.main.community_feed.*
import android.content.Intent
import android.provider.SyncStateContract.Helpers.update
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_community.view.*
import kotlinx.android.synthetic.main.community_search_result.*

class CommunityActivity(var connector: Connector = Connector(), val likeList: MutableList<MusicListClass> = MutableList<MusicListClass>(0, { MusicListClass () }),
                        val sharedList: MutableList<MusicListClass> = MutableList<MusicListClass>(0, { MusicListClass () }),
                        val feedNewestList : MutableList<MusicListClass> = MutableList<MusicListClass>(0, { MusicListClass () }),
                        val feedLikeList : MutableList<MusicListClass> = MutableList<MusicListClass>(0, { MusicListClass () }),
                        val feedDownloadList : MutableList<MusicListClass> = MutableList<MusicListClass>(0, { MusicListClass () })) : AppCompatActivity() {

    lateinit var itt : MusicListClass
    var isTrackFragOpen : Boolean = false
    val transaction = supportFragmentManager.beginTransaction()
    var isCategorySelected : Boolean = false
    var isSearchBtnClicked : Boolean = false
    var ButtonState = "Tag"
    var sharingFinish : Boolean = false
    var isLikedDataChanged : Boolean = true
    var isTrackDataChanged : Boolean = false
    var isDownloadDataChanged:Boolean = false
    var musicId : String? =null
    var musicLikesNum : Int? = null
    var musicDownloadNum : Int? =null
    private var currentPage: Int = 0
    val pagerAdapter by lazy { CommunityPagerAdapter(supportFragmentManager) }
    private val mDecorView: View by lazy { window.decorView }
    private var mUiOption: Int = 0

    private var SelectedPage : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Hide Bottom Soft Navigation Bar
        if(intent.getStringExtra("from") == "Asyncer") connector.feedResult = intent.getSerializableExtra("feedResult") as FeedResult
    Log.d("hhhhCOMMUNITYOPEN" ,"true")
        mUiOption = mDecorView.systemUiVisibility
        mUiOption = mUiOption or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        mUiOption = mUiOption or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        mDecorView.systemUiVisibility = mUiOption

        setContentView(R.layout.activity_community)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        likeList.addAll(com.treasure.loopang.communication.UserManager.getUser().likedList) //초기화
        sharedList.addAll(com.treasure.loopang.communication.UserManager.getUser().trackList) //초기화

        if(feedNewestList.size == 0 && connector.feedResult?.recent_musics?.size != 0)feedNewestList.addAll(connector.feedResult!!.recent_musics)
        if(feedLikeList.size == 0 && connector.feedResult?.likes_top?.size!=0) feedLikeList.addAll(connector.feedResult!!.likes_top)
        if(feedDownloadList.size == 0 && connector.feedResult?.download_top?.size != 0) feedDownloadList.addAll(connector.feedResult!!.download_top)


        btn_feed.setImageDrawable(getResources().getDrawable(R.drawable.community_feedbtn))
        btn_feed.setBackgroundColor(resources.getColor(R.color.shared_comunity_bottom_button))
        btn_userpage.setImageDrawable(getResources().getDrawable(R.drawable.community_userpagebtn_ver_gray))
        btn_userpage.setBackgroundColor(Color.WHITE)
        btn_community_search.setImageDrawable(getResources().getDrawable(R.drawable.icon_search))
        btn_community_search.setBackgroundColor(Color.WHITE)

        CommunityContainer.adapter = pagerAdapter
        CommunityContainer.addOnPageChangeListener(PageChangeListener())
        CommunityContainer.setOnTouchListener { _, _ -> false}

        btn_feed.setOnClickListener { CommunityContainer.setCurrentItem(0) }
        btn_userpage.setOnClickListener { CommunityContainer.setCurrentItem(1) }
        btn_community_search.setOnClickListener { CommunityContainer.setCurrentItem(2) }
    }
    fun onFragmentChangedtoTrack(songitem : MusicListClass) {
        itt = songitem
        if(itt.id!=null) {
            val ft = getSupportFragmentManager().beginTransaction()
            ft.replace(R.id.TrackFrame, CommunityTrackFragment())
            ft.addToBackStack(null)
            ft.commit()
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
                btn_userpage.setImageDrawable(getResources().getDrawable(R.drawable.community_userpagebtn_ver_gray))
                btn_community_search.setImageDrawable(getResources().getDrawable(R.drawable.icon_search))
                btnSetting(btn_feed,btn_userpage,btn_community_search)
            }
            else if(currentPage == 1){ // UserFrag
                btn_feed.setImageDrawable(getResources().getDrawable(R.drawable.community_feedbtn_ver_gray))
                btn_userpage.setImageDrawable(getResources().getDrawable(R.drawable.community_userpagebtn))
                btn_community_search.setImageDrawable(getResources().getDrawable(R.drawable.icon_search))
                btnSetting(btn_userpage,btn_community_search,btn_feed)
               // (supportFragmentManager.findFragmentByTag("fragmentTag") as CommunityUserPageFragment).update()
            }
            else if( currentPage == 2){ //SearchFrag
                btn_feed.setImageDrawable(getResources().getDrawable(R.drawable.community_feedbtn_ver_gray))
                btn_userpage.setImageDrawable(getResources().getDrawable(R.drawable.community_userpagebtn_ver_gray))
                btn_community_search.setImageDrawable(getResources().getDrawable(R.drawable.icon_search_white))
                btnSetting(btn_community_search, btn_feed,btn_userpage)
            }
        }
    }
    override fun onBackPressed() {
        if (isTrackFragOpen == true) {
            TrackFrame.visibility = View.GONE
            val fragmentManager = supportFragmentManager
            fragmentManager.popBackStack()
            isTrackFragOpen = false
        }
        else if(isTrackFragOpen == false && isCategorySelected == true && SelectedPage == 0 ){
            isCategorySelected = false
            communityFeedCategoryListView.visibility = View.VISIBLE
            communityFeedListView.visibility= View.GONE
            CategotyTextView.visibility=View.INVISIBLE
        }
        else if(isTrackFragOpen== false && isSearchBtnClicked  == true && ButtonState == "Tag" && SelectedPage == 2){
            isSearchBtnClicked  = false
            community_search_tag_table.visibility = View.VISIBLE
            community_search_result_listview.visibility = View.GONE
        }
        else if(sharingFinish ==true){
            val intent = Intent(this, Recording::class.java)
            startActivity(intent)
            sharingFinish = false
        }
        else super.onBackPressed()
    }
    fun btnSetting(selectedBtn : ImageButton, none1 :ImageButton, none2:ImageButton){
        selectedBtn.setBackgroundColor(resources.getColor(R.color.shared_comunity_bottom_button))
        none1.setBackgroundColor(Color.WHITE)
        none2.setBackgroundColor(Color.WHITE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        sharingFinish = true
        isTrackDataChanged = true
        Log.d("llll/종료", "끝남 ㅇㅇ")
        sharedList.clear()
        sharedList.addAll(com.treasure.loopang.communication.UserManager.getUser().trackList)

        feedNewestList.clear()
        feedNewestList.addAll(connector.feedResult!!.recent_musics)

        for (item in connector.feedResult!!.recent_musics) {
            Log.d("llll/feedNew기능", "name : " + item.name + "날짜: " +item.updated_date)
        }
        for (item in feedNewestList) {
            Log.d("llll/feedNewUI", "name : " + item.name + "날짜: " +item.updated_date)
        }

        CommunityContainer.setCurrentItem(1)
        updateFragment()
    }
    fun updateFragment(){
        val userFragment = pagerAdapter.getItem(1) as CommunityUserPageFragment
        userFragment.update()
        val feedFragment = pagerAdapter.getItem(0) as CommunityFeedFragment
        feedFragment.update()
    }
}
