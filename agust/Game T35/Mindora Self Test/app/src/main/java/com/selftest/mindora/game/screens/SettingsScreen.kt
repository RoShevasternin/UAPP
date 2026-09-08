package com.selftest.mindora.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.checkbox.base.ACheckBox
import com.selftest.mindora.game.actors.checkbox.base.ACheckBoxStyles
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.panel.APanelTop
import com.selftest.mindora.game.actors.settings.ACardSettings
import com.selftest.mindora.game.actors.settings.AChipLanguage
import com.selftest.mindora.game.content.LanguageCatalog
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.TIME_ANIM_SCREEN
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.runGDX
import kotlinx.coroutines.launch

// ═════════════════════════════════════════════════════════════════════════════
//  SettingsScreen — Language · Sound Effect · Privacy Policy.
//
//  БЕЗ СКРОЛУ: рядків рівно три, вони фіксовані й ніколи не виростуть за
//  екран. ScrollPane тут був би зайвим шаром, який ще й ловить драг.
//
//  ЗВУК ЖИВЕ В ДВОХ МІСЦЯХ, І ОБИДВА ПОТРІБНІ:
//    gdxGame.settings.IS_SOUND — негайний ефект (це фасад над SoundUtil.isPause)
//    modelPlayer.setSoundOn()  — щоб вибір пережив перезапуск
//  Джерело правди для ГАЛОЧКИ — модель, а не SoundUtil: SoundUtil створюється
//  ліниво, і на момент відкриття екрана його могло ще не бути.
// ═════════════════════════════════════════════════════════════════════════════
class SettingsScreen : AdvancedScreen() {

    companion object {
        private const val ROW_GAP  = 12f
        private const val TOP_GAP  = 32f

        /** Тумблер і шеврон — регіони з атласа, спаковані в 3×. */
        private const val TOGGLE_W = 34f
        private const val TOGGLE_H = 20f
        private const val CHEVRON  = 24f

        private const val NOTE =
            "Personal Portrait is for entertainment and self-reflection. " +
                    "It is not a medical or psychological diagnosis"
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val styleNote by lazy {
        MsdfStyle(gdxGame.msdfManager, gdxGame.msdfManager.fontMontserrat_Regular, 11f, GameColor.white_70)
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop by lazy { APanelTop(this) }

    private val aLanguageCard by lazy { ACardSettings(this) }
    private val aSoundCard    by lazy { ACardSettings(this) }
    private val aPrivacyCard  by lazy { ACardSettings(this) }

    private val aLanguageChip by lazy { AChipLanguage(this) }
    private val aSoundBox     by lazy { ACheckBox(this, ACheckBoxStyles.BOX) }
    private val aChevronImg   by lazy { Image(gdxGame.assetsAll.chevrone_right) }

    private val aNoteLbl by lazy { AMsdfLabel(NOTE, styleNote) }

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
        addLanguageCard()
        addSoundCard()
        addPrivacyCard()
        addNoteLbl()
    }

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 48f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }
        aPanelTop.setTitle("Settings")
    }

    private fun AConstraintLayout.addLanguageCard() {
        aLanguageCard.setSize(ACardSettings.W, ACardSettings.H)
        add(aLanguageCard) { centerX(); topToBottom(aPanelTop, TOP_GAP) }
        aLanguageCard.setTitle("Language")

        // Ширина чіпа плаває від назви мови, тому вона рахується в setLanguage,
        // а не задається тут.
        aLanguageChip.setLanguage(LanguageCatalog.byId(gdxGame.modelPlayer.languageId()).name)
        aLanguageCard.setRight(aLanguageChip, aLanguageChip.width, AChipLanguage.H)

        aLanguageCard.onClick = {
            animHideScreen {
                gdxGame.navigationManager.navigate(
                    LanguageScreen::class.java.name,
                    SettingsScreen::class.java.name,   // назад — сюди ж
                )
            }
        }

        collectLanguage()
    }

    private fun AConstraintLayout.addSoundCard() {
        aSoundCard.setSize(ACardSettings.W, ACardSettings.H)
        add(aSoundCard) { centerX(); topToBottom(aLanguageCard, ROW_GAP) }
        aSoundCard.setTitle("Sound Effect")

        aSoundCard.setRight(aSoundBox, TOGGLE_W, TOGGLE_H)
        renderSound(gdxGame.modelPlayer.isSoundOn())

        aSoundCard.onClick = { toggleSound() }
    }

    private fun AConstraintLayout.addPrivacyCard() {
        aPrivacyCard.setSize(ACardSettings.W, ACardSettings.H)
        add(aPrivacyCard) { centerX(); topToBottom(aSoundCard, ROW_GAP) }
        aPrivacyCard.setTitle("Privacy Policy")

        aPrivacyCard.setRight(aChevronImg, CHEVRON, CHEVRON)
        aPrivacyCard.onClick = { gdxGame.activity.openPrivacyPolicy() }
    }

    private fun AConstraintLayout.addNoteLbl() {
        aNoteLbl.setSize(ACardSettings.W, 34f)
        add(aNoteLbl) { centerX(); topToBottom(aPrivacyCard, 10f) }
        aNoteLbl.setAlignment(Align.topLeft)
        aNoteLbl.setWrap(true)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun toggleSound() {
        val on = !gdxGame.modelPlayer.isSoundOn()

        gdxGame.modelPlayer.setSoundOn(on)   // переживе перезапуск
        gdxGame.settings.IS_SOUND = on       // діє негайно

        renderSound(on)
    }

    /** invokeBlock = false: слухача на чекбоксі немає, стан жене екран. */
    private fun renderSound(on: Boolean) {
        if (on) aSoundBox.check(invokeBlock = false) else aSoundBox.uncheck(invokeBlock = false)
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------
    /**
     * Мова прилітає з LanguageScreen через модель, а не через навігацію: так
     * чіп оновлюється однаково і коли юзер повернувся назад, і якби мову
     * колись міняли з іншого місця.
     */
    private fun collectLanguage() {
        coroutine?.launch {
            gdxGame.modelPlayer.languageIdFlow.collect { id ->
                runGDX {
                    aLanguageChip.setLanguage(LanguageCatalog.byId(id).name)
                    // Чіп прибитий до правого краю, ширина змінилась — рядку
                    // треба перерахувати констрейнти.
                    aLanguageCard.invalidate()
                }
            }
        }
    }
}
