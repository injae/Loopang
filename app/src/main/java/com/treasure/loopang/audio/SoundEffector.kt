package com.treasure.loopang.audio

import io.github.junyuecao.soundtouch.SoundTouch

class SoundEffector(private var channel: Int = 1, private var sampleRate: Int = 44100,
                    private var tempo: Double = 1.0, private var pitch: Double = 1.0,
                    private val st: SoundTouch = SoundTouch()) {
    fun setChannel(cn: Int): SoundEffector {
        st.setChannels(cn)
        return this
    }

    fun setSampleRate(sr: Int): SoundEffector {
        st.setSampleRate(sr)
        return this
    }

    fun preSetting(): SoundEffector {
        st.setChannels(channel)
        st.setSampleRate(sampleRate)
        return this
    }

    fun setTempo(tp: Double): SoundEffector {
        st.setTempo(tp)
        return this
    }

    fun setPitch(pt: Double): SoundEffector {
        st.setPitch(pt)
        return this
    }

    fun process(sourceData: ByteArray): ByteArray {
        st.putSamples(sourceData, sourceData.size)

        val result = MutableList<Byte>(0, {0})
        var bufferSize: Int
        val tempBuffer = ByteArray(8192)

        do {
            bufferSize = st.receiveSamples(tempBuffer, 8192)
            if (bufferSize > 0) {
                for (i in 0 until bufferSize) {
                    result.add(tempBuffer[i])
                }
            }
        } while(bufferSize != 0)

        return result.toByteArray()
    }

    fun release() { st.release() }
}