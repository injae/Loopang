package com.treasure.loopang.audio

import android.media.*
import android.media.AudioManager
import android.os.Environment
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.FileOutputStream

open class AudioManager(val audioSource: Int = MediaRecorder.AudioSource.MIC, val sampleRate: Int = 44100,
                   val channel: Int = AudioFormat.CHANNEL_IN_MONO, val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT, val bufferSize: Int = 1024) {
    var isRecording = false
    var recorder: AudioRecord? = null
    var filePath: String = Environment.getExternalStorageDirectory().absolutePath + "/recorded.pcm"
    var recThread: RecordThread? = null
    var playThread: PlayThread? = null

    fun makeRecorder() = AudioRecord(audioSource, sampleRate, channel, audioFormat, bufferSize)

    fun goRecording() {
        recorder = makeRecorder()
        recorder?.startRecording()
        isRecording = true
        recThread = RecordThread()
        recThread?.start()
    }

    fun stopRecording() {
        if(recorder != null) {
            isRecording = false
            recorder?.stop()
            recorder?.release()
            recorder = null
            recThread?.interrupt()
            recThread = null
        }
    }

    fun playMusic() {
        playThread = PlayThread()
        playThread?.start()
    }

    open fun stopMusic() {
        // playThread.interrupt()
    }

    inner class RecordThread() : Thread() {
        override fun run() {
            writeDataToFile()
        }
    }

    private fun writeDataToFile() {
        val data = ByteArray(bufferSize)
        val fos = FileOutputStream(filePath)

        while(isRecording) {
            recorder?.read(data, 0, bufferSize)
            fos.write(data, 0, bufferSize)
        }
        fos.close()
    }

    inner class PlayThread() : Thread() {
        override fun run() {
            readDataToFile()
        }
    }

    private fun readDataToFile() {
        val data = ByteArray(bufferSize)
        val fis = FileInputStream(filePath)
        val dis = DataInputStream(fis)
        val audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM)

        audioTrack.play()
        while(true) {
            val ret = dis.read(data, 0, bufferSize)
            audioTrack.write(data, 0, ret)
            if(ret == -1) break
        }

        audioTrack.stop()
        audioTrack.release()
        dis.close()
        fis.close()
    }
}