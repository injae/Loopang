package com.treasure.loopang

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.AdapterView
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
import kotlin.math.abs

private const val SWIPE_THRESHOLD = 100    // 스와이프 진단을 위한 위치차 임계치
private const val SWIPE_VELOCITY_THRESHOLD = 100   // 스와이프 진단을 위한 속도 임계치

class RecordFragment : androidx.fragment.app.Fragment() {
    private val trackItemList : ArrayList<TrackItem> = arrayListOf()
    private val trackListAdapter : TrackListAdapter = TrackListAdapter(trackItemList)
    private lateinit var visualizerUpdater : VisualizerUpdater
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
        track_list.setOnItemLongClickListener{ parent, v, position, id ->
            processWhenItemLongClicked(parent, v, position, id)
        }

        /* 리스트 아이템 싱글 클릭 이벤트 설정 */
        track_list.setOnItemClickListener { parent, v, position, id ->
            processWhenItemClicked(parent, v, position, id)
        }

        recording_title.setOnClickListener {
            MaterialDialog(context!!).show{
                input { _, text ->
                    changeSongName(text.toString())
                }
            }
        }
    }

    private fun changeSongName(text: String) {
        recording_title.text = text
    }

    override fun onDestroy() {
        looper.recorder.stop()
        looper.mixer.stop()
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
        if (looper.mixerCount == -1){
            Toast.makeText(this.context,"You have to swipe up at first time",Toast.LENGTH_SHORT).show()
            return
        }

        looper.recordAction()
        if(looper.checkRecordingState()){
            Toast.makeText(this.context,"Make New Track and Recording Start!",Toast.LENGTH_SHORT).show()
            addTrack()
        } else {
            Toast.makeText(this.context,"Recording Stop!",Toast.LENGTH_SHORT).show()
        }

    }

    // 위로 스와이프 시의 처리동작
    private fun processWhenSwipeToUp() {
        if (looper.mixerCount == -1)
            Toast.makeText(this.context,"Make New Track and Recording Start!",Toast.LENGTH_SHORT).show()
        else if (looper.mixerCount == 0)
            Toast.makeText(this.context,"PlayBack Stop!",Toast.LENGTH_SHORT).show()
        else if (looper.mixerCount == 1 && trackItemList.size == 0){
            Toast.makeText(this.context,"There is no any Track!",Toast.LENGTH_SHORT).show()
            return
        }
        else if (looper.mixerCount == 1)
            Toast.makeText(this.context,"PlayBack!",Toast.LENGTH_SHORT).show()

        looper.mixerAction()

        if(looper.checkRecordingState()){
            addTrack()
        }
    }

    private fun processWhenSwipeToDown() {
        if(trackItemList.isEmpty()){
            Toast.makeText(this.context,"Please make Track at least 1",Toast.LENGTH_SHORT).show()
            return
        }

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
        if(looper.mixer.isPlaying.get()) return false
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
        return true
    }

    private fun addTrack() {
        val trackItem = TrackItem()
        trackItem.trackName ="Track${trackItemList.size+1}"
        trackListAdapter.addItem(trackItem)
        visualizerUpdater = VisualizerUpdater()
        visualizerUpdater.start()
    }

    inner class VisualizerUpdater : Thread() {
        override fun run() {
            while(track_list.childCount != trackItemList.size) {
                SystemClock.sleep(200)
            }
            val currentItemView:View          = track_list.getChildAt(0)
            val visualizerView:VisualizerView = currentItemView.visualizer
            var nowMaxAmplitude: Float

            visualizerView.clear()
            while(looper.checkRecordingState()) {
                activity?.runOnUiThread {
                    nowMaxAmplitude = looper.recorder.maxAmplitude.get().toFloat()
                    visualizerView.addAmplitudeAndInvalidate(nowMaxAmplitude)
                }
                SystemClock.sleep(20)
            }
            trackItemList[0].amplitudes = visualizerView.amplitudes
        }
    }
}
