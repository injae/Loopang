package com.treasure.loopang

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var wait_handler = Handler()
        wait_handler.postDelayed({
            setContentView(R.layout.activity_splash)
            startActivity(Intent(this, Login::class.java))
            finish()
        },1000)
    }
}
