package com.driftglass.home.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

val Any.currentClassName: String get() = this::class.java.simpleName

/** Тег логів апки: `adb logcat -s driftglass:V AndroidRuntime:E`. */
fun log(message: String) {
    Log.i("driftglass", message)
}

fun cancelCoroutinesAll(vararg coroutine: CoroutineScope?) {
    coroutine.forEach { it?.cancel() }
}
