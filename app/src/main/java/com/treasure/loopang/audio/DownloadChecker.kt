package com.treasure.loopang.audio

import android.content.Context
import com.treasure.loopang.CommunityTrackFragment
import com.treasure.loopang.LoadingActivity
import com.treasure.loopang.communication.Connector
import com.treasure.loopang.communication.ResultManager
import com.treasure.loopang.communication.makeSHA256
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class DownloadChecker(val cacheDirectory: String = FileManager().looperCacheDir.path,
                      val layerDirectory: String = FileManager().looperSoundDir.path) {
    fun cacheCheck(musicID: String) = if(File(cacheDirectory + '/' + makeSHA256(musicID)).exists()) true else false
    fun download(musicName: String?, musicID: String, context: Context, fragment: CommunityTrackFragment? = null) {
        val ld = LoadingActivity(context)
        val connector = Connector()
        GlobalScope.launch {
            CoroutineScope(Dispatchers.Main).launch { ld.show() }
            connector.process(ResultManager.FILE_DOWNLOAD, null, musicName, null, musicID)
            CoroutineScope(Dispatchers.Main).launch { ld.dismiss() }
            if(fragment != null) {
                fragment.setSound(musicID)
                fragment.soundPlay()
            }
        }
    }
}