package com.skindustry.skinly.game.screens

import com.skindustry.skinly.businesModule.backend.Bt
import com.skindustry.skinly.businesModule.backend.Events
import com.skindustry.skinly.businesModule.economy.Econ
import com.skindustry.skinly.businesModule.economy.Wallet
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

    override val analyticsBt    = Bt.CATALOG
    override val analyticsBlock = "home_select_screen"

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

    private val stateUnlockPopup by lazy {
        StateUnlockPopup(stateMachine, aPopupUnlock, aDim).apply {
            // Нативка перекривала низ попапа — ховаємо на час діалогу, потім повертаємо
            onOpened = { gdxGame.activity.hideNative() }
            onClosed = { showNative() }
        }
    }

    // Переходи — викликаєш з будь-якого місця
    private fun goToUnlockPopup() {
        // Центр вільної зони над рекламою: попап (398) вищий за старий, і з
        // фіксованим bias його низ (Cancel) ховався під нативкою/банером
        rootConstraintLayout.update(aPopupUnlock) { marginBottom = adBannerUI.coerceAtLeast(0f) }   // нативка на час попапа схована
        stateMachine.pushState(stateUnlockPopup)
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        showNative()

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    private fun showNative() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)
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
            // Прибираємо попереднє прев'ю — інакше вони складались стопкою
            popupBgImg?.remove()
            popupImg?.remove()

            val textureCard = SkinRepository.getCards(GLOBAL_selectedHomeType)[index]
            val aBgImg      = Image(gdxGame.assetsAll.MINI_CARD).also { popupBgImg = it }
            val aImg        = Image(textureCard).also { popupImg = it }

            aBgImg.setSize(86f, 86f)
            aImg.setSize(86f, 86f)

            aPopupUnlock.add(aBgImg) { centerX(); topToTop(margin = 107f) }
            aPopupUnlock.add(aImg) { centerX(); topToTop(margin = 107f) }

            bindUnlock {
                gdxGame.modelPlayer.unlockCard(GLOBAL_selectedHomeType, index)
                aCards.unlock(index)
            }


            goToUnlockPopup()
        }
    }

    private var popupBgImg: Image? = null
    private var popupImg  : Image? = null

    // ------------------------------------------------------------------------
    // Unlock: реклама або монети
    // ------------------------------------------------------------------------
    // Ціна з Econ (ключ = analyticsBlock): підпис на кнопці і списання — одне
    // число. Wallet.spend сам відмовляє при нестачі і в мінус не йде.
    private fun bindUnlock(unlock: () -> Unit) {
        val price = Econ.price(analyticsBlock, UNLOCK_PRICE_DEF)
        aPopupUnlock.setPrice(price)

        stateUnlockPopup.onWatch = {
            gdxGame.activity.showInterstitial {
                unlock()
                Events.featureComplete(bt = analyticsBt, block = analyticsBlock)
            }
        }
        stateUnlockPopup.onCoins = {
            if (Wallet.spend(price, bt = analyticsBt, block = analyticsBlock)) {
                unlock()
                Events.featureComplete(bt = analyticsBt, block = analyticsBlock, amount = price)
                true
            } else {
                gdxGame.activity.showToast("Not enough coins — you need $price")
                false
            }
        }
    }

    companion object {
        // Ціна розблокування за монети (альтернатива рекламі). Сервер міняє
        // через economy.prices без релізу.
        private const val UNLOCK_PRICE_DEF = 100
    }

    private fun AConstraintLayout.addDim() {
        add(aDim) { fillParent() }
        aDim.animHideAndDisable()
    }

    private fun AConstraintLayout.addUnlockPopup() {
        aPopupUnlock.setSize(APopupUnlock.WIDTH, APopupUnlock.HEIGHT)
        add(aPopupUnlock) { center() }
        aPopupUnlock.animHideAndDisable()
    }
}