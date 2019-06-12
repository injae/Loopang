package com.treasure.loopang

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.treasure.loopang.adapter.TrackListAdapter
import com.treasure.loopang.audio.*
import com.treasure.loopang.customview.VisualizerView
import com.treasure.loopang.listitem.TrackItem
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.tracklist_item.view.*
import kotlinx.android.synthetic.main.tracklist_item.view.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

private const val SWIPE_THRESHOLD = 100    // 스와이프 진단을 위한 위치차 임계치
private const val SWIPE_VELOCITY_THRESHOLD = 100   // 스와이프 진단을 위한 속도 임계치

class RecordFragment : androidx.fragment.app.Fragment() {
    private val trackItemList : ArrayList<TrackItem> = arrayListOf()
    private val trackListAdapter : TrackListAdapter = TrackListAdapter(trackItemList)
    var looper: Looper = Looper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track_list.adapter = trackListAdapter

        /* 프래그먼트 전체에 해당하는 제스쳐 이벤트 */
        val gesture = GestureDetector(view.context, RecordFragmentGestureListener())
        view.setOnTouchListener{ _, event -> gesture.onTouchEvent(event)}

        /* 리스트 아이템 롱클릭 이벤트 설정 */
        track_list.isLongClickable = true
        track_list.setOnItemLongClickListener{ parent, view, position, id ->
            processWhenItemLongClicked(parent, view, position, id)
        }

        /* 리스트 아이템 싱글 클릭 이벤트 설정 */
        track_list.setOnItemClickListener { parent, view, position, id ->
            processWhenItemClicked(parent, view, position, id)
        }

        recording_title.setOnClickListener {
            MaterialDialog(context!!).show{
                input { _, text ->
                    changeSongName(text.toString())
                }
            }
        }
    }

    fun changeSongName(text: String) {
        recording_title.text = text
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RecordFragment", "RecordFragment Destroyed!")
    }

    override fun onPause() {
        super.onPause()
        Log.d("RecordFragment", "RecordFragment Paused!")
    }

    // 스와이프와 싱글탭 이벤트 리스너 구현을 위한 제스처 디텍터 리스너
    inner class RecordFragmentGestureListener : GestureDetector.OnGestureListener {

        /* 각 이벤트 시 어떤 처리를 할 것인지 결정 */
        private fun onSingleTap() = processWhenSingleTaped()
        private fun onSwipeUp() = processWhenSwipeToUp()
        private fun onSwipeDown() = processWhenSwipeToDown()
        private fun onSwipeLeft() {} // = processWhenSwipeToLeft
        private fun onSwipeRight() {} // = processWhenSwipeToRight

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            onSingleTap()
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            var result = false

            if(e1 == null || e2 == null) return result
            val diffY: Float = e2.y - e1.y
            val diffX: Float = e2.x - e1.x
            if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) onSwipeRight()
                else onSwipeLeft()
                result = true
            }
            if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) onSwipeDown()
                else onSwipeUp()
                result = true
            }
            return result
        }

        override fun onDown(e: MotionEvent?): Boolean = true

        override fun onShowPress(e: MotionEvent?) {}

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = false

        override fun onLongPress(e: MotionEvent?) {}
    }

    /******************************* 제스쳐 이벤트 처리 *******************************/
    // 싱글 탭 시의 처리동작
    private fun processWhenSingleTaped() {
        Toast.makeText(this.context,"tap",Toast.LENGTH_SHORT).show()

        looper.recordAction()

        if(looper.checkRecordingState()){
            addTrack()
            VisualizerUpdater().start()
        }

    }

    // 위로 스와이프 시의 처리동작
    private fun processWhenSwipeToUp() {
        Toast.makeText(this.context,"swipe",Toast.LENGTH_SHORT).show()

        if (looper.mixerCount == 1 && trackItemList.size == 0) return
        looper.mixerAction()

        if(looper.checkRecordingState()){
            addTrack()
            VisualizerUpdater().start()
        }

    }

    private fun processWhenSwipeToDown() {
        val fileManager = FileManager()
        val path = fileManager.looperDir.absolutePath
        val name: String = recording_title.text.toString()

        Sound(looper.mixer.mixSounds()).save(path+'/'+name)
        Log.d("RecordFragmentTest", "아래로 스와이프 하셨습니다.")
    }

    /*
    private fun processWhenSwipeToRight() {
        //Log.d("RecordFragmentTest", "위로 스와이프 하셨습니다.")
    }

    private fun processWhenSwipeToLeft() {
        //Log.d("RecordFragmentTest", "위로 스와이프 하셨습니다.")
    }
    */

    /* 리스트 아이템 클릭 시 처리동작 (onItemClick 함수와 같이 사용) */
    private fun processWhenItemClicked(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.d("RecordFragmentTest", "아이템 클릭! postion: $position")
    }

    private fun processWhenItemLongClicked(parent: AdapterView<*>, view: View, position: Int, id: Long) : Boolean {
        if(looper.mixer.isPlaying.get()) return false;
        val menuList = listOf("Drop Track")
        val context = this.context!!

        MaterialDialog(context).show {
            listItems(items = menuList) { _, index, _ ->
                when (index) {
                    0 -> {
                        looper.mixer.sounds.removeAt(looper.mixer.sounds.size - (position+1))
                        trackListAdapter.removeItem(position)
                        Toast.makeText(this.context, "track is removed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        Log.d("RecordFragmentTest", "아이템 롱 클릭! postion: $position")
        return true
    }

    private fun addTrack() {
        val trackItem = TrackItem()
        trackItem.trackName ="Track${trackItemList.size+1}"
        trackListAdapter.addItem(trackItem)
    }

    inner class VisualizerUpdater : Thread() {
        override fun run() {
            while(track_list.childCount != trackItemList.size) {
                SystemClock.sleep(200)
            }
            val currentItemView:View = track_list.getChildAt(0)
            val visualizerView:VisualizerView = currentItemView.visualizer
            val trackName:TextView
            var nowMaxAmplitude: Float

            while(looper.checkRecordingState()) {
                activity?.runOnUiThread {
                    // currentItemView = trackListAdapter.getView(0, null, track_list)
                    // visualizerView  = currentItemView.visualizer
                    nowMaxAmplitude = looper.recorder.maxAmplitude.get().toFloat()
                    visualizerView.addAmplitudeAndInvalidate(nowMaxAmplitude)
                    Log.d("VisualizerUpdater","visualizer update ${currentItemView.track_name.text}")
                }
                SystemClock.sleep(20)
                Log.d("VisualizerUpdater","visualizer update")
            }
        }
    }
}
