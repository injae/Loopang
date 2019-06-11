package com.treasure.loopang.audio

import android.os.Environment
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

data class Music(var name: String, var date: String, var path: String)


class FileManager {
   val looperDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"looper")
   init{
      if(!looperDir.isDirectory) looperDir.mkdir()
   }

   fun SoundList() = looperDir.list().map{ Music(it,File(it).lastModified().toString(), looperDir.absolutePath+'/'+it)}
}