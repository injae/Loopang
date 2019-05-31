package com.treasure.loopang.audio

interface Converter {
    fun encording(sound: Sound)
    fun decording(paht: String) : Sound
}