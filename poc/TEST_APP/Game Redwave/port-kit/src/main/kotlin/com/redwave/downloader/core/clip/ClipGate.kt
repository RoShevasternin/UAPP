package com.redwave.downloader.core.clip

import com.redwave.downloader.core.link.LinkResolver
import com.redwave.downloader.core.link.ResolvedLink
import com.redwave.downloader.core.link.isDownloadable
import com.redwave.downloader.core.link.sourceUrl

// ═════════════════════════════════════════════════════════════════════════════
//  ClipGate — коли лаунчеру можна читати буфер і чи показувати картку.
//
//  ТРИ ПРАВИЛА ANDROID, ЗАРАДИ ЯКИХ ЦЕ ІСНУЄ:
//    1. Android 10+: буфер читає лише апка У ФОКУСІ. У onResume фокусу ще
//       немає → getPrimaryClip() поверне null. Викликати з
//       onWindowFocusChanged(hasFocus = true).
//    2. Android 12+: кожне ПЕРШЕ читання кліпу з іншої апки показує системний
//       тост «Redwave pasted from your clipboard». getPrimaryClipDescription()
//       тосту не викликає — тому спершу дивимось timestamp опису і читаємо
//       текст лише для НОВОГО кліпу.
//    3. ClipDescription.getTimestamp() — з API 26. На 24–25 (minSdk 24) тосту
//       ще немає взагалі, тож там читаємо і дедуплікуємо за самим текстом.
//
//  Стан (lastSeenTimestamp, dismissed) треба зберігати між запусками —
//  інакше після холодного старту картка вискочить на старий кліп.
// ═════════════════════════════════════════════════════════════════════════════
class ClipGate(
    var lastSeenTimestamp: Long = NONE,
    var lastSeenText: String? = null,
    dismissed: Collection<String> = emptyList(),
) {
    companion object {
        const val NONE = -1L
        /** Скільки закритих лінків пам'ятаємо. */
        const val DISMISSED_LIMIT = 50
    }

    private val dismissedUrls = LinkedHashSet(dismissed)
    val dismissed: List<String> get() = dismissedUrls.toList()

    /**
     * Крок 1: є опис кліпу. Чи варто читати текст?
     * @param timestamp ClipDescription.getTimestamp() або null на API < 26
     * @param hasText   description.hasMimeType(MIMETYPE_TEXT_PLAIN) || TEXT_HTML || TEXT_URILIST
     */
    fun shouldRead(timestamp: Long?, hasText: Boolean): Boolean {
        if (!hasText) return false
        if (timestamp == null) return true               // API 24–25: дедуп за текстом у onClipText
        return timestamp != lastSeenTimestamp
    }

    /**
     * Крок 2: текст прочитано. Повертає лінк для картки або null.
     * Заблоковані сервіси й «не аудіо» → null: картку не показуємо зовсім.
     */
    fun onClipText(timestamp: Long?, text: String?): ResolvedLink? {
        if (timestamp != null) lastSeenTimestamp = timestamp
        else if (text == lastSeenText) return null
        lastSeenText = text

        val r = LinkResolver.resolve(text)
        if (!r.isDownloadable) return null
        if (r.sourceUrl in dismissedUrls) return null
        return r
    }

    /** Користувач закрив картку або вже завантажив — для цього лінка більше не показуємо. */
    fun dismiss(url: String) {
        dismissedUrls.remove(url)
        dismissedUrls.add(url)
        while (dismissedUrls.size > DISMISSED_LIMIT) dismissedUrls.remove(dismissedUrls.first())
    }
}
