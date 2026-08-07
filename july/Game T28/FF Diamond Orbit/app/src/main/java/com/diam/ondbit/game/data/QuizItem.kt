package com.diam.ondbit.game.data

// ------------------------------------------------------------------------
// QuizItem — одне питання
// ------------------------------------------------------------------------
// isTrue = правильна відповідь. true → кнопка TRUE, false → кнопка FALSE.
data class QuizItem(
    val question : String,
    val isTrue   : Boolean,
)

// ------------------------------------------------------------------------
// QuizData — банк питань
// ------------------------------------------------------------------------
// 20 питань, з них у раунд береться 10 випадкових.
// Питання тримаємо короткими (до ~65 символів) — довші не влазять
// у два рядки лейбла 296×36.
object QuizData {

    fun items(): List<QuizItem> = listOf(

        // ── Діаманти ──────────────────────────────────────────────────────────
        QuizItem("Do scientists believe diamond rain falls on Neptune?" , true),
        QuizItem("Is diamond the hardest natural material on Earth?"    , true),
        QuizItem("Are diamonds made of pure carbon?"                    , true),
        QuizItem("Can diamonds be created in a laboratory?"             , true),
        QuizItem("Can a diamond burn if it gets hot enough?"            , true),
        QuizItem("Do most diamonds form deep beneath Earth's surface?"  , true),
        QuizItem("Is there a known planet made entirely of diamond?"    , false),

        // ── Сонячна система ───────────────────────────────────────────────────
        QuizItem("Is the Sun a planet?"                                 , false),
        QuizItem("Is Jupiter the largest planet in the Solar System?"   , true),
        QuizItem("Is Venus hotter than Mercury?"                        , true),
        QuizItem("Does Mars have two moons?"                            , true),
        QuizItem("Is Saturn the only planet with rings?"                , false),
        QuizItem("Is Pluto still officially classified as a planet?"    , false),
        QuizItem("Is one day on Venus longer than its entire year?"     , true),
        QuizItem("Is Earth the only planet not named after a god?"      , true),

        // ── Космос загалом ────────────────────────────────────────────────────
        QuizItem("Does sound travel through empty space?"               , false),
        QuizItem("Is a light-year a measurement of time?"               , false),
        QuizItem("Does the Moon produce its own light?"                 , false),
        QuizItem("Do astronauts get slightly taller in space?"          , true),
        QuizItem("Is space completely empty?"                           , false),
    )
}