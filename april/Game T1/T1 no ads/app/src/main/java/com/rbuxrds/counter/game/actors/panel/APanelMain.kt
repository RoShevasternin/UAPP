package com.rbuxrds.counter.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.rbuxrds.counter.game.actors.AScrollPane
import com.rbuxrds.counter.game.actors.ATmpGroup
import com.rbuxrds.counter.game.actors.button.base.AButtonAnim
import com.rbuxrds.counter.game.actors.button.base.AButtonStyles
import com.rbuxrds.counter.game.screens.main.DailyConverterScreen
import com.rbuxrds.counter.game.screens.MainScreen
import com.rbuxrds.counter.game.screens.main.AccessoriesScreen
import com.rbuxrds.counter.game.screens.main.AllCharactersScreen
import com.rbuxrds.counter.game.screens.main.AnimationsScreen
import com.rbuxrds.counter.game.screens.main.ClothingScreen
import com.rbuxrds.counter.game.screens.main.DailyFreeRbxCalculatorScreen
import com.rbuxrds.counter.game.screens.main.HeadAndBodyScreen
import com.rbuxrds.counter.game.screens.main.LogicQuizTimeScreen
import com.rbuxrds.counter.game.screens.main.MemesForFunScreen
import com.rbuxrds.counter.game.screens.main.RedeemCoinScreen
import com.rbuxrds.counter.game.screens.main.ScratchScreen
import com.rbuxrds.counter.game.screens.main.SettingsScreen
import com.rbuxrds.counter.game.screens.main.SpinWheelScreen
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.setBounds
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame

class APanelMain(override val screen: AdvancedScreen): AdvancedGroup() {

    data class ButtonData(
        val style       : AButtonAnim.Style,
        val bounds      : Rectangle,
        val toScreenName: String
    )

    private val listData = listOf(
        ButtonData(AButtonStyles.DAILY_CONVERTER, Rectangle(16f, 982f, 344f, 124f)  , DailyConverterScreen::class.java.name),
        ButtonData(AButtonStyles.SCRATCH_WIN    , Rectangle(16f, 860f, 168f, 106f)  , ScratchScreen::class.java.name),
        ButtonData(AButtonStyles.SPIN_WHEEL     , Rectangle(192f, 860f, 168f, 106f) , SpinWheelScreen::class.java.name),
        ButtonData(AButtonStyles.MEME_FOR_FUN   , Rectangle(16f, 746f, 168f, 106f)  , MemesForFunScreen::class.java.name),
        ButtonData(AButtonStyles.DAILY_NEW_RBX  , Rectangle(192f, 746f, 168f, 106f) , DailyFreeRbxCalculatorScreen::class.java.name),
        ButtonData(AButtonStyles.LOGIC_QUIZ_TIME, Rectangle(16f, 666f, 344f, 72f)   , LogicQuizTimeScreen::class.java.name),
        ButtonData(AButtonStyles.REDEEM_COIN    , Rectangle(16f, 586f, 344f, 72f)   , RedeemCoinScreen::class.java.name),
        ButtonData(AButtonStyles.RBX_TO_DOLLAR  , Rectangle(16f, 472f, 168f, 106f)  , DailyFreeRbxCalculatorScreen::class.java.name),
        ButtonData(AButtonStyles.DOLLAR_TO_RBX  , Rectangle(192f, 472f, 168f, 106f) , DailyFreeRbxCalculatorScreen::class.java.name),
        ButtonData(AButtonStyles.ALL_CHARATCERS , Rectangle(16f, 332f, 344f, 124f)  , AllCharactersScreen::class.java.name),
        ButtonData(AButtonStyles.ACCESSORIES    , Rectangle(16f, 210f, 168f, 106f)  , AccessoriesScreen::class.java.name),
        ButtonData(AButtonStyles.ANIMATIONS     , Rectangle(192f, 210f, 168f, 106f) , AnimationsScreen::class.java.name),
        ButtonData(AButtonStyles.ALL_CLOTHING   , Rectangle(16f, 96f, 168f, 106f)   , ClothingScreen::class.java.name),
        ButtonData(AButtonStyles.HEAD_BODY      , Rectangle(192f, 96f, 168f, 106f)  , HeadAndBodyScreen::class.java.name),
        ButtonData(AButtonStyles.SETTINGS       , Rectangle(16f, 16f, 344f, 72f)    , SettingsScreen::class.java.name),
    )

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentGroup  = ATmpGroup(screen)
    private val aScrollPane    = AScrollPane(aContentGroup)
    private val listBtn        = List(listData.size) { AButtonAnim(screen, listData[it].style) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aScrollPane)
        setUpContentGroup()

        aScrollPane.scrollTo(0f, aContentGroup.height, 0f, 0f)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun setUpContentGroup() {
        aContentGroup.setSize(376f, 1122f)
        aContentGroup.addListBtn()
    }

    private fun ATmpGroup.addListBtn() {
        listBtn.forEachIndexed { index, btn ->
            val data = listData[index]
            btn.setBounds(data.bounds)
            addActor(btn)

            btn.setOnClickListener {
                screen.animHideScreen {
                    gdxGame.navigationManager.navigate(data.toScreenName, screen::class.java.name)
                }
            }
        }
    }

}