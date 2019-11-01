package com.treasure.loopang

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import kotlinx.android.synthetic.main.loading_layout.*

open class LoadingActivity(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)
        window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        setCancelable(false)
    }

    override fun show() {
        super.show()
        avi.smoothToShow()
    }

    override fun dismiss() {
        super.dismiss()
        avi.smoothToHide()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        return
    }
}