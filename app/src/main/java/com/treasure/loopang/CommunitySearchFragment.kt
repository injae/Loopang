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
import android.widget.Button
import android.widget.TableLayout
import com.treasure.loopang.communication.ResultManager
import kotlinx.android.synthetic.main.activity_shared_community.*
import kotlinx.android.synthetic.main.community_user_page.*
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

        var editResult: String = ""
        val CommunitySearchAdapter: CommunitySearchitemAdapter = CommunitySearchitemAdapter()


        communitySearchEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action != KeyEvent.KEYCODE_ENTER) {
                editResult = communitySearchEditText.getText().toString()
                return@OnKeyListener true
            }
            false
        })

        communitySearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}//텍스트 바뀌기 전
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}//텍스트 바뀌는 중
            override fun afterTextChanged(edit: Editable) {//텍스트 바뀐 후
                editResult = communitySearchEditText.getText().toString()
                if ((activity as CommunityActivity).isButtonStateTag == true &&  (activity as CommunityActivity).isTableBtnClicked == false) {
                    community_search_result_tag_listview.adapter = CommunitySearchAdapter
                    (activity as CommunityActivity).connector?.searchResult?.tagList?.forEach { CommunitySearchAdapter.addItem(it) }
                } else if ((activity as CommunityActivity).isButtonStateTag == false) {
                    community_search_result_user_listview.adapter = CommunitySearchAdapter
                    (activity as CommunityActivity).connector?.searchResult?.userList?.forEach { CommunitySearchAdapter.addItem(it) }
                }
            }
        })
        communitySearchBtn.setOnClickListener {
            (activity as CommunityActivity).isTableBtnClicked = true
            setVisibillity((activity as CommunityActivity).isButtonStateTag,(activity as CommunityActivity).isTableBtnClicked)
            val ld = LoadingActivity(activity!!)
            GlobalScope.launch {
                CoroutineScope(Dispatchers.Main).launch { ld?.show() }
                val result = (activity as CommunityActivity).connector.process(ResultManager.SEARCH_REQUEST, null, null, editResult)
                CoroutineScope(Dispatchers.Main).launch { ld?.dismiss() }
            }
        }
        xbutton.setOnClickListener { communitySearchEditText.setText("") }

        val searchButton: List<Button> = listOf(SearchTagBtn, SearchUserBtn)
        for (i in 0..searchButton.size - 1) {
            searchButton[i].setOnClickListener {
                searchButton[i].setTextColor(Color.argb(200, 115, 115, 115))
                searchButton[i].setBackgroundColor(Color.WHITE)
                searchButton[1 - i].setBackgroundColor(Color.argb(0, 0, 0, 0))
                searchButton[1 - i].setTextColor(Color.WHITE)
                if (searchButton[i] == SearchTagBtn) {
                    (activity as CommunityActivity).isButtonStateTag = true
                    setVisibillity((activity as CommunityActivity).isButtonStateTag,(activity as CommunityActivity).isTableBtnClicked)
                } else {
                    (activity as CommunityActivity).isButtonStateTag = false
                    setVisibillity((activity as CommunityActivity).isButtonStateTag,(activity as CommunityActivity).isTableBtnClicked)
                }
            }
        }
        val tableBtnList: List<Button> = listOf(btnClap, btnViolin, btnPiano, btnPercussionInstrument, btnJanggu, btnDrum, btnBeat, btnAcappella)
        for (btn in tableBtnList) {
            btn.setOnClickListener {
                communitySearchEditText.setText(btn.text.toString())
                editResult = btn.text.toString()
                Log.d("qqqqqq","버튼은"+btn.text +"클릭인증: "+(activity as CommunityActivity).isTableBtnClicked)
                setVisibillity((activity as CommunityActivity).isButtonStateTag,(activity as CommunityActivity).isTableBtnClicked)
            }
        }

        community_search_result_user_listview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
        community_search_result_tag_listview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
    }
    fun setVisibillity(isBtnStateTag : Boolean, isTableBtnClicked :Boolean){
        if(isBtnStateTag== true && isTableBtnClicked == false){
            community_search_tag_table.visibility = View.VISIBLE
            community_search_result_tag_listview.visibility = View.GONE
            community_search_result_user_listview.visibility = View.GONE
        }else if(isBtnStateTag == true && isTableBtnClicked == true) {
            community_search_tag_table.visibility = View.GONE
            community_search_result_tag_listview.visibility = View.VISIBLE
            community_search_result_user_listview.visibility = View.GONE
        }else if( isBtnStateTag == false){
            community_search_tag_table.visibility = View.GONE
            community_search_result_tag_listview.visibility = View.GONE
            community_search_result_user_listview.visibility = View.VISIBLE
        }
    }
}