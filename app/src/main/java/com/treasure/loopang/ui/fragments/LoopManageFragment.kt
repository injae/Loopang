package com.treasure.loopang.ui.fragments


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.treasure.loopang.R
import com.treasure.loopang.audio.FileManager
import com.treasure.loopang.audio.LoopMusic
import com.treasure.loopang.ui.interfaces.ILoopManager
import com.treasure.loopang.ui.interfaces.IPageFragment
import com.treasure.loopang.ui.adapter.v2.LoopListAdapter
import kotlinx.android.synthetic.main.fragment_loop_manage.*
import kotlinx.android.synthetic.main.fragment_song_manage.loop_list
import kotlin.RuntimeException


class LoopManageFragment : androidx.fragment.app.Fragment()
    , IPageFragment
    , ILoopManager {
    private val mFileManager: FileManager = FileManager()
    private var mProjectList = listOf<LoopMusic>()
    private val mAdapter = LoopListAdapter(mProjectList)
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
        search_bar.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                stop()
                val filterText = s.toString()
                mAdapter.filter.filter(filterText)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
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

    override fun play(position: Int) {}

    override fun stop() {
        mAdapter.stop()
    }

    override fun setLoop(position: Int){
        stop()
        mOnLoopManageListener?.onImport(mAdapter.getItem(position) as LoopMusic, true)
    }

    override fun addLoop(position: Int) {
        stop()
        mOnLoopManageListener?.onImport(mAdapter.getItem(position) as LoopMusic, false)
    }

    override fun clear() {
        stop()
        Log.d("FileManagerTest","removeAll()")
        mFileManager.deleteAllFiles()
        loadLoops()
    }

    override fun remove(position: Int) {
        val project = (mAdapter.getItem(position) as LoopMusic)
        Log.d("FileManagerTest","remove($project.name)")
        stop()
        project.delete()
        loadLoops()
    }

    override fun loadLoops() {
        Log.d("LoopManagerTest","loadLoops()")
        val projects = mFileManager.soundList()

        mProjectList = projects
        mAdapter.setLoopList(mProjectList)
    }

    private fun initLoopList(){
        val loopList = loop_list!!
        loopList.adapter = mAdapter
        loopList.setOnItemClickListener { _, _, position, _ ->
            showLoopManagerDialog(position)
        }
    }

    private fun showLoopManagerDialog(position: Int) {
        val loopTitle = (mAdapter.getItem(position) as LoopMusic).name
        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(null, loopTitle)
            listItems(R.array.loop_manager_dialog_menu_array_kr){ _, index: Int, _ ->
                when(index){
                    0 -> addLoop(position)
                    1 -> setLoop(position)
                    2 -> remove(position)
                }
            }
        }
    }

    interface OnLoopManageListener{
        fun onClear()
        fun onImport(project: LoopMusic, newLoadFlag: Boolean)
    }
}
