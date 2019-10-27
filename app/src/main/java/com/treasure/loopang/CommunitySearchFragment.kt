package com.treasure.loopang

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.treasure.loopang.listitem.CommunitySongItem
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.treasure.loopang.adapter.CommunitySearchitemAdapter
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_search.communitySearchBtn
import kotlinx.android.synthetic.main.community_search.communitySearchEditText
import kotlinx.android.synthetic.main.community_search.xbutton
import kotlinx.android.synthetic.main.community_search_result.*
import kotlin.math.log
import android.view.KeyEvent.KEYCODE_ENTER
import android.R
import android.view.KeyEvent
import android.widget.EditText



class CommunitySearchFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_search,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var btnSortState : String ="Tag" //tag로 초기화
        var editResult : String = ""
        val CommunitySearchResultTagView : ListView = community_search_result_tag_listview
        val CommunitySearchResultUserView: ListView = community_search_result_user_listview
        val CommunitySearchAdapter: CommunitySearchitemAdapter = CommunitySearchitemAdapter()

        CommunitySearchResultTagView.adapter = CommunitySearchAdapter
        CommunitySearchResultUserView.adapter = CommunitySearchAdapter
/*
        CommunitySearchAdapter.addItem("punch","Done For Me","p_d")
        CommunitySearchAdapter.addItem("punch","Another Day","p_a")
        CommunitySearchAdapter.addItem("Melanie Martinez","Lunchbox Friends","m_l")
        CommunitySearchAdapter.addItem("Melanie Martinez","Orange Juice","m_o")
        CommunitySearchAdapter.addItem("Melanie Martinez","Wheels on the Bus","m_w")
        CommunitySearchAdapter.addItem("Melanie Martinez","Class Fight","m_c")
*/

        communitySearchEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action != KeyEvent.KEYCODE_ENTER) {
                editResult = communitySearchEditText.getText().toString()
                Log.d("ddddddddddddddd","editReult : " + editResult)
                return@OnKeyListener true
            }
            false
        })

        communitySearchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {  }//텍스트 바뀌기 전
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {  }//텍스트 바뀌는 중
            override fun afterTextChanged(edit: Editable) {//텍스트 바뀐 후
               editResult = communitySearchEditText.getText().toString()
            }
        })

        communitySearchBtn.setOnClickListener {Log.d("aaaaaaaaaaaaaaaabb","editReult : " + editResult) }
        xbutton.setOnClickListener { communitySearchEditText.setText("") }

        SearchTagBtn.setOnClickListener {
            SearchTagBtn.setTextColor(Color.argb(200, 115, 115, 115))
            SearchTagBtn.setBackgroundColor(Color.WHITE)
            SearchUserBtn.setBackgroundColor(Color.argb(0, 0, 0, 0))
            SearchUserBtn.setTextColor(Color.WHITE)
            btnSortState = "Tag"
            CommunitySearchResultTagView.visibility = View.VISIBLE
            CommunitySearchResultUserView.visibility= View.GONE
        }

        SearchUserBtn.setOnClickListener {
            SearchUserBtn.setTextColor(Color.argb(200, 115, 115, 115))
            SearchUserBtn.setBackgroundColor(Color.WHITE)
            SearchTagBtn.setTextColor(Color.WHITE)
            SearchTagBtn.setBackgroundColor(Color.argb(0, 0, 0, 0))
            btnSortState = "User"
            CommunitySearchResultTagView.visibility = View.GONE
            CommunitySearchResultUserView.visibility= View.VISIBLE
        }

        CommunitySearchResultUserView.onItemClickListener=AdapterView.OnItemClickListener{ parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(item.songId)
        }
        CommunitySearchResultTagView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(item.songId)
        }
    }

}