//package com.rbuxrds.counter.game.model
//
//import com.rbuxrds.counter.game.data.PlayerData
//import com.rbuxrds.counter.game.dataStore.DS_Player
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.flow.*
//import kotlin.math.ln
//import kotlin.math.pow
//
//class PlayerModel(
//    private val ds: DS_Player,
//    scope: CoroutineScope
//) {
//
//    // =====================================================
//    // Джерело правди
//    // =====================================================
//
//    val playerFlow: StateFlow<PlayerData> =
//        ds.flow.stateIn(
//            scope = scope,
//            started = SharingStarted.Eagerly,
//            initialValue = ds.flow.value
//        )
//
//    val currentPlayer: PlayerData
//        get() = playerFlow.value
//
//}
