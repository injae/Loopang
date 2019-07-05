package com.treasure.loopang.audiov2

import com.treasure.loopang.audiov2.format.FormatInfo
import com.treasure.loopang.audiov2.format.IFormat
import com.treasure.loopang.audiov2.format.Pcm16
import com.treasure.loopang.audiov2.format.formatFactory
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean


class Sound ( var data: MutableList<Short> = mutableListOf(),
              var format: IFormat = Pcm16(),
              var info: FormatInfo = format.info(),
              var isPlaying: AtomicBoolean = AtomicBoolean(false)) : SoundFlow() {
    fun play() {
        if(!isPlaying.get()) {
            info.outputAudio.play()
            callStart(data)
            isPlaying.set(true)
            run exit@{
                data.chunked(info.outputBufferSize)
                    .map { it.toShortArray() }
                    .map { effect(it) }
                    .forEach { if(!isPlaying.get()) return@exit else info.outputAudio.write(it, 0, it.size) }
            }
            isPlaying.set(false)
            callSuccess(data)
        }
    }

    fun stop() {
        if(isPlaying.get()) {
            isPlaying.set(false)
            info.outputAudio.stop()
            callStop(data)
            //audioTrack.release()
        }
    }

    fun save(path: String) {
        val format = formatFactory(path)
        val fstream = FileOutputStream(path)
        data.chunked(info.inputBufferSize)
            .map{ it.toShortArray() }
            .map{ format.encord(it) }
            .forEach { fstream.write(it,0,it.size) }
        fstream.close()
    }

    fun load(path: String) {
        val format = formatFactory(path)
        val buffer = ByteArray(info.outputBufferSize)
        val fis = FileInputStream(path)
        val dis = DataInputStream(fis)
        data.clear()

        while(true) {
            val ret = dis.read(buffer, 0, info.outputBufferSize)
            format.decord(buffer).forEach { data.add(it) }
            if(ret == -1) break
        }
        dis.close()
        fis.close()
    }

}

