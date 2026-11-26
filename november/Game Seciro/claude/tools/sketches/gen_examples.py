#!/usr/bin/env python3
# Начерки-приклади для нотаток прототипу (українською) → prototype/examples/<ключ>.svg
#   python3 claude/tools/sketches/gen_examples.py
import os, sys
HERE = os.path.dirname(os.path.abspath(__file__)); sys.path.insert(0, HERE)
import wf, flows
OUT = os.path.normpath(os.path.join(HERE, "..", "..", "..", "prototype", "examples")); os.makedirs(OUT, exist_ok=True)
KEYS = ["lock_a", "lock_b", "al_list", "al_empty", "help_sheet", "web_4", "web_vpn", "and_review", "gp_sheet", "kb_android", "notif_vpn", "upd_android", "onb_phone", "perm_denied", "qr_safe", "qr_warn", "qr_text", "qr_cam", "qr_err"]
W = wf.wireframes(); F = flows.flows()
for k in KEYS: open(os.path.join(OUT, k + ".svg"), "w").write(W[k][1]("uk"))
for k in ["fl_vpn", "fl_cam"]: open(os.path.join(OUT, k + ".svg"), "w").write(F[k][1]("uk"))
print(len(KEYS) + 1, "→", OUT)
