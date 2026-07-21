package com.mon.sterbx.game.dataStore//package com.mon.sterbx.game.dataStore
//
//import com.mon.sterbx.game.data.PlayerData
//import com.mon.sterbx.game.manager.DataStoreManager
//import com.mon.sterbx.util.log
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