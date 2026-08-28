package com.rbxhubpro.rohumex.businesModule.backend

import com.rbxhubpro.rohumex.util.log
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

// ═══════════════════════════════════════════════════════════════════════════
// ПРАВКА 4 — события. Единственная связь дохода (он приходит ПО ДОМЕНУ) с
// установкой, механикой и монетами. Без событий не считаются LTV установки,
// retention и эффект любых осей.
//
// КОНТРАКТ (общий на все 30 приложений — имена событий фиксированы, специфика
// едет полями, см. APK_INTEGRATION.md правка 4):
//   session.data = atk — ЕДИНСТВЕННАЯ привязка события к установке/партнёру.
//   install_id в теле НЕ передаётся: сервер игнорирует его намеренно и берёт
//   из подписи atk — клиентскому полю про деньги верить нельзя.
//   bt     — общий тип механики. Типизирован enum-ом Bt: строка из словаря
//            приезжает из одного места, опечатка не компилируется.
//   block  — местное имя экрана/механики (snake_case)
//   slot   — плейсмент (для gate_open)
//   amount — монеты/награда/счёт. ⚠️ именно amount, НЕ step: step на сервере
//            UInt8 (макс 255), награда 800 молча превратилась бы в 32.
//   token  — только для push_token (FCM-токен, правка 6.3)
//
// ДОСТАВКА: fire-and-forget, батч копится в памяти и уходит одним POST.
// Потеря батча при убийстве процесса — приемлемая цена: это метрики, не
// транзакции; ретраи и спулинг здесь дали бы дубли, с которыми серверу
// бороться дороже, чем нам — смириться с недосчётом долей процента.
// ═══════════════════════════════════════════════════════════════════════════

object Events {

    // id запуска: живёт от холодного старта до убийства процесса
    private val sessionId = UUID.randomUUID().toString()
    private val startedAt = System.currentTimeMillis()

    private val queue = ConcurrentLinkedQueue<JSONObject>()
    private val flushing = AtomicBoolean(false)

    fun track(
        type  : String,
        bt    : Bt?     = null,
        block : String? = null,
        slot  : String? = null,
        amount: Int?    = null,
        token : String? = null,
        hookId: String? = null, // правка 6.2: push_* события несут id хука (арм бандита)
    ) {
        val e = JSONObject()
        e.put("type", type)
        e.put("t", System.currentTimeMillis() - startedAt) // мс от старта — диагностика
        bt?.let     { e.put("bt", it.key) }
        block?.let  { e.put("block", it) }
        slot?.let   { e.put("slot", it) }
        amount?.let { e.put("amount", it) }
        token?.let  { e.put("token", it) }
        hookId?.let { e.put("hook_id", it) }
        queue.add(e)
        flush()
    }

    // Готовые обёртки под контракт — чтобы имена событий не размножались
    // опечатками по коду. Новые события добавлять СЮДА, не инлайном.
    fun appOpen() = track("app_open")
    fun screenView(bt: Bt, block: String) = track("screen_view", bt = bt, block = block)
    fun featureComplete(bt: Bt, block: String, amount: Int? = null) =
        track("feature_complete", bt = bt, block = block, amount = amount)

    fun coinsEarned(bt: Bt, block: String, amount: Int) =
        track("coins_earned", bt = bt, block = block, amount = amount)

    fun coinsSpent(bt: Bt, block: String, amount: Int) =
        track("coins_spent", bt = bt, block = block, amount = amount)

    fun gateOpen(placement: String) = track("gate_open", slot = placement)
    fun pushToken(token: String, appVersion: String) =
        track("push_token", token = token, block = appVersion)

    // Один POST на всё, что накопилось. flushing-гейт: параллельные track()
    // не порождают параллельных отправок — второй просто увидит пустую очередь.
    private fun flush() {
        if (!flushing.compareAndSet(false, true)) return
        thread(name = "backend-events") {
            try {
                drainAndSend()
            } finally {
                flushing.set(false)
                // пока отправляли, могли накопиться новые
                if (queue.isNotEmpty()) flush()
            }
        }
    }

    // ДОБАВЛЕНО: синхронная отправка — ТОЛЬКО для LocalPush.PushWorker.
    //
    // ЗАЧЕМ: WorkManager может поднять процесс с нуля исключительно ради показа
    // уведомления и убить его сразу после doWork(). Асинхронный flush() в этот
    // момент только создаёт поток — POST не успевает уйти, и push_receive /
    // push_suppressed теряются РОВНО в том сценарии, ради которого их завели
    // (в проде мы уже видели ноль push_receive при 88 push_scheduled).
    // Звать только из фонового потока: doWork() у Worker'а именно такой.
    fun flushBlocking() {
        runCatching { drainAndSend() }.onFailure { log("Events flushBlocking failed: $it") }
    }

    // Забрать всё из очереди и отправить одним POST'ом. Каждое событие
    // вынимается ровно один раз, поэтому параллельный вызов из flush() и
    // flushBlocking() дубли не породит — в худшем случае будет два POST'а.
    private fun drainAndSend() {
        val batch = JSONArray()
        while (true) {
            val e = queue.poll() ?: break
            batch.put(e)
        }
        if (batch.length() == 0) return

        val session = JSONObject().put("id", sessionId)
        // atk может ещё не существовать (первый старт до ответа /appconfig) —
        // событие уйдёт как анонимное веб-событие и в app-отчёты не попадёт.
        // Это честнее, чем задерживать отправку.
        Backend.atk?.let { session.put("data", it) }

        val body = JSONObject()
            .put("v", 1)
            .put("session", session)
            .put("events", batch)

        runCatching { Backend.httpPost(Backend.EVENTS_URL, body.toString()) }
            .onFailure { log("Events flush failed: $it") }
    }
}