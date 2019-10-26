package com.treasure.loopang

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListView
import com.treasure.loopang.adapter.CommunityUserPageAdapter
import com.treasure.loopang.listitem.CommunitySongItem
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_user_page.*
import kotlinx.android.synthetic.main.loop_item.*

class CommunityUserPageFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.community_user_page,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userNickname.setText(com.treasure.loopang.communication.UserManager.getUser().name)
        userSharedTrackNumber.setText((com.treasure.loopang.communication.UserManager.getUser().trackList.size).toString())
        likenumber.setText((com.treasure.loopang.communication.UserManager.getUser().likedList.size).toString())

        var isButtonStateTrack = true
        val userPageListView: ListView = userpageListView
        val userPageLikedListView : ListView = userpageLikedListView
        val userPageItemAdapter: CommunityUserPageAdapter = CommunityUserPageAdapter()

        if (isButtonStateTrack == true) {
            userPageListView.adapter = userPageItemAdapter
            for (i in 0..com.treasure.loopang.communication.UserManager.getUser().trackList.size - 1) {
                userPageItemAdapter.addItem(
                    com.treasure.loopang.communication.UserManager.getUser().trackList[i].owner,
                    com.treasure.loopang.communication.UserManager.getUser().trackList[i].name,
                    com.treasure.loopang.communication.UserManager.getUser().trackList[i].id
                )
            }
        }else {
            userPageLikedListView.adapter = userPageItemAdapter
            for (i in 0..com.treasure.loopang.communication.UserManager.getUser().likedList.size-1) {
                userPageItemAdapter.addItem(
                    com.treasure.loopang.communication.UserManager.getUser().likedList[i].owner,
                    com.treasure.loopang.communication.UserManager.getUser().likedList[i].name,
                    com.treasure.loopang.communication.UserManager.getUser().likedList[i].id
                )
            }
        }

        userPageTrackBtn.setOnClickListener {
            userPageListView.visibility = View.VISIBLE
            userPageLikedListView.visibility = View.GONE
            Log.d("Track Btn","Track Btn Cliked")
            userPageTrackBtn.setTextColor(Color.argb(200,115,115,115))
            userPageTrackBtn.setBackgroundColor(Color.WHITE)
            userPageLikedBtn.setBackgroundColor(Color.argb(0,0,0,0))
            userPageLikedBtn.setTextColor(Color.WHITE)
            isButtonStateTrack = true
        }
        userPageLikedBtn.setOnClickListener{
            userPageListView.visibility = View.GONE
            userPageLikedListView.visibility = View.VISIBLE
            Log.d("Liked btn","Liked Btn Cliked")
            userPageLikedBtn.setTextColor(Color.argb(200,115,115,115))
            userPageLikedBtn.setBackgroundColor(Color.WHITE)
            userPageTrackBtn.setBackgroundColor(Color.argb(0,0,0,0))
            userPageTrackBtn.setTextColor(Color.WHITE)
            isButtonStateTrack = false
        }
        userPageListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as CommunitySongItem

            val songName = item.songName
            val userNickName = item.userNickName
            val downloadNum = item.downloadNum
            val likedNum = item.likedNum
            val songId = item.songId

            var trackFrame : FrameLayout = activity!!.TrackFrame
            trackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(songId) //likednum,downloadnum 넣어주기 ㅇㅇ
        }

        addTrackButton.setOnClickListener {
            val intent = Intent(activity, CommunityShareActivity::class.java)  //intent.putExtra()
            startActivity(intent)
        }
    }

}