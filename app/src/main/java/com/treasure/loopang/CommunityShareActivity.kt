package com.treasure.loopang

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.treasure.loopang.adapter.CommunityFeedCategoryAdapter
import com.treasure.loopang.adapter.CommunityShareAdapter
import com.treasure.loopang.audio.FileManager
import com.treasure.loopang.listitem.CommunityFeedCategoryItem
import com.treasure.loopang.listitem.CommunityShareItem
import com.treasure.loopang.listitem.CommunitySongItem
import kotlinx.android.synthetic.main.activity_community_share.*
import kotlinx.android.synthetic.main.community_feed.*

class CommunityShareActivity : AppCompatActivity() {
    var isShareing : Boolean = false
    var isProjectClicked: Boolean =false
    private var mUiOption: Int = 0
    private val mDecorView: View by lazy { window.decorView }
    val userTagSet : MutableSet<String> = mutableSetOf()
    lateinit var layerItem : CommunityShareItem
    val tag = {tagPreset: tagPresets ->
        when(tagPreset){
            tagPresets.BEAT     -> "비트"
            tagPresets.ACAPELLA -> "아카펠라"
            tagPresets.PEN_BEAT -> "펜비트"
            tagPresets.PIANO    -> "피아노"
            tagPresets.VOICE    -> "목소리"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_share)

        mUiOption = mDecorView.systemUiVisibility
        mUiOption = mUiOption or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        mUiOption = mUiOption or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        mDecorView.systemUiVisibility = mUiOption

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val fm = FileManager()

        val ShareAdapter = CommunityShareAdapter()
        communityShareListView.adapter = ShareAdapter
        communityShareListView.visibility=View.VISIBLE
        shareProjectClikedLayer.visibility = View.GONE

        if(fm.soundList().size != 0) fm.soundList().forEach { ShareAdapter.addItem(it) }
        var tagg : String ="1123?"

        //mytList<String>list에 item 추가
        communityShareListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as CommunityShareItem
            if(item.childItems != null) {
                val ShareAdapter2 = CommunityShareAdapter()
                shareProjectClikedLayer.adapter = ShareAdapter2
                for(layer in item.childItems!!){ ShareAdapter2.addItem(layer) } // 프로젝트니깐 프로젝트의 레이어 보여주기 프로젝트의 레이어는 item.childItems 임
                communityShareListView.visibility=View.GONE
                shareProjectClikedLayer.visibility = View.VISIBLE
                isProjectClicked =true
            } else openShareFrag(item)

        }
        shareProjectClikedLayer.onItemClickListener= AdapterView.OnItemClickListener { parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as CommunityShareItem
            openShareFrag(item)
        }
    }
    override fun onBackPressed() {
        if(isShareing ==  true){
            shareContainer.visibility = View.GONE
            isShareing= false
        }
        else if(isShareing== false && isProjectClicked == true){
            communityShareListView.visibility=View.VISIBLE
            shareProjectClikedLayer.visibility = View.GONE
        }
        super.onBackPressed()
    }
    fun openShareFrag(item :CommunityShareItem){
        layerItem = item
        shareContainer.visibility=View.VISIBLE
        getSupportFragmentManager().beginTransaction().replace(R.id.shareContainer, CommunityShareFragment()).commit()
    }
}
enum class tagPresets { BEAT, PEN_BEAT, PIANO, VOICE, ACAPELLA }

