#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;

// Центр кола у нормалізованих координатах маски (0..1)
uniform vec2  u_center;
// Радіус кола у нормалізованих координатах (наприклад 0.05 = 5% від розміру)
uniform float u_radius;

void main() {
    float dist = distance(v_texCoords, u_center);

    // М'який край кола — smoothstep дає "coin scratch" ефект
    // innerEdge = починаємо fade з 90% від радіусу
    float innerEdge = u_radius * 0.9;
    float alpha = 1.0 - smoothstep(innerEdge, u_radius, dist);

    // Записуємо тільки в R канал — він буде mask.r у scratchRevealFS
    gl_FragColor = vec4(alpha, alpha, alpha, alpha);
}
