package com.treasure.loopang.ui

import android.widget.Toast
import androidx.fragment.app.Fragment

private var toast: Toast? = null

internal fun Fragment.toast(message: CharSequence) {
    activity?.let{
        toast?.cancel()
        toast = Toast.makeText(it, message, Toast.LENGTH_SHORT)
            .apply { show() }
    }
}

internal fun Fragment.toast(res: Int) {
    activity?.let{
        val message = getString(res)
        toast?.cancel()
        toast = Toast.makeText(it, message, Toast.LENGTH_SHORT)
            .apply { show() }
    }
}