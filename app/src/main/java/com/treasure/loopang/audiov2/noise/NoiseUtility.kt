package com.treasure.loopang.audiov2.noise

fun getHighestPoint(sourceData: MutableList<Short>): Short {
    var highest: Short = sourceData[0]
    for(index in 0 until sourceData.size - 1) {
        if(highest < sourceData[index + 1])
            highest = sourceData[index + 1]
    }
    return highest
}

fun getLowestPoint(sourceData: MutableList<Short>): Short {
    var lowest: Short = sourceData[0]
    for(index in 0 until sourceData.size - 1) {
        if(lowest > sourceData[index + 1])
            lowest = sourceData[index + 1]
    }
    return lowest
}