package com.treasure.loopang.audio

import android.media.AudioFormat
import android.provider.MediaStore
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.FileOutputStream

class SoundPcm16
    : Sound( inputChannel  = AudioFormat.CHANNEL_IN_MONO
           , outputChannel = AudioFormat.CHANNEL_OUT_MONO
           , audioFormat   = AudioFormat.ENCODING_PCM_16BIT) {
    override fun save(path: String) {
        val fstream = FileOutputStream(path)
        for(i in 0 until data.size/inputBufferSize) {
            val shortArray = data.subList(i * inputBufferSize, (i * inputBufferSize) + inputBufferSize).toShortArray()
            val byteArray = convertShortArrayToByteArray(shortArray)
            fstream.write(byteArray,0, byteArray.size)
        }
        fstream.close()
    }

    override fun load(path: String) {
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