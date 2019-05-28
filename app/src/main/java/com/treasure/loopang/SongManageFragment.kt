package com.treasure.loopang


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.adapter.SongListAdapter
import com.treasure.loopang.listitem.SongItem
import kotlinx.android.synthetic.main.fragment_song_manage.*
import java.util.*

class SongManageFragment : Fragment() {

    private val songItemList = arrayListOf<SongItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_manage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songListAdapter = SongListAdapter(songItemList)
        song_list!!.adapter = songListAdapter

        /* 테스트 코드 */
        for(i in 1..3) songItemList.add(SongItem())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Loopang", "SongManageFragment Destroyed!")
    }


}
