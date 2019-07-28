package com.treasure.loopang


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.audiov2.FileManager
import com.treasure.loopang.ui.interfaces.ILoopManager
import com.treasure.loopang.ui.interfaces.IPageFragment
import com.treasure.loopang.ui.adapter.LoopListAdapter
import com.treasure.loopang.ui.item.LoopItem
import kotlinx.android.synthetic.main.fragment_loop_manage.*
import kotlinx.android.synthetic.main.fragment_song_manage.*
import kotlinx.android.synthetic.main.fragment_song_manage.loop_list

class LoopManageFragment : androidx.fragment.app.Fragment()
    , IPageFragment
    , ILoopManager {
    private val mFileManager: FileManager = FileManager()
    private val mLoopItemList = arrayListOf<LoopItem>()
    private val mAdapter = LoopListAdapter(mLoopItemList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loop_manage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loop_list!!.adapter = mAdapter
        btn_drop_all_loop.setOnClickListener {
            clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        Log.d("Loopang", "LoopManageFragment Destroyed!")
    }

    override fun onSelected() {
        loadLoops()
        Log.d("RecordFragment", "LoopManageFragment.onSelected()")
    }

    override fun onUnselected() {
        stop()
        Log.d("RecordFragment", "LoopManageFragment.onSelected()")
    }

    override fun play(position: Int) {
        mAdapter.play(position)
    }

    override fun stop() {
        mAdapter.stop()
    }

    override fun addLoop() {
        stop()
    }

    override fun clear() {
        stop()
        mFileManager.deleteAllFiles()
        loadLoops()
    }

    override fun remove(position: Int) {
        val fileString = mLoopItemList[position].loopTitle

        stop()
        mFileManager.deleteFile(fileString)
        loadLoops()
    }

    override fun loadLoops() {
        val soundList = mFileManager.SoundList()
        mLoopItemList.clear()
        for(sound in  soundList)
            mLoopItemList.add(LoopItem(sound.name, sound.date, sound.path))
        mAdapter.notifyDataSetChanged()
    }
}
