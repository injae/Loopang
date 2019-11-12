package com.treasure.loopang.ui.fragments


import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.treasure.loopang.R
import com.treasure.loopang.Recording
import com.treasure.loopang.audio.LoopStation
import com.treasure.loopang.audio.Sound
import com.treasure.loopang.ui.adapter.v2.LayerListAdapter
import com.treasure.loopang.ui.interfaces.IPageFragment
import com.treasure.loopang.ui.listener.SwipeDismissListViewTouchListener
import com.treasure.loopang.ui.listener.TouchGestureListener
import com.treasure.loopang.ui.toast
import com.treasure.loopang.ui.util.ProgressControl
import kotlinx.android.synthetic.main.dialog_save_loop.*
import kotlinx.android.synthetic.main.dialog_save_loop.check_drop
import kotlinx.android.synthetic.main.dialog_save_loop.check_split
import kotlinx.android.synthetic.main.dialog_save_loop.edit_loop_title
import kotlinx.android.synthetic.main.dialog_save_loop.spinner
import kotlinx.android.synthetic.main.dialog_save_new.*
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.*

class RecordFragment : Fragment(), IPageFragment {
    private val mLayerListAdapter : LayerListAdapter = LayerListAdapter()
    private val mTouchGestureListener = TouchGestureListener()
    val loopStation: LoopStation = LoopStation()
    private val mProgressControl: ProgressControl = ProgressControl()
    private var mSaveDialog: MaterialDialog? = null
    private var mProjectTitle: String = ""

    init {
        mTouchGestureListener.apply{
            onSingleTap = { onThisSingleTap() }
            onSwipeToDown = { onThisSwipeToDown() }
            onSwipeToUp = { onThisSwipeToUp() }
        }
        initLoopStation()
    }

    override fun onSelected() {
        // Log.d("RecordFragment", "RecordFragment.onSelected()")
    }

    override fun onUnselected() {
        // Log.d("RecordFragment", "RecordFragment.onUnselected()")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* 프래그먼트 전체에 해당하는 제스쳐 이벤트 */
        val gesture = GestureDetector(view.context, mTouchGestureListener)
        view.setOnTouchListener{ _, event -> gesture.onTouchEvent(event)}
        initLayerListView()
        btn_drop_all_layer.setOnClickListener {
            when {
                loopStation.isRecording() -> toast(R.string.toast_drop_all_of_layer_error_recording)
                loopStation.isEmpty() -> toast(R.string.toast_drop_all_of_layer_error_empty)
                else -> showDropAllLayerDialog()
            }
        }
        loopStation.linkVisualizer(realtime_visualizer_view)
        metronome_view.onStart = {
            toast("metronome start!")
            val metronome = (activity as Recording).setMetronomeFrag.metronome
            metronome.task = { metronome_view.tik() }
            metronome_view.bpm =  metronome.bpm.toInt()
            metronome.excute()
        }
        metronome_view.onStop = {
            toast("metronome stop!")
            Log.d("Metronome test", "stop")
            (activity as Recording).setMetronomeFrag.metronome.cancle()
        }
        btn_change_project_title.setOnClickListener {
            showChangeProjectTitleDialog()
        }
        layer_list.setOnItemLongClickListener { _, _, position, _ ->
            if(loopStation.isRecording()){
                false
            } else {
                showChangeLayerLabelDialog(position)
                true
            }
        }

        loop_seek_bar.isEnabled = false
        mProgressControl.setView(loop_seek_bar)
        mProgressControl.max = 100000
    }

    override fun onDestroy() {
        loopStation.stopAll()
        super.onDestroy()
        // Log.d("RecordFragment", "RecordFragment Destroyed!")
    }

    override fun onPause() {
        super.onPause()
        // Log.d("RecordFragment", "RecordFragment Paused!")
    }

    private fun onThisSingleTap(): Boolean {
        if (loopStation.isRecording()) { loopStation.recordStop() }
        else { loopStation.recordStart() }
        return true
    }

    private fun onThisSwipeToUp(): Boolean {
        if (loopStation.isLooping()) { loopStation.loopStop() }
        else { loopStation.loopStart() }
        return true
    }

    private fun onThisSwipeToDown(): Boolean {
        if(loopStation.isRecording()) {
            toast(R.string.toast_save_error_while_record)
            return true
        }
        if(loopStation.isEmpty()) {
            toast(R.string.toast_save_error_no_layer)
            return true
        }
        if(loopStation.isLooping()) loopStation.loopStop(messageFlag = false)
        showSaveDialog()
        return true
    }

    private fun onLayerClick(parent: AdapterView<*>, view: View, position: Int) {
        if(loopStation.isLooping()) {
            val muteState = loopStation.muteLayer(position)
            mLayerListAdapter.setLayerMuteState(position,view, muteState)
        } else {
            loopStation.playLayer(position)
        }
    }

    private fun showSaveDialog(){
        if(loopStation.loopMusic == null){ showNewSaveDialog() }
        else { showProjectSaveDialog() }
    }

    private fun showProjectSaveDialog() {
        MaterialDialog(activity!!).show {
            title(R.string.title_save_project)
            listItems(R.array.save_project_menu) { dialog, index, _ ->
                when(index){
                    0 -> {
                        loopStation.export(newFlag = false)
                        dialog.dismiss()
                    }
                    1 -> {
                        showNewSaveDialog()
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    private fun showNewSaveDialog() {
        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            noAutoDismiss()
            title(R.string.title_save_loop)
            cornerRadius(16f)
            cancelable(false)
            customView(R.layout.dialog_save_new, horizontalPadding = true)
            this.edit_loop_title.setText(mProjectTitle, TextView.BufferType.EDITABLE)

            if(loopStation.hasSingleLayer()) {
                this.check_split.visibility = View.GONE
                this.spinner_save_type.setSelection(1) //Sound 로 세팅.
            }
            this.spinner_save_type.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when(position){
                        0 -> { // project
                            this@show.check_split.visibility = View.GONE
                            this@show.txt_title.text = "Project Title"
                        }
                        1 -> {
                            this@show.check_split.visibility = View.VISIBLE
                            this@show.txt_title.text = "Loop Title"
                        }
                    }
                }
            }

            positiveButton(R.string.btn_save) {
                // callback on positive button click
                val loopTitle = it.edit_loop_title.text.toString()
                if(loopTitle == ""){
                    toast(R.string.toast_save_error_without_title)
                } else {
                    val newFlag = true
                    val saveType = spinner_save_type.selectedItem.toString()
                    val fileType = spinner.selectedItem.toString()
                    val splitFlag = check_split.isChecked
                    val clearFlag = check_drop.isChecked
                    val overwriteFlag = false

                    val result =  loopStation.export(newFlag, saveType, loopTitle, fileType, splitFlag, clearFlag, overwriteFlag)
                    when (result){
                        LoopStation.SAVE_SUCCESS -> toast(getString(R.string.toast_save))
                        LoopStation.SAVE_ERROR_DUPLICATE_NAME -> {
                            mSaveDialog = it
                            showOverwriteDialog()
                        }
                        LoopStation.SAVE_ERROR_NONE_LAYER -> toast("None Layer")
                        else -> toast("Save Failed")
                    }

                    it.dismiss()
                }
            }
            negativeButton(R.string.btn_cancel) {
                it.dismiss()
            }
            lifecycleOwner(activity)
        }
    }

    private fun showOverwriteDialog() {
        MaterialDialog(activity!!).show {
            title(R.string.title_overwrite)
            message(R.string.message_overwrite)
            positiveButton(R.string.btn_overwrite) {
                val newFlag = true
                val saveType = mSaveDialog!!.spinner_save_type.selectedItem.toString()
                val loopTitle = it.edit_loop_title.text.toString()
                val fileType = mSaveDialog!!.spinner.selectedItem.toString()
                val splitFlag = mSaveDialog!!.check_split.isChecked
                val clearFlag = mSaveDialog!!.check_drop.isChecked
                val overwriteFlag = true
                loopStation.export(newFlag, saveType, loopTitle, fileType, splitFlag, clearFlag, overwriteFlag)
                it.dismiss()
            }
            negativeButton(R.string.btn_cancel) {
                mSaveDialog?.show()
                it.dismiss()
            }
        }
    }

    private fun showDropAllLayerDialog() {
        if(loopStation.isRecording()) return

        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.title_drop_all_of_layer)
            message(R.string.query_drop_all_of_layer)
            cornerRadius(16f)
            cancelable(false)
            positiveButton(R.string.btn_ok) {
                // callback on positive button click
                loopStation.dropAllLayer()
            }
            negativeButton(R.string.btn_cancel)
            lifecycleOwner(activity)
        }
    }

    private fun showWhiteNoiseCheckDialog() {
        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            noAutoDismiss()
            title(null, "Check WhiteNoise")
            message(null, "버튼을 눌러 화이트 노이즈를 체크해주세요")
            cornerRadius(16f)
            cancelable(false)
            positiveButton(null, "Check") {
                // callback on positive button click
                toast("화이트 노이즈 체크 중")
                if(loopStation.checkWhiteNoise()){
                    toast("화이트 노이즈 체크 완료")
                    it.dismiss()
                } else {
                    toast("다시 시도해주세요")
                }
            }
            negativeButton(R.string.btn_cancel){
                toast("Check white noise before recording")
                it.dismiss()
            }
            lifecycleOwner(activity)
        }
    }

    private fun showChangeLayerLabelDialog(index: Int) {
        if(loopStation.isRecording()) return

        MaterialDialog(activity!!).show {
            title(R.string.changelayerlabeldialog_title)
            message(text="Original Label is ${loopStation.getLayerLabels()[index]}")
            input(hint="New Layer Label", allowEmpty = false, maxLength = 15) { _, text ->
                changeLayerLabel(index, text.toString())
                mLayerListAdapter.notifyDataSetChanged()
            }
            cornerRadius(16f)
            cancelable(false)
            positiveButton(R.string.btn_ok)
            negativeButton(R.string.btn_cancel)
            lifecycleOwner(activity)
        }
    }

    private fun showChangeProjectTitleDialog() {
        if(loopStation.isRecording()) return

        MaterialDialog(activity!!).show {
            title(R.string.changeprojecttitledialog_title)
            message(text="Please input text what it will be new title :)")
            input(hint="New Project Title", allowEmpty = false, maxLength = 15) { _, text ->
                changeProjectTitle(text.toString())
            }
            cornerRadius(16f)
            cancelable(false)
            positiveButton(R.string.btn_ok)
            negativeButton(R.string.btn_cancel)
            lifecycleOwner(activity)
        }
    }

    private fun changeProjectTitle(text: String) {
        mProjectTitle = text
        this.loop_title_label.text = text
    }

    private fun changeLayerLabel(index: Int, text: String) {
        loopStation.changeLayerLabel(index, text, true)
    }

    private fun initLoopStation() {
        loopStation.setLoopStationEventListener(MyLoopStationEventListener())
        loopStation.setLoopStationMessageListener(MyLoopStationMessageListener())
    }

    private fun initLayerListView() {
        val layerList = layer_list
        val swipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(layerList, SwipeDismissListener())
        layerList.adapter = mLayerListAdapter
        layerList.isLongClickable = true
        layerList.setOnItemLongClickListener{ parent, v, position, id ->
            // onLayerListItemLongClick(parent, v, position, id)
            false
        }
        layerList.setOnItemClickListener { parent, v, position, _ ->
            onLayerClick(parent, v, position)
        }
        layerList.setOnTouchListener(swipeDismissListViewTouchListener)
        layerList.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener())
    }

    private inner class MyLoopStationEventListener: LoopStation.LoopStationEventListener {
        override fun onLayerAdd(sound: Sound, layerLabel: String) {
            activity?.runOnUiThread { mLayerListAdapter.addLayer(sound, layerLabel) }
        }
        override fun onLayerDrop(position: Int) {
            activity?.runOnUiThread { mLayerListAdapter.dropLayer(position) }
        }

        override fun onLayerAllDrop() {
            activity?.runOnUiThread {
                mLayerListAdapter.dropAllLayer()
                loop_title_label.text = getString(R.string.title_activity_recording)
            }
        }
        override fun onImport(loopTitle: String, newLoadFlag: Boolean) {
            activity?.runOnUiThread {
                loop_title_label.text = loopTitle
                mLayerListAdapter.setLayerItemList(
                    loopStation.getSounds(),
                    loopStation.getLayerLabels() )
            }
        }
        override fun onRecordStart() {
            val layerList = layer_list
            layerList.setOnTouchListener(null)
            layerList.setOnScrollListener(null)
            // 페이저 디서블 설정 해야함
        }
        override fun onRecordFinish() {
            activity?.runOnUiThread {
                val layerList = layer_list
                val swipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(layerList, SwipeDismissListener())
                layerList.setOnTouchListener(swipeDismissListViewTouchListener)
                layerList.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener())
            }
        }
        override fun onFirstRecord(): Boolean {
            showWhiteNoiseCheckDialog()
            return true
        }
        override fun onFirstLayerSaved(sound: Sound, layerLabel: String, duration: Int) {
            mProgressControl.duration = duration
        }

        override fun onLoopStart() {}

        override fun onPositionChanged(position: Int) {
            updateLoopSeekBar(position)
        }

        private fun updateLoopSeekBar(position: Int) {
            /*CoroutineScope(Dispatchers.Default).launch {
                while (loopStation.isLooping()) {
                    CoroutineScope(Dispatchers.Main).run {
                        mProgressControl.setProgressUsingMs(loopStation.position)
                        mProgressControl.updateTask()
                    }
                    SystemClock.sleep(100)
                }
            }*/
            mProgressControl.setProgressUsingMs(position)
            mProgressControl.updateTask()
        }

        override fun onLoopStop() {
            mProgressControl.progress = 0
        }
    }

    private inner class MyLoopStationMessageListener: LoopStation.LoopStationMessageListener {
        override fun onDuplicateRecordStartError() {}
        override fun onRecordWithoutLoopingError() { toast(R.string.toast_record_start_error_without_playback) }
        override fun onSaveDuringRecordingError() { toast(R.string.toast_save_error_while_record) }
        override fun onSaveNoneLayerError() { toast(R.string.toast_save_error_no_layer) }
        override fun onFirstRecord() { toast("첫녹음입니다.") }
        override fun onDropAllLayerDuringRecordingError() { toast(R.string.toast_drop_all_of_layer_error_recording)}
        override fun onStopLoopDuringRecordingError() { toast(R.string.toast_playback_stop_error) }
        override fun onLoopStartWithoutLayerError() { toast(R.string.toast_playback_start_error) }
        override fun onDropAllLayerEmptyError() { toast(R.string.toast_drop_all_of_layer_error_empty) }

        override fun onRecordStart() { toast(R.string.toast_record_start)}
        override fun onRecordStop() { toast(R.string.toast_record_stop)}
        override fun onLoopStart() { toast(R.string.toast_playback_start) }
        override fun onLoopStop() { toast(R.string.toast_playback_stop)}
    }

    private inner class SwipeDismissListener : SwipeDismissListViewTouchListener.DismissCallbacks{
        override fun canDismiss(position: Int): Boolean = true
        override fun onDismiss(listView: ListView?, reverseSortedPositions: IntArray?) {
            for(position in reverseSortedPositions!!) { loopStation.dropLayer(position) }
        }
    }
}
