package com.treasure.loopang.audio

import com.treasure.loopang.audio.format.FormatInfo
import com.treasure.loopang.audio.format.IFormat
import com.treasure.loopang.audio.format.Pcm16
import com.treasure.loopang.audio.format.formatFactory
import java.io.*
import java.util.concurrent.atomic.AtomicBoolean


open class Sound (var data: MutableList<Short> = mutableListOf(),
                  var format: IFormat = Pcm16(),
                  var info: FormatInfo = format.info(),
                  var isPlaying: AtomicBoolean = AtomicBoolean(false),
                  var isMute: AtomicBoolean = AtomicBoolean(false)
) : SoundFlow<Sound>() {

    // millisecond
    fun duration(): Int { return data.size / info.tenMsSampleRate }

    fun play(start: Int? = null) {
        if(!isPlaying.get()) {
            info.outputAudio.play()
            isPlaying.set(true)
            callStart(this)
            if(isPlaying.get()) {
                run exit@{
                    var playData = if(start != null) data.subList(start, data.size) else data
                    playData.chunked(info.sampleRate)
                        .map { timeEffect(it.toShortArray()).toList() }
                        .forEach {
                            it.chunked(info.inputBufferSize)
                                .map { effect(it.toShortArray()) }
                                .forEach {
                                    if(!isPlaying.get()) return@exit
                                    if(!isMute.get()) {
                                        var count = 0
                                        while(count < it.size) {
                                            var written = info.outputAudio.write(it, count, it.size)
                                            if(written <= 0) break
                                            count += written
                                        }
                                    }
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

