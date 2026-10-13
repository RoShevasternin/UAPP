package com.vidtools.framefall.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

val Any.currentClassName: String get() = this::class.java.simpleName

/** Тег логів апки: `adb logcat -s framefall:V AndroidRuntime:E`. */
fun log(message: String) {
    Log.i("FRAMEFALL", message)
}

fun cancelCoroutinesAll(vararg coroutine: CoroutineScope?) {
    coroutine.forEach { it?.cancel() }
}
