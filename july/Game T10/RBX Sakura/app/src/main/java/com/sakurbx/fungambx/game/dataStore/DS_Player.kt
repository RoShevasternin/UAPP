package com.sakurbx.fungambx.game.dataStore//package com.sakurbx.fungambx.game.dataStore
//
//import com.sakurbx.fungambx.game.data.PlayerData
//import com.sakurbx.fungambx.game.manager.DataStoreManager
//import com.sakurbx.fungambx.util.log
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//import kotlinx.serialization.json.Json
//
//class DS_Player(override val coroutine: CoroutineScope): DataStoreJsonUtil<PlayerData>(
//    serializer   = PlayerData.serializer(),
//    deserializer = PlayerData.serializer(),
//) {
//
//    override val dataStore = DataStoreManager.Player
//
//    override val flow = MutableStateFlow(PlayerData())
//
//    init { initialize() }
//
//}