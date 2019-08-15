package com.treasure.loopang.ui.adapter.v2

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.treasure.loopang.R
import com.treasure.loopang.audio.LoopMusic
import com.treasure.loopang.ui.item.LoopItem
import kotlinx.android.synthetic.main.dialog_save_loop.view.*
import kotlinx.android.synthetic.main.loop_item.view.*
import kotlinx.android.synthetic.main.loop_item.view.btn_playback
import kotlinx.android.synthetic.main.loop_item.view.txt_date
import kotlinx.android.synthetic.main.project_item.view.*

class LoopListAdapter (var loopItemList: List<LoopMusic>)
    : BaseAdapter(), Filterable {
    var onPreviewButtonClick: (LoopMusic) -> Unit = {}
    var onInfoButtonClick: (LoopMusic) -> Unit = {}
    var onMoreButtonClick: (LoopMusic) -> Unit = {}

    private var mFilteredLoopItemList: List<LoopMusic> = loopItemList
    private val mLoopListFilter = LoopListFilter()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent?.context
        val holder: LoopListHolder
        val view: View
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.project_item, parent, false)
            holder = LoopListHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as LoopListHolder
        }

        val loopItem = mFilteredLoopItemList[position]
        holder.fileTypeImage.setImageResource(loopItem.child?.let{ R.drawable.round_insert_chart_outlined_white_24dp } ?: R.drawable.round_music_note_white_24dp)
        holder.fileTypeText.text = loopItem.child?.let{ "Loopang Project" } ?: "${loopItem.type.toUpperCase()} Sound File"
        holder.loopTitleText.text = loopItem.name
        holder.loopDateText.text = loopItem.date
        holder.previewButton.setOnClickListener { onPreviewButtonClick(loopItem) }
        holder.infoButton.setOnClickListener { onInfoButtonClick(loopItem) }
        holder.moreButton.setOnClickListener { onMoreButtonClick(loopItem) }

        return view
    }

    override fun getItem(position: Int): Any {
        return mFilteredLoopItemList[position]
    }

    override fun getItemId(position: Int): Long {
        /* 사용용도를 모르겠어서 0으로 설정 */
        return 0
    }

    override fun getCount(): Int {
        return mFilteredLoopItemList.size
    }

    override fun getFilter(): Filter {
        return mLoopListFilter
    }

    fun setLoopList(projects: List<LoopMusic>) {
        loopItemList = projects
        mFilteredLoopItemList = loopItemList
        if(loopItemList.isEmpty()) notifyDataSetInvalidated()
        else notifyDataSetChanged()
    }

    inner class LoopListHolder(view: View) {
        var fileTypeImage: ImageView = view.image_type
        var fileTypeText: TextView = view.txt_type
        var loopTitleText: TextView = view.txt_title
        var loopDateText: TextView = view.txt_date
        var previewButton: ImageButton = view.btn_playback
        var infoButton: ImageButton = view.btn_info
        var moreButton: ImageButton = view.btn_more
    }

    inner class LoopListFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint == null || constraint.isEmpty()){
                results.values = loopItemList
                results.count = loopItemList.size
            } else {
                val filteredLoopItemList = arrayListOf<LoopMusic>()
                loopItemList.forEach {
                    if (it.name.toUpperCase().contains(constraint.toString().toUpperCase()))
                        filteredLoopItemList.add(it)
                }
                results.values = filteredLoopItemList
                results.count = filteredLoopItemList.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.let{
                mFilteredLoopItemList = it.values as List<LoopMusic>

                if (results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}