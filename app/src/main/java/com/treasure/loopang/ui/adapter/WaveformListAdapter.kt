package com.treasure.loopang.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.treasure.loopang.R
import com.treasure.loopang.ui.view.WaveformView
import kotlinx.android.synthetic.main.waveform_item.view.*

class WaveformListAdapter(var itemList: List<MutableList<Short>>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val waveformHolder: WaveformHolder
        val item = itemList[position]

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate(
                R.layout.waveform_item, parent, false
            )

            waveformHolder = WaveformHolder()
            waveformHolder.waveform = view.vd_waveform

            view.tag = waveformHolder
        } else {
            view = convertView
            waveformHolder = view.tag as WaveformHolder
        }

        waveformHolder.waveform.amplitudes = item

        return view
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return itemList.size
    }
}

class WaveformHolder{
    lateinit var waveform: WaveformView
}