---
name: mindora-texturepacker-cli
description: "Mindora Self Test: never repack atlases from the TexturePacker CLI — it runs unlicensed and watermarks sprites; the user repacks in the GUI"
metadata:
  node_type: memory
  type: project
---

**Mindora Self Test (agust/Game T35).** `/Applications/TexturePacker.app/Contents/MacOS/TexturePacker`
runs in **Essential (lite) mode** — no license. Any command-line invocation that
passes a `.tps` counts as an advanced feature ("Commandline update of .tps
files") and bakes red "please purchase a license" watermarks into several
sprites. Passing explicit `--sheet`/`--data` does not avoid it.

**So: never repack `assets/all/assets.tps` from the shell.** Ask the user to
publish from the TexturePacker GUI instead — his GUI workflow produces clean
sheets.

**Also (2026-09-04): `app/src/main/assets/atlas/all.*` is tracked by git but the
working copy is often NEWER than HEAD** — he packs the atlas without committing.
Restoring it with `git show HEAD:...` therefore destroys freshly packed regions.
That is exactly how a pack of his was lost. Before touching those files, check
`git status`; if they are modified, back them up to the scratchpad first.

See [[workflow-direct-edits]].
