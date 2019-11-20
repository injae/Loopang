package com.treasure.loopang

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import com.treasure.loopang.adapter.CommunityUserPageAdapter
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.communication.UserManager
import com.treasure.loopang.listitem.CommunitySongItem
import kotlinx.android.synthetic.main.activity_community.*
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
        userSharedTrackNumber.setText((com.treasure.loopang.communication.UserManager.getUser().trackList.size).toString())
        likenumber.setText((activity as CommunityActivity).likeList.size.toString())

        var isButtonStateTrack = true
        val userPageListView: ListView = userpageListView
        val userPageLikedListView : ListView = userpageLikedListView
        var userPageItemAdapter: CommunityUserPageAdapter

        userPageItemAdapter= CommunityUserPageAdapter()

        if(isButtonStateTrack) {
            userPageListView.adapter = userPageItemAdapter
            addItem(userPageItemAdapter, com.treasure.loopang.communication.UserManager.getUser().trackList)// 초기화
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
                        addItem(userPageItemAdapter, com.treasure.loopang.communication.UserManager.getUser().trackList)
                        userPageItemAdapter.notifyDataSetChanged()
                        Log.d("aaaaaaaaaaaaaa","트랙 리스트 업데이트")
                    }
                }else{
                    isButtonStateTrack = false
                    userPageListView.visibility = View.GONE
                    userPageLikedListView.visibility = View.VISIBLE
                    if(userPageLikedListView.adapter == null){
                        userPageItemAdapter = CommunityUserPageAdapter()
                        userPageLikedListView.adapter = userPageItemAdapter
                        addItem(userPageItemAdapter,com.treasure.loopang.communication.UserManager.getUser().likedList)//초기화 2
                        Log.d("aaaaaaaaaaaaaa","라이크 초기화 하러 옴 likedlist.size = ${com.treasure.loopang.communication.UserManager.getUser().likedList.size}")
                    }else{
                        Log.d("aaaaaaaaaaaaaa","라이크 리스트 업데이트")
                        userPageItemAdapter = CommunityUserPageAdapter()
                        userPageLikedListView.adapter = userPageItemAdapter
                        addItem(userPageItemAdapter,(activity as CommunityActivity).likeList)
                    }
                }
                userPageButton[i].setTextColor(Color.argb(200,115,115,115))
                userPageButton[i].setBackgroundColor(Color.WHITE)
                userPageButton[1-i].setBackgroundColor(Color.argb(0,0,0,0))
                userPageButton[1-i].setTextColor(Color.WHITE)
            }
        }
        userPageListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as MusicListClass
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
            Log.d("tttttttttt","itt.tag: "+itt.tags)
        }
        userPageLikedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as MusicListClass
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
            Log.d("tttttttttt","itt.tag: "+itt.tags)
        }
        addTrackButton.setOnClickListener {
            val intent = Intent(activity, CommunityShareActivity::class.java)
            startActivity(intent)
        }
    }
    fun addItem(userPageItemAdapter:CommunityUserPageAdapter, list : List<MusicListClass>){
        for (i in 0..list.size - 1) {
            userPageItemAdapter.addItem(list[i])
        }
    }
}