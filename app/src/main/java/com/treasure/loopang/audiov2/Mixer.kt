package com.treasure.loopang.audiov2

import android.util.Log
import com.treasure.loopang.audiov2.format.FormatInfo
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class MixerSound(var _sound: Sound) {
    var isMute: Boolean = false
    val data : MutableList<Short> get() = _sound.data
    val info : FormatInfo get() = _sound.info
    fun play() { if(!isMute) _sound.play() }
    fun stop() { if(!isMute) _sound.stop() }
    fun save(path: String) = _sound.save(path)
    fun load(path: String) = _sound.load(path)
}

class Mixer(val sounds: MutableList<MixerSound> = mutableListOf()) : SoundFlow<Mixer>(), IPlayable {
    var isLooping = AtomicBoolean(false)

    fun addSound(sound: Sound) {
        sound.onSuccess { Log.d("SoundTest", "${sounds.count() + 1} Sound Success") } //debug
        sound.onStart   { Log.d("SoundTest", "${sounds.count() + 1} Sound Start") }   //debug
        sound.onStop    { Log.d("SoundTest", "${sounds.count() + 1} Sound Stop") }    //debug
        sounds.add(MixerSound(sound))
    }

    fun setMute(index: Int, isMute: Boolean) { sounds[index].isMute = isMute}

    override fun start() {
        isLooping.set(true)
        callStart(this)
        launch { while(isLooping.get()) { sounds.map { async { it.play() } }.forEach{ it.await() }; callSuccess(this@Mixer) } }.start()
    }

    override fun stop() {
        isLooping.set(false)
        callStop(this)
        sounds.forEach { it.stop() }
    }

    fun stop(index: Int) { sounds[index].stop() }

    fun mixSounds() =
        sounds.map { it.data }
              .fold(MutableList<Short>(sounds[0].data.size) {0}) {
                    acc, it -> acc.zip(it){ a, b -> (a + b).toShort() }.toMutableList() }

    fun mixSounds(filteredIndex: Int) =
        sounds.map { it.data }
              .filterIndexed{ index, _ -> index != filteredIndex }
              .fold(MutableList<Short>(sounds[0].data.size) {0}) {
                    acc, it -> acc.zip(it){ a, b -> (a + b).toShort() }.toMutableList() }
}