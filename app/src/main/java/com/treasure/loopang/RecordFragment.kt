package com.treasure.loopang

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.treasure.loopang.audiov2.FileManager
import com.treasure.loopang.audiov2.Mixer
import com.treasure.loopang.audiov2.Recorder
import com.treasure.loopang.audiov2.Sound
import com.treasure.loopang.ui.adapter.LayerListAdapter
import com.treasure.loopang.ui.listener.TouchGestureListener
import kotlinx.android.synthetic.main.fragment_record.*
import java.lang.RuntimeException

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

    init{
        mTouchGestureListener.onSingleTap = { onThisSingleTap() }
        mTouchGestureListener.onSwipeToDown = { onThisSwipeToDown() }
        mTouchGestureListener.onSwipeToUp = { onThisSwipeToUp() }

        recorder.onSuccess { addLayer() }
        // recorder.onSuccess { Log.d("recorder","recorder.onSuccess()")}
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
            MaterialDialog(context!!).show{
                input { _, text ->
                    changeLoopTitle(text.toString())
                }
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

    private fun changeLoopTitle(string: String){
        loop_title_label.text = string
    }

    private fun addLayer() {
        try {
            Toast.makeText(this.context,"Recording Stop!",Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            activity?.runOnUiThread {
                Toast.makeText(this.context,"Recording Stop!",Toast.LENGTH_SHORT).show()
            }
        }
        val sound = recorder.getSound()
        mixer.addSound(sound)
        if(!mixer.isLooping.get()) mixer.start()
        mLayerListAdapter.addLayer(sound)
    }

    private fun onThisSingleTap(): Boolean {
        if(mixer.sounds.isNotEmpty() && !mixer.isLooping.get()){
            Toast.makeText(this.context,"You can start record only loop is playing!",Toast.LENGTH_SHORT).show()
        }
        else if (recorder.isRecording.get()) {
            recorder.stop()
        }
        else {
            Toast.makeText(this.context,"Record New Layer!",Toast.LENGTH_SHORT).show()
            if(mixer.sounds.isNotEmpty()) { recorder.start(mixer.sounds[0].data.size) }
            else { recorder.start() }
        }

        return true
    }

    private fun onThisSwipeToUp() {
        if (recorder.isRecording.get()){
            Toast.makeText(this.context,"You can't stop Looper during recording.",Toast.LENGTH_SHORT).show()
        }
        if (!mixer.isLooping.get() && mixer.sounds.isEmpty()){
            Toast.makeText(this.context,"Make New Track and Recording Start!",Toast.LENGTH_SHORT).show()
        }
        else if (mixer.isLooping.get()) {
            Toast.makeText(this.context, "PlayBack Stop!", Toast.LENGTH_SHORT).show()
            mixer.stop()
        }
        else {
            Toast.makeText(this.context,"PlayBack!",Toast.LENGTH_SHORT).show()
            mixer.start()
        }
    }

    private fun onThisSwipeToDown() {
        if(mixer.sounds.isEmpty()){
            Toast.makeText(this.context,"Please make Track at least 1",Toast.LENGTH_SHORT).show()
            return
        }

        val fileManager = FileManager()
        val path = fileManager.looperDir.absolutePath
        val name: String = loop_title_label.text.toString()

        Sound(mixer.mixSounds()).save(path+'/'+ name + ".pcm")
        Log.d("RecordFragmentTest", "아래로 스와이프 하셨습니다.")
    }

    /* 리스트 아이템 클릭 시 처리동작 (onItemClick 함수와 같이 사용) */
    private fun onLayerListItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.d("RecordFragmentTest", "아이템 클릭! postion: $position")
    }

    private fun onLayerListItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long) : Boolean {
/*        if(looper.mixer.isPlaying.get()) return false
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
}
