package com.treasure.loopang.listitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.treasure.loopang.R

import kotlinx.android.synthetic.main.set_metronome_frame.*
import org.w3c.dom.Text

class setMetronome : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_metronome_frame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var metronomeBpm: Int = 120 //얘를 나중에 메트로놈 bpm 실제 매개변수로 변경
        metronomeBpmText.setText("" + metronomeBpm)
        imformBpmTempo(metronomeBpm)

        btn_bpmUp.setOnClickListener {
            metronomeBpm += 1
            metronomeBpmText.setText("" + metronomeBpm)
            imformBpmTempo(metronomeBpm)
        }
        btn_bpmDown.setOnClickListener {
            if (metronomeBpm > 10) {
                metronomeBpm -= 1
                metronomeBpmText.setText("" + metronomeBpm)
                imformBpmTempo(metronomeBpm)
            }
        }

        var NumOfClick: Int = 0
        btn_beat.setOnClickListener {
            NumOfClick++
            if (NumOfClick % 2 == 0) {
                btn_beat.setText("4/4")
                //비트 4/4로 설정
            } else {
                btn_beat.setText("3/4")
                //비트 3/4로 설정
            }
        }
    }

    //얘는 그냥 어떤 템포인지 이름 알려주는 앤데 필요없으면 삭제할거임
    fun imformBpmTempo(metronomeBpm: Int) {
        val sortTempo: Array<Int> =
            arrayOf(20, 40, 45, 50, 55, 65, 69, 73, 78, 83, 86, 98, 110, 132, 140, 150, 168, 178) //메트로놈 앱 따라서 하긴 했는데 수정하려면 숫자랑 이름만 바꾸면 됨
        val TempoName: Array<String> = arrayOf("Laghissimo", "Grave", "Lento","Largo", "Larghetto",
            "Adagio", "Adagietto", "Andante moderato", "Andante", "Andantino",
            "Marcia moderato", "Moderato", "Allegretto", "Allegro", "Vivace",
            "Vivacissimo", "Allegrissimo", "Presto", "Prestissimo"
        )
        for (i in 0..18) { //TempoName의  index로 접근
            if (i == 0) {
                if (metronomeBpm < sortTempo[i])
                    bpmTempo.setText(TempoName[i])
            }
            else if (i >= 1  && i <= 17){
                if (metronomeBpm >= sortTempo[i - 1] && metronomeBpm < sortTempo[i])
                    bpmTempo.setText(TempoName[i])
            }
            else { // i == 18
                if (metronomeBpm >= sortTempo[i-1])
                    bpmTempo.setText(TempoName[i])
            }
        }
    }

}



