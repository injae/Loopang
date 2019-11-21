
package com.treasure.loopang

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import android.widget.*
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.communication.ResultManager
import kotlinx.android.synthetic.main.activity_shared_community.*
import kotlinx.android.synthetic.main.community_user_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CommunitySearchFragment : androidx.fragment.app.Fragment() {
    private var editResultForView : String = ""
    private var editResult: String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_search,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchTagSet : MutableSet<String> = mutableSetOf()
        var CommunitySearchAdapter: CommunitySearchitemAdapter

        setVisibillity()
        communitySearchEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action != KeyEvent.KEYCODE_ENTER) {
                (activity as CommunityActivity).isSearchBtnClicked = true
                setVisibillity()
                editResult = communitySearchEditText.getText().toString()
                CommunitySearchAdapter= CommunitySearchitemAdapter() //어댑터 새로 붙혀주고 add item하므로 굉장히 잘 나와야함
                addItem(CommunitySearchAdapter)
                return@OnKeyListener true
            }
            false
        })

        communitySearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}//텍스트 바뀌기 전
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}//텍스트 바뀌는 중
            override fun afterTextChanged(edit: Editable) {//텍스트 바뀐 후
                editResult = communitySearchEditText.getText().toString()
            }
        })

        communitySearchBtn.setOnClickListener {
            Log.d("pppppppppppp","btn click!")
            (activity as CommunityActivity).isSearchBtnClicked = true
            setVisibillity()
            val ld = LoadingActivity(activity!!)
            GlobalScope.launch {
                CoroutineScope(Dispatchers.Main).launch { ld?.show() }
                val tempList = List<String>(1, {editResult}) // 이거는 지은이가 수정다하면 내가 하면됌
                val result = (activity as CommunityActivity).connector.process(ResultManager.SEARCH_REQUEST, null, null, tempList)
                CoroutineScope(Dispatchers.Main).launch { ld?.dismiss() }
            }
            Log.d("pppppppppppp","addItem 해야 함 ㅇㅇ")
            CommunitySearchAdapter= CommunitySearchitemAdapter() //어댑터 새로 붙혀주고 add item하므로 굉장히 잘 나와야함
            addItem(CommunitySearchAdapter)
        }
        xbutton.setOnClickListener { communitySearchEditText.setText("") }

        val filterBtnList: List<Button> = listOf(SearchTagBtn, SearchUserBtn)
        for (i in 0..filterBtnList.size - 1) {
            filterBtnList[i].setOnClickListener {
                filterBtnList[i].setTextColor(Color.argb(200, 115, 115, 115))
                filterBtnList[i].setBackgroundColor(Color.WHITE)
                filterBtnList[1 - i].setBackgroundColor(Color.argb(0, 0, 0, 0))
                filterBtnList[1 - i].setTextColor(Color.WHITE)
                if (filterBtnList[i] == SearchTagBtn) (activity as CommunityActivity).isButtonStateTag = true
                else (activity as CommunityActivity).isButtonStateTag = false
                setVisibillity()
                Log.d("pppppppppppp","btn State Tag? : "+(activity as CommunityActivity).isButtonStateTag)
            }
        }

        val tableBtnList: List<ToggleButton> = listOf(btnClap, btnViolin, btnPiano, btnPercussionInstrument, btnJanggu, btnDrum, btnBeat, btnAcappella)
        for (btn in tableBtnList) {
            btn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    searchTagSet.add(btn.text.toString())
                    setEditTextView(searchTagSet)
                } else {
                    searchTagSet.remove(btn.text.toString())
                    setEditTextView(searchTagSet)
                }
            }
        }
        community_search_result_user_listview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as MusicListClass
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
        community_search_result_tag_listview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as MusicListClass
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
    }
    fun setEditTextView(searchTagSet:MutableSet<String>){
        editResultForView=""
        for(tag in searchTagSet){
            if(editResultForView=="") editResultForView = tag
            else editResultForView = editResultForView + ", " + tag
        }
        communitySearchEditText.setText(editResultForView)
        Log.d("pppppppppppp",""+searchTagSet)
    }
    fun setVisibillity(){
        if((activity as CommunityActivity).isButtonStateTag){
            community_search_result_user_listview.visibility = View.GONE
            if((activity as CommunityActivity).isSearchBtnClicked ){
                Log.d("pppppppppppp","ButtonTag? "+(activity as CommunityActivity).isButtonStateTag + ", SearchBtnClick?" +(activity as CommunityActivity).isSearchBtnClicked)
                community_search_tag_table.visibility = View.GONE
                community_search_result_tag_listview.visibility = View.VISIBLE
            }else{
                Log.d("pppppppppppp","ButtonTag? "+(activity as CommunityActivity).isButtonStateTag + ", SearchBtnClick?" +(activity as CommunityActivity).isSearchBtnClicked)
                community_search_tag_table.visibility = View.VISIBLE
                community_search_result_tag_listview.visibility = View.GONE
            }
        } else{
            Log.d("pppppppppppp","ButtonTag? "+(activity as CommunityActivity).isButtonStateTag + ", SearchBtnClick?" +(activity as CommunityActivity).isSearchBtnClicked)
            community_search_tag_table.visibility = View.GONE
            community_search_result_tag_listview.visibility = View.GONE
            community_search_result_user_listview.visibility = View.VISIBLE
        }
    }
    fun addItem(CommunitySearchAdapter :CommunitySearchitemAdapter){
        if ((activity as CommunityActivity).isButtonStateTag == true) {
            community_search_result_tag_listview.adapter = CommunitySearchAdapter
            Log.d("pppppppppppp","add Item Tag List")
            (activity as CommunityActivity).connector?.searchResult?.tagList?.forEach { CommunitySearchAdapter.addItem(it) }
        } else{
            community_search_result_user_listview.adapter = CommunitySearchAdapter
            Log.d("pppppppppppp","add Item User List")
            (activity as CommunityActivity).connector?.searchResult?.userList?.forEach { CommunitySearchAdapter.addItem(it) }
        }
    }
}