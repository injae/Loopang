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
import kotlinx.android.synthetic.main.activity_community.*

class CommunityShareFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_share_fragment,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("들어옴?","들어왔다.")
        (activity as CommunityShareActivity).isShareing = true
        //(activity as CommunityShareActivity).layerItem >> 어떤 애를 선택했는 지 알려줌 ㅇㅇ

        var post : String
        writePostAboutLayer.setEnabled(true)
        writePostAboutLayer.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(edit: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                post = writePostAboutLayer.toString()
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
            val intent = Intent(activity, CommunityActivity::class.java)  //intent.putExtra()
            intent.putExtra("finish","true")
            intent.putExtra("from", "CommunityShareFragment")
            startActivity(intent)

        }
    }
}