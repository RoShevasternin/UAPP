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
import ex_web_flow from "./examples/web_flow.svg";
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
  web_flow: [ex_web_flow, "Як буде на Android: рядок → екран VPN"], st_dupes: [ex_st_dupes, "Дублікати групами"], perm_limited: [ex_perm_limited, "Доступ лише до частини фото"], lang_pick: [ex_lang_pick, "Системний вибір мови"], perm_denied: [ex_perm_denied, "Людина відмовила в доступі до фото"],
  br_logo: [ex_br_logo, "Без логотипа: перша літера назви"], pw_range: [ex_pw_range, "Межі повзунка: варіанти A, B, C"],
};
import ex_st_update from "./examples/st_update.svg";
import ex_st_pay from "./examples/st_pay.svg";
import ex_st_seat from "./examples/st_seat.svg";
import ex_st_off from "./examples/st_off.svg";
import ex_store from "./examples/store.svg";
import ex_perm_pre from "./examples/perm_pre.svg";
EX.st_update = [ex_st_update, "Оновіть апку"]; EX.st_pay = [ex_st_pay, "Оплата не пройшла"]; EX.st_seat = [ex_st_seat, "Пристрій вийшов"];
EX.st_off = [ex_st_off, "Немає інтернету понад добу"]; EX.store = [ex_store, "Google Play: банер і скріншоти"]; EX.perm_pre = [ex_perm_pre, "Екран перед вікном дозволу"];

export const KINDS = {
  ok: { label: "Так і буде", color: "#1f9d55" },
  android: { label: "Як буде на Android (робить Влад)", color: "#7c5cff" },
  design: { label: "Завдання дизайнеру", color: "#e8900c" },
  ask: { label: "Питання Віталію", color: "#e5484d" },
};
export const RANK = ["design", "ask", "android", "ok"];
export const NEW_BUILD = 21;
export const NEW = ["st-first", "st-intro", "st-scan", "st-ready", "st-shots", "st-clean", "st-done", "br-first", "br-intro", "br-idle", "br-typing", "br-filled", "br-check", "br-none", "br-found", "br-detail",
  "pw-first", "pw-intro", "pw-on", "pw-copied", "settings", "account", "account-guest"];
export const NEW_LABEL = "23 екрани: Storage Cleaner, Breach Check, Password Generator, Settings і My Account";

// ─────────────────────────────────────────────────────────────────────────────
// Нотатки (третя редакція, 28.11.2026 — для дзвінка з Віталієм). Формат картки — marks.jsx (NoteBody):
//   t — що це за місце · now — що в макеті · why — чому інакше / чому питаємо · fix — що зробити / пропонуємо
//   opts — варіанти [літера, текст, пропонуємо?] · copy — текст для макета (EN) · at / row / box — рамка на екрані
// Правила (VELDAN): просто й зрозуміло; факт — як факт; вибір — Віталію з пропозицією й прикладом; чого точно знаємо — не питаємо.
// ─────────────────────────────────────────────────────────────────────────────

// ── спільні ──
const KB = { kind: "android", box: [0, 486, 375, 326], t: "Клавіатура", text: "Тут намальована клавіатура iPhone. На Android з'явиться клавіатура самого телефона (Gboard, Samsung тощо). Дизайнеру нічого робити." };
const VPN_SRV = { kind: "android", t: "VPN ще налаштовують на сервері", text: "Екрани робимо як у дизайні. Підключення запрацює, коли сервер відкриє VPN для Android (зараз відповідає «немає доступу»).\nЙмовірний протокол — IKEv2: тоді апка працюватиме на Android 11 і новіших." };
const FASTEST = { kind: "ask", at: "Connects to the fastest available server", row: true, t: "Хто вибирає «Fastest Connection»?",
  why: "Сама апка швидкість виміряти не може: сервер дає лише список міст, без адрес серверів.",
  opts: [["A", "Беремо першу локацію зі списку — сервер і так задає порядок і може ставити першою найкращу. Нічого міняти не треба.", true],
    ["B", "Сервер окремо каже, яка локація найшвидша для цієї людини (наприклад, для Києва — Варшава). Потрібна невелика зміна на сервері."],
    ["C", "Як в iOS-апці — скажіть, як там."]] };
const SPEED = { kind: "ok", at: "Download", row: true, t: "Швидкість Download / Upload", text: "Рахує сама апка з трафіку телефона — як у дизайні." };
const VPN_CASE = { kind: "design", at: "Vpn is off", t: "Опечатка: «Vpn is off»", now: "На першому вході й у знайомстві — «Vpn is off», на інших екранах — «VPN is off».", fix: "Виправити регістр (і в iOS-макеті теж).", copy: ["VPN is off"] };

const WEB_HOW = { kind: "android", t: "Як працює захист сайтів на Android",
  text: "У браузерах Android немає розширень, тому Seciro не може вбудуватись у Chrome, як у Safari на iPhone.\nЗахист працює через VPN Seciro (так у документації сервера): поки VPN увімкнений, небезпечні сайти не відкриваються в жодному браузері чи апці. VPN вимкнений — захисту сайтів немає." };
const WEB_VPN = { kind: "design", ex: EX.web_vpn, at: "Security Level", row: 170, t: "Плашка «VPN вимкнений»",
  why: "Захист сайтів працює лише з увімкненим VPN. Людина має це бачити, інакше думатиме, що захищена.",
  fix: "Плашка вгорі екрана Web Protection з кнопкою, що відкриває VPN (див. приклад). Показуємо, лише коли VPN вимкнений.",
  copy: [["Заголовок", "Protection works inside the VPN"], ["Текст", "VPN is off right now"], ["Кнопка", "Turn on VPN"]] };
const COOKIE = { kind: "design", ex: EX.web_4, at: "Hide Cookie Prompts", row: true, t: "Прибрати фільтр «Hide Cookie Prompts»",
  now: "5 фільтрів, серед них «Hide Cookie Prompts — Remove obtrusive banners».",
  why: "Вікна про cookie — частина самої сторінки. VPN бачить лише адресу сайту, а не те, що на сторінці, тож сховати їх не може.",
  fix: "Прибрати рядок. Лишаються 4 фільтри, шкала рівня — з 4 (див. приклад).",
  copy: [["Шкала, приклад", "Security Level 4/4"]] };
const WEB_DUP = { kind: "design", at: "Stop Tracking", row: true, t: "Підпис «Stop Tracking» — копія з General Ads",
  now: "Stop Tracking — «Block intrusive ads». Такий самий підпис, як у General Ads.", fix: "Свій підпис для трекінгу (і в iOS-макеті теж).",
  copy: [["Підпис, пропозиція", "Block trackers that follow you across sites"]] };
const WEB_SRV = { kind: "ask", t: "Чи вміє сервер вмикати фільтри окремо?",
  why: "На екрані 4 перемикачі. Щоб вони працювали, VPN-сервер має вмикати кожен фільтр окремо для людини. Приклад: рекламу блокуємо, а сайти ставок — ні.",
  opts: [["A", "Так, кожен окремо — екран як у дизайні.", true], ["B", "Лише все разом — тоді на екрані один перемикач, дизайнер перемалює."]],
  fix: "Якщо не знаєте — перешліть серверній команді." };

const QR_WHO = { kind: "ask", t: "Хто перевіряє посилання з QR?",
  why: "Після сканування апка показує «безпечне», «підозріле» або «заблоковано». Для цього вона має спитати сервер, а в документації такого запиту немає. Без нього сканер працюватиме, а перевірка — ні.",
  opts: [["A", "Як в iOS-апці — той самий запит, просто дайте його Владу.", true], ["B", "Сервер Seciro, нова перевірка — потрібен новий запит на сервері."],
    ["C", "Сторонній сервіс (наприклад, Google Web Risk), але через сервер Seciro — ключ сервісу не можна класти в апку."]] };
const QR_MISSING = { kind: "design", ex: [EX.qr_safe, EX.qr_warn, EX.qr_text, EX.qr_cam, EX.qr_err], at: "Access blocked", t: "Ще 5 екранів результату QR",
  now: "Є лише «Access blocked» — небезпечне посилання.",
  why: "Більшість посилань безпечні, QR буває не посиланням (Wi-Fi, текст), камеру можуть заборонити, а перевірка — не відповісти. На все це екранів немає.",
  fix: "Намалювати 5 станів у стилі «Access blocked» (приклади A–E).",
  copy: [["A · Безпечне посилання", "This link is safe\nWe found no threats\nOpen\nScan again"],
    ["B · Підозріле посилання", "Be careful\nThis link looks suspicious\nBack\nOpen anyway"],
    ["C · У коді не посилання", "Wi-Fi network\nCopy password\nScan again"],
    ["D · Немає доступу до камери", "No camera access\nAllow the camera in settings to scan QR codes\nOpen settings\nFrom gallery"],
    ["E · Перевірка не вдалася", "Couldn't check the link\nCheck your connection and try again\nTry again\nOpen without checking"]] };
const QR_WIFI = { kind: "ask", ex: EX.qr_text, t: "QR з Wi-Fi: лише копіювати пароль?",
  why: "Приклад: QR у кафе з мережею «Cafe_Guest» і паролем. Що даємо людині?",
  opts: [["A", "Лише «Copy password» — людина вставляє пароль у налаштуваннях Wi-Fi. Просто, без дозволів.", true],
    ["B", "Ще й «Connect» — Android покаже своє вікно «Підключитися до Cafe_Guest?» і підключиться сам. Зручніше, на екрані ще одна кнопка."]] };
const COPIED = { kind: "android", at: "Copied!", t: "Підказка «Copied!»",
  text: "На Android 13 і новіших система сама показує внизу підказку «Скопійовано». Google радить апкам у такому разі свою не показувати — інакше дві однакові підказки одночасно.\nТож: на Android 12 і старіших — «Copied!», як у дизайні; на 13+ — лише системна. Дизайнеру нічого робити." };

const LOCKED = { kind: "design", ex: EX.lock_a, at: ["Stay private on public Wi-Fi", "Your email in known leaks?"], row: true, t: "Головна без підписки",
  now: "Головна намальована лише з підпискою — усі модулі відкриті.",
  why: "Без підписки працюють лише Protection Review і Password Generator (так у документації). VPN, Web Protection, Safe QR Scanner, Storage Cleaner і Breach Check закриті.",
  fix: "Намалювати головну, де ці 5 модулів із замком. Вигляд замка — ваш (див. приклад)." };
const LOCK_Q = { kind: "ask", ex: [EX.lock_a, EX.lock_b], t: "Що буде після натискання на модуль із замком?",
  why: "У документації: пейвол показується не частіше разу на день, а його хрестик веде на головну «із замками». Що буде після натискання на замок — не сказано.",
  opts: [["A", "Одразу пейвол — найменше кроків до оплати. Правило «раз на день» тут не діє.", true],
    ["B", "Коротке вікно «Available in Premium» з кнопками «See plans» і «Not now» (приклад B)."]] };
const ALERTS = { kind: "ask", ex: [EX.al_list, EX.al_empty], at: "Alerts", t: "Вкладка Alerts — що в ній?",
  why: "У нижньому меню є Alerts, але екрана для неї немає ні в дизайні, ні в документації.",
  opts: [["A", "Прибрати вкладку — у меню лишаються Home і Settings. Повернемо, коли буде що показувати.", true],
    ["B", "Список подій: «VPN disconnected · 2 min», «Leak found · yesterday»; порожньо — «All quiet» (приклади)."]] };
const HELP = { kind: "ask", ex: EX.help_sheet, at: "Get help", row: true, t: "Куди ведуть «Get help» і «Contact»?",
  why: "Кнопки є на головній і в Settings, а що вони відкривають — ніде не сказано. Підпис «Replies in minutes» натякає на чат.",
  opts: [["A", "Пошта підтримки: відкриваємо лист на адресу (див. приклад). Скажіть адресу.", true], ["B", "Чат підтримки в апці — скажіть, який сервіс."],
    ["C", "Сторінка підтримки на сайті — дайте посилання."], ["D", "Прибрати обидві кнопки."]] };
const PUSH = { kind: "ask", ex: EX.fl_notif, t: "Чи будуть пуш-сповіщення?",
  why: "Від цього залежить, чи питати в людини дозвіл на сповіщення і чи потрібен Firebase. Android 13+ питає дозвіл окремим вікном (приклад).",
  opts: [["A", "Лише нагадування самої апки, без сервера: «Your VPN is off», «Run a protection check».", true],
    ["B", "Ще й із сервера: «New leak found for your email». Потрібен Firebase-проєкт для Android — хто його створює?"], ["C", "Без пушів."]],
  fix: "Дозвіл питаємо не на старті, а коли сповіщення справді знадобиться — наприклад, після першої перевірки." };
const WEB_SUB = { kind: "design", at: "Enjoy safer browsing in Safari", t: "Підпис Web Protection: без Safari",
  now: "Enjoy safer browsing in Safari", why: "На Android захист працює через VPN — у всіх браузерах і апках, а не лише в Safari.",
  copy: ["Enjoy safer browsing in all your apps"] };

// ── Protection Review: що в макеті → чому інакше → що зробити ──
const RV_WEB = (at) => ({ kind: "design", at, row: true, t: "Рядок «Web Protection»",
  now: "Зелений підпис — «Protection is on for Safari».",
  why: "На iPhone Seciro — розширення Safari, звідси «for Safari». На Android захист працює через VPN, одразу в усіх браузерах і апках — назва браузера була б неправдою.\nЧервоний підпис «Turn on protection for safer browsing» підходить і для Android.",
  fix: "Змінити зелений підпис. Натискання на червоний рядок відкриватиме екран VPN (на iPhone — інструкцію Safari).",
  copy: [["Зелений підпис", "Protection is on via VPN"]] });
const RV_UPD = (at) => ({ kind: "design", ex: EX.and_review, at, row: true, t: "Рядок «Software Update»",
  now: "Зелений — «Your iOS is up to date», червоний — «An iOS security update is available».",
  why: "«iOS» — система iPhone. До того ж Android не каже апці, чи вийшло нове оновлення: апка знає лише дату останнього (наприклад, «1 червня 2026»). Тож «оновлення доступне» написати не можна.",
  fix: "Замінити підписи. Червоний текст залежить від відповіді Віталія (питання нижче) — нижче варіант для пропозиції A.",
  copy: [["Зелений", "Your Android is up to date"], ["Червоний", "Last security update: June 2026"]] });
const RV_LOCK = (at) => ({ kind: "design", at, row: true, t: "Рядок «Passcode» → «Screen lock»",
  now: "«Passcode» · «Your device has a passcode set».",
  why: "«Passcode» — слово з iPhone (там це цифровий код). На Android це «Screen lock», і це не лише цифри: PIN, графічний ключ або пароль — так написано в налаштуваннях кожного Android. Саму перевірку апка робить точно, змінюються лише слова.",
  fix: "Замінити назву й підпис. Червоного стану в Figma немає — додати (як у рядків на екрані «2 issues»). Іконка ключа підходить.",
  copy: [["Назва", "Screen lock"], ["Зелений", "Your phone has a screen lock"], ["Червоний", "Set a PIN, pattern or password"]] });
const RV_FACE = (at) => ({ kind: "design", ex: EX.and_review, at, row: true, t: "Рядок «Face ID» → «Biometric unlock»",
  now: "«Face ID» · «Face ID is set up on your device», іконка Face ID.",
  why: "Face ID — назва Apple. Android-телефони відмикаються відбитком, обличчям або обома, а апка бачить лише «біометрія налаштована: так / ні» — що саме, Android не каже. Тож «Fingerprint is set up» може бути неправдою.",
  fix: "Загальна назва й підписи, нейтральна іконка (не значок Face ID). Залежить від відповіді Віталія — нижче варіант для пропозиції A.",
  copy: [["Назва", "Biometric unlock"], ["Зелений", "Fingerprint or face unlock is set up"], ["Червоний", "Add a fingerprint or face unlock"]] });
const RV_HOW = { kind: "android", t: "Як працює перевірка", text: "Усі 4 перевірки робить сам телефон, на сервер нічого не йде. Перевіряємо при відкритті й щоразу, коли людина повертається з налаштувань — галочка зеленіє одразу, без нового сканування." };
const RV_UPD_Q = { kind: "ask", t: "Коли рядок «оновлення» зелений?",
  why: "Апка бачить лише дату останнього оновлення безпеки телефона. Чи вийшло новіше — Android не каже. (Бібліотека Google «In-App Updates» оновлює лише саму апку Seciro, не систему.)",
  opts: [["A", "За віком: зелене, якщо оновленню до 4 місяців. Сьогодні 28.11 → оновлення від 1 жовтня ✓, від 1 червня ✕ «Last security update: June 2026». Сервер не потрібен. 4 місяці — бо Samsung і Xiaomi часто оновлюють раз на квартал.", true],
    ["B", "Сервер щомісяця каже, яке оновлення Android найсвіжіше; апка порівнює: «Your phone: June · Latest: November». Мінус: виробник міг ще не випустити його для цієї моделі — людина бачить червоне, а оновитись не може."],
    ["C", "Прибрати рядок на Android — лишається 3 перевірки."]] };
const RV_BIO_Q = { kind: "ask", t: "Що показуємо замість Face ID?",
  why: "Face ID на Android немає. Апка знає лише, чи налаштована біометрія — не відбиток це чи обличчя.",
  opts: [["A", "«Biometric unlock» — правда на будь-якому телефоні.", true],
    ["B", "«Fingerprint» — звичніше слово, але на телефоні лише з розблокуванням обличчям рядок буде червоним, хоча захист є."],
    ["C", "Прибрати рядок — лишається 3 перевірки."]] };
const RV_NOSENS_Q = { kind: "ask", t: "Телефон без сканера відбитка й обличчя",
  why: "У дешевих моделях буває. Людина нічого не може «увімкнути».",
  opts: [["A", "Ховаємо рядок: «3 of 3 passed».", true], ["B", "Сірий рядок «Not available on this phone», у результат не рахується."],
    ["C", "Червоний рядок — людина бачитиме проблему, яку не виправити."]] };

// ── статуси підписки й тестування ──
const STATUS = { kind: "design", ex: [EX.st_update, EX.st_pay, EX.st_seat, EX.st_off], t: "Ще 4 екрани статусу підписки",
  why: "Сервер може відповісти ще так — екранів для цього в Figma немає (є лише «Subscription ended»).",
  fix: "Намалювати 4 екрани в стилі цього (приклади A–D). «Update Seciro» закрити не можна.",
  copy: [["A · Треба оновити апку", "Update Seciro\nThis version no longer works. The update takes a minute.\nUpdate"],
    ["B · Оплата не пройшла", "Payment failed\nUpdate your card to keep protection on\nManage subscription"],
    ["C · Підписка на іншому телефоні", "You're signed out here\nYour subscription is in use on another device\nManage devices\nSign in again"],
    ["D · Немає інтернету понад добу", "No connection\nConnect to the internet to check your subscription\nTry again"]] };
const TESTACC = { kind: "ask", t: "Як перевірити рідкісні екрани до релізу?",
  why: "Екрани «підписка закінчилась», «оплата не пройшла», «оновіть апку» з'являються лише, коли так відповість сервер. На звичайному тестовому акаунті вони самі не виникають.",
  opts: [["A", "Серверна команда на прохання Влада перемикає тестовий акаунт у потрібний стан і повертає назад.", true],
    ["B", "Кілька тестових акаунтів, кожен уже в потрібному стані."],
    ["C", "Ніяк — Влад перевірить на підставних відповідях сервера в тестовій збірці (гірше, але можливо)."]] };

export const NOTES = {
  splash: [{ kind: "ok", text: "Як у дизайні." },
    { kind: "design", ex: EX.store, t: "Google Play: усе для сторінки апки",
      why: "Без цього апку не опублікувати.",
      fix: "Іконка — 512×512 PNG, до 1 МБ, повний квадрат без заокруглень і тіней (Google заокруглює сам).\nБанер — 1024×500, без прозорості; головне по центру, краї можуть обрізатись.\nСкріншоти телефона — 1080×1920, вертикальні, 4–8 штук.\nСкріншоти планшета — такі самі або 1920×1080 (уточнити у Віталія, чи потрібні)." }],
  onb1: [{ kind: "ok", text: "Як у дизайні." }],
  onb2: [
    { kind: "design", at: "Your iPhone, Protected", t: "Заголовок без iPhone", now: "Your iPhone, Protected", why: "Апка для Android.", copy: ["Your Phone, Protected"] },
    { kind: "design", at: "Run a full security scan", t: "Текст без iPhone", now: "…to keep your iPhone safe, private, and running at its best.", why: "Апка для Android.",
      copy: ["Run a full security scan to keep your phone safe, private, and running at its best."] },
    { kind: "design", ex: EX.onb_phone, box: [110, 190, 160, 265], t: "Ілюстрація: телефон без «чубчика»", now: "Намальований iPhone з вирізом угорі.", fix: "Нейтральний телефон без вирізу (див. приклад). Те саме — на останній сторінці онбордингу." },
  ],
  onb3: [{ kind: "ok", text: "Як у дизайні." }],
  onb4: [{ kind: "ok", text: "Як у дизайні." }],
  onb5: [{ kind: "ok", text: "Як у дизайні." }],
  onb6: [
    { kind: "design", at: "Remove duplicate photos and large videos", t: "Текст без iPhone", now: "…to free up space on your iPhone.", why: "Апка для Android.",
      copy: ["Remove duplicate photos and large videos to free up space on your phone."] },
    { kind: "design", ex: EX.onb_phone, box: [110, 190, 160, 265], t: "Ілюстрація: телефон без «чубчика»", now: "Намальований iPhone з вирізом угорі.", fix: "Нейтральний телефон без вирізу (див. приклад)." },
  ],
  paywall: [
    { kind: "android", ex: EX.gp_sheet, at: "Try 3 Days for $0.99", row: true, t: "Оплата — через Google Play",
      text: "Підписку в Android-апці можна продавати лише через Google Play. Ціни приходять з Google Play у валюті людини (в Україні — «₴…»), тому цифри з макета в коді не пишемо.\nПісля натискання Google Play сам показує своє вікно оплати знизу (приклад) — його не малюємо." },
    { kind: "android", box: [300, 66, 64, 34], t: "Restore", text: "Відновлює покупки Google Play на цьому акаунті Google. Текст кнопки той самий." },
    { kind: "android", at: ["Terms of Use", "Privacy Policy"], t: "Terms of Use і Privacy Policy", text: "Відкриваються поверх апки у вбудованій вкладці браузера — людина не виходить із Seciro, хрестиком повертається назад. На вигляд нічого не міняється." },
    { kind: "ask", t: "Які адреси Terms of Use і Privacy Policy?",
      why: "У документації їх немає. Privacy Policy ще й обов'язкова для сторінки апки в Google Play — без неї не опублікувати.",
      fix: "Ті самі сторінки, що в iOS-апці, — просто дайте два посилання (наприклад, seciro.app/terms і seciro.app/privacy)." },
    { kind: "ask", t: "Ключ RevenueCat для Android (дія)",
      why: "Без нього в апці не запрацює покупка. Хто що робить: Влад створює продукти в Play Console і дає Віталію JSON-ключ Google Play; Віталій створює Android-проєкт в окремому акаунті RevenueCat.",
      fix: "Коли проєкт буде готовий — надішліть Владу ключ SDK (починається з «goog_»). Окремо: прийом покупок Google Play на сервері поки «в розробці» — скажіть, коли запрацює." },
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
    { kind: "design", at: "Renew to keep your", t: "Текст без iPhone", now: "Renew to keep your iPhone protected.", why: "Апка для Android.", copy: ["Renew to keep your phone protected."] },
    { kind: "ok", text: "Цей екран вмикає сервер: підписка закінчилась." },
    STATUS, TESTACC,
  ],
  "no-sub": [{ kind: "ok", text: "Як у дизайні." }],
  dashboard: [WEB_SUB, LOCKED, LOCK_Q, ALERTS, HELP, PUSH],
  scan: [{ kind: "ok", text: "Перевірка на самому телефоні — робимо самі." }],
  "result-ok": [RV_WEB("Protection is on for Safari"), RV_UPD("Your iOS is up to date"), RV_LOCK("Your device has a passcode set"), RV_FACE("Face ID is set up on your device"), RV_HOW, RV_UPD_Q, RV_BIO_Q, RV_NOSENS_Q],
  "result-issues": [RV_WEB("Turn on protection for safer browsing"), RV_UPD("An iOS security update is available"), RV_LOCK("Your device has a passcode set"), RV_FACE("Face ID is set up on your device"), RV_HOW, RV_UPD_Q, RV_BIO_Q, RV_NOSENS_Q],
  "sheet-safari": [
    { kind: "design", ex: EX.web_flow, at: "How to activate Seciro protection in Safari", t: "Інструкція Safari на Android не потрібна",
      now: "5 кроків, як увімкнути розширення Seciro в Safari.",
      why: "На iPhone Seciro — розширення Safari, тож людина вмикає його сама за інструкцією. У браузерах Android розширень немає — вмикати в браузері нічого.",
      fix: "Цей екран для Android не малювати. Натискання на рядок Web Protection відкриватиме екран VPN: Connect → сайти захищені в усіх браузерах (див. начерк)." },
    WEB_HOW,
  ],
  "sheet-update": [
    { kind: "design", ex: EX.upd_android, at: "Go to Settings", row: true, t: "Крок 1",
      now: "Go to Settings → General then tap Software Update",
      why: "Шлях до оновлення в кожного виробника свій (Pixel, Samsung, Xiaomi). А пошук у Налаштуваннях є на всіх Android.",
      copy: ["Open Settings and type “update” in the search bar"] },
    { kind: "design", at: "If you see two update options", row: true, t: "Підказка під кроком 1",
      now: "If you see two update options, choose the newer one.",
      why: "Це про iPhone: Apple іноді пропонує дві версії iOS. На Android такого немає. Замість цього підкажемо, де шукати на найпоширеніших телефонах.",
      copy: ["Can't find it? On Samsung: Settings → Software update. On Xiaomi: Settings → About phone."] },
    { kind: "design", at: "Tap Update Now or Download", row: true, t: "Крок 2", now: "Tap Update Now or Download", why: "На Android пункт називається інакше.",
      copy: ["Tap Software update or System update"] },
    { kind: "design", at: "Enter your passcode when prompted", row: true, t: "Крок 3", now: "Enter your passcode when prompted",
      why: "Android перед оновленням пароль зазвичай не питає. Замість цього кроку — перевірка й завантаження.",
      copy: ["Tap Check for updates, then Download and install"] },
    { kind: "design", at: "Then tap Agree", row: true, t: "Крок 4", now: "Then tap Agree and wait for the update to finish", why: "На Android після завантаження телефон пропонує перезапуститись.",
      copy: ["Tap Restart now and wait for the update to finish"] },
    { kind: "design", at: "It is recommended that you connect", t: "Текст унизу", now: "It is recommended that you connect your device to a power source…",
      why: "Те саме, але простіше й про телефон.",
      copy: ["Connect your phone to a charger and Wi-Fi first. Your phone will restart and can't be used while the update installs."] },
    { kind: "ask", ex: EX.upd_android, at: "Got it", row: true, t: "Додати кнопку «Open Settings»?",
      why: "На iPhone апка не може відкрити налаштування оновлення — тому в Figma лише «Got it». На Android апка може відкрити Налаштування телефона.",
      opts: [["A", "Дві кнопки: «Open Settings» (відкриває Налаштування) і «Got it». Людині менше шукати.", true], ["B", "Лише «Got it», як у Figma."]],
      copy: [["Кнопка (якщо A)", "Open Settings"]] },
  ],
  "dash-issues": [{ kind: "ok", text: "Тексти проблем («Website blocking is off», «Email found in 1 leak») підходять і для Android." }, ALERTS, HELP],
  "vpn-first": [VPN_CASE, VPN_SRV, FASTEST, SPEED],
  "vpn-intro": [{ kind: "android", ex: EX.fl_vpn, at: "Connect to VPN", row: true, t: "Системне вікно перед першим підключенням",
    text: "Після «Connect to VPN» Android один раз сам показує вікно «Запит на підключення» (приклад). Його малює Android — дизайнеру нічого робити. Якщо людина натисне «Скасувати», лишаємось на цьому екрані, VPN вимкнений." }, VPN_CASE, VPN_SRV],
  "vpn-off": [VPN_SRV, FASTEST, SPEED],
  "vpn-loc": [{ kind: "ok", text: "Список локацій приходить із сервера." }, FASTEST],
  "vpn-connecting": [VPN_SRV, FASTEST],
  "vpn-connected": [VPN_SRV, SPEED, { kind: "android", ex: EX.notif_vpn, t: "Сповіщення «VPN увімкнено»",
    text: "Поки VPN увімкнений, Android обов'язково тримає сповіщення в шторці й значок ключа вгорі — прибрати не можна. Наш текст сповіщення:",
    copy: ["Seciro VPN is on · Tap to open"] }],
  "vpn-loc-ny": [{ kind: "ok", text: "Список локацій приходить із сервера." }, FASTEST],
  "vpn-loc-paris": [{ kind: "ok", text: "Список локацій приходить із сервера." }, FASTEST],
  "vpn-connecting-paris": [VPN_SRV],
  "vpn-connected-paris": [VPN_SRV, SPEED],
  "web-first": [WEB_HOW, WEB_VPN, COOKIE, WEB_DUP, WEB_SRV],
  "web-intro": [{ kind: "ok", text: "Знайомство при першому вході — як у дизайні." }, WEB_HOW],
  "web-on": [WEB_VPN, COOKIE, WEB_DUP, WEB_SRV],
  "web-4": [WEB_VPN, COOKIE, WEB_DUP],
  "web-3": [WEB_VPN, COOKIE, WEB_DUP],
  "web-2": [WEB_VPN, COOKIE, WEB_DUP],
  "web-1": [WEB_VPN, COOKIE, WEB_DUP],
  "web-0": [WEB_VPN, COOKIE, WEB_DUP],
  "qr-first": [{ kind: "ok", text: "Як у дизайні. Камера й розпізнавання QR — на самому телефоні." }],
  "qr-intro": [{ kind: "android", ex: EX.fl_cam, at: "Scan a QR Code", row: true, t: "Дозвіл на камеру",
    text: "Після «Scan a QR Code» Android сам спитає дозвіл на камеру (приклад) — це системне вікно, не малюємо. Якщо людина відмовить — показуємо екран «No camera access» (екран D у завданні на «Access blocked»)." }],
  "qr-scan": [{ kind: "ok", text: "Як у дизайні. У прототипі: дотик до рамки — сканувати ще раз." },
    { kind: "android", at: "From gallery", row: true, t: "From gallery", text: "Відкриває системний вибір фото Android. Дозвіл на фото для цього не потрібен — людина сама вибирає одне фото." }, QR_WHO],
  "qr-check": [QR_WHO],
  "qr-blocked": [{ kind: "ok", text: "Як у дизайні: «Back to Safety», «Continue to Site», копіювання посилання." }, QR_MISSING, QR_WIFI, QR_WHO],
  "qr-copied": [COPIED],
  // ── ряд 7: Storage Cleaner ──
  "st-first": [{ kind: "ok", text: "Як у дизайні: при першому вході — знайомство «Scan Storage»." }],
  "st-intro": [{ kind: "android", ex: EX.fl_photo, at: "Scan Storage", row: true, t: "Дозвіл на фото й відео",
    text: "Після «Scan Storage» Android сам спитає доступ до фото й відео (приклад) — системне вікно, не малюємо." }],
  "st-scan": [{ kind: "ok", text: "Сканування на самому телефоні, файли нікуди не вивантажуються — як у дизайні." }],
  "st-ready": [{ kind: "ok", text: "Розміри й кількість файлів — справжні дані з пам'яті телефона." },
    { kind: "ok", at: "Clean up storage", row: true, t: "Clean up storage", text: "Відкриває вибір файлів — як у Figma (далі «Clean selected» → очищення → «Cleanup Complete!»)." },
    { kind: "design", ex: [EX.perm_limited, EX.perm_denied, EX.perm_pre], t: "Ще 2 стани доступу до фото (+ 1 за бажанням)",
      why: "Android дає людині три відповіді на запит доступу: «дозволити все», «лише вибрані фото» (з Android 14) або «заборонити». На два останні екранів немає.",
      fix: "A — плашка вгорі, коли видно лише частину фото. B — екран, коли доступу немає. C (за бажанням) — короткий екран «навіщо» перед системним вікном.",
      copy: [["A · Частина фото", "We can see only some photos\nAllow all to find duplicates\nAllow all"],
        ["B · Доступу немає", "No photo access\nWithout it Storage Cleaner can't find duplicates. Turn it on in settings.\nOpen settings\nNot now"],
        ["C · Перед вікном (за бажанням)", "Photo access\nWe'll find duplicates and free up space. Photos never leave your phone.\nContinue\nNot now"]] }],
  "st-shots": [{ kind: "design", ex: EX.st_dupes, at: "Screenshots", t: "Екрани інших категорій",
      now: "Намальований лише Screenshots. Duplicate Photos, Similar Photos і Similar Videos у прототипі — той самий екран з іншою назвою.",
      why: "Дублікати й схожі фото зручніше показувати групами: видно, що лишається, а що видаляється.",
      fix: "Duplicate Photos і Similar Photos — групами: найкраще фото з позначкою «Best» лишається, решта вже вибрані на видалення (приклад; залежить від відповіді Віталія нижче).\nSimilar Videos — сітка як у Screenshots, на мініатюрі тривалість.",
      copy: [["Позначка", "Best"], ["Підпис групи", "Keep the best, delete the rest"], ["Кнопка, приклад", "Delete 3 photos"], ["Тривалість на мініатюрі, приклад", "0:42"]] },
    { kind: "ask", ex: EX.st_dupes, t: "Як показувати дублікати фото?",
      why: "Від цього залежить екран і скільки натискань треба людині.",
      opts: [["A", "Групами: найкраще фото лишається, решта вже вибрані — людина натискає «Delete 3 photos» (приклад).", true],
        ["B", "Парами «оригінал — копія», людина вибирає сама."], ["C", "Як в iOS-апці — скажіть, як там."]] },
    { kind: "android", at: "Clean selected", row: true, t: "Підтвердження видалення",
      text: "Перед видаленням Android сам показує вікно «Дозволити Seciro видалити ці 3 фото?» — системне, не малюємо. Видалені фото лежать у кошику галереї ще 30 днів." }],
  "st-clean": [{ kind: "ok", text: "Як у дизайні." }],
  "st-done": [{ kind: "ok", text: "Як у дизайні. «4.5 GB» і відсотки — справжні дані з телефона." }],
  // ── ряд 8: Breach Check ──
  "br-first": [{ kind: "ok", text: "Як у дизайні: при першому вході — знайомство «Check My Email»." }],
  "br-intro": [{ kind: "ok", text: "Як у дизайні." }],
  "br-idle": [{ kind: "android", at: "name@example.com", t: "Поле пошти", text: "Якщо людина увійшла, поле одразу заповнене поштою її акаунта — так у документації сервера." }],
  "br-typing": [KB],
  "br-filled": [KB, { kind: "ok", text: "Кнопка стає активною, коли пошта введена правильно." }],
  "br-check": [{ kind: "ok", text: "Перевіряє сервер Seciro: до 5 різних адрес на добу." },
    { kind: "design", ex: [EX.br_limit, EX.br_error, EX.br_need], t: "Ще 3 відповіді сервера — екранів немає",
      why: "Сервер може відповісти не лише «витоків немає / знайдено», а й ще трьома способами.",
      fix: "Намалювати 3 екрани (приклади A–C). Важливо: помилка не має бути схожою на «витоків немає».",
      copy: [["A · Ліміт на сьогодні", "Daily limit reached\nYou can check up to 5 addresses a day. Try again tomorrow.\nGot it"],
        ["B · Перевірка не вдалася", "Couldn't check\nThis doesn't mean there are no leaks. Please try again.\nTry again"],
        ["C · Потрібна підписка або вхід", "Subscription needed\nBreach Check is part of Premium\nSee plans\nSign in"]] }],
  "br-none": [{ kind: "ok", text: "Як у дизайні." }],
  "br-found": [{ kind: "ok", text: "Список витоків приходить із сервера: назва, рік і які дані витекли." },
    { kind: "ask", ex: EX.br_logo, at: "LinkedIn — 2021", grow: [52, 6, 0, 6], t: "Звідки брати логотипи компаній?",
      why: "Сервер дає лише назву й сайт (LinkedIn, linkedin.com), логотипа немає.",
      opts: [["A", "Поки — кружечок з першою літерою «L» (приклад). Якщо сервер колись дасть логотипи — покажемо їх.", true],
        ["B", "Сервер одразу додає посилання на логотип."],
        ["C", "Апка бере значок сайту в стороннього сервісу. Мінус: чужий сервіс бачить, які витоки в людини."]] }],
  "br-detail": [{ kind: "ask", at: "This breach exposed data from 700M accounts.", t: "Рядок «…data from 700M accounts»",
      why: "Кількість акаунтів у витоку сервер не дає.",
      opts: [["A", "Прибрати рядок.", true], ["B", "Сервер додає це число (наприклад, LinkedIn 2021 → «700M»)."]] },
    { kind: "ask", at: ["Change Password", "Beware of Scam Calls"], t: "Що роблять «Change Password» і «Beware of Scam Calls»?",
      why: "У дизайні це кнопки, але куди вони ведуть — ніде не сказано.",
      opts: [["A", "Це просто мітки-поради, не кнопки — текст поруч уже каже, що робити.", true],
        ["B", "Відкривають сторінку на сайті компанії (наприклад, зміна пароля LinkedIn)."],
        ["C", "Відкривають коротку пораду у вікні — текст дасте ви."]] },
    { kind: "ok", text: "Картки ризиків будуємо з того, які дані витекли (сервер дає список)." }],
  // ── ряд 9: Password Generator ──
  "pw-first": [{ kind: "ok", text: "Як у дизайні: при першому вході — знайомство «Generate a Password»." }],
  "pw-intro": [{ kind: "ok", text: "Як у дизайні." }],
  "pw-on": [{ kind: "ok", text: "Пароль створюється на телефоні й нікуди не відправляється. Strong / Medium / Weak — з дизайн-системи." },
    { kind: "ask", ex: EX.pw_range, at: "Length · 16", row: true, grow: [0, 0, 0, 36], t: "Від скількох до скількох символів?",
      why: "На повзунку довжини в дизайні немає чисел на краях.",
      opts: [["A", "8–32: вистачає для будь-якого сайту.", true], ["B", "8–20 — так стоїть повзунок у Figma (зараз у прототипі)."], ["C", "12–64."], ["D", "Як в iOS-апці."]] }],
  "pw-copied": [COPIED],
  // ── ряд 10: Settings і My Account ──
  "settings": [{ kind: "ask", ex: EX.lang_pick, at: "Language", row: true, t: "Мови апки й пункт Language",
      why: "Пункт Language є, а екрана вибору мови немає. Що робити з пунктом, залежить від кількості мов.",
      opts: [["A", "Лише англійська — пункт прибираємо.", true],
        ["B", "Кілька мов — пункт відкриває системний вибір мови для апки (є в Android 13+), свій екран не потрібен. Які мови й хто дасть переклади?"],
        ["C", "Кілька мов і свій екран у Settings — дизайнер малює."]] },
    { kind: "android", at: ["Terms", "Privacy"], row: true, t: "Terms і Privacy", text: "Відкриваються поверх апки у вбудованій вкладці браузера — людина не виходить із Seciro. Адреси — питання на пейволі." },
    { kind: "ask", at: "Contact", row: true, t: "Contact — те саме питання, що «Get help»", text: "Вирішуємо разом із «Get help» на головній: пошта, чат, сайт чи прибрати." },
    { kind: "android", at: "Unsubscribe", row: true, t: "Unsubscribe", text: "Підписку з Google Play відкриваємо на сторінці підписок Google Play; підписку з сайту — у кабінеті на сайті (так у документації). Текст той самий." },
    ALERTS],
  "account": [{ kind: "android", at: "Manage Subscription", row: true, t: "Manage Subscription", text: "Підписка з Google Play → сторінка підписок Google Play; підписка з сайту → кабінет на сайті (одноразове посилання)." },
    { kind: "design", ex: EX.acc_states, at: ["Next Billing", "Sep 12, 2026"], row: true, t: "Рядок «Next Billing»: ще 2 стани",
      now: "Лише «Next Billing · Sep 12, 2026».",
      why: "Сервер повідомляє ще два стани підписки: скасована, але оплачена до дати; і оплата не пройшла.",
      copy: [["Скасована, але оплачена", "Access until"], ["Оплата не пройшла (без дати, червоним)", "Payment failed — update your card"]] },
    { kind: "android", at: "Restore Purchases", row: true, t: "Restore Purchases", text: "Відновлює покупки Google Play на цьому акаунті Google." },
    { kind: "design", ex: EX.del_confirm, at: "Delete Account", row: true, t: "Підтвердження перед видаленням акаунта",
      why: "За документацією, видалення акаунта не скасовує підписку — людину треба попередити. Екрана підтвердження немає.",
      copy: [["Заголовок", "Delete your account?"], ["Текст", "This does NOT cancel your subscription. Cancel it in Google Play or your account."], ["Кнопки", "Delete\nCancel"]] },
    { kind: "ok", text: "Sign Out — повернення на екран входу з порожнім полем (так у документації). Ціна й дата — з відповіді сервера." }],
  "account-guest": [{ kind: "ok", text: "Як у дизайні: Sign In і Restore Purchases." }],
};
// приклад(и) нотатки списком
export const examplesOf = (n) => !n.ex ? [] : Array.isArray(n.ex[0]) ? n.ex : [n.ex];
export const statusOf = (id) => { const ks = (NOTES[id] || []).map((n) => n.kind); return RANK.find((k) => ks.includes(k)) || "ok"; };
