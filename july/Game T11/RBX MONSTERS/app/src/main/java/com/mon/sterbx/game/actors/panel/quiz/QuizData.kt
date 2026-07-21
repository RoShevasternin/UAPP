package com.mon.sterbx.game.actors.panel.quiz

data class QuizQuestion(
    val text   : String,
    val answer : Boolean,   // правильна відповідь: true / false
)

object QuizData {
    val QUESTIONS = listOf(
        QuizQuestion("Do monsters help you earn more RBX?", true),
        QuizQuestion("Can you collect different monsters?", true),
        QuizQuestion("Do monsters have unique powers?",    true),
        QuizQuestion("Can spinning give you a reward?",    true),
        QuizQuestion("Do quizzes reward correct answers?", true),

        QuizQuestion("Is every monster exactly the same?", false),
        QuizQuestion("Do you lose RBX by opening cards?",  false),
        QuizQuestion("Are all monsters locked forever?",   false),
        QuizQuestion("Is one wrong answer game over?",     false),
        QuizQuestion("Do monsters vanish after one game?", false),
    )
}