package com.treasure.loopang

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import com.treasure.loopang.adapter.TrackListAdapter
import com.treasure.loopang.audio.*
import com.treasure.loopang.listitem.TrackItem
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

private const val SWIPE_THRESHOLD = 100    // 스와이프 진단을 위한 위치차 임계치
private const val SWIPE_VELOCITY_THRESHOLD = 100   // 스와이프 진단을 위한 속도 임계치

class RecordFragment : androidx.fragment.app.Fragment() {
    private val trackItemList : ArrayList<TrackItem> = arrayListOf()
    var looper: Looper = Looper()

    /******************************* 제스쳐 이벤트 처리 *******************************/
    // 싱글 탭 시의 처리동작
    private fun processWhenSingleTaped() {
        Toast.makeText(this.context,"tap",Toast.LENGTH_SHORT).show()
        looper.recordAction()
        Log.d("RecordFragmentTest", "한번 탭 하셨습니다.")
    }

    // 위로 스와이프 시의 처리동작
    private fun processWhenSwipeToUp() {
        Toast.makeText(this.context,"swipe",Toast.LENGTH_SHORT).show()
        Log.d("RecordFragmentTest", "위로 스와이프 하셨습니다.")
        looper.mixerAction()
    }

    /* 스와이프 처리 함수들
    private fun processWhenSwipeToDown() {
        //Log.d("RecordFragmentTest", "위로 스와이프 하셨습니다.")
    }

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
        Log.d("RecordFragmentTest", "아이템 롱 클릭! postion: $position")
        return true
    }

    /********************** 프래그먼트 라이프사이클 관련 이벤트 **********************/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackListAdapter = TrackListAdapter(trackItemList)
        recording_sound_list.adapter = trackListAdapter

        /* 프래그먼트 전체에 해당하는 제스쳐 이벤트 */
        val gesture = GestureDetector(view.context, RecordFragmentGestureListener())
        view.setOnTouchListener{ _, event -> gesture.onTouchEvent(event)}

        /* 리스트 아이템을 제외한 리스트뷰 영역을 위한 터치 이벤트 리스너 */
        // recording_sound_list.setOnTouchListener(ListTouchListener())

        /* 리스트 아이템 롱클릭*/
        recording_sound_list.isLongClickable = true
        recording_sound_list.setOnItemLongClickListener{ parent, view, position, id ->
            processWhenItemLongClicked(parent, view, position, id)
        }

        /* 리스트 아이템 클릭 */
        recording_sound_list.setOnItemClickListener { parent, view, position, id ->
            processWhenItemClicked(parent, view, position, id)
        }


        /* 테스트 코드 */
        for(i in 1..3) trackItemList.add(TrackItem())
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
        private fun onSwipeDown() {} // = processWhenSwipeToDown
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

/*

    inner class ListTouchListener : View.OnTouchListener {
        var x: Float = 0.0f
        var y: Float = 0.0f
        var isSwiped: Boolean = false


/* 각 이벤트 시 어떤 처리를 할 것인지 결정 */

        private fun onSingleTap() = processWhenSingleTaped()
        private fun onSwipeUp() = processWhenSwipeToUp()
        private fun onSwipeDown() {}
        private fun onSwipeLeft() {}
        private fun onSwipeRight() {}

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event == null || v == null) return false

            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    x = event.x
                    y = event.y
                    Log.d("ListTouchListener", "$v.id")
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if(isSwiped) return false   //스와이프 이벤트 중복 호출 방지

                    val diffY: Float = event.y - y
                    val diffX: Float = event.x - x
                    if (abs(diffX) > SWIPE_THRESHOLD) {
                        if (diffX > 0) onSwipeRight()
                        else onSwipeLeft()
                        isSwiped = true
                        return true
                    }
                    if (abs(diffY) > SWIPE_THRESHOLD) {
                        if (diffY > 0) onSwipeDown()
                        else onSwipeUp()
                        isSwiped = true
                        return true
                    }
                    isSwiped = false
                    return false
                }
                MotionEvent.ACTION_UP -> {
                    if(isSwiped) {
                        isSwiped = false
                    }
                    return false
                }
                else -> return false
            }
        }
    }
*/

}
