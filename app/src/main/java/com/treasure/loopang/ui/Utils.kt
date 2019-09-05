package com.treasure.loopang.ui

import android.app.Activity
import android.content.Context
import android.text.format.Formatter
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

fun Activity.toast(res: Int) {
        val message = getString(res)
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .apply { show() }
}

fun Activity.toast(message: CharSequence) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        .apply { show() }
}

fun stringForTime(timeMs: Int): String {
    val totalSeconds = timeMs / 1000

    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

fun statusBarHeight(context: Context): Int {
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")

    return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId)
    else 0
}