package com.coinsclub.funrbx.game.dataStore//package com.coinsclub.funrbx.game.dataStore
//
//import com.coinsclub.funrbx.game.manager.DataStoreManager
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