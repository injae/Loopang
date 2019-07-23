package com.treasure.loopang.audiov2

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat

data class Music(var name: String, var date: String, var path: String)

class FileManager {
   val looperDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"Loopang")
   private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
   init{ if(!looperDir.isDirectory) looperDir.mkdir() }
   fun SoundList() = looperDir.list().map{ Music(it,simpleDateFormat.format(File(looperDir.absolutePath+'/'+it).lastModified()) , looperDir.absolutePath+'/'+it)}

   fun deleteFile(fileName: String) {
      val targetFile = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}/Loopang/${fileName}")
      targetFile.delete()
   }

   fun deleteAllFiles() {
      val directory = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}/Loopang")
      directory.list().forEach {
         deleteFile(it)
      }
   }
}