package com.treasure.loopang

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.treasure.loopang.adapter.SettingAdapter
import com.treasure.loopang.listitem.SettingItem
import kotlinx.android.synthetic.main.setting_frame.*
import android.text.Editable
import android.content.Intent
import com.treasure.loopang.ui.toast


class setting : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.setting_frame,container,false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter: SettingAdapter
        adapter = SettingAdapter()
        settingListView.adapter = adapter

        adapter.addItem(ContextCompat.getDrawable(context!!, R.drawable.icon_setting_help)!!,"Notice")
        adapter.addItem(ContextCompat.getDrawable(context!!, R.drawable.icon_setting_visualizer)!!,"Visualizer")
        adapter.addItem(ContextCompat.getDrawable(context!!, R.drawable.icon_setting_pcm)!!,"Pcm")
        adapter.addItem(ContextCompat.getDrawable(context!!, R.drawable.icon_setting_help)!!,"도움말")

        var editTextFilter : EditText? =null
        editTextFilter = editText

        editTextFilter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(edit: Editable) {
                val filterText : String = edit.toString()
                (settingListView.getAdapter() as SettingAdapter).getFilter().filter(filterText)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        btn_deleteAll.setOnClickListener{
            editText.setText("")
        }

     //   val itemFragment = settingItemFragment()
   //     var bundle : Bundle

        //val intent = Intent(context, settingItemActivity::class.java)


        settingListView.setOnItemClickListener { parent, view, position, id ->
            // get item
            val item = parent.getItemAtPosition(position) as SettingItem
            val title = item.title
            val icon = item.icon
            var isPageOpen = false

            toast(title + " click")

            val intent1 = Intent(activity, settingItemActivity::class.java)
            intent1.putExtra("title",title)
            startActivity(intent1)
        }
    }
    /*
    public interface  itemTitleSetListener{
        fun itemTitleSet(title :String, listItemNum :  Int)
    }*/
}