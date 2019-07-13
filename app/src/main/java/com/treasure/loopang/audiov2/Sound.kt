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
              var isPlaying: AtomicBoolean = AtomicBoolean(false)) : SoundFlow<Sound>() {
    fun play() {
        if(!isPlaying.get()) {
            info.outputAudio.play()
            isPlaying.set(true)
            callStart(this)
            if(isPlaying.get()) {
                run exit@{
                    data.chunked(info.outputBufferSize)
                        .map { it.toShortArray() }
                        .map { effect(it) }
                        .forEach { if (!isPlaying.get()) return@exit else info.outputAudio.write(it, 0, it.size) }
                }
            }
            isPlaying.set(false)
            callSuccess(this)
        }
    }

    fun stop() {
        if(isPlaying.get()) {
            isPlaying.set(false)
            info.outputAudio.stop()
            callStop(this)
            //audioTrack.release()
        }
    }

    fun save(path: String) {
        val format = formatFactory(path)
        val fstream = FileOutputStream(path)
        var preprocess = data.chunked(info.inputBufferSize)
            .map{ effect(it.toShortArray()) }
            .flatMap { it.toList() }.toMutableList()
        format.encord(preprocess).chunked(info.inputBufferSize).map{ it.toByteArray() }.forEach { fstream.write(it,0,it.size) }
        fstream.close()
    }

    fun load(path: String) {
        val format = formatFactory(path)
        val buffer = ByteArray(info.outputBufferSize)
        val originData : MutableList<Byte> = mutableListOf()
        val fis = FileInputStream(path)
        val dis = DataInputStream(fis)
        data.clear()

        while(true) {
            val ret = dis.read(buffer, 0, info.outputBufferSize)
            buffer.forEach { originData.add(it) }
            if(ret == -1) break
        }
        data = format.decord(originData)

        dis.close()
        fis.close()
    }

}

