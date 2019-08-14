package com.treasure.loopang.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.treasure.loopang.R
import com.treasure.loopang.audio.LoopStation
import com.treasure.loopang.audio.Sound
import com.treasure.loopang.ui.adapter.LayerListAdapter2
import com.treasure.loopang.ui.interfaces.IPageFragment
import com.treasure.loopang.ui.listener.SwipeDismissListViewTouchListener
import com.treasure.loopang.ui.listener.TouchGestureListener
import com.treasure.loopang.ui.toast
import kotlinx.android.synthetic.main.dialog_save_loop.*
import kotlinx.android.synthetic.main.fragment_record.*

class RecordFragment : Fragment(), IPageFragment {
    private val mLayerListAdapter : LayerListAdapter2 = LayerListAdapter2()
    private val mTouchGestureListener = TouchGestureListener()

    private val mLoopStation: LoopStation = LoopStation()

    init {
        mTouchGestureListener.apply{
            onSingleTap = { onThisSingleTap() }
            onSwipeToDown = { onThisSwipeToDown() }
            onSwipeToUp = { onThisSwipeToUp() }
        }
        initLoopStation()
    }

    override fun onSelected() {
        Log.d("RecordFragment", "RecordFragment.onSelected()")
    }

    override fun onUnselected() {
        Log.d("RecordFragment", "RecordFragment.onUnselected()")
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
        btn_drop_all_layer.setOnClickListener { showDropAllLayerDialog() }
        mLoopStation.linkVisualizer(realtime_visualizer_view)
        metronome_view.onStart = { mLoopStation.MetronomeStart() }
        metronome_view.onStop = { mLoopStation.MetronomeStop() }
    }

    override fun onDestroy() {
        mLoopStation.stopAll()
        super.onDestroy()
        Log.d("RecordFragment", "RecordFragment Destroyed!")
    }

    override fun onPause() {
        super.onPause()
        Log.d("RecordFragment", "RecordFragment Paused!")
    }

    private fun onThisSingleTap(): Boolean {
        if (mLoopStation.isRecording()) { mLoopStation.recordStop() }
        else { mLoopStation.recordStart() }
        return true
    }

    private fun onThisSwipeToUp(): Boolean {
        if (mLoopStation.isLooping()) { mLoopStation.loopStop() }
        else { mLoopStation.loopStart() }
        return true
    }

    private fun onThisSwipeToDown(): Boolean {
        if(mLoopStation.isRecording()) {
            toast(R.string.toast_save_error_while_record)
            return true
        }
        if(mLoopStation.isLooping()) mLoopStation.loopStop(messageFlag = false)
        showSaveLoopDialog()
        return true
    }

    private fun onLayerClick(parent: AdapterView<*>, view: View, position: Int) {
        if(mLoopStation.isLooping()) {
            val muteState = mLoopStation.muteLayer(position)
            mLayerListAdapter.setLayerMuteState(position,view, muteState)
        } else {
            mLoopStation.playLayer(position)
        }
    }

    private fun showSaveLoopDialog() {
        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            noAutoDismiss()
            title(R.string.title_save_loop)
            customView(R.layout.dialog_save_loop, horizontalPadding = true)
            cornerRadius(16f)
            cancelable(false)
            positiveButton(R.string.btn_save) {
                // callback on positive button click
                val loopTitle = it.edit_loop_title.text.toString()
                if(loopTitle == ""){
                    toast(R.string.toast_save_error_without_title)
                } else {
                    val fileType = spinner.selectedItem.toString()
                    val isSaveChecked = check_split.isChecked
                    val isDropChecked = check_drop.isChecked
                    Log.d("SaveDialog", "\nloopTitle: $loopTitle, \nfileType: $fileType, \nisSaveChecked: $isSaveChecked")
                    val isSaved = mLoopStation.export(loopTitle, fileType, isSaveChecked, isDropChecked)

                    if(isSaved != LoopStation.SAVE_SUCCESS) toast(getString(R.string.toast_save))
                    else toast(R.string.toast_save_error_failed)

                    it.dismiss()
                }
            }
            negativeButton(R.string.btn_cancel) {
                it.dismiss()
            }
            lifecycleOwner(activity)
        }
    }

    private fun showDropAllLayerDialog() {
        if(mLoopStation.isRecording()) return

        MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.title_drop_all_of_layer)
            message(R.string.query_drop_all_of_layer)
            cornerRadius(16f)
            cancelable(false)
            positiveButton(R.string.btn_ok) {
                // callback on positive button click
                mLoopStation.dropAllLayer()
            }
            negativeButton(R.string.btn_cancel)
            lifecycleOwner(activity)
        }
    }

    private fun initLoopStation() {
        mLoopStation.setLoopStationEventListener(MyLoopStationEventListener())
        mLoopStation.setLoopStationMessageListener(MyLoopStationMessageListener())
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
            activity?.runOnUiThread { mLayerListAdapter.dropAllLayer() }
        }
        override fun onImport(loopTitle: String, newLoadFlag: Boolean) {
            activity?.runOnUiThread {
                loop_title_label.text = loopTitle
                mLayerListAdapter.setLayerItemList(
                    mLoopStation.getSounds(),
                    mLoopStation.getLayerLabels() )
            }
        }
        override fun onRecordStart() {
            val layerList = layer_list
            layerList.setOnTouchListener(null)
            layerList.setOnScrollListener(null)
        }
        override fun onRecordFinish() {
            val layerList = layer_list
            val swipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(layerList, SwipeDismissListener())
            layerList.setOnTouchListener(swipeDismissListViewTouchListener)
            layerList.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener())
        }
    }

    private inner class MyLoopStationMessageListener: LoopStation.LoopStationMessageListener {
        override fun onDuplicateRecordStartError() {}
        override fun onRecordWithoutLoopingError() { toast(R.string.toast_record_start_error_without_playback) }
        override fun onSaveDuringRecordingError() { toast(R.string.toast_save_error_while_record) }
        override fun onSaveNoneLayerError() { toast(R.string.toast_save_error_no_layer) }
    }

    private inner class SwipeDismissListener : SwipeDismissListViewTouchListener.DismissCallbacks{
        override fun canDismiss(position: Int): Boolean {
            return true
        }

        override fun onDismiss(listView: ListView?, reverseSortedPositions: IntArray?) {
            for(position in reverseSortedPositions!!) {
                mLoopStation.dropLayer(position)
            }
        }
    }

}
