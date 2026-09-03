package com.selftest.mindora.game.actors.panel.home.main

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.progress.AProgressItemPortrait
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ─────────────────────────────────────────────────────────────────────────────
//  Картка портрета на хабі: прогрес по вимірах + замок.
//
//  ЗАМОК ЗНИКАЄ НА 5 З 5, а не на порозі синтезу з конфігу
//  (portrait.synthesisThreshold = 4). Свідоме розходження: замок — це
//  обіцянка «портрет твій повністю», і знімати його, коли одну грань ще не
//  відкрито, означало б збрехати. Синтез при цьому лишається доступним з 4 —
//  за це відповідає PortraitController, не картка.
//
//  Клік по ВСІЙ картці. stopEvent = false обов'язково: картка лежить у
//  ScrollPane хаба, і event.stop() обірвав би спливання, вбивши скрол.
// ─────────────────────────────────────────────────────────────────────────────
class APanelItemPortrait(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef by lazy {
        MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, GameColor.white_80)
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg       = Image(gdxGame.assetsAll.ITEM_PORTRAIT)
    private val aProgressLbl = AMsdfLabel("0 of 5 dimensions unlocked", styleDef)
    private val aProgress    = AProgressItemPortrait(screen)
    private val aLockImg     = Image(gdxGame.assetsAll.lock)

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    var onClick: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addProgressLbl()
        addProgress()
        addLockImg()

        setOnClickListener(stopEvent = false) { onClick() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBgImg() {
        aBgImg.setSize(362f, 121f)
        add(aBgImg) { center() }
    }

    private fun addProgressLbl() {
        aProgressLbl.setSize(165f, 14f)
        add(aProgressLbl) { startToStart(margin = 112f); topToTop(margin = 51f) }
    }

    private fun addProgress() {
        aProgress.setSize(175f, 5f)
        add(aProgress) { startToStart(margin = 112f); bottomToBottom(margin = 25f) }
    }

    private fun addLockImg() {
        aLockImg.setSize(23f, 29f)
        add(aLockImg) { startToStart(margin = 42f); bottomToBottom(margin = 40f) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * @param done  скільки тестів пройдено
     * @param total скільки всього (5)
     * @param animated false при першому біндінгу — інакше видно, як замок
     *        «прилітає» на екрані, який щойно з'явився
     */
    /**
     * @param done          скільки тестів пройдено
     * @param total         скільки всього
     * @param portraitOpen  чи ВІДКРИТИЙ портрет (синтез зроблено)
     *
     * ⚠️ ЗАМОК ЗАЛЕЖИТЬ ВІД ПОРТРЕТА, А НЕ ВІД ЛІЧИЛЬНИКА.
     *
     * Було `done >= total` — тобто замок зникав рівно на 5 з 5. Але поріг
     * синтезу задає менеджер у конфізі (portrait_synthesis_threshold), і при
     * значенні 3 виходило безглуздя: портрет уже відкритий і показує назву,
     * а на хабі досі висить замок.
     *
     * Тепер джерело одне — сам факт синтезу. Поставлять поріг 2 чи 5,
     * картка буде права в обох випадках, і міняти тут нічого не треба.
     */
    fun bind(done: Int, total: Int, portraitOpen: Boolean, animated: Boolean = true) {
        aProgressLbl.setText("$done of $total dimensions unlocked")
        aProgress.progressPercentFlow.value = if (total > 0) done * 100f / total else 0f

        val t = if (animated) 0.2f else 0f
        if (portraitOpen) aLockImg.animHide(t) else aLockImg.animShow(t)
    }
}