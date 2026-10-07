package com.redwave.downloader.android

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.core.content.edit
import androidx.core.content.getSystemService
import com.redwave.downloader.core.clip.ClipGate
import com.redwave.downloader.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  ClipWatcher — автоматичне читання буфера, коли Redwave отримує фокус
//  (насамперед після «Додому»). Правила — core/clip/ClipGate.kt:
//    1. лише в onWindowFocusChanged(true), не в onResume;
//    2. спершу ClipDescription (тосту немає) → ClipGate.shouldRead();
//    3. лише для НОВОГО кліпу — getPrimaryClip() (Android 12+ покаже тост).
//  Стан ClipGate — у SharedPreferences, щоб після холодного старту картка
//  не вискочила на старий кліп.
// ═════════════════════════════════════════════════════════════════════════════
class ClipWatcher(context: Context, private val bridge: AndroidBridge) {

    private val ctx   = context.applicationContext
    private val prefs = ctx.getSharedPreferences("clip_gate", Context.MODE_PRIVATE)
    private val cm    = ctx.getSystemService<ClipboardManager>()

    private val gate = ClipGate(
        lastSeenTimestamp = prefs.getLong(KEY_TS, ClipGate.NONE),
        lastSeenText      = prefs.getString(KEY_TEXT, null),
        dismissed         = prefs.getString(KEY_DISMISSED, null)?.split('\n')?.filter { it.isNotBlank() }.orEmpty(),
    )

    fun check() {
        val cm = cm ?: return
        runCatching {
            val desc = cm.primaryClipDescription ?: return
            val ts   = if (Build.VERSION.SDK_INT >= 26) desc.timestamp else null
            val hasText = desc.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ||
                desc.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML) ||
                desc.hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)
            if (!gate.shouldRead(ts, hasText)) return

            val text = cm.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(ctx)?.toString()
            val link = gate.onClipText(ts, text)
            save()
            log("clip: ts=$ts → ${link ?: "skip"}")
            if (link != null) bridge.emit { onClipboardLink(link) }
        }.onFailure { log("clip check: ${it.message}") }
    }

    fun dismiss(url: String) {
        gate.dismiss(url)
        save()
    }

    private fun save() = prefs.edit {
        putLong(KEY_TS, gate.lastSeenTimestamp)
        putString(KEY_TEXT, gate.lastSeenText)
        putString(KEY_DISMISSED, gate.dismissed.joinToString("\n"))
    }

    private companion object {
        const val KEY_TS = "ts"
        const val KEY_TEXT = "text"
        const val KEY_DISMISSED = "dismissed"
    }
}
