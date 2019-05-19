package com.treasure.loopang

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_record.*

class RecordFragment : Fragment() {

    private val trackItemList = arrayListOf<TrackItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val trackListAdapter = TrackListAdapter(trackItemList)

        /*
            TODO("recording_sound_list null pointer exception 고쳐야함")
            recording_sound_list.adapter = trackListAdapter
        */

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }
}
