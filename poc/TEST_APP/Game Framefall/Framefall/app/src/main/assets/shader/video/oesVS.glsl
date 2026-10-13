// Відео з SurfaceTexture: координати текстури проходять через матрицю декодера
// (u_stMatrix: віддзеркалення / кроп / поворот). v_local — 0..1 по актору для заокруглення кутів.
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform mat4 u_stMatrix;
uniform vec4 u_uvRect;   // u, v, u2, v2 — вікно кадру (cover-кроп)

varying vec4 v_color;
varying vec2 v_tc;
varying vec2 v_local;

void main() {
    v_color = a_color;
    v_color.a = v_color.a * (255.0 / 254.0);
    v_local = (a_texCoord0 - u_uvRect.xy) / (u_uvRect.zw - u_uvRect.xy);
    v_tc = (u_stMatrix * vec4(a_texCoord0, 0.0, 1.0)).xy;
    gl_Position = u_projTrans * a_position;
}
