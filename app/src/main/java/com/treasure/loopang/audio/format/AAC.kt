package com.treasure.loopang.audio.format

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import com.treasure.loopang.audio.convertShortArrayToByteArray

class AAC(private var info: FormatInfo = FormatInfo()) : IFormat {
    override fun info() = info
    override fun encord(data: MutableList<Short>) = getEncodedData(data)
    override fun decord(data: MutableList<Byte>): MutableList<Short> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val mediaEncodeCodec = MediaCodec.createEncoderByType("audio/mp4a-latm")
    private val mediaEncodeFormat = MediaFormat()

    private val mediaDecodeCodec = MediaCodec.createDecoderByType("audio/mp4a-latm")
    private val mediaDecodeFormat = MediaFormat()

    private fun encodePrepare() {
        mediaEncodeFormat.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm")
        mediaEncodeFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
        mediaEncodeFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100)
        mediaEncodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 320 * 1024)
        mediaEncodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)

        mediaEncodeCodec.configure(mediaEncodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mediaEncodeCodec.start()
    }

    private fun encodeProcess(pcmData: MutableList<Short>): ByteArray {
        encodePrepare()
        val sourceData = convertShortArrayToByteArray(pcmData.toShortArray())
        var inputBufferIndex = 0
        var readCount = 0
        var leftSize = sourceData.size
        var encodedData = ByteArray(0)
        val time = 1000000L * (sourceData.size / 2) / 44100

        while(leftSize != 0) {
            inputBufferIndex = mediaEncodeCodec.dequeueInputBuffer(100)
            Log.d("M4ATEST","inputBufferIndex = ${inputBufferIndex}")

            if(inputBufferIndex >= 0) {
                val buffer = mediaEncodeCodec.getInputBuffer(inputBufferIndex)
                buffer?.clear()

                if (leftSize < buffer!!.limit()) {
                    buffer.put(sourceData.copyOfRange((readCount * buffer.limit()), sourceData.size))
                    mediaEncodeCodec.queueInputBuffer(inputBufferIndex, 0, leftSize, time, 0)
                    leftSize -= leftSize
                } else {
                    buffer.put(sourceData.copyOfRange((readCount * buffer.limit()), (readCount * buffer.limit()) + buffer.limit()))
                    mediaEncodeCodec.queueInputBuffer(inputBufferIndex, 0, buffer.limit(), time, 0)
                    leftSize -= buffer.limit()
                    ++readCount
                }
            }

            val bufferInfo = MediaCodec.BufferInfo()
            var outputBufferIndex = mediaEncodeCodec.dequeueOutputBuffer(bufferInfo, 100)

            while(outputBufferIndex >= 0) {
                val outputBuffer = mediaEncodeCodec.getOutputBuffer(outputBufferIndex)
                outputBuffer?.position(bufferInfo.offset)
                outputBuffer?.limit(bufferInfo.offset + bufferInfo.size)

                val tempBuffer = ByteArray(bufferInfo.size + 7)
                addADTSPacket(tempBuffer, bufferInfo.size + 7)
                outputBuffer?.get(tempBuffer, 7, bufferInfo.size)

                encodedData = encodedData + tempBuffer
                mediaEncodeCodec.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = mediaEncodeCodec.dequeueOutputBuffer(bufferInfo, 100)
            }
        }

        mediaEncodeCodec.stop()
        mediaEncodeCodec.release()

        return encodedData
    }

    private fun addADTSPacket(packet: ByteArray, length: Int) {
        val profile = 1  // AAC LC
        val freqIdx = 4 // samplerate = 44100
        val chanCfg = 1  // Channel count

        packet[0] = (0xFF).toByte()
        packet[1] = (0xF1).toByte()
        packet[2] = ((profile shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = (((chanCfg and 3) shl 6) + (length shr 11)).toByte()
        packet[4] = ((length and 0x7FF) shr 3).toByte()
        packet[5] = (((length and 7) shl 5) + 0x1F).toByte()
        packet[6] = (0xFC).toByte()
    }

    private fun getEncodedData(pcmData: MutableList<Short>) = encodeProcess(pcmData).toMutableList()

    private fun decodePrepare() {
        mediaDecodeFormat.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm")
        mediaDecodeFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
        mediaDecodeFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100)
        mediaDecodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 320 * 1024)
        mediaDecodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)

        mediaDecodeCodec.configure(mediaDecodeFormat, null, null, 0)
        mediaDecodeCodec.start()
    }
}