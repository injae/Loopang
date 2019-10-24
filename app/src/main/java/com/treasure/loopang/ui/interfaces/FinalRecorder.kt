package com.treasure.loopang.ui.interfaces

interface FinalRecorder {
    fun getRecordDuration(): Int    // 녹음 총 길이 반환
    fun getLoopDuration(): Int  // 루프(반주) 총 길이 반환
    fun getRecordPosition(): Int    // 현재 녹음(재생) 위치
    fun getLoopPosition(): Int      // 현재 녹음위치에 상대적인 루프(반주) 위치 (계산해줘야함) 반환
    fun seekToStart()               // 맨 앞으로 이동
    fun seekToEnd()                 // 마지막 녹음 위치로 이동
    fun seekTo(ms: Int)             // 해당 ms 위치로 이동
    fun playStart()                 // 재생 시작
    fun playStop()                  // 재생 정지
    fun recordStart()               // 녹음 시작
    fun recordStop()                // 녹음 정지
    fun export(title: String, soundFormat: String): Boolean         // 저장(파일이름, 저장형식), 성공여부 반환

    fun onPlayStart(recordPosition: Int)              // 재생시작일 때
    fun onPlayStop(recordPosition: Int)                 // 재생정지일 때
    fun onRecrodStart(recordPosition: Int)              // 녹음시작일 때
    fun onRecordStop(recordPosition: Int)               // 녹음 정지일 때

    fun isRecording(): Boolean          // 녹음 중인가?
    fun isPlaying(): Boolean            // 재생 중

    fun setMute(layerId: Int, flag: Boolean)    // 레이어 뮤트 (레이어 아이디, 뮤트 여부)
    fun setOverwrite(flag: Boolean)             // 덮어 쓸지 말지 결정(덮어쓰는 여부)
    fun setMetronome(flag: Boolean)             // 메트로놈 켜기(켜는 여부)

    fun setEffectToBlock(layerId: Int, blockId: Int, effect: Any? = null)   // 블록에 이펙트 적용(레이어아이디, 블록아이디, 이펙트) // 이펙트는 형식이 뭘까...
    fun setVolumeToBlock(layerId: Int, blockId: Int, volume: Int)   // 블록 볼륨 조정(레이어아이디, 블록아이디, 볼륨)
    fun setVolumeToLayer(layerId: Int, volume: Int)                 // 레이어 볼륨 조정(레이어아이디, 볼륨)
    fun setVolumeToLoop(volume: Int)                                // 루프(반주) 볼륨조정(볼륨)

    fun getEffectList(): List<Any>
    fun getBlockList(): List<List<Any>>

}