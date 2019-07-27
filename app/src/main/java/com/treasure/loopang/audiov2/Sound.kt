package com.treasure.loopang.audiov2

import android.util.Log
import com.treasure.loopang.audiov2.format.FormatInfo
import com.treasure.loopang.audiov2.format.IFormat
import com.treasure.loopang.audiov2.format.Pcm16
import com.treasure.loopang.audiov2.format.formatFactory
import java.io.*
import java.util.concurrent.atomic.AtomicBoolean


open class Sound (var data: MutableList<Short> = mutableListOf(),
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
                    data.chunked(info.sampleRate)
                        .map { timeEffect(it.toShortArray()).toList() }
                        .forEach {
                            it.chunked(info.outputBufferSize)
                                .map { effect(it.toShortArray()) }
                                .forEach {
                                    if (!isPlaying.get()) return@exit else info.outputAudio.write(it, 0, it.size)
                                }
                        }
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
        var bufos = BufferedOutputStream(fstream)
        var preprocess = data.chunked(info.sampleRate)
            .map { timeEffect(it.toShortArray()).toList() }
            .flatMap {
                it.chunked(info.outputBufferSize)
                  .map { it.toShortArray() }
                  .flatMap { effect(it).toList() }
           }.toMutableList()

        bufos.write(format.encord(preprocess).toByteArray())


        bufos.close()
        fstream.close()
    }

    fun load(path: String) {
        val format = formatFactory(path)
        data.clear()
        var originData = File(path).inputStream().readBytes().toMutableList()
        data = format.decord(originData)
    }

}

