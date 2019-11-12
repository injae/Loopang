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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CommunityShareFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_share_fragment,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as CommunityShareActivity).isShareing = true
        var post = ""
        writePostAboutLayer.setEnabled(true)
        writePostAboutLayer.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(edit: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { post = writePostAboutLayer.text.toString() } })
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
            val ld = LoadingActivity(activity!!)
            GlobalScope.launch {
                CoroutineScope(Dispatchers.Main).launch { ld.show() }
                at.connector.process(ResultManager.FILE_UPLOAD, null, at.layerItem.loopTitle, null, null, makeUploadInfo(post, at))
                CoroutineScope(Dispatchers.Main).launch { ld.dismiss() }
            }
            val intent = Intent(activity, CommunityActivity::class.java)
            intent.putExtra("finish","true")
            intent.putExtra("from", "CommunityShareFragment")
            startActivity(intent)

        }
    }

    private fun makeUploadInfo(post: String, activity: CommunityShareActivity)
            = MusicListClass("",
        activity.parent_name + '_' + activity.layerItem.loopTitle + '.' + activity.layerItem.extension,
        "", "",0, 0, post, activity.userTagSet.toList())
}