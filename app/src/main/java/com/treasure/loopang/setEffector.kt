package com.treasure.loopang.listitem

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.adapter.EffectorListAdapter
import kotlinx.android.synthetic.main.set_effector_frame.*
import android.util.Log

class setEffector : androidx.fragment.app.Fragment() {
    val adapter: EffectorListAdapter =  EffectorListAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.set_effector_frame,container,false);
    }
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        effectorListView.adapter =adapter

        effectorListView.setOnItemClickListener { parent, view, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as EffectorListItem
            val title = item.title
            val music = item.music
            val isPlayingMusic = item.isPlayingMusic
            // TODO : use item data.
        }
    }
}
