package com.treasure.loopang.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


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
       thread {
            val data = ShortArray(bufferSize)
            while(isRecording.get()) {
                audioRecord.read(data,0,bufferSize)
                data.forEach { audioData.add(it) }
            }
       }.start()
    }

    fun stop() {
        isRecording.set(false)
        audioRecord.stop()
        audioRecord.release()
    }

    fun writeToPcm16(path: String) {
        var fstream = FileOutputStream(path)
        var index = 0
        for(i in 0 until audioData.size/bufferSize) {
            var short_array = audioData.subList(i,i+bufferSize).toShortArray()
            var byte_array = convertShortArrayToByteArray(short_array)
            fstream.write(byte_array,0,byte_array.size)
        }
    }
}