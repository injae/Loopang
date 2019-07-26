package com.treasure.loopang.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.R
import com.treasure.loopang.listitem.EffectorListItem
import kotlinx.android.synthetic.main.effectorlist.view.*

class EffectorListAdapter : BaseAdapter() {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private var listViewItemList = ArrayList<EffectorListItem>()

    // Adapter에 사용되는 데이터의 개수
    override fun getCount(): Int {
        return listViewItemList.size
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val context = parent.context

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.effectorlist, parent, false)
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        val titleTextView = view?.findViewById(R.id.name_musical_instrument) as TextView

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem = listViewItemList[position]

        // 아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(listViewItem.title)

        var NumOfClick: Int = 0
        view.btn_playback.setImageResource(R.drawable.icon_play__musical_instrument)//버튼 바뀌게 하려고 백그라운드를 투명하게 만든 후 play버튼으로 초기화

        view.findViewById<View>(R.id.btn_playback).setOnClickListener{
            NumOfClick = 1 - NumOfClick;
            if ( NumOfClick == 0 ) {
                // 음원 멈추기 기능추가 , 초기화되는지는 모르겠음
                view.btn_playback.setImageResource(R.drawable.icon_play__musical_instrument)
            }
            else {
                // 저장된 음원 재생 되게 하는 기능 추가
                view.btn_playback.setImageResource(R.drawable.icon_stop__musical_instrument)
            }
        }
        view.findViewById<View>(R.id.btn_add).setOnClickListener{
            view.name_musical_instrument.setText("추가")
            //recording 에 저장된 음원 추가되게 하는 기능 여기다 추가
        }



        return view
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    // 아이템 데이터
    fun addItem(title: String) {
        val item = EffectorListItem()

        item.title = title

        listViewItemList.add(item)
    }
}