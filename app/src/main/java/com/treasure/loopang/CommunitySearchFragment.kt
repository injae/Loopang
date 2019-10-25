package com.treasure.loopang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
//import com.treasure.loopang.adapter.CommunitySearchitemAdapter
import com.treasure.loopang.adapter.settingItemNoticeListAdapter
import com.treasure.loopang.listitem.CommunitySongItem
import com.treasure.loopang.listitem.settingItemNoticeListItem
import kotlinx.android.synthetic.main.community_search.*
import kotlinx.android.synthetic.main.setting_notice.*
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.FrameLayout
import com.treasure.loopang.adapter.CommunitySearchitemAdapter
import com.treasure.loopang.adapter.SettingAdapter
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_search_ing.*
import kotlinx.android.synthetic.main.community_search_result.*
import kotlinx.android.synthetic.main.setting_frame.*
import java.util.*
import java.util.Locale.filter


class CommunitySearchFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_search,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isNowSearching : Boolean = false
        var btnSortState : String? = null

        val CommunitySearchview: ListView = community_search_ing_listview
        val CommunitySearchResultView : ListView = community_search_result_listview

       val CommunitySearchAdapter: CommunitySearchitemAdapter = CommunitySearchitemAdapter()
     ////   CommunitySearchAdapter.addItem("userName","songName") >> isNowSearching 넘겨줘야함

        CommunitySearchResultView.adapter = CommunitySearchAdapter
        //전체 리스트에서 add item 시키고 여기서 찾게 하면 됨 ㅇㅇ
        //CommunitySearchAdapter.additem()
        CommunitySearchAdapter.addItem("AKMU","고래","")
        CommunitySearchAdapter.addItem("폴킴","눈","")
        CommunitySearchAdapter.addItem("펀치","가끔이러다","")
        CommunitySearchAdapter.addItem("심규선","화조도","")

        community_search_ing_list.visibility=View.INVISIBLE
        community_search_result_list.visibility=View.INVISIBLE

        communitySearchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { //텍스트 바뀌기 전

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {//텍스트 바뀌는 중
                isNowSearching = true
                community_search_ing_list.visibility=View.VISIBLE
                community_search_result_list.visibility=View.INVISIBLE
            }
            override fun afterTextChanged(edit: Editable) {//텍스트 바뀐 후
                isNowSearching = true
                communitySearchBtn.setOnClickListener { //버튼 클릭으로 검색 해야지 결과가 나옴
                    community_search_ing_list.visibility=View.INVISIBLE
                    community_search_result_list.visibility=View.VISIBLE
                    val filterText : String = edit.toString()
                    (CommunitySearchResultView.getAdapter() as CommunitySearchitemAdapter).getFilter().filter(filterText)
                }
            }
        })

        xbutton.setOnClickListener {
            communitySearchEditText.setText("")
        }


        CommunitySearchview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as CommunitySongItem

            val songName = item.songName
            val userNickName = item.userNickName
            val downloadNum = item.downloadNum
            val likedNum = item.likedNum
            val songId = item.songId

            var trackFrame : FrameLayout = activity!!.TrackFrame
            trackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChanged(songName,userNickName ,0, 0) //likednum,downloadnum 넣어주기 ㅇㅇ

        }

        SearcgSongBtn.setOnClickListener {
            btnSortState="Song"
        }
        SearchArtistBtn.setOnClickListener {
            btnSortState="Artist"
        }
    }
}