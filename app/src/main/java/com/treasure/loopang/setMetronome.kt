package com.treasure.loopang.listitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.set_metronome_frame.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.treasure.loopang.R
import com.treasure.loopang.audio.Metronome
import kotlinx.android.synthetic.main.fragment_record.*



class setMetronome : androidx.fragment.app.Fragment() {
    var metronome = Metronome(task={ Log.d("Metronome test", "tick")})

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

        return inflater.inflate(com.treasure.loopang.R.layout.set_metronome_frame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       // note.bpm(bpm = metronomeBpm.toLong()) getMetronomeBpm(Metronome()).toString()
        metronomeBpmText.text =  metronome.bpm.toString()
        informBpsTempo(metronome.bpm)

        btn_bpmUp.setOnClickListener {
            metronome.bpm += 1
            metronomeBpmText.text = metronome.bpm.toString()
            informBpsTempo(metronome.bpm)
        }
        btn_bpmDown.setOnClickListener {
            if ( metronome.bpm> 10) {
                metronome.bpm -= 1
                metronomeBpmText.text =  metronome.bpm.toString()
                informBpsTempo(metronome.bpm)
            }
        }

        StateNonEditTempo()

        btn_for_edit_bpm.setOnClickListener{
            StateEditTempo()
        }
        EditBpm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //텍스트에 변화가 있을때
            }

            override fun afterTextChanged(s: Editable) {
                //                //텍스트 변화가 끝났을때
                metronomeBpmText.text = EditBpm.text//텍스트 뷰를 바꿈
                metronome.bpm = EditBpm.text.toString().toLong()
                informBpsTempo(metronome.bpm) //알려줌 ㅇㅇ
            }
        })

        //n분음표 -> 2,4,8,16,32,64,128 7개
        var NumOfClick = 0
        btn_note.setOnClickListener {
            NumOfClick++
            when(NumOfClick%7){
                0 -> { btn_note.setImageResource(R.drawable.note_4)
                    metronome.note.parent = 4}
                1 -> {  btn_note.setImageResource(R.drawable.note_8)
                    metronome.note.parent = 8 }
                2 -> {  btn_note.setImageResource(R.drawable.note_16)
                    metronome.note.parent = 16 }
                3 -> {  btn_note.setImageResource(R.drawable.note_32)
                    metronome.note.parent = 32 }
                4 -> {  btn_note.setImageResource(R.drawable.note_64)
                    metronome.note.parent = 64 }
                5 -> { btn_note.setImageResource(R.drawable.note_128)
                    metronome.note.parent = 128 }
                6 -> {  btn_note.setImageResource(R.drawable.note_2)
                    metronome.note.parent = 2 }
            }
        }
    }
    //버그있음 수정하렴 근데 얘가 문제는 아니지 ㅋㅋ 저위에 적어ㅜ
    fun StateEditTempo(){
        metronomeBpmText.visibility = View.INVISIBLE
        btn_for_edit_bpm.visibility =View.INVISIBLE
        EditBpm.visibility = View.VISIBLE
    }
    fun StateNonEditTempo(){
        metronomeBpmText.visibility = View.VISIBLE
        btn_for_edit_bpm.visibility =View.VISIBLE
        EditBpm.visibility = View.GONE
    }
    //얘는 그냥 어떤 템포인지 이름 알려주는 앤데 필요없으면 삭제할거임
    fun informBpsTempo(metronomeBpm: Long) {
        tempos.reversed()
              .fold("") { acc, tempo -> if(metronomeBpm < tempo.first) tempo.second else acc }
              .apply { bpmTempo.text = this }
    }
}



