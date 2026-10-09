#ifdef GL_ES
precision mediump float;
#endif

// ─────────────────────────────────────────────────────────────────────────────
// ROUND IMAGE — текстура з заокругленими кутами (обкладинки, банери).
// Альфа = SDF заокругленого прямокутника, радіус у world-юнітах (як roundRectFS).
// «cover»-кроп робить TextureRegion, тут лише маска.
// ─────────────────────────────────────────────────────────────────────────────

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_localUV;

uniform sampler2D u_texture;
uniform vec2  u_size;
uniform float u_radius;
uniform float u_aa;

float roundedBox(vec2 p, vec2 halfSize, float r) {
    vec2 q = abs(p) - halfSize + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    vec2 p = (v_localUV - 0.5) * u_size;
    // радіус «999» = пігулка: обмежуємо половиною меншої сторони, інакше SDF ламається
    float d = roundedBox(p, u_size * 0.5, min(u_radius, min(u_size.x, u_size.y) * 0.5));
    float shape = 1.0 - smoothstep(-u_aa, 0.0, d);
    vec4 c = texture2D(u_texture, v_texCoords) * v_color;
    gl_FragColor = vec4(c.rgb, c.a * shape);
}
