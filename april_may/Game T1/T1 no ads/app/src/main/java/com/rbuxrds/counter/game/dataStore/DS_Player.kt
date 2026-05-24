//package com.rbuxrds.counter.game.dataStore
//
//import com.rbuxrds.counter.game.data.PlayerData
//import com.rbuxrds.counter.game.manager.DataStoreManager
//import com.rbuxrds.counter.util.log
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
//    // Mutex гарантує що update-и виконуються строго один за одним,
//    // навіть якщо їх викликають одночасно з кількох корутинів
//    private val mutex = Mutex()
//
//    init { initialize() }
//
//    override fun initialize() {
//        coroutine.launch(Dispatchers.IO) {
//
//            val raw = dataStore.get()
//
//            if (raw != null) {
//                val decoded = Json.decodeFromString(deserializer, raw)
//                flow.value = decoded
//                logPlayerInit(decoded)
//            } else {
//                log("DS_Player INIT → Default data")
//                logPlayerInit(flow.value)
//            }
//        }
//    }
//
//    override fun update(block: (PlayerData) -> PlayerData) {
//        coroutine.launch(Dispatchers.IO) {
//            mutex.withLock {
//                val old = flow.value
//                val new = block(old)
//
//                flow.value = new
//                dataStore.update { Json.encodeToString(serializer, new) }
//
//                logPlayerUpdate(old, new)
//            }
//        }
//    }
//
//}