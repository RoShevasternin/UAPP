#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;
varying vec2 v_localUV;   // 0..1 в межах спрайту (від BATCH_VERT)

uniform sampler2D u_texture;  // картка (те що ховаємо)
uniform sampler2D u_mask;     // маска подряпин (чорна = закрита, біла = подряпана)

void main() {
    vec4 tex      = texture2D(u_texture, v_texCoords);

    // Маска у локальних координатах 0..1
    float scratched = texture2D(u_mask, v_localUV).r;

    // smoothstep: м'який перехід між закритою і відкритою зонами
    // Дає плавний край "монетки" як в реальному scratch card
    float alpha = tex.a * (1.0 - smoothstep(0.3, 0.7, scratched));

    gl_FragColor = vec4(tex.rgb, alpha) * v_color;
}
