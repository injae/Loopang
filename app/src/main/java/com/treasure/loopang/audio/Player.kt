package com.treasure.loopang.audio

import android.media.AudioFormat
import android.media.AudioTrack
import android.media.AudioManager
import android.provider.Settings
import android.util.Log
import java.io.DataInputStream
import java.io.FileInputStream
import java.util.concurrent.atomic.AtomicBoolean

class Player( val audioSorce : Int = AudioManager.STREAM_MUSIC
            , val sampleRate : Int = 44100
            , val channel    : Int = AudioFormat.CHANNEL_OUT_MONO
            , val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
            , val bufferSize : Int = AudioTrack.getMinBufferSize(sampleRate,channel,audioFormat)
            , val audioTrack : AudioTrack = AudioTrack(audioSorce, sampleRate, channel, audioFormat, bufferSize, AudioTrack.MODE_STREAM)){
    var isPlaying = AtomicBoolean(false)
    var isLooping = AtomicBoolean(false)
    var audioData: MutableList<Short>? = null



    fun start() {
        if(audioData != null && !isPlaying.get()) {
            audioTrack.play()
            Thread{
                isPlaying.set(true)
                do {
                    val subLists = audioData!!.size / bufferSize
                    var sliceSize = bufferSize
                    for (i in 0 until subLists) {
                        if(i == subLists-1) sliceSize = audioData!!.size % bufferSize
                        val array = audioData!!.subList(i* bufferSize,(i*bufferSize) + sliceSize).toShortArray()
                        Log.d("AudioTest","${i+sliceSize}")
                        audioTrack.write(array,0, array.size)
                    }
                } while(isLooping.get())
                isPlaying.set(false)
            }.start()
        }
    }

    fun stop(){
        if(isPlaying.get()) {
            isPlaying.set(false)
            audioTrack.stop()
            //audioTrack.release()
        }
    }

    fun readToPcm16(path: String) {
        audioData = mutableListOf()
        val data = ByteArray(bufferSize)
        val fis = FileInputStream(path)
        val dis = DataInputStream(fis)
        while(true) {
            val ret = dis.read(data, 0, bufferSize)
            convertByteArrayToShortArray(data)
                .forEach { audioData?.add(it) }
            if(ret == -1) break
        }
        dis.close()
        fis.close()
    }

}