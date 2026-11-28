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
import ex_st_dupes from "./examples/st_dupes.svg";
import ex_perm_limited from "./examples/perm_limited.svg";
import ex_lang_pick from "./examples/lang_pick.svg";
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
  st_dupes: [ex_st_dupes, "Дублікати групами"], perm_limited: [ex_perm_limited, "Доступ лише до частини фото"], lang_pick: [ex_lang_pick, "Системний вибір мови"], perm_denied: [ex_perm_denied, "Людина відмовила в доступі до фото"],
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

// ── Як писати нотатки (VELDAN 28.11): Android вирішуємо ми (kind android — рішення + чому);
//    дизайнеру — дослівний текст EN на заміну; Віталію — лише продукт / сервер, A/B/C з прикладами.
const KB = { kind: "android", ex: EX.kb_android, box: [0, 486, 375, 326], text: "Тут намальована клавіатура iPhone. На Android з'явиться клавіатура самого телефона (Gboard, Samsung тощо) — малювати її не треба, дизайнеру нічого робити." };
const VPN_SRV = { kind: "android", text: "VPN для Android ще налаштовують на сервері. Екрани робимо як у дизайні, підключення запрацює, коли сервер буде готовий.\nПротокол, найімовірніше, IKEv2 — тоді апка працюватиме на Android 11 і новіших (зараз у проєкті мінімум Android 10). Вирішувати нічого не треба, лише знати." };
const FASTEST = (y) => ({ kind: "ask", box: [32, y, 311, 40], text: "Віталію (сервер): хто вибирає «Fastest Connection»?\nСама апка виміряти швидкість не може — у списку локацій від сервера немає адрес серверів, лише назви міст.\nA — сервер у відповіді позначає найшвидшу локацію для людини (наприклад, для Києва — Варшава).\nB — беремо першу локацію зі списку сервера (сервер сам ставить її першою).\nC — як в iOS (напишіть, як там)." });
const SPEED = { kind: "ok", box: [16, 488, 343, 72], text: "Швидкість Download / Upload рахує сама апка з трафіку телефона — як у дизайні." };
// Web Protection: вирішили ми — через VPN (див. sheet-safari)
const WEB_HOW = { kind: "android", text: "Як буде на Android (вирішили ми): захист сайтів працює через VPN Seciro.\nРозширень для браузерів на Android немає, тому інструкції «увімкніть Seciro у браузері» не буде. Коли VPN увімкнений, сервер Seciro не пускає на небезпечні адреси — у Chrome, інших браузерах і апках одразу.\nЧому VPN, а не DNS у налаштуваннях: людині нічого не треба налаштовувати руками, і шлях не залежить від Samsung чи Xiaomi." };
const WEB_VPN = { kind: "design", ex: EX.web_vpn, text: "Дизайнеру: коли VPN вимкнений, захист сайтів не працює — на екрані Web Protection потрібна плашка зверху (див. приклад):\nTitle: «Protection works inside the VPN»\nText: «VPN is off right now»\nButton: «Turn on VPN» — відкриває екран VPN." };
const COOKIE = { kind: "design", ex: EX.web_4, box: [32, 449, 311, 40], text: "Дизайнеру: рядок «Hide Cookie Prompts» прибрати.\nЧому (вирішили ми): вікна про cookie — частина самого сайту, VPN їх не бачить і сховати не може.\nЛишаються 4 фільтри: General Ads · Stop Tracking · Block Adult Content · Block Gambling.\nШкала — на 4 рівні: «Security Level 4/4», «3/4» … «0/4» (див. приклад)." };
const WEB_SRV = { kind: "ask", text: "Віталію (сервер): VPN має вміти вмикати й вимикати кожен з 4 фільтрів окремо для людини (наприклад, реклама — так, азартні ігри — ні). Це вже є в плані сервера? A — так · B — ні, лише все разом (тоді на екрані лишається один перемикач)." };
const QR_WHO = { kind: "ask", text: "Віталію (сервер): хто вирішує, чи посилання з QR безпечне?\nПриклад: людина сканує QR у кафе → https://bit.ly/3xYz. Апка питає сервер — і показує «безпечне», «підозріле» або «заблоковано».\nA — сервер Seciro (своя база).\nB — сторонній сервіс (наприклад, Google Safe Browsing), але через сервер Seciro.\nC — як в iOS (напишіть, як там).\nУ документації сервера такого запиту поки немає." };
const QR_MISSING = { kind: "design", ex: [EX.qr_safe, EX.qr_warn, EX.qr_text, EX.qr_cam, EX.qr_err], text: "Дизайнеру: бракує 5 екранів результату. Текст (EN):\nA · Safe — «This link is safe» · «We found no threats» · адреса · «Open» · «Scan again».\nB · Suspicious — «Be careful» · «This link looks suspicious» · адреса · «Back» · «Open anyway».\nC · Not a link (Wi-Fi, text, phone, contact) — тип зверху («Wi-Fi network»), вміст, кнопка «Copy password» / «Copy text» / «Copy number» · «Scan again».\nD · No camera access — «No camera access» · «Allow the camera in settings to scan QR codes» · «Open settings» · «From gallery».\nE · Check failed — «Couldn't check the link» · «Check your connection and try again» · «Try again» · «Open without checking».\nВирішили ми: Wi-Fi лише копіюємо пароль, автопідключення не робимо — Android питає на це окремий дозвіл." };
const LOCKED = { kind: "design", ex: [EX.lock_a, EX.lock_b], text: "Дизайнеру: як виглядає головна без підписки. Безкоштовні — Protection Review і Password Generator, решта 5 (VPN, Web Protection, Safe QR Scanner, Storage Cleaner, Breach Check) — із замком. Вигляд замка — ваш (приклад A).\nЩо після натискання (вирішили ми, так у документації сервера): одразу пейвол. Хрестик пейвола повертає на головну із замками. Вікна «Available in Premium» (приклад B) не робимо — менше кроків до оплати." };
const ALERTS = { kind: "ask", ex: [EX.al_list, EX.al_empty], box: [140, 728, 96, 48], text: "Віталію (продукт): вкладка Alerts у нижньому меню — для чого вона?\nA — список подій (див. приклад): «VPN disconnected · 2 min», «Leak found · yesterday», «Protection off · 3 d»; порожній стан «All quiet».\nB — прибираємо вкладку, у меню лишаються Home і Settings.\nЕкрана Alerts у Figma немає." };
const HELP = { kind: "ask", ex: EX.help_sheet, text: "Віталію (продукт): куди ведуть «Get help» (на головній) і «Contact» (у Settings)?\nA — пошта: вікно «Need help? · Write to us and we'll reply soon · support@… · Write» (див. приклад). Яка адреса?\nB — чат усередині апки (який сервіс?).\nC — сторінка підтримки на сайті (яке посилання?).\nD — прибрати обидві кнопки." };
const PUSH = { kind: "ask", ex: EX.fl_notif, text: "Віталію (продукт): чи будуть пуш-сповіщення (наприклад, «Your VPN is off» або «New leak found for your email»)?\nA — так, із сервера (через Firebase; хто створює проєкт Firebase?).\nB — лише нагадування самої апки, без сервера.\nC — без пушів.\nЯкщо A або B: на Android 13+ телефон спершу питає дозвіл — коли саме питати, вирішимо ми (після першої перевірки, не на старті)." };
const IPHONE_ART = (y = 120) => ({ kind: "design", ex: EX.onb_phone, box: [70, y, 235, 380], text: "Дизайнеру: на ілюстрації телефон із «чубчиком» iPhone. Для Android — нейтральний телефон без вирізу (див. приклад)." });
// ── Protection Review на Android: по нотатці на рядок; рішення наші ──
const RV_WEB = (y) => ({ kind: "design", box: y ? [32, y, 311, 40] : undefined, text: "Рядок 1 · Web Protection.\nНа iPhone захист — розширення Safari, тому «Protection is on for Safari». На Android захист працює через VPN Seciro (вирішили ми — див. Sheet · Safari setup).\nЗелене: VPN підключений і Web Protection увімкнений. Червоне — інакше; натискання відкриває екран VPN.\nДизайнеру, текст (EN): зелений підпис «Protection is on via VPN», червоний — «Turn on VPN to block dangerous sites»." });
const IOSUPD = (y) => ({ kind: "design", ex: EX.and_review, box: [32, y, 311, 40], text: "Рядок 2 · Software Update.\niPhone знає, чи вийшла новіша iOS. Android такого апкам не каже — лише дату останнього оновлення безпеки (наприклад, «1 жовтня 2026»).\nПравило (вирішили ми): зелене, якщо оновленню безпеки до 4 місяців. Чому 4: Samsung і Xiaomi на багатьох моделях оновлюють раз на квартал — хто оновлюється вчасно, не побачить червоного.\nНатискання на червоне — інструкція «як оновити» (Sheet · Software Update).\nДизайнеру, текст (EN): зелений «Your Android is up to date», червоний «Last security update: June 2026» (замість «An iOS security update is available» — апка не знає, чи вийшло нове)." });
const RV_LOCK = (y) => ({ kind: "design", box: y ? [32, y, 311, 40] : undefined, text: "Рядок 3 · Passcode → блокування екрана.\nНа Android це PIN, графічний ключ або пароль — апка точно знає, чи він є.\nЧервоне — натискання відкриває системний екран «Блокування екрана».\nДизайнеру, текст (EN): назва «Screen lock», зелений «Your phone has a screen lock», червоний «Set a PIN, pattern or password»." });
const FACE = (y) => ({ kind: "design", ex: EX.and_review, box: [32, y, 311, 40], text: "Рядок 4 · Face ID → біометрія.\nFace ID — назва Apple. Розблокування обличчям на Android є, але апка дізнається лише «біометрія налаштована чи ні» — не відбиток це чи обличчя. Тому рядок загальний.\nТелефон без сканера (буває в дешевих) — рядок ховаємо, перевірок 3: «3 of 3 passed» (вирішили ми: людина нічого не може «увімкнути», вічна червона позначка лише дратує).\nДизайнеру, текст (EN): назва «Biometric unlock», зелений «Fingerprint or face unlock is set up», червоний «Add a fingerprint or face unlock»; іконка нейтральна (не Face ID)." });
const RV_HOW = { kind: "android", text: "Як працює екран на Android (вирішили ми): усі перевірки робить сам телефон, на сервер нічого не йде. Перевіряємо при відкритті й щоразу, коли людина повертається з налаштувань — галочка стає зеленою одразу, без повторного сканування.\nНабір той самий, що на iPhone, з андроїдними назвами: Web Protection · Android update · Screen lock · Biometric unlock (див. приклад)." };

export const NOTES = {
  splash: [{ kind: "ok", text: "Як у дизайні." }],
  onb1: [{ kind: "ok", text: "Як у дизайні." }],
  onb2: [
    { kind: "design", ex: EX.onb_phone, box: [32, 535, 311, 72], text: "Дизайнеру, заголовок (EN): «Your iPhone, Protected» → «Your Phone, Protected»." },
    { kind: "design", box: [32, 623, 311, 40], text: "Дизайнеру, текст (EN): «Run a full security scan to keep your iPhone safe, private, and running at its best.» → «Run a full security scan to keep your phone safe, private, and running at its best.»" },
    IPHONE_ART(),
  ],
  onb3: [{ kind: "ok", text: "Як у дизайні." }],
  onb4: [{ kind: "ok", text: "Як у дизайні." }],
  onb5: [{ kind: "ok", text: "Як у дизайні." }],
  onb6: [
    { kind: "design", ex: EX.onb_phone, box: [32, 623, 311, 40], text: "Дизайнеру, текст (EN): «…free up space on your iPhone.» → «Remove duplicate photos and large videos to free up space on your phone.»" },
    IPHONE_ART(),
  ],
  paywall: [
    { kind: "android", ex: EX.gp_sheet, box: [24, 697, 327, 52], text: "Оплата — через Google Play (вирішено). Ціни підтягуються з Google Play у валюті людини: в Україні буде «₴…», у США «$…». Цифри з макета ($0.99, $29.99) у коді не пишемо.\nПісля натискання кнопки Google Play сам показує своє вікно оплати знизу (див. приклад) — його не малюємо." },
    { kind: "ask", text: "Віталію (дія, не рішення): коли Android-проєкт у RevenueCat буде готовий — надішліть Владу ключ SDK (починається з «goog_»). Продукти в Play Console створює Влад. Прийом покупок Google Play на сервері поки «в розробці»." },
    { kind: "android", box: [300, 66, 64, 34], text: "Restore — відновлює покупки Google Play на цьому акаунті Google. Текст кнопки той самий." },
    { kind: "android", box: [80, 762, 215, 26], text: "Terms of Use і Privacy Policy (вирішили ми): сторінка відкривається поверх апки у вбудованій вкладці браузера (Chrome Custom Tabs). Людина не виходить із Seciro, хрестиком повертається на пейвол. Так само в Settings. Дизайнеру нічого робити." },
    { kind: "ask", text: "Віталію: які адреси Terms of Use і Privacy Policy? Наприклад, seciro.app/terms і seciro.app/privacy. Ті самі сторінки, що в iOS-апці?\nPrivacy Policy потрібна ще й для сторінки апки в Google Play — без неї апку не опублікувати." },
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
    { kind: "design", box: [16, 319, 343, 40], text: "Дизайнеру, текст (EN): «Renew to keep your iPhone protected.» → «Renew to keep your phone protected.»" },
    { kind: "ok", text: "Екран вмикає сервер: підписка закінчилась (state: cancelled)." },
  ],
  "no-sub": [{ kind: "ok", text: "Як у дизайні." }],
  dashboard: [
    { kind: "design", box: [32, 429, 311, 40], text: "Дизайнеру, текст (EN): «Enjoy safer browsing in Safari» → «Enjoy safer browsing in all your apps».\nЧому: на Android захист працює через VPN — одразу в усіх браузерах і апках, не лише в одному (вирішили ми)." },
    ALERTS, HELP, LOCKED, PUSH,
  ],
  scan: [{ kind: "ok", text: "Перевірка на самому телефоні — робимо самі." }],
  "result-ok": [RV_WEB(308), IOSUPD(357), RV_LOCK(430), FACE(503), RV_HOW],
  "result-issues": [RV_WEB(284), IOSUPD(357), RV_LOCK(469), FACE(542), RV_HOW],
  "sheet-safari": [
    { kind: "design", box: [16, 235, 343, 72], text: "Дизайнеру: цей екран (інструкція «How to activate Seciro protection in Safari») на Android прибрати.\nЧому (вирішили ми): захист працює через VPN — людині нічого вмикати в браузері не треба. Натискання на рядок Web Protection замість інструкції відкриває екран VPN.\nУ прототипі поки показано Chrome — лише щоб екран не був порожнім." },
    WEB_HOW,
  ],
  "sheet-update": [
    { kind: "design", ex: EX.upd_android, box: [84, 262, 259, 52], text: "Рядок 1 — кроки iOS. На Android пишемо:\n«Open Settings and type “update” in the search bar».\nЧому через пошук (вирішили ми): шлях до оновлення в кожного виробника свій — Pixel: System → Software updates; Samsung: Software update; Xiaomi: About phone. А пошук у Налаштуваннях є на всіх Android." },
    { kind: "design", box: [39, 322, 287, 57], text: "Підказка «If you see two update options, choose the newer one» — про iPhone (Apple іноді пропонує дві версії iOS). На Android такого немає. Замінити на:\n«Can\'t find it? On Samsung: Settings → Software update. On Xiaomi: Settings → About phone.»" },
    { kind: "design", box: [84, 412, 259, 36], text: "Рядок 2 → «Tap Software update or System update»." },
    { kind: "design", box: [84, 498, 259, 32], text: "Рядок 3 → «Tap Check for updates, then Download and install».\nКрок «Enter your passcode» прибираємо: Android перед оновленням пароль зазвичай не питає." },
    { kind: "design", box: [84, 546, 259, 40], text: "Рядок 4 → «Tap Restart now and wait for the update to finish»." },
    { kind: "design", box: [16, 623, 343, 42], text: "Текст унизу → «Connect your phone to a charger and Wi-Fi first. Your phone will restart and can\'t be used while the update installs.»" },
    { kind: "android", ex: EX.upd_android, box: [16, 688, 343, 52], text: "Кнопки (вирішили ми): головна «Open Settings» відкриває Налаштування Android, друга «Got it» закриває вікно.\nНа iPhone апка не може відкрити налаштування, тому в Figma лише Got it. На Android може — людині менше шукати.\nЯк виглядають дві кнопки — вирішує дизайнер." },
    { kind: "android", text: "Увесь текст екрана для Android (EN) — щоб дизайнер скопіював:\nSoftware Update\nUpdates patch vulnerabilities, helping protect your phone, your data, and your identity from theft.\n1. Open Settings and type “update” in the search bar\nⓘ Can\'t find it? On Samsung: Settings → Software update. On Xiaomi: Settings → About phone.\n2. Tap Software update or System update\n3. Tap Check for updates, then Download and install\n4. Tap Restart now and wait for the update to finish\nConnect your phone to a charger and Wi-Fi first. Your phone will restart and can\'t be used while the update installs.\n[Open Settings]  [Got it]" },
  ],
  "dash-issues": [
    { kind: "design", box: [32, 600, 311, 40], text: "Дизайнеру, текст (EN): «Enjoy safer browsing in Safari» → «Enjoy safer browsing in all your apps»." },
    ALERTS, LOCKED,
  ],
  "vpn-first": [VPN_SRV, FASTEST(616), SPEED],
  "vpn-intro": [{ kind: "android", ex: EX.fl_vpn, box: [16, 719, 343, 56], text: "Після «Connect to VPN» Android один раз сам показує системне вікно «Запит на підключення» (див. приклад). Його малює Android — дизайнеру нічого робити. Якщо людина натисне «Скасувати», лишаємось на цьому екрані, VPN вимкнений." }, VPN_SRV],
  "vpn-off": [VPN_SRV, FASTEST(616), SPEED],
  "vpn-loc": [{ kind: "ok", text: "Список локацій приходить із сервера." }, FASTEST(188)],
  "vpn-connecting": [VPN_SRV, FASTEST(616)],
  "vpn-connected": [VPN_SRV, SPEED, { kind: "android", ex: EX.notif_vpn, text: "Поки VPN увімкнений, Android обов'язково тримає сповіщення «VPN увімкнено» у шторці (і значок ключа вгорі). Прибрати його не можна — так вимагає Android. Текст сповіщення (EN, вирішили ми): «Seciro VPN is on · Tap to open»." }],
  "vpn-loc-ny": [{ kind: "ok", text: "Список локацій приходить із сервера." }, FASTEST(188)],
  "vpn-loc-paris": [{ kind: "ok", text: "Список локацій приходить із сервера." }, FASTEST(188)],
  "vpn-connecting-paris": [VPN_SRV],
  "vpn-connected-paris": [VPN_SRV, SPEED],
  "web-first": [WEB_HOW, WEB_VPN, COOKIE, WEB_SRV],
  "web-intro": [{ kind: "ok", text: "Знайомство при першому вході — як у дизайні." }, WEB_HOW],
  "web-on": [WEB_VPN, COOKIE, WEB_SRV],
  "web-4": [WEB_VPN, COOKIE],
  "web-3": [WEB_VPN, COOKIE],
  "web-2": [WEB_VPN, COOKIE],
  "web-1": [WEB_VPN, COOKIE],
  "web-0": [WEB_VPN, COOKIE],
  "qr-first": [{ kind: "ok", text: "Як у дизайні. Камера й розпізнавання QR — на самому телефоні." }],
  "qr-intro": [{ kind: "android", ex: EX.fl_cam, box: [16, 719, 343, 56], text: "Після «Scan a QR Code» Android сам спитає дозвіл на камеру (див. приклад) — системне вікно, не малюємо. Якщо людина відмовить — показуємо екран «No camera access» (екран D у завданні дизайнеру на Access blocked)." }],
  "qr-scan": [{ kind: "ok", text: "Як у дизайні. У прототипі: дотик до рамки — сканувати ще раз." },
    { kind: "android", box: [189, 598, 138, 40], text: "«From gallery» відкриває системний вибір фото Android. Дозволу на фото для цього не треба — людина сама вибирає одне фото." }, QR_WHO],
  "qr-check": [QR_WHO],
  "qr-blocked": [{ kind: "ok", text: "Як у дизайні: «Back to Safety», «Continue to Site», копіювання посилання." }, QR_MISSING, QR_WHO],
  "qr-copied": [{ kind: "android", text: "«Copied!» (вирішили ми): на Android 13 і новіших система сама показує внизу «Скопійовано» — там свій «Copied!» не показуємо, щоб не було двох підказок. На Android 12 і старіших — показуємо, як у дизайні. Дизайнеру нічого робити." }],
  // ── ряд 7: Storage Cleaner ──
  "st-first": [{ kind: "ok", text: "Як у дизайні: при першому вході — знайомство «Scan Storage»." }],
  "st-intro": [{ kind: "android", ex: EX.fl_photo, box: [16, 719, 343, 56], text: "Після «Scan Storage» Android сам спитає доступ до фото й відео (див. приклад) — системне вікно, не малюємо. Якщо людина відмовить — екран «No photo access» з кнопкою «Open settings» (див. завдання дизайнеру на st-ready)." }],
  "st-scan": [{ kind: "ok", text: "Сканування на самому телефоні, файли нікуди не вивантажуються — як у дизайні." }],
  "st-ready": [{ kind: "ok", text: "Розміри й кількість файлів — з пам'яті телефона." },
    { kind: "ok", box: [32, 272, 311, 56], text: "«Clean up storage» відкриває вибір файлів — як у Figma (далі «Clean selected» → очищення → «Cleanup Complete!»)." },
    { kind: "design", ex: [EX.perm_limited, EX.perm_denied], text: "Дизайнеру: два стани доступу до фото, яких немає в Figma.\nA · Android 14+: людина може дати доступ лише до частини фото. Плашка зверху (EN): «We can see only some photos» · «Allow all to find duplicates» · кнопка «Allow all».\nB · Людина відмовила: «No photo access» · «Without it Storage Cleaner can't find duplicates. Turn it on in settings.» · «Open settings» · «Not now»." }],
  "st-shots": [{ kind: "design", ex: EX.st_dupes, box: [128, 70, 120, 28], text: "Дизайнеру: екран є лише для Screenshots. Для інших категорій (вирішили ми, як шукаємо — див. README):\n• Duplicate Photos — однакові фото групами. У кожній групі найкраще фото позначене «Best» і лишається, решта вже вибрані на видалення. Підпис групи «Keep the best, delete the rest», кнопка «Delete 3 photos» (див. приклад).\n• Similar Photos — так само групами, але фото схожі (серія знімків). «Best» — найчіткіше.\n• Similar Videos — сітка як у Screenshots, на мініатюрі тривалість «0:42»." },
    { kind: "android", box: [16, 719, 343, 56], text: "Перед видаленням Android сам показує вікно «Дозволити Seciro видалити ці 3 фото?» — системне, не малюємо. Видалені фото потрапляють у кошик галереї на 30 днів." }],
  "st-clean": [{ kind: "ok", text: "Як у дизайні." }],
  "st-done": [{ kind: "ok", text: "Як у дизайні. «4.5 GB» і відсотки — справжні дані з телефона." }],
  // ── ряд 8: Breach Check ──
  "br-first": [{ kind: "ok", text: "Як у дизайні: при першому вході — знайомство «Check My Email»." }],
  "br-intro": [{ kind: "ok", text: "Як у дизайні." }],
  "br-idle": [{ kind: "android", box: [32, 439, 311, 40], text: "Якщо людина увійшла, поле одразу заповнене поштою акаунта — так у документації сервера." }],
  "br-typing": [KB],
  "br-filled": [KB, { kind: "ok", text: "Кнопка стає активною, коли пошта введена правильно." }],
  "br-check": [{ kind: "ok", text: "Перевіряє сервер Seciro. До 5 різних адрес на добу." },
    { kind: "design", ex: [EX.br_limit, EX.br_error, EX.br_need], text: "Дизайнеру: сервер може відповісти ще трьома способами — екранів немає. Текст (EN):\nA · Ліміт — «Daily limit reached» · «You can check up to 5 addresses a day. Try again tomorrow.» · «Got it».\nB · Помилка — «Couldn't check» · «This doesn't mean there are no leaks. Please try again.» · «Try again». Важливо: не схоже на «витоків немає».\nC · Без підписки — «Subscription needed» · «Breach Check is part of Premium» · «See plans» · «Sign in»." }],
  "br-none": [{ kind: "ok", text: "Як у дизайні." }],
  "br-found": [{ kind: "ok", text: "Список витоків приходить із сервера: назва, рік і які дані витекли." },
    { kind: "android", ex: EX.br_logo, box: [32, 288, 40, 40], text: "Логотип (вирішили ми): сервер дає лише назву й сайт компанії (linkedin.com), логотипа немає. Тому поки — кружечок з першою літерою назви («L») (див. приклад). Значки з чужих сайтів не тягнемо: тоді чужий сервіс бачить, які витоки в людини." },
    { kind: "ask", text: "Віталію (сервер): додасте в відповідь посилання на логотип компанії? A — так (тоді покажемо справжній логотип) · B — ні, лишаємо першу літеру." }],
  "br-detail": [{ kind: "ask", box: [156, 170, 191, 64], text: "Віталію (сервер): рядок «This breach exposed data from 700M accounts» — сервер не дає кількість акаунтів.\nA — сервер додає це число (наприклад, LinkedIn 2021 → «700M»).\nB — прибираємо рядок." },
    { kind: "ask", box: [96, 346, 250, 36], text: "Віталію (продукт): що роблять «Change Password» і «Beware of Scam Calls»?\nA — відкривають сторінку зміни пароля на сайті компанії (наприклад, linkedin.com/psettings).\nB — коротка порада у вікні («Change your LinkedIn password and turn on two-step login»).\nC — це просто мітки, не кнопки." },
    { kind: "ok", text: "Картки ризиків будуємо з того, які дані витекли (сервер дає список)." }],
  // ── ряд 9: Password Generator ──
  "pw-first": [{ kind: "ok", text: "Як у дизайні: при першому вході — знайомство «Generate a Password»." }],
  "pw-intro": [{ kind: "ok", text: "Як у дизайні." }],
  "pw-on": [{ kind: "ok", text: "Пароль створюється на телефоні й нікуди не відправляється." },
    { kind: "ok", text: "Стани Strong / Medium / Weak — з дизайн-системи; залежать від довжини й перемикачів." },
    { kind: "ask", ex: EX.pw_range, box: [32, 256, 311, 40], text: "Віталію (продукт): від скількох до скількох символів пароль? На повзунку в дизайні чисел немає.\nA — 8–20 (так стоїть повзунок у Figma; зараз у прототипі)\nB — 8–32\nC — 12–64\nD — як в iOS (скільки там?)" }],
  "pw-copied": [{ kind: "android", text: "«Copied!» (вирішили ми): на Android 13 і новіших система сама показує «Скопійовано» — свій «Copied!» там не показуємо. На Android 12 і старіших — як у дизайні. Дизайнеру нічого робити." }],
  // ── ряд 10: Settings і My Account ──
  "settings": [{ kind: "android", box: [32, 216, 311, 44], text: "Language (вирішили ми): свого екрана мови не робимо. Пункт відкриває системний вибір мови для апки — на Android 13+ він є в кожного телефона. Якщо мова одна — пункт прибираємо. Дизайнеру малювати нічого." },
    { kind: "ask", text: "Віталію (продукт): якими мовами буде апка? A — лише англійська (пункт Language прибираємо) · B — кілька (якими?)." },
    { kind: "android", box: [32, 308, 311, 113], text: "Terms і Privacy відкриваються поверх апки у вбудованій вкладці браузера (Chrome Custom Tabs) — людина не виходить з Seciro. Адреси — питання Віталію (див. нотатки пейвола)." },
    { kind: "ask", box: [32, 454, 311, 40], text: "Contact — те саме питання, що й «Get help» на головній: пошта, чат, сайт чи прибрати (див. нотатки головної)." },
    { kind: "android", box: [32, 542, 311, 40], text: "Unsubscribe: підписку з Google Play відкриваємо на сторінці підписок Google Play; підписку з сайту — у кабінеті на сайті (так у документації). Текст той самий." },
    ALERTS],
  "account": [{ kind: "android", box: [32, 259, 311, 48], text: "Manage Subscription: підписка з Google Play → сторінка підписок Google Play; підписка з сайту → кабінет на сайті (одноразове посилання)." },
    { kind: "design", ex: EX.acc_states, box: [32, 349, 311, 120], text: "Дизайнеру: рядок «Next Billing» має ще два стани (EN):\nA · Скасована, але оплачена — «Access until · Sep 12, 2026».\nB · Оплата не пройшла — «Payment failed — update your card» (без дати, червоним)." },
    { kind: "android", box: [32, 512, 311, 48], text: "Restore Purchases — відновлює покупки Google Play на цьому акаунті Google." },
    { kind: "design", ex: EX.del_confirm, box: [32, 675, 311, 48], text: "Дизайнеру: перед видаленням акаунта — вікно підтвердження (EN):\n«Delete your account?» · «This does NOT cancel your subscription. Cancel it in Google Play or your account.» · «Delete» (червона) · «Cancel».\nЧому: так вимагає документація сервера." },
    { kind: "ok", text: "Sign Out — повернення на екран входу з порожнім полем (так у документації). Ціна й дата — з відповіді сервера." }],
  "account-guest": [{ kind: "ok", text: "Як у дизайні: Sign In і Restore Purchases." }],
};
// приклад(и) нотатки списком
export const examplesOf = (n) => !n.ex ? [] : Array.isArray(n.ex[0]) ? n.ex : [n.ex];
export const statusOf = (id) => { const ks = (NOTES[id] || []).map((n) => n.kind); return RANK.find((k) => ks.includes(k)) || "ok"; };
