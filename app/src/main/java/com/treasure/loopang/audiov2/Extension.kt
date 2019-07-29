package com.treasure.loopang.audiov2

fun Mixer.switchMute(position: Int){
    setMute(position, !sounds[position].isMute)
}