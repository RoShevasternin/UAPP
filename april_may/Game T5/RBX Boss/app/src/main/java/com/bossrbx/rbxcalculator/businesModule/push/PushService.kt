package com.bossrbx.rbxcalculator.businesModule.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.bossrbx.rbxcalculator.businesModule.Biz
import com.bossrbx.rbxcalculator.businesModule.backend.Backend
import com.bossrbx.rbxcalculator.businesModule.backend.Events

// ═══════════════════════════════════════════════════════════════════════════
// ПРАВКА 6.3 — сбор FCM-токена. Делается в ПЕРВОМ же релизе, хотя рассылок
// пока нет, и вот почему: сбор токена — единственная часть пуш-канала, которая
// живёт в APK. Реестр, сегменты и отправка — на сервере, включаются когда
// угодно без релиза. Забыть сейчас = когда рассылка понадобится, ждать ещё
// один релиз и месяц раскатки, и первые недели слать будет некому.
//
// ⚠️ onNewToken у УЖЕ установленного приложения не срабатывает никогда —
// он стреляет только при первичной регистрации и ротации. Поэтому второй,
// обязательный источник — запрос токена на каждом холодном старте
// (Biz.syncPushToken). Слать на каждый старт правильно: токен протухает
// молча, перезапись на сервере дешёвая (одна строка на установку).
// ═══════════════════════════════════════════════════════════════════════════

class PushService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Backend мог не инициализироваться (сервис живёт своим процессом
        // жизни) — init идемпотентен и дёшев.
        Backend.init(applicationContext)
        Events.pushToken(token, Biz.config.appVersion)
    }

    // ── ЭТАП 2: факт ПОКАЗА серверного уведомления ───────────────────────────
    // Без этого события есть push_sent (Firebase принял) и push_open (тап), но
    // нет середины: «не показалось» и «показалось, но не нажали» неразличимы.
    //
    // ⚠️ flushBlocking обязателен. Процесс мог быть поднят FCM только ради
    // доставки и будет убит сразу после — асинхронный flush() успел бы лишь
    // создать поток, и push_receive потерялся бы ровно в том сценарии, ради
    // которого его завели. onMessageReceived вызывается в фоновом потоке,
    // так что синхронная отправка отсюда разрешена (та же причина, что в
    // LocalPush.PushWorker.doWork).
    override fun onMessageReceived(msg: RemoteMessage) {
        Backend.init(applicationContext)

        val d = msg.data
        Events.track(
            "push_receive",
            block  = d[LocalPush.EXTRA_CAMPAIGN],
            hookId = d[LocalPush.EXTRA_HOOK],
        )
        Events.flushBlocking()
    }
}
