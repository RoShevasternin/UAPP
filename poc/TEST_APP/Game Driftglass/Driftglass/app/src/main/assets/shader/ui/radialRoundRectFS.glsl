#ifdef GL_ES
precision mediump float;
#endif

// ─────────────────────────────────────────────────────────────────────────────
// RADIAL ROUND RECT — заглушка обкладинки без арту (.cv.ph у прототипі):
//   radial-gradient(120% 90% at 25% 15%, c0, c1 60%, c2)
// Еліпс: радіуси u_radii (частки розміру), центр u_center (0..1, y — зверху).
// ─────────────────────────────────────────────────────────────────────────────

varying vec4 v_color;
varying vec2 v_localUV;

uniform vec2  u_size;
uniform float u_radius;
uniform float u_aa;
uniform vec2  u_center;
uniform vec2  u_radii;
uniform vec4  u_c0;
uniform vec4  u_c1;
uniform vec4  u_c2;

float roundedBox(vec2 p, vec2 halfSize, float r) {
    vec2 q = abs(p) - halfSize + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    vec2 p = (v_localUV - 0.5) * u_size;
    // радіус «999» = пігулка: обмежуємо половиною меншої сторони, інакше SDF ламається
    float d = roundedBox(p, u_size * 0.5, min(u_radius, min(u_size.x, u_size.y) * 0.5));
    float shape = 1.0 - smoothstep(-u_aa, 0.0, d);

    float t = length((v_localUV - u_center) / u_radii);
    vec4 c = t < 0.6 ? mix(u_c0, u_c1, t / 0.6) : mix(u_c1, u_c2, clamp((t - 0.6) / 0.4, 0.0, 1.0));
    gl_FragColor = vec4(c.rgb, c.a * shape) * v_color;
}
