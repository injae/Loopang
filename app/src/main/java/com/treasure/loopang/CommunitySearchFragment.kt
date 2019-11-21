
package com.treasure.loopang

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.treasure.loopang.adapter.CommunitySearchitemAdapter
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_search.communitySearchBtn
import kotlinx.android.synthetic.main.community_search.communitySearchEditText
import kotlinx.android.synthetic.main.community_search.xbutton
import kotlinx.android.synthetic.main.community_search_result.*
import android.view.KeyEvent.KEYCODE_ENTER
import android.widget.*
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.communication.ResultManager
import kotlinx.android.synthetic.main.activity_shared_community.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CommunitySearchFragment(var selection: Int = 2) : androidx.fragment.app.Fragment() {
    private var editResultForView : String = ""
    private var editResult: String = ""
    val searchTagSet : MutableSet<String> = mutableSetOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_search,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as CommunityActivity).ButtonState = "Tag"
        val tableBtnList: List<ToggleButton> = listOf(btnClap, btnViolin, btnPiano, btnPercussionInstrument, btnJanggu, btnDrum, btnBeat, btnAcappella)


        var CommunitySearchAdapter: CommunitySearchitemAdapter

        setVisibillity()
        communitySearchEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action != KEYCODE_ENTER) {
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
            val tempList: List<String> = when(selection) {
                2 -> { searchTagSet.toList() }
                else -> { List(1, {editResult}) }
            }
            (activity as CommunityActivity).isSearchBtnClicked = true
            setVisibillity()
            val ld = LoadingActivity(activity!!)
            GlobalScope.launch {
                CoroutineScope(Dispatchers.Main).launch { ld.show() }
                (activity as CommunityActivity).connector.process(ResultManager.SEARCH_REQUEST, null, null, tempList, null, null, selection)
                CoroutineScope(Dispatchers.Main).launch { ld.dismiss() }
            }
            Log.d("pppppppppppp","addItem 해야 함 ㅇㅇ")
            CommunitySearchAdapter= CommunitySearchitemAdapter() //어댑터 새로 붙혀주고 add item하므로 굉장히 잘 나와야함
            addItem(CommunitySearchAdapter)
        }
        xbutton.setOnClickListener { communitySearchEditText.setText("") }

        SearchTagBtn.setOnClickListener {
            selection = 2
            SearchTagBtn.setTextColor(Color.argb(200, 115, 115, 115))
            SearchTagBtn.setBackgroundColor(Color.WHITE)
            SearchLayerBtn.setBackgroundColor(Color.argb(26, 0, 0, 0))
            SearchLayerBtn.setTextColor(Color.WHITE)
            SearchUserBtn.setBackgroundColor(Color.argb(26, 0, 0, 0))
            SearchUserBtn.setTextColor(Color.WHITE)
            communitySearchEditText.setText("")
            (activity as CommunityActivity).isSearchBtnClicked= false
            (activity as CommunityActivity).ButtonState = "Tag"
            tagManager(tableBtnList,(activity as CommunityActivity).ButtonState)
            setVisibillity()
        }
        SearchLayerBtn.setOnClickListener {
            selection = 1
            SearchLayerBtn.setTextColor(Color.argb(200, 115, 115, 115))
            SearchLayerBtn.setBackgroundColor(Color.WHITE)
            SearchUserBtn.setBackgroundColor(Color.argb(26, 0, 0, 0))
            SearchUserBtn.setTextColor(Color.WHITE)
            SearchTagBtn.setBackgroundColor(Color.argb(26, 0, 0, 0))
            SearchTagBtn.setTextColor(Color.WHITE)
            communitySearchEditText.setText("")
            (activity as CommunityActivity).isSearchBtnClicked= false
            (activity as CommunityActivity).ButtonState = "Layer"
            setVisibillity()
        }
        SearchUserBtn.setOnClickListener {
            selection = 3
            SearchUserBtn.setTextColor(Color.argb(200, 115, 115, 115))
            SearchUserBtn.setBackgroundColor(Color.WHITE)
            SearchLayerBtn.setBackgroundColor(Color.argb(26, 0, 0, 0))
            SearchLayerBtn.setTextColor(Color.WHITE)
            SearchTagBtn.setBackgroundColor(Color.argb(26, 0, 0, 0))
            SearchTagBtn.setTextColor(Color.WHITE)
            communitySearchEditText.setText("")
            (activity as CommunityActivity).isSearchBtnClicked= false
            (activity as CommunityActivity).ButtonState = "User"
            setVisibillity()
        }
        tagManager(tableBtnList,(activity as CommunityActivity).ButtonState)
        community_search_result_listview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
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
        if((activity as CommunityActivity).ButtonState == "Tag"&&(activity as CommunityActivity).isSearchBtnClicked== false){
            community_search_tag_table.visibility = View.VISIBLE
            community_search_result_listview.visibility = View.GONE
            Log.d("pppppppppppp","button: "+(activity as CommunityActivity).ButtonState+ "SearchBtnClick? :"+(activity as CommunityActivity).isSearchBtnClicked )
        } else{
            community_search_tag_table.visibility = View.GONE
            community_search_result_listview.visibility = View.VISIBLE
            Log.d("pppppppppppp","button: "+(activity as CommunityActivity).ButtonState+ "SearchBtnClick? :"+(activity as CommunityActivity).isSearchBtnClicked )
        }
    }
    fun addItem(CommunitySearchAdapter :CommunitySearchitemAdapter){
        Log.d("pppppppppppp","add: "+(activity as CommunityActivity).ButtonState)
        (activity as CommunityActivity).connector.searchResult?.results?.forEach { CommunitySearchAdapter.addItem(it) }
    }
    fun tagManager(tableBtnList:List<ToggleButton>,ButtonState:String){
        for (btn in tableBtnList) {
            if(ButtonState == "Tag"){
                if(btn.isChecked == true) {
                    btn.toggle()
                    Log.d("pppppppppppp","toggle!")
                }
            }
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
    }
}