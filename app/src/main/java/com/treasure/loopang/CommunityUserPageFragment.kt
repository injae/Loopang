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
                    com.treasure.loopang.communication.UserManager.getUser().trackList[i].likes,
                    com.treasure.loopang.communication.UserManager.getUser().trackList[i].downloads,
                    com.treasure.loopang.communication.UserManager.getUser().trackList[i].id,
                    com.treasure.loopang.communication.UserManager.getUser().trackList[i].updated_date
                )//trackInfo : String
            }
        }else {
            userPageLikedListView.adapter = userPageItemAdapter
            for (i in 0..com.treasure.loopang.communication.UserManager.getUser().likedList.size-1) {
                userPageItemAdapter.addItem(
                    com.treasure.loopang.communication.UserManager.getUser().likedList[i].owner,
                    com.treasure.loopang.communication.UserManager.getUser().likedList[i].name,
                    com.treasure.loopang.communication.UserManager.getUser().likedList[i].likes,
                    com.treasure.loopang.communication.UserManager.getUser().likedList[i].downloads,
                    com.treasure.loopang.communication.UserManager.getUser().likedList[i].id,
                    com.treasure.loopang.communication.UserManager.getUser().likedList[i].updated_date
                )
            }
        }
        val userPageButton : List<Button> = listOf(userPageTrackBtn,userPageLikedBtn)
        for(i in 0.. userPageButton.size-1){
            userPageButton[i].setOnClickListener {
                if(userPageButton[i] == userPageTrackBtn){
                    isButtonStateTrack = true
                    userPageListView.visibility = View.VISIBLE
                    userPageLikedListView.visibility = View.GONE
                }else{
                    isButtonStateTrack = false
                    userPageListView.visibility = View.GONE
                    userPageLikedListView.visibility = View.VISIBLE
                }
                userPageButton[i].setTextColor(Color.argb(200,115,115,115))
                userPageButton[i].setBackgroundColor(Color.WHITE)
                userPageButton[1-i].setBackgroundColor(Color.argb(0,0,0,0))
                userPageButton[1-i].setTextColor(Color.WHITE)
            }
        }

        userPageListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as CommunitySongItem
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
            Log.d("bbbbbbbbbb","clicked"+itt.songName)
        }
        addTrackButton.setOnClickListener {
            val intent = Intent(activity, CommunityShareActivity::class.java)  //intent.putExtra()
            startActivity(intent)
        }
    }

}