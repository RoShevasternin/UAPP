#ifdef GL_ES
precision mediump float;
#endif

// ─────────────────────────────────────────────────────────────────────────────
// MIP BLUR — фон плеєра (.pl-bg: blur(40px) saturate(1.4) brightness(.55)).
// Замість FBO-гауса беремо дрібний мип-рівень обкладинки (bias): текстури
// обкладинок і так з мипмапами, а розмиття 40 px на всю сторінку — це кілька
// пасів FBO щокадру. Тут — одна вибірка.
// ─────────────────────────────────────────────────────────────────────────────

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_bias;
uniform float u_saturation;
uniform float u_brightness;

void main() {
    vec4 c = texture2D(u_texture, v_texCoords, u_bias);
    float l = dot(c.rgb, vec3(0.299, 0.587, 0.114));
    vec3 rgb = mix(vec3(l), c.rgb, u_saturation) * u_brightness;
    gl_FragColor = vec4(rgb, c.a) * v_color;
}
