package com.treasure.loopang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.core.content.ContextCompat
import com.treasure.loopang.adapter.settingItemNoticeListAdapter
import com.treasure.loopang.listitem.settingItemNoticeListItem
import kotlinx.android.synthetic.main.setting_notice.*

class settingItemNoticeFragment :androidx.fragment.app.Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.setting_notice,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val noticelistview: ListView = noticeListView
        val noticeAdapter: settingItemNoticeListAdapter = settingItemNoticeListAdapter()

        noticelistview.adapter = noticeAdapter

        noticeAdapter.addItem(
            "19.08.10", "content1","update 0.1.0"
        )
        noticeAdapter.addItem(
            "19.09.10", "content2","update 0.2.0 기능 추가 & 사용법"
        )

        noticelistview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as settingItemNoticeListItem

            val title = item.noticeTitle
            val date = item.date
            val Content = item.noticeContent

            // TODO : use item data.
        }
    }

}