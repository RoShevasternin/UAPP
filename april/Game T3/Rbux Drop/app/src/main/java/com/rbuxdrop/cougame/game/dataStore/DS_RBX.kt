package com.rbuxdrop.cougame.game.dataStore//package com.rbuxdrop.cougame.game.dataStore
//
//import com.rbuxdrop.cougame.game.manager.DataStoreManager
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.flow.MutableStateFlow
//
//class DS_RBX(override val coroutine: CoroutineScope): DataStoreUtil<Long>() {
//
//    override val dataStore = DataStoreManager.RBX
//
//    override val flow = MutableStateFlow(100L)
//
//    init {
//        initialize()
//    }
//}