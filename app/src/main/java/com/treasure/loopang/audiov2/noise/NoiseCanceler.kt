package com.treasure.loopang.audiov2.noise

class NoiseCanceler {
    fun makeReverse(sourceData: MutableList<Short>) = sourceData.map { (it * (-1)).toShort() }.toMutableList()
    fun getPoints(sourceData: MutableList<Short>) = listOf(getHighestPoint(sourceData), getLowestPoint(sourceData))
}