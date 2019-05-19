package com.treasure.loopang


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.fragment_song_manage.*

class SongManageFragment : Fragment() {

    private val songItemList = arrayListOf<SongItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_song_manage, container, false)

        val songListAdapter = SongListAdapter(songItemList)

        /*
        // 테스트용
        TODO("song_list null pointer exception 고쳐야함")
        Log.d("song_list",(song_list.id).toString())
        song_list!!.adapter = songListAdapter
        */

        // Inflate the layout for this fragment
        return view
    }


}
