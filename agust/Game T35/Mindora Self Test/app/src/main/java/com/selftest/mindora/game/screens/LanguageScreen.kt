package com.selftest.mindora.game.screens

import com.selftest.mindora.game.actors.AScrollPane
import com.selftest.mindora.game.actors.checkbox.base.ACheckBoxGroup
import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.panel.APanelTop
import com.selftest.mindora.game.actors.settings.ACardLanguage
import com.selftest.mindora.game.content.LanguageCatalog
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.TIME_ANIM_SCREEN
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  LanguageScreen — вибір мови зі списку.
//
//  РАДІО, А НЕ НАБІР ГАЛОЧОК: усі рядки в одному ACheckBoxGroup, тож знімати
//  попередній вибір вручну не треба — група робить це сама.
//
//  ЗБЕРІГАЄМО ОДРАЗУ, без кнопки «зберегти»: у списку з одним вибором
//  підтверджувати нічого — тап уже і є рішенням. Settings підписаний на той
//  самий флоу, тому чіп там міняється сам, ще до того як юзер вийде назад.
//
//  ⚠️ ВІДНОВЛЕННЯ ВИБОРУ — ТИХО (invokeBlock = false). Інакше «намалювати
//  збережену мову» виглядало б для екрана як новий тап користувача і одразу
//  перезаписувало б стан на самого себе.
// ═════════════════════════════════════════════════════════════════════════════
class LanguageScreen : AdvancedScreen() {

    companion object {
        private const val ROW_GAP = 8f
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop by lazy { APanelTop(this) }

    private val aContent by lazy {
        AAutoLayout(
            screen     = this,
            direction  = AAutoLayout.Direction.VERTICAL,
            gapMain    = ROW_GAP,
            sizingH    = AAutoLayout.Sizing.HUG,
            alignCross = AAutoLayout.AlignCross.CENTER,
        )
    }
    private val aScrollPane by lazy { AScrollPane(aContent) }

    private val aCards = mutableListOf<ACardLanguage>()

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val checkGroup = ACheckBoxGroup()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addContent()
        restoreSelection()
    }

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 48f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }
        aPanelTop.setTitle("Language")
    }

    private fun AConstraintLayout.addContent() {
        aContent.width = ACardLanguage.W

        // matchHeight() обов'язковий: два вертикальні якорі самі по собі
        // висоту не задають — heightMode за замовчуванням FIXED.
        aScrollPane.setSize(ACardLanguage.W, 1f)
        add(aScrollPane) {
            matchHeight()
            centerX()
            topToBottom(aPanelTop, 16f)
            bottomToBottom(margin = 16f)
        }

        LanguageCatalog.ALL.forEachIndexed { i, entry ->
            val card = ACardLanguage(this@LanguageScreen)
            card.setSize(ACardLanguage.W, ACardLanguage.H)
            aContent.add(card)
            card.bind(i, entry)

            card.checkBoxGroup = checkGroup
            card.setOnCheckListener { isCheck -> if (isCheck) selectLanguage(entry) }

            aCards += card
        }

        aContent.invalidate()
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun restoreSelection() {
        val index = LanguageCatalog.indexOf(gdxGame.modelPlayer.languageId())
        aCards.getOrNull(index)?.let { checkGroup.select(it, invokeBlock = false) }
    }

    private fun selectLanguage(entry: LanguageCatalog.Entry) {
        gdxGame.modelPlayer.setLanguageId(entry.id)
    }
}
