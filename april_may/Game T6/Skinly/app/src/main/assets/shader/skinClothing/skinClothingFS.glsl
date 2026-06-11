#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;
varying vec2 v_localUV;

uniform sampler2D u_texture;    // персонаж (slot 0)
uniform sampler2D u_clothing;   // шар 1 — базовий одяг (slot 1)
uniform sampler2D u_clothing2;  // шар 2 — верхній одяг поверх першого (slot 2)

uniform vec3  u_keyColor;
uniform float u_tolerance;

uniform vec2  u_clothScale;
uniform vec2  u_clothOffset;

uniform vec2  u_cloth2Scale;
uniform vec2  u_cloth2Offset;

uniform float u_keepShading;
uniform float u_hasClothing;   // 0 = без одягу, 1 = є базовий
uniform float u_hasClothing2;  // 0 = без верхнього, 1 = є верхній

void main() {
    vec4 charColor = texture2D(u_texture, v_texCoords);

    // Без жодного одягу — персонаж як є
    if (u_hasClothing < 0.5 && u_hasClothing2 < 0.5) {
        gl_FragColor = charColor * v_color;
        return;
    }

    // ─── Детекція жовтої зони ───────────────────────────────────────────
    float dist       = distance(charColor.rgb, u_keyColor);
    float isClothing = 1.0 - step(u_tolerance, dist);

    // ─── Тіні ───────────────────────────────────────────────────────────
    float charLum = dot(charColor.rgb, vec3(0.299, 0.587, 0.114));
    float keyLum  = dot(u_keyColor,    vec3(0.299, 0.587, 0.114));
    float shade   = charLum / max(keyLum, 0.001);
    shade = mix(1.0, shade, u_keepShading);

    // ─── Шар 1: базовий одяг ────────────────────────────────────────────
    vec3 clothed = charColor.rgb;

    if (u_hasClothing > 0.5) {
        vec2 uv1   = v_localUV * u_clothScale + u_clothOffset;
        vec4 cloth1 = texture2D(u_clothing, uv1);
        // Заміна жовтої зони базовим одягом
        clothed = mix(clothed, cloth1.rgb * shade, isClothing * cloth1.a);
    }

    // ─── Шар 2: верхній одяг поверх першого ─────────────────────────────
    if (u_hasClothing2 > 0.5) {
        vec2 uv2    = v_localUV * u_cloth2Scale + u_cloth2Offset;
        vec4 cloth2 = texture2D(u_clothing2, uv2);
        // Поверх попереднього шару (в межах жовтої зони)
        clothed = mix(clothed, cloth2.rgb * shade, isClothing * cloth2.a);
    }

    // ─── Фінал ──────────────────────────────────────────────────────────
    gl_FragColor = vec4(clothed, charColor.a) * v_color;
}
