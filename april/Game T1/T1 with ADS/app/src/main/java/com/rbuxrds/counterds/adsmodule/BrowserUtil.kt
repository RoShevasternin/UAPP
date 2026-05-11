package com.rbuxrds.counterds.adsmodule

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

// Відкриває URL через Chrome Custom Tabs
//
// Чим відрізняється від звичайного Intent:
// - Браузер відкривається ПОВЕРХ додатку (не виходить з нього)
// - Юзер бачить кнопку X щоб повернутись назад в гру
// - Швидше завантаження ніж окремий браузер
// - Кращий UX — саме так реалізовано в еталоні

object BrowserUtil {

    fun open(context: Context, url: String) {
        if (url.isEmpty()) return

        runCatching {
            CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
                .launchUrl(context, url.toUri())
        }
    }
}