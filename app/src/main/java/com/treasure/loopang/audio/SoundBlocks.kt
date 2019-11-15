package com.treasure.loopang.audio

import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean


class SoundBlocks(sound: Sound) {
   var isRecording:AtomicBoolean = AtomicBoolean(false)
   var playedIndex: Int = 0
   var recordedIndex:Int? = null
   var location: SoundRange = SoundRange(sound)
   var blocks: MutableList<SoundRange> = mutableListOf()

   fun isEmpty()=blocks.isEmpty()
   fun size()=blocks.size
   fun current()=blocks[playedIndex]
   fun next()=blocks[playedIndex+1]
   fun duration()=blocks.last().endIndex()
   fun point()=location.endIndex()
   operator fun inc():SoundBlocks { playedIndex++; return this }
   operator fun get(index:Int)=blocks[index]
   operator fun set(index:Int, value:SoundRange) { blocks[index]=value }
   operator fun plusAssign(value:Int){ location.expand(value)}

   fun seek(index: Int) {
      location.remove(index)
      var current = location.nextRange()
      playedIndex = 0
      for((index, block) in blocks.withIndex()) {
         if(block.isComplict(current)) playedIndex = index
         else if(!block.isOver(current))    playedIndex = index
      }
      Log.d("AudioTest","seek index: ${location.endIndex()}")
   }

   fun record() {
      isRecording.set(true)
      var newBlock = location.nextRange()
      blocks = blocks.filter{ !it.isOver(newBlock) }.toMutableList()
      blocks.forEach{ Log.d("AudioTest", "- remain blocks: ${it.startIndex()} ${it.endIndex()}") }
      for(index in 0 until blocks.size) {
         if(blocks[index].overWrite(newBlock))  { //overwrite
            Log.d("AudioTest", "or block: ${blocks[index].startIndex()}:${blocks[index].endIndex()}")
            Log.d("AudioTest", "cu block: ${newBlock.startIndex()} ${newBlock.endIndex()}")
            recordedIndex = index
         }
         if(recordedIndex == null) { //new block
            blocks.add(newBlock)
            recordedIndex = blocks.lastIndex
         }
      }
   }

   fun stop(limit:Int? = null) {
      if(!isRecording.get()) {
         isRecording.set(false)
         if(limit != null) location = location.subRange(limit)
         blocks[recordedIndex!!] = blocks[recordedIndex!!]
                                      .subRange(location.endIndex()-blocks[recordedIndex!!].startIndex())
      }
   }
}