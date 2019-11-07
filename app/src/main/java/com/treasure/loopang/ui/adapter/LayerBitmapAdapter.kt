package com.treasure.loopang.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.treasure.loopang.R
import kotlinx.android.synthetic.main.layer_amplitudes_item.view.*

class LayerBitmapAdapter(var itemList: ArrayList<Bitmap>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val layerImageHolder: LayerImageHolder
        val image = itemList[position]

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context).inflate(
                R.layout.layer_amplitudes_item, parent, false
            )

            layerImageHolder = LayerImageHolder()
            layerImageHolder.imageView = view.layer_iv

            view.tag = layerImageHolder
        } else {
            view = convertView
            layerImageHolder = view.tag as LayerImageHolder
        }

        layerImageHolder.imageView.setImageBitmap(image)

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

class LayerImageHolder{
    lateinit var imageView: ImageView
}