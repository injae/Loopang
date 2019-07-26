package com.treasure.loopang.ui

import android.content.Context
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

fun statusBarHeight(context: Context): Int {
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")

    return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId)
    else 0
}