package com.treasure.loopang.audio

class NativeAudioManager{
   companion object {
      init { System.loadLibrary("native-audio-lib") }
   }

   external fun create() : Boolean
   external fun delete()
   external fun EchoOn(is_on: Boolean)
   external fun setRecordingDeviceId(device_id: Int)
   external fun setPlaybackDeviceId(device_id: Int)
   external fun startEngine(cpuIds: IntArray)
   external fun tab(b: Boolean)
   external fun stopEngine()
   external fun startTest()
   external fun tabTest()
}