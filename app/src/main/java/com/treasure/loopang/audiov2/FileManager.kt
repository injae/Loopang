package com.treasure.loopang.audiov2

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat

data class Music(var name: String, var date: String, var path: String)

class FileManager {
   val looperDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"looper")
   private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
   init{ if(!looperDir.isDirectory) looperDir.mkdir() }
   fun SoundList() = looperDir.list().map{ Music(it,simpleDateFormat.format(File(looperDir.absolutePath+'/'+it).lastModified()) , looperDir.absolutePath+'/'+it)}
}