package com.treasure.loopang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.treasure.loopang.adapter.SettingAdapter
import com.treasure.loopang.listitem.SettingItem
import kotlinx.android.synthetic.main.setting_frame.*

class setting : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.setting_frame,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter: SettingAdapter
        adapter = SettingAdapter()
        settingListView.adapter = adapter

        adapter.addItem(ContextCompat.getDrawable(context!!, R.drawable.icon_setting_help)!!,"공지사항")
        adapter.addItem(ContextCompat.getDrawable(context!!, R.drawable.icon_setting_visualizer)!!,"비주얼라이저")
        adapter.addItem(ContextCompat.getDrawable(context!!, R.drawable.icon_setting_pcm)!!,"pcm")
        adapter.addItem(ContextCompat.getDrawable(context!!, R.drawable.icon_setting_help)!!,"도움말")

        settingListView.setOnItemClickListener { parent, view, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as SettingItem
            val title = item.title
            val icon = item.icon
            //to do
        }
        btn_deleteAll.setOnClickListener{
            //검색하던 모든 내용 지우기 기능
        }
    }
}