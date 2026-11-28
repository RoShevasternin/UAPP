# Нейтральні начерки екранів (сірі прямокутники + один синій для головної кнопки) трьома мовами.
from html import escape as E

LANGS = ("ru", "uk", "en")
def T(ru, uk, en): return {"ru": ru, "uk": uk, "en": en}

BG, CARD, LINE, INK, MUT, ACC, DIM = "#f2f3f6", "#ffffff", "#d5d9e0", "#2b303a", "#8a919e", "#3d6fe0", "#c9ced7"
FONT = "font-family=\"Inter, -apple-system, Segoe UI, Roboto, sans-serif\""

def txt(x, y, s, size=15, fill=INK, w=400, anchor="middle"):
    out = []
    for i, line in enumerate(s.split("\n")):
        out.append(f'<text x="{x}" y="{y + i * size * 1.35:.0f}" font-size="{size}" font-weight="{w}" fill="{fill}" text-anchor="{anchor}" {FONT}>{E(line)}</text>')
    return "".join(out)

def phone(body, dim_until=None):
    s = [f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 375 812" width="375" height="812">',
         f'<rect width="375" height="812" rx="36" fill="{BG}"/>',
         f'<rect x="24" y="18" width="40" height="8" rx="4" fill="{DIM}"/><rect x="300" y="18" width="50" height="8" rx="4" fill="{DIM}"/>']
    s += body
    s.append(f'<rect x="137" y="790" width="100" height="5" rx="3" fill="{INK}" opacity=".5"/></svg>')
    return "".join(s)

def icon(y, r=44, sym=""):
    g = f'<circle cx="187.5" cy="{y}" r="{r}" fill="{DIM}"/>'
    if sym == "lock":
        g += f'<rect x="172" y="{y-4}" width="31" height="24" rx="5" fill="#fff"/><path d="M178 {y-4} v-8 a9.5 9.5 0 0 1 19 0 v8" stroke="#fff" stroke-width="5" fill="none"/>'
    elif sym == "check":
        g += f'<path d="M168 {y} l13 13 l26 -26" stroke="#fff" stroke-width="7" fill="none" stroke-linecap="round" stroke-linejoin="round"/>'
    elif sym == "warn":
        g += f'<rect x="183" y="{y-24}" width="9" height="30" rx="4" fill="#fff"/><circle cx="187.5" cy="{y+18}" r="5.5" fill="#fff"/>'
    elif sym == "x":
        g += f'<path d="M171 {y-16} l33 33 M204 {y-16} l-33 33" stroke="#fff" stroke-width="7" stroke-linecap="round"/>'
    elif sym == "wifi":
        g += f'<path d="M163 {y-4} a36 36 0 0 1 49 0 M171 {y+5} a24 24 0 0 1 33 0" stroke="#fff" stroke-width="6" fill="none" stroke-linecap="round"/><circle cx="187.5" cy="{y+17}" r="5" fill="#fff"/>'
    elif sym == "arrow":
        g += f'<path d="M187.5 {y-20} v34 M172 {y} l15.5 15 l15.5 -15" stroke="#fff" stroke-width="7" fill="none" stroke-linecap="round" stroke-linejoin="round"/>'
    elif sym == "card":
        g += f'<rect x="164" y="{y-15}" width="47" height="31" rx="5" fill="#fff"/><rect x="164" y="{y-7}" width="47" height="6" fill="{DIM}"/>'
    elif sym == "phone":
        g += f'<rect x="174" y="{y-22}" width="27" height="44" rx="5" fill="#fff"/>'
    elif sym == "bell":
        g += f'<path d="M172 {y+10} h31 l-4 -6 v-12 a11.5 11.5 0 0 0 -23 0 v12 z" fill="#fff"/><circle cx="187.5" cy="{y+15}" r="4" fill="#fff"/>'
    elif sym == "photo":
        g += f'<rect x="166" y="{y-16}" width="43" height="32" rx="5" fill="#fff"/><path d="M170 {y+12} l12 -13 l9 9 l6 -6 l9 10 z" fill="{DIM}"/>'
    elif sym == "cam":
        g += f'<rect x="165" y="{y-12}" width="45" height="28" rx="6" fill="#fff"/><circle cx="187.5" cy="{y+2}" r="8" fill="{DIM}"/>'
    return g

def title(y, s): return txt(187.5, y, s, 22, INK, 700)
def text(y, s): return txt(187.5, y, s, 15, MUT)
def btn(y, s): return f'<rect x="24" y="{y}" width="327" height="52" rx="26" fill="{ACC}"/>' + txt(187.5, y + 32, s, 16, "#fff", 600)
def btn2(y, s): return f'<rect x="24.5" y="{y+.5}" width="326" height="51" rx="25.5" fill="none" stroke="{LINE}"/>' + txt(187.5, y + 32, s, 16, INK, 600)
def link(y, s): return txt(187.5, y, s, 15, MUT, 500)
def field(y, s): return f'<rect x="24" y="{y}" width="327" height="46" rx="12" fill="{CARD}" stroke="{LINE}"/>' + txt(40, y + 29, s, 14, INK, 400, "start")
def nav(s): return f'<path d="M34 72 l-9 9 l9 9" stroke="{INK}" stroke-width="2.5" fill="none" stroke-linecap="round"/>' + txt(187.5, 87, s, 17, INK, 600)

def rows(y, items, right=None, box=True):
    h = 56
    g = f'<rect x="16" y="{y}" width="343" height="{h*len(items)}" rx="16" fill="{CARD}"/>' if box else ""
    for i, it in enumerate(items):
        yy = y + i * h
        lab, r = (it if isinstance(it, tuple) else (it, right))
        g += f'<rect x="32" y="{yy+14}" width="28" height="28" rx="8" fill="{DIM}"/>' + txt(74, yy + 33, lab, 15, INK, 500, "start")
        if r == "lock":
            g += f'<rect x="323" y="{yy+26}" width="14" height="11" rx="2" fill="{MUT}"/><path d="M326 {yy+26} v-4 a4 4 0 0 1 8 0 v4" stroke="{MUT}" stroke-width="2" fill="none"/>'
        elif r == "chev":
            g += f'<path d="M327 {yy+22} l6 6 l-6 6" stroke="{MUT}" stroke-width="2" fill="none"/>'
        elif r == "toggle":
            g += f'<rect x="309" y="{yy+16}" width="40" height="24" rx="12" fill="{ACC}"/><circle cx="337" cy="{yy+28}" r="9" fill="#fff"/>'
        elif r == "ok":
            g += f'<circle cx="331" cy="{yy+28}" r="10" fill="{DIM}"/><path d="M326 {yy+28} l4 4 l7 -8" stroke="#fff" stroke-width="2.4" fill="none"/>'
        elif isinstance(r, str) and r:
            g += txt(343, yy + 33, r, 13, MUT, 400, "end")
        if i < len(items) - 1: g += f'<rect x="74" y="{yy+h}" width="269" height="1" fill="{LINE}"/>'
    return g

def tabbar(active, labels):
    g = f'<rect x="60" y="716" width="255" height="56" rx="28" fill="{CARD}" stroke="{LINE}"/>'
    for i, l in enumerate(labels):
        cx = 103 + i * 85
        if i == active: g += f'<rect x="{cx-38}" y="721" width="76" height="46" rx="23" fill="{DIM}"/>'
        g += f'<rect x="{cx-9}" y="729" width="18" height="16" rx="4" fill="{MUT}"/>' + txt(cx, 760, l, 11, INK, 500)
    return g

def dim(): return f'<rect width="375" height="812" rx="36" fill="#1b2030" opacity=".45"/>'
def sheet(y): return f'<rect x="0" y="{y}" width="375" height="{812-y}" rx="28" fill="{CARD}"/><rect x="167" y="{y+10}" width="40" height="5" rx="3" fill="{DIM}"/>'

def home(locked, L):
    names = [L["vpn"], "Web Protection", "Safe QR Scanner", "Storage Cleaner", "Breach Check", "Password Generator"]
    it = [(n, "lock" if (locked and i < 5) else "chev") for i, n in enumerate(names)]
    g = [txt(24, 92, "Seciro", 22, INK, 700, "start"), f'<rect x="16" y="116" width="343" height="160" rx="22" fill="{DIM}" opacity=".6"/>',
         txt(187.5, 190, L["review"], 17, INK, 600), f'<rect x="40" y="218" width="295" height="40" rx="20" fill="{CARD}"/>', rows(292, it), tabbar(0, L["tabs"])]
    if locked:
        for i in range(5): g.append(f'<rect x="16" y="{292+i*56}" width="300" height="56" fill="{BG}" opacity=".45"/>')
    return g

def wireframes():
    C = {
      "vpn": T("VPN", "VPN", "VPN"), "review": T("Protection Review — бесплатно", "Protection Review — безкоштовно", "Protection Review — free"),
      "tabs": {"ru": ["Home", "Alerts", "Settings"], "uk": ["Home", "Alerts", "Settings"], "en": ["Home", "Alerts", "Settings"]},
    }
    W = {}  # key -> (caption by lang, builder(lang)->svg)
    def add(key, cap, fn): W[key] = (cap, fn)
    def L(l): return {k: (v[l] if isinstance(v, dict) else v) for k, v in C.items()}

    add("lock_a", T("Вариант A: замок на модуле", "Варіант A: замок на модулі", "Option A: a lock on the module"),
        lambda l: phone(home(True, L(l))))
    add("lock_b", T("Вариант B: окно «Доступно в Premium»", "Варіант B: вікно «Доступно в Premium»", "Option B: an “Available in Premium” sheet"),
        lambda l: phone(home(True, L(l)) + [dim(), sheet(440), icon(520, 36, "lock"),
            title(598, T("Доступно в Premium", "Доступно в Premium", "Available in Premium")[l]),
            text(630, T("VPN откроется после подписки", "VPN відкриється після підписки", "VPN opens with a subscription")[l]),
            btn(668, T("Смотреть планы", "Дивитися плани", "See plans")[l]), link(752, T("Не сейчас", "Не зараз", "Not now")[l])]))
    qr_bg = lambda l: [nav("Safe QR Scanner"), f'<rect x="62" y="130" width="250" height="250" rx="24" fill="{DIM}" opacity=".7"/>', dim(), sheet(330)]
    add("qr_safe", T("Безопасная ссылка", "Безпечне посилання", "Safe link"),
        lambda l: phone(qr_bg(l) + [icon(420, 40, "check"), title(500, T("Ссылка безопасна", "Посилання безпечне", "This link is safe")[l]),
            text(532, T("Мы не нашли угроз", "Ми не знайшли загроз", "We found no threats")[l]), field(560, "https://example.com/menu"),
            btn(630, T("Открыть", "Відкрити", "Open")[l]), btn2(694, T("Сканировать ещё", "Сканувати ще", "Scan again")[l])]))
    add("qr_warn", T("Подозрительная ссылка", "Підозріле посилання", "Suspicious link"),
        lambda l: phone(qr_bg(l) + [icon(420, 40, "warn"), title(500, T("Будьте осторожны", "Будьте обережні", "Be careful")[l]),
            text(532, T("Ссылка похожа на подозрительную", "Посилання схоже на підозріле", "This link looks suspicious")[l]), field(560, "https://bit.ly/3xYz"),
            btn(630, T("Назад", "Назад", "Back")[l]), link(722, T("Всё равно открыть", "Все одно відкрити", "Open anyway")[l])]))
    add("qr_text", T("В коде не ссылка (Wi-Fi, текст)", "У коді не посилання (Wi-Fi, текст)", "Not a link (Wi-Fi, text)"),
        lambda l: phone(qr_bg(l) + [icon(420, 40, "wifi"), title(500, T("Сеть Wi-Fi", "Мережа Wi-Fi", "Wi-Fi network")[l]),
            f'<rect x="24" y="528" width="327" height="84" rx="14" fill="{BG}"/>', txt(44, 560, "Cafe_Guest", 15, INK, 600, "start"),
            txt(44, 590, T("Пароль: ••••••••", "Пароль: ••••••••", "Password: ••••••••")[l], 14, MUT, 400, "start"),
            btn(640, T("Скопировать пароль", "Скопіювати пароль", "Copy password")[l]), btn2(704, T("Сканировать ещё", "Сканувати ще", "Scan again")[l])]))
    add("qr_cam", T("Нет доступа к камере", "Немає доступу до камери", "No camera access"),
        lambda l: phone([nav("Safe QR Scanner"), icon(300, 48, "cam"), title(392, T("Нет доступа к камере", "Немає доступу до камери", "No camera access")[l]),
            text(426, T("Разрешите камеру в настройках,\nчтобы сканировать QR-коды", "Дозвольте камеру в налаштуваннях,\nщоб сканувати QR-коди", "Allow the camera in settings\nto scan QR codes")[l]),
            btn(620, T("Открыть настройки", "Відкрити налаштування", "Open settings")[l]), btn2(684, T("Из галереи", "З галереї", "From gallery")[l])]))
    add("qr_err", T("Проверка не удалась", "Перевірка не вдалася", "Check failed"),
        lambda l: phone(qr_bg(l) + [icon(420, 40, "x"), title(500, T("Не удалось проверить", "Не вдалося перевірити", "Couldn't check the link")[l]),
            text(532, T("Проверьте интернет и попробуйте ещё раз", "Перевірте інтернет і спробуйте ще раз", "Check your connection and try again")[l]),
            btn(630, T("Повторить", "Повторити", "Try again")[l]), link(722, T("Открыть без проверки", "Відкрити без перевірки", "Open without checking")[l])]))
    def status(sym, t, x, b1, b2=None):
        return lambda l: phone([txt(187.5, 92, "Seciro", 20, INK, 700), icon(300, 56, sym), title(400, t[l]), text(434, x[l]), btn(640, b1[l])] + ([btn2(704, b2[l])] if b2 else []))
    add("st_update", T("Обновите приложение — закрыть нельзя", "Оновіть апку — закрити не можна", "Update required — can't be closed"),
        status("arrow", T("Обновите приложение", "Оновіть апку", "Update Seciro"), T("Эта версия больше не работает.\nОбновление займёт минуту.", "Ця версія більше не працює.\nОновлення займе хвилину.", "This version no longer works.\nThe update takes a minute."), T("Обновить", "Оновити", "Update")))
    add("st_pay", T("Оплата не прошла", "Оплата не пройшла", "Payment failed"),
        status("card", T("Оплата не прошла", "Оплата не пройшла", "Payment failed"), T("Обновите карту,\nчтобы защита продолжала работать", "Оновіть картку,\nщоб захист продовжував працювати", "Update your card\nto keep protection on"), T("Manage subscription", "Manage subscription", "Manage subscription")))
    add("st_seat", T("Устройство вышло (подписку занял другой телефон)", "Пристрій вийшов (підписку зайняв інший телефон)", "Signed out (another phone took the subscription)"),
        status("phone", T("Вы вышли на этом телефоне", "Ви вийшли на цьому телефоні", "You're signed out here"), T("Подписка сейчас используется\nна другом устройстве", "Підписка зараз використовується\nна іншому пристрої", "Your subscription is in use\non another device"), T("Manage devices", "Manage devices", "Manage devices"), T("Sign in again", "Sign in again", "Sign in again")))
    add("st_off", T("Нет интернета больше суток", "Немає інтернету понад добу", "Offline for over a day"),
        status("wifi", T("Нет подключения", "Немає підключення", "No connection"), T("Подключитесь к интернету,\nчтобы проверить подписку", "Підключіться до інтернету,\nщоб перевірити підписку", "Connect to the internet\nto check your subscription"), T("Повторить", "Повторити", "Try again")))
    add("al_list", T("Alerts: список", "Alerts: список", "Alerts: list"),
        lambda l: phone([txt(24, 92, "Alerts", 26, INK, 700, "start"), rows(120, [(T("VPN отключился", "VPN вимкнувся", "VPN disconnected")[l], T("2 мин", "2 хв", "2 min")[l]), (T("Найдена утечка", "Знайдено витік", "Leak found")[l], T("вчера", "учора", "yesterday")[l]), (T("Защита выключена", "Захист вимкнено", "Protection off")[l], "3 d")]), tabbar(1, ["Home", "Alerts", "Settings"])]))
    add("al_empty", T("Alerts: пусто", "Alerts: порожньо", "Alerts: empty"),
        lambda l: phone([txt(24, 92, "Alerts", 26, INK, 700, "start"), icon(330, 48, "bell"), title(420, T("Пока тихо", "Поки тихо", "All quiet")[l]), text(454, T("Здесь появятся важные события", "Тут з'являться важливі події", "Important events will show up here")[l]), tabbar(1, ["Home", "Alerts", "Settings"])]))
    add("help_sheet", T("Get help, если это почта", "Get help, якщо це пошта", "Get help, if it's email"),
        lambda l: phone(home(False, L(l)) + [dim(), sheet(470), title(540, T("Нужна помощь?", "Потрібна допомога?", "Need help?")[l]), text(572, T("Напишите нам — ответим быстро", "Напишіть нам — відповімо швидко", "Write to us and we'll reply soon")[l]), field(600, "support@…"), btn(670, T("Написать", "Написати", "Write")[l])]))
    add("web_4", T("4 фильтра вместо 5", "4 фільтри замість 5", "4 filters instead of 5"),
        lambda l: phone([nav("Web Protection"), f'<rect x="16" y="116" width="343" height="120" rx="22" fill="{DIM}" opacity=".6"/>', txt(187.5, 184, "Security Level 4/4", 17, INK, 600),
            rows(252, [("Protect Your Device", "toggle")]), rows(324, [("General Ads", "toggle"), ("Stop Tracking", "toggle"), ("Block Adult Content", "toggle"), ("Block Gambling", "toggle")])]))
    add("web_dns", T("Инструкция «Частный DNS»", "Інструкція «Приватний DNS»", "Private DNS guide"),
        lambda l: phone([dim(), sheet(150), txt(187.5, 210, "Seciro", 18, INK, 700), title(258, T("Защита во всём телефоне", "Захист у всьому телефоні", "Protection across the phone")[l]),
            rows(290, [(x, "") for x in [T("1  Откройте Настройки", "1  Відкрийте Налаштування", "1  Open Settings")[l], T("2  Сеть и интернет", "2  Мережа й інтернет", "2  Network & internet")[l], T("3  Частный DNS", "3  Приватний DNS", "3  Private DNS")[l], T("4  Вставьте адрес Seciro", "4  Вставте адресу Seciro", "4  Paste the Seciro address")[l], T("5  Сохранить — готово", "5  Зберегти — готово", "5  Save — done")[l]]]),
            btn(600, T("Скопировать адрес", "Скопіювати адресу", "Copy the address")[l]), link(690, T("Понятно", "Зрозуміло", "Got it")[l])]))
    add("and_review", T("Проверки для Android", "Перевірки для Android", "Android checks"),
        # 27.11: ті самі 4 пункти, що в дизайні (Web Protection, Software Update, Passcode, Face ID), лише з Android-змістом — нічого від себе
        lambda l: phone([nav("Protection Review"), rows(120, [(x, "ok") for x in [T("Web Protection (через VPN)", "Web Protection (через VPN)", "Web Protection (via VPN)")[l], T("Обновление Android", "Оновлення Android", "Android update")[l], T("Блокировка экрана", "Блокування екрана", "Screen lock")[l], T("Биометрия (отпечаток / лицо)", "Біометрія (відбиток / обличчя)", "Biometric unlock")[l]]])]))
    add("perm_pre", T("Перед системным окном", "Перед системним вікном", "Before the system dialog"),
        lambda l: phone([icon(300, 56, "photo"), title(400, T("Доступ к фото", "Доступ до фото", "Photo access")[l]), text(434, T("Найдём дубликаты и освободим место.\nФото не покидают телефон.", "Знайдемо дублікати й звільнимо місце.\nФото не залишають телефон.", "We'll find duplicates and free up space.\nPhotos never leave your phone.")[l]), btn(640, T("Продолжить", "Продовжити", "Continue")[l]), link(730, T("Не сейчас", "Не зараз", "Not now")[l])]))
    add("perm_limited", T("Доступ только к части фото", "Доступ лише до частини фото", "Access to some photos only"),
        lambda l: phone([nav("Storage Cleaner"), f'<rect x="16" y="120" width="343" height="110" rx="16" fill="{CARD}" stroke="{LINE}"/>', txt(36, 156, T("Видим только часть фото", "Бачимо лише частину фото", "We can see only some photos")[l], 15, INK, 600, "start"),
            txt(36, 182, T("Разрешите все, чтобы найти дубликаты", "Дозвольте всі, щоб знайти дублікати", "Allow all to find duplicates")[l], 13, MUT, 400, "start"), f'<rect x="36" y="196" width="120" height="24" rx="12" fill="{ACC}"/>', txt(96, 213, T("Разрешить все", "Дозволити всі", "Allow all")[l], 12, "#fff", 600)]
            + [f'<rect x="{16 + (i % 3) * 116}" y="{250 + (i // 3) * 116}" width="108" height="108" rx="10" fill="{DIM}"/>' for i in range(9)]))
    add("perm_denied", T("Если человек отказал", "Якщо людина відмовила", "If the person declined"),
        lambda l: phone([icon(300, 56, "lock"), title(400, T("Нет доступа к фото", "Немає доступу до фото", "No photo access")[l]), text(434, T("Без него Storage Cleaner не найдёт\nдубликаты. Включите в настройках.", "Без нього Storage Cleaner не знайде\nдублікати. Увімкніть у налаштуваннях.", "Without it Storage Cleaner can't find\nduplicates. Turn it on in settings.")[l]), btn(640, T("Открыть настройки", "Відкрити налаштування", "Open settings")[l]), link(730, T("Не сейчас", "Не зараз", "Not now")[l])]))
    def large(l):
        cap = T("Колонка 480–600 pt по центру", "Колонка 480–600 pt по центру", "A 480–600 pt column in the centre")[l]
        return ('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 720" width="1024" height="720">'
            f'<rect width="1024" height="720" rx="36" fill="{BG}"/><rect x="232" y="0" width="560" height="720" fill="{CARD}" opacity=".55"/>'
            f'<path d="M232 30 v660 M792 30 v660" stroke="{ACC}" stroke-dasharray="8 8" stroke-width="2"/>' + txt(512, 26, cap, 18, ACC, 600)
            + txt(262, 92, "Seciro", 26, INK, 700, "start") + f'<rect x="252" y="120" width="520" height="150" rx="24" fill="{DIM}" opacity=".7"/>'
            + "".join(f'<rect x="252" y="{290+i*62}" width="520" height="54" rx="14" fill="{CARD}" stroke="{LINE}"/><rect x="268" y="{303+i*62}" width="28" height="28" rx="8" fill="{DIM}"/><rect x="310" y="{310+i*62}" width="{160+(i%3)*40}" height="12" rx="6" fill="{DIM}"/>' for i in range(5))
            + f'<rect x="362" y="636" width="300" height="56" rx="28" fill="{CARD}" stroke="{LINE}"/>'
            + txt(120, 360, T("пусто", "порожньо", "empty")[l], 16, MUT) + txt(904, 360, T("пусто", "порожньо", "empty")[l], 16, MUT) + "</svg>")
    add("large", T("Планшет / раскрытая раскладушка, горизонтально", "Планшет / розгорнута розкладачка, горизонтально", "Tablet / unfolded foldable, landscape"), large)
    def icons(l):
        g = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 900 400" width="900" height="400">', f'<rect width="900" height="400" rx="24" fill="{BG}"/>']
        g.append(f'<rect x="40" y="60" width="216" height="216" fill="{DIM}"/><circle cx="148" cy="168" r="66" fill="none" stroke="{ACC}" stroke-dasharray="6 6" stroke-width="2"/>')
        g.append(f'<path d="M148 126 l40 16 v26 c0 26 -17 44 -40 54 c-23 -10 -40 -28 -40 -54 v-26 z" fill="{INK}"/>')
        g.append(txt(148, 310, T("108 dp: фон + знак", "108 dp: фон + знак", "108 dp: background + mark")[l], 15, INK, 600) + txt(148, 334, T("знак в круге 66 dp", "знак у колі 66 dp", "mark inside the 66 dp circle")[l], 13, ACC))
        g.append(f'<rect x="340" y="96" width="144" height="144" rx="72" fill="#fff" stroke="{LINE}"/><path d="M412 140 l30 12 v20 c0 20 -13 33 -30 40 c-17 -7 -30 -20 -30 -40 v-20 z" fill="{MUT}"/>')
        g.append(txt(412, 310, T("Монохромная", "Монохромна", "Monochrome")[l], 15, INK, 600) + txt(412, 334, T("Android 13: под обои", "Android 13: під шпалери", "Android 13: wallpaper tint")[l], 13, MUT))
        g.append(f'<rect x="580" y="120" width="96" height="96" rx="16" fill="{INK}"/><path d="M628 146 l20 8 v13 c0 13 -9 22 -20 27 c-11 -5 -20 -14 -20 -27 v-13 z" fill="#fff"/>')
        g.append(f'<rect x="716" y="120" width="140" height="44" rx="22" fill="#fff" stroke="{LINE}"/><path d="M738 132 l10 4 v7 c0 7 -5 11 -10 13 c-5 -2 -10 -6 -10 -13 v-7 z" fill="{INK}"/>' + txt(800, 147, "VPN", 14, INK, 600))
        g.append(txt(718, 310, T("Уведомление и плитка", "Сповіщення й плитка", "Notification and tile")[l], 15, INK, 600) + txt(718, 334, T("белый силуэт, 24 dp", "білий силует, 24 dp", "white silhouette, 24 dp")[l], 13, MUT))
        g.append("</svg>"); return "".join(g)
    add("icons", T("Адаптивная, монохромная и маленькие иконки", "Адаптивна, монохромна й маленькі іконки", "Adaptive, monochrome and small icons"), icons)
    def store(l):
        g = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 520" width="1000" height="520">', f'<rect width="1000" height="520" rx="24" fill="{BG}"/>']
        g.append(f'<rect x="30" y="60" width="614" height="300" rx="8" fill="{DIM}"/><rect x="132" y="110" width="410" height="200" rx="8" fill="none" stroke="{ACC}" stroke-dasharray="8 8" stroke-width="2"/>')
        g.append(txt(337, 205, T("Логотип и одна мысль", "Логотип і одна думка", "Logo and one message")[l], 20, INK, 700) + txt(337, 236, T("по центру — края обрезаются", "по центру — краї обрізаються", "centred — edges get cropped")[l], 15, ACC))
        g.append(txt(337, 400, T("Баннер 1024×500", "Банер 1024×500", "Banner 1024×500")[l], 17, INK, 600))
        for i in range(2):
            x = 690 + i * 150
            g.append(f'<rect x="{x}" y="40" width="132" height="320" rx="14" fill="{ACC}"/>' + txt(x + 66, 74, T("Быстрый VPN", "Швидкий VPN", "Fast VPN")[l] if i == 0 else T("Проверка ссылок", "Перевірка посилань", "Link check")[l], 14, "#fff", 700))
            g.append(f'<rect x="{x+14}" y="96" width="104" height="250" rx="12" fill="#fff"/><rect x="{x+26}" y="120" width="80" height="60" rx="8" fill="{DIM}"/>' + "".join(f'<rect x="{x+26}" y="{196+j*26}" width="80" height="16" rx="5" fill="{DIM}"/>' for j in range(5)))
        g.append(txt(840, 400, T("Скриншоты: подпись + экран", "Скріншоти: підпис + екран", "Screenshots: caption + screen")[l], 17, INK, 600))
        g.append(txt(500, 470, T("+ иконка 512×512 · скриншоты 1080×1920 · планшет 1080×1920 или 1920×1080", "+ іконка 512×512 · скріншоти 1080×1920 · планшет 1080×1920 або 1920×1080", "+ 512×512 icon · 1080×1920 screenshots · tablet 1080×1920 or 1920×1080")[l], 15, MUT))
        g.append("</svg>"); return "".join(g)
    add("store", T("Обложка и скриншоты Google Play", "Обкладинка й скріншоти Google Play", "Google Play feature graphic and screenshots"), store)
    # ── приклади для нотаток прототипу (26.11) ──
    add("web_vpn", T("Пример: защита зависит от VPN", "Приклад: захист залежить від VPN", "Example: protection depends on VPN"),
        lambda l: phone([nav("Web Protection"), f'<rect x="16" y="116" width="343" height="96" rx="20" fill="#e9efff" stroke="{ACC}"/>',
            txt(36, 150, T("Защита работает внутри VPN", "Захист працює всередині VPN", "Protection works inside the VPN")[l], 15, INK, 700, "start"),
            txt(36, 174, T("Сейчас VPN выключен", "Зараз VPN вимкнений", "VPN is off right now")[l], 13, MUT, 400, "start"),
            f'<rect x="232" y="158" width="110" height="36" rx="18" fill="{ACC}"/>', txt(287, 181, T("Включить VPN", "Увімкнути VPN", "Turn on VPN")[l], 13, "#fff", 600),
            f'<rect x="16" y="228" width="343" height="110" rx="22" fill="{DIM}" opacity=".6"/>', txt(187.5, 290, "Security Level 4/4", 17, INK, 600),
            rows(354, [("General Ads", "toggle"), ("Stop Tracking", "toggle"), ("Block Adult Content", "toggle"), ("Block Gambling", "toggle")])]))
    add("gp_sheet", T("Пример: оплата Google Play", "Приклад: оплата Google Play", "Example: Google Play checkout"),
        lambda l: phone([dim(), sheet(430), txt(40, 482, "Google Play", 16, INK, 700, "start"), f'<rect x="24" y="500" width="327" height="1" fill="{LINE}"/>',
            txt(40, 540, "Seciro Premium · Weekly", 16, INK, 600, "start"),
            txt(40, 566, T("3 дня за $0.99, затем $8.99 в неделю", "3 дні за $0.99, далі $8.99 на тиждень", "3 days for $0.99, then $8.99/week")[l], 13, MUT, 400, "start"),
            txt(40, 600, T("Отмена в любой момент в Google Play", "Скасувати можна будь-коли в Google Play", "Cancel anytime in Google Play")[l], 13, MUT, 400, "start"),
            f'<rect x="24" y="680" width="327" height="48" rx="24" fill="#01875f"/>', txt(187.5, 710, T("Подписаться", "Підписатися", "Subscribe")[l], 16, "#fff", 600),
            txt(187.5, 760, T("Окно рисует Google Play, не мы", "Вікно малює Google Play, не ми", "Google Play draws this, not us")[l], 12, MUT)]))
    def kb(l):
        g = [title(170, T("Вход", "Вхід", "Sign in")[l]), field(220, "name@example.com"), btn(290, "Continue"), f'<rect x="0" y="486" width="375" height="326" fill="#e3e6ec"/>']
        for r, (n, x0) in enumerate([(10, 6), (9, 24), (7, 60)]):
            for i in range(n): g.append(f'<rect x="{x0 + i * 36.3:.1f}" y="{506 + r * 54}" width="32" height="44" rx="8" fill="#fff"/>')
        g.append(f'<rect x="6" y="668" width="62" height="44" rx="8" fill="#c9cfd9"/><rect x="74" y="668" width="227" height="44" rx="8" fill="#fff"/><rect x="307" y="668" width="62" height="44" rx="8" fill="{ACC}"/>')
        g.append(txt(187.5, 750, T("Системная клавиатура Android (Gboard)", "Системна клавіатура Android (Gboard)", "Android system keyboard (Gboard)")[l], 12, MUT))
        return phone(g)
    add("kb_android", T("Пример: клавиатура Android", "Приклад: клавіатура Android", "Example: Android keyboard"), kb)
    add("notif_vpn", T("Пример: уведомление «VPN включён»", "Приклад: сповіщення «VPN увімкнено»", "Example: “VPN on” notification"),
        lambda l: phone([f'<rect width="375" height="812" rx="36" fill="#1c2230"/>', txt(28, 92, "12:30", 34, "#fff", 300, "start"),
            f'<rect x="16" y="130" width="343" height="86" rx="22" fill="#2a3242"/>', f'<rect x="32" y="150" width="22" height="22" rx="6" fill="{ACC}"/>',
            txt(64, 166, "Seciro · VPN", 13, "#c9d1e0", 500, "start"), txt(32, 196, T("VPN включён · Paris, FR", "VPN увімкнено · Paris, FR", "VPN on · Paris, FR")[l], 15, "#fff", 600, "start"),
            txt(187.5, 260, T("Android держит это уведомление,", "Android тримає це сповіщення,", "Android keeps this notification")[l], 13, "#9aa3b5"),
            txt(187.5, 280, T("пока VPN включён", "поки VPN увімкнений", "while the VPN is on")[l], 13, "#9aa3b5")]))
    add("upd_android", T("Пример: инструкция обновления Android", "Приклад: інструкція оновлення Android", "Example: Android update guide"),
        lambda l: phone([dim(), sheet(150), txt(187.5, 210, "Seciro", 18, INK, 700), title(258, "Software Update"),
            rows(290, [(x, "") for x in ["1  Open Settings, search “update”", "2  Tap Software update", "3  Download and install", "4  Tap Restart now"]]),
            text(500, "Can't find it?\nSamsung: Settings → Software update\nXiaomi: Settings → About phone"), btn(640, "Open Settings"), btn2(704, "Got it")]))
    add("web_flow", T("Пример: Web Protection на Android", "Приклад: Web Protection на Android", "Example: Web Protection on Android"),
        lambda l: phone([nav("Protection Review"),
            rows(120, [("Web Protection", "Turn on VPN"), ("Software Update", "ok"), ("Screen lock", "ok"), ("Biometric unlock", "ok")]),
            text(380, T("↓ нажали Web Protection — открылся VPN", "↓ натиснули Web Protection — відкрився VPN", "↓ tap Web Protection — the VPN screen opens")[l]),
            f'<rect x="16" y="404" width="343" height="196" rx="16" fill="{CARD}"/>', title(448, "VPN"), text(478, "Not connected"),
            btn(520, "Connect"),
            text(650, T("После Connect сайты защищены\nво всех браузерах и приложениях", "Після Connect сайти захищені\nв усіх браузерах і апках", "After Connect, sites are protected\nin every browser and app")[l])]))
    add("onb_phone", T("Пример: без «iPhone»", "Приклад: без «iPhone»", "Example: no “iPhone”"),
        lambda l: phone([f'<rect x="132" y="150" width="111" height="220" rx="18" fill="none" stroke="{INK}" stroke-width="4"/><rect x="176" y="160" width="23" height="5" rx="2.5" fill="{INK}"/>',
            f'<path d="M187.5 220 l30 12 v20 c0 20 -13 33 -30 40 c-17 -7 -30 -20 -30 -40 v-20 z" fill="{DIM}"/>',
            txt(187.5, 430, T("нейтральный телефон, без «чёлки»", "нейтральний телефон, без «чубчика»", "a neutral phone, no notch")[l], 12, MUT),
            txt(187.5, 560, "Your phone,", 30, INK, 700), txt(187.5, 600, "Protected", 30, INK, 700),
            text(645, "Run a full security scan to keep your\nphone safe, private, and running at its best."), btn(720, "Continue")]))
    # ── ряди 7–10 (27.11) ──
    def bstatus(sym, t, x, b1, b2=None):
        return lambda l: phone([nav("Breach Check"), icon(300, 56, sym), title(400, t[l]), text(434, x[l]), btn(640, b1[l])] + ([btn2(704, b2[l])] if b2 else []))
    add("br_limit", T("Пример: лимит проверок", "Приклад: ліміт перевірок", "Example: check limit"),
        bstatus("warn", T("Лимит на сегодня", "Ліміт на сьогодні", "Daily limit reached"), T("Можно проверить до 5 адресов в сутки.\nПопробуйте завтра.", "Можна перевірити до 5 адрес на добу.\nСпробуйте завтра.", "You can check up to 5 addresses a day.\nTry again tomorrow."), T("Понятно", "Зрозуміло", "Got it")))
    add("br_error", T("Пример: проверка не удалась", "Приклад: перевірка не вдалася", "Example: check failed"),
        bstatus("x", T("Не удалось проверить", "Не вдалося перевірити", "Couldn't check"), T("Это не значит, что утечек нет.\nПопробуйте ещё раз.", "Це не означає, що витоків немає.\nСпробуйте ще раз.", "This doesn't mean there are no leaks.\nPlease try again."), T("Повторить", "Повторити", "Try again")))
    add("br_need", T("Пример: нужна подписка или вход", "Приклад: потрібна підписка або вхід", "Example: subscription or sign-in needed"),
        bstatus("lock", T("Нужна подписка", "Потрібна підписка", "Subscription needed"), T("Проверка утечек входит в Premium", "Перевірка витоків входить у Premium", "Breach Check is part of Premium"), T("Смотреть планы", "Дивитися плани", "See plans"), T("Войти", "Увійти", "Sign in")))
    add("acc_states", T("Пример: строка подписки по состоянию", "Приклад: рядок підписки за станом", "Example: plan line by state"),
        lambda l: phone([txt(24, 92, "My Account", 20, INK, 700, "start"),
            rows(130, [("Premium Plan", "$29.99 / month"), ("Next Billing", "Sep 12, 2026")]),
            txt(24, 268, T("отменена, но оплачена:", "скасована, але оплачена:", "cancelled, still paid:")[l], 13, MUT, 500, "start"),
            rows(280, [("Access until", "Sep 12, 2026")]),
            txt(24, 368, T("платёж не прошёл:", "оплата не пройшла:", "payment failed:")[l], 13, MUT, 500, "start"),
            f'<rect x="16" y="380" width="343" height="56" rx="16" fill="#fdecee"/>', txt(32, 413, T("Оплата не прошла — обновите карту", "Оплата не пройшла — оновіть картку", "Payment failed — update your card")[l], 14, "#d92635", 600, "start")]))
    add("del_confirm", T("Пример: перед удалением аккаунта", "Приклад: перед видаленням акаунта", "Example: before deleting the account"),
        lambda l: phone([dim(), sheet(420), icon(500, 36, "x"), title(580, T("Удалить аккаунт?", "Видалити акаунт?", "Delete your account?")[l]),
            text(612, T("Подписка при этом НЕ отменяется.\nОтмените её в Google Play или в кабинете.", "Підписка при цьому НЕ скасовується.\nСкасуйте її в Google Play або в кабінеті.", "This does NOT cancel your subscription.\nCancel it in Google Play or your account.")[l]),
            f'<rect x="24" y="672" width="327" height="52" rx="26" fill="#d92635"/>', txt(187.5, 704, T("Удалить", "Видалити", "Delete")[l], 16, "#fff", 600),
            link(752, T("Отмена", "Скасувати", "Cancel")[l])]))
    # ── для кіту дизайнера (27.11) ──
    def dupes(l):
        g = [nav("Duplicate Photos"), txt(24, 140, T("2 группы · 5 фото", "2 групи · 5 фото", "2 groups · 5 photos")[l], 14, MUT, 500, "start")]
        for gi, (y, n) in enumerate([(160, 3), (420, 2)]):
            g.append(f'<rect x="16" y="{y}" width="343" height="240" rx="18" fill="{CARD}"/>')
            for i in range(n):
                x = 28 + i * 108
                g.append(f'<rect x="{x}" y="{y+14}" width="100" height="150" rx="10" fill="{DIM}"/>')
                if i == 0: g.append(f'<rect x="{x+6}" y="{y+20}" width="52" height="20" rx="10" fill="#1f9d55"/>' + txt(x + 32, y + 34, T("Лучшее", "Найкраще", "Best")[l], 11, "#fff", 700))
                else: g.append(f'<circle cx="{x+86}" cy="{y+30}" r="10" fill="{ACC}"/><path d="M{x+81} {y+30} l4 4 l7 -8" stroke="#fff" stroke-width="2.4" fill="none"/>')
            g.append(txt(28, y + 196, T("Оставить лучшее, удалить остальные", "Лишити найкраще, видалити решту", "Keep the best, delete the rest")[l], 13, INK, 600, "start"))
        g.append(btn(700, T("Удалить 3 фото", "Видалити 3 фото", "Delete 3 photos")[l]))
        return phone(g)
    add("st_dupes", T("Пример: дубликаты группами", "Приклад: дублікати групами", "Example: duplicates in groups"), dupes)
    add("lang_pick", T("Пример: выбор языка", "Приклад: вибір мови", "Example: language picker"),
        # мови не вигадуємо: English (як у Figma) + сірі рядки-заглушки
        lambda l: phone([nav("Language"), rows(120, [("English", "ok"), ("", ""), ("", "")])]
            + [f'<rect x="74" y="{120 + i * 56 + 22}" width="{150 - i * 34}" height="12" rx="6" fill="{DIM}"/>' for i in (1, 2)]
            + [text(330, T("Какие языки — решает менеджер.\nИли убрать пункт, если язык один", "Які мови — вирішує менеджер.\nАбо прибрати пункт, якщо мова одна", "Which languages is the manager's call.\nOr remove the item if there's one language")[l])]))
    # ── приклади для питань Віталію (27.11) ──
    def brl(l):
        g = [nav("Breach Check"), txt(24, 140, T("Найдено в 2 утечках", "Знайдено у 2 витоках", "Found in 2 breaches")[l], 15, INK, 600, "start")]
        for i, (name, dom, yr) in enumerate([("Example Shop", "example-shop.com", "2023"), ("Sample Forum", "sample-forum.net", "2021")]):
            y = 160 + i * 150
            g.append(f'<rect x="16" y="{y}" width="343" height="134" rx="18" fill="{CARD}"/>')
            g.append(f'<circle cx="52" cy="{y+40}" r="20" fill="{ACC}"/>' + txt(52, y + 47, name[0], 18, "#fff", 700))
            g.append(txt(84, y + 36, name, 16, INK, 600, "start") + txt(84, y + 56, f"{dom} · {yr}", 13, MUT, 400, "start"))
            g.append(f'<rect x="32" y="{y+80}" width="96" height="26" rx="13" fill="{BG}" stroke="{LINE}"/>' + txt(80, y + 98, "Email", 12, INK, 500))
            g.append(f'<rect x="136" y="{y+80}" width="110" height="26" rx="13" fill="{BG}" stroke="{LINE}"/>' + txt(191, y + 98, T("Пароли", "Паролі", "Passwords")[l], 12, INK, 500))
        g.append(text(500, T("Вместо логотипа — первая буква названия", "Замість логотипа — перша літера назви", "The name's first letter instead of a logo")[l]))
        return phone(g)
    add("br_logo", T("Пример: вместо логотипа — первая буква", "Приклад: замість логотипа — перша літера", "Example: first letter instead of a logo"), brl)
    def pwr(l):
        g = [nav("Password Generator"), f'<rect x="16" y="120" width="343" height="72" rx="16" fill="{CARD}"/>', txt(187.5, 164, "k7#Qm2!vR9pL4xZ8", 20, INK, 600)]
        y = 222
        for name, lo, hi in [("A", 8, 20), ("B", 8, 32), ("C", 12, 64)]:
            g.append(f'<rect x="16" y="{y}" width="343" height="124" rx="16" fill="{CARD}"/>')
            g.append(txt(32, y + 32, f"{T('Вариант', 'Варіант', 'Option')[l]} {name}: {lo}–{hi}", 15, INK, 600, "start"))
            g.append(txt(343, y + 32, T("длина 16", "довжина 16", "length 16")[l], 13, MUT, 500, "end"))
            x0, x1 = 48, 327
            kx = x0 + (16 - lo) / (hi - lo) * (x1 - x0)
            g.append(f'<rect x="{x0}" y="{y+64}" width="{x1-x0}" height="6" rx="3" fill="{DIM}"/><rect x="{x0}" y="{y+64}" width="{kx-x0:.0f}" height="6" rx="3" fill="{ACC}"/>')
            g.append(f'<circle cx="{kx:.0f}" cy="{y+67}" r="12" fill="#fff" stroke="{LINE}" stroke-width="2"/>')
            g.append(txt(x0, y + 104, str(lo), 13, MUT, 600) + txt(x1, y + 104, str(hi), 13, MUT, 600))
            y += 140
        return phone(g)
    add("pw_range", T("Пример: границы ползунка — варианты A, B, C", "Приклад: межі повзунка — варіанти A, B, C", "Example: slider limits — options A, B, C"), pwr)
    return W

if __name__ == "__main__":
    import os
    W = wireframes(); os.makedirs("wfout", exist_ok=True)
    for k, (cap, fn) in W.items():
        for l in LANGS: open(f"wfout/{k}_{l}.svg", "w").write(fn(l))
    print(len(W), "wireframes")
