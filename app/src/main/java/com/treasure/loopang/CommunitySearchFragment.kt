
package com.treasure.loopang

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import com.treasure.loopang.adapter.CommunitySearchitemAdapter
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_search.communitySearchBtn
import kotlinx.android.synthetic.main.community_search.communitySearchEditText
import kotlinx.android.synthetic.main.community_search.xbutton
import kotlinx.android.synthetic.main.community_search_result.*
import android.widget.*
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.communication.ResultManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.treasure.loopang.ui.toast

class CommunitySearchFragment(var selection: Int = 2) : androidx.fragment.app.Fragment() {
    private var editResultForView : String = ""
    private var editResult: String = ""
    val searchTagSet : MutableSet<String> = mutableSetOf()
    private lateinit var CommunitySearchAdapter: CommunitySearchitemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_search,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as CommunityActivity).ButtonState = "Tag"
        val tableBtnList: List<ToggleButton> = listOf(btnClap, btnViolin, btnPiano, btnPercussionInstrument, btnJanggu, btnDrum, btnBeat, btnAcappella)

        setVisibillity()
        communitySearchEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(editResult == "") toast("검색어를 입력하세요.")
                    else{
                        Log.d("RRRRRRRRR휴대폰 내장 버튼"," 클릭")
                        (activity as CommunityActivity).isSearchBtnClicked = true
                        ccc()
                        CommunitySearchAdapter= CommunitySearchitemAdapter()
                        community_search_result_listview.adapter = CommunitySearchAdapter //어댑터 새로 붙혀주고 add item하므로 굉장히 잘 나와야함
                        addItem(CommunitySearchAdapter)
                        setVisibillity()
                        return true
                    }
                }
                return false
            }
        })

        communitySearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}//텍스트 바뀌기 전
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}//텍스트 바뀌는 중
            override fun afterTextChanged(edit: Editable) {//텍스트 바뀐 후
                editResult = communitySearchEditText.getText().toString()
            }
        })

        communitySearchBtn.setOnClickListener {
            if(editResult == "") toast("검색어를 입력하세요.")
            else {
                Log.d("RRRRRRRRR휴대폰화면버튼", " 클릭")
                ccc()
                CommunitySearchAdapter = CommunitySearchitemAdapter()
                community_search_result_listview.adapter = CommunitySearchAdapter //어댑터 새로 붙혀주고 add item하므로 굉장히 잘 나와야함
                addItem(CommunitySearchAdapter)
                setVisibillity()
            }
        }
        xbutton.setOnClickListener { communitySearchEditText.setText("")
            CommunitySearchAdapter= CommunitySearchitemAdapter()
            community_search_result_listview.adapter = CommunitySearchAdapter
        }

        SearchTagBtn.setOnClickListener {
            selection = 2
            tagManager(tableBtnList,(activity as CommunityActivity).ButtonState)
            buttonSetting(SearchTagBtn,SearchLayerBtn,SearchUserBtn,"Tag")
        }
        SearchLayerBtn.setOnClickListener {
            selection = 1
            buttonSetting(SearchLayerBtn,SearchTagBtn,SearchUserBtn,"Layer")
        }
        SearchUserBtn.setOnClickListener {
            selection = 3
            buttonSetting(SearchUserBtn,SearchTagBtn,SearchLayerBtn,"User")
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
    }
    fun setVisibillity(){
        if((activity as CommunityActivity).ButtonState == "Tag"&&(activity as CommunityActivity).isSearchBtnClicked== false){
            community_search_tag_table.visibility = View.VISIBLE
            community_search_result_listview.visibility = View.GONE
        } else{
            community_search_tag_table.visibility = View.GONE
            community_search_result_listview.visibility = View.VISIBLE
        }
    }
    fun addItem(CommunitySearchAdapter :CommunitySearchitemAdapter){
        Log.d("RRRRRRRRR"," additem")
        Log.d("RRRRRRRRR","item : " + (activity as CommunityActivity).connector.searchResult?.results)
        (activity as CommunityActivity).connector.searchResult?.results?.forEach { CommunitySearchAdapter.addItem(it) }
    }
    fun tagManager(tableBtnList:List<ToggleButton>,ButtonState:String){
        for (btn in tableBtnList) {
            if(ButtonState == "Tag"){
                if(btn.isChecked == true)
                    btn.toggle()
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
    fun ccc(){
        // editResult = communitySearchEditText.getText().toString()
        (activity as CommunityActivity).isSearchBtnClicked = true
        val tempList: List<String> = when(selection) {
            2 -> { searchTagSet.toList() }
            else -> { List(1, {editResult}) }
        }
        Log.d("RRRRRRRRR","editResult : "+ editResult)
        Log.d("RRRRRRRRR","List : " + tempList)
        val ld = LoadingActivity(activity!!)
        GlobalScope.launch {
            CoroutineScope(Dispatchers.Main).launch { ld.show() }
            (activity as CommunityActivity).connector.process(ResultManager.SEARCH_REQUEST, null, null, tempList, null, null, selection)
            CoroutineScope(Dispatchers.Main).launch { ld.dismiss() }
        }
        Log.d("RRRRRRRRR","connector: " )
    }
    fun buttonSetting(SelectedBtn : Button,unSelected1 : Button , unSelected2: Button,ButtonState: String){
        SelectedBtn.setTextColor(Color.argb(200, 115, 115, 115))
        SelectedBtn.setBackgroundColor(Color.WHITE)
        unSelected1.setBackgroundColor(Color.argb(26, 0, 0, 0))
        unSelected1.setTextColor(Color.WHITE)
        unSelected2.setBackgroundColor(Color.argb(26, 0, 0, 0))
        unSelected2.setTextColor(Color.WHITE)
        communitySearchEditText.setText("")
        (activity as CommunityActivity).isSearchBtnClicked= false
        (activity as CommunityActivity).ButtonState = ButtonState
        CommunitySearchAdapter= CommunitySearchitemAdapter()
        community_search_result_listview.adapter = CommunitySearchAdapter
        setVisibillity()
    }
}