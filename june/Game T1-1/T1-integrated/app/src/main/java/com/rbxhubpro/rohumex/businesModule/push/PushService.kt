package com.rbxhubpro.rohumex.businesModule.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.rbxhubpro.rohumex.businesModule.Biz
import com.rbxhubpro.rohumex.businesModule.backend.Backend
import com.rbxhubpro.rohumex.businesModule.backend.Events

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
// (MainActivity.syncPushToken). Слать на каждый старт правильно: токен
// протухает молча, перезапись на сервере дешёвая (одна строка на установку).
//
// Сами уведомления этапа 1 — ЛОКАЛЬНЫЕ (WorkManager по правилам из конфига,
// правка 6.2) — этому сервису не нужны; onMessageReceived здесь появится
// только на этапе 2 (FCM-рассылка).
// ═══════════════════════════════════════════════════════════════════════════

class PushService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Backend мог не инициализироваться (сервис живёт своим процессом
        // жизни) — init идемпотентен и дешёв.
        Backend.init(applicationContext)
        Events.pushToken(token, Biz.config.appVersion)
    }
}
