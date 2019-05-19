package com.treasure.loopang


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.fragment_song_manage.*

class SongManageFragment : Fragment() {

    private val songItemList = arrayListOf<SongItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val songListAdapter = SongListAdapter(songItemList)

        song_list.adapter = songListAdapter

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_manage, container, false)
    }


}
