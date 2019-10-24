package com.treasure.loopang.audio

import io.github.junyuecao.soundtouch.SoundTouch

class SoundEffector(private var channel: Int = 1, private var sampleRate: Int = 44100,
                    private var tempo: Double = 1.0, private var pitch: Double = 1.0,
                    private val st: SoundTouch = SoundTouch()) {
    init {
        preSetting()
    }

    private fun preSetting() {
        st.setChannels(channel)
        st.setSampleRate(sampleRate)
        st.setTempo(tempo)
    }

    private fun setChannel(cn: Int) { st.setChannels(cn) }
    private fun setSampleRate(sr: Int) { st.setSampleRate(sr) }
    private fun setTempo(tp: Double) { st.setTempo(tp) }
    private fun setPitch(pt: Double) { st.setPitch(pt) }

    fun process(sourceData: ByteArray, preset: EffectorPresets): ByteArray {
        when(preset) {
            EffectorPresets.SCARY -> { setPitch(0.5) }
            EffectorPresets.SINKING -> { setPitch(0.8) }
            EffectorPresets.EXCITING -> { setPitch(1.2) }
            EffectorPresets.FANTASTIC -> { setPitch(1.4) }
        }
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

        st.release()    // 만약 음원소스 여러개 이펙팅 할때 에러나면 이걸 st = SoundTouch()로 바꾸기
        return result.toByteArray()
    }

    fun release() { st.release() }
}

enum class EffectorPresets { SCARY, SINKING, EXCITING, FANTASTIC }