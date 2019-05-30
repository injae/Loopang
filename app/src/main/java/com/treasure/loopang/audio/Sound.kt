package com.treasure.loopang.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.FileOutputStream


class Sound( var data : MutableList<Short> = mutableListOf()
           , val sampleRate : Int = 44100
           , val inputChannel : Int = AudioFormat.CHANNEL_IN_MONO
           , val outputChannel: Int = AudioFormat.CHANNEL_OUT_MONO
           , val audioFormat  : Int = AudioFormat.ENCODING_PCM_16BIT
           , val inputBufferSize : Int = AudioRecord.getMinBufferSize(sampleRate,inputChannel,audioFormat)
           , val outputBufferSize: Int = AudioTrack.getMinBufferSize(sampleRate,outputChannel,audioFormat)) {

    fun save(path: String) {
        val fstream = FileOutputStream(path)
        for(i in 0 until data.size/inputBufferSize) {
            val shortArray = data.subList(i * inputBufferSize, (i * inputBufferSize) + inputBufferSize).toShortArray()
            val byteArray = convertShortArrayToByteArray(shortArray)
            fstream.write(byteArray,0, byteArray.size)
        }
        fstream.close()
    }

    fun load(path: String) {
        val buffer = ByteArray(outputBufferSize)
        val fis = FileInputStream(path)
        val dis = DataInputStream(fis)
        while(true) {
            val ret = dis.read(buffer, 0, outputBufferSize)
            convertByteArrayToShortArray(buffer).forEach { data.add(it) }
            if(ret == -1) break
        }
        dis.close()
        fis.close()
    }

}