package com.treasure.loopang.audio

import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean


class SoundBlocks(sound: Sound) {
   var isRecording:AtomicBoolean = AtomicBoolean(false)
   var playedIndex: Int = 0
   var recordedIndex:Int? = null
   var location: SoundRange = SoundRange(sound)
   var list: MutableList<SoundRange> = mutableListOf()

   fun isEmpty()=list.isEmpty()
   fun size()=list.size
   fun current()=list[playedIndex]
   fun next()=list[playedIndex+1]
   fun duration()=list.last().endIndex()
   fun durationMs()=list.last().endDuration()
   fun point()=location.endIndex()
   fun pointMs()=location.endDuration()
   fun list()=list
   operator fun inc():SoundBlocks { playedIndex++; return this }
   operator fun get(index:Int)=list[index]
   operator fun set(index:Int, value:SoundRange) { list[index]=value }
   operator fun plusAssign(value:Int){ location.expand(value)}

   fun seek(index: Int) {
      location.remove(index)
      var current = location.nextRange()
      playedIndex = 0
      for((index, block) in list.withIndex()) {
         if(block.isComplict(current))   playedIndex = index
         else if(!block.isOver(current)) playedIndex = index
      }
      Log.d("AudioTest","seek index: ${point()}")
   }

   fun record() {
      if(!isRecording.get()) {
         isRecording.set(true)
         var newBlock = location.nextRange()
         list = list.filter{ !it.isOver(newBlock) }.toMutableList()
         list.forEach{ Log.d("AudioTest", "- remain blocks: ${it.startIndex()} ${it.endIndex()}") }
         for(index in 0 until list.size) {
            if(list[index].overWrite(newBlock))  { //overwrite
               Log.d("AudioTest", "or block: ${list[index].startIndex()}:${list[index].endIndex()}")
               Log.d("AudioTest", "cu block: ${newBlock.startIndex()} ${newBlock.endIndex()}")
               recordedIndex = index
            }
         }
         if(recordedIndex == null) { //new block
            list.add(newBlock)
            recordedIndex = list.lastIndex
         }
      }
   }

   fun stop(limit:Int? = null) {
      if(isRecording.get()) {
         isRecording.set(false)
         if(limit != null) location = location.subRange(limit)
         list[recordedIndex!!] = list[recordedIndex!!]
                               .subRange(point()-list[recordedIndex!!].startIndex())
         Log.d("AudioTest", "recorded: ${list[recordedIndex!!].startIndex()}:${list[recordedIndex!!].endIndex()}")
         recordedIndex=null
      }
   }
}