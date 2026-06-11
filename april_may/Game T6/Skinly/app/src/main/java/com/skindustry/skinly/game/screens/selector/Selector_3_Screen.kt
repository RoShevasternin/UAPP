package com.skindustry.skinly.game.screens.selector

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.skindustry.skinly.adsmodule.AdSizeManager
import com.skindustry.skinly.game.actors.button.AOrangeButton
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.selector.APanelSelector
import com.skindustry.skinly.game.screens.HomeScreen
import com.skindustry.skinly.game.utils.Block
import com.skindustry.skinly.game.utils.GLOBAL_listDesc
import com.skindustry.skinly.game.utils.GLOBAL_listSelectorItem_3
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.MAX_SELECTOR
import com.skindustry.skinly.game.utils.TIME_ANIM_SCREEN
import com.skindustry.skinly.game.utils.actor.animDelay
import com.skindustry.skinly.game.utils.actor.animHide
import com.skindustry.skinly.game.utils.actor.animShow
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.runGDX
import com.skindustry.skinly.util.log
import kotlinx.coroutines.launch

class Selector_3_Screen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter20 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(20)
    private val parameter14 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "Selected /")
        .setSize(14)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg     by lazy { Image(gdxGame.assetsAll.logo) }
    private val aDescLbl     by lazy { Label(GLOBAL_listDesc[2], FontFactory.create(this, parameter20, fontGenerator_SemiBold, Color.BLACK)) }
    private val aSelector    by lazy { APanelSelector(this) }
    private val aSelectedLbl by lazy { Label("Selected 0/$MAX_SELECTOR", FontFactory.create(this, parameter14, fontGenerator_SemiBold, GameColor.gray_818181)) }
    private val aNextBtn     by lazy { AOrangeButton(this, "Next") }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addLogoImg()
        addDescLbl()
        addSelector()
        addNextBtn()
        addSelectedLbl()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addLogoImg() {
        aLogoImg.setSize(124f, 47f)
        add(aLogoImg) { centerX(); topToTop(margin = 9f) }
    }

    private fun AConstraintLayout.addDescLbl() {
        aDescLbl.setSize(344f, 56f)
        add(aDescLbl) { centerX(); topToBottom(aLogoImg, 22f) }
        aDescLbl.setAlignment(Align.center)
        aDescLbl.wrap = true
    }

    private fun AConstraintLayout.addSelector() {
        aSelector.setSize(WIDTH, 1f)
        add(aSelector) { centerX(); topToBottom(aDescLbl, 16f) }

        aSelector.setTexts(GLOBAL_listSelectorItem_3)

        // Підписка на кількість вибраних
        coroutine?.launch {
            aSelector.selectedCountFlow.collect { count ->
                runGDX {
                    aSelectedLbl.setText("Selected $count/$MAX_SELECTOR")
                    if (count >= MAX_SELECTOR) aNextBtn.enable()
                    else aNextBtn.disable()
                }
            }
        }
    }

    private fun AConstraintLayout.addNextBtn() {
        aNextBtn.setSize(344f, 56f)
        add(aNextBtn) { centerX(); bottomToBottom(margin = 24f) }

        aNextBtn.disable()
        aNextBtn.setOnClickListener {
            animHideScreen { gdxGame.navigationManager.navigate(HomeScreen::class.java.name, Selector_3_Screen::class.java.name) }
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aNextBtn) { marginBottom += screen.adBottomUI }
                log("OnboardingScreen: marginBottom += ${screen.adBottomUI}")
            } }
        }
    }

    private fun AConstraintLayout.addSelectedLbl() {
        aSelectedLbl.setSize(344f, 22f)
        add(aSelectedLbl) { centerX(); bottomToTop(aNextBtn, 24f) }
        aSelectedLbl.setAlignment(Align.center)
    }

}