#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform sampler2D u_texture;    // Основна текстура
uniform sampler2D u_mask;       // Текстура маски

void main() {
    vec4 maskColor = texture2D(u_mask, vec2(v_texCoords.x, 1.0 - v_texCoords.y)); //texture2D(u_mask, v_texCoords);
    vec4 texColor  = texture2D(u_texture, v_texCoords);

    // Маскуємо альфу
    //texColor.a *= maskColor.a;

    //// Якщо premultiplied alpha — множимо RGB на альфу
    ////texColor.rgb *= texColor.a;

    // Застосовуємо колір від batch (v_color)
    //gl_FragColor = texColor * v_color;

    texColor.rgb *= maskColor.a; // ← важливо! множимо кольори, щоб приглушити їх на прозорих ділянках маски
    texColor.a *= maskColor.a;

    gl_FragColor = texColor * v_color;
}
