package com.treasure.loopang.ui.fragments


import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import com.treasure.loopang.LoadingActivity
import com.treasure.loopang.R
import com.treasure.loopang.audio.FileManager
import com.treasure.loopang.audio.LoopMusic
import com.treasure.loopang.audio.Sound
import com.treasure.loopang.ui.interfaces.IPageFragment
import com.treasure.loopang.ui.adapter.v2.LoopListAdapter
import com.treasure.loopang.ui.stringForTime
import kotlinx.android.synthetic.main.fragment_loop_manage.*
import kotlinx.android.synthetic.main.fragment_loop_manage.loop_list
import kotlinx.android.synthetic.main.manage_preview_dialog.*
import kotlinx.android.synthetic.main.manager_detail_information_dialog_layout.*
import kotlinx.android.synthetic.main.manager_detail_information_dialog_layout.txt_title
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import kotlin.RuntimeException


class LoopManageFragment : androidx.fragment.app.Fragment()
    , IPageFragment {
    private val mFileManager: FileManager = FileManager()
    private var mProjectList = listOf<LoopMusic>()
    private val mAdapter = LoopListAdapter(mProjectList)
    private var mOnLoopManageListener: OnLoopManageListener? = null
    private val loadingActivity by lazy {LoadingActivity(this@LoopManageFragment.context!!)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_loop_manage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListView()
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
        refreshList()
        Log.d("RecordFragment", "LoopManageFragment.onSelected()")
    }

    override fun onUnselected() {
        stop()
        Log.d("RecordFragment", "LoopManageFragment.onSelected()")
    }

    fun play(position: Int) {}

    fun stop() {}

    private fun clear() {
        loadingActivity.show()
        stop()
        Log.d("FileManagerTest","removeAll()")
        mFileManager.deleteAllFiles()
        refreshList()
        loadingActivity.dismiss()
    }

    private fun deleteSound(soundMusic: LoopMusic) {
        GlobalScope.launch {
            CoroutineScope(Dispatchers.Main).launch { loadingActivity.show() }
            mFileManager.deleteFilePath(soundMusic.path)
            refreshList()
            CoroutineScope(Dispatchers.Main).launch { loadingActivity.dismiss() }
        }
        /*mFileManager.deleteFilePath(soundMusic.path)
        refreshList()
        */
    }

    private fun addLoopAsLayer(soundMusic: LoopMusic) {
        GlobalScope.launch {
            CoroutineScope(Dispatchers.Main).launch { loadingActivity.show() }
            mOnLoopManageListener?.onImport(soundMusic, newLoadFlag = false)
            CoroutineScope(Dispatchers.Main).launch { loadingActivity.dismiss() }
        }
        // mOnLoopManageListener?.onImport(soundMusic, newLoadFlag = false)
    }

    private fun deleteProject(project: LoopMusic) {
        GlobalScope.launch {
            CoroutineScope(Dispatchers.Main).launch { loadingActivity.show() }
            project.delete()
            CoroutineScope(Dispatchers.Main).launch {
                refreshList()
                loadingActivity.dismiss()
            }
        }
        /*
        * project.delete()
        refreshList()
        * */
    }

    private fun loadProject(project: LoopMusic, clearFlag: Boolean) {
        GlobalScope.launch {
            CoroutineScope(Dispatchers.Main).launch { loadingActivity.show() }
            mOnLoopManageListener?.onImport(project, clearFlag)
            CoroutineScope(Dispatchers.Main).launch { loadingActivity.dismiss() }
        }
        // mOnLoopManageListener?.onImport(project, clearFlag)
    }

    private fun refreshList() {
        Log.d("LoopManagerTest","refreshList()")
        val projects = mFileManager.soundList()

        mProjectList = projects
        mAdapter.setLoopList(mProjectList)
    }

    private fun initListView(){
        val loopList = loop_list!!
        loopList.adapter = mAdapter
        mAdapter.apply {
            onPreviewButtonClick = { this@LoopManageFragment.showPreviewPlayDialog(it) }
            onInfoButtonClick = { this@LoopManageFragment.showDetailInfoDialog(it) }
            onMoreButtonClick = { this@LoopManageFragment.showMoreDialog(it) }
        }
        loopList.setOnItemClickListener { _, _, position, _ ->
            showPreviewPlayDialog(mAdapter.getItem(position) as LoopMusic)
        }
    }

    private fun showDetailInfoDialog(loopMusic: LoopMusic) {
        val projectTitle = loopMusic.name
        MaterialDialog(activity!!).show {
            title(null, projectTitle)
            customView(R.layout.manager_detail_information_dialog_layout)
            this.txt_title.text = loopMusic.name
            if(loopMusic.child == null) { this.tr_child_num.visibility = View.GONE }
            else { this.txt_child_num.text = loopMusic.child?.size?.toString() }
            this.txt_date.text = loopMusic.date
            this.txt_path.text = loopMusic.path
            this.txt_type.text = loopMusic.type
            positiveButton(R.string.btn_ok)
            cornerRadius(16f)
        }
    }

    private fun showPreviewPlayDialog(loopMusic: LoopMusic) {
        val dialog = if(loopMusic.isPCM()){
            getDialogForPCM(loopMusic)
        } else {
            getDialogForElse(loopMusic)
        }
        dialog.show()
    }

    private fun getDialogForElse(loopMusic: LoopMusic): MaterialDialog {
        return MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).apply{
            val projectTitle = loopMusic.name
            var isPlaying = false
            var mpCanceled = false
            var previousIsPlaying = false
            val mp = MediaPlayer()
            val updateTask = {
                dialog: MaterialDialog ->
                while(isPlaying){
                    dialog.seekBar.progress = (1000 * mp.currentPosition / mp.duration)
                }
            }

            customView(R.layout.manage_preview_dialog)
            cornerRadius(16f)

            /* start content setting */
            txt_title.text = projectTitle

            btn_playback.isClickable = false
            btn_stop.isClickable = false
            btn_pause.isClickable = false
            btn_repeat.isClickable = false

            /* button setting*/
            btn_add.setOnClickListener {
                this.dismiss()
                showMoreDialog(loopMusic)
            }
            btn_info.setOnClickListener {
                showDetailInfoDialog(loopMusic)
            }
            btn_drop.setOnClickListener {
                this.dismiss()
                if (loopMusic.child == null) { deleteSound(loopMusic) }
                else { deleteProject(loopMusic) }
            }
            btn_playback.setOnClickListener {
                btn_playback.visibility = View.GONE
                btn_pause.visibility = View.VISIBLE
                isPlaying = true
                CoroutineScope(Dispatchers.Default).launch { updateTask(this@apply) }
                mp.start()
            }
            btn_pause.setOnClickListener {
                mp.pause()
                /* button switching */
                btn_playback.visibility = View.VISIBLE
                btn_pause.visibility = View.GONE
                isPlaying = false
            }
            btn_repeat.setOnClickListener {
                mp.isLooping = !(mp.isLooping)
            }
            btn_stop.setOnClickListener {
                mp.pause()
                mp.seekTo(0)
                /* button switching */
                btn_playback.visibility = View.VISIBLE
                btn_pause.visibility = View.GONE
                isPlaying = false
            }
            seekBar.max = 1000
            seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    txt_current_time.text = stringForTime(mp.currentPosition)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    mp.pause()
                    previousIsPlaying = isPlaying
                    isPlaying = false
                }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    isPlaying = previousIsPlaying
                    mp.seekTo((mp.duration * seekBar!!.progress) / 1000)
                }
            })

            onDismiss {
                try {
                    isPlaying = false
                    mp.stop()
                    mp.release()
                } catch (e : IllegalStateException) {
                    mpCanceled = true
                }
            }

            onShow {
                mp.apply{
                    setDataSource(loopMusic.path)
                    setOnPreparedListener{mp ->
                        if (mpCanceled) {
                            mp.release()
                            mpCanceled = false
                        } else {
                            /* button setting */
                            it.btn_playback.isClickable = true
                            it.btn_stop.isClickable = true
                            it.btn_pause.isClickable = true
                            it.btn_repeat.isClickable = true
                            it.txt_whole_time.text = stringForTime(mp.duration)
                        }
                    }
                    setOnCompletionListener {mp ->
                        /* button switching */
                        it.btn_playback.visibility = View.VISIBLE
                        it.btn_pause.visibility = View.GONE
                        isPlaying = false
                    }
                    setOnSeekCompleteListener {mp ->
                        if(isPlaying){
                            it.seekBar.progress = (1000 * mp.currentPosition / mp.duration)
                            async { updateTask(it) }
                            mp.start()
                        }
                    }
                }.prepareAsync()
            }
        }
    }

    private fun getDialogForPCM(loopMusic: LoopMusic): MaterialDialog {
        return MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).apply {
            val projectTitle = loopMusic.name
            var isPlaying = false
            val sound: Sound = Sound().apply { load (loopMusic.path) }

            customView(R.layout.manage_preview_dialog)
            cornerRadius(16f)

            /* start content setting */
            txt_title.text = projectTitle
            txt_whole_time.visibility = View.GONE
            txt_current_time.visibility = View.GONE
            seekBar.visibility = View.GONE
            btn_repeat.visibility = View.GONE
            btn_stop.visibility = View.GONE

            /* button setting*/
            btn_add.setOnClickListener {
                this.dismiss()
                showMoreDialog(loopMusic)
            }
            btn_info.setOnClickListener {
                showDetailInfoDialog(loopMusic)
            }
            btn_drop.setOnClickListener {
                this.dismiss()
                if (loopMusic.child == null) { deleteSound(loopMusic) }
                else { deleteProject(loopMusic) }
            }

            btn_playback.setOnClickListener {
                if(isPlaying) return@setOnClickListener
                btn_playback.visibility = View.GONE
                btn_pause.visibility = View.VISIBLE
                isPlaying = true
                sound.play()
            }
            btn_pause.setOnClickListener {
                if(!isPlaying) return@setOnClickListener
                btn_playback.visibility = View.VISIBLE
                btn_pause.visibility = View.GONE
                isPlaying = false
                sound.stop()
            }

            sound.onStop {
                isPlaying = false
                btn_playback.visibility = View.VISIBLE
                btn_pause.visibility = View.GONE
            }

            onDismiss {
                isPlaying = false
                sound.stop()
            }
        }
    }

    private fun showMoreDialog(position: Int) {
        val loopMusic = mAdapter.getItem(position) as LoopMusic
        if(loopMusic.child == null){ showSoundMoreDialog(loopMusic) }
        else { showProjectMoreDialog(loopMusic) }
    }

    private fun showMoreDialog(loopMusic: LoopMusic) {
        if(loopMusic.child == null){ showSoundMoreDialog(loopMusic) }
        else { showProjectMoreDialog(loopMusic) }
    }

    private fun showProjectMoreDialog(project: LoopMusic) {
        val projectTitle = project.name
        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(null, projectTitle)
            cornerRadius(16f)
            listItems(R.array.project_more_dialog_menu_array_kr){ _, index: Int, _ ->
                when(index){
                    0 -> this@LoopManageFragment.loadProject(project, true)
                    1 -> this@LoopManageFragment.loadProject(project, false)
                    2 -> this@LoopManageFragment.deleteProject(project)
                }
            }
        }
    }

    private fun showSoundMoreDialog(soundMusic: LoopMusic) {
        val soundTitle = soundMusic.name
        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(null, soundTitle)
            cornerRadius(16f)
            listItems(R.array.sound_more_dialog_menu_array_kr){ _, index: Int, _ ->
                when(index){
                    0 -> this@LoopManageFragment.addLoopAsLayer(soundMusic)
                    1 -> this@LoopManageFragment.deleteSound(soundMusic)
                }
            }
        }
    }

    interface OnLoopManageListener{

        fun onClear()
        fun onImport(project: LoopMusic, newLoadFlag: Boolean)
    }
}
