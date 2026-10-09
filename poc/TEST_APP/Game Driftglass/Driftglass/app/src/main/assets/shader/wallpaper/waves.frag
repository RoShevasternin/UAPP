void main(){
  vec2 p = cuv() + u_par * 0.03; float t = u_time * 0.25;
  float y = FC.y / u_res.y;
  vec3 col = mix(u_c4 * 1.2 + u_c2 * 0.06, u_c4 * 0.6, y);
  for (int i = 0; i < 6; i++){
    float fi = float(i);
    float h = 0.82 - fi * 0.115 + 0.045 * sin(p.x * (2.0 + fi * 0.6) * u_scale + t * (1.0 + fi * 0.25) + fi * 1.7 + u_seed)
            + 0.02 * sin(p.x * 7.0 * u_scale - t * 1.3 + fi * 2.1);
    float m = smoothstep(h + 0.003, h - 0.003, y);
    vec3 c = mix(u_c3, u_c1, fi / 5.0); c = mix(c, u_c2, 0.5 - 0.5 * cos(fi * 1.4));
    c *= 0.28 + 0.15 * fi;
    float edge = exp(-abs(y - h) * 140.0) * 0.55;
    col = mix(col, c * (0.8 + 0.4 * (h - y) * 3.0), m) + edge * mix(c, vec3(1.0), 0.4) * 0.5;
  }
  gl_FragColor = outc(grain(col));
}
