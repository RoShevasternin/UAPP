package com.skindustry.skinly.game.screens

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.skindustry.skinly.adsmodule.AdSizeManager
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.homeSelect.SkinRepository
import com.skindustry.skinly.game.actors.panel.personalization.ABottom
import com.skindustry.skinly.game.screens.state.StateUnlockPopup
import com.skindustry.skinly.game.actors.panel.personalization.ACharacterSkin
import com.skindustry.skinly.game.actors.panel.personalization.AFilterItem
import com.skindustry.skinly.game.actors.panel.personalization.APanelFilter
import com.skindustry.skinly.game.actors.panel.personalization.APanelStickerCards
import com.skindustry.skinly.game.actors.panel.personalization.APanelTextureCards
import com.skindustry.skinly.game.actors.panel.personalization.APanelTopPersonalization
import com.skindustry.skinly.game.actors.popup.APopupUnlock
import com.skindustry.skinly.game.model.PlayerModel
import com.skindustry.skinly.game.screens.state.personalization.StateStickerTab
import com.skindustry.skinly.game.screens.state.personalization.StateTextureTab
import com.skindustry.skinly.game.utils.Block
import com.skindustry.skinly.game.utils.GLOBAL_selectedHomeType
import com.skindustry.skinly.game.utils.GLOBAL_selectedPersonageIndex
import com.skindustry.skinly.game.utils.GLOBAL_sharedSkinPath
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.TIME_ANIM_SCREEN
import com.skindustry.skinly.game.utils.actor.animDelay
import com.skindustry.skinly.game.utils.actor.animHide
import com.skindustry.skinly.game.utils.actor.animHideAndDisable
import com.skindustry.skinly.game.utils.actor.animShow
import com.skindustry.skinly.game.utils.actor.disable
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter
import com.skindustry.skinly.game.utils.gdxGame
import com.skindustry.skinly.game.utils.runGDX
import com.skindustry.skinly.game.utils.screenState.ScreenStateMachine
import kotlinx.coroutines.launch
import java.io.File

class PersonalizationScreen : AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterFilterItem = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val labelStyleFilterItem by lazy { FontFactory.create(this, parameterFilterItem, fontGenerator_SemiBold, Color.BLACK) }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop         by lazy { APanelTopPersonalization(this) }
    private val aCharSkin    by lazy { ACharacterSkin(this, SkinRepository.getCards(GLOBAL_selectedHomeType)[GLOBAL_selectedPersonageIndex]) }
    private val aBottomPanel by lazy { ABottom(this) }

    private val aPanelFilterTexture by lazy { APanelFilter(this) }
    private val aPanelFilterSticker by lazy { APanelFilter(this) }

    private val aCardPanelsTexture: Map<PlayerModel.TextureType, APanelTextureCards> by lazy { PlayerModel.TextureType.entries.associateWith { APanelTextureCards(this) } }
    private val aCardPanelsSticker: Map<PlayerModel.StickerType, APanelStickerCards> by lazy { PlayerModel.StickerType.entries.associateWith { APanelStickerCards(this) } }

    private val aDim         by lazy { Image(drawerUtil.getTexture(GameColor.black_80)) }
    private val aPopupUnlock by lazy { APopupUnlock(this) }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateMachine = ScreenStateMachine()

    private val stateUnlockPopup by lazy { StateUnlockPopup(stateMachine, aPopupUnlock, aDim) }

    private val stateTextureTab by lazy {
        StateTextureTab(stateMachine, aPanelFilterTexture, aCardPanelsTexture[PlayerModel.TextureType.SOLID]!!)
    }
    private val stateStickerTab by lazy {
        StateStickerTab(stateMachine, aPanelFilterSticker, aCardPanelsSticker[PlayerModel.StickerType.FUN]!!)
    }

    // Переходи — викликаєш з будь-якого місця
    private fun goToUnlockPopup() {
        stateMachine.pushState(stateUnlockPopup)
    }

    private fun goToTextureTab() { stateMachine.setState(stateTextureTab) }
    private fun goToStickerTab() { stateMachine.setState(stateStickerTab) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        addCharSkin()
        addBottomPanel()

        addPanelFilterTexture()
        addPanelFilterSticker()

        addAllCardPanelsTexture()
        addAllCardPanelsSticker()

        addDim()
        addUnlockPopup()

        aBottomPanel.check(ABottom.Type.TEXTURE)
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
        aTop.setSize(WIDTH, 56f)
        add(aTop) { centerX(); topToTop() }

        aTop.onBack   = { animHideScreen { gdxGame.navigationManager.back() } }
        aTop.onEraser = { aCharSkin.clearClothing() }
        aTop.onShare  = { handleShare() }
    }

    private fun AConstraintLayout.addCharSkin() {
        aCharSkin.disable()

        aCharSkin.setSize(483f, 483f)
        add(aCharSkin) { centerX(); topToTop(aTop, -61f) }

        //aCharSkin.setClothing(drawerUtil.getTexture(Color.WHITE))
    }

    private fun AConstraintLayout.addBottomPanel() {
        aBottomPanel.height = 74f
        add(aBottomPanel) {
            centerX(); bottomToBottom()
            matchWidth()
        }

        aBottomPanel.onTabChanged = { type ->
            when(type) {
                ABottom.Type.TEXTURE -> goToTextureTab()
                ABottom.Type.STICKER -> goToStickerTab()
            }
        }

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { update(aBottomPanel) { marginBottom += screen.adBottomUI } } }
        }
    }

    private fun AConstraintLayout.addPanelFilterTexture() {
        aPanelFilterTexture.setSize(WIDTH, 68f)
        add(aPanelFilterTexture) { centerX(); bottomToBottom(aCharSkin, -10f) }
        aPanelFilterTexture.animHideAndDisable()

        val types = PlayerModel.TextureType.entries
        val items = List(types.size) { AFilterItem(this@PersonalizationScreen, labelStyleFilterItem) }
        aPanelFilterTexture.setFilterItems(items)

        types.forEachIndexed { index, type ->
            items[index].setText(types[index].label)
            items[index].onCheck = {
                //if (it) stateMachine.setState(cardStates[type]!!)
                (stateMachine.getCurrentState() as? StateTextureTab)?.showCards(aCardPanelsTexture[type]!!)
            }
        }
    }

    private fun AConstraintLayout.addPanelFilterSticker() {
        aPanelFilterSticker.setSize(WIDTH, 68f)
        add(aPanelFilterSticker) { centerX(); bottomToBottom(aCharSkin, -10f) }
        aPanelFilterSticker.animHideAndDisable()

        val types = PlayerModel.StickerType.entries
        val items = List(types.size) { AFilterItem(this@PersonalizationScreen, labelStyleFilterItem) }
        aPanelFilterSticker.setFilterItems(items)

        types.forEachIndexed { index, type ->
            items[index].setText(types[index].label)
            items[index].onCheck = {
                //if (it) stateMachine.setState(cardStates[type]!!)
                (stateMachine.getCurrentState() as? StateStickerTab)?.showCards(aCardPanelsSticker[type]!!)
            }
        }
    }

    private fun AConstraintLayout.addAllCardPanelsTexture() {
        PlayerModel.TextureType.entries.forEach { type ->
            val panel = aCardPanelsTexture[type]!!

            panel.width = WIDTH
            add(panel) {
                centerX(); topToBottom(aPanelFilterTexture); bottomToTop(aBottomPanel)
                matchHeight()
            }
            panel.animHideAndDisable()

            // Завантажуємо картки
            panel.setCards(type.textures, gdxGame.modelPlayer.getUnlockedTexture(type))

            // Відкрита картка — вдягаємо одяг
            panel.onOpen = { index ->
                aCharSkin.setClothing(type.textures[index], 10f)
            }

            // Закрита картка — попап розблокування
            panel.onLocked = { index ->
                showUnlockPopupTexture(type, index, panel)
            }
        }
    }

    private fun AConstraintLayout.addAllCardPanelsSticker() {
        PlayerModel.StickerType.entries.forEach { type ->
            val panel = aCardPanelsSticker[type]!!

            panel.width = WIDTH
            add(panel) {
                centerX(); topToBottom(aPanelFilterSticker); bottomToTop(aBottomPanel)
                matchHeight()
            }
            panel.animHideAndDisable()

            // Завантажуємо картки
            panel.setCards(type.textures, gdxGame.modelPlayer.getUnlockedSticker(type))

            // Відкрита картка — вдягаємо одяг
            panel.onOpen = { index ->
                aCharSkin.setClothing2(type.textures[index], 20f)
            }

            // Закрита картка — попап розблокування
            panel.onLocked = { index ->
                showUnlockPopupSticker(type, index, panel)
            }
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

    // ------------------------------------------------------------------------
    // Popup
    // ------------------------------------------------------------------------
    private var popupBgImg : Image? = null
    private var popupImg   : Image? = null

    private fun showUnlockPopupTexture(
        type : PlayerModel.TextureType,
        index: Int,
        panel: APanelTextureCards,
    ) {
        // Прибираємо попереднє прев'ю
        popupBgImg?.remove()
        popupImg?.remove()

        // Створюємо нове
        val textureCard = type.textures[index]
        popupBgImg = Image(gdxGame.assetsAll.texture_def).apply { setSize(80f, 80f) }
        popupImg   = Image(textureCard).apply { setSize(72f, 72f) }

        aPopupUnlock.add(popupBgImg!!) { centerX(); topToTop(margin = 110f) }
        aPopupUnlock.add(popupImg!!)   { center(popupBgImg!!) }

        stateUnlockPopup.onWatch = {
            gdxGame.activity.showInterstitial {
                gdxGame.modelPlayer.unlockCardTexture(type, index)
                panel.unlock(index)
            }
        }

        goToUnlockPopup()
    }

    private fun showUnlockPopupSticker(
        type : PlayerModel.StickerType,
        index: Int,
        panel: APanelStickerCards,
    ) {
        // Прибираємо попереднє прев'ю
        popupBgImg?.remove()
        popupImg?.remove()

        // Створюємо нове
        val textureCard = type.textures[index]
        popupBgImg = Image(gdxGame.assetsAll.texture_def).apply { setSize(80f, 80f) }
        popupImg   = Image(textureCard).apply { setSize(72f, 72f) }

        aPopupUnlock.add(popupBgImg!!) { centerX(); topToTop(margin = 110f) }
        aPopupUnlock.add(popupImg!!)   { center(popupBgImg!!) }

        stateUnlockPopup.onWatch = {
            gdxGame.activity.showInterstitial {
                gdxGame.modelPlayer.unlockCardSticker(type, index)
                panel.unlock(index)
            }
        }

        goToUnlockPopup()
    }

    // ------------------------------------------------------------------------
    // Handler
    // ------------------------------------------------------------------------
    private fun handleShare() {
        val pixmap = aCharSkin.captureToPixmap()

        // Зберігаємо в файл
        val dir = gdxGame.activity.getExternalFilesDir(null)
        val file = File(dir, "shared/skin.png")
        file.parentFile?.mkdirs()

        PixmapIO.writePNG(FileHandle(file), pixmap)
        pixmap.dispose()

        GLOBAL_sharedSkinPath = file.absolutePath

        animHideScreen { gdxGame.navigationManager.navigate(ShareScreen::class.java.name, PersonalizationScreen::class.java.name) }
    }
}