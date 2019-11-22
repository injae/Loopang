package com.treasure.loopang

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.adapter.CommunityUserPageAdapter
import com.treasure.loopang.audio.FileManager
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.communication.UserManager
import com.treasure.loopang.listitem.CommunitySongItem
import com.treasure.loopang.ui.toast
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_search_result.*
import kotlinx.android.synthetic.main.community_user_page.*
import kotlinx.android.synthetic.main.community_user_page_item.*
import kotlinx.android.synthetic.main.loop_item.*

class CommunityUserPageFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.community_user_page,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userNickname.setText(com.treasure.loopang.communication.UserManager.getUser().name)
        updateText()

        var isButtonStateTrack = true
        val userPageListView: ListView = userpageListView
        val userPageLikedListView : ListView = userpageLikedListView
        var userPageItemAdapter: CommunityUserPageAdapter

        userPageItemAdapter= CommunityUserPageAdapter()

        if(isButtonStateTrack) {
            userPageListView.adapter = userPageItemAdapter
            addItem(userPageItemAdapter, com.treasure.loopang.communication.UserManager.getUser().trackList,isButtonStateTrack)// 초기화
        }
        if((activity as CommunityActivity).isLikedDataChanged== true && isButtonStateTrack!= false){
            //update Like List
            userPageItemAdapter = CommunityUserPageAdapter()
            userPageLikedListView.adapter = userPageItemAdapter
            addItem(userPageItemAdapter,(activity as CommunityActivity).likeList,isButtonStateTrack)
            (activity as CommunityActivity).isLikedDataChanged = false
            updateText()
        }
        if((activity as CommunityActivity).isTrackDataChanged==true && isButtonStateTrack == true){
            //update Shared Layer
            userPageItemAdapter = CommunityUserPageAdapter()
            userPageListView.adapter = userPageItemAdapter
            addItem(userPageItemAdapter,(activity as CommunityActivity).sharedList,isButtonStateTrack)
            //addItem(userPageItemAdapter, com.treasure.loopang.communication.UserManager.getUser().trackList,isButtonStateTrack)
            userPageItemAdapter.notifyDataSetChanged()
            (activity as CommunityActivity).isTrackDataChanged = false
            updateText()
        }
        val userPageButton : List <Button> = listOf(userPageTrackBtn,userPageLikedBtn)
        for(i in 0.. userPageButton.size-1){
            userPageButton[i].setOnClickListener {
                if(userPageButton[i] == userPageTrackBtn){
                    isButtonStateTrack = true
                    userPageListView.visibility = View.VISIBLE
                    userPageLikedListView.visibility = View.GONE
                    if (userPageListView.adapter != null) {
                        userPageItemAdapter = CommunityUserPageAdapter()
                        userPageListView.adapter = userPageItemAdapter
                        addItem(userPageItemAdapter,(activity as CommunityActivity).sharedList,isButtonStateTrack)
                        Log.d("aaaaaaaaaaaaaa","트랙 리스트 업데이트")
                    }
                }else{
                    isButtonStateTrack = false
                    userPageListView.visibility = View.GONE
                    userPageLikedListView.visibility = View.VISIBLE
                    if(userPageLikedListView.adapter == null){
                        userPageItemAdapter = CommunityUserPageAdapter()
                        userPageLikedListView.adapter = userPageItemAdapter
                        addItem(userPageItemAdapter,com.treasure.loopang.communication.UserManager.getUser().likedList,isButtonStateTrack)//초기화 2
                        Log.d("aaaaaaaaaaaaaa","라이크 초기화 하러 옴 likedlist.size = ${com.treasure.loopang.communication.UserManager.getUser().likedList.size}")
                    }else{
                        Log.d("aaaaaaaaaaaaaa","라이크 리스트 업데이트")
                        userPageItemAdapter = CommunityUserPageAdapter()
                        userPageLikedListView.adapter = userPageItemAdapter
                        addItem(userPageItemAdapter,(activity as CommunityActivity).likeList,isButtonStateTrack)
                    }
                }
                updateText()
                userPageButton[i].setTextColor(Color.argb(200,115,115,115))
                userPageButton[i].setBackgroundColor(Color.WHITE)
                userPageButton[1-i].setBackgroundColor(Color.argb(26, 0, 0, 0))
                userPageButton[1-i].setTextColor(Color.WHITE)
                //userPageButton[1-i].setBackgroundColor(Color.argb(0,0,0,0))
               // userPageButton[1-i].setTextColor(Color.WHITE)
            }
        }
        userPageListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as MusicListClass
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
        userPageLikedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as MusicListClass
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
        addTrackButton.setOnClickListener {
            val fm = FileManager()
            if(fm.soundList().size == 0){ //projectManager 에
              toast("파일이 없습니다.")
            }else{
                val intent = Intent(activity, CommunityShareActivity::class.java)
                startActivity(intent)
            }
        }
    }
    fun addItem(userPageItemAdapter:CommunityUserPageAdapter, list : List<MusicListClass>,isButtonStateTrack :Boolean){
        for (i in 0..list.size - 1) {
            userPageItemAdapter.addItem(list[i],isButtonStateTrack)
        }
    }
    fun updateText(){
        likenumber.setText((activity as CommunityActivity).likeList.size.toString())
        userSharedTrackNumber.setText((com.treasure.loopang.communication.UserManager.getUser().trackList.size).toString())
    }
}