package com.treasure.loopang

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.community_share_fragment.*
import android.graphics.Color
import android.util.Log
import android.widget.ToggleButton
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.communication.ResultManager
import com.treasure.loopang.ui.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER

class CommunityShareFragment : androidx.fragment.app.Fragment() {
    private var enterNum : Int =0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.community_share_fragment,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as CommunityShareActivity).isSharinginFrag = true
        var post = ""
        enterNum = 0
        share_layer_title.setText((activity as CommunityShareActivity).layerItem.loopTitle)
        share_typeTextView.setText((activity as CommunityShareActivity).layerItem.fileType)
        share_layerDate.setText((activity as CommunityShareActivity).layerItem.dateString)

        writePostAboutLayer.setEnabled(true)
        writePostAboutLayer.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(edit: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (writePostAboutLayer.text.toString().toByteArray().size < 100) {
                    post = writePostAboutLayer.text.toString()
                }else{
                    toast("글자 수 제한")
                    writePostAboutLayer.setText(post)
                }

            }
        })

        val tagButtonList : List<ToggleButton> = listOf(tagBeat,tagAcappella,tagClap,tagDrum,tagJanggu,tagPercussionInstrument,tagPiano,tagViolin)
        for(tagButton in tagButtonList){
            tagButton.setOnClickListener {
                if (tagButton.isChecked == true) {
                    tagButton.setTextColor(Color.WHITE)
                    (activity as CommunityShareActivity).userTagSet.add(tagButton.text.toString())
                } else {
                    tagButton.setTextColor(Color.argb(200, 136, 136, 136))
                    (activity as CommunityShareActivity).userTagSet.remove(tagButton.text.toString())
                }
            }
        }
        shareButton.setOnClickListener {
            val at = (activity as CommunityShareActivity)
            if((activity as CommunityShareActivity).userTagSet.isEmpty()){
                toast("TAG를 선택해주세요.")
            }else{
                if(at.layerItem.extension == "pcm") {
                    val ld = LoadingActivity(activity!!)
                    GlobalScope.launch {
                        CoroutineScope(Dispatchers.Main).launch { ld.show() }
                        at.connector.process(ResultManager.FILE_UPLOAD, null, at.layerItem.loopTitle, null, null, makeUploadInfo(post, at))
                        at.connector.process(ResultManager.INFO_REQUEST)
                        CoroutineScope(Dispatchers.Main).launch {
                            ld.dismiss()
                            (activity as CommunityShareActivity).shareFinish()
                        }}
                }
                else {
                    toast("레이어만 업로드 할 수 있습니다.")
                }
            }

        }
    }

    private fun makeUploadInfo(post: String, activity: CommunityShareActivity)
            = MusicListClass("",
        activity.parent_name + '_' + activity.layerItem.loopTitle + '.' + activity.layerItem.extension,
        "", "",0, 0, post, activity.userTagSet.toList())
}