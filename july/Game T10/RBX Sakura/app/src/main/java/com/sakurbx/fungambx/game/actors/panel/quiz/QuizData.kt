package com.sakurbx.fungambx.game.actors.panel.quiz

data class QuizQuestion(
    val text   : String,
    val answer : Boolean,   // правильна відповідь: true / false
)

object QuizData {
    val QUESTIONS = listOf(
        // ── Roblox загальне ──
        QuizQuestion("Roblox lets you play games made by other players?", true),
        QuizQuestion("You need to be a company to make a Roblox game?", false),
        QuizQuestion("Avatars in Roblox can be customized with items?", true),
        QuizQuestion("Roblox games are called 'experiences'?", true),
        QuizQuestion("You can only play Roblox on a computer?", false),
        QuizQuestion("Roblox has its own game creation tool?", true),
        QuizQuestion("Every Roblox game costs money to play?", false),
        QuizQuestion("You can chat with friends inside Roblox?", true),
        QuizQuestion("Roblox avatars are always the same for everyone?", false),
        QuizQuestion("You can create your own game world in Roblox?", true),

        // ── аватари / кастомізація ──
        QuizQuestion("Hats and accessories can change your avatar's look?", true),
        QuizQuestion("An avatar can wear more than one accessory?", true),
        QuizQuestion("Changing clothes requires deleting your account?", false),
        QuizQuestion("Animations can change how your avatar moves?", true),
        QuizQuestion("Your avatar's outfit is locked forever once chosen?", false),

        // ── ігровий здоровий глузд ──
        QuizQuestion("Playing more games can be more fun with friends?", true),
        QuizQuestion("Guessing carefully improves your chances in games?", true),
        QuizQuestion("A losing card in a pick game gives you a big prize?", false),
        QuizQuestion("Trying different games helps you find favorites?", true),
        QuizQuestion("Reading the rules helps you play better?", true),

        // ── прості логічні (легкі бали) ──
        QuizQuestion("A wheel usually has more than one section?", true),
        QuizQuestion("Spinning a wheel always lands on the same spot?", false),
        QuizQuestion("A daily reward is meant to be claimed each day?", true),
        QuizQuestion("You get stronger by not playing at all?", false),
        QuizQuestion("Collecting items lets you build a collection?", true),
    )
}