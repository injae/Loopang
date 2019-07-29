package com.treasure.loopang.audiov2

fun Mixer.switchMute(position: Int){
    sounds[position].isMute = !sounds[position].isMute
}