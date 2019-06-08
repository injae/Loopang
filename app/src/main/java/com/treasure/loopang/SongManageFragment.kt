package com.treasure.loopang


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.adapter.SongListAdapter
import com.treasure.loopang.audio.FileManager
import com.treasure.loopang.listitem.SongItem
import kotlinx.android.synthetic.main.fragment_song_manage.*
import java.util.*

class SongManageFragment : androidx.fragment.app.Fragment() {

    private val fileManager: FileManager = FileManager()
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

        val soundList = fileManager.SoundList()
        for(sound in  soundList)
            songItemList.add(SongItem(sound.name, sound.date))
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Loopang", "SongManageFragment Destroyed!")
    }


}
