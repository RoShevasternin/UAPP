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
import ex_qr_safe from "./examples/qr_safe.svg";
import ex_qr_warn from "./examples/qr_warn.svg";
import ex_qr_text from "./examples/qr_text.svg";
import ex_qr_cam from "./examples/qr_cam.svg";
import ex_qr_err from "./examples/qr_err.svg";
import ex_fl_cam from "./examples/fl_cam.svg";
import ex_br_limit from "./examples/br_limit.svg";
import ex_br_error from "./examples/br_error.svg";
import ex_br_need from "./examples/br_need.svg";
import ex_acc_states from "./examples/acc_states.svg";
import ex_del_confirm from "./examples/del_confirm.svg";
import ex_fl_photo from "./examples/fl_photo.svg";
import ex_fl_notif from "./examples/fl_notif.svg";
import ex_br_logo from "./examples/br_logo.svg";
import ex_pw_range from "./examples/pw_range.svg";
// Приклад «як має виглядати» до нотатки: ex — [картинка, підпис]; показується зліва замість кадру Figma
const EX = {
  lock_a: [ex_lock_a, "Варіант A: замок на модулі"], lock_b: [ex_lock_b, "Варіант B: вікно «Доступно в Premium»"],
  al_list: [ex_al_list, "Alerts: список подій"], al_empty: [ex_al_empty, "Alerts: порожньо"], help_sheet: [ex_help_sheet, "Get help, якщо це пошта"],
  web_4: [ex_web_4, "4 фільтри замість 5"], web_vpn: [ex_web_vpn, "Підказка: захист працює з VPN"], and_review: [ex_and_review, "Перевірки для Android"],
  gp_sheet: [ex_gp_sheet, "Вікно оплати Google Play"], kb_android: [ex_kb_android, "Клавіатура Android"], notif_vpn: [ex_notif_vpn, "Сповіщення «VPN увімкнено»"],
  upd_android: [ex_upd_android, "Інструкція оновлення Android"], onb_phone: [ex_onb_phone, "Текст і телефон без «iPhone»"], fl_vpn: [ex_fl_vpn, "Вікно Android перед першим підключенням"],
  qr_safe: [ex_qr_safe, "Безпечне посилання"], qr_warn: [ex_qr_warn, "Підозріле посилання"], qr_text: [ex_qr_text, "У коді не посилання (Wi-Fi, текст)"],
  qr_cam: [ex_qr_cam, "Немає доступу до камери"], qr_err: [ex_qr_err, "Перевірка не вдалася"], fl_cam: [ex_fl_cam, "Вікно Android: дозвіл на камеру"],
  br_limit: [ex_br_limit, "Ліміт перевірок на сьогодні"], br_error: [ex_br_error, "Перевірка не вдалася"], br_need: [ex_br_need, "Потрібна підписка або вхід"],
  acc_states: [ex_acc_states, "Рядок підписки в різних станах"], del_confirm: [ex_del_confirm, "Попередження перед видаленням акаунта"],
  fl_photo: [ex_fl_photo, "Вікно Android: доступ до фото"], fl_notif: [ex_fl_notif, "Вікно Android: дозвіл на сповіщення"],
  br_logo: [ex_br_logo, "Без логотипа: перша літера назви"], pw_range: [ex_pw_range, "Межі повзунка: варіанти A, B, C"],
};
export const KINDS = {
  ok: { label: "Так і буде", color: "#1f9d55" },
  android: { label: "На Android інакше (робить Влад)", color: "#7c5cff" },
  design: { label: "Завдання дизайнеру", color: "#e8900c" },
  ask: { label: "Питання Віталію (менеджер)", color: "#e5484d" },
};
export const RANK = ["design", "ask", "android", "ok"];

export const NEW_BUILD = 21;
export const NEW = ["st-first", "st-intro", "st-scan", "st-ready", "st-shots", "st-clean", "st-done", "br-first", "br-intro", "br-idle", "br-typing", "br-filled", "br-check", "br-none", "br-found", "br-detail",
  "pw-first", "pw-intro", "pw-on", "pw-copied", "settings", "account", "account-guest"];
export const NEW_LABEL = "23 екрани: Storage Cleaner, Breach Check, Password Generator, Settings і My Account";

const KB = { kind: "android", ex: EX.kb_android, box: [0, 486, 375, 326], text: "Тут намальована клавіатура iPhone. На Android буде звичайна клавіатура телефона — її малювати не треба." };
const VPN_SRV = { kind: "android", text: "VPN для Android ще налаштовують на сервері. Екрани робимо як у дизайні, а підключення запрацює, коли сервер буде готовий." };
const FASTEST = (y) => ({ kind: "ask", box: [32, y, 311, 40], text: "Віталію: «Fastest Connection» — хто вибирає найшвидший сервер? A — сервер, B — перша локація зі списку, C — як в iOS. Сама апка виміряти не може: адрес серверів у списку немає." });
const SPEED = { kind: "ok", box: [16, 488, 343, 72], text: "Швидкість Download / Upload рахує сама апка з трафіку телефона." };
const WEB_VPN = { kind: "ask", ex: EX.web_vpn, text: "На Android захист сайтів працює тільки коли увімкнений VPN. Віталію: показувати на цьому екрані підказку «Увімкніть VPN»? (див. приклад)" };
const COOKIE = { kind: "design", ex: EX.web_4, box: [32, 449, 311, 40], text: "«Hide Cookie Prompts» на Android зробити неможливо. Дизайнеру: прибрати цей рядок (див. приклад з 4 фільтрами)." };
const WEB_SET = { kind: "ask", text: "Віталію: які саме фільтри лишаються на Android? Цей список ще не затверджений." };
const QR_WHO = { kind: "ask", text: "Віталію: хто перевіряє посилання? A — сервер Seciro, B — сторонній сервіс (через сервер), C — як в iOS. У документації сервера такого запиту немає." };
const QR_MISSING = { kind: "design", ex: [EX.qr_safe, EX.qr_warn, EX.qr_text, EX.qr_cam, EX.qr_err], text: "Дизайнеру: немає екранів для безпечного посилання, підозрілого, коду без посилання (Wi-Fi, текст), «немає доступу до камери» і помилки перевірки. Див. приклади A–E." };
const LOCKED = { kind: "design", ex: [EX.lock_a, EX.lock_b], text: "Дизайнеру: намалювати, як виглядає головна без підписки — 5 модулів із замком — і що буде після натискання. Див. приклади A і B." };
const ALERTS = { kind: "ask", ex: [EX.al_list, EX.al_empty], box: [140, 728, 96, 48], text: "Віталію: вкладка Alerts потрібна? Що в ній показувати? Екрана для неї немає (див. приклади)." };
const HELP = { kind: "ask", ex: EX.help_sheet, text: "Віталію: кнопка «Get help» (внизу головної) відкриває чат, пошту чи сайт?" };
// ── Protection Review на Android: по нотатці на кожен рядок (VELDAN 28.11: «розписати детальніше») ──
const RV_WEB = (y) => ({ kind: "design", box: y ? [32, y, 311, 40] : undefined, text: "Рядок 1 · Web Protection.\nНа iPhone захист сайтів — це розширення Safari, тому підпис «Protection is on for Safari» (у прототипі тимчасово Chrome).\nНа Android розширень для браузерів немає. Як саме вмикається захист — питання Віталію на екрані «Sheet · Safari setup» (A через VPN / B Chrome / C весь телефон).\nЯкщо A (VPN): зелена галочка — VPN підключений і Web Protection увімкнений; інакше червоне, натискання веде на екран VPN.\nДизайнеру: підпис без «Safari», наприклад «Protection is on via VPN» (якщо A) або «Protection is on for Chrome» (якщо B)." });
const IOSUPD = (y) => ({ kind: "design", ex: EX.and_review, box: [32, y, 311, 40], text: "Рядок 2 · Software Update.\niPhone знає, чи вийшла новіша iOS. Android такого апкам не дає: жодна апка не може спитати «чи є оновлення».\nЩо апка знає точно: версію Android і дату останнього оновлення безпеки (наприклад, «1 жовтня 2026»).\nПропонуємо: зелене, якщо оновленню безпеки не більше 3 місяців. Інакше — червоне, натискання відкриває інструкцію «як оновити».\nДизайнеру: замість «Your iOS is up to date» — «Your Android is up to date» або «Security update: Oct 2026»." });
const RV_LOCK = (y) => ({ kind: "design", box: y ? [32, y, 311, 40] : undefined, text: "Рядок 3 · Passcode → блокування екрана.\nНа Android це PIN, графічний ключ або пароль. Апка точно знає, чи він є.\nЯкщо немає — червоне, натискання відкриває системний екран «Блокування екрана».\nДизайнеру: назва «Screen lock», підпис «Your phone has a screen lock»." });
const FACE = (y) => ({ kind: "design", ex: EX.and_review, box: [32, y, 311, 40], text: "Рядок 4 · Face ID → біометрія.\nFace ID — назва Apple, на Android її немає. Розблокування обличчям на Android є, але:\n• на більшості телефонів воно працює через звичайну фронтальну камеру, і Android вважає його «слабким» (банки його не приймають);\n• апка дізнається лише «чи налаштована біометрія», а не що саме — відбиток чи обличчя.\nТому рядок загальний: «Biometric unlock» · «Fingerprint or face unlock is set up», нейтральна іконка (не Face ID).\nНатискання, якщо не налаштовано, — системний екран додавання відбитка / обличчя." });
const RV_HOW = { kind: "android", text: "Як працює екран на Android: усі 4 перевірки робить сам телефон, на сервер нічого не йде. Перевіряємо при відкритті й щоразу, коли людина повертається з налаштувань — галочка стає зеленою одразу, без повторного сканування." };
const RV_Q1 = { kind: "ask", ex: EX.and_review, text: "Віталію, питання 1 з 3 — які перевірки показуємо на Android?\nНа iPhone їх 4: Web Protection, оновлення iOS, Passcode, Face ID. Пропонуємо для Android ті самі 4, лише з андроїдними назвами (див. приклад):\n1) Web Protection — увімкнений захист через VPN;\n2) оновлення Android;\n3) блокування екрана (PIN, ключ або пароль);\n4) біометрія (відбиток або обличчя).\nA — так, ці 4 · B — інший набір (напишіть, який)." };
const RV_Q2 = { kind: "ask", text: "Віталію, питання 2 з 3 — коли рядок «оновлення» зелений?\niPhone сам каже апці: «вийшла нова iOS — оновіться». Android так не вміє. Він знає лише дату останнього оновлення безпеки (Налаштування → Про телефон → «Оновлення безпеки: 1 жовтня 2026»). Тож правило треба вибрати нам.\nПриклад: сьогодні 28.11.2026.\nA — до 3 місяців: оновлення від 1 жовтня → зелене ✓; від 1 червня → червоне «Оновіть систему».\nB — до 6 місяців: обидва приклади зелені; червоне — лише старші за 1 червня.\nC — прибрати цей рядок на Android." };
const RV_Q3 = { kind: "ask", text: "Віталію, питання 3 з 3 — телефон узагалі без біометрії.\nУ дешевих телефонах буває, що немає ні сканера відбитка, ні розблокування обличчям. Людина нічого не може «увімкнути».\nA — рядок ховаємо, перевірок 3: «3 of 3 passed».\nB — рядок сірий «Not available on this phone» і не рахується в результат.\nC — червоний «Set up biometrics» (не радимо: виправити неможливо, людина бачить вічну проблему)." };
const RV_ASK = [RV_Q1, RV_Q2, RV_Q3];

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
    { kind: "android", ex: EX.gp_sheet, box: [24, 697, 327, 52], text: "Оплата буде через Google Play: ціни й «3 дні за $0.99» підтягуються звідти. Вікно оплати малює сам Google Play (див. приклад)." },
    { kind: "ask", text: "Віталію: коли Android-проєкт у RevenueCat буде готовий — надішліть Владу SDK-ключ (продукти в Play Console створює Влад). Прийом покупок Google Play на сервері поки «в розробці»." },
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
    { kind: "ask", ex: EX.fl_notif, text: "Віталію: чи будуть пуш-сповіщення? Якщо так, Android 13+ спершу питає дозвіл (див. приклад, де це вікно може з'являтися)." },
  ],
  scan: [{ kind: "ok", text: "Перевірка на самому телефоні — робимо самі." }],
  "result-ok": [RV_WEB(308), IOSUPD(357), RV_LOCK(430), FACE(503), RV_HOW, ...RV_ASK],
  "result-issues": [RV_WEB(284), IOSUPD(357), RV_LOCK(469), FACE(542), RV_HOW, ...RV_ASK],
  "sheet-safari": [
    { kind: "android", box: [16, 235, 343, 72], text: "Інструкцію Safari в прототипі тимчасово замінили на Chrome (рішення VELDAN) — поки Віталій не вибере спосіб нижче." },
    { kind: "android", text: "Як буде на Android — що вже точно, а що ні.\n✔ Точно: розширення Seciro для браузера, як у Safari, на Android зробити неможливо — Chrome і інші браузери на телефоні розширень не підтримують. Тому інструкція «увімкніть Seciro у браузері» в такому вигляді не працює.\n✔ Точно: захистити сайти можна лише через DNS — сервер Seciro не дає відкрити небезпечну адресу. Підключити DNS можна трьома способами (A, B, C — у питанні нижче).\n◌ Не вирішено: який спосіб — вирішує Віталій. Від цього залежить, чи лишається цей екран і який у ньому текст.\nНаша порада — A: людина нічого не налаштовує руками, захищені всі апки, а VPN у Seciro і так є." },
    { kind: "ask", ex: EX.web_vpn, text: "Віталію: як на Android людина вмикає захист сайтів? Від цього залежить, чи потрібен цей екран.\nНа iPhone: Seciro — це розширення Safari, людина вмикає його сама за інструкцією (ліворуч).\nНа Android розширень для браузерів немає. Є три способи:\nA — через VPN Seciro (радимо). Людина просто вмикає VPN, і небезпечні сайти блокуються в усіх браузерах і апках. Інструкція не потрібна — екран прибираємо. Мінус: без VPN захисту немає (див. приклад).\nB — «Захищений DNS» у Chrome (як у прототипі праворуч): Chrome → ⋮ → Налаштування → Конфіденційність і безпека → Захищений DNS → вставити адресу Seciro. Працює без VPN, але захищає лише Chrome. Потрібна адреса DNS від сервера.\nC — «Приватний DNS» Android: Налаштування → Мережа → Приватний DNS → вписати адресу Seciro. Працює без VPN і захищає весь телефон, але в Samsung і Xiaomi шлях у налаштуваннях інший. Теж потрібна адреса від сервера." },
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
  "qr-first": [{ kind: "ok", text: "Як у дизайні. Камера й розпізнавання QR — на самому телефоні." }],
  "qr-intro": [{ kind: "android", ex: EX.fl_cam, box: [16, 719, 343, 56], text: "Після «Scan a QR Code» Android сам спитає дозвіл на камеру — це системне вікно, його не малюємо." }],
  "qr-scan": [{ kind: "ok", text: "Як у дизайні. У прототипі: дотик до рамки — сканувати ще раз." },
    { kind: "android", box: [189, 598, 138, 40], text: "«From gallery» на Android відкриває системний вибір фото." }, QR_WHO],
  "qr-check": [QR_WHO],
  "qr-blocked": [{ kind: "ok", text: "Як у дизайні: «Back to Safety», «Continue to Site», копіювання посилання." }, QR_MISSING, QR_WHO],
  "qr-copied": [{ kind: "ok", text: "Як у дизайні." }],
  // ── ряд 7: Storage Cleaner ──
  "st-first": [{ kind: "ok", text: "Як у дизайні: при першому вході — знайомство «Scan Storage»." }],
  "st-intro": [{ kind: "android", ex: EX.fl_photo, box: [16, 719, 343, 56], text: "Після «Scan Storage» Android сам спитає доступ до фото й відео — це системне вікно, його не малюємо." }],
  "st-scan": [{ kind: "ok", text: "Сканування на самому телефоні, файли нікуди не вивантажуються — як у дизайні." }],
  "st-ready": [{ kind: "ok", text: "Розміри й кількість файлів — з пам'яті телефона." },
    { kind: "ok", box: [32, 272, 311, 56], text: "«Clean up storage» відкриває вибір файлів — як у Figma (далі «Clean selected» → очищення → «Cleanup Complete!»)." }],
  "st-shots": [{ kind: "design", box: [128, 70, 120, 28], text: "Дизайн є лише для Screenshots. Для Duplicate Photos, Similar Photos і Similar Videos у прототипі той самий екран з іншою назвою. Дизайнеру: чи потрібні їм свої екрани (наприклад, групи схожих фото)?" },
    { kind: "android", box: [16, 719, 343, 56], text: "Перед видаленням Android сам покаже вікно «Дозволити видалити ці фото?» — це системне вікно." }],
  "st-clean": [{ kind: "ok", text: "Як у дизайні." }],
  "st-done": [{ kind: "ok", text: "Як у дизайні. «4.5 GB» і відсотки — справжні дані з телефона." }],
  // ── ряд 8: Breach Check ──
  "br-first": [{ kind: "ok", text: "Як у дизайні: при першому вході — знайомство «Check My Email»." }],
  "br-intro": [{ kind: "ok", text: "Як у дизайні." }],
  "br-idle": [{ kind: "android", box: [32, 439, 311, 40], text: "Якщо людина увійшла, поле одразу заповнене поштою акаунта — так у документації сервера." }],
  "br-typing": [KB],
  "br-filled": [KB, { kind: "ok", text: "Кнопка стає активною, коли пошта введена правильно." }],
  "br-check": [{ kind: "ok", text: "Перевіряє сервер Seciro. До 5 різних адрес на добу." },
    { kind: "design", ex: [EX.br_limit, EX.br_error, EX.br_need], text: "Дизайнеру: немає екранів «ліміт на сьогодні», «перевірка не вдалася» і «потрібна підписка або вхід» — сервер так відповідає. Див. приклади A–C." }],
  "br-none": [{ kind: "ok", text: "Як у дизайні." }],
  "br-found": [{ kind: "ok", text: "Список витоків приходить із сервера: назва, рік і які дані витекли." },
    { kind: "ask", ex: EX.br_logo, box: [32, 288, 40, 40], text: "Віталію: сервер дає про витік лише назву й сайт компанії (linkedin.com). Звідки брати логотип? Варіанти: сервер дає посилання на логотип / апка бере значок сайту / без логотипа (перша літера назви)." }],
  "br-detail": [{ kind: "ask", box: [156, 170, 191, 64], text: "Віталію: рядок «This breach exposed data from 700M accounts» — сервер не дає кількість акаунтів. Додати це число на сервері чи прибрати рядок?" },
    { kind: "ask", box: [96, 346, 250, 36], text: "Віталію: що відбувається після натискання «Change Password» і «Beware of Scam Calls»? Варіанти: відкривається сайт компанії / коротка порада у вікні / це просто мітки, не кнопки." },
    { kind: "ok", text: "Картки ризиків будуємо з того, які дані витекли (сервер дає список)." }],
  // ── ряд 9: Password Generator ──
  "pw-first": [{ kind: "ok", text: "Як у дизайні: при першому вході — знайомство «Generate a Password»." }],
  "pw-intro": [{ kind: "ok", text: "Як у дизайні." }],
  "pw-on": [{ kind: "ok", text: "Пароль створюється на телефоні й нікуди не відправляється." },
    { kind: "ok", text: "Стани Strong / Medium / Weak — з дизайн-системи; залежать від довжини й перемикачів." },
    { kind: "ask", ex: EX.pw_range, box: [32, 256, 311, 40], text: "Віталію: у дизайні повзунок довжини без чисел на краях. Від скількох до скількох символів? Варіанти: 8–20 (так стоїть повзунок у Figma), 8–32, 12–64. У прототипі поки 8–20." }],
  "pw-copied": [{ kind: "android", text: "На Android 13+ система сама показує внизу «Скопійовано». Разом з «Copied!» з дизайну вийде дві підказки — одну, можливо, прибрати." }],
  // ── ряд 10: Settings і My Account ──
  "settings": [{ kind: "design", box: [32, 216, 311, 44], text: "Пункт Language: екрана вибору мови в дизайні немає. Дизайнеру: потрібен такий екран чи прибрати пункт?" },
    { kind: "android", box: [32, 308, 311, 113], text: "Terms і Privacy відкриваються в браузері поверх апки (Custom Tabs)." },
    { kind: "ask", box: [32, 454, 311, 40], text: "Віталію: Contact відкриває пошту, чат чи сайт?" },
    { kind: "android", box: [32, 542, 311, 40], text: "Unsubscribe: підписку з Google Play скасовують на сторінці підписок Google Play, підписку з сайту — у кабінеті на сайті (так у документації)." },
    { kind: "ask", ex: [EX.al_list, EX.al_empty], box: [140, 726, 96, 52], text: "Віталію: вкладка Alerts потрібна? Що в ній показувати? Екрана для неї немає (див. приклади)." }],
  "account": [{ kind: "android", box: [32, 259, 311, 48], text: "Manage Subscription: підписка з Google Play → сторінка підписок Google Play; підписка з сайту → кабінет на сайті (одноразове посилання)." },
    { kind: "design", ex: EX.acc_states, box: [32, 349, 311, 120], text: "Дизайнеру: рядок «Next Billing» має ще два стани — «Access until» (скасована, але оплачена) і «Payment failed — update your card». У дизайні їх немає." },
    { kind: "android", box: [32, 512, 311, 48], text: "Restore Purchases — відновлення покупок Google Play." },
    { kind: "design", ex: EX.del_confirm, box: [32, 675, 311, 48], text: "Дизайнеру: перед Delete Account треба попередити, що підписка не скасовується (так у документації). Екрана підтвердження немає." },
    { kind: "ok", text: "Sign Out — повернення на екран входу з порожнім полем (так у документації). Ціна й дата — з відповіді сервера." }],
  "account-guest": [{ kind: "ok", text: "Як у дизайні: Sign In і Restore Purchases." }],
};
// приклад(и) нотатки списком
export const examplesOf = (n) => !n.ex ? [] : Array.isArray(n.ex[0]) ? n.ex : [n.ex];
export const statusOf = (id) => { const ks = (NOTES[id] || []).map((n) => n.kind); return RANK.find((k) => ks.includes(k)) || "ok"; };
