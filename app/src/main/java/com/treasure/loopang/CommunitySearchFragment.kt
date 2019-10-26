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

class CommunitySearchFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_search,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var isNowSearching : Boolean = false
        var btnSortState : String? = null

        val CommunitySearchResultSongView : ListView = community_search_result_song_listview
        val CommunitySearchResultArtistView: ListView = community_search_result_artist_listview
        val CommunitySearchAdapter: CommunitySearchitemAdapter = CommunitySearchitemAdapter()

        CommunitySearchResultSongView.adapter = CommunitySearchAdapter
        CommunitySearchResultArtistView.adapter = CommunitySearchAdapter

        CommunitySearchAdapter.addItem("punch","Done For Me","p_d")
        CommunitySearchAdapter.addItem("punch","Another Day","p_a")
        CommunitySearchAdapter.addItem("Melanie Martinez","Lunchbox Friends","m_l")
        CommunitySearchAdapter.addItem("Melanie Martinez","Orange Juice","m_o")
        CommunitySearchAdapter.addItem("Melanie Martinez","Wheels on the Bus","m_w")
        CommunitySearchAdapter.addItem("Melanie Martinez","Class Fight","m_c")

        communitySearchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {  }//텍스트 바뀌기 전
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {  }//텍스트 바뀌는 중
            override fun afterTextChanged(edit: Editable) {//텍스트 바뀐 후
                val filterText : String = edit.toString()
                if(btnSortState == "Song")
                    (CommunitySearchResultSongView.getAdapter() as CommunitySearchitemAdapter).getFilter().filter(filterText)
                else if(btnSortState == "Artist")
                    (CommunitySearchResultArtistView.getAdapter() as CommunitySearchitemAdapter).getFilter().filter(filterText)
            }
        })

        communitySearchBtn.setOnClickListener { isNowSearching = false }//버튼 클릭으로 검색 해야지 결과가 나옴
        xbutton.setOnClickListener { communitySearchEditText.setText("") }

        //초기화
        CommunitySearchResultSongView.visibility = View.VISIBLE
        CommunitySearchResultArtistView.visibility= View.GONE

        SearchSongBtn.setOnClickListener {
            SearchSongBtn.setTextColor(Color.argb(200, 115, 115, 115))
            SearchSongBtn.setBackgroundColor(Color.WHITE)
            SearchArtistBtn.setBackgroundColor(Color.argb(0, 0, 0, 0))
            SearchArtistBtn.setTextColor(Color.WHITE)
            btnSortState = "Song"
            CommunitySearchResultSongView.visibility = View.VISIBLE
            CommunitySearchResultArtistView.visibility= View.GONE
        }
        SearchArtistBtn.setOnClickListener {
            SearchArtistBtn.setTextColor(Color.argb(200, 115, 115, 115))
            SearchArtistBtn.setBackgroundColor(Color.WHITE)
            SearchSongBtn.setBackgroundColor(Color.argb(0, 0, 0, 0))
            SearchSongBtn.setTextColor(Color.WHITE)
            btnSortState = "Artist"
            CommunitySearchResultSongView.visibility = View.GONE
            CommunitySearchResultArtistView.visibility= View.VISIBLE
        }

        CommunitySearchResultArtistView.onItemClickListener=AdapterView.OnItemClickListener{ parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(item.songId)
        }
        CommunitySearchResultSongView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(item.songId)
        }
    }

}