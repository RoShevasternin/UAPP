void main(){
  vec2 p = cuv() + u_par * 0.035; float t = u_time * 0.15;
  float y = FC.y / u_res.y;
  vec3 col = mix(u_c4 * 1.6 + u_c3 * 0.05, u_c4 * 0.55, y);
  for (int i = 0; i < 3; i++){
    float fi = float(i);
    float band = 0.06 + fi * 0.13 + (fbm(vec2(p.x * 1.2 * u_scale + fi * 3.1 + u_seed, t + fi)) - 0.5) * 0.55;
    float d = p.y - band;
    float glow = exp(-abs(d) * (4.0 + fi * 2.0)) * smoothstep(-0.55, 0.1, d + 0.2);
    float rays = 0.55 + 0.45 * fbm(vec2(p.x * 7.0 * u_scale + fi * 10.0, t * 2.0 + u_seed));
    vec3 c = i == 0 ? u_c1 : (i == 1 ? u_c2 : u_c3);
    col += c * glow * rays * 0.6;
  }
  col += u_c1 * 0.05 * smoothstep(0.3, -0.5, p.y);
  float st = step(0.996, hash(floor(FC * 0.5))) * smoothstep(0.0, 0.5, p.y) * 0.6;
  gl_FragColor = outc(grain(col + st));
}
