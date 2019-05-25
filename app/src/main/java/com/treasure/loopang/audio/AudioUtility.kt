package com.treasure.loopang.audio

fun convertShortToByte(short: Short): ByteArray {
    var array = ByteArray(2)
    var byte = short.toInt()
    array[0] = (byte and 0xff) as Byte
    array[1] = ((byte shr 8) and 0xff) as Byte
    return array
}

fun convertShortArrayToByteArray(array: ShortArray): ByteArray {
    var byte_array = ByteArray(array.size*2)
    var index = 0
    array.forEach {
        var bytes = convertShortToByte(it)
        byte_array[index]  = bytes[0]
        byte_array[index+1]= bytes[1]
        index+=2
    }
    return byte_array
}

fun convertByteArrayToShortArray(array: ByteArray): ShortArray {
    return array.asList()
                .chunked(2)
                .map { (l, h) -> (l.toInt() + h.toInt().shl(8)).toShort() }
                .toShortArray()
}
