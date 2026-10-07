package com.redwave.downloader.android.ringtone

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.content.edit
import com.redwave.downloader.core.ringtone.SaveAs
import com.redwave.downloader.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  SystemSounds — пам'ять «що стояло до Redwave» для рингтону / будильника /
//  сповіщення, щоб кнопка Restore повертала саме це (VELDAN, 07.10.2026).
//
//  Оригінал запам'ятовуємо ЛИШЕ коли поточний звук не наш: друга, третя заміна
//  не затирають справжній системний звук. Повернули → пам'ять очищаємо.
// ═════════════════════════════════════════════════════════════════════════════
object SystemSounds {

    private const val PREFS = "system_sounds"

    fun type(saveAs: SaveAs) = when (saveAs) {
        SaveAs.RINGTONE     -> RingtoneManager.TYPE_RINGTONE
        SaveAs.ALARM        -> RingtoneManager.TYPE_ALARM
        SaveAs.NOTIFICATION -> RingtoneManager.TYPE_NOTIFICATION
    }

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Перед setActualDefaultRingtoneUri: зберегти поточний, якщо він не наш. */
    fun rememberBeforeSet(ctx: Context, saveAs: SaveAs) {
        val p = prefs(ctx)
        val current = RingtoneManager.getActualDefaultRingtoneUri(ctx, type(saveAs))?.toString() ?: NONE
        val ours = p.getStringSet("ours_${saveAs.name}", emptySet()).orEmpty()
        if (current in ours) return
        p.edit { putString("orig_${saveAs.name}", current) }
        log("remember original ${saveAs.name}: $current")
    }

    /** Після встановлення нашого файлу — позначити його як «наш». */
    fun markOurs(ctx: Context, saveAs: SaveAs, uri: Uri) {
        val p = prefs(ctx)
        val ours = p.getStringSet("ours_${saveAs.name}", emptySet()).orEmpty() + uri.toString()
        p.edit { putStringSet("ours_${saveAs.name}", ours) }
    }

    /** Що стояло до Redwave; null — не знаємо (змінено ще до цієї функції). */
    fun original(ctx: Context, saveAs: SaveAs): Uri? =
        prefs(ctx).getString("orig_${saveAs.name}", null)?.let { if (it == NONE) null else Uri.parse(it) }

    fun hasOriginal(ctx: Context, saveAs: SaveAs) = prefs(ctx).contains("orig_${saveAs.name}")

    /** Чи стоїть зараз наш звук. */
    fun isOursActive(ctx: Context, saveAs: SaveAs): Boolean {
        val current = RingtoneManager.getActualDefaultRingtoneUri(ctx, type(saveAs))?.toString() ?: return false
        return current in prefs(ctx).getStringSet("ours_${saveAs.name}", emptySet()).orEmpty()
    }

    fun set(ctx: Context, saveAs: SaveAs, uri: Uri?) {
        RingtoneManager.setActualDefaultRingtoneUri(ctx, type(saveAs), uri)
        log("set ${saveAs.name} → $uri")
    }

    fun forgetOriginal(ctx: Context, saveAs: SaveAs) = prefs(ctx).edit { remove("orig_${saveAs.name}") }

    /** «Тиша» теж буває оригіналом: користувач міг мати сповіщення без звуку. */
    private const val NONE = "none"
}
