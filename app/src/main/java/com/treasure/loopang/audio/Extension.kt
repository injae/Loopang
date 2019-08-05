package com.treasure.loopang.audio

fun Mixer.switchMute(position: Int){
    setMute(position, !sounds[position].isMute)
}