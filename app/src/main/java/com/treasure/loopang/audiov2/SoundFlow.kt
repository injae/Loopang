package com.treasure.loopang.audiov2

typealias Effector = (ShortArray) -> ShortArray
typealias Flow<T> = (T) -> Unit

abstract open class SoundFlow<T> {
    var effectorList : MutableList<Effector> = mutableListOf()
    fun addEffector(effector: Effector) { effectorList.add(effector) }
    fun effect(data: ShortArray) : ShortArray { return effectorList.fold(data, {acc, it -> it(acc)}) }

    protected var callSuccess: Flow<T> = {  }
    protected var callStart:   Flow<T> = {  }
    protected var callStop:    Flow<T> = {  }

    fun onSuccess(body: Flow<T>) { this.callSuccess = body}
    fun onStart(body: Flow<T>)   { this.callStart = body }
    fun onStop(body: Flow<T>)    { this.callStop = body }
}
