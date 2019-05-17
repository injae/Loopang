package com.treasure.loopang

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class Recording : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        // fragment test
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_frame, RecordFragment())
            .commit()
    }
}
