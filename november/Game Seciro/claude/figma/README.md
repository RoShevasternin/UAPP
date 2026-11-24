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
