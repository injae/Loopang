package com.treasure.loopang

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.treasure.loopang.adapter.SongPagerAdapter
import kotlinx.android.synthetic.main.activity_recording.*

class Recording : AppCompatActivity() {
    private val pagerAdapter by lazy { SongPagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        /*
        // fragment test
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_frame, SongManageFragment())
            .commit()
        */

        pager.adapter = Recording@pagerAdapter
    }
}