package com.treasure.loopang.audio

fun Mixer.switchMute(position: Int){
    setMute(position, !sounds[position].isMute)
}

fun Mixer.switchMuteWithReturnState(position: Int): Boolean{
    switchMute(position)
    return sounds[position].isMute
}

fun FileManager.checkDuplication(projectTitle: String): Boolean {
    val projects = soundList()
    projects.forEach {
        if (it.name == projectTitle)
            return true
    }
    return false
}