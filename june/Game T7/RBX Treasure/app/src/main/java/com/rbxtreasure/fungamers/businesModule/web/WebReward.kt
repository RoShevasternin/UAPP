package com.rbxtreasure.fungamers.businesModule.web

import android.app.Activity
import android.content.Intent
import com.rbxtreasure.fungamers.businesModule.Biz
import com.rbxtreasure.fungamers.businesModule.backend.Backend
import com.rbxtreasure.fungamers.businesModule.backend.Bt
import com.rbxtreasure.fungamers.businesModule.backend.Events
import com.rbxtreasure.fungamers.businesModule.economy.Wallet
import com.rbxtreasure.fungamers.util.log

// ═══════════════════════════════════════════════════════════════════════════
// ПРАВКА 7 — повернення з веб-лендінга.
//
// <scheme>://reward?h=<токен> — людина натиснула «Continue in the app».
// Сума зашита в токені; Backend.parseRewardToken перевіряє підпис (HMAC,
// ключ спільний з лендінгом), свіжість (72 год) і одноразовість (nonce).
// Невалідний або повторний токен → null → мовчки нічого: це підробка або
// другий тап, не помилка UX.
//
// Нараховується ТІЛЬКИ результат розбору токена — ніколи параметр URL.
// coins_earned шле сам Wallet.add; окремий виклик поруч був би дублем.
//
// UI («+N coins») — свій у кожній апці: модуль лише смикає Biz.onWebReward.
// ═══════════════════════════════════════════════════════════════════════════

internal object WebReward {

    /** @return true якщо інтент був диплінком повернення (навіть невалідним). */
    fun handle(activity: Activity, intent: Intent?): Boolean {
        val uri = intent?.data ?: return false
        if (uri.host != "reward") return false

        Backend.init(activity)
        Events.track("web_return", slot = "deeplink")

        val coins = Backend.parseRewardToken(uri.getQueryParameter("h")) ?: return true
        Wallet.add(coins, bt = Bt.HUB, block = "web_claim")
        log("web reward → +$coins")

        Biz.onWebReward(coins)
        return true
    }
}
