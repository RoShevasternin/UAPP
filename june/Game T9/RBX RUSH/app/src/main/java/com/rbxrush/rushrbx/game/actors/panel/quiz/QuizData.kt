package com.rbxrush.rushrbx.game.actors.panel.quiz

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
        // ── нові ──
        QuizQuestion("Spinning the wheel can give you RBX?", true),
        QuizQuestion("You lose RBX every time you log in?", false),
        QuizQuestion("Scratch cards can reveal a virtual prize?", true),
        QuizQuestion("A longer daily streak gives bigger rewards?", true),
        QuizQuestion("The Flip Card game has only one card to pick?", false),
        QuizQuestion("Watching an ad can give you extra card picks?", true),
        QuizQuestion("RBX in this game is real Robux?", false),
        QuizQuestion("Answering correctly in the quiz earns RBX?", true),
        QuizQuestion("You can collect a reward without logging in?", false),
        QuizQuestion("The wheel is animated when you spin it?", true),
        QuizQuestion("Every card in Treasure Finds is a winner?", false),
        QuizQuestion("You can come back tomorrow if you miss a day?", true),
        QuizQuestion("The converter shows how your RBX adds up?", true),
        QuizQuestion("Daily rewards reset your balance to zero?", false),
    )
}