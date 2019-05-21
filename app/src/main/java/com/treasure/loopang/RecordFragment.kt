package com.treasure.loopang

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import com.jakewharton.rxbinding3.widget.itemClicks
import com.treasure.loopang.adapter.TrackListAdapter
import com.treasure.loopang.listitem.TrackItem
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_record.*

class RecordFragment : Fragment() {

    private val trackItemList : ArrayList<TrackItem> = arrayListOf<TrackItem>()
    private val disposables by lazy { CompositeDisposable() }

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

        recording_sound_list.setOnTouchListener{ _,_ -> false }

        /* 프래그먼트 전체에 해당하는 제스쳐 이벤트 */
        val gesture = GestureDetector(view.context, RecordFragmentGestureListener())
        view.setOnTouchListener{ _, event -> gesture.onTouchEvent(event)}

        /* 리스트 아이템 클릭 */
        recording_sound_list
            .itemClicks()
            .subscribe {
                Log.d("RecordFragment", "item Click!!")
            }.apply { disposables.add(this) }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RecordFragment", "RecordFragment Destroyed!")
    }

    inner class RecordFragmentGestureListener : GestureDetector.OnGestureListener {
        private var currentX: Float = 0.0f
        private var currentY: Float = 0.0f

        private fun processWhenSingleTaped() {
            /*여기에 한번 탭했을 때 처리를 작성*/
            Log.d("RecordFragmentTest", "한번 탭 하셨습니다.")
        }
        private fun processWhenSwipeToUp() {
            /* 위로 스와이프 했을때의 처리를 작성 */
            Log.d("RecordFragmentTest", "위로 스와이프 하셨습니다.")
        }

        override fun onDown(e: MotionEvent?): Boolean {
            this.currentX = e!!.x
            this.currentY = e!!.y

            return true
        }

        override fun onShowPress(e: MotionEvent?) {}

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            val x = e!!.x
            val y = e!!.y

            /* simple touch */
            if(currentX == x && currentY == y)
                processWhenSingleTaped()
            else if(currentY != y)
                processWhenSwipeToUp()

            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = false

        override fun onLongPress(e: MotionEvent?) {}
     }


}


