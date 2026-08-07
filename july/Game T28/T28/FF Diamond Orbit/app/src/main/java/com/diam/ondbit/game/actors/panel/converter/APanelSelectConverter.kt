//package com.diam.ondbit.game.actors.panel.converter
//
//import com.badlogic.gdx.scenes.scene2d.ui.Image
//import com.diam.ondbit.game.actors.checkbox.base.ACheckBox
//import com.diam.ondbit.game.actors.checkbox.base.ACheckBoxGroup
//import com.diam.ondbit.game.actors.checkbox.base.ACheckBoxStyles
//import com.diam.ondbit.game.utils.actor.disable
//import com.diam.ondbit.game.utils.actor.setOnClickListener
//import com.diam.ondbit.game.utils.advanced.AdvancedGroup
//import com.diam.ondbit.game.utils.advanced.AdvancedScreen
//import com.diam.ondbit.game.utils.gdxGame
//import com.diam.ondbit.game.utils.global.ConverterType
//import com.diam.ondbit.game.utils.global.GLOBAL_SELECTED_CONVERTER_TYPE
//
//class APanelSelectConverter(override val screen: AdvancedScreen): AdvancedGroup() {
//
//    // ------------------------------------------------------------------------
//    // Actors
//    // ------------------------------------------------------------------------
//    private val listBox     = List(5) { ACheckBox(screen, ACheckBoxStyles.ITEM) }
//    private val aContentImg = Image(gdxGame.assetsAll.PANEL_CONVERTER_SELECT)
//
//    // ------------------------------------------------------------------------
//    // Callback
//    // ------------------------------------------------------------------------
//    var onSelectType: (ConverterType) -> Unit = {}
//
//    // ------------------------------------------------------------------------
//    // Lifecycle
//    // ------------------------------------------------------------------------
//    override fun addActorsOnGroup() {
//        addListBtn()
//        addAndFillActor(aContentImg)
//        aContentImg.disable()
//    }
//
//    // ------------------------------------------------------------------------
//    // Add Actors
//    // ------------------------------------------------------------------------
//    private fun AdvancedGroup.addListBtn() {
//        var nx = 0f
//        var ny = 166f
//
//        val cbg = ACheckBoxGroup()
//
//        listBox.forEachIndexed { index, box ->
//            addActor(box)
//            box.setBounds(nx, ny, 168f, 75f)
//
//            nx += 8f + 168f
//            if (index.inc() % 2 == 0) {
//                nx = 0f
//                ny -= 8f + 75f
//            }
//
//            box.checkBoxGroup = cbg
//
//            box.setOnClickListener {
//                GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.entries[index]
//                onSelectType(GLOBAL_SELECTED_CONVERTER_TYPE)
//            }
//        }
//
//        listBox.first().check()
//    }
//
//}