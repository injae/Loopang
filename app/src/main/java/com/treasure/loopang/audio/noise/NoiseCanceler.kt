package com.treasure.loopang.audio.noise

class NoiseCanceler() {
    fun makeReverse(sourceData: Short) = sourceData * (-1)
    fun makeReverse(sourceData: MutableList<Short>) = sourceData.map { (it * (-1)).toShort() }.toMutableList() // 음원소스 데이터 반대값 줌
    fun getPoints(sourceData: MutableList<Short>) = listOf(getHighestPoint(sourceData), getLowestPoint(sourceData)) // list[0] = 최고값, list[1] = 최저값
    fun getPattern(sourceData: MutableList<Short>): MutableMap<Short, Int> {
        val pattern: MutableMap<Short, Int> = mutableMapOf()
        sourceData.forEach {
            if(pattern.containsKey(it)) changeValueOfMap(pattern, it)
            else pattern.put(it, 1)
        }
        return pattern
    }   // 데이터들의 분포도를 가진 map 리턴
}