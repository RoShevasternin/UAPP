// ─────────────────────────────────────────────────────────────────────────────
// Нотатки до екранів прототипу: що буде так, як у дизайні, а що на Android інакше / виправити дизайнеру / уточнити в менеджера.
// Правило (VELDAN 26.11): робимо строго як у Figma, нічого не правимо — лише позначаємо тут.
//   kind: ok — так і буде · android — на Android працює інакше · design — виправити в Figma · ask — уточнити в менеджера
//   box: [x, y, w, h] у координатах кадру 375×812 — рамка на телефоні (без box — лише текст)
// NEW: екрани, додані в поточній збірці (мітка NEW у списку).
// ─────────────────────────────────────────────────────────────────────────────
import ex_lock_a from "./examples/lock_a.svg";
import ex_lock_b from "./examples/lock_b.svg";
import ex_al_list from "./examples/al_list.svg";
import ex_al_empty from "./examples/al_empty.svg";
import ex_help_sheet from "./examples/help_sheet.svg";
import ex_web_4 from "./examples/web_4.svg";
import ex_web_vpn from "./examples/web_vpn.svg";
import ex_and_review from "./examples/and_review.svg";
import ex_gp_sheet from "./examples/gp_sheet.svg";
import ex_kb_android from "./examples/kb_android.svg";
import ex_notif_vpn from "./examples/notif_vpn.svg";
import ex_upd_android from "./examples/upd_android.svg";
import ex_onb_phone from "./examples/onb_phone.svg";
import ex_perm_denied from "./examples/perm_denied.svg";
import ex_fl_vpn from "./examples/fl_vpn.svg";
// Приклад «як має виглядати» до нотатки: ex — [картинка, підпис]; показується зліва замість кадру Figma
const EX = {
  lock_a: [ex_lock_a, "Варіант A: замок на модулі"], lock_b: [ex_lock_b, "Варіант B: вікно «Доступно в Premium»"],
  al_list: [ex_al_list, "Alerts: список подій"], al_empty: [ex_al_empty, "Alerts: порожньо"], help_sheet: [ex_help_sheet, "Get help, якщо це пошта"],
  web_4: [ex_web_4, "4 фільтри замість 5"], web_vpn: [ex_web_vpn, "Підказка: захист працює з VPN"], and_review: [ex_and_review, "Перевірки для Android"],
  gp_sheet: [ex_gp_sheet, "Вікно оплати Google Play"], kb_android: [ex_kb_android, "Клавіатура Android"], notif_vpn: [ex_notif_vpn, "Сповіщення «VPN увімкнено»"],
  upd_android: [ex_upd_android, "Інструкція оновлення Android"], onb_phone: [ex_onb_phone, "Текст і телефон без «iPhone»"], fl_vpn: [ex_fl_vpn, "Вікно Android перед першим підключенням"],
};
export const KINDS = {
  ok: { label: "Так і буде", color: "#1f9d55" },
  android: { label: "На Android інакше", color: "#7c5cff" },
  design: { label: "Виправити в дизайні", color: "#e8900c" },
  ask: { label: "Уточнити в менеджера", color: "#e5484d" },
};
export const RANK = ["design", "ask", "android", "ok"];

export const NEW_BUILD = 18;
export const NEW = ["web-first", "web-intro", "web-on", "web-4", "web-3", "web-2", "web-1", "web-0"];

const KB = { kind: "android", ex: EX.kb_android, box: [0, 486, 375, 326], text: "Клавіатура в макеті — iOS. На Android з'явиться системна клавіатура телефона (Gboard тощо); її не малюємо, вона буде своя." };
const VPN_SRV = { kind: "android", text: "VPN на Android: протокол ще вирішують (найімовірніше IKEv2, тоді мінімум Android 11). Поки сервер на запити VPN від Android відповідає «немає доступу». Екрани — як у дизайні." };
const FASTEST = (y) => ({ kind: "ask", box: [32, y, 311, 40], text: "«Fastest Connection»: у документації сервера є лише список локацій — хто обирає найшвидший сервер: сервер чи апка?" });
const SPEED = { kind: "ok", box: [16, 488, 343, 72], text: "Швидкість Download / Upload рахує сама апка з трафіку телефона." };
const WEB_VPN = { kind: "ask", ex: EX.web_vpn, text: "За документацією захист від шахрайських сайтів на Android працює всередині VPN і лише коли VPN увімкнений. На екрані цього не видно — як показати (підказка «увімкніть VPN»?) — уточнити." };
const COOKIE = { kind: "design", ex: EX.web_4, box: [32, 449, 311, 40], text: "«Hide Cookie Prompts» на Android неможливий: банер cookie — частина сторінки, фільтр його не сховає. Прибрати рядок і перерахувати шкалу (4 фільтри)." };
const WEB_SET = { kind: "ask", text: "Який набір фільтрів буде на Android, погоджують разом з екранами модуля (так у документації) — чекаємо від менеджера." };
const LOCKED = { kind: "design", ex: [EX.lock_a, EX.lock_b], text: "Немає стану без підписки: VPN, Web Protection, Safe QR Scanner, Storage Cleaner, Breach Check мають бути замкнені (замок + що відбувається після натискання)." };
const ALERTS = { kind: "ask", ex: [EX.al_list, EX.al_empty], box: [140, 728, 96, 48], text: "Вкладка Alerts: екрана немає ні в дизайні, ні в документації сервера. Чи потрібна і що там показувати?" };
const HELP = { kind: "ask", ex: EX.help_sheet, text: "Картка «Need help? Replies in minutes · Get help» (внизу прокрутки): що вона відкриває — чат, пошту чи сторінку?" };
const FACE = (y) => ({ kind: "design", ex: EX.and_review, box: [32, y, 311, 40], text: "Face ID — функція iPhone. На Android — відбиток пальця або розблокування обличчям: інший текст та іконка." });
const IOSUPD = (y) => ({ kind: "design", ex: EX.and_review, box: [32, y, 311, 40], text: "«iOS security update» — для Android: оновлення системи Android." });

export const NOTES = {
  splash: [{ kind: "ok", text: "Як у дизайні." }],
  onb1: [{ kind: "ok", text: "Як у дизайні." }],
  onb2: [
    { kind: "design", ex: EX.onb_phone, box: [32, 535, 311, 72], text: "«Your iPhone, Protected» — на Android без «iPhone» (наприклад «Your phone, Protected»)." },
    { kind: "design", box: [32, 623, 311, 40], text: "«keep your iPhone safe» — замінити iPhone." },
    { kind: "design", box: [70, 120, 235, 380], text: "На ілюстрації телефон із «чубчиком» iPhone — краще нейтральний телефон." },
  ],
  onb3: [{ kind: "ok", text: "Як у дизайні." }],
  onb4: [{ kind: "ok", text: "Як у дизайні." }],
  onb5: [{ kind: "ok", text: "Як у дизайні." }],
  onb6: [
    { kind: "design", ex: EX.onb_phone, box: [32, 623, 311, 40], text: "«on your iPhone» — замінити iPhone." },
    { kind: "design", box: [70, 120, 235, 380], text: "На ілюстрації телефон із «чубчиком» iPhone — краще нейтральний телефон." },
  ],
  paywall: [
    { kind: "android", ex: EX.gp_sheet, box: [24, 697, 327, 52], text: "Покупка — через Google Play (RevenueCat). Ціни й «3 дні за $0.99» беремо з Google Play: підписка app.seciro.android.premium, плани weekly / monthly / yearly, пробна weekly-trial." },
    { kind: "ask", text: "Прийом покупок Google Play на сервері ще в розробці; проєкт RevenueCat для Android і його ключ — від менеджера." },
    { kind: "android", box: [300, 66, 64, 34], text: "Restore — відновлення покупок Google Play (RevenueCat)." },
  ],
  signin: [{ kind: "ok", text: "Вхід кодом з пошти — сервер це вже вміє." }],
  "signin-typing": [KB],
  "signin-filled": [KB],
  offline: [{ kind: "ok", text: "Як у дизайні." }],
  code: [{ kind: "ok", text: "Код приходить листом: 6 цифр, діє 60 хв, новий — не частіше разу на хвилину. Так працює сервер." }],
  "code-typing": [KB],
  "code-filled": [KB],
  verifying: [{ kind: "ok", text: "Як у дизайні." }],
  "code-error": [{ kind: "ok", text: "Невірний код — відповідь сервера (5 спроб)." }],
  "sub-ended": [
    { kind: "design", box: [16, 319, 343, 40], text: "«Renew to keep your iPhone protected» — замінити iPhone." },
    { kind: "ok", text: "Екран вмикає сервер: підписка закінчилась (state: cancelled)." },
  ],
  "no-sub": [{ kind: "ok", text: "Як у дизайні." }],
  dashboard: [
    { kind: "android", box: [32, 429, 311, 40], text: "Було «in Safari» — у прототипі вже Chrome (рішення VELDAN). Дизайнеру внести в макет." },
    ALERTS, HELP, LOCKED,
  ],
  scan: [{ kind: "ok", text: "Перевірка на самому телефоні — робимо самі." }],
  "result-ok": [
    { kind: "design", box: [32, 308, 311, 40], text: "«Protection is on for Safari» — на Android захист працює у VPN; підпис інший." },
    IOSUPD(357), FACE(503),
  ],
  "result-issues": [IOSUPD(357), FACE(542), { kind: "ask", ex: EX.and_review, text: "Список перевірок для Android (замість iOS-пунктів) — погодити з дизайнером і менеджером." }],
  "sheet-safari": [
    { kind: "android", box: [16, 235, 343, 72], text: "Інструкцію Safari ми вже замінили на Chrome (рішення VELDAN)." },
    { kind: "ask", ex: EX.web_vpn, text: "За документацією захист на Android працює всередині VPN, а не в браузері — тоді ця інструкція, мабуть, не потрібна взагалі." },
  ],
  "sheet-update": [
    { kind: "design", ex: EX.upd_android, box: [84, 262, 259, 52], text: "Кроки iOS (Settings → General → Software Update) — потрібна інструкція для Android (Налаштування → Система → Оновлення системи)." },
    { kind: "design", box: [84, 498, 259, 32], text: "Passcode — iOS. На Android крок інший." },
  ],
  "dash-issues": [
    { kind: "android", box: [32, 600, 311, 40], text: "Було «in Safari» — у прототипі вже Chrome. Дизайнеру внести в макет." },
    WEB_VPN, ALERTS, LOCKED,
  ],
  "vpn-first": [VPN_SRV, FASTEST(616), SPEED],
  "vpn-intro": [{ kind: "android", ex: EX.fl_vpn, box: [16, 719, 343, 56], text: "Після «Connect to VPN» Android сам показує системне вікно «Запит на підключення» — у Figma його немає, у прототипі показано макет." }, VPN_SRV],
  "vpn-off": [VPN_SRV, FASTEST(616), SPEED],
  "vpn-loc": [{ kind: "ok", text: "Список локацій приходить із сервера." }, FASTEST(188)],
  "vpn-connecting": [VPN_SRV, FASTEST(616)],
  "vpn-connected": [VPN_SRV, SPEED, { kind: "android", ex: EX.notif_vpn, text: "Поки VPN увімкнений, Android обов'язково тримає сповіщення «VPN увімкнено» у шторці." }],
  "vpn-loc-ny": [{ kind: "ok", text: "Список локацій приходить із сервера." }, FASTEST(188)],
  "vpn-loc-paris": [{ kind: "ok", text: "Список локацій приходить із сервера." }, FASTEST(188)],
  "vpn-connecting-paris": [VPN_SRV],
  "vpn-connected-paris": [VPN_SRV, SPEED],
  "web-first": [WEB_VPN, COOKIE, WEB_SET],
  "web-intro": [{ kind: "ok", text: "Знайомство при першому вході — як у дизайні." }, WEB_VPN],
  "web-on": [WEB_VPN, COOKIE, WEB_SET],
  "web-4": [WEB_VPN, COOKIE],
  "web-3": [WEB_VPN, COOKIE],
  "web-2": [WEB_VPN, COOKIE],
  "web-1": [WEB_VPN, COOKIE],
  "web-0": [WEB_VPN, COOKIE],
};
// приклад(и) нотатки списком
export const examplesOf = (n) => !n.ex ? [] : Array.isArray(n.ex[0]) ? n.ex : [n.ex];
export const statusOf = (id) => { const ks = (NOTES[id] || []).map((n) => n.kind); return RANK.find((k) => ks.includes(k)) || "ok"; };
