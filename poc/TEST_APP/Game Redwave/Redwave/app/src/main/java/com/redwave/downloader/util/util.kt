package com.redwave.downloader.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

val Any.currentClassName: String get() = this::class.java.simpleName

/** Тег логів апки: `adb logcat -s redwave:V AndroidRuntime:E`. */
fun log(message: String) {
    Log.i("redwave", message)
}

fun cancelCoroutinesAll(vararg coroutine: CoroutineScope?) {
    coroutine.forEach { it?.cancel() }
}
