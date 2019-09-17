package com.treasure.loopang.audio

import android.os.Environment
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat

class FileManager {
   val looperDir = File(Environment.getExternalStorageDirectory(), "Loopang")
   val looperSoundDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"Loopang")
   val looperProjectDir = File(Environment.getExternalStorageDirectory(), "Loopang/Data")
   private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
   init{
      if(!looperDir.exists()) looperDir.mkdir()
      if(!looperProjectDir.exists()) looperProjectDir.mkdir()
      if(!looperSoundDir.isDirectory) looperSoundDir.mkdir()
   }
   fun soundList() : List<LoopMusic> {
       var projects = LoopMusic.searchAllLoopMusic()
       var sounds = looperSoundDir.list()
           .filter { it ->
               var path = "${looperSoundDir.absolutePath}/$it"
               var result = true
               projects.forEach { pj ->
                   if(pj.path == path) result = false
                   pj.child?.forEach{ch ->if( ch.path == path) result = false }

               }
               result
           }
           .map{
               var token =  it.split(".")
               LoopMusic( name= token[0]
                        , type= token[1]
                        , path=looperSoundDir.absolutePath+'/'+it
                        , date=simpleDateFormat.format(File(looperSoundDir.absolutePath+'/'+it).lastModified()))
           }

       return projects + sounds
   }
   fun deleteFile(fileName: String) { File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}/Loopang/${fileName}").delete() }
   fun deleteFilePath(filePath: String){ File(filePath).delete() }
   fun deleteAllFiles() {
       looperSoundDir.listFiles().forEach { it.delete() }
       looperProjectDir.listFiles().forEach{ it.delete() }
   }

    fun checkSoundDuplication(fileName: String): Boolean {
        return false
    }

    fun checkProjectDuplication(loopMusic: LoopMusic): Boolean {
        return false
    }
}