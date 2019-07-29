package com.treasure.loopang


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.treasure.loopang.audiov2.FileManager
import com.treasure.loopang.audiov2.Sound
import com.treasure.loopang.ui.interfaces.ILoopManager
import com.treasure.loopang.ui.interfaces.IPageFragment
import com.treasure.loopang.ui.adapter.LoopListAdapter
import com.treasure.loopang.ui.item.LoopItem
import kotlinx.android.synthetic.main.fragment_loop_manage.*
import kotlinx.android.synthetic.main.fragment_song_manage.loop_list
import kotlin.RuntimeException

class LoopManageFragment : androidx.fragment.app.Fragment()
    , IPageFragment
    , ILoopManager {
    private val mFileManager: FileManager = FileManager()
    private val mLoopItemList = arrayListOf<LoopItem>()
    private val mAdapter = LoopListAdapter(mLoopItemList)
    private var mOnLoopManageListener: OnLoopManageListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_loop_manage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoopList()
        btn_drop_all_loop.setOnClickListener {
            clear()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context?.let{
            if(it is OnLoopManageListener){
                mOnLoopManageListener = it
            } else {
                RuntimeException("$it must implement OnLoopManageListener")
            }
        }
    }

    override fun onPause() {
        stop()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
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

    override fun setLoop(position: Int){
        stop()
        mOnLoopManageListener?.onClear() ?: return
        addLoop(position)
    }

    override fun addLoop(position: Int) {
        stop()
        mLoopItemList[position].filePath?.let{
            val sound = Sound().apply{
                load(path=it)
            }
            mOnLoopManageListener?.onAddLoop(sound) ?: return
        }?: return
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

    private fun initLoopList(){
        val loopList = loop_list!!
        loopList.adapter = mAdapter
        loopList.setOnItemClickListener { _, _, position, _ ->
            showLoopManagerDialog(position)
        }
    }

    private fun showLoopManagerDialog(position: Int) {
        val loopTitle = mLoopItemList[position].loopTitle
        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(null, loopTitle)
            listItems(R.array.loop_manager_dialog_menu_array_kr){ _, index: Int, _ ->
                when(index){
                    0 -> {
                        addLoop(position)
                    }
                    1 -> {
                        setLoop(position)
                    }
                    2 -> {
                        remove(position)
                    }
                }
            }
        }
    }

    interface OnLoopManageListener{
        fun onAddLoop(sound: Sound)
        fun onClear()
    }
}
