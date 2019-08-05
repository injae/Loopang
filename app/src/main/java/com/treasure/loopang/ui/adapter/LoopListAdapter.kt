package com.treasure.loopang.ui.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.treasure.loopang.R
import com.treasure.loopang.audio.Sound
import com.treasure.loopang.ui.item.LoopItem
import kotlinx.android.synthetic.main.loop_item.view.*
import kotlinx.android.synthetic.main.songlist_item.view.song_date
import kotlinx.android.synthetic.main.songlist_item.view.song_name
import kotlinx.coroutines.async

class LoopListAdapter(private val loopItemList: ArrayList<LoopItem>) : BaseAdapter()
    , Filterable {
    var onPlaybackButtonClick: (Int) -> Unit = {}
    var onStopButtonClick: (Int) -> Unit = {}

    private val mHandler: Handler = Handler()
    private var mFilteredLoopItemList: ArrayList<LoopItem> = loopItemList
    private val mLoopListFilter = LoopListFilter()

    private var nowPlaySound: Sound? = null


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent?.context
        val holder: LoopListHolder
        val view: View
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.loop_item, null)
            holder = LoopListHolder(view)

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as LoopListHolder
        }

        val loopItem = mFilteredLoopItemList[position]
        holder.loopTitleText.text = loopItem.loopTitle
        holder.loopDateText.text = loopItem.dateString
        holder.playbackButton.setOnClickListener {
            onPlaybackButtonClick(position)
            play(position)
        }
        holder.stopButton.setOnClickListener{
            onStopButtonClick(position)
            stop()
        }
        if(loopItem.playState){
            holder.playbackButton.visibility = View.GONE
            holder.stopButton.visibility = View.VISIBLE
        } else {
            holder.stopButton.visibility = View.GONE
            holder.playbackButton.visibility = View.VISIBLE
        }

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

    fun play(position: Int) {
        nowPlaySound?.let{
            if(it.isPlaying.get()){
                it.stop()
            }
        }
        val loopItem = mFilteredLoopItemList[position]
        loopItem.filePath?.let{
            nowPlaySound = Sound()
                .apply{ load(path=it) }
                .apply{
                    onStart {
                        loopItem.playState = true
                        mHandler.post{ notifyDataSetChanged() }
                    }
                    onSuccess {
                        loopItem.playState = false
                        mHandler.post{ notifyDataSetChanged() }
                    }
                }.also { async{ it.play() } }
        }
    }

    fun stop() {
        nowPlaySound?.let{
            if(it.isPlaying.get())
                it.stop()
        }
    }

    inner class LoopListHolder(view: View) {
        var loopTitleText = view.song_name
        var loopDateText = view.song_date
        var playbackButton = view.btn_playback
        var stopButton = view.btn_stop
    }

    inner class LoopListFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint == null || constraint.isEmpty()){
                results.values = loopItemList
                results.count = loopItemList.size
            } else {
                val filteredLoopItemList = arrayListOf<LoopItem>()
                loopItemList.forEach {
                    if (it.loopTitle.toUpperCase().contains(constraint.toString().toUpperCase()))
                        filteredLoopItemList.add(it)
                }
                results.values = filteredLoopItemList
                results.count = filteredLoopItemList.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.let{
                mFilteredLoopItemList = it.values as ArrayList<LoopItem>

                if (results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}