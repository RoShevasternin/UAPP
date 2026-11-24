# Дані з Figma (Seciro)

Знято через вбудований браузер + `window.figma` (Plugin API у сторінці Figma), без MCP.

- `frames/r<ряд>_<№>_<node>.png` — кадри @2x, порядок як у Figma.
- `json/<node>.json` — дерево шарів: x/y/w/h, заливки, рамки, радіуси, тіні, auto-layout, текст (шрифт, кегль,
  інтерліньяж). Вузли `img`, `logo`, `Shape Set` не розгорнуті — вони в `art/`. `system bar` і `Home` — `skip: ios`.
- `art/<кадр>_<шар>_<node>.svg` — експорт SVG (текст у кривих). Позиція на кадрі — `absoluteRenderBounds`
  (може відрізнятися від x/y шару, бо тінь / фігура фону виходять за межі).
- `icons/<назва>.svg` — Material Symbols з головних компонентів (інстанс поза кліпом кадру не експортується).

## Рецепт: багато файлів одним завантаженням

У консолі вкладки Figma (через `javascript_tool`) визначити `window.__tar(files)` (простий ustar) і
`window.__save(blob, name)`, зібрати `[{name, data: await node.exportAsync(...)}]`, зберегти один `.tar`
→ він падає в `~/Downloads` → `tar -xf … -C claude/figma`. Кілька окремих завантажень підряд браузер губить.

## Пастки вбудованого браузера (24.11)

- Глобальний `figma` у вкладці час від часу «закривається» («plugin has already been closed») — перезавантажити вкладку.
- Коли панель браузера схована, завантаження не довершується: файл лишається в `~/Downloads/.Q6L2SF6YDW.com.anthropic.claudefordesktop.*`
  (повний, лише не перейменований) — брати найновіший такий файл і `tar -xf` з нього.
- `absoluteRenderBounds` буває `null` (прихований / порожній шар) — пропускати.

## Збирач v2 (рендерер, з ряду 3)

Код — у історії сесії 24.11 (функція `ser` у вкладці): для кожного шару ax/ay від кадру, fills / strokes / sw / sa / r / fx / clip / op,
тексти `t.runs` (шрифт, кегль, колір, letterSpacing px, lineHeight px, textDecoration, textCase; U+2028 → \n). Шар без тексту,
але з вектором / картинкою → `png` (PNG @3x, ім'я = хеш вмісту) + rx/ry/rw/rh (absoluteRenderBounds). Інстанси → `comp`.
Шари з іменем «Home» (текст) не плутати з home-індикатором — пропускаємо лише RECTANGLE «Home» і «system bar».
