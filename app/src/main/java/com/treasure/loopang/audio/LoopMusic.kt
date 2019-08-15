package com.treasure.loopang.audio

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat

class LoopMusic( var name: String = ""
               , var path: String = ""
               , var date: String = ""
               , var type: String = "pcm"
               , var child: List<LoopMusic>? = null) {
        fun save() {
            var rootPath = FileManager().looperSoundDir
            path = "${rootPath.absolutePath}/$name.$type"

            var projectConfig = JSONObject().apply {
                put("name", name)
                put("path", path)
            }

            var subSounds = JSONArray()
            child?.forEach {
                it.path = "${rootPath.absolutePath}/${name}_${it.name}.${it.type}"
                subSounds.put(JSONObject().apply {
                    put("name",it.name)
                    put("path",it.path)
                })
            }
            projectConfig.put("sub_sounds", subSounds)

            var configPath = "${FileManager().looperProjectDir.absolutePath}/$name.json"
            File(configPath).also {
                BufferedWriter(FileWriter(it)).apply {
                    write(projectConfig.toString())
                    close()
                }
            }
        }

        fun delete() {
            FileManager().deleteFilePath(path)
            child?.let {
                it.forEach{ loopMusic -> loopMusic.delete() }
                FileManager().let{ fileManager ->
                    //delete config file
                    fileManager.deleteFilePath("${fileManager.looperProjectDir.absolutePath}/$name.json")
                }
            }
        }

    companion object{
        fun searchLoopMusic(path: String) : LoopMusic? {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
            var file = File(path)
            if(!file.exists()) return null
            var jsonData = JSONObject(file.readText())

            var project = LoopMusic()
            project.name = jsonData.get("name").toString()
            project.path = jsonData.get("path").toString()
            project.date = simpleDateFormat.format(file.lastModified())

            var subSounds = jsonData.getJSONArray("sub_sounds")
            project.child = (0 until subSounds.length()).map { num ->
                var obj = subSounds.getJSONObject(num)
                LoopMusic().also {
                    it.name = obj.get("name").toString()
                    it.path = obj.get("path").toString()
                }
            }.toMutableList()
            return project
        }

        fun searchAllLoopMusic() : List<LoopMusic> {
            return FileManager().looperProjectDir.list().map { searchLoopMusic("${FileManager().looperProjectDir.absolutePath}/$it")!! }
        }
    }
}
