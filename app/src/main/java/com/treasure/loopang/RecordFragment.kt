package com.treasure.loopang

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.adapter.TrackListAdapter
import com.treasure.loopang.listitem.TrackItem
import kotlinx.android.synthetic.main.fragment_record.*

class RecordFragment : Fragment() {

    private val trackItemList : ArrayList<TrackItem> = arrayListOf<TrackItem>()

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
    }

}
