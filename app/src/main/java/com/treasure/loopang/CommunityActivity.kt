package com.treasure.loopang

import android.content.Intent.getIntent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_community.*

class CommunityActivity : AppCompatActivity() {
    lateinit var communityFeedFrag : CommunityFeedFragment
    lateinit var communitySearchFrag: CommunitySearchFragment
    lateinit var communityUserPageFrag : CommunityUserPageFragment

    //리스트 세개 필요함 1.전체 공유 큐,  2. 내가 좋아요 누른 곡 리스트, 3. 내가 공유한 곡 리스트
    var likedSongNum : Int = 0 //위에 리스트 만들면 리스트에 들어있는 아이템 개수로 변수 대체 가능
    // var likedSongList

    var userSharedTrackNum : Int = 0
    // var userSharedList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val intent = getIntent()
        val userId = intent.extras!!.getString("userId")
        communityFeedFrag = CommunityFeedFragment()
        communitySearchFrag = CommunitySearchFragment()
        communityUserPageFrag = CommunityUserPageFragment( )
        var presentFragment : Int= 1

        btn_feed.setOnClickListener { getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
            .replace(R.id.CommunityContainer,communityFeedFrag)
            .commit()
          //btn_feed.setImageDrawable()
            //btn_feed.setBackgroundColor()

            presentFragment = 1

        }
        btn_community_search.setOnClickListener { getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
            .replace(R.id.CommunityContainer, communitySearchFrag )
            .commit()
            presentFragment =2

        }
        btn_userpage.setOnClickListener { getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
            .replace(R.id.CommunityContainer, communityUserPageFrag )
            .commit()
            presentFragment =3


            //btn_userpage.setImageDrawable(community_userpagebtn)
          //  btn_userpage.setBackgroundColor()
        }

        if (presentFragment == 1){
            btn_community_search.setImageResource(R.drawable.icon_search)
            btn_community_search.setBackgroundColor(resources.getColor(android.R.color.white))
            btn_userpage.setBackgroundColor(resources.getColor(android.R.color.white))
            btn_userpage.setImageResource(R.drawable.community_userpagebtn_ver_gray)
        }
        else if (presentFragment ==2 ){
            btn_feed.setImageResource(R.drawable.community_feedbtn_ver_gray)
            btn_feed.setBackgroundColor(resources.getColor(android.R.color.white))
            btn_userpage.setBackgroundColor(resources.getColor(android.R.color.white))
            btn_userpage.setImageResource(R.drawable.community_userpagebtn_ver_gray)
        }
        else if(presentFragment ==3){
            btn_feed.setImageResource(R.drawable.community_feedbtn_ver_gray)
            btn_feed.setBackgroundColor(resources.getColor(android.R.color.white))
            btn_community_search.setImageResource(R.drawable.icon_search)
            btn_community_search.setBackgroundColor(resources.getColor(android.R.color.white))
        }
    }

    fun onFragmentChanged(fragmentIndex:Int, songName : String?, UserNickName : String?) {
        //fragmentIndex == 1 ~ 3 (feed , search, myPage) , 0 == TrackFragment
        if (fragmentIndex == 0)
        {

        }
        else if(fragmentIndex>-1 &&fragmentIndex <=3)
        {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out).replace(R.id.CommunityContainer, CommunityTrackFragment()).commit();
        }
    }

}
