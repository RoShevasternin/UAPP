//package com.rsbuxs.rcounbux.game.dataStore
//
//import com.rsbuxs.rcounbux.game.manager.DataStoreManager
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