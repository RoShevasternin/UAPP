package com.rbxtreasure.fungamers.game.actors.panel.quiz

data class QuizQuestion(
    val text   : String,
    val answer : Boolean,   // правильна відповідь: true / false
)

object QuizData {
    val QUESTIONS = listOf(
        QuizQuestion("There is no way to get RBX?", false),
        QuizQuestion("RBX can be earned by completing tasks?", true),
        QuizQuestion("You can spend RBX in the game?", true),
        QuizQuestion("Daily rewards give you RBX every day?", true),
        QuizQuestion("Picking the wrong card gives you RBX?", false),
        QuizQuestion("Treasure Finds has winning cards hidden inside?", true),
        QuizQuestion("RBX is completely useless in this game?", false),
        QuizQuestion("You can convert RBX into rewards?", true),
        QuizQuestion("The quiz gives RBX for correct answers?", true),
        QuizQuestion("All cards in Treasure Finds are losing cards?", false),
    )
}