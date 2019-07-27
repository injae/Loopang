package com.treasure.loopang.listitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.R

import kotlinx.android.synthetic.main.set_metronome_frame.*

class setMetronome : androidx.fragment.app.Fragment() {
    var tempos = listOf(
           20 to "Laghissimo"
        ,  40 to "Grave"
        ,  45 to "Lento"
        ,  50 to "Largo"
        ,  55 to "Larghetto"
        ,  65 to "Adagio"
        ,  69 to "Adagietto"
        ,  73 to "Andante Moderato"
        ,  78 to "Andante"
        ,  83 to "Andantino"
        ,  86 to "Marcia Moderato"
        ,  98 to "Moderato"
        , 110 to "Allegretto"
        , 132 to "Allegro"
        , 140 to "Vivace"
        , 150 to "Vivacissimo"
        , 168 to "Allegrissimo"
        , 178 to "Presto"
        , 219 to "Prestissimo")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_metronome_frame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var metronomeBpm = 120 //얘를 나중에 메트로놈 bpm 실제 매개변수로 변경
        metronomeBpmText.text = "" + metronomeBpm
        informBpsTempo(metronomeBpm)

        btn_bpmUp.setOnClickListener {
            metronomeBpm += 1
            metronomeBpmText.text = "" + metronomeBpm
            informBpsTempo(metronomeBpm)
        }
        btn_bpmDown.setOnClickListener {
            if (metronomeBpm > 10) {
                metronomeBpm -= 1
                metronomeBpmText.text = "" + metronomeBpm
                informBpsTempo(metronomeBpm)
            }
        }

        var NumOfClick = 0
        btn_beat.setOnClickListener {
            NumOfClick++
            if (NumOfClick % 2 == 0) {
                btn_beat.text ="4/4"
                //비트 4/4로 설정
            } else {
                btn_beat.text ="3/4"
                //비트 3/4로 설정
            }
        }
    }

    //얘는 그냥 어떤 템포인지 이름 알려주는 앤데 필요없으면 삭제할거임
    fun informBpsTempo(metronomeBpm: Int) {
        tempos.reversed()
              .fold("") { acc, tempo -> if(metronomeBpm < tempo.first) tempo.second else acc }
              .apply { bpmTempo.text = this }
    }

}



