void main(){
  vec2 uv = FC / u_res + u_par * 0.02; float t = u_time * 0.2;
  uv += 0.07 * vec2(fbm(uv * 3.0 * u_scale + t), fbm(uv * 3.0 * u_scale - t + 4.0)) - 0.035;
  vec2 a = vec2(0.25 + 0.2 * sin(t * 0.9 + u_seed), 0.25 + 0.18 * cos(t * 0.7));
  vec2 b = vec2(0.78 + 0.18 * cos(t * 0.8), 0.32 + 0.2 * sin(t * 1.1 + u_seed));
  vec2 c = vec2(0.3 + 0.2 * cos(t * 1.2), 0.8 + 0.12 * sin(t * 0.6));
  vec2 d = vec2(0.75 + 0.15 * sin(t * 0.5 + u_seed), 0.62 + 0.2 * cos(t * 0.9));
  float wa = 1.0 / (pow(length(uv - a), 2.0) + 0.02), wb = 1.0 / (pow(length(uv - b), 2.0) + 0.02);
  float wc = 1.0 / (pow(length(uv - c), 2.0) + 0.02), wd = 1.0 / (pow(length(uv - d), 2.0) + 0.02);
  vec3 col = (u_c1 * wa + u_c2 * wb + u_c3 * wc + u_c4 * wd * 1.4) / (wa + wb + wc + wd * 1.4);
  gl_FragColor = outc(grain(col));
}
