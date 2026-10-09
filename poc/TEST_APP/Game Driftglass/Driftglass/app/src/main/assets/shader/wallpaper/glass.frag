void main(){
  vec2 p = cuv() * u_scale * 3.4 + u_par * 0.07; float t = u_time * 0.12;
  vec2 g = floor(p), f = fract(p);
  float d1 = 8.0, d2 = 8.0; vec2 id = vec2(0.0); vec2 bp = vec2(0.0);
  for (int j = -1; j <= 1; j++){ for (int i = -1; i <= 1; i++){
    vec2 o = vec2(float(i), float(j));
    vec2 h = vec2(hash(g + o + u_seed), hash(g + o + u_seed + 19.7));
    vec2 pt = o + 0.5 + 0.36 * sin(t + 6.2831 * h);
    float d = length(pt - f);
    if (d < d1){ d2 = d1; d1 = d; id = g + o; bp = pt; } else if (d < d2){ d2 = d; }
  } }
  float k = hash(id + u_seed * 3.1);
  vec3 c = k < 0.33 ? u_c1 : (k < 0.66 ? u_c2 : u_c3);
  float edge = d2 - d1;
  vec3 col = mix(u_c4, c, 0.72);
  col *= 0.45 + 0.75 * (1.0 - d1);
  col = mix(u_c4 * 0.5, col, smoothstep(0.015, 0.07, edge));
  col += smoothstep(0.1, 0.0, abs(edge - 0.08)) * 0.07;
  float spec = smoothstep(0.24, 0.0, length(f - bp - vec2(-0.13, 0.15)));
  col += spec * 0.28;
  gl_FragColor = outc(grain(col));
}
