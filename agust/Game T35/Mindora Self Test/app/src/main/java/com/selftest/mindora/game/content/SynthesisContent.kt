package com.selftest.mindora.game.content

import com.badlogic.gdx.Gdx
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ═════════════════════════════════════════════════════════════════════════════
//  PortraitSynthesis — «логічний» фінальний портрет.
//
//  Джерело: assets/portrait/synthesis.json. Титули перебираються ЗВЕРХУ ВНИЗ,
//  перемагає перший, у якого зійшлися ВСІ rules. Правило матчиться, якщо
//  результат юзера по тесту перетинається з anyOf. Останній титул має
//  rules: [] — це гарантований фолбек (The Layered Original).
//
//  Кейс «4 з 5»: правило по непройденому тесту просто не матчиться, і перебір
//  падає далі — тому синтез при 4 тестах завжди дає валідний титул.
//
//  Резолв ДЕТЕРМІНОВАНИЙ: один і той самий набір результатів завжди дає той
//  самий титул. Обраний id фіксується в PlayerData, щоб додавання титулів у
//  майбутніх версіях не перейменувало вже виданий портрет.
// ═════════════════════════════════════════════════════════════════════════════

@Serializable
data class SynthesisContent(
    val header : String,
    val titles : List<SynthesisTitle>,
) {
    private val byId by lazy { titles.associateBy { it.id } }
    fun titleById(id: String): SynthesisTitle? = byId[id]
}

@Serializable
data class SynthesisTitle(
    val id      : String,
    val name    : String,
    val tagline : String,
    val body    : String,
    @SerialName("rules")
    val rules   : List<SynthesisRule> = emptyList(),
)

@Serializable
data class SynthesisRule(
    val test  : String,
    val anyOf : List<String>,
)

object PortraitSynthesis {

    private val json = Json { ignoreUnknownKeys = true }

    val content: SynthesisContent by lazy {
        val raw = Gdx.files.internal("portrait/synthesis.json").readString("UTF-8")
        json.decodeFromString<SynthesisContent>(raw)
    }

    /**
     * @param outcomes testId → resultIds збережених результатів
     *                 (для big_five це 5 id рівнів рис — теж матчаться в anyOf)
     */
    fun resolve(outcomes: Map<String, List<String>>): SynthesisTitle =
        content.titles.first { title ->
            title.rules.all { rule ->
                outcomes[rule.test]?.any { it in rule.anyOf } == true
            }
        }
}