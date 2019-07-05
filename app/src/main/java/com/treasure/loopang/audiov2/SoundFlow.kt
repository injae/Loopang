package com.treasure.loopang.audiov2

typealias Effector = (ShortArray) -> ShortArray
typealias Flow = (MutableList<Short>) -> UInt

abstract open class SoundFlow {
    var effectorList : MutableList<Effector> = mutableListOf()
    fun addEffector(effector: Effector) { effectorList.add(effector) }
    fun effect(data: ShortArray) : ShortArray { return effectorList.fold(data, {acc, it -> it(acc)}) }

    protected var callSuccess: Flow = { 0u }
    protected var callStart:   Flow = { 0u }
    protected var callStop:    Flow = { 0u }

    fun onSuccess(body: Flow) { this.callSuccess = body}
    fun onStart(body: Flow)   { this.callStart = body }
    fun onStop(body: Flow)    { this.callStop = body }
}
