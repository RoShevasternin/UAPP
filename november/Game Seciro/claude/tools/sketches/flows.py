# Схеми «ваш екран → дія → системне вікно Android» для пункту про дозволи.
import base64, io
from PIL import Image
from html import escape as E
from wf import T, txt, INK, MUT, ACC
F = "/Users/admin/Apps/UAPP/november/Game Seciro/claude/figma/"
RED = "#e5332a"
def b64img(path):
    im = Image.open(path).convert("RGB").resize((375, 812), Image.LANCZOS); bio = io.BytesIO(); im.save(bio, "JPEG", quality=80)
    return "data:image/jpeg;base64," + base64.b64encode(bio.getvalue()).decode()
def wrap(s, n):
    out, cur = [], ""
    for w in s.split():
        if len(cur) + len(w) + 1 > n and cur: out.append(cur); cur = w
        else: cur = (cur + " " + w).strip()
    out.append(cur); return out
def dialog(kind, title, body, opts):
    g = []
    lines = wrap(title, 26)
    bl = wrap(body, 36) if body else []
    h = 40 + 34 + len(lines) * 26 + (len(bl) * 20 + 14 if bl else 0) + (len(opts) * 52 if kind == "perm" else 56) + 20
    y0 = 406 - h / 2
    g.append(f'<rect x="32" y="{y0:.0f}" width="311" height="{h:.0f}" rx="28" fill="#eef0f5"/>')
    g.append(f'<rect x="175" y="{y0+24:.0f}" width="25" height="25" rx="6" fill="{ACC}"/>')
    y = y0 + 84
    for i, l in enumerate(lines): g.append(txt(187.5, y + i * 26, l, 19, INK, 500))
    y += len(lines) * 26
    if bl:
        for i, l in enumerate(bl): g.append(txt(56, y + 4 + i * 20, l, 14, MUT, 400, "start"))
        y += len(bl) * 20 + 14
    if kind == "perm":
        for i, o in enumerate(opts):
            g.append(f'<rect x="52" y="{y+i*52:.0f}" width="271" height="44" rx="22" fill="#dfe3ec"/>' + txt(187.5, y + i * 52 + 28, o, 15, INK, 600))
    else:
        g.append(txt(262, y + 34, opts[0], 15, ACC, 600, "end") + txt(318, y + 34, opts[1], 15, ACC, 600, "end"))
    return "".join(g)
def flow(img, box, d, l):
    s = ['<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 860 812" width="860" height="812">',
         '<defs><clipPath id="c"><rect width="375" height="812" rx="36"/></clipPath>', f'<image id="s" width="375" height="812" href="{img}"/></defs>',
         '<g clip-path="url(#c)"><use href="#s"/></g>']
    if box:
        x, y, w, h = box
        s.append(f'<rect x="{x-6}" y="{y-6}" width="{w+12}" height="{h+12}" rx="{(h+12)/2:.0f}" fill="none" stroke="{RED}" stroke-width="5"/>')
    s.append(f'<path d="M392 406 h60 m-14 -14 l14 14 l-14 14" stroke="{RED}" stroke-width="5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>')
    s.append(f'<g transform="translate(485 0)"><g clip-path="url(#c)"><use href="#s"/><rect width="375" height="812" fill="#10131c" opacity=".55"/>{dialog(*d)}</g></g></svg>')
    return "".join(s)
def flows():
    vpn = b64img(F + "frames/r4_02_5-3427.png"); cam = b64img(F + "overview/r6_02_5-6417.png")
    pho = b64img(F + "overview/r7_02_5-6268.png"); home = b64img(F + "frames/r3_01_5-1686.png")
    btn = (24, 719, 327, 52)
    W = {}
    W["fl_vpn"] = (T("VPN: первое нажатие «Connect to VPN» → окно Android", "VPN: перше натискання «Connect to VPN» → вікно Android", "VPN: first tap on “Connect to VPN” → Android dialog"),
        lambda l: flow(vpn, btn, ("vpn", T("Запрос на подключение", "Запит на підключення", "Connection request")[l],
            T("Seciro хочет настроить VPN-подключение, которое позволит отслеживать трафик. Разрешайте, только если доверяете источнику.", "Seciro хоче налаштувати VPN-підключення, яке дозволить відстежувати трафік. Дозволяйте, лише якщо довіряєте джерелу.", "Seciro wants to set up a VPN connection that allows it to monitor network traffic. Only accept if you trust the source.")[l],
            [T("Отмена", "Скасувати", "Cancel")[l], "OK"]), l))
    W["fl_cam"] = (T("QR Scanner: «Scan a QR Code» → камера", "QR Scanner: «Scan a QR Code» → камера", "QR Scanner: “Scan a QR Code” → camera"),
        lambda l: flow(cam, btn, ("perm", T("Разрешить приложению Seciro снимать фото и видео?", "Дозволити додатку Seciro знімати фото й відео?", "Allow Seciro to take pictures and record video?")[l], "",
            [T("При использовании приложения", "Під час використання", "While using the app")[l], T("Только в этот раз", "Лише цього разу", "Only this time")[l], T("Не разрешать", "Не дозволяти", "Don't allow")[l]]), l))
    W["fl_photo"] = (T("Storage Cleaner: «Scan Storage» → фото", "Storage Cleaner: «Scan Storage» → фото", "Storage Cleaner: “Scan Storage” → photos"),
        lambda l: flow(pho, btn, ("perm", T("Разрешить приложению Seciro доступ к фото и видео?", "Дозволити додатку Seciro доступ до фото й відео?", "Allow Seciro to access photos and videos?")[l], "",
            [T("Разрешить все", "Дозволити всі", "Allow all")[l], T("Выбрать фото и видео", "Вибрати фото й відео", "Select photos and videos")[l], T("Не разрешать", "Не дозволяти", "Don't allow")[l]]), l))
    W["fl_notif"] = (T("Первое открытие главной → уведомления (Android 13+)", "Перше відкриття головної → сповіщення (Android 13+)", "First time on Home → notifications (Android 13+)"),
        lambda l: flow(home, None, ("perm", T("Разрешить приложению Seciro отправлять уведомления?", "Дозволити додатку Seciro надсилати сповіщення?", "Allow Seciro to send you notifications?")[l], "",
            [T("Разрешить", "Дозволити", "Allow")[l], T("Не разрешать", "Не дозволяти", "Don't allow")[l]]), l))
    return W
