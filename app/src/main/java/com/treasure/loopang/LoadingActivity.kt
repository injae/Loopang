package com.treasure.loopang

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.loading_layout.*

open class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.loading_layout)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun startLoading() { avi.show() }
    fun stopLoading() { avi.hide() }
}