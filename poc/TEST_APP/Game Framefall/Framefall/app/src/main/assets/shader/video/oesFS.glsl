#extension GL_OES_EGL_image_external : require
// Кадр декодера (samplerExternalOES) × колір актора, з опційним заокругленням кутів.
#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_tc;
varying vec2 v_local;

uniform samplerExternalOES u_texture;
uniform vec2  u_size;    // розмір актора у world-юнітах
uniform float u_radius;  // радіус кутів у world-юнітах (0 — без заокруглення)
uniform float u_aa;      // ширина згладжування у world-юнітах (≈ 1 піксель)

void main() {
    vec4 c = texture2D(u_texture, v_tc);
    float a = 1.0;
    if (u_radius > 0.0) {
        vec2 p = (v_local - 0.5) * u_size;
        vec2 q = abs(p) - (u_size * 0.5 - vec2(u_radius));
        float d = length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - u_radius;
        a = 1.0 - smoothstep(-u_aa, u_aa, d);
    }
    gl_FragColor = vec4(c.rgb, 1.0) * v_color * vec4(1.0, 1.0, 1.0, a);
}
