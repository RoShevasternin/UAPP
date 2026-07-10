//package com.zahbx.blitzrbx.game.dataStore
//
//import com.zahbx.blitzrbx.game.manager.DataStoreManager
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