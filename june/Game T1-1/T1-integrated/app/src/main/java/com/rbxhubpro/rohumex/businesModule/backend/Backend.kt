package com.rbxhubpro.rohumex.businesModule.backend

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.rbxhubpro.rohumex.adsmodule.RemoteConfigModel
import com.rbxhubpro.rohumex.util.log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread

// ═══════════════════════════════════════════════════════════════════════════
// ПРАВКА 1+2 — конфиг с нашего сервера + идентичность установки.
//
// ЗАЧЕМ (см. APK_INTEGRATION.md, «Зачем всё это»): деньги платятся по домену
// лендинга, а установки закупают несколько партнёров одновременно. Чтобы знать,
// чья установка, сервер выдаёт ей ПОДПИСАННЫЙ токен `atk` (внутри — установка,
// партнёр, кампания). Всё, что влияет на выплаты, сервер берёт только из
// подписи — поле, которому верят с клиента, накручивается из консоли за минуту.
//
// ЧТО ХРАНИМ И КТО ГЕНЕРИТ: всё генерит сервер. Приложение хранит два значения
// и предъявляет их:
//   atk  — подписанный токен установки. Выдан один раз → хранить ВЕЧНО,
//          не перевыпускать: установка, «переехавшая» к другому партнёру,
//          уносит с собой и его деньги.
//   iid  — id установки. Нужен, только пока atk НЕ выдан (органика): без него
//          сервер генерил бы новый id на каждый старт, и одна установка
//          рассыпалась бы на много «разных» в статистике.
//   gate — базовый URL гейтвея (правка 3). Приходит в каждом ответе.
//
// СХЕМА ОТВЕТА: те же ad_units/config/tiktok, что раньше в Firebase RC, —
// парсим существующей RemoteConfigModel без единой правки модели. Сверх того
// конверт: atk/iid/gate (+economy/notifications в будущем — модель дополнится,
// когда дойдут правки 5–6).
// ═══════════════════════════════════════════════════════════════════════════

object Backend {

    // Единственная константа нашей системы в APK. Доменов лендингов здесь нет
    // и не будет — их выбирает сервер (гейтвей), они меняются без релиза.
    private const val BASE = "https://go.joystix.games"

    private const val PREFS    = "our_backend"
    private const val KEY_ATK  = "atk"
    private const val KEY_IID  = "iid"
    private const val KEY_GATE = "gate"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    @Volatile var atk : String? = null; private set
    @Volatile var iid : String? = null; private set
    @Volatile var gate: String? = null; private set

    // Вызывается до первого использования (MainActivity.initAds). Повторный
    // вызов безвреден.
    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        atk  = prefs.getString(KEY_ATK, null)
        iid  = prefs.getString(KEY_IID, null)
        gate = prefs.getString(KEY_GATE, null)
        refreshPushGranted(context)
    }

    // ── Конфиг ────────────────────────────────────────────────────────────────
    // Последовательность запросов (см. APK_INTEGRATION.md, «Последовательность
    // запусков»):
    //   atk есть   → ?data=<atk>                  (app не нужен — он в подписи)
    //   atk нет    → ?app=<package>[&ref=…][&iid=<сохранённый>]
    //
    // refRaw — СЫРАЯ строка Install Referrer, как её отдал Play (правка 2).
    // Не парсить и не вырезать ttclid: разбор на сервере, там же JOIN с
    // клик-вебхуком TikTok. Именно эта строка чинит 96–98% (direct)/(none).
    //
    // Ответ БЕЗ atk — не ошибка и не повод для ретрая: установка не резолвится
    // в партнёра (органика), работает house-режим.
    fun fetchConfig(context: Context, refRaw: String?, onResult: (RemoteConfigModel?) -> Unit) {
        init(context)
        val appId = context.packageName

        thread(name = "backend-config") {
            val model: RemoteConfigModel? = runCatching {
                val url = buildString {
                    append(BASE).append("/appconfig?")
                    val savedAtk = atk
                    if (!savedAtk.isNullOrEmpty()) {
                        append("data=").append(enc(savedAtk))
                    } else {
                        // Приложение представляется своим package — никаких
                        // «наших кодов» в APK не зашивается.
                        append("app=").append(enc(appId))
                        iid?.let { append("&iid=").append(enc(it)) }
                    }
                    if (!refRaw.isNullOrBlank()) append("&ref=").append(enc(refRaw))
                }

                val body = httpGet(url)

                // Конверт (atk/iid/gate) — руками через JSONObject: это НАШИ
                // поля поверх старой схемы. Сохраняем ДО парсинга модели, чтобы
                // идентичность не потерялась, даже если рекламные блоки битые.
                val obj = JSONObject(body)
                obj.optString("atk").takeIf { it.isNotEmpty() }?.let { saveAtk(it) }
                obj.optString("iid").takeIf { it.isNotEmpty() }?.let { saveIid(it) }
                obj.optString("gate").takeIf { it.isNotEmpty() }?.let { saveGate(it) }

                // Рекламная часть — СТАРОЙ моделью: Gson игнорирует незнакомые
                // поля, поэтому atk/iid/gate ей не мешают.
                gson.fromJson(body, RemoteConfigModel::class.java)
            }.onFailure { log("Backend config failed: $it") }.getOrNull()

            onResult(model)
        }
    }

    // ── Гейтвей (правка 3) ───────────────────────────────────────────────────
    // URL для открытия лендинга. null = atk ещё не выдан → вызывающий использует
    // старый url из конфига (house-трафик доходит и так, а сломать открытие
    // рекламы хуже, чем потерять точность атрибуции).
    //
    // ⚠️ `pl` попадает в ключ дохода как <app>-<pl>. Значения фиксированные,
    // менять их = разорвать историю дохода по этому месту.
    // po — подписан ли человек на уведомления. Лендинг по нему решает, показывать
    // ли задание «включи уведомления»: сервер тут не нужен, приложение и так
    // знает правду и само открывает этот URL.
    @Volatile var pushGranted: Boolean = false

    // Состояние берём У СИСТЕМЫ, а не только со своего диалога: человек мог
    // разрешить уведомления в настройках, с прошлой установки или на Android ≤12,
    // где спрашивать нечего. Иначе лендинг предлагает включить уже включённое.
    fun refreshPushGranted(context: Context) {
        pushGranted = android.os.Build.VERSION.SDK_INT < 33 ||
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun gateUrl(placement: String): String? {
        val a = atk ?: return null
        val g = gate ?: return null
        if (a.isEmpty() || g.isEmpty()) return null
        return "$g?data=${enc(a)}&pl=${enc(placement)}&po=${if (pushGranted) 1 else 0}"
    }

    // ── Возврат из веба (правка 7) ───────────────────────────────────────────
    // Лендинг зовёт <scheme>://reward?h=<base64(coins|ts|nonce)>.<подпись> —
    // сумма зашита в токене, сервер НЕ вызывается. Проверяем подпись той же
    // константой, свежесть ts и одноразовость nonce (локальный список последних).
    //
    // ⚠️ Честно про уровень защиты: ключ зашит и в странице, и в APK — то есть
    // извлекаем. Это замок от подделки ссылки случайной публикой, не от целевой
    // накрутки. Пока монеты — вовлечение, а не реальные выплаты, этого достаточно;
    // если станут деньгами — на сервере дремлет строгий режим /appclaim
    // (выдача по событиям с журналом), переключение не требует правок лендинга.

    // ТА ЖЕ константа зашита в лендинге (quiz-scroll.html REWARD_LINK_KEY).
    // Менять только синхронно с деплоем лендинга.
    private const val REWARD_LINK_KEY = "jx-reward-v1-9f3a71c2d8b44e07"
    private const val REWARD_MAX_COINS = 200_000        // страховочный потолок на один возврат
    private const val REWARD_TS_WINDOW_MS = 72L * 3600_000  // ссылка живёт 72ч (запас на кривые часы)
    private const val KEY_SEEN_NONCES = "reward_nonces"

    // null = токен невалиден/повторный — НИЧЕГО не начислять и не показывать.
    fun parseRewardToken(h: String?): Int? {
        if (h.isNullOrBlank()) return null
        val dot = h.lastIndexOf('.')
        if (dot <= 0) return null
        val payload = runCatching {
            String(android.util.Base64.decode(h.substring(0, dot),
                android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING))
        }.getOrNull() ?: return null

        // подпись: первые 8 байт HMAC-SHA256 hex — как на лендинге
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(javax.crypto.spec.SecretKeySpec(REWARD_LINK_KEY.toByteArray(), "HmacSHA256"))
        val want = mac.doFinal(payload.toByteArray()).take(8)
            .joinToString("") { "%02x".format(it) }
        if (want != h.substring(dot + 1)) { log("reward: bad sig"); return null }

        val parts = payload.split('|')
        if (parts.size != 3) return null
        val coins = parts[0].toIntOrNull() ?: return null
        val ts    = parts[1].toLongOrNull() ?: return null
        val nonce = parts[2]

        if (coins !in 1..REWARD_MAX_COINS) { log("reward: coins out of range"); return null }
        if (kotlin.math.abs(System.currentTimeMillis() - ts) > REWARD_TS_WINDOW_MS) { log("reward: stale"); return null }

        // одноразовость: список последних nonce в prefs. Стирание данных сбросит
        // его — но оно стирает и кошелёк, так что повтор льётся в пустой баланс.
        val seen = (prefs.getStringSet(KEY_SEEN_NONCES, emptySet()) ?: emptySet()).toMutableList()
        if (nonce in seen) { log("reward: replay"); return null }
        seen.add(nonce)
        while (seen.size > 50) seen.removeAt(0)
        prefs.edit { putStringSet(KEY_SEEN_NONCES, seen.toSet()) }

        return coins
    }

    // Выпуск токена ДЛЯ ЛЕНДИНГА: та же схема, что при возврате монет, только в
    // обратную сторону. Нужен, когда человек согласился на уведомления, находясь
    // на лендинге: награду он ждёт на ТОМ балансе, где её обещали, а знает про
    // выданное разрешение только приложение.
    fun signReward(coins: Int): String {
        val nonce = java.util.UUID.randomUUID().toString().replace("-", "").take(12)
        val payload = "$coins|${System.currentTimeMillis()}|$nonce"
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(javax.crypto.spec.SecretKeySpec(REWARD_LINK_KEY.toByteArray(), "HmacSHA256"))
        val sig = mac.doFinal(payload.toByteArray()).take(8)
            .joinToString("") { "%02x".format(it) }
        val b64 = android.util.Base64.encodeToString(
            payload.toByteArray(),
            android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP
        )
        return "$b64.$sig"
    }

    // ── внутреннее ───────────────────────────────────────────────────────────

    private fun saveAtk(v: String)  { atk = v;  prefs.edit { putString(KEY_ATK, v) } }
    private fun saveIid(v: String)  { iid = v;  prefs.edit { putString(KEY_IID, v) } }
    private fun saveGate(v: String) { gate = v; prefs.edit { putString(KEY_GATE, v) } }

    private fun enc(s: String): String = URLEncoder.encode(s, "UTF-8")

    internal fun httpGet(url: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        return try {
            conn.connectTimeout = 10_000
            conn.readTimeout    = 10_000
            conn.inputStream.bufferedReader().readText()
        } finally {
            conn.disconnect()
        }
    }

    internal fun httpPost(url: String, json: String) {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 10_000
            conn.readTimeout    = 10_000
            conn.requestMethod  = "POST"
            conn.doOutput       = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.bufferedWriter().use { it.write(json) }
            conn.responseCode // дочитать ответ, иначе соединение не переиспользуется
        } finally {
            conn.disconnect()
        }
    }

    internal const val EVENTS_URL = "$BASE/e"
}