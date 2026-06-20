package com.coinsclub.funrbx.game.actors.panel.quiz

data class QuizQuestion(
    val text   : String,
    val answer : Boolean,   // правильна відповідь: true / false
)

object QuizData {
    val QUESTIONS = listOf(
        QuizQuestion("There is no way to get RBX?", false),
        QuizQuestion("RBX can be earned by tasks?", true),
        QuizQuestion("You can spend RBX in game?", true),
        QuizQuestion("Daily rewards give RBX?", true),
        QuizQuestion("Wrong cards always give RBX?", false),

        QuizQuestion("Treasure Finds has prizes?", true),
        QuizQuestion("RBX is useless here?", false),
        QuizQuestion("Quiz rewards correct answers?", true),
        QuizQuestion("All treasure cards lose?", false),
        QuizQuestion("You can collect daily gifts?", true),

        QuizQuestion("Tasks can reward RBX?", true),
        QuizQuestion("Every answer is correct?", false),
        QuizQuestion("RBX can be collected daily?", true),
        QuizQuestion("Treasure cards are all empty?", false),
        QuizQuestion("Games can reward RBX?", true),

        QuizQuestion("Daily bonus exists?", true),
        QuizQuestion("RBX disappears every day?", false),
        QuizQuestion("Quizzes can earn rewards?", true),
        QuizQuestion("All cards have the same result?", false),
        QuizQuestion("You can play mini games?", true),

        QuizQuestion("Wrong quiz answers win RBX?", false),
        QuizQuestion("Rewards can be claimed daily?", true),
        QuizQuestion("Treasure Finds has winners?", true),
        QuizQuestion("RBX cannot be collected?", false),
        QuizQuestion("Mini games are available?", true),

        QuizQuestion("Daily gifts are unlimited?", false),
        QuizQuestion("Tasks help earn rewards?", true),
        QuizQuestion("Every treasure card wins?", false),
        QuizQuestion("You can answer quizzes?", true),
        QuizQuestion("RBX can be used in game?", true),

        QuizQuestion("Daily rewards never reset?", false),
        QuizQuestion("Treasure cards hide prizes?", true),
        QuizQuestion("Mini games give rewards?", true),
        QuizQuestion("RBX has no purpose?", false),
        QuizQuestion("You can earn rewards daily?", true),

        QuizQuestion("All quiz answers are true?", false),
        QuizQuestion("You can collect bonuses?", true),
        QuizQuestion("Treasure Finds is a mini game?", true),
        QuizQuestion("Daily gifts cost RBX?", false),
        QuizQuestion("Tasks can unlock rewards?", true),
    )
}