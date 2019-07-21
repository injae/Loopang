package com.treasure.loopang

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.*
import android.widget.AdapterView
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.treasure.loopang.audiov2.FileManager
import com.treasure.loopang.audiov2.Mixer
import com.treasure.loopang.audiov2.Recorder
import com.treasure.loopang.audiov2.Sound
import com.treasure.loopang.ui.adapter.LayerListAdapter
import com.treasure.loopang.ui.listener.TouchGestureListener
import com.treasure.loopang.ui.toast
import kotlinx.android.synthetic.main.dialog_save_loop.*
import kotlinx.android.synthetic.main.fragment_record.*
import kotlin.math.abs

class RecordFragment : androidx.fragment.app.Fragment() {
    private val mLayerListAdapter : LayerListAdapter = LayerListAdapter()
    private val mTouchGestureListener = TouchGestureListener()

    private var mixer: Mixer = Mixer()
    private var recorder: Recorder = Recorder()

    private var loopTitle: String = ""
        set(value) {
            changeLoopTitle(value)
            field = value
        }

    private val mFileManager = FileManager()
    private val mDirectoryPath = mFileManager.looperDir.absolutePath

    private var mLoopPlaybackState: Boolean = false
    private var mRecordState: Boolean = false

    init{
        mTouchGestureListener.onSingleTap = { onThisSingleTap() }
        mTouchGestureListener.onSwipeToDown = { onThisSwipeToDown() }
        mTouchGestureListener.onSwipeToUp = { onThisSwipeToUp() }

        initRecorder()
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

        layer_list.adapter = mLayerListAdapter

        /* 프래그먼트 전체에 해당하는 제스쳐 이벤트 */
        val gesture = GestureDetector(view.context, mTouchGestureListener)
        view.setOnTouchListener{ _, event -> gesture.onTouchEvent(event)}

        /* 리스트 아이템 롱클릭 이벤트 설정 */
        layer_list.isLongClickable = true
        layer_list.setOnItemLongClickListener{ parent, v, position, id ->
            onLayerListItemLongClick(parent, v, position, id)
        }

        /* 리스트 아이템 싱글 클릭 이벤트 설정 */
        layer_list.setOnItemClickListener { parent, v, position, id ->
            onLayerListItemClick(parent, v, position, id)
        }

        loop_title_label.setOnClickListener {
            var loopTitle = ""
            MaterialDialog(context!!).show{
                title(R.string.title_loop_title)
                input { _, text ->
                    loopTitle = text.toString()
                }
                positiveButton(R.string.btn_ok) {
                    changeLoopTitle(loopTitle)
                }
                negativeButton(R.string.btn_cancel)
            }
        }
    }

    override fun onDestroy() {
        recorder.stop()
        mixer.stop()
        super.onDestroy()
        Log.d("RecordFragment", "RecordFragment Destroyed!")
    }

    override fun onPause() {
        super.onPause()
        Log.d("RecordFragment", "RecordFragment Paused!")
    }

    private fun initRecorder(){
        recorder.onSuccess {
            addLayer()
            mRecordState = false
        }
        recorder.onStart {
            mRecordState = true
        }
        // recorder.onSuccess { Log.d("recorder","recorder.onSuccess()")}
        // For Realtime Visualizer
        recorder.addEffector {
            realtime_visualizer_view.analyze(
                it.fold(0) { acc, next->
                    acc + abs(next.toInt())
                } / it.size
            )
            it
        }
    }

    private fun initMixer() {}

    private fun addLayer() {
        activity?.runOnUiThread {
            toast(R.string.toast_record_stop)
            if(realtime_visualizer_view.visibility == View.VISIBLE) {
                realtime_visualizer_view.clear()
                realtime_visualizer_view.visibility = View.GONE
            }
        }

        val sound = recorder.getSound()
        mixer.addSound(sound)
        if(!mixer.isLooping.get()) mixer.start()
        mLayerListAdapter.addLayer(sound)
    }

    private fun changeLoopTitle(string: String){
        loop_title_label.text = string
    }

    private fun onThisSingleTap(): Boolean {
        if(mixer.sounds.isNotEmpty() && !mixer.isLooping.get()){
            toast(R.string.toast_record_start_error_without_playback)
        }
        else if (mRecordState) {
            recorder.stop()
        }
        else {
            toast(R.string.toast_record_start)
            if(realtime_visualizer_view.visibility == View.GONE) {
                realtime_visualizer_view.visibility = View.VISIBLE
            }
            if(mixer.sounds.isNotEmpty()) { recorder.start(mixer.sounds[0].data.size) }
            else { recorder.start() }
        }

        return true
    }

    private fun onThisSwipeToUp() {
        if (mRecordState){
            toast(R.string.toast_playback_stop_error)
            return
        }
        if (!mixer.isLooping.get() && mixer.sounds.isEmpty()){
            toast(R.string.toast_record_start)
        }
        else if (mixer.isLooping.get()) {
            toast(R.string.toast_playback_stop)
            mixer.stop()
        }
        else {
            toast(R.string.toast_playback_start)
            mixer.start()
        }
    }

    private fun onThisSwipeToDown() {
        Log.d("RecordFragmentTest", "아래로 스와이프 하셨습니다.")
        if(mRecordState) {
            toast(R.string.toast_save_error_while_record)
            return
        }
        if(mixer.isLooping.get()) mixer.stop()

        showSaveLoopDialog()
    }

    /* 리스트 아이템 클릭 시 처리동작 (onItemClick 함수와 같이 사용) */
    private fun onLayerListItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.d("RecordFragmentTest", "아이템 클릭! postion: $position")
    }

    private fun onLayerListItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long) : Boolean {
/*        if(mixer.isPlaying.get()) return false
        val menuList = listOf("Drop Track")
        val context = this.context!!

        MaterialDialog(context).show {
            listItems(items = menuList) { _, index, _ ->
                when (index) {
                    0 -> {
                        looper.mixer.sounds.removeAt(looper.mixer.sounds.size - (position+1))
                        trackListAdapter.removeItem(position)
                        Toast.makeText(this.context, "track is droped!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        Log.d("RecordFragmentTest", "아이템 롱 클릭! postion: $position")
        return true*/
        return false
    }

    private fun showSaveLoopDialog() {
        if(mixer.sounds.isEmpty()){
            toast(R.string.toast_save_error_no_layer)
            return
        }

        val dialog = MaterialDialog(activity!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.title_save_loop)
            customView(R.layout.dialog_save_loop, horizontalPadding = true)
            cancelable(false)
            positiveButton(R.string.btn_save) {
                // callback on positive button click
                val loopTitle = it.edit_loop_title.text.toString()
                val fileType = spinner.selectedItem.toString()
                val isSaved = saveLoop(loopTitle, fileType)
                if(isSaved) toast(getString(R.string.toast_save))
                else toast(R.string.toast_save_error_failed)
            }
            negativeButton(R.string.btn_cancel)
            lifecycleOwner(activity)
        }
        dialog.edit_loop_title.text = SpannableStringBuilder(loopTitle)
    }

    private fun saveLoop(loopTitle: String, fileType: String): Boolean {
        if(mixer.sounds.isEmpty()){
            toast(R.string.toast_save_error_no_layer)
            return false
        }
        val fileLabel = when(fileType){
            "PCM" -> {
                "/$loopTitle.pcm"
            }
            "WAV" -> {
                "/$loopTitle.wav"
            }
            else -> {
                "/$loopTitle"
            }
        }

        Sound(mixer.mixSounds()).save(mDirectoryPath+fileLabel)

        return true
    }

    private fun dropLayer(position: Int) {

    }
}
