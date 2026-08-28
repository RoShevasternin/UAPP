package com.rbxhubpro.rohumex.adsmodule

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.rbxhubpro.rohumex.businesModule.backend.Backend
import com.rbxhubpro.rohumex.businesModule.backend.Events

// Відкриває URL через Chrome Custom Tabs
//
// Чим відрізняється від звичайного Intent:
// - Браузер відкривається ПОВЕРХ додатку (не виходить з нього)
// - Юзер бачить кнопку X щоб повернутись назад в гру
// - Швидше завантаження ніж окремий браузер

object BrowserUtil {

    // ═══ ПРАВКА 3 — рекламные открытия идут через НАШ гейтвей ═══════════════
    // Приложение больше не знает доменов лендингов: предъявляет atk гейтвею,
    // тот отвечает 302 на домен ЭТОЙ установки. Что это даёт:
    //   - выгоревший/сменившийся домен правится на сервере, ни одно устройство
    //     не обновляется (APK раскатывается месяц);
    //   - трафик разных партнёров физически не смешивается на домене → разрез
    //     дохода AdSense по доменам = разрез по партнёрам;
    //   - куда вести (какой раздел лендинга) настраивается в карточке
    //     приложения (gate_paths), тоже без релиза.
    //
    // fallbackUrl — старый target_url из конфига. Используется, ТОЛЬКО пока
    // atk не выдан (органика до резолва): сломать показ рекламы хуже, чем
    // потерять точность атрибуции на этом хвосте.
    //
    // ⚠️ placement попадает в ключ дохода как <app>-<placement>. Значения
    // фиксированные (banner/native/front/back/interstitial/app_open) — смена
    // строки разрывает историю дохода по этому месту.
    fun openAd(context: Context, fallbackUrl: String, placement: String) {
        val target = Backend.gateUrl(placement) ?: fallbackUrl
        if (target.isEmpty()) return
        // gate_open — знаменатель воронки монетизации (открыли → показ → доход)
        Events.gateOpen(placement)
        open(context, target)
    }

    // Прямое открытие БЕЗ гейтвея — только для НЕ-рекламных ссылок
    // (privacy policy, Play Store). Рекламные вызовы обязаны идти через openAd.
    fun open(context: Context, url: String) {
        if (url.isEmpty()) return

        // Уходим в СВОЙ Custom Tab — помечаем, чтобы возврат из него не был
        // воспринят как «пользователь вернулся из фона» и не запустил app_open-гейт
        // поверх (иначе таб открывается сам сразу после закрытия — петля).
        // Тот же механизм уже используют AdManager и app_open-таб AppOpenManager.
        AdConfig.isFullscreenAdShowing = true

        runCatching {
            CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
                .launchUrl(context, url.toUri())
        }
    }
}