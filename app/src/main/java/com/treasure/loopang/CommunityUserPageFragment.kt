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

    fun update(){
        if((activity as CommunityActivity).isLikedDataChanged== true){
            //update Like List
            userPageItemAdapter = CommunityUserPageAdapter()
            userpageLikedListView.adapter = userPageItemAdapter
            Log.d("sssssssss",""+ (activity as CommunityActivity).likeList.size)
            addItem(userPageItemAdapter,(activity as CommunityActivity).likeList,isButtonStateTrack)
            updateText()
            Log.d("sssssssss","update Like List")
            (activity as CommunityActivity).isLikedDataChanged = false
        }
        if((activity as CommunityActivity).isTrackDataChanged==true){
            //update Shared Layer
            userPageItemAdapter = CommunityUserPageAdapter()
            userpageListView.adapter = userPageItemAdapter
            addItem(userPageItemAdapter,(activity as CommunityActivity).sharedList,isButtonStateTrack)
            //addItem(userPageItemAdapter, com.treasure.loopang.communication.UserManager.getUser().trackList,isButtonStateTrack)
            userPageItemAdapter.notifyDataSetChanged()
            (activity as CommunityActivity).isTrackDataChanged = false
            updateText()
            Log.d("sssssssss",""+ (activity as CommunityActivity).likeList)
            Log.d("sssssssss","update Track List")
        }

    }
    var isButtonStateTrack = true
    private lateinit var userPageItemAdapter: CommunityUserPageAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userNickname.setText(com.treasure.loopang.communication.UserManager.getUser().name)
        updateText()

        userPageItemAdapter= CommunityUserPageAdapter()

        if(isButtonStateTrack) {
            Log.d("sssssssss"," 초기화ㅏㅏㅏㅏㅏㅏㅏ")
            userpageListView.adapter = userPageItemAdapter
            addItem(userPageItemAdapter, com.treasure.loopang.communication.UserManager.getUser().trackList,isButtonStateTrack)// 초기화
        }

        val userPageButton : List <Button> = listOf(userPageTrackBtn,userPageLikedBtn)
        for(i in 0.. userPageButton.size-1){
            userPageButton[i].setOnClickListener {
                if(userPageButton[i] == userPageTrackBtn){
                    isButtonStateTrack = true
                    userpageListView.visibility = View.VISIBLE
                    userpageLikedListView.visibility = View.GONE
                    /*if (userpageListView.adapter != null) {
                        userPageItemAdapter = CommunityUserPageAdapter()
                        userpageListView.adapter = userPageItemAdapter
                        addItem(userPageItemAdapter,(activity as CommunityActivity).sharedList,isButtonStateTrack)
                        Log.d("aaaaaaaaaaaaaa","트랙 리스트 업데이트")
                    }*/
                }else{
                    isButtonStateTrack = false
                    userpageListView.visibility = View.GONE
                    userpageLikedListView.visibility = View.VISIBLE
                    if(userpageLikedListView.adapter == null){
                        userPageItemAdapter = CommunityUserPageAdapter()
                        userpageLikedListView.adapter = userPageItemAdapter
                        addItem(userPageItemAdapter,com.treasure.loopang.communication.UserManager.getUser().likedList,isButtonStateTrack)//초기화 2
                        Log.d("aaaaaaaaaaaaaa","라이크 초기화 하러 옴 likedlist.size = ${com.treasure.loopang.communication.UserManager.getUser().likedList.size}")
                    }/*else{
                        Log.d("aaaaaaaaaaaaaa","라이크 리스트 업데이트")
                        userPageItemAdapter = CommunityUserPageAdapter()
                        userpageLikedListView.adapter = userPageItemAdapter
                        addItem(userPageItemAdapter,(activity as CommunityActivity).likeList,isButtonStateTrack)
                    }*/
                }
                updateText()
                userPageButton[i].setTextColor(Color.argb(200,115,115,115))
                userPageButton[i].setBackgroundColor(Color.WHITE)
                userPageButton[1-i].setBackgroundColor(Color.argb(26, 0, 0, 0))
                userPageButton[1-i].setTextColor(Color.WHITE)
            }
        }
        userpageListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as MusicListClass
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
        userpageLikedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
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