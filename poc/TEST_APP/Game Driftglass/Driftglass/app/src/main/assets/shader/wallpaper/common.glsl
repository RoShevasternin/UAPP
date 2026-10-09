// ─────────────────────────────────────────────────────────────────────────────
// Спільне для шейдерів шпалер Driftglass (GLSL ES 1.00 — той самий код, що в прототипі).
// Різниця з прототипом одна: там gl_FragCoord, тут FC — координата пікселя актора
// з v_texCoords (актор може стояти будь-де на екрані, а не лише на весь екран).
// WallpaperRenderer склеює: common.glsl + <style>.frag.
// ─────────────────────────────────────────────────────────────────────────────
#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#else
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform vec2 u_res;      // розмір актора в пікселях екрана
uniform float u_time;
uniform vec3 u_c1;
uniform vec3 u_c2;
uniform vec3 u_c3;
uniform vec3 u_c4;       // темна основа
uniform float u_scale;
uniform float u_grain;
uniform float u_seed;
uniform vec2 u_par;      // паралакс -1..1

#define FC (vec2(v_texCoords.x, 1.0 - v_texCoords.y) * u_res)

float hash(vec2 p){ p = fract(p * vec2(123.34, 456.21)); p += dot(p, p + 45.32); return fract(p.x * p.y); }
float noise(vec2 p){ vec2 i = floor(p), f = fract(p); vec2 u = f * f * (3.0 - 2.0 * f);
  return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), u.x), mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y); }
float fbm(vec2 p){ float v = 0.0, a = 0.5; for (int i = 0; i < 5; i++){ v += a * noise(p); p = p * 2.02 + vec2(17.1, 9.2); a *= 0.5; } return v; }
vec2 cuv(){ return (FC - 0.5 * u_res) / u_res.y; }
vec3 grain(vec3 c){ return c + (hash(FC + fract(u_time * 7.0) * 113.0) - 0.5) * u_grain; }
vec4 outc(vec3 c){ return vec4(c, v_color.a); }
