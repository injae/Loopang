package com.treasure.loopang.audio

typealias Effector = (ShortArray) -> ShortArray
typealias Flow<T> = (T) -> Unit

abstract open class SoundFlow<T> {
    var effectorList : MutableList<Effector> = mutableListOf()
    fun addEffector(effector: Effector): Int { effectorList.add(effector); return effectorList.size-1 }
    fun addEffector(index: Int, effector: Effector) { effectorList[index] = effector }
    fun removeEffector(effector: Effector) { effectorList.remove(effector) }
    fun removeEffector(index: Int) { effectorList.removeAt(index) }
    fun effect(data: ShortArray) : ShortArray { return effectorList.fold(data, {acc, it -> it(acc)}) }

    var timeEffectorList : MutableList<Effector> = mutableListOf()
    fun addTimeEffector(effector: Effector): Int { timeEffectorList.add(effector); return timeEffectorList.size-1 }
    fun addTimeEffector(index: Int, effector: Effector) { timeEffectorList[index] = effector }
    fun removeTimeEffector(effector: Effector) { timeEffectorList.remove(effector) }
    fun removeTimeEffector(index: Int) { timeEffectorList.removeAt(index) }
    fun timeEffect(data: ShortArray) : ShortArray { return timeEffectorList.fold(data, {acc, it -> it(acc)}) }

    protected var callSuccess: Flow<T> = {  }
    protected var callStart:   Flow<T> = {  }
    protected var callStop:    Flow<T> = {  }

    fun onSuccess(body: Flow<T>) { this.callSuccess = body}
    fun onStart(body: Flow<T>)   { this.callStart = body }
    fun onStop(body: Flow<T>)    { this.callStop = body }
}
