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
import com.treasure.loopang.communication.ResultManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CommunitySearchFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_search,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var isButtonStateTag = true
        var editResult : String = ""
        val CommunitySearchResultTagView : ListView = community_search_result_tag_listview
        val CommunitySearchResultUserView: ListView = community_search_result_user_listview
        val CommunitySearchAdapter: CommunitySearchitemAdapter = CommunitySearchitemAdapter()

        communitySearchEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action != KeyEvent.KEYCODE_ENTER) {
                editResult = communitySearchEditText.getText().toString()
                return@OnKeyListener true
            }
            false
        })

        communitySearchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {  }//텍스트 바뀌기 전
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {  }//텍스트 바뀌는 중
            override fun afterTextChanged(edit: Editable) {//텍스트 바뀐 후
               editResult = communitySearchEditText.getText().toString()
                if(isButtonStateTag == true) addItemByBtnState(CommunitySearchResultTagView,CommunitySearchAdapter,0)
                else addItemByBtnState(CommunitySearchResultUserView,CommunitySearchAdapter,1)
            }
        })

        communitySearchBtn.setOnClickListener {
            val ld = LoadingActivity(activity!!)
            GlobalScope.launch {
                CoroutineScope(Dispatchers.Main).launch { ld?.show() }
                val result = (activity as CommunityActivity).connector.process(ResultManager.SEARCH_REQUEST, null, null, editResult)
                CoroutineScope(Dispatchers.Main).launch { ld?.dismiss() }
            }
        }
        xbutton.setOnClickListener { communitySearchEditText.setText("") }

        SearchTagBtn.setOnClickListener {
            SearchTagBtn.setTextColor(Color.argb(200, 115, 115, 115))
            SearchTagBtn.setBackgroundColor(Color.WHITE)
            SearchUserBtn.setBackgroundColor(Color.argb(0, 0, 0, 0))
            SearchUserBtn.setTextColor(Color.WHITE)
            isButtonStateTag = true
            CommunitySearchResultTagView.visibility = View.VISIBLE
            CommunitySearchResultUserView.visibility= View.GONE
        }

        SearchUserBtn.setOnClickListener {
            SearchUserBtn.setTextColor(Color.argb(200, 115, 115, 115))
            SearchUserBtn.setBackgroundColor(Color.WHITE)
            SearchTagBtn.setTextColor(Color.WHITE)
            SearchTagBtn.setBackgroundColor(Color.argb(0, 0, 0, 0))
            isButtonStateTag = false
            CommunitySearchResultTagView.visibility = View.GONE
            CommunitySearchResultUserView.visibility= View.VISIBLE
        }

        CommunitySearchResultUserView.onItemClickListener=AdapterView.OnItemClickListener{ parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
        CommunitySearchResultTagView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
    }
    fun addItemByBtnState(listView :ListView ,CommunitySearchAdapter : CommunitySearchitemAdapter, btn : Int){
        listView.adapter = CommunitySearchAdapter
        (activity as CommunityActivity).connector.searchResult?.let{
            for(i in 0.. (activity as CommunityActivity).connector.searchResult!!.searchResult[btn].size-1) {
                CommunitySearchAdapter.addItem(
                    (activity as CommunityActivity).connector.searchResult!!.searchResult[btn][i].owner,
                    (activity as CommunityActivity).connector.searchResult!!.searchResult[btn][i].name,
                    (activity as CommunityActivity).connector.searchResult!!.searchResult[btn][i].likes,
                    (activity as CommunityActivity).connector.searchResult!!.searchResult[btn][i].downloads,
                    (activity as CommunityActivity).connector.searchResult!!.searchResult[btn][i].id,
                    (activity as CommunityActivity).connector.searchResult!!.searchResult[btn][i].updated_date
                )
            }
        }
    }

}