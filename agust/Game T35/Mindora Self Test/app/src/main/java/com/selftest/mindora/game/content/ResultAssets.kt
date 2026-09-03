package com.selftest.mindora.game.content

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ResultAssets — ЄДИНЕ місце, де результат перетворюється на картинку.
//
//  Картинки зараз ПО ТЕСТУ, а не по результату: одна на archetype, одна на
//  types16 і т.д. (listResult[0..3]) і одна на кожну рису Big Five
//  (listResultBig5[0..4]). Коли дизайнер віддасть по картинці на КОЖЕН
//  результат (6 архетипів, 16 типів…) — міняється лише тіло цих двох
//  методів, актори не чіпаються.
// ═════════════════════════════════════════════════════════════════════════════
object ResultAssets {

    /**
     * Картинка результату для чотирьох «одиночних» тестів.
     * @param testIndex позиція теста в TestRepository.ALL (= TestCatalog.Entry.index);
     *        big_five (index 4) сюди не потрапляє — у нього свій список.
     */
    fun single(testIndex: Int): Drawable {
        val list = gdxGame.assetsAll.listResult
        return TextureRegionDrawable(list[testIndex.coerceIn(0, list.lastIndex)])
    }

    /**
     * Картинка риси Big Five.
     * @param traitIndex позиція в outcome.resultIds — вона ж порядок axes у JSON:
     *        0 openness · 1 conscientiousness · 2 extraversion · 3 agreeableness · 4 neuroticism
     */
    fun trait(traitIndex: Int): Drawable {
        val list = gdxGame.assetsAll.listResultBig5
        return TextureRegionDrawable(list[traitIndex.coerceIn(0, list.lastIndex)])
    }
}