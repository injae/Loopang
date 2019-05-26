package com.treasure.loopang.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean

class Recorder( val audioSorce : Int = MediaRecorder.AudioSource.MIC
              , val sampleRate : Int = 44100
              , val channel    : Int = AudioFormat.CHANNEL_IN_MONO
              , val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
              , val bufferSize : Int = AudioRecord.getMinBufferSize(sampleRate,channel,audioFormat)
              , var audioRecord: AudioRecord = AudioRecord(audioSorce,sampleRate,channel,audioFormat,bufferSize)){
    var isRecording = AtomicBoolean(true)
    var audioData = mutableListOf<Short>()

    fun start() {
       audioRecord.startRecording()
       Thread{
           val data = ShortArray(bufferSize)
           while(isRecording.get()) {
               audioRecord.read(data,0, bufferSize)
               data.forEach { audioData.add(it) } // stop되버리면 data가 덜 들어가는 현상 발생
           }
       }.start()
    }

    fun stop() {
        isRecording.set(false)
        audioRecord.stop()
        audioRecord.release()
    }

    fun writeToPcm16(path: String) {
        val fstream = FileOutputStream(path)
        for(i in 0 until audioData.size/bufferSize) {
            val shortArray = audioData.subList(i * bufferSize, (i * bufferSize) + bufferSize).toShortArray()
            val byteArray = convertShortArrayToByteArray(shortArray)
            fstream.write(byteArray,0, byteArray.size)
        }
        fstream.close()
    }
}