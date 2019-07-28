package com.treasure.loopang


import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.adapter.SongListAdapter
import com.treasure.loopang.audiov2.FileManager
import com.treasure.loopang.audiov2.Sound
import com.treasure.loopang.listitem.SongItem
import kotlinx.android.synthetic.main.fragment_song_manage.*

class LoopManageFragment : androidx.fragment.app.Fragment() {

    private val fileManager: FileManager = FileManager()
    private val songItemList = arrayListOf<SongItem>()
    private val songListAdapter = SongListAdapter(songItemList)
    private val sound = Sound()

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

        song_list!!.adapter = songListAdapter

        refreshSongList()
        Thread{
            while(true) {
                refreshSongList()
                activity?.runOnUiThread {
                    songListAdapter.notifyDataSetChanged()
                }
                SystemClock.sleep(100)
            }
        }.start()

        song_list.setOnItemClickListener { parent, view, position, id ->
            if(!sound.isPlaying.get()){
                val path: String = songItemList[position].filePath!!

                sound.load(path = path)
                sound.play()
            } else {
                sound.stop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Loopang", "LoopManageFragment Destroyed!")
    }

    private fun refreshSongList(){
        val soundList = fileManager.SoundList()
        songItemList.clear()
        for(sound in  soundList)
            songItemList.add(SongItem(sound.name, sound.date, sound.path))
    }


}
