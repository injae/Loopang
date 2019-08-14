package com.treasure.loopang.ui.adapter

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.treasure.loopang.R
import com.treasure.loopang.ui.item.LoopItem
import kotlinx.android.synthetic.main.loop_item.view.*

class LoopListAdapter2 (private val loopItemList: ArrayList<LoopItem>)
    : BaseAdapter(), Filterable {
    var onPlaybackButtonClick: (Int) -> Unit = {}
    var onStopButtonClick: (Int) -> Unit = {}

    private val mHandler: Handler = Handler()
    private var mFilteredLoopItemList: ArrayList<LoopItem> = loopItemList
    private val mLoopListFilter = LoopListFilter()

    private val mMediaPlayer: MediaPlayer = MediaPlayer()
    private var nowPlayLoopItem: LoopItem? = null
    private var nowPlayHolder: LoopListHolder? = null


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
            play(holder, position)
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

    fun play(holder: LoopListHolder, position: Int) {
        if(mMediaPlayer.isPlaying) {
            // 플레이어를 멈추고 리셋
            mMediaPlayer.stop()
            nowPlayLoopItem?.playState = false
            nowPlayHolder?.let{
                if(it.playbackButton.visibility == View.VISIBLE){
                    it.playbackButton.visibility = View.GONE
                    it.stopButton.visibility = View.VISIBLE
                }
            }
        }
        val loopItem = mFilteredLoopItemList[position]
        loopItem.filePath?.let{
            mMediaPlayer.apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                Log.d("MediaPlayerTest", "$it")
                setDataSource(it)
                prepare()
            }.start()
            loopItem.playState = true
            switchPlayIcon(holder)
            nowPlayHolder = holder
        }
    }

    fun stop() {
        if(mMediaPlayer.isPlaying){
            mMediaPlayer.let{
                it.stop()
                it.release()
            }
            nowPlayHolder?.let{
                if(it.playbackButton.visibility == View.VISIBLE){
                    it.playbackButton.visibility = View.GONE
                    it.stopButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun switchPlayIcon(holder: LoopListHolder){
        if(holder.playbackButton.visibility == View.VISIBLE){
            holder.playbackButton.visibility = View.GONE
            holder.stopButton.visibility = View.VISIBLE
        } else {
            holder.stopButton.visibility = View.GONE
            holder.playbackButton.visibility = View.VISIBLE
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