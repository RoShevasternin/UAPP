#ifdef GL_ES
precision mediump float;
#endif

// ─────────────────────────────────────────────────────────────────────────────
// GRADIENT ROUND RECT — заокруглений прямокутник з ЛІНІЙНИМ градієнтом
// заливки (CSS linear-gradient) і окремим кольором обводки.
//
// Основа — roundRectFS (та сама SDF і той самий «край усередині квада»),
// але колір не з тінту, а з u_colA → u_colB уздовж u_dir (одиничний вектор
// у локальних world-юнітах, y — вгору). Тінт актора (v_color) множиться
// зверху — так альфа actor.color гасить усе разом (fade-анімації).
//
// CSS 135deg (зліва-зверху → справа-вниз) = u_dir (0.707, -0.707).
// ─────────────────────────────────────────────────────────────────────────────

varying vec4 v_color;
varying vec2 v_localUV;

uniform vec2  u_size;
uniform float u_radius;
uniform float u_aa;
uniform vec4  u_colA;
uniform vec4  u_colB;
uniform vec2  u_dir;
uniform float u_start;       // до цієї точки — чистий A («A 35%, …»)
uniform float u_mid;         // з цієї точки — чистий B («…, B 55%»)
uniform float u_strokeWidth;
uniform vec4  u_strokeColor;

float roundedBox(vec2 p, vec2 halfSize, float r) {
    vec2 q = abs(p) - halfSize + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    vec2 p = (v_localUV - 0.5) * u_size;
    // v_localUV.y = 0 зверху (текстура), а u_dir — у y-вгору: перевертаємо y
    vec2 pu = vec2(p.x, -p.y);
    // радіус «999» = пігулка: обмежуємо половиною меншої сторони, інакше SDF ламається
    float d = roundedBox(p, u_size * 0.5, min(u_radius, min(u_size.x, u_size.y) * 0.5));
    float shape = 1.0 - smoothstep(-u_aa, 0.0, d);

    // Проєкція на напрям градієнта, нормована на «півдіагональ» уздовж нього
    float ext = abs(u_dir.x) * u_size.x * 0.5 + abs(u_dir.y) * u_size.y * 0.5;
    float t = clamp((dot(pu, u_dir) / max(ext, 0.0001)) * 0.5 + 0.5, 0.0, 1.0);
    t = clamp((t - u_start) / max(u_mid - u_start, 0.0001), 0.0, 1.0);
    vec4 fill = mix(u_colA, u_colB, t);
    fill.a *= shape;

    vec4 col = fill;
    if (u_strokeWidth > 0.0001) {
        float inner = 1.0 - smoothstep(-u_strokeWidth - u_aa, -u_strokeWidth, d);
        float s = clamp(shape - inner, 0.0, 1.0) * u_strokeColor.a;
        // stroke over fill (premultiplied-like mix)
        float a = s + fill.a * (1.0 - s);
        vec3 rgb = (u_strokeColor.rgb * s + fill.rgb * fill.a * (1.0 - s)) / max(a, 0.0001);
        col = vec4(rgb, a);
    }
    gl_FragColor = col * v_color;
}
