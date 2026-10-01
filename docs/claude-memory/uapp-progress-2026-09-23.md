---
name: uapp-progress-2026-09-23
description: "UAPP: що зроблено 23–24.09.2026 (T3, T6, T7, T8, T2-1, T10, T23 мігровані) і що лишилось відкритим"
metadata:
  node_type: memory
  type: project
  originSessionId: a6e2e037-4a0f-4f54-80ed-506610898865
  modified: 2026-09-23T11:46:52.258Z
---

**23.09.2026 мігровано до еталона + тулчейн (Gradle 9.7.1, AGP 9.4.1, Kotlin 2.4.20,
firebase-bom 34.19.0, ads 25.5.0, appcompat 1.8.0, navigation 2.10.1, TikTok 1.7.1):**
T3 Rbux Drop (`cougame`), T6 Skinly (`skinly`, монети як альтернатива рекламі при
розблокуванні скінів), T7 RBX Treasure (`fungamers`), T8 RBX COINS (`funrbx`),
T2-1 RSBUX (`blitzrbx`). Усе перевірено на девайсі, **нічого не закомічено**.
Журнал кожної — в `docs/procedures/ETALON_MIGRATION.md`, список мігрованих — у `CLAUDE.md`.

**24.09.2026:** T10 RBX Sakura (`july/Game T10`, `fungambx`) — той самий шаблон
T9/T8, донор T8 трибічним злиттям + ручні правки (своя верстка меню). Перевірено
на девайсі, **не закомічено**. Колесо T10 має 15 номіналів — `DEFAULT_SUMS`
звіряти з enum. Потім T23 RBX Racing (`funtols`) — свої механіки + MSDF-шрифти,
переважно вручну (див. журнал). Поруч у `july/` ще T11 RBX MONSTERS і T28 — не
мігровані; T28 найближча до T23 за кодом (94 файли) — донор для неї T23.

**Відкрите — спитати користувача на старті:**
1. Бекпорт фіксу крашу на оновленні (`ignoreUnknownKeys` + runCatching у
   `DataStoreJsonUtil` І `SaveGameStateManager`) у T5, T1, T4, T9, T2, T7-1 —
   користувач ще не відповів. Відтворено на T3 і T7: після прибирання
   `PlayerData.rbx` старі юзери крашаться на кожному старті.
2. T7-1: досі нативка в меню і `marginBottom +=` у Finds/Wheel.
3. T6: після фіксу `SaveGameStateManager` зібрано, але на девайсі не перепроганяно.
4. Усі нові апки: versionCode не піднятий, release непідписаний (пуші на release
   не перевірені); серверній команді віддати схеми диплінків і ключі економіки.

**Прийоми, що спрацювали:** апку-двійника вже мігрованої (T7↔T7-1, T8↔T9,
T2-1↔T2) мігрувати трибічним злиттям `git merge-file` (апка × донор-до-міграції з
git × донор-після) — див. журнал T8. Краш на оновленні перевіряти, підкладаючи
старий JSON у `files/datastore/DATA_STORE.preferences_pb` через `run-as`.
Redmi 10C (MIUI): install поверх (`-r`) відхиляється — лише uninstall → install,
кнопку «Встановити» тиснути за bounds з uiautomator.

Пов'язане: [[uapp-server-patch-backport]], [[uapp-no-native-on-menu]], [[uapp-release-notes-style]]
