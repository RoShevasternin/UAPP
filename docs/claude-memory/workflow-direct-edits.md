---
name: workflow-direct-edits
description: "For the Mindora Self Test project: edit files directly in the working copy, then build, install and screenshot on the device — never hand back files to paste"
metadata:
  node_type: memory
  type: feedback
  originSessionId: 8922f1d9-a419-4eb6-90c8-2dd1a2abb9fa
  modified: 2026-09-04T00:00:00.000Z
---

**UAPP, з 01.10.2026: замінено на сторінки-патчі — див. [[uapp-patch-page-flow]].** Нижче — старе правило; з нього лишається урок: усе, що даємо на вибір, спершу зібрано й перевірено на девайсі в лабораторії, і нічого не кладемо всередину source root.

**Old rule: edit the project directly.** Change files in place, then build,
install and screenshot on the connected device to prove it works. Show a
`git diff` after each step; he reviews the full diff in GitHub Desktop before
committing (I never run git commands).

**Why:** anything handed back or staged outside the compile path cannot be
verified — no compile check, no run, no screenshot. He has now landed on this
three times, the last one (2026-09-04) explicitly reversing a hand-back
experiment he had asked for himself an hour earlier: «давай ти роби все
змінюй встівляй створюй що б працювало і тестуй». Do not propose paste-back
or a staging folder again.

**History:** an earlier attempt put generated code in
`app/src/main/java/aaa/` — inside the source root, so a staged `.kt` collided
with the real class ("Redeclaration") and broke `assembleDebug` until a gradle
`exclude` was added; once excluded the file was dead text. That is the concrete
failure this rule prevents.

Personal (non-work) questions he takes to the browser Claude chat instead of
Claude Code. See [[uapp-employer-and-repo]].
