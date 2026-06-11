package com.skindustry.skinly.game.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.APanelTopHomeSelect
import com.skindustry.skinly.game.actors.panel.homeSelect.APanelCharacterCards
import com.skindustry.skinly.game.actors.panel.homeSelect.SkinRepository
import com.skindustry.skinly.game.screens.state.StateUnlockPopup
import com.skindustry.skinly.game.actors.popup.APopupUnlock
import com.skindustry.skinly.game.utils.Block
import com.skindustry.skinly.game.utils.GLOBAL_selectedHomeType
import com.skindustry.skinly.game.utils.GLOBAL_selectedPersonageIndex
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.TIME_ANIM_SCREEN
import com.skindustry.skinly.game.utils.actor.animDelay
import com.skindustry.skinly.game.utils.actor.animHide
import com.skindustry.skinly.game.utils.actor.animHideAndDisable
import com.skindustry.skinly.game.utils.actor.animShow
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.screenState.ScreenStateMachine

class HomeSelectScreen : AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop   by lazy { APanelTopHomeSelect(this) }
    private val aCards by lazy { APanelCharacterCards(this) }

    private val aDim         by lazy { Image(drawerUtil.getTexture(GameColor.black_80)) }
    private val aPopupUnlock by lazy { APopupUnlock(this) }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateMachine = ScreenStateMachine()

    private val stateUnlockPopup by lazy { StateUnlockPopup(stateMachine, aPopupUnlock, aDim) }

    // Переходи — викликаєш з будь-якого місця
    private fun goToUnlockPopup() {
        stateMachine.pushState(stateUnlockPopup)
    }

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
        addTop()
        addCards()

        addDim()
        addUnlockPopup()
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

    private fun AConstraintLayout.addTop() {
        aTop.setSize(WIDTH, 60f)
        add(aTop) { centerX(); topToTop() }

        aTop.setTitle(GLOBAL_selectedHomeType.title)
        aTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addCards() {
        aCards.width = WIDTH
        add(aCards) {
            centerX(); topToBottom(aTop); bottomToBottom()
            matchHeight()
        }

        // Завантажуємо карточки для поточного типу
        val textures = SkinRepository.getCards(GLOBAL_selectedHomeType)
        val unlocked = gdxGame.modelPlayer.getUnlocked(GLOBAL_selectedHomeType)
        aCards.setCards(textures, unlocked)

        // Відкрита карточка — перехід на екран редактора
        aCards.onOpen = { index ->
            GLOBAL_selectedPersonageIndex = index
            animHideScreen { gdxGame.navigationManager.navigate(PersonalizationScreen::class.java.name, HomeSelectScreen::class.java.name) }
        }

        // Закрита карточка — показати діалог розблокування
        aCards.onLocked = { index ->
            val textureCard = SkinRepository.getCards(GLOBAL_selectedHomeType)[index]
            val aBgImg      = Image(gdxGame.assetsAll.MINI_CARD)
            val aImg        = Image(textureCard)

            aBgImg.setSize(86f, 86f)
            aImg.setSize(86f, 86f)

            aPopupUnlock.add(aBgImg) { centerX(); topToTop(margin = 107f) }
            aPopupUnlock.add(aImg) { centerX(); topToTop(margin = 107f) }

            stateUnlockPopup.onWatch = {
                gdxGame.activity.showInterstitial {
                    // Після підтвердження:
                    gdxGame.modelPlayer.unlockCard(GLOBAL_selectedHomeType, index)
                    aCards.unlock(index)
                }
            }


            goToUnlockPopup()
        }
    }

    private fun AConstraintLayout.addDim() {
        add(aDim) { fillParent() }
        aDim.animHideAndDisable()
    }

    private fun AConstraintLayout.addUnlockPopup() {
        aPopupUnlock.setSize(344f, 334f)
        add(aPopupUnlock) { center(); verticalBias = 0.7f }
        aPopupUnlock.animHideAndDisable()
    }
}