void main(){
  vec2 p = cuv() * u_scale * 1.7 + u_par * 0.05; float t = u_time * 0.07;
  vec2 q = vec2(fbm(p + vec2(0.0, t) + u_seed), fbm(p + vec2(5.2, 1.3) - t));
  vec2 r = vec2(fbm(p + 3.0 * q + vec2(1.7, 9.2) + t * 1.5), fbm(p + 3.0 * q + vec2(8.3, 2.8) - t));
  float f = fbm(p + 3.0 * r);
  vec3 col = mix(u_c4, u_c1, clamp(f * f * 2.4, 0.0, 1.0));
  col = mix(col, u_c2, clamp(length(q) * 0.95 - 0.25, 0.0, 1.0));
  col = mix(col, u_c3, clamp(r.x * r.x * 1.5, 0.0, 1.0) * 0.75);
  col *= 0.68 + 0.62 * f;
  col += u_c1 * 0.06;
  gl_FragColor = outc(grain(col));
}
